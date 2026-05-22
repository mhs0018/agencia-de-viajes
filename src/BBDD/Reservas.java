package BBDD;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Reservas extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public ConexionMySQL conexion = new ConexionMySQL("root", "", "agencia-viajes");

	private JTable table;
	private DefaultTableModel modelo;
	private int idUsuario;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Reservas(String usuarioLogueado, int idUsuario) {
		this.idUsuario = idUsuario;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 540, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lbl_Titulo = new JLabel("Mis Reservas - " + usuarioLogueado);
		lbl_Titulo.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lbl_Titulo.setBounds(10, 10, 500, 25);
		contentPane.add(lbl_Titulo);

		modelo = new DefaultTableModel(
				new Object[] { "id_reserva", "Destino", "Fecha", "Presupuesto" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable(modelo);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.setCellSelectionEnabled(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// El id se mantiene en el modelo para modificar/eliminar la reserva correcta,
		// pero la columna queda completamente oculta al usuario.
		table.getColumnModel().getColumn(0).setMinWidth(0);
		table.getColumnModel().getColumn(0).setMaxWidth(0);
		table.getColumnModel().getColumn(0).setPreferredWidth(0);
		table.getColumnModel().getColumn(0).setWidth(0);
		table.getColumnModel().getColumn(0).setResizable(false);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(10, 50, 500, 200);
		contentPane.add(scrollPane);

		JButton btn_Eliminar = new JButton("Eliminar");
		btn_Eliminar.setBounds(10, 270, 140, 30);
		btn_Eliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eliminarReserva();
			}
		});
		contentPane.add(btn_Eliminar);

		JButton btn_Modificar = new JButton("Modificar");
		btn_Modificar.setBounds(165, 270, 140, 30);
		btn_Modificar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modificarReserva();
			}
		});
		contentPane.add(btn_Modificar);

		JButton btn_Nueva = new JButton("Nueva Reserva");
		btn_Nueva.setBounds(320, 270, 160, 30);
		btn_Nueva.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nuevaReserva();
			}
		});
		contentPane.add(btn_Nueva);

		cargarReservas();
	}

	private void cargarReservas() {
		modelo.setRowCount(0);
		try {
			conexion.conectar();
			String sql = "SELECT id_reserva, destino, fecha, presupuesto "
					+ "FROM reserva WHERE id_usuario = " + idUsuario;
			ResultSet rs = conexion.ejecutarSelect(sql);
			while (rs.next()) {
				modelo.addRow(new Object[] {
						rs.getInt("id_reserva"),
						rs.getString("destino"),
						rs.getString("fecha"),
						rs.getString("presupuesto")
				});
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error al cargar reservas: " + ex.getMessage());
		} finally {
			cerrarConexion();
		}
	}

	public void recargarReservas() {
		cargarReservas();
	}

	private void eliminarReserva() {
		int fila = table.getSelectedRow();
		if (fila == -1) {
			JOptionPane.showMessageDialog(null, "Seleccione una reserva para eliminar");
			return;
		}

		int idReserva = (int) modelo.getValueAt(fila, 0);

		int confirmacion = JOptionPane.showConfirmDialog(null,
				"¿Seguro que quiere eliminar esta reserva?",
				"Confirmar", JOptionPane.YES_NO_OPTION);

		if (confirmacion == JOptionPane.YES_OPTION) {
			try {
				conexion.conectar();
				String sql = "DELETE FROM reserva WHERE id_reserva = " + idReserva;
				conexion.ejecutarInsertDeleteUpdate(sql);
				modelo.removeRow(fila);
				JOptionPane.showMessageDialog(null, "Reserva eliminada");
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error al eliminar: " + ex.getMessage());
			} finally {
				cerrarConexion();
			}
		}
	}

	private void modificarReserva() {
		int fila = table.getSelectedRow();
		if (fila == -1) {
			JOptionPane.showMessageDialog(null, "Selecciona una reserva para modificar.");
			return;
		}

		int idReserva = (int) modelo.getValueAt(fila, 0);
		String destino = (String) modelo.getValueAt(fila, 1);
		String fecha = (String) modelo.getValueAt(fila, 2);
		String presupuesto = (String) modelo.getValueAt(fila, 3);

		ModificarReserva ventana = new ModificarReserva(idReserva, destino, fecha, presupuesto, this);
		ventana.setVisible(true);
	}

	private void nuevaReserva() {
		// La ventana de creación recibe el id del usuario logueado para guardar
		// la reserva asociada a ese usuario y recargar esta tabla al terminar.
		CrearReserva ventana = new CrearReserva(idUsuario, this);
		ventana.setVisible(true);
	}

	private void cerrarConexion() {
		try {
			conexion.desconectar();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}

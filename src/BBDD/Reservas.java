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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Reservas extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	// Conexión con la base de datos
	public ConexionMySQL conexion = new ConexionMySQL("root", "", "agencia-viajes");

	private JTable table;
	private DefaultTableModel modelo;
	private int idUsuario; // ID del cliente logueado

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
	 * Constructor: recibe el nombre de usuario y su id para filtrar sus reservas.
	 */
	public Reservas(String usuarioLogueado, int idUsuario) {
		this.idUsuario = idUsuario;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 540, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// Título
		JLabel lbl_Titulo = new JLabel("Mis Reservas - " + usuarioLogueado);
		lbl_Titulo.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lbl_Titulo.setBounds(10, 10, 500, 25);
		contentPane.add(lbl_Titulo);

		// Modelo con 4 columnas: id_reserva (oculta), Destino, Fecha, Presupuesto
		modelo = new DefaultTableModel(
				new Object[] { "id_reserva", "Destino", "Fecha", "Presupuesto" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // No se puede editar directamente en la tabla
			}
		};

		table = new JTable(modelo);

		// Ocultar la columna id_reserva (columna 0) — se usa internamente
		table.getColumnModel().getColumn(0).setMinWidth(0);
		table.getColumnModel().getColumn(0).setMaxWidth(0);
		table.getColumnModel().getColumn(0).setWidth(0);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(10, 50, 500, 200);
		contentPane.add(scrollPane);

		// ── Botón ELIMINAR ──────────────────────────────────────────────────
		JButton btn_Eliminar = new JButton("Eliminar");
		btn_Eliminar.setBounds(10, 270, 140, 30);
		btn_Eliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eliminarReserva();
			}
		});
		contentPane.add(btn_Eliminar);

		// ── Botón MODIFICAR ─────────────────────────────────────────────────
		JButton btn_Modificar = new JButton("Modificar");
		btn_Modificar.setBounds(165, 270, 140, 30);
		btn_Modificar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modificarReserva();
			}
		});
		contentPane.add(btn_Modificar);

		// ── Botón NUEVA RESERVA ─────────────────────────────────────────────
		JButton btn_Nueva = new JButton("Nueva Reserva");
		btn_Nueva.setBounds(320, 270, 160, 30);
		btn_Nueva.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nuevaReserva();
			}
		});
		contentPane.add(btn_Nueva);

		// Cargar las reservas del usuario al abrir la ventana
		cargarReservas();
	}

	/**
	 * Consulta la BD y rellena la tabla con las reservas del usuario.
	 */
	private void cargarReservas() {
		modelo.setRowCount(0); // Limpia filas anteriores
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
			conexion.desconectar();
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error al cargar reservas: " + ex.getMessage());
		}
	}

	/**
	 * Elimina la reserva seleccionada en la tabla de la BD y de la vista.
	 */
	private void eliminarReserva() {
		int fila = table.getSelectedRow();
		if (fila == -1) {
			JOptionPane.showMessageDialog(null, "Selecciona una reserva para eliminar.");
			return;
		}

		int idReserva = (int) modelo.getValueAt(fila, 0);

		int confirmacion = JOptionPane.showConfirmDialog(null,
				"¿Seguro que quieres eliminar esta reserva?",
				"Confirmar eliminación", JOptionPane.YES_NO_OPTION);

		if (confirmacion == JOptionPane.YES_OPTION) {
			try {
				conexion.conectar();
				String sql = "DELETE FROM reserva WHERE id_reserva = " + idReserva;
				conexion.ejecutarInsertDeleteUpdate(sql);
				conexion.desconectar();
				modelo.removeRow(fila); // Quita la fila de la tabla visualmente
				JOptionPane.showMessageDialog(null, "Reserva eliminada correctamente.");
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error al eliminar: " + ex.getMessage());
			}
		}
	}

	/**
	 * Permite editar los datos de la reserva seleccionada mediante diálogos.
	 */
	private void modificarReserva() {
		int fila = table.getSelectedRow();
		if (fila == -1) {
			JOptionPane.showMessageDialog(null, "Selecciona una reserva para modificar.");
			return;
		}

		int    idReserva   = (int)    modelo.getValueAt(fila, 0);
		String destino     = (String) modelo.getValueAt(fila, 1);
		String fecha       = (String) modelo.getValueAt(fila, 2);
		String presupuesto = (String) modelo.getValueAt(fila, 3);

		// Diálogos con los valores actuales precargados
		String nuevoDestino = JOptionPane.showInputDialog(null, "Destino:", destino);
		if (nuevoDestino == null) return; // Cancelado

		String nuevaFecha = JOptionPane.showInputDialog(null, "Fecha (YYYY-MM-DD):", fecha);
		if (nuevaFecha == null) return;

		String nuevoPresupuesto = JOptionPane.showInputDialog(null, "Presupuesto:", presupuesto);
		if (nuevoPresupuesto == null) return;

		try {
			conexion.conectar();
			String sql = "UPDATE reserva SET "
					+ "destino = '"     + nuevoDestino     + "', "
					+ "fecha = '"       + nuevaFecha        + "', "
					+ "presupuesto = '" + nuevoPresupuesto  + "' "
					+ "WHERE id_reserva = " + idReserva;
			conexion.ejecutarInsertDeleteUpdate(sql);
			conexion.desconectar();

			// Actualiza la tabla visualmente sin recargar de la BD
			modelo.setValueAt(nuevoDestino,    fila, 1);
			modelo.setValueAt(nuevaFecha,      fila, 2);
			modelo.setValueAt(nuevoPresupuesto, fila, 3);

			JOptionPane.showMessageDialog(null, "Reserva modificada correctamente.");
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error al modificar: " + ex.getMessage());
		}
	}

	/**
	 * Muestra diálogos para introducir una nueva reserva y la guarda en la BD.
	 */
	private void nuevaReserva() {
		String destino = JOptionPane.showInputDialog(null, "Destino:");
		if (destino == null || destino.trim().isEmpty()) return;

		String fecha = JOptionPane.showInputDialog(null, "Fecha (YYYY-MM-DD):");
		if (fecha == null || fecha.trim().isEmpty()) return;

		String presupuesto = JOptionPane.showInputDialog(null, "Presupuesto:");
		if (presupuesto == null || presupuesto.trim().isEmpty()) return;

		try {
			conexion.conectar();
			String sql = "INSERT INTO reserva (destino, fecha, presupuesto, id_usuario) VALUES ('"
					+ destino + "', '" + fecha + "', '" + presupuesto + "', " + idUsuario + ")";
			conexion.ejecutarInsertDeleteUpdate(sql);
			conexion.desconectar();
			cargarReservas(); // Recarga la tabla para mostrar la nueva fila con su id
			JOptionPane.showMessageDialog(null, "Reserva añadida correctamente.");
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error al añadir reserva: " + ex.getMessage());
		}
	}
}
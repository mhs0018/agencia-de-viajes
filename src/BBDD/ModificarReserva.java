package BBDD;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ModificarReserva extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textDestino;
	private JTextField textFecha;
	private JTextField textPresupuesto;

	public ConexionMySQL conexion = new ConexionMySQL("root", "", "agencia-viajes");
	private int idReserva;
	private Reservas ventanaReservas;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ModificarReserva frame = new ModificarReserva(0, "", "", "", null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ModificarReserva(int idReserva, String destino, String fecha, String presupuesto, Reservas ventanaReservas) {
		this.idReserva = idReserva;
		this.ventanaReservas = ventanaReservas;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(200, 200, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lbl_Titulo = new JLabel("Modificar Reserva");
		lbl_Titulo.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lbl_Titulo.setBounds(102, 10, 231, 22);
		contentPane.add(lbl_Titulo);

		JLabel lbl_Destino = new JLabel("Destino");
		lbl_Destino.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl_Destino.setBounds(48, 42, 84, 18);
		contentPane.add(lbl_Destino);

		JLabel lbl_Fecha = new JLabel("Fecha");
		lbl_Fecha.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl_Fecha.setBounds(48, 70, 108, 22);
		contentPane.add(lbl_Fecha);

		JLabel lbl_Presupuesto = new JLabel("Presupuesto");
		lbl_Presupuesto.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl_Presupuesto.setBounds(48, 104, 145, 13);
		contentPane.add(lbl_Presupuesto);

		textDestino = new JTextField();
		textDestino.setFont(new Font("Tahoma", Font.PLAIN, 18));
		textDestino.setBounds(211, 42, 125, 18);
		contentPane.add(textDestino);
		textDestino.setColumns(10);

		textFecha = new JTextField();
		textFecha.setFont(new Font("Tahoma", Font.PLAIN, 18));
		textFecha.setBounds(211, 70, 125, 18);
		contentPane.add(textFecha);
		textFecha.setColumns(10);

		textPresupuesto = new JTextField();
		textPresupuesto.setBounds(211, 98, 125, 18);
		contentPane.add(textPresupuesto);
		textPresupuesto.setColumns(10);

		textDestino.setText(destino);
		textFecha.setText(fecha);
		textPresupuesto.setText(presupuesto);

		JButton btn_Guardar = new JButton("Guardar");
		btn_Guardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modificarReserva();
			}
		});
		btn_Guardar.setBounds(143, 205, 84, 20);
		contentPane.add(btn_Guardar);

		JButton btn_Volver = new JButton("Volver");
		btn_Volver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btn_Volver.setBounds(237, 205, 84, 20);
		contentPane.add(btn_Volver);
	}

	private void modificarReserva() {
		String destino = textDestino.getText().trim();
		String fecha = textFecha.getText().trim();
		String presupuesto = textPresupuesto.getText().trim();

		if (destino.isEmpty() || fecha.isEmpty() || presupuesto.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Rellena todos los campos");
			return;
		}

		try {
			conexion.conectar();
			String sql = "UPDATE reserva SET "
					+ "destino = '" + destino + "', "
					+ "fecha = '" + fecha + "', "
					+ "presupuesto = '" + presupuesto + "' "
					+ "WHERE id_reserva = " + idReserva;
			conexion.ejecutarInsertDeleteUpdate(sql);

			if (ventanaReservas != null) {
				ventanaReservas.recargarReservas();
			}

			JOptionPane.showMessageDialog(null, "Reserva modificada correctamente.");
			dispose();
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error al modificar: " + ex.getMessage());
		} finally {
			try {
				conexion.desconectar();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}

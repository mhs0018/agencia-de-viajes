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

		crearEtiqueta("Modificar Reserva", 102, 10, 231, 22, 25);
		crearEtiqueta("Destino", 48, 42, 84, 18, 12);
		crearEtiqueta("Fecha", 48, 70, 108, 22, 12);
		crearEtiqueta("Presupuesto", 48, 104, 145, 13, 12);

		textDestino = crearCampoTexto(211, 42, 125, 18, 18);
		textFecha = crearCampoTexto(211, 70, 125, 18, 18);
		textPresupuesto = crearCampoTexto(211, 98, 125, 18, 18);

		textDestino.setText(destino);
		textFecha.setText(fecha);
		textPresupuesto.setText(presupuesto);

		JButton btnGuardar = crearBoton("Guardar");
		btnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modificarReserva();
			}
		});
		btnGuardar.setBounds(143, 205, 84, 20);

		JButton btnVolver = crearBoton("Volver");
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnVolver.setBounds(237, 205, 84, 20);
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

	private JLabel crearEtiqueta(String texto, int x, int y, int ancho, int alto, int tamanoFuente) {
		JLabel etiqueta = new JLabel(texto);
		etiqueta.setFont(new Font("Tahoma", Font.PLAIN, tamanoFuente));
		etiqueta.setBounds(x, y, ancho, alto);
		contentPane.add(etiqueta);
		return etiqueta;
	}

	private JTextField crearCampoTexto(int x, int y, int ancho, int alto, int tamanoFuente) {
		JTextField campo = new JTextField();
		campo.setFont(new Font("Tahoma", Font.PLAIN, tamanoFuente));
		campo.setBounds(x, y, ancho, alto);
		campo.setColumns(10);
		contentPane.add(campo);
		return campo;
	}

	private JButton crearBoton(String texto) {
		JButton boton = new JButton(texto);
		contentPane.add(boton);
		return boton;
	}
}

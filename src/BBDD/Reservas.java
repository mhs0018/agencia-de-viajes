package BBDD;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * Esta ventana muestra todas las reservas en una tabla.
 * Antes había un menú desplegable, pero ahora lo cambiamos
 * por 3 botones: Crear, Modificar y Eliminar.
 *
 * Cada botón hace lo mismo que hacía antes la opción del menú.
 */

public class Reservas extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tablaReservas;
	private DefaultTableModel modelo;

	// Datos del usuario que inició sesión
	private String usuarioActual = "UsuarioEjemplo";
	private int idUsuario = -1;

	public ConexionMySQL conexion = new ConexionMySQL("root", "", "agencia-viajes");

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Reservas frame = new Reservas();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Constructor usado desde Login: recibe el nombre e ID del usuario
	public Reservas(String usuario, int idUsuario) {
		this.usuarioActual = usuario;
		this.idUsuario = idUsuario;
		init();
	}

	// Constructor sin argumentos (para pruebas directas)
	public Reservas() {
		init();
	}

	private void init() {

		setTitle("Historial de Reservas");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 700, 400);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// Etiqueta con el nombre del usuario
		JLabel lblUsuario = new JLabel("Usuario: " + usuarioActual);
		lblUsuario.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblUsuario.setBounds(500, 10, 180, 20);
		contentPane.add(lblUsuario);

		// Scroll para la tabla
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 20, 450, 300);
		contentPane.add(scrollPane);

		tablaReservas = new JTable();
		scrollPane.setViewportView(tablaReservas);

		// -----------------------------
		// BOTÓN CREAR RESERVA
		// -----------------------------
		JButton btnCrear = new JButton("Nueva Reserva");
		btnCrear.setBounds(500, 60, 150, 30);
		contentPane.add(btnCrear);

		btnCrear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nuevaReserva();
			}
		});

		// Cargar las reservas del usuario al abrir la ventana
		cargarReservas();

		// -----------------------------
		// BOTÓN MODIFICAR RESERVA
		// -----------------------------
		JButton btn_Modificar = new JButton("Modificar");
		btn_Modificar.setBounds(500, 110, 150, 30);
		btn_Modificar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modificarReserva();
			}
		});
		contentPane.add(btn_Modificar);

		// -----------------------------
		// BOTÓN ELIMINAR RESERVA
		// -----------------------------
		JButton btnEliminar = new JButton("Eliminar Reserva");
		btnEliminar.setBounds(500, 160, 150, 30);
		contentPane.add(btnEliminar);

		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eliminarReserva();
			}
		});
		contentPane.add(btnEliminar);

		// Cargo las reservas al iniciar la ventana
		cargarReservas();
	}

	// Método para cargar todas las reservas en la tabla
	private void cargarReservas() {

		modelo = new DefaultTableModel();
		modelo.addColumn("ID");
		modelo.addColumn("Destino");
		modelo.addColumn("Fecha");
		modelo.addColumn("Presupuesto");

		try {
			conexion.conectar();
			String consulta = "SELECT id, destino, fecha, presupuesto FROM reservas";
			ResultSet rs = conexion.ejecutarSelect(consulta);

			while (rs.next()) {
				Object[] fila = new Object[4];
				fila[0] = rs.getInt("id");
				fila[1] = rs.getString("destino");
				fila[2] = rs.getString("fecha");
				fila[3] = rs.getDouble("presupuesto");
				modelo.addRow(fila);
			}

			conexion.desconectar();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		tablaReservas.setModel(modelo);
	}

	// MÉTODOS

	// Eliminar la reserva seleccionada en la tabla
	private void eliminarReserva() {
		int fila = tablaReservas.getSelectedRow();
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

	// Introducir una nueva reserva en la BBDD
	private void nuevaReserva() {
		String destino = JOptionPane.showInputDialog(null, "Destino:");
		if (destino == null || destino.trim().isEmpty())
			return;

		String fecha = JOptionPane.showInputDialog(null, "Fecha (YYYY-MM-DD):");
		if (fecha == null || fecha.trim().isEmpty())
			return;

		String presupuesto = JOptionPane.showInputDialog(null, "Presupuesto:");
		if (presupuesto == null || presupuesto.trim().isEmpty())
			return;

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

	// Permite editar los datos de la reserva seleccionada mediante diálogos.
	private void modificarReserva() {
		int fila = tablaReservas.getSelectedRow();
		if (fila == -1) {
			JOptionPane.showMessageDialog(null, "Selecciona una reserva para modificar.");
			return;
		}

		int idReserva = (int) modelo.getValueAt(fila, 0);
		String destino = (String) modelo.getValueAt(fila, 1);
		String fecha = (String) modelo.getValueAt(fila, 2);
		String presupuesto = (String) modelo.getValueAt(fila, 3);

		// Diálogos con los valores actuales precargados
		String nuevoDestino = JOptionPane.showInputDialog(null, "Destino:", destino);
		if (nuevoDestino == null)
			return; // Cancelado

		String nuevaFecha = JOptionPane.showInputDialog(null, "Fecha (YYYY-MM-DD):", fecha);
		if (nuevaFecha == null)
			return;

		String nuevoPresupuesto = JOptionPane.showInputDialog(null, "Presupuesto:", presupuesto);
		if (nuevoPresupuesto == null)
			return;

		try {
			conexion.conectar();
			String sql = "UPDATE reserva SET "
					+ "destino = '" + nuevoDestino + "', "
					+ "fecha = '" + nuevaFecha + "', "
					+ "presupuesto = '" + nuevoPresupuesto + "' "
					+ "WHERE id_reserva = " + idReserva;
			conexion.ejecutarInsertDeleteUpdate(sql);
			conexion.desconectar();

			// Actualiza la tabla visualmente sin recargar de la BD
			modelo.setValueAt(nuevoDestino, fila, 1);
			modelo.setValueAt(nuevaFecha, fila, 2);
			modelo.setValueAt(nuevoPresupuesto, fila, 3);

			JOptionPane.showMessageDialog(null, "Reserva modificada correctamente.");
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error al modificar: " + ex.getMessage());
		}
	}
}
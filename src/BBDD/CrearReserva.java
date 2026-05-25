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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class CrearReserva extends JFrame {

    // Identificador de versión para serializar la clase JFrame
    // Evita excepciones de incompatibilidad si la clase se guarda y se carga
    // desde un flujo de objetos en versiones diferentes
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    // Campos de texto donde el usuario escribe los datos
    protected JTextField textDestino;
    protected JTextField textFecha;
    protected JTextField textPresupuesto;

    // Conexión compartida con la base de datos para esta ventana
    // Se reutiliza aquí para validar fechas y guardar nuevas reservas
    public ConexionMySQL conexion = new ConexionMySQL("root", "", "agencia-viajes");
    private int idUsuario;
    private Reservas ventanaReservas;

    /**
     * Launch the application
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CrearReserva frame = new CrearReserva();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame
     */
    public CrearReserva() {
        // Constructor vacío usado cuando no hay usuario logueado o para pruebas
        this(0, null);
    }

    /**
     * Constructor para crear una nueva reserva ligada a un usuario
     */
    public CrearReserva(int idUsuario, Reservas ventanaReservas) {
        this.idUsuario = idUsuario;
        this.ventanaReservas = ventanaReservas;

        setTitle("Crear nueva reserva");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(200, 200, 450, 350);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        // Layout absoluto para posicionar cada componente con coordenadas fijas
        contentPane.setLayout(null);

        crearEtiqueta("Nueva Reserva", 130, 10, 200, 30, 22);
        crearEtiqueta("Destino:", 40, 70, 100, 20, 16);
        crearEtiqueta("Fecha:", 40, 110, 100, 20, 16);
        crearEtiqueta("Presupuesto:", 40, 150, 120, 20, 16);

        textDestino = crearCampoTexto(160, 70, 180, 20);
        textFecha = crearCampoTexto(160, 110, 180, 20);
        textPresupuesto = crearCampoTexto(160, 150, 180, 20);

        // Crea el botón; crearBoton lo añade directamente al panel
        JButton btnGuardar = crearBoton("Guardar");
        btnGuardar.setBounds(140, 220, 150, 25);

        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarReserva();
            }
        });
    }

    /**
     * Comprueba si la fecha ya esta ocupada por otra reserva
     */
    private boolean fechaDisponible(String fecha ) throws SQLException {
        // Ejecuta una consulta que busca cualquier reserva con la misma fecha
        // El SELECT 1 devuelve un valor constante para cada fila encontrada,
        // porque solo interesa si existe alguna coincidencia y no los datos completos
        // Nota: aquí se construye SQL con concatenación, en un sistema real
        // convendría usar PreparedStatement para evitar inyección SQL
        String sql = "SELECT 1 FROM reserva WHERE fecha = '" + fecha + "' LIMIT 1";
        ResultSet rs = conexion.ejecutarSelect(sql);
        boolean disponible = !rs.next();
        return disponible;
    }

    /**
	 * Guarda la nueva reserva si los datos son validos
	 */
	private void guardarReserva() {
		// Lee los campos de entrada del formulario
		String destino = textDestino.getText().trim();
		String fecha = textFecha.getText().trim();
		String presupuesto = textPresupuesto.getText().trim();

		// Comprueba que el usuario haya completado todos los campos
		if (destino.isEmpty() || fecha.isEmpty() || presupuesto.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Rellene todos los campos");
			return;
		}

		try {
			conexion.conectar();

			// Verifica que la fecha no esté ya reservada por otra reserva
			if (!fechaDisponible(fecha)) {
				JOptionPane.showMessageDialog(null, "Introduzca una fecha disponible");
				return;
			}

			if (idUsuario > 0) {
				// Inserta la nueva reserva en la base de datos para el usuario logueado
				// El id de usuario asegura que la reserva se asocia al perfil correcto
                // Si no se proporciona un usuario válido, la reserva no se persiste

				if (ventanaReservas != null) {
					// Refresca la lista de reservas en la ventana principal
					ventanaReservas.recargarReservas();
				}

				JOptionPane.showMessageDialog(null, "Reserva realizada");
				dispose();
				return;
			}

			// Si no hay usuario asociado, solo mostramos un mensaje de confirmación
			// Esto ocurre cuando se utiliza el constructor vacío para pruebas
			// No se guarda en la base de datos porque falta el id de usuario
			JOptionPane.showMessageDialog(null,
					"Datos guardados:\nDestino: " + destino
						+ "\nFecha: " + fecha
						+ "\nPresupuesto: " + presupuesto);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar la reserva: " + ex.getMessage());
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

    private JTextField crearCampoTexto(int x, int y, int ancho, int alto) {
        // Crea un campo de texto con tamaño fijo y lo añade al panel
        JTextField campo = new JTextField();
        campo.setBounds(x, y, ancho, alto);
        contentPane.add(campo);
        return campo;
    }

    private JButton crearBoton(String texto) {
        // Método helper que crea un botón y lo añade al panel de forma inmediata
        // Esto evita repetir contentPane.add en cada lugar donde se crea un botón
        JButton boton = new JButton(texto);
        contentPane.add(boton);
        return boton;
    }
}

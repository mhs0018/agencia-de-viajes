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

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    // Campos de texto donde el usuario escribe los datos
    protected JTextField textDestino;
    protected JTextField textFecha;
    protected JTextField textPresupuesto;

    public ConexionMySQL conexion = new ConexionMySQL("root", "", "agencia-viajes");
    private int idUsuario;
    private Reservas ventanaReservas;

    /**
     * Método main para ejecutar esta ventana de forma independiente.
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
     * Constructor principal.
     */
    public CrearReserva() {
        this(0, null);
    }

    /**
     * Constructor para crear una nueva reserva ligada a un usuario.
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
        contentPane.setLayout(null);

        crearEtiqueta("Nueva Reserva", 130, 10, 200, 30, 22);
        crearEtiqueta("Destino:", 40, 70, 100, 20, 16);
        crearEtiqueta("Fecha:", 40, 110, 100, 20, 16);
        crearEtiqueta("Presupuesto:", 40, 150, 120, 20, 16);

        textDestino = crearCampoTexto(160, 70, 180, 20);
        textFecha = crearCampoTexto(160, 110, 180, 20);
        textPresupuesto = crearCampoTexto(160, 150, 180, 20);

        JButton btnGuardar = crearBoton("Guardar");
        btnGuardar.setBounds(140, 220, 150, 25);

        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarReserva();
            }
        });
    }

    /**
     * Comprueba si la fecha ya está ocupada por otra reserva.
     */
    private boolean fechaDisponible(String fecha) throws SQLException {
        String sql = "SELECT id_reserva FROM reserva WHERE fecha = '" + fecha + "'";
        ResultSet rs = conexion.ejecutarSelect(sql);
        return !rs.next();
    }

    /**
     * Guarda la nueva reserva si los datos son válidos.
     */
    private void guardarReserva() {
        String destino = textDestino.getText().trim();
        String fecha = textFecha.getText().trim();
        String presupuesto = textPresupuesto.getText().trim();

        if (destino.isEmpty() || fecha.isEmpty() || presupuesto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Rellene todos los campos");
            return;
        }

        try {
            conexion.conectar();

            if (!fechaDisponible(fecha)) {
                JOptionPane.showMessageDialog(null, "Introduzca una fecha disponible");
                return;
            }

            if (idUsuario > 0) {
                String sql = "INSERT INTO reserva (destino, fecha, presupuesto, id_usuario) VALUES ('"
                        + destino + "', '" + fecha + "', '" + presupuesto + "', " + idUsuario + ")";
                conexion.ejecutarInsertDeleteUpdate(sql);

                if (ventanaReservas != null) {
                    ventanaReservas.recargarReservas();
                }

                JOptionPane.showMessageDialog(null, "Reserva realizada");
                dispose();
                return;
            }

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

    /**
     * Constructor secundario para rellenar datos existentes.
     */
    public CrearReserva(String destino, String fecha, String presupuesto) {
        this();
        setTitle("Modificar reserva");
        textDestino.setText(destino);
        textFecha.setText(fecha);
        textPresupuesto.setText(presupuesto);
    }

    private JLabel crearEtiqueta(String texto, int x, int y, int ancho, int alto, int tamanoFuente) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Tahoma", Font.PLAIN, tamanoFuente));
        etiqueta.setBounds(x, y, ancho, alto);
        contentPane.add(etiqueta);
        return etiqueta;
    }

    private JTextField crearCampoTexto(int x, int y, int ancho, int alto) {
        JTextField campo = new JTextField();
        campo.setBounds(x, y, ancho, alto);
        contentPane.add(campo);
        return campo;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        contentPane.add(boton);
        return boton;
    }
}

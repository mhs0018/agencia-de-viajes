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

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField text_Usuario;
	private JTextField text_Pass;

	// Creo el objeto que gestiona la conexión con la base de datos
	// Se puede reutilizar la misma instancia para varias consultas
	public ConexionMySQL conexion = new ConexionMySQL("root", "", "agencia-viajes");

	/**
	 * Launch the application
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
	 * Create the frame
	 */
	public Login() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// Etiqueta del titulo
		JLabel lbl_Titulo = new JLabel("¡Bienvenido!");
		lbl_Titulo.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lbl_Titulo.setBounds(151, 10, 145, 22);
		contentPane.add(lbl_Titulo);

		// Etiqueta del usuario
		JLabel lbl_Usuario = new JLabel("Usuario");
		lbl_Usuario.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lbl_Usuario.setBounds(48, 70, 108, 22);
		contentPane.add(lbl_Usuario);

		// Etiqueta de la contraseña
		JLabel lbl_Pass = new JLabel("Contraseña");
		lbl_Pass.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lbl_Pass.setBounds(48, 113, 108, 22);
		contentPane.add(lbl_Pass);

		// Campo usuario
		text_Usuario = new JTextField();
		text_Usuario.setFont(new Font("Tahoma", Font.PLAIN, 18));
		text_Usuario.setBounds(211, 75, 125, 18);
		contentPane.add(text_Usuario);
		text_Usuario.setColumns(10);

		// Campo contraseña
		text_Pass = new JTextField();
		text_Pass.setFont(new Font("Tahoma", Font.PLAIN, 18));
		text_Pass.setBounds(211, 118, 125, 18);
		contentPane.add(text_Pass);
		text_Pass.setColumns(10);

		// Boton de acceso
		JButton btnLogin = new JButton("Log in");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String usuario = text_Usuario.getText().trim();
				String pass = text_Pass.getText().trim();

				// Quita espacios en blanco no deseados para evitar errores por un espacio accidental
				// Luego valida que la contraseña tenga exactamente 12 caracteres
				if (pass.length() != 12) {
					JOptionPane.showMessageDialog(null, "La contraseña debe tener exactamente 12 caracteres");
					return;
				}

				try {
					conexion.conectar();

					// Consulta el usuario por nombre para comprobar su existencia
					// En una aplicación más segura se debería usar PreparedStatement en lugar de concatenar
					String sql = "SELECT * FROM usuario WHERE usuario = '" + usuario + "'";
					ResultSet rs = conexion.ejecutarSelect(sql);

					// Si no hay registros, el usuario no existe en la base de datos
					if (!rs.next()) {
						JOptionPane.showMessageDialog(null, "El usuario no existe");
						return;
					}

					// Compara la contraseña ingresada con la contraseña guardada
					String passGuardada = rs.getString("contraseña");
					if (!pass.equals(passGuardada)) {
						JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
						return;
					}

					// Si el login fue correcto, abre la ventana de reservas del usuario
					int idUsuario = rs.getInt("id_usuario");
					Reservas ventanaReservas = new Reservas(usuario, idUsuario);
					ventanaReservas.setVisible(true);

					// Limpia los campos en este formulario antes de cerrarlo
					text_Usuario.setText("");
					text_Pass.setText("");
					dispose();
				} catch (SQLException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error de conexión: " + ex.getMessage());
				} finally {
					// Siempre cierra la conexión para liberar recursos
					try {
						conexion.desconectar();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		btnLogin.setBounds(123, 205, 84, 20);
		contentPane.add(btnLogin);

		JButton btnSignUp = new JButton("Sing Up");
		btnSignUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Abre la ventana de registro sin cerrar esta pantalla
				// De esta forma el usuario puede volver al login fácilmente
				RegistroUsuario ventanaRegistro = new RegistroUsuario();
				ventanaRegistro.setVisible(true);
			}
		});
		btnSignUp.setBounds(217, 206, 95, 19);
		contentPane.add(btnSignUp);
	}
}

package BBDD;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField text_Usuario;
	private JTextField text_Pass;

	// CREO EL OBJETO QUE GESTIONA LA CONEXION CON LA BASE DE DATOS Y ESTARÁ
	// DISPONIBLE PARA TODO EL CÓDIGO
	public ConexionMySQL conexion = new ConexionMySQL("root", "", "agencia-viajes");

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
	public Login() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// Etiqueta del título
		JLabel lbl_Titulo = new JLabel("¡Bienvenido!");
		lbl_Titulo.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lbl_Titulo.setBounds(151, 10, 145, 22);
		contentPane.add(lbl_Titulo);

		// Etiqueta del usuario
		JLabel lbl_Usuario = new JLabel("Usuario");
		lbl_Usuario.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lbl_Usuario.setBounds(48, 70, 108, 22);
		contentPane.add(lbl_Usuario);

		// Etiqueta de la contraseña (pass)
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

		// Botón

		JButton btn_Insertar = new JButton("Log in");
		btn_Insertar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					conexion.conectar(); // Conecta con la BD
					String usuario = text_Usuario.getText().trim();
					String pass = text_Pass.getText().trim();

					// Consulta para verificar usuario y contraseña
					String sql = "SELECT * FROM usuario WHERE usuario = '" + usuario
							+ "' AND contraseña = '" + pass + "'";
					ResultSet rs = conexion.ejecutarSelect(sql);

					if (rs.next()) {
						// Login correcto: obtiene el id_usuario y abre Reservas
						int idUsuario = rs.getInt("id_usuario");
						Reservas ventanaReservas = new Reservas(usuario, idUsuario);
						ventanaReservas.setVisible(true);
						text_Usuario.setText("");
						text_Pass.setText("");
					} else {
						JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
					}
					conexion.desconectar();
				} catch (SQLException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error de conexión: " + ex.getMessage());
				}
			}
		});
		btn_Insertar.setBounds(123, 205, 84, 20);
		contentPane.add(btn_Insertar);

		JButton btn_SingUp = new JButton("Sing Up");
		btn_SingUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegistroUsuario x = new RegistroUsuario();
				x.setVisible(true);
			}
		});
		btn_SingUp.setBounds(217, 206, 95, 19);
		contentPane.add(btn_SingUp);

	}
}

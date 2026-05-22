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

public class RegistroUsuario extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField text_Usuario;
	private JTextField text_Pass;

	public ConexionMySQL conexion = new ConexionMySQL("root", "", "agencia-viajes");
	private JTextField text_NombreA;
	private JTextField text_DNI;
	private JTextField text_Email;

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
	public RegistroUsuario() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lbl_Titulo = new JLabel("Nuevo Usuario");
		lbl_Titulo.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lbl_Titulo.setBounds(151, 10, 145, 22);
		contentPane.add(lbl_Titulo);

		JLabel lbl_Usuario = new JLabel("Usuario");
		lbl_Usuario.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl_Usuario.setBounds(48, 42, 84, 18);
		contentPane.add(lbl_Usuario);

		JLabel lbl_Pass = new JLabel("Contraseña");
		lbl_Pass.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl_Pass.setBounds(48, 70, 108, 22);
		contentPane.add(lbl_Pass);

		JLabel lblNombreCompleto = new JLabel("Nombre y apellidos");
		lblNombreCompleto.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNombreCompleto.setBounds(48, 104, 145, 13);
		contentPane.add(lblNombreCompleto);

		JLabel lblDni = new JLabel("DNI");
		lblDni.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblDni.setBounds(48, 127, 44, 12);
		contentPane.add(lblDni);

		JLabel lblEmail = new JLabel("Email");
		lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblEmail.setBounds(48, 156, 44, 12);
		contentPane.add(lblEmail);

		text_Usuario = new JTextField();
		text_Usuario.setFont(new Font("Tahoma", Font.PLAIN, 18));
		text_Usuario.setBounds(211, 42, 125, 18);
		contentPane.add(text_Usuario);
		text_Usuario.setColumns(10);

		text_Pass = new JTextField();
		text_Pass.setFont(new Font("Tahoma", Font.PLAIN, 18));
		text_Pass.setBounds(211, 70, 125, 18);
		contentPane.add(text_Pass);
		text_Pass.setColumns(10);

		text_NombreA = new JTextField();
		text_NombreA.setBounds(211, 98, 125, 18);
		contentPane.add(text_NombreA);
		text_NombreA.setColumns(10);

		text_DNI = new JTextField();
		text_DNI.setBounds(211, 127, 125, 18);
		contentPane.add(text_DNI);
		text_DNI.setColumns(10);

		text_Email = new JTextField();
		text_Email.setBounds(211, 154, 125, 18);
		contentPane.add(text_Email);
		text_Email.setColumns(10);

		JButton btnSignUp = new JButton("Sign up");
		btnSignUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// Conecta con la BD
					conexion.conectar();
					String nombre = text_Usuario.getText(); // Lee el texto de la casilla nombre del formulario
					String contrasena = text_Pass.getText(); // Pasa a entero el texto leido de la casilla número
					String nombreApellido = text_NombreA.getText();
					String dni = text_DNI.getText();
					String email = text_Email.getText();
					String sql = "INSERT INTO USUARIOS (USUARIO,CONTRASEÑA,NOMBRE_COMPLETO,DNI,EMAIL) VALUES ('"
							+ nombre + ",'" + contrasena + ",'" + nombreApellido + ",'" + dni + ",'" + email + "')";
					conexion.ejecutarInsertDeleteUpdate(sql); // Manda la orden a la base de datos
					conexion.desconectar();

					JOptionPane.showMessageDialog(null, "Usuario creado correctamente");

					text_Usuario.setText("");
					text_Pass.setText("");
					text_NombreA.setText("");
					text_DNI.setText("");
					text_Email.setText("");

				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				Login ventanaLogin = new Login();
				ventanaLogin.setVisible(true);
				dispose();
			}
		});
		btnSignUp.setBounds(143, 205, 84, 20);
		contentPane.add(btnSignUp);

		JButton btnVolver = new JButton("Log in");
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Login ventanaLogin = new Login();
				ventanaLogin.setVisible(true);
				dispose();
			}
		});
		btnVolver.setBounds(237, 205, 84, 20);
		contentPane.add(btnVolver);
	}
}

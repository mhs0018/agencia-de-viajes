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
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class RegistroUsuario extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField text_Usuario;
	private JTextField text_Pass;

	// CREO EL OBJETO QUE GESTIONA LA CONEXION CON LA BASE DE DATOS Y ESTARÁ
	// DISPONIBLE PARA TODO EL CÓDIGO
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

		// Etiqueta del título
		JLabel lbl_Titulo = new JLabel("¡Bienvenido!");
		lbl_Titulo.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lbl_Titulo.setBounds(151, 10, 145, 22);
		contentPane.add(lbl_Titulo);

		// Etiqueta del usuario
		JLabel lbl_Usuario = new JLabel("Usuario");
		lbl_Usuario.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl_Usuario.setBounds(48, 42, 84, 18);
		contentPane.add(lbl_Usuario);

		// Etiqueta de la contraseña (pass)
		JLabel lbl_Pass = new JLabel("Contraseña");
		lbl_Pass.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl_Pass.setBounds(48, 70, 108, 22);
		contentPane.add(lbl_Pass);

		// Etiqueta del Nombre y Apellido
		JLabel lblNewLabel = new JLabel("Nombre y apellidos");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(48, 104, 145, 13);
		contentPane.add(lblNewLabel);

		// Etiqueta de DNI
		JLabel lblNewLabel_1 = new JLabel("DNI");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(48, 127, 44, 12);
		contentPane.add(lblNewLabel_1);

		// Etiqueta de Email
		JLabel lblNewLabel_2 = new JLabel("Email");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(48, 156, 44, 12);
		contentPane.add(lblNewLabel_2);

		// Campo usuario
		text_Usuario = new JTextField();
		text_Usuario.setFont(new Font("Tahoma", Font.PLAIN, 18));
		text_Usuario.setBounds(211, 42, 125, 18);
		contentPane.add(text_Usuario);
		text_Usuario.setColumns(10);

		// Campo contraseña
		text_Pass = new JTextField();
		text_Pass.setFont(new Font("Tahoma", Font.PLAIN, 18));
		text_Pass.setBounds(211, 70, 125, 18);
		contentPane.add(text_Pass);
		text_Pass.setColumns(10);

		// Campo Nombre Apellido
		text_NombreA = new JTextField();
		text_NombreA.setBounds(211, 98, 125, 18);
		contentPane.add(text_NombreA);
		text_NombreA.setColumns(10);

		// Campo DNi
		text_DNI = new JTextField();
		text_DNI.setBounds(211, 127, 125, 18);
		contentPane.add(text_DNI);
		text_DNI.setColumns(10);

		// Campo Email
		text_Email = new JTextField();
		text_Email.setBounds(211, 154, 125, 18);
		contentPane.add(text_Email);
		text_Email.setColumns(10);

		// Botón
		JButton btn_Insertar = new JButton("Sign in");
		btn_Insertar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					conexion.conectar();// Conecta con la BD
					String nombre = text_Usuario.getText(); // Lee el texto de la casilla nombre del formulario
					String contraseña = text_Pass.getText();// Pasa a entero el texto leido de la casilla número
					String NApellido = text_NombreA.getText();
					String DNI = text_DNI.getText();
					String Email = text_Email.getText();
					String sentencia = "INSERT INTO USUARIOS (USUARIO,CONTRASEÑA,NOMBRE_COMPLETO,DNI,EMAIL) VALUES ('"
							+ nombre + ",'" + contraseña + ",'" + NApellido + ",'" + DNI + ",'" + Email + "')";
					conexion.ejecutarInsertDeleteUpdate(sentencia);// Manda la orden a la base de datos
					conexion.desconectar();// Desconecta de la base de datos

					// Llamo a un aviso
					String mensaje = "Usuario introducido correctamente";
					JOptionPane.showMessageDialog(null, mensaje);

					// Dejo los campos vacios para el próximo usuario
					text_Usuario.setText("");
					text_Pass.setText("");
					text_NombreA.setText("");
					text_DNI.setText("");
					text_Email.setText("");

				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				Login x = new Login();
				x.setVisible(true);
			}
		});
		btn_Insertar.setBounds(174, 205, 84, 20);
		contentPane.add(btn_Insertar);

	}
}

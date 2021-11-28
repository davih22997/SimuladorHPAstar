package program;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

// Clase para crear la interfaz gráfica del simulador
public class Interfaz extends JFrame implements ActionListener {

	static private final String newline = "\n";

	// Panel con todo el contenido
	private JPanel panel;

	// Control de todas las acciones
	private JTextArea log;

	// Parte para la gestión de archivos
	private File archivo;
	private JButton btnSave, btnOpen, btnNew;
	private JFileChooser fc;
	private JLabel titulo;
	private JTextField direccion;
	private Dimension dIcons = new Dimension(16, 16);

	// Rutas de las imágenes que se usarán como iconos
	private final static String start = "/images/start.png";
	private final static String play = "/images/play.png";
	private final static String pause = "/images/pause.png";
	private final static String shutdown = "/images/shutdown.png";
	private final static String stop = "/images/stop.jpg";

	private final static String save16 = "/images/Save16.gif";
	private final static String open16 = "/images/Open16.gif";
	private final static String new16 = "/images/New16.gif";

	public Interfaz() {
		// ImageIcon st = new ImageIcon(getClass().getResource("/images/play.png"));
		// Definimos los parámetros de la aplicación (título, tamaño...):
		this.setTitle("Simulador");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		this.setContentPane(panel);

		// Creamos el logger de la aplicación
		log = new JTextArea(5, 20);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		log.append("Registro de acciones:" + newline + newline);
		JScrollPane logScrollPane = new JScrollPane(log);
		logScrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		this.add(logScrollPane, BorderLayout.CENTER);

		// Definimos el apartado para gestión de archivos

		// Creamos el gestor de archivos
		fc = new JFileChooser();

		// Creamos el filtro para el gestor
		FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos .txt", "txt");

		// Le indicamos el filtro al gestor de archivos
		fc.setFileFilter(filtro);

		// Creamos el título para ese apartado
		titulo = new JLabel("Archivos");
		titulo.setAlignmentX(CENTER_ALIGNMENT);
		// this.add(titulo, BorderLayout.NORTH);

		// this.add(titulo, BorderLayout.NORTH);

		// Añadimos un panel exclusivo para los botones
		JPanel btnPanel = new JPanel();

		// Damos a cada botón sus características propias:

		// Botón para nuevo archivo
		btnNew = new JButton();
		btnNew.setSize(dIcons);
		btnNew.setIcon(this.createIcon(new16, btnNew));
		btnNew.addActionListener(this);

		// Botón para abrir archivo
		btnOpen = new JButton();
		btnOpen.setSize(dIcons);
		btnOpen.setIcon(this.createIcon(open16, btnOpen));
		btnOpen.addActionListener(this);

		// Botón para guardar archivo
		btnSave = new JButton();
		btnSave.setSize(dIcons);
		btnSave.setIcon(this.createIcon(save16, btnSave));
		btnSave.addActionListener(this);

		// Barra con la dirección del archivo
		JLabel dirAct = new JLabel("Actual:");
		direccion = new JTextField(20);
		direccion.setEditable(false);
		direccion.setBackground(Color.white);

		// Añadimos los botones al panel de botones
		btnPanel.add(btnNew);
		btnPanel.add(btnOpen);
		btnPanel.add(btnSave);
		btnPanel.add(dirAct);
		btnPanel.add(direccion);

		Box vB1 = Box.createVerticalBox();
		vB1.add(titulo);
		vB1.add(btnPanel);
		vB1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		// this.panel.add(titulo, BorderLayout.NORTH);
		this.add(vB1, BorderLayout.NORTH);
		// this.panel.add(direccion, BorderLayout.NORTH);
		// this.add(logScrollPane, BorderLayout.SOUTH);

		this.pack();
		this.setVisible(true);

		/*
		 * this.setResizable(false); this.setBounds(100, 100, 600, 350);
		 * 
		 * JPanel contentPane = new JPanel(); contentPane.setBorder(new EmptyBorder(5,
		 * 5, 5, 5)); this.setContentPane(contentPane);
		 * 
		 * // Definimos el primer submenú (ALGORITMO): Box vB1 =
		 * Box.createVerticalBox(); vB1.setBorder(new BevelBorder(BevelBorder.LOWERED,
		 * null, null, null, null));
		 * 
		 * JLabel lbl1 = new JLabel("Algoritmo");
		 * lbl1.setAlignmentX(Component.CENTER_ALIGNMENT); vB1.add(lbl1);
		 * 
		 * JComboBox<String> cb1 = new JComboBox<>(); cb1.addItem("A*");
		 * cb1.addItem("HPA*"); cb1.setToolTipText(""); vB1.add(cb1);
		 * 
		 * // Definimos el segundo submenú (HEURÍSTICO): Box vB2 =
		 * Box.createVerticalBox(); vB2.setBorder(new BevelBorder(BevelBorder.LOWERED,
		 * null, null, null, null));
		 * 
		 * JLabel lbl2 = new JLabel("Heurístico");
		 * lbl2.setAlignmentX(Component.CENTER_ALIGNMENT); vB2.add(lbl2);
		 * 
		 * JComboBox<String> cb2 = new JComboBox<>(); cb2.addItem("DIST-MANHATAN");
		 * cb2.addItem("DIST-EUCLIDEA"); cb2.addItem("H=0"); vB2.add(cb2);
		 * 
		 * // Definimos el tercer menú (CONTROL SIMULACIÓN): Box vB3 =
		 * Box.createVerticalBox(); vB3.setBorder(new BevelBorder(BevelBorder.LOWERED,
		 * null, null, null, null));
		 * 
		 * JLabel lbl3 = new JLabel("Control simulación");
		 * lbl3.setAlignmentX(Component.CENTER_ALIGNMENT); vB3.add(lbl3);
		 * 
		 * JPanel p1 = new JPanel(); vB3.add(p1);
		 * 
		 * JSplitPane sp1 = new JSplitPane(); p1.add(sp1);
		 * 
		 * JButton b1 = new JButton("Play"); sp1.setLeftComponent(b1);
		 * 
		 * JButton b2 = new JButton("Stop"); sp1.setRightComponent(b2);
		 * 
		 * JLabel jl1 = new JLabel("Velocidad"); p1.add(jl1);
		 * 
		 * JSplitPane sp2 = new JSplitPane(); p1.add(sp2);
		 * 
		 * JButton b3 = new JButton("-"); sp2.setLeftComponent(b3);
		 * 
		 * JButton b4 = new JButton("+"); sp2.setRightComponent(b4);
		 * 
		 * // Lo añadimos todo al mismo GroupLayout GroupLayout gl = new
		 * GroupLayout(contentPane);
		 * gl.setHorizontalGroup(gl.createParallelGroup(Alignment.LEADING).addGroup(gl.
		 * createSequentialGroup() .addContainerGap()
		 * .addGroup(gl.createParallelGroup(Alignment.LEADING).addGroup(gl.
		 * createSequentialGroup() .addComponent(vB1, GroupLayout.PREFERRED_SIZE, 190,
		 * GroupLayout.PREFERRED_SIZE).addGap(165) .addComponent(vB2,
		 * GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)).addComponent(
		 * vB3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		 * GroupLayout.PREFERRED_SIZE)) .addContainerGap(GroupLayout.DEFAULT_SIZE,
		 * Short.MAX_VALUE)));
		 * gl.setVerticalGroup(gl.createParallelGroup(Alignment.LEADING)
		 * .addGroup(gl.createSequentialGroup()
		 * .addGroup(gl.createParallelGroup(Alignment.LEADING) .addComponent(vB2,
		 * GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		 * GroupLayout.PREFERRED_SIZE) .addComponent(vB1, GroupLayout.DEFAULT_SIZE,
		 * GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		 * .addPreferredGap(ComponentPlacement.RELATED).addComponent(vB3,
		 * GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		 * GroupLayout.PREFERRED_SIZE) .addGap(204))); // Lo añadimos como contenido de
		 * la aplicación: contentPane.setLayout(gl);
		 * 
		 */
		/*
		 * JButton b = new JButton();
		 * 
		 * b.setBounds(100, 100, 100, 40); b.setIcon(this.createIcon(stop, b));
		 * 
		 * this.add(b); this.setSize(300, 400);
		 * 
		 * this.setLayout(null); this.setVisible(true);
		 */
	}

	/**
	 * Método para obtener una imagen que se adapte al tamaño del botón
	 * 
	 * @param url    URL de la imagen
	 * @param button Botón al que se adapta la imagen
	 * @return La imagen adaptada
	 */
	protected ImageIcon createIcon(String url, JButton button) {

		return this.setIcon(new ImageIcon(getClass().getResource(url)), button);
	}

	// Modifica un icono para adaptarlo a un botón
	private ImageIcon setIcon(ImageIcon icon, JButton button) {
		// Recoge el ancho y el alto del botón
		int width = button.getWidth();
		int height = button.getHeight();

		// Devuelve la imagen reescalada
		return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		// Si pulsamos el botón de "nuevo"
		if (e.getSource() == btnNew) {
			direccion.setText("");

			log.append("Se ha seleccionado reiniciar el contenido." + newline);
			log.setCaretPosition(log.getDocument().getLength());
			// Si pulsamos en abrir
		} else if (e.getSource() == btnOpen) {
			// Seleccionamos el fichero
			int returnVal = fc.showOpenDialog(Interfaz.this);

			// Si pulsamos en aceptar
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				File file = fc.getSelectedFile();

				if (!file.exists()) {
					log.append("No existe el fichero: " + file.getName() + " en la ruta dada." + newline);
					log.append("Se ha cancelado la apertura de fichero." + newline);
				} else {
					direccion.setText(file.getAbsolutePath());

					// Registramos la apertura de fichero
					log.append("Abriendo fichero: " + file.getName() + "." + newline);
				}
			} else {
				log.append("Se ha cancelado la apertura de fichero." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());

			// Si pulsamos en guardar
		} else if (e.getSource() == btnSave) {
			int returnVal = fc.showSaveDialog(Interfaz.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();

				String name = file.getName();
				Pattern pat = Pattern.compile(".+[.]+[^.]+$");

				Matcher mat = pat.matcher(name);

				// Mira a ver si es un archivo con terminación en ".algo" y si no lo convierte
				// en ".txt"
				if (!mat.find()) {
					file = new File(file.getAbsolutePath() + ".txt");
				}
				/*
				 * if (!name.endsWith(".txt") || !name.endsWith(".TXT")) { file = new
				 * File(file.getAbsolutePath() + ".txt"); }
				 */

				if (file.exists()) {
					int result = JOptionPane.showConfirmDialog(this, "El archivo ya existe, ¿sobreescribir?",
							"Archivo ya existente", JOptionPane.YES_NO_OPTION);
					switch (result) {
					case JOptionPane.YES_OPTION:
						try (FileWriter fw = new FileWriter(file)) {
							fw.write("");
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						direccion.setText(file.getAbsolutePath());
						// Texto que aparece cuando sobreescibes el fichero ya existente
						log.append("Sobreescribiendo fichero: " + file.getName() + "." + newline);
						break;
					case JOptionPane.NO_OPTION:
						log.append("Se ha cancelado el guardado de archivo." + newline);
						break;
					}
				} else {
					try (FileWriter fw = new FileWriter(file)) {
						fw.write("");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					direccion.setText(file.getAbsolutePath());
					// Texto que aparece cuando guardas el fichero
					log.append("Guardando fichero: " + file.getName() + "." + newline);
				}

			} else {
				log.append("Se ha cancelado el guardado de fichero." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());
		}
	}

}

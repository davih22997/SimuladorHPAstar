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
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

// Clase para crear la interfaz gráfica del simulador
public class Interfaz extends JFrame implements ActionListener, ChangeListener {

	private static final String newline = "\n";

	// Lista de las dimensiones posibles a escoger
	private static final String[] dimensiones = { "40x40", "20x30", "30x20" };

	// Panel con todo el contenido
	private JPanel panel;

	// Control de todas las acciones
	protected static JTextArea log;

	// Dimensión para los botones con iconos
	private Dimension dIcons = new Dimension(16, 16);

	// Parte para la gestión de archivos
	private File archivo;
	private JButton btnSave, btnOpen, btnNew;
	private JFileChooser fc;
	private JLabel titulo;
	private JTextField direccion;
	private JPanel btnPanel;

	// Parte central
	private JPanel pCentral;

	// Parte para la gestión de algoritmo
	private JLabel titulo2;
	private JComboBox<String> algCB;
	private JPanel algPanel;

	// Parte para el control de la simulación
	private JLabel titulo3;
	private JButton btnStart, btnStop;
	// Control de velocidad
	private JButton btnMinus, btnPlus;
	private JPanel btnPanel2;
	// Variable que controla el valor de la velocidad:
	private float v = 1;
	// Para mostrar formato decimal
	private DecimalFormat frmt;

	// JLabel que mostrará el valor de la velocidad
	private JLabel velocity;

	// Parte para la configuración del mapa
	private JLabel titulo4;
	// Selección de las dimensiones del mapa
	private JLabel titulo4_1;
	private JPanel pDims;
	private JComboBox<String> dims;
	// Edición malla
	private JLabel titulo4_2;
	private JPanel pMalla;
	private ButtonGroup bGroup;
	private JRadioButton rInic, rFin, rObs, rCons;
	private JButton btnReverse;

	// Parte del Mapa
	private Mapa mapa = new Mapa(0, 0);

	public Interfaz() {
		// ImageIcon st = new ImageIcon(getClass().getResource("/images/play.png"));

		// Inicializamos el formato decimal
		frmt = new DecimalFormat();
		frmt.setMaximumFractionDigits(2);
		frmt.setMinimumFractionDigits(2);
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
		// log.append("Registro de acciones:" + newline + newline);
		JScrollPane logScrollPane = new JScrollPane(log);

		Box vB0 = Box.createVerticalBox();
		JLabel reg = new JLabel("Registro de acciones");
		reg.setAlignmentX(CENTER_ALIGNMENT);
		vB0.add(reg);
		vB0.add(logScrollPane);
		vB0.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		this.add(vB0, BorderLayout.SOUTH);

		// Definimos el apartado para GESTIÓN DE ARCHIVOS

		// Creamos el gestor de archivos
		fc = new JFileChooser();

		// Creamos el filtro para el gestor
		FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos .txt", "txt");

		// Le indicamos el filtro al gestor de archivos
		fc.setFileFilter(filtro);

		// Creamos el título para ese apartado
		titulo = new JLabel("Archivos");
		titulo.setAlignmentX(CENTER_ALIGNMENT);

		// Añadimos un panel exclusivo para los botones
		btnPanel = new JPanel();

		// Damos a cada botón sus características propias:

		// Botón para nuevo archivo
		btnNew = initButtonIcon(Direccion.new16);

		// Botón para abrir archivo
		btnOpen = initButtonIcon(Direccion.open16);

		// Botón para guardar archivo
		btnSave = initButtonIcon(Direccion.save16);

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

		// Agrupamos cada elemento de manera que esté uno encima de otro
		Box vB1 = Box.createVerticalBox();
		vB1.add(titulo);
		vB1.add(btnPanel);
		vB1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		this.add(vB1, BorderLayout.NORTH);

		// Parte CENTRAL del simulador
		// Inicializamos el panel central
		pCentral = new JPanel();
		pCentral.setLayout(new BorderLayout());

		// Definimos el apartado para la selección de ALGORITMO

		// Creamos el correspondiente titulo
		titulo2 = new JLabel("Algoritmo");
		titulo2.setAlignmentX(CENTER_ALIGNMENT);

		// Añadimos un panel exclusivo para la gestión de algoritmo

		algPanel = new JPanel();

		// Añadimos el selector
		algCB = new JComboBox<>();
		algCB.addItem("A*");
		algCB.addItem("HPA*");
		algCB.setBackground(Color.white);
		algCB.addActionListener(this);

		// Añadimos al panel el selector
		algPanel.add(algCB);

		// Agrupamos cada elemento de manera que esté uno encima de otro
		Box vB2 = Box.createVerticalBox();
		vB2.add(titulo2);
		vB2.add(algPanel);
		vB2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		Box boxizda = Box.createVerticalBox();
		boxizda.add(vB2);
		// pCentral.add(vB2, BorderLayout.WEST);

		// Definimos el apartado para el CONTROL SIMULACIÓN
		titulo3 = new JLabel("Control de la simulación");
		titulo3.setAlignmentX(CENTER_ALIGNMENT);

		// Añadimos un panel exclusivo para los botones
		btnPanel2 = new JPanel();
		// Damos a cada botón sus características propias:

		// Botón para empezar la simulación
		btnStart = initButtonIcon(Direccion.start16);

		// Botón para finalizar la simulación
		btnStop = initButtonIcon(Direccion.stop16);

		// Parte de la velocidad
		JLabel vel = new JLabel("Velocidad:");

		// Botón para reducir la velocidad
		btnMinus = initButtonIcon(Direccion.minus16);

		// Indicador de la velocidad
		velocity = new JLabel(new String("x").concat(frmt.format(v)));

		// Botón para aumentar la velocidad
		btnPlus = initButtonIcon(Direccion.plus16);

		// Añadimos todo al panel de botones
		btnPanel2.add(btnStart);
		btnPanel2.add(btnStop);
		btnPanel2.add(vel);
		btnPanel2.add(btnMinus);
		btnPanel2.add(velocity);
		btnPanel2.add(btnPlus);

		Box vB3 = Box.createVerticalBox();
		vB3.add(titulo3);
		vB3.add(btnPanel2);
		vB3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		// Lo añadimos al panel central

		boxizda.add(vB3);
		pCentral.add(boxizda, BorderLayout.WEST);

		// Definimos el apartado para la CONFIGURACIÓN DEL MAPA
		titulo4 = new JLabel("Configuración del mapa");
		titulo4.setAlignmentX(CENTER_ALIGNMENT);

		Box vB4 = Box.createVerticalBox();
		vB4.add(titulo4);
		vB4.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		// Añadimos el panel para la selección de las dimensiones
		pDims = new JPanel();
		titulo4_1 = new JLabel("Dimensiones:");
		// Añadimos el selector de dimensiones
		dims = new JComboBox<>();
		// Añadimos las opciones al selector

		dims.addItem("Seleccionar dimensiones");
		for (String tam : dimensiones)
			dims.addItem(tam);

		dims.setBackground(Color.white);
		dims.addActionListener(this);

		// Añadimos los componentes al panel
		pDims.add(titulo4_1);
		pDims.add(dims);

		vB4.add(pDims);

		// Añadimos el panel para la edición de las mallas
		titulo4_2 = new JLabel("Edición malla");
		titulo4_2.setAlignmentX(CENTER_ALIGNMENT);

		Box vB4_2 = Box.createVerticalBox();
		vB4_2.add(titulo4_2);

		// Creamos un panel para los botones de edición de malla
		pMalla = new JPanel();
		// Consulta (seleccionado por defecto)
		rCons = new JRadioButton("Consulta");
		rCons.addChangeListener(this);
		rCons.setSelected(true);
		// Punto inicial
		rInic = new JRadioButton("Inicial");
		rInic.addChangeListener(this);
		// Punto final
		rFin = new JRadioButton("Final");
		rFin.addChangeListener(this);
		// Obstáculo
		rObs = new JRadioButton("Obstáculo");
		rObs.addChangeListener(this);

		// Botón para cambiar inicio-fin
		btnReverse = initButtonIcon(Direccion.refresh16);

		// Añadimos los botones al panel
		pMalla.add(rCons);
		pMalla.add(rInic);
		pMalla.add(rFin);
		pMalla.add(rObs);
		pMalla.add(btnReverse);

		// Añadimos todos los RadioButtons al mismo grupo
		bGroup = new ButtonGroup();
		bGroup.add(rCons);
		bGroup.add(rInic);
		bGroup.add(rFin);
		bGroup.add(rObs);

		// Lo añadimos al VerticalBox
		vB4_2.add(pMalla);

		// Finalmente, lo añadimos todo al panel central
		vB4.add(vB4_2);
		pCentral.add(vB4, BorderLayout.CENTER);

		// Parte del Mapa
		// mapa = new Mapa(40, 40);
		// mapa.crearTablero();

		pCentral.add(mapa.tablero, BorderLayout.SOUTH);

		// Añadimos el panel central
		this.add(pCentral, BorderLayout.WEST);

		// Empaquetamos y hacemos visible
		log.append("Iniciamos la interfaz con los valores por defecto." + newline);
		this.pack();
		this.setVisible(true);
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

	/**
	 * Método para crear un botón de un icono
	 * 
	 * @param dim Tamaño del botón (dimensiones)
	 * @param dir Dirección de la imagen del icono
	 * @return El botón
	 */
	protected JButton initButtonIcon(Dimension dim, String dir) {
		JButton res = new JButton();
		// Establece el tamaño
		res.setSize(dim);
		// Establece el icono
		res.setIcon(this.createIcon(dir, res));
		// Además le añade un ActionListener
		res.addActionListener(this);

		return res;
	}

	/**
	 * Método para crear un botón de un icono usando la variable dIcons para las
	 * dimensiones
	 * 
	 * @param dir Dirección de la imagen del icono
	 * @return El botón
	 */
	protected JButton initButtonIcon(String dir) {
		return initButtonIcon(dIcons, dir);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		// Si pulsamos el botón de "nuevo"
		if (e.getSource() == btnNew) {
			direccion.setText("");

			log.append("Se ha seleccionado reiniciar el contenido." + newline);
			log.setCaretPosition(log.getDocument().getLength());
		}
		// Si pulsamos en abrir
		else if (e.getSource() == btnOpen) {
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

		}
		// Si pulsamos en guardar
		else if (e.getSource() == btnSave) {

			int filas = mapa.getFilas();
			int cols = mapa.getCols();
			if (filas == 0 && cols == 0) {
				JOptionPane.showMessageDialog(new JFrame(),
						"Debe seleccionar un tamaño de mapa para poder guardar en un fichero.");
			} else {

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
								StringBuilder sb = new StringBuilder();

								sb.append("Mapa:" + newline);
								sb.append("Dimensiones: " + filas + " x " + cols + newline);

								sb.append("Punto inicial: ");
								if (mapa.pto_inicial == null)
									sb.append("NULL");
								else
									sb.append(mapa.pto_inicial.toString());
								sb.append(newline);

								sb.append("Punto final: ");
								if (mapa.pto_final == null)
									sb.append("NULL");
								else
									sb.append(mapa.pto_final.toString());
								sb.append(newline);

								sb.append("Obstáculos: [");

								Iterator<Punto> it = mapa.obstaculos.iterator();

								while (it.hasNext()) {
									sb.append(it.next());
									if (it.hasNext())
										sb.append(", ");
								}

								sb.append("]" + newline);

								fw.write(sb.toString());

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
							StringBuilder sb = new StringBuilder();

							sb.append("Mapa:" + newline);
							sb.append("Dimensiones: " + filas + " x " + cols + newline);

							sb.append("Punto inicial: ");
							if (mapa.pto_inicial == null)
								sb.append("NULL");
							else
								sb.append(mapa.pto_inicial.toString());
							sb.append(newline);

							sb.append("Punto final: ");
							if (mapa.pto_final == null)
								sb.append("NULL");
							else
								sb.append(mapa.pto_final.toString());
							sb.append(newline);

							sb.append("Obstáculos: [");

							Iterator<Punto> it = mapa.obstaculos.iterator();

							while (it.hasNext()) {
								sb.append(it.next());
								if (it.hasNext())
									sb.append(", ");
							}

							sb.append("]" + newline);

							fw.write(sb.toString());

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
		// Controlador de la parte del algoritmo
		else if (e.getSource() == algCB) {
			String option = algCB.getSelectedItem().toString();
			if (option.equals("A*")) {
				log.append("Se ha seleccionado el algoritmo A*." + newline);
			} else if (option.equals("HPA*")) {
				log.append("Se ha seleccionado el algoritmo HPA*." + newline);
			}
		}
		// Control de la velocidad
		// Si pulsamos el botón de reducir velocidad
		else if (e.getSource() == btnMinus) {
			log.append("Se ha reducido la velocidad de la simulación " + newline);

			v -= 0.25;
			velocity.setText(new String("x").concat(frmt.format(v)));
			// No puede bajar de 0.25
			if (v == 0.25) {
				btnMinus.setEnabled(false);
				log.append("Se ha alcanzado el mínimo de velocidad" + newline);
			}

			// Si el botón de aumentar estaba bloqueado
			if (!btnPlus.isEnabled())
				btnPlus.setEnabled(true);

		}
		// Si pulsamos el botón de aumentar velocidad
		else if (e.getSource() == btnPlus) {
			log.append("Se ha aumentado la velocidad de la simulación " + newline);

			v += 0.25;
			velocity.setText(new String("x").concat(frmt.format(v)));
			// No puede subir de 2.00
			if (v == 2) {
				btnPlus.setEnabled(false);
				log.append("Se ha alcanzado el máximo de velocidad" + newline);
			}

			// Si el botón de reducir estaba bloqueado
			if (!btnMinus.isEnabled())
				btnMinus.setEnabled(true);
		}
		// Controlador del selector de las dimensiones del mapa
		else if (e.getSource() == dims) {

			String option = dims.getSelectedItem().toString();
			if (option.equals(dimensiones[0])) { // 40x40
				if (mapa != null)
					mapa.destruirTablero();
				mapa.setDims(40, 40);
				mapa.crearTablero();
			} else if (option.equals(dimensiones[1])) { // 20x30
				if (mapa != null)
					mapa.destruirTablero();

				mapa.setDims(20, 30);
				mapa.crearTablero();
			} else if (option.equals(dimensiones[2])) { // 30x20
				if (mapa != null)
					mapa.destruirTablero();

				mapa.setDims(30, 20);
				mapa.crearTablero();
			} else { // Seleccionar dimensiones
				if (mapa != null)
					mapa.destruirTablero();
				mapa.setDims(0, 0);
			}

		}
		// Si se pulsa el botón de cambio de inicio por fin
		else if (e.getSource() == btnReverse) {
			if (mapa.pto_final != null && mapa.pto_inicial != null) {
				Punto aux = mapa.pto_final.clone();
				mapa.pto_final = mapa.pto_inicial;
				mapa.pto_inicial = aux;
				mapa.MatrizBotones[mapa.pto_final.getFila()][mapa.pto_final.getCol()].setBackground(Color.RED);
				mapa.MatrizBotones[mapa.pto_inicial.getFila()][mapa.pto_inicial.getCol()].setBackground(Color.GREEN);

				log.append("Se han intercambiado las posiciones de los puntos inicial y final." + newline);
			} else if (mapa.pto_final != null) {
				mapa.pto_inicial = mapa.pto_final.clone();
				mapa.MatrizBotones[mapa.pto_inicial.getFila()][mapa.pto_inicial.getCol()].setBackground(Color.GREEN);
				mapa.pto_final = null;

				log.append("Se ha convertido el punto final en un punto inicial." + newline);
			} else if (mapa.pto_inicial != null) {
				mapa.pto_final = mapa.pto_inicial.clone();
				mapa.MatrizBotones[mapa.pto_final.getFila()][mapa.pto_final.getCol()].setBackground(Color.RED);
				mapa.pto_inicial = null;

				log.append("Se ha convertido el punto inicial en un punto final." + newline);
			}
		}
	}

	@Override
	/**
	 * Gestor de los JRadioButtons
	 */
	public void stateChanged(ChangeEvent e) {
		// Si seleccionamos la opción de consulta
		if (rCons.isSelected())
			mapa.setTipo(Mapa.TIPO_CONSULTA);
		// Si seleccionamos la opción de pto inicial
		else if (rInic.isSelected())
			mapa.setTipo(Mapa.TIPO_INICIAL);
		// Si seleccionamos la opción de pto final
		else if (rFin.isSelected())
			mapa.setTipo(Mapa.TIPO_FINAL);
		// Si seleccionamos la opción de obstáculo
		else if (rObs.isSelected())
			mapa.setTipo(Mapa.TIPO_OBSTACULO);

	}

}

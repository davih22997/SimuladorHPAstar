package program;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
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

	// Constantes String muy usadas
	private static final String newline = "\n";
	private static final String space = "(\t|\s)";
	private static final String number = "(([1-9][0-9]+)|[0-9])";

	// Patrones que se usan
	// Patrón lista
	private static final Pattern patlist = Pattern.compile(space + "*[{].*[}]" + space + "*");
	// Patrón punto
	private static final Pattern patpoint = Pattern.compile(
			space + "*[(]" + space + "*" + number + space + "*," + space + "*" + number + space + "*[)]" + space + "*");
	// Patrón archivo (se comprueba que finalice en .algo)
	private static final Pattern patfile = Pattern.compile(".+[.]+[^.]+$");

	// Lista de las dimensiones de mapa posibles a escoger
	private static final String[] dimensiones = { "40x40", "20x30", "30x20" };

	// Lista de números de vecinos para el algoritmo A*
	private static final String[] numVecinos = { "4-vecinos", "8-vecinos" };

	// Lista de las dimensiones de cluster posibles a escoger
	private static final String[] clusters = { "10x10", "5x5" };

	// Texto por defecto para seleccionar las dimensiones
	private static final String selDims = "Seleccionar dimensiones";

	// Texto por defecto para seleccionar número de vecinos
	// private static final String selNum = "Seleccionar cantidad";

	// Panel con todo el contenido
	private JPanel panel;

	// Control de todas las acciones
	protected static JTextArea log;

	// Dimensión para los botones con iconos
	private Dimension dIcons = new Dimension(16, 16);

	// Parte para la gestión de archivos
	private JButton btnSave, btnOpen, btnNew;
	private JFileChooser fc;
	private JLabel titulo;
	private JTextField direccion;
	private JPanel btnPanel;

	// Parte central
	private JPanel pCentral;

	// Parte para la gestión de algoritmo
	// Panel que contendrá la info de arriba (selector de algoritmo + selector de
	// número de vecinos / selector de cluster)
	private JPanel upPanel;
	// Parte para la gestión de A* en la parte de algoritmo
	private JLabel titulo2;
	private JComboBox<String> algCB;
	private JPanel algPanel;

	// Parte para la gestión de A* en la parte de algoritmo (incluye selector de
	// algoritmo y de cantidad de vecinos
	private JLabel titulo2a;
	private Box vB2a;
	private JPanel vecPanel;
	private JComboBox<String> vecCB;

	// Parte para la gestión de HPA* en la parte de algoritmo (incluye selector de
	// algoritmo y de dimensiones de cluster)
	// Parte para la gestión de selección de tamaño de cluster
	private JLabel titulo2hpa;
	private Box vB2hpa;
	private JPanel clusterPanel;
	private JComboBox<String> cbTCluster;

	// Parte para el control de la simulación
	private JLabel titulo3;

	// Panel de control de simulación solo para A*
	private JPanel panelCAstar;
	protected static JButton btnStart, btnStop;
	protected static boolean start = false;
	// Control de velocidad
	private JButton btnMinus, btnPlus;
	private JPanel btnPanel2;
	// Variable que controla el valor de la velocidad:
	protected static float v = 1;
	// Para mostrar formato decimal
	private DecimalFormat frmt;
	// JLabel que mostrará el valor de la velocidad
	private JLabel vel;
	private JLabel velocity;

	// Panel de control de simulación solo para HPA*
	private JPanel panelCHPAstar;
	protected static JButton btnStart2, btnStop2;

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
	protected Mapa mapa = new Mapa(0, 0);
	private Box mapaBox = Box.createVerticalBox();
	// Copia de seguridad del mapa para HPA*
	// private JButton[][] copiaMapa;

	// Parte de datos para la simulación A*
	protected static JLabel datosAstar;

	// Parte para la simulación de HPA*
	// Te cuenta cuántas veces has pulsado el botón de simulación
	private int step = 0;

	// Para la creación de puntos desde el fichero
	// private Punto pto_inicial;
	// private Punto pto_final;
	// private ArrayList<Punto> obstaculos;

	public Interfaz() {
		// ImageIcon st = new ImageIcon(getClass().getResource("/images/play.png"));

		// Inicializamos el formato decimal (para la velocidad)
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
		direccion = new JTextField(40);
		direccion.setEditable(false);
		direccion.setBackground(Color.WHITE);

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

		// Creamos el panel que contendrá todo
		upPanel = new JPanel();
		// Creamos el correspondiente titulo
		titulo2 = new JLabel("Algoritmo");
		titulo2.setAlignmentX(CENTER_ALIGNMENT);

		// Añadimos un panel exclusivo para la gestión de algoritmo
		algPanel = new JPanel();

		// Añadimos el selector
		algCB = new JComboBox<>();
		algCB.addItem("A*");
		algCB.addItem("HPA*");
		algCB.setBackground(Color.WHITE);
		algCB.addActionListener(this);

		// Añadimos al panel el selector
		algPanel.add(algCB);

		// Agrupamos cada elemento de manera que esté uno encima de otro
		Box vB2 = Box.createVerticalBox();
		vB2.add(titulo2);
		vB2.add(algPanel);
		// vB2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		// Parte de la gestión de algoritmo de HPA* (incluye clusters)
		// Le creamos el título para los clusters
		titulo2hpa = new JLabel("Clusters");
		titulo2hpa.setAlignmentX(CENTER_ALIGNMENT);

		// Añadimos un panel exclusivo para la gestión de tamaño de clusters
		clusterPanel = new JPanel();
		// Creamos la parte para seleccionar el tamaño de cluster
		cbTCluster = new JComboBox<>();

		cbTCluster.addItem(selDims);
		for (String tam : clusters)
			cbTCluster.addItem(tam);

		cbTCluster.setBackground(Color.WHITE);
		cbTCluster.addActionListener(this);

		clusterPanel.add(cbTCluster);

		// Agrupamos los elementos de forma que estén uno sobre el otro:
		vB2hpa = Box.createVerticalBox();
		vB2hpa.add(titulo2hpa);
		vB2hpa.add(clusterPanel);

		// Lo añadimos todo al panel general
		upPanel.add(vB2);
		upPanel.add(vB2hpa);
		upPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		upPanel.setPreferredSize(upPanel.getPreferredSize());
		// Ocultamos la parte de clusters (ya que por defecto está seleccionado A*)
		vB2hpa.setVisible(false);

		// Parte de la gestión de algoritmo A* (incluye número de vecinos)
		// Le creamos el título para la cantidad de vecinos
		titulo2a = new JLabel("Cantidad de vecinos");
		titulo2a.setAlignmentX(CENTER_ALIGNMENT);

		// Añadimos un panel exclusivos para la gestión de cantidad de vecinos
		vecPanel = new JPanel();
		// Creamos la parte para seleccionar la cantidad de vecinos
		vecCB = new JComboBox<>();

		// vecCB.addItem(selNum);

		// Por defecto tendrá la opción de 4-vecinos
		for (String vec : numVecinos)
			vecCB.addItem(vec);

		vecCB.setBackground(Color.WHITE);
		vecCB.addActionListener(this);

		vecPanel.add(vecCB);

		// Agrupamos los elementos para que estén uno sobre el otro:
		vB2a = Box.createVerticalBox();
		vB2a.add(titulo2a);
		vB2a.add(vecPanel);

		// Lo añadimos al panel general
		upPanel.add(vB2a);
		// No quitamos la opción de que sea visible, dado que por defecto estará
		// seleccionado el algoritmo A*
		vB2a.setVisible(true);

		// Creamos una caja y le añadimos los elementos
		Box boxizda = Box.createVerticalBox();
		boxizda.add(upPanel);
		// boxizda.add(pAstar);
		// pCentral.add(vB2, BorderLayout.WEST);

		// Definimos el apartado para el CONTROL SIMULACIÓN
		titulo3 = new JLabel("Control de la simulación");
		titulo3.setAlignmentX(CENTER_ALIGNMENT);

		// Añadimos un panel exclusivo para los botones
		btnPanel2 = new JPanel();

		// Parte de A*:
		panelCAstar = new JPanel();

		// Damos a cada botón sus características propias:
		// Botón para empezar la simulación
		btnStart = initButtonIcon(Direccion.start16);

		// Botón para finalizar la simulación
		btnStop = initButtonIcon(Direccion.stop16);
		btnStop.setEnabled(false);

		// Añadimos los botones de inicio / fin al panel
		panelCAstar.add(btnStart);
		panelCAstar.add(btnStop);

		// Parte de la velocidad
		vel = new JLabel("Velocidad:");

		// Botón para reducir la velocidad
		btnMinus = initButtonIcon(Direccion.minus16);

		// Indicador de la velocidad
		velocity = new JLabel(new String("x").concat(frmt.format(v)));

		// Botón para aumentar la velocidad
		btnPlus = initButtonIcon(Direccion.plus16);

		// Añadimos la parte de velocidad al panel de A*
		panelCAstar.add(vel);
		panelCAstar.add(btnMinus);
		panelCAstar.add(velocity);
		panelCAstar.add(btnPlus);

		// Añadimos todo al panel general
		btnPanel2.add(panelCAstar);
		btnPanel2.setPreferredSize(btnPanel2.getPreferredSize());

		// Parte de HPA*:
		panelCHPAstar = new JPanel();

		// Creamos una copia de los botones de inicio y de fin de la simulación
		// Botón para empezar la simulación
		btnStart2 = initButtonIcon(Direccion.start16);

		// Botón para finalizar la simulación
		btnStop2 = initButtonIcon(Direccion.stop16);
		btnStop2.setEnabled(false);

		// Añadimos los botones de inicio / fin al panel
		panelCHPAstar.add(btnStart2);
		panelCHPAstar.add(btnStop2);

		// Añadimos todo al panel general
		btnPanel2.add(panelCHPAstar);
		// Y ocultamos la parte de HPA* (por defecto está en A*)
		panelCHPAstar.setVisible(false);

		/*
		 * // Añadimos todo al panel de botones btnPanel2.add(btnStart);
		 * btnPanel2.add(btnStop); btnPanel2.add(vel); btnPanel2.add(btnMinus);
		 * btnPanel2.add(velocity); btnPanel2.add(btnPlus);
		 */

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
		dims.addItem(selDims);
		for (String tam : dimensiones)
			dims.addItem(tam);

		dims.setBackground(Color.WHITE);
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
		// Añadimos el mapa y el texto con los datos de la simulación dentro de una caja
		// vertical
		mapaBox.add(mapa.tablero);
		datosAstar = new JLabel();
		datosAstar.setAlignmentX(CENTER_ALIGNMENT);
		mapaBox.add(datosAstar);

		// Definimos el tamaño de la caja con el mapa y los datos, para que no afecte a
		// la simulación
		mapaBox.setPreferredSize(mapaBox.getPreferredSize());
		// Escondemos los datos (se van a mostrar solo en simulación).
		datosAstar.setVisible(false);

		// Añadimos la caja al panel central
		pCentral.add(mapaBox, BorderLayout.SOUTH);

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

	/**
	 * Método para contar los espacios al principio de un String
	 * 
	 * @param s
	 * @return
	 */
	private int cuentaEspacios(String s) {
		int cont = 0;
		int i = 0;
		while (s.charAt(i) == '\t' || s.charAt(i) == ' ') {
			cont++;
			i++;
		}

		return cont;
	}

	/**
	 * Gestor de todas las acciones.
	 */
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

					boolean mapaValido = true;
					direccion.setText(file.getAbsolutePath());

					// Registramos la apertura de fichero
					log.append("Abriendo fichero: " + file.getName() + "." + newline);

					try (Scanner sc = new Scanner(file)) {
						log.append("Cargando datos..." + newline);
						// Son 5 líneas
						// Linea mapa (inutil)
						sc.nextLine();
						// String linea1 = sc.nextLine();
						// Linea Dimensiones: Filas x Columnas
						String linea2 = sc.nextLine().toUpperCase();
						try (Scanner scan = new Scanner(linea2)) {
							scan.useDelimiter("(\t|\s)*DIMENSIONES(\t|\s)*:(\t|\s)*");
							// (FILAS X COLUMNAS)
							String dimens = scan.next();
							try (Scanner scan2 = new Scanner(dimens)) {
								scan2.useDelimiter("(\t|\s)*X(\t|\s)*");
								int dim1 = scan2.nextInt();
								int dim2 = scan2.nextInt();
								if (dim1 == 40 && dim2 == 40) {
									dims.setSelectedItem(dimensiones[0]);
								} else if (dim1 == 20 && dim2 == 30) {
									dims.setSelectedItem(dimensiones[1]);
								} else if (dim1 == 30 && dim2 == 20) {
									dims.setSelectedItem(dimensiones[2]);
								} else {
									JOptionPane.showMessageDialog(new JFrame(),
											"Las dimensiones dadas no se encuentran entre las opciones disponibles.\nNo se creará un mapa nuevo a partir del fichero.");
									mapaValido = false;
								}

							} catch (NumberFormatException exc) {
								JOptionPane.showMessageDialog(new JFrame(),
										"Formato inválido para definir el mapa. No se creará");
								mapaValido = false;
							}
						}
						if (mapaValido) {
							log.append("Cargadas las dimensiones del mapa." + newline);
							// Definimos un punto inicial
							Punto pto_inicial = null;
							// Definimos un punto final
							Punto pto_final = null;
							// Definimos la lista de obstaculos
							ArrayList<Punto> obstaculos = new ArrayList<>();
							// Punto inicial: (FILA, COLUMNA)
							String linea3 = sc.nextLine().toUpperCase();
							try (Scanner scan = new Scanner(linea3)) {
								scan.useDelimiter("(\t|\s)*PUNTO(\t|\s)+INICIAL(\t|\s)*:(\t|\s)*");
								// (x,y)
								String ptoinic = scan.next();
								if (ptoinic.equals("NULL"))
									pto_inicial = null;
								else {
									// Comprobamos que tenga el formato de un punto
									Matcher matpoint = patpoint.matcher(ptoinic);
									// Si no lo tiene
									if (!matpoint.matches()) {
										JOptionPane.showMessageDialog(new JFrame(),
												"Formato no válido para definir un punto. Se le asignará el valor null");
									} // Si lo tiene
									else {
										try (Scanner scan2 = new Scanner(ptoinic)) {
											scan2.useDelimiter(space + "*[(]" + space + "*");
											try (Scanner scan3 = new Scanner(scan2.next())) {
												scan3.useDelimiter(space + "*," + space + "*");
												int f = scan3.nextInt();
												try (Scanner scan4 = new Scanner(scan3.next())) {
													scan4.useDelimiter(space + "*[)]");
													int c = scan4.nextInt();
													pto_inicial = new Punto(f, c);
													mapa.MatrizBotones[f][c].setBackground(Mapa.cInicial);
													mapa.pto_inicial = pto_inicial;
												}
											}
										} catch (NumberFormatException exc) {
											JOptionPane.showMessageDialog(new JFrame(),
													"Debe dar un formato válido para el punto. Se le asignará el valor null.");
										} catch (Exception exc) {
											JOptionPane.showMessageDialog(new JFrame(),
													"Valores no válidos para el punto. Se le asignará el valor null.");
										}
									}
								}

								log.append("Cargados los datos relativos al punto inicial." + newline);
							}

							// Punto final: (FILA, COLUMNA)
							String linea4 = sc.nextLine().toUpperCase();
							try (Scanner scan = new Scanner(linea4)) {
								scan.useDelimiter(space + "*PUNTO" + space + "+FINAL" + space + "*:" + space + "*");
								// (x, y)
								String ptofin = scan.next();
								if (ptofin.equals("NULL"))
									pto_final = null;
								else {
									// Comprobamos que tenga el formato de un punto
									Matcher matpoint = patpoint.matcher(ptofin);
									// Si no lo tiene
									if (!matpoint.matches()) {
										JOptionPane.showMessageDialog(new JFrame(),
												"Formato no válido para definir un punto. Se le asignará el valor null");
									} // Si lo tiene
									else {
										try (Scanner scan2 = new Scanner(ptofin)) {
											scan2.useDelimiter("(" + space + "*[(]" + space + "*)");
											try (Scanner scan3 = new Scanner(scan2.next())) {
												scan3.useDelimiter(space + "*," + space + "*");
												int f = scan3.nextInt();
												try (Scanner scan4 = new Scanner(scan3.next())) {
													scan4.useDelimiter(space + "*[)]" + space + "*");
													int c = scan4.nextInt();
													pto_final = new Punto(f, c);
													if (pto_final.equals(pto_inicial))
														JOptionPane.showMessageDialog(new JFrame(),
																"El punto final definido coincide con el punto inicial definido. Se le asignará el valor null.");
													else {
														mapa.MatrizBotones[f][c].setBackground(Mapa.cFinal);
														mapa.pto_final = pto_final;
													}
												}
											}
										} catch (NumberFormatException exc) {
											JOptionPane.showMessageDialog(new JFrame(),
													"Debe dar un formato válido para el punto. Se le asignará el valor null.");
										} catch (Exception exc) {
											JOptionPane.showMessageDialog(new JFrame(),
													"Valores no válidos para el punto. Se le asignará el valor null.");
										}
									}
								}

								log.append("Cargados los datos relativos al punto final." + newline);
							}

							// Obstáculos: {Punto1, Punto2, ... }
							String linea5 = sc.nextLine().toUpperCase();
							try (Scanner scan = new Scanner(linea5)) {
								scan.useDelimiter(space + "*OBSTÁCULOS" + space + "*:" + space + "*");
								// { lista_ptos }
								String lista = scan.next();
								// Comprobamos que siga el patrón lista
								Matcher matlist = patlist.matcher(lista);
								// Si no sigue el patrón, creamos una lista vacía.
								if (!matlist.matches()) {
									JOptionPane.showMessageDialog(new JFrame(),
											"La lista de obstáculos debe ser dada como lista. Se generará una lista vacía.");
								} // Si lo sigue, operamos
								else {

									// Quitamos el inicio de la lista
									try (Scanner scan2 = new Scanner(lista.substring(1))) {
										// booleano para comprobar si hay puntos ya definidos
										boolean rep = false;

										scan2.useDelimiter("([)]" + space + "*," + space + "*)" + "|([)]" + space
												+ "*[}]" + space + "*)");

										while (scan2.hasNext()) {
											String p = scan2.next();
											Matcher matpoint = patpoint.matcher(p + ")");
											if (!matpoint.matches()) {
												throw new Exception();
											} else {
												Punto obs = new Punto(p.substring(cuentaEspacios(p) + 1));
												if (obs.equals(pto_inicial) || obs.equals(pto_final)
														|| obstaculos.contains(obs)) {
													rep = true;
												} else
													obstaculos.add(obs);
											}
										}

										for (Punto o : obstaculos) {
											mapa.MatrizBotones[o.getFila()][o.getCol()].setBackground(Mapa.cObs);
											mapa.obstaculos.add(o);
										}

										if (rep) {
											JOptionPane.showMessageDialog(new JFrame(),
													"Alguno de los puntos dados ya se ha definido, no se añadirá como nuevo obstáculo.");
										}

										if (obstaculos.size() == 1)
											log.append("Se ha añadido 1 obstáculo." + newline);
										else
											log.append("Se ha añadido un total de " + obstaculos.size() + " obstáculos."
													+ newline);
									} catch (Exception exp) {
										JOptionPane.showMessageDialog(new JFrame(),
												"Valores no válidos para puntos. No se añadirán obstáculos.");
									}
								}

								// Ordenamos la lista de obstaculos (si viene desordenada y le das a guardar
								// nuevamente, te la ordena)
								Collections.sort(mapa.obstaculos);

								log.append("Cargados los datos relativos a la lista de obstáculos." + newline);

							}

						} else {
							log.append("Dimensiones no válidas para el mapa. Se cancela la carga de datos." + newline);
							mapaValido = true;
						}

					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

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
						"Debe seleccionar al menos un tamaño de mapa para poder guardar datos en un fichero.");
			} else {

				int returnVal = fc.showSaveDialog(Interfaz.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();

					String name = file.getName();

					// Para comprobar que el nombre sigue el patrón dado
					Matcher mat = patfile.matcher(name);

					// Mira a ver si es un archivo con terminación en ".algo" y si no lo convierte
					// en ".txt"
					if (!mat.matches()) {
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

								sb.append("Obstáculos: {");

								Iterator<Punto> it = mapa.obstaculos.iterator();

								while (it.hasNext()) {
									sb.append(it.next());
									if (it.hasNext())
										sb.append(", ");
								}

								sb.append("}" + newline);

								fw.write(sb.toString());

							} catch (IOException e1) {
								e1.printStackTrace();
							}
							direccion.setText(file.getAbsolutePath());
							// Texto que aparece cuando sobreescribes el fichero ya existente
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

							sb.append("Obstáculos: {");

							Iterator<Punto> it = mapa.obstaculos.iterator();

							while (it.hasNext()) {
								sb.append(it.next());
								if (it.hasNext())
									sb.append(", ");
							}

							sb.append("}" + newline);

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
				// Ocultamos elementos propios de HPA*
				panelCHPAstar.setVisible(false);
				vB2hpa.setVisible(false);
				// Reiniciamos el valor de la velocidad
				restartVelocity();
				// Dejamos por defecto seleccionado 4-Vecinos
				vecCB.setSelectedIndex(0);
				// Hacemos visible los elementos propios de A*
				vB2a.setVisible(true);
				panelCAstar.setVisible(true);
			} else if (option.equals("HPA*")) {
				log.append("Se ha seleccionado el algoritmo HPA*." + newline);
				// Ocultamos los elementos propios de A*
				vB2a.setVisible(false);
				panelCAstar.setVisible(false);
				// Reiniciamos las variables de HPA*
				step = 0;
				// Dejamos por defecto seleccionada la opción para escoger las dimensiones de
				// clusters
				cbTCluster.setSelectedIndex(0);
				// Mostramos los elementos propios de HPA*
				vB2hpa.setVisible(true);
				panelCHPAstar.setVisible(true);
			}
		}
		// Controlador del selector de la cantidad de vecinos (solo para A*)
		else if (e.getSource() == vecCB) {
			String option = vecCB.getSelectedItem().toString();

			if (option.equals(numVecinos[0])) { // 4-Vecinos

			} else if (option.equals(numVecinos[1])) { // 8-Vecinos

			} else { // Seleccionar cantidad de vecinos

			}
		}

		// Controlador del selector de las dimensiones de los clusters (solo para HPA*)
		else if (e.getSource() == cbTCluster) {
			String option = cbTCluster.getSelectedItem().toString();

			if (option.equals(clusters[0])) { // 10x10

			} else if (option.equals(clusters[1])) { // 5x5

			} else { // Seleccionar dimensiones

			}

		}

		// Controlador del selector de las dimensiones del mapa
		else if (e.getSource() == dims) {

			String algoritmo = algCB.getSelectedItem().toString();

			if (algoritmo.equals("A*")) {
				datosAstar.setVisible(false);
				if (btnStop.isEnabled()) {
					btnStart.setIcon(new ImageIcon(getClass().getResource(Direccion.start16)));
					btnStart.setEnabled(true);
					btnStop.setEnabled(false);
					rInic.setEnabled(true);
					rFin.setEnabled(true);
					rObs.setEnabled(true);
					btnReverse.setEnabled(true);
				}
			} else if (algoritmo.equals("HPA*")) {
				if (btnStop2.isEnabled()) {
					step = 0;
					btnStop2.setEnabled(false);
					rInic.setEnabled(true);
					rFin.setEnabled(true);
					rObs.setEnabled(true);
					btnReverse.setEnabled(true);
				}
			}

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
		// Si se pulsa el botón de cambio de pt inicio por pto fin
		else if (e.getSource() == btnReverse) {
			if (mapa.pto_final != null && mapa.pto_inicial != null) {
				Punto aux = mapa.pto_final.clone();
				mapa.pto_final = mapa.pto_inicial;
				mapa.pto_inicial = aux;
				mapa.MatrizBotones[mapa.pto_final.getFila()][mapa.pto_final.getCol()].setBackground(Mapa.cFinal);
				mapa.MatrizBotones[mapa.pto_inicial.getFila()][mapa.pto_inicial.getCol()].setBackground(Mapa.cInicial);

				log.append("Se han intercambiado las posiciones de los puntos inicial y final." + newline);
			} else if (mapa.pto_final != null) {
				mapa.pto_inicial = mapa.pto_final.clone();
				mapa.MatrizBotones[mapa.pto_inicial.getFila()][mapa.pto_inicial.getCol()].setBackground(Mapa.cInicial);
				mapa.pto_final = null;

				log.append("Se ha convertido el punto final en un punto inicial." + newline);
			} else if (mapa.pto_inicial != null) {
				mapa.pto_final = mapa.pto_inicial.clone();
				mapa.MatrizBotones[mapa.pto_final.getFila()][mapa.pto_final.getCol()].setBackground(Mapa.cFinal);
				mapa.pto_inicial = null;

				log.append("Se ha convertido el punto inicial en un punto final." + newline);
			}
		}

		// Control de la simulación
		// Si pulsamos el botón de iniciar (con la opción del algoritmo A*)
		else if (e.getSource() == btnStart) {
			// String option = algCB.getSelectedItem().toString();
			// Primero, comprobamos que está el mapa creado
			if (mapa.getFilas() == 0 && mapa.getCols() == 0) {
				JOptionPane.showMessageDialog(new JFrame(),
						"Debe definir el tamaño del mapa y seleccionar los puntos inicial y final para poder iniciar la simulación.");
			}
			// Segundo, que estén definidos los puntos inicial y final
			else if (mapa.pto_inicial == null || mapa.pto_final == null) {
				JOptionPane.showMessageDialog(new JFrame(),
						"Debe seleccionar los puntos inicial y final para poder iniciar la simulación.");
			}
			// Si se cumplen todas las condiciones, se empieza la simulación
			else {
				// Bloqueamos el selector de algoritmos
				algCB.setEnabled(false);
				// Bloqueamos también el selector de cantidad de vecinos
				vecCB.setEnabled(false);

				if (!btnStop.isEnabled()) {
					log.append("Iniciada la simulación del algoritmo A*." + newline);

					// Cambiamos a que solo se pueda consultar, bloqueamos el resto de botones
					rCons.setSelected(true);
					rInic.setEnabled(false);
					rFin.setEnabled(false);
					rObs.setEnabled(false);
					btnReverse.setEnabled(false);

					// Se bloquea el botón de iniciar y se desbloquea el botón de parar
					btnStart.setIcon(new ImageIcon(getClass().getResource(Direccion.pause16)));
					btnStop.setEnabled(true);
					start = true;

					datosAstar.setVisible(true);
					// Iniciamos la búsqueda y mostramos debajo del mapa los datos con las
					// iteraciones y la memoria usada
					// Antes vemos qué opción está seleccionada en cuanto a cantidad de vecinos
					String option = vecCB.getSelectedItem().toString();
					// Si seleccionamos 4-vecinos
					if (option.equals(numVecinos[0])) {
						log.append("Simulación con 4 vecinos. Se aplica la distancia Manhattan.");
						Astar.BusquedaAstar(mapa, Astar.VECINOS_4);
					}
					// Si seleccionamos 8-vecinos
					else if (option.equals(numVecinos[1])) {
						log.append("Simulación con 8 vecinos. Se aplica la distancia octil.");
						Astar.BusquedaAstar(mapa, Astar.VECINOS_8);
					}

				} else {
					if (start == true) {
						btnStart.setIcon(new ImageIcon(getClass().getResource(Direccion.start16)));
						start = false;
						Astar.timer.stop();
					} else {
						btnStart.setIcon(new ImageIcon(getClass().getResource(Direccion.pause16)));
						start = true;
						Astar.timer.restart();
					}

				}
			}

		}
		// Si pulsamos el botón de parar simulación (con la opción del algoritmo A*)
		else if (e.getSource() == btnStop) {

			// Borramos y escondemos los datos de la simulación
			datosAstar.setText("");
			datosAstar.setVisible(false);

			reiniciarMapa();

			// Se bloquea el botón de parar y se desbloquea el de iniciar
			btnStop.setEnabled(false);
			btnStart.setEnabled(true);
			btnStart.setIcon(new ImageIcon(getClass().getResource(Direccion.start16)));

			// Desbloqueamos los botones
			rInic.setEnabled(true);
			rFin.setEnabled(true);
			rObs.setEnabled(true);
			btnReverse.setEnabled(true);

			// Desbloqueamos el selector de algoritmos
			algCB.setEnabled(true);
			// Desbloqueamos también el selector de cantidad de vecinos
			vecCB.setEnabled(true);
		}

		// Si pulsamos el botón de iniciar (con la opción del algoritmo HPA*)
		else if (e.getSource() == btnStart2) {
			log.append("Todavía está en desarrollo el algoritmo HPA*." + newline);

			// Si es la primera vez que pulsamos el botón de inicio en HPA*
			if (!btnStop2.isEnabled()) {
				// Comprobamos que los datos están creados:
				// 1. Comprobamos que está el mapa creado
				if (mapa.getFilas() == 0 && mapa.getCols() == 0) {
					JOptionPane.showMessageDialog(new JFrame(),
							"Debe definir el tamaño del mapa y seleccionar los puntos inicial y final para poder iniciar la simulación.");
				}
				// 2. Comprobamos que estén definidos los puntos inicial y final
				else if (mapa.pto_inicial == null || mapa.pto_final == null) {
					JOptionPane.showMessageDialog(new JFrame(),
							"Debe seleccionar los puntos inicial y final para poder iniciar la simulación.");
				}
				// 3. Comprobamos que está definido el tamaño de clusters
				else {
					String tCluster = cbTCluster.getSelectedItem().toString();

					if (tCluster.equals(selDims)) {
						JOptionPane.showMessageDialog(new JFrame(),
								"Debe seleccionar las dimensiones de los clusters para poder realizar una simulación de HPA*.");
					}
					// 4. Una vez comprobado que están todos los datos definidos
					else {
						log.append("Iniciada la simulación del algoritmo HPA*." + newline);
						// 1. Desbloqueamos el botón de pausar la simulación
						btnStop2.setEnabled(true);
						// 2. Bloqueamos los botones de modificación del mapa (ptos inicio, fin,
						// obstáculos...)
						rCons.setSelected(true);
						rInic.setEnabled(false);
						rFin.setEnabled(false);
						rObs.setEnabled(false);
						btnReverse.setEnabled(false);
						// 3. Bloqueamos el selector de clusters y el de algoritmos (temporalmente)
						algCB.setEnabled(false);
						cbTCluster.setEnabled(false);
						// 4. Iniciamos el proceso de pintar bordes
						// Bloqueamos el botón de start
						btnStart2.setEnabled(false);
						if (tCluster.equals(clusters[0])) {
							log.append("Se van a dibujar los clústers de tamaño " + tCluster + "." + newline);
							HPAstar.definirClusters(mapa, HPAstar.CLUSTER_10X10);
						} else if (tCluster.equals(clusters[1])) {
							log.append("Se van a dibujar los clústers de tamaño " + tCluster + "." + newline);
							HPAstar.definirClusters(mapa, HPAstar.CLUSTER_5X5);

						}
						// Una vez terminamos:
						// 1. Sumamos un "step", para indicar que se ha pulsado una vez el botón, y
						// continuar con el siguiente paso de la simulación
						step++;
						// 2. Desbloqueamos el botón de start
						btnStart2.setEnabled(true);

					}
				}

			}
			// Si no es la primera vez que se pulsa -> Vamos al siguiente paso de la
			// simulación
			else {
				switch (step) {
				// El segundo paso de la simulación -> Crear las entradas entre los clústers
				case 1:
					// Bloqueamos el botón de start
					btnStart2.setEnabled(false);
					// Realizamos la siguiente fase
					HPAstar.definirEdges(mapa);
					// Incrementamos un "step"
					step++;
					// Desbloqueamos el botón de start
					btnStart2.setEnabled(true);

					break;
				default:
					System.out.println("Estás en el paso " + step + ", todavía está el algoritmo en desarrollo");

					break;
				}
			}

		}

		// Si pulsamos el botón de parar simulación (con la opción del algoritmo HPA*)
		else if (e.getSource() == btnStop2) {
			// Reiniciamos el contador de pasos:
			step = 0;
			// Bloqueamos el botón de stop
			btnStop2.setEnabled(false);
			// Activamos el botón de start
			btnStart2.setEnabled(true);

			// Reiniciamos el mapa para borrar lo que se ha pintado
			reiniciarMapa();

			// Desbloqueamos también los selectores
			algCB.setEnabled(true);
			cbTCluster.setEnabled(true);
			// Desbloqueamos los botones
			rInic.setEnabled(true);
			rFin.setEnabled(true);
			rObs.setEnabled(true);
			btnReverse.setEnabled(true);
		}

		// Control de la velocidad (solo para A*)
		// Si pulsamos el botón de reducir velocidad
		else if (e.getSource() == btnMinus)

		{
			log.append("Se ha reducido la velocidad de la simulación " + newline);

			v /= 2;
			velocity.setText(new String("x").concat(frmt.format(v)));
			// No puede bajar de 0.25
			if (v == (0.25 / 2)) {
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

			v *= 2;
			velocity.setText(new String("x").concat(frmt.format(v)));
			// No puede subir de 2.00
			if (v == 8) {
				btnPlus.setEnabled(false);
				log.append("Se ha alcanzado el máximo de velocidad" + newline);
			}

			// Si el botón de reducir estaba bloqueado
			if (!btnMinus.isEnabled())
				btnMinus.setEnabled(true);
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

	/**
	 * Para reiniciar la parte de la velocidad
	 */
	private void restartVelocity() {
		v = 1;
		velocity.setText(new String("x").concat(frmt.format(v)));

		if (!btnPlus.isEnabled())
			btnPlus.setEnabled(true);
		else if (!btnMinus.isEnabled())
			btnMinus.setEnabled(true);

	}

	/**
	 * Devuelve el mapa a su estado original una vez parada una simulación
	 */
	private void reiniciarMapa() {
		// Copiamos los datos
		Punto pini = mapa.pto_inicial;
		Punto pfin = mapa.pto_final;
		ArrayList<Punto> lobs = mapa.obstaculos;

		int tipo = mapa.getTipo();

		// Y generamos un mapa nuevo, con los mismos datos pero sin la simulación hecha
		mapa.destruirTablero();
		mapa.crearTablero();

		mapa.MatrizBotones[pini.getFila()][pini.getCol()].setBackground(Mapa.cInicial);
		mapa.MatrizBotones[pfin.getFila()][pfin.getCol()].setBackground(Mapa.cFinal);

		for (Punto obs : lobs)
			mapa.MatrizBotones[obs.getFila()][obs.getCol()].setBackground(Mapa.cObs);

		mapa.pto_inicial = pini;
		mapa.pto_final = pfin;
		mapa.obstaculos = lobs;

		mapa.setTipo(tipo);
	}

}

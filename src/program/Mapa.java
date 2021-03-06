package program;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class Mapa {

	// Constantes para los colores:
	protected static final Color cInicial = Color.GREEN;
	protected static final Color cFinal = Color.RED;
	protected static final Color cObs = Color.BLACK;

	// Constantes que (de momento) se usan en A*
	protected static final Color cAbierto = Color.CYAN;
	protected static final Color cCerrado = Color.BLUE;
	protected static final Color cRecorrido = Color.PINK;
	protected static final Color cDefault = UIManager.getColor("Button.background");

	// Para insertar una nueva línea
	private final static String newline = "\n";

	// Enteros para controlar el tipo
	public static final int TIPO_CONSULTA = 0;
	public static final int TIPO_INICIAL = 1;
	public static final int TIPO_FINAL = 2;
	public static final int TIPO_OBSTACULO = 3;
	public static final int TIPO_VERTABLA = 4;
	public static final int TIPO_TEST = 5;

	// Variable para controlar el tipo del mapa al pulsar un botón
	private int tipo;

	// Variables básicas del mapa:
	// Cantidad total de filas y de columnas
	private int dY = 0; // Y -> fils
	private int dX = 0; // X -> cols

	// Dimensiones de cada botón en el mapa
	private int tamY = 0; // Y -> alto
	private int tamX = 0; // X -> ancho

	// Dimensiones en píxeles del mapa
	private int dimY = 650; // Y -> alto
	private int dimX = 600; // X -> ancho

	// Elementos del mapa
	protected JPanel tablero;
	private GroupLayout tableroLayout;

	// Matriz de botones
	private JButton[][] MatrizBotones;

	// Variables para los puntos
	// Puntos inicial y final
	Punto pto_inicial;
	Punto pto_final;
	// Lista de obstáculos
	ArrayList<Punto> obstaculos;

	public Mapa(int fils, int cols) {
		this(fils, cols, TIPO_CONSULTA);
	}

	public Mapa(int fils, int cols, int tipo) {
		definirDimensiones();
		dY = fils;
		dX = cols;
		setTipo(tipo);
		pto_inicial = null;
		pto_final = null;
		obstaculos = new ArrayList<>();

		if (tipo != TIPO_TEST)
			initComponents();
	}

	/**
	 * Devuelve el número de filas del mapa
	 * 
	 * @return
	 */
	public int getFilas() {
		return dY;
	}

	/**
	 * Devuelve el número de columnas del mapa
	 * 
	 * @return
	 */
	public int getCols() {
		return dX;
	}

	/**
	 * Define el tipo del mapa para gestionar la pulsación de botón
	 * 
	 * @param n
	 */
	public void setTipo(int n) {
		tipo = n;
	}

	/**
	 * Devuelve el tipo del mapa
	 */
	public int getTipo() {
		return tipo;
	}

	/**
	 * Modifica el número de filas y de columnas
	 * 
	 * @param fils
	 * @param cols
	 */
	public void setDims(int fils, int cols) {
		this.dX = cols;
		this.dY = fils;
	}

	/**
	 * Cambia el ancho y el alto del mapa
	 * 
	 * @param height
	 * @param width
	 */
	public void setSize(int height, int width) {
		this.dimX = width;
		this.dimY = height;

		initComponents();
		destruirTablero();
		crearTablero();
	}

	/**
	 * Inicializa el tablero
	 */
	private void initComponents() {
		tablero = new JPanel();
		tablero.setPreferredSize(new Dimension(dimX, dimY));

		tablero.setBackground(new Color(204, 204, 204));
		tablero.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
		tablero.setToolTipText("");

		tableroLayout = new GroupLayout(tablero);
		tablero.setLayout(tableroLayout);

		tableroLayout.setHorizontalGroup(
				tableroLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, dimX, Short.MAX_VALUE));
		tableroLayout.setVerticalGroup(
				tableroLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, dimY, Short.MAX_VALUE));

	}

	/**
	 * Método para redibujar el tablero
	 */
	private void redibujar() {
		// Se valida los componentes del tablero
		tablero.validate();
		// Se redibuja el elemento tablero y sus componentes hijos
		tablero.repaint();
	}

	public void crearTablero() {

		// Si hay parámetros de texto, los validamos, si no, validamos los numéricos
		// boolean val = (this.tbxDimX == null || this.tbxDimY == null) ? validarDims2()
		// : validarDims();

		// Si las dimensiones son válidas
		if (validarDims()) {
			// Se genera el tamaño de la matriz de botones
			MatrizBotones = new JButton[dY][dX];
			// Se crea el tamaño de gridLayout de nuestro panel del tablero
			// tablero.setLayout(new GridLayout(dY, dX));
			tablero.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();

			// Se obtiene las dimensiones de cada botón
			getDimButtons(dX, dY);

			// Si sale impar alguna las dimensiones, se le resta 1 para que salga par y no
			// generar huecos
			if (tamX > 3 && tamX % 2 == 1)
				tamX--;
			if (tamY > 3 && tamY % 2 == 1)
				tamY--;

			for (int contY = 0; contY < dY; contY++) {
				for (int contX = 0; contX < dX; contX++) {
					// Se crea un nuevo JButton
					JButton bNew = new JButton();
					// Se le asignan sus dimensiones (ancho, alto)
					// bNew.setSize(tamX, tamY);

					// Debe ser un cuadrado:
					if (tamX == tamY)
						bNew.setPreferredSize(new Dimension(tamX, tamY));
					else if (tamX < tamY)
						bNew.setPreferredSize(new Dimension(tamX, tamX));
					else
						bNew.setPreferredSize(new Dimension(tamY, tamY));

					// Se asigna un texto con la posición del botón en la matriz al botón, al
					// tooltip del botón
					bNew.setToolTipText(Integer.toString(contY) + ", " + Integer.toString(contX));
					// Se agrega a la matriz el botón
					MatrizBotones[contY][contX] = bNew;
					// Se agrega un elemento que manege la acción click sobre el botón
					MatrizBotones[contY][contX].addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Click(bNew);
						}
					});

					// Definimos la posición
					gbc.gridx = contX;
					gbc.gridy = contY;
					// gbc.fill = GridBagConstraints.BOTH;

					// Se agrega al panel
					tablero.add(MatrizBotones[contY][contX], gbc);
					// tablero.add(MatrizBotones[contY][contX]);
					// Se redibuja el panel
					redibujar();
				}
			}
		}
		// Si no lo son, se muestra mensaje de error
		else {
			JOptionPane.showMessageDialog(new JFrame(), "Las dimensiones deben ser numéricas y mayores que 0");
		}
	}

	/**
	 * Elimina todos los elementos del tablero
	 */
	public void destruirTablero() {
		if (hayElementos()) {
			pto_inicial = null;
			pto_final = null;
			obstaculos = new ArrayList<>();
			// Se eliminan todos los elementos columna - fila
			for (int x = 0; x < dX; x++)
				for (int y = 0; y < dY; y++)
					MatrizBotones[y][x] = null;

			// Se eliminan todos los hijos de tablero
			tablero.removeAll();

			// Se redibuja el tablero
			redibujar();
		}
	}

	/**
	 * Determina si el tablero contiene elementos
	 * 
	 * @return
	 */
	private boolean hayElementos() {
		return tablero.getComponentCount() > 0;
	}

	/**
	 * Gestión de lo que ocurre al pulsar un botón
	 * 
	 * @param btn
	 */
	private void Click(JButton btn) {
		Punto aux = new Punto(btn.getToolTipText());
		switch (tipo) {
		case TIPO_CONSULTA: // Consulta
			Interfaz.escribir("Se ha seleccionado la celda de la posición: (" + btn.getToolTipText() + ")" + newline);
			JOptionPane.showMessageDialog(new JFrame(), "Posición: (" + btn.getToolTipText() + ")");
			break;
		case TIPO_INICIAL: // Pto inicial
			// Si ya existe pto_inicial
			if (pto_inicial != null) {
				// Si el mismo pto coincide
				if (pto_inicial.equals(aux)) {
					btn.setBackground(Mapa.cDefault);
					pto_inicial = null;
					Interfaz.escribir("Se ha seleccionado quitar el punto inicial, que estaba en la posición: "
							+ aux.toString() + newline);
				} else {
					// Si coincide con el pto_final -> Lo borramos
					if (pto_final != null && pto_final.equals(aux)) {
						Interfaz.escribir("Se quita el punto final establecido." + newline);
						pto_final = null;
					} else if (obstaculos.contains(aux)) {
						Interfaz.escribir("Se elimina el obstáculo de la posición seleccionada." + newline);
						obstaculos.remove(aux);
					}

					Interfaz.escribir("Se cambia el punto inicial de la posición: " + pto_inicial.toString()
							+ " a la posición: " + aux.toString() + "." + newline);
					btn.setBackground(cInicial);
					MatrizBotones[pto_inicial.getFila()][pto_inicial.getCol()].setBackground(Mapa.cDefault);
					pto_inicial = aux;
				}
				// Si no existe
			} else {
				// Si el pto_final no es null y coincide: Lo borramos
				Interfaz.escribir("Se establece el punto inicial en la posición: " + aux.toString() + "." + newline);
				if (pto_final != null && pto_final.equals(aux)) {
					Interfaz.escribir("Se quita el punto final establecido." + newline);
					pto_final = null;
				} else if (obstaculos.contains(aux)) {
					Interfaz.escribir("Se elimina el obstáculo de la posición seleccionada." + newline);
					obstaculos.remove(aux);
				}

				pto_inicial = aux;
				btn.setBackground(cInicial);
			}
			break;
		case TIPO_FINAL: // Pto final
			// Si ya existe pto_final
			if (pto_final != null) {
				// Si el mismo pto coincide
				if (pto_final.equals(aux)) {
					btn.setBackground(Mapa.cDefault);
					pto_final = null;
					Interfaz.escribir("Se ha seleccionado quitar el punto final, que estaba en la posición: "
							+ aux.toString() + "." + newline);
				} else {
					// Si coincide con el pto_inicial -> Lo borramos
					if (pto_inicial != null && pto_inicial.equals(aux)) {
						Interfaz.escribir("Se quita el punto inicial establecido." + newline);
						pto_inicial = null;
					} else if (obstaculos.contains(aux)) {
						Interfaz.escribir("Se elimina el obstáculo de la posición seleccionada." + newline);
						obstaculos.remove(aux);
					}

					Interfaz.escribir("Se cambia el punto final de la posición: " + pto_final.toString()
							+ " a la posición: " + aux.toString() + "." + newline);
					btn.setBackground(cFinal);
					btn.setBorder(HPAstar.defaultborder);
					MatrizBotones[pto_final.getFila()][pto_final.getCol()].setBackground(Mapa.cDefault);
					pto_final = aux;
				}
				// Si no existe
			} else {
				// Si el pto_inicial no es null y coincide: Lo borramos
				Interfaz.escribir("Se establece el punto final en la posición: " + aux.toString() + "." + newline);
				if (pto_inicial != null && pto_inicial.equals(aux)) {
					Interfaz.escribir("Se quita el punto inicial establecido." + newline);
					pto_inicial = null;
				} else if (obstaculos.contains(aux)) {
					Interfaz.escribir("Se elimina el obstáculo de la posición seleccionada." + newline);
					obstaculos.remove(aux);
				}

				pto_final = aux;
				btn.setBackground(cFinal);
			}
			break;
		case TIPO_OBSTACULO: // Obstáculo
			btn.setBackground(cObs);
			// Si la lista de obstaculos contiene el punto seleccionado
			if (obstaculos.contains(aux)) {
				Interfaz.escribir("Se quita un obstáculo de la posición: " + aux.toString() + "." + newline);
				obstaculos.remove(aux);
				btn.setBackground(Mapa.cDefault);
			} else {
				// Si coincide con el pto inicial
				if (pto_inicial != null && pto_inicial.equals(aux)) {
					Interfaz.escribir("Se quita el punto inicial establecido." + newline);
					pto_inicial = null;
				} // Si coincide con el pto final
				else if (pto_final != null && pto_final.equals(aux)) {
					Interfaz.escribir("Se quita el punto final establecido." + newline);
					pto_final = null;
				}
				Interfaz.escribir("Se añade un obstáculo en la posición: " + aux.toString() + "." + newline);
				obstaculos.add(aux);
				btn.setBackground(cObs);

				// Dejamos ordenada la lista de obstaculos
				Collections.sort(obstaculos);

			}
			break;
		case TIPO_VERTABLA: // Opción de ver tabla activada (solo cuando tenemos los arcos hechos en HPA*)
			// Cogemos la lista de clusters de HPAstar
			ArrayList<Cluster> clusters = HPAstar.clusters;
			// Creamos la variable con el índice del cluster del botón
			int i = 0;
			for (i = 0; i < clusters.size() && !clusters.get(i).inCluster(aux); i++)
				continue;
			// Vemos la tabla de los arcos externos
			HPAstar.verTabla(i);

			break;
		case TIPO_TEST:

			JOptionPane.showMessageDialog(new JFrame(), "Posición: (" + btn.getToolTipText() + ")");
			// Cogemos la lista de clusters de HPAstar
			ArrayList<Cluster> clusts = HPAstar.clusters;
			// Creamos la variable con el índice del cluster del botón
			int i2 = 0;
			for (i2 = 0; i2 < clusts.size() && !clusts.get(i2).inCluster(aux); i2++)
				continue;
			// Vemos la tabla de los arcos externos
			HPAstar.verTabla(i2);
			break;
		default:
			break;

		}
	}

	/**
	 * Igual que el anterior pero sin los parámetros de texto
	 * 
	 * @return
	 */
	private boolean validarDims() {
		boolean res = false;

		if (this.dX > 0 && dY > 0)
			res = true;

		return res;
	}

	/**
	 * Función para obtener las dimensiones de cada botón
	 * 
	 * @param cX Cantidad de botones por fila
	 * @param cY cantidad de botones por columna
	 */
	private void getDimButtons(int cX, int cY) {

		tamX = dimX / cX;
		tamY = dimY / cY;

	}

	/**
	 * Función para obtener las dimensiones de los botones
	 * 
	 * @return
	 */
	public Dimension getDimsButton() {
		return MatrizBotones[0][0].getPreferredSize();
	}

	/**
	 * Método para pintar el mapa de dados un color una posición
	 * 
	 * @throws InterruptedException
	 */
	public void pintarMapa(Color color, int fila, int columna) {
		MatrizBotones[fila][columna].setBackground(color);
	}

	/**
	 * Mismo método pero para pintar dado un punto
	 * 
	 * @param color
	 * @param p
	 */
	public void pintarMapa(Color color, Punto p) {
		pintarMapa(color, p.getFila(), p.getCol());
	}

	/**
	 * Método para cambiar el borde de una casilla
	 * 
	 * @param border
	 * @param fila
	 * @param columna
	 */
	public void pintarBorde(Border border, int fila, int columna) {
		MatrizBotones[fila][columna].setBorder(border);
	}

	/**
	 * Método para limpiar el mapa (tras hacer A* o HPA*, o tras detener la
	 * simulación)
	 */
	public void limpiarMapa() {
		// Coloreamos del color original
		for (int i = 0; i < dY; i++)
			for (int j = 0; j < dX; j++) {
				pintarMapa(cDefault, i, j);
				pintarBorde(HPAstar.defaultborder, i, j);
			}

		// Pintamos de nuevo el punto inicial
		if (pto_inicial != null)
			pintarMapa(cInicial, pto_inicial);

		// Pintamos de nuevo el punto final
		if (pto_final != null)
			pintarMapa(cFinal, pto_final);

		// Pintamos los obstáculos
		for (Punto o : obstaculos)
			pintarMapa(cObs, o);

	}

	private void definirDimensiones() {
		// Se obtienen la resolución de la pantalla
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();

		// Multiplicador para definir las dimensiones del tablero
		double pH = 0.61;
		double pW = 0.35;

		// Hay que adaptar el multiplicador de la altura en caso de reducir aún más la
		// resolución
		if (d.getHeight() < 720)
			pH = 0.43;
		else if (d.getHeight() <= 800)
			pH = 0.52;

		// Se asigna el valor a cada dimensión
		dimY = (int) (pH * d.getHeight());
		dimX = (int) (pW * d.getWidth());

	}

}

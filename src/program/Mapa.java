package program;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class Mapa {

	// Para insertar una nueva línea
	private final static String newline = "\n";

	// Enteros para controlar el tipo
	public static final int TIPO_CONSULTA = 0;
	public static final int TIPO_INICIAL = 1;
	public static final int TIPO_FINAL = 2;
	public static final int TIPO_OBSTACULO = 3;

	// Variable para controlar el tipo del mapa al pulsar un botón
	private int tipo;

	// Variables básicas del mapa:
	// Cantidad total de filas y de columnas
	protected int dY = 0; // Y -> fils
	protected int dX = 0; // X -> cols

	// Dimensiones de cada botón en el mapa
	private int tamY = 0; // Y -> alto
	private int tamX = 0; // X -> ancho

	// Dimensiones en píxeles del mapa
	private final int dimY = 500; // Y -> alto
	private final int dimX = 500; // X -> ancho

	// Elementos del mapa
	protected JPanel tablero;
	private GroupLayout tableroLayout;

	// Variables de gestión del mapa (por ahora no lo usamos):
	private JTextField tbxDimX, tbxDimY;

	// Matriz de botones
	protected JButton[][] MatrizBotones;

	// Variables para los puntos
	Punto pto_inicial;
	Punto pto_final;
	ArrayList<Punto> obstaculos;

	public Mapa(int fils, int cols) {
		this(fils, cols, TIPO_CONSULTA);
	}

	public Mapa(int fils, int cols, int tipo) {
		dY = fils;
		dX = cols;
		setTipo(tipo);
		pto_inicial = null;
		pto_final = null;
		obstaculos = new ArrayList<>();

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

	private void initComponents() {
		tablero = new JPanel();
		tablero.setPreferredSize(new Dimension(dimY, dimX));

		tablero.setBackground(new Color(204, 204, 204));
		tablero.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
		tablero.setToolTipText("");

		tableroLayout = new GroupLayout(tablero);
		tablero.setLayout(tableroLayout);

		tableroLayout.setHorizontalGroup(
				tableroLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 500, Short.MAX_VALUE));
		tableroLayout.setVerticalGroup(
				tableroLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 500, Short.MAX_VALUE));

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
		// Si las dimensiones son válidas

		// Si hay parámetros de texto, los validamos, si no, validamos los numéricos
		boolean val = (this.tbxDimX == null || this.tbxDimY == null) ? validarDims2() : validarDims();

		if (val) {
			// Se genera el tamaño de la matriz de botones
			MatrizBotones = new JButton[dY][dX];
			// Se crea el tamaño de gridLayout de nuestro panel del tablero
			tablero.setLayout(new GridLayout(dY, dX));
			// Se obtiene las dimensiones de cada botón
			getDimButtons(dY, dX);

			// Se declaran los contadores a utilizar
			// int contX, contY;
			// Se recorre la dimensión Y desde 0 hasta dY
			for (int contY = 0; contY < dY; contY++) {
				for (int contX = 0; contX < dX; contX++) {
					// Se crea un nuevo JButton
					JButton bNew = new JButton();
					// Se le asignan sus dimensiones (ancho, alto)
					bNew.setSize(tamY, tamX);
					// se asigna un texto con la posición del botón en la matriz al botón, al
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

					// Se agrega al panel
					tablero.add(MatrizBotones[contY][contX]);
					// Se redibuja el panel
					redibujar();
				}
			}
		}
		// Si no lo son
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
			Interfaz.log.append("Se ha seleccionado la celda de la posición: (" + btn.getToolTipText() + ")" + newline);
			JOptionPane.showMessageDialog(new JFrame(), "Posición: (" + btn.getToolTipText() + ")");
			break;
		case TIPO_INICIAL: // Pto inicial
			// Si ya existe pto_inicial
			if (pto_inicial != null) {
				// Si el mismo pto coincide
				if (pto_inicial.equals(aux)) {
					btn.setBackground(UIManager.getColor("Button.background"));
					pto_inicial = null;
					Interfaz.log.append("Se ha seleccionado quitar el punto inicial, que estaba en la posición: "
							+ aux.toString() + newline);
				} else {
					// Si coincide con el pto_final -> Lo borramos
					if (pto_final != null && pto_final.equals(aux)) {
						Interfaz.log.append("Se quita el punto final establecido." + newline);
						pto_final = null;
					} else if (obstaculos.contains(aux)) {
						Interfaz.log.append("Se elimina el obstáculo de la posición seleccionada." + newline);
						obstaculos.remove(aux);
					}

					Interfaz.log.append("Se cambia el punto inicial de la posición: " + pto_inicial.toString()
							+ " a la posición: " + aux.toString() + "." + newline);
					btn.setBackground(Color.GREEN);
					MatrizBotones[pto_inicial.getFila()][pto_inicial.getCol()]
							.setBackground(UIManager.getColor("Button.background"));
					pto_inicial = aux;
				}
				// Si no existe
			} else {
				// Si el pto_final no es null y coincide: Lo borramos
				Interfaz.log.append("Se establece el punto inicial en la posición: " + aux.toString() + "." + newline);
				if (pto_final != null && pto_final.equals(aux)) {
					Interfaz.log.append("Se quita el punto final establecido." + newline);
					pto_final = null;
				} else if (obstaculos.contains(aux)) {
					Interfaz.log.append("Se elimina el obstáculo de la posición seleccionada." + newline);
					obstaculos.remove(aux);
				}

				pto_inicial = aux;
				btn.setBackground(Color.GREEN);
			}
			break;
		case TIPO_FINAL: // Pto final
			// Si ya existe pto_final
			if (pto_final != null) {
				// Si el mismo pto coincide
				if (pto_final.equals(aux)) {
					btn.setBackground(UIManager.getColor("Button.background"));
					pto_final = null;
					Interfaz.log.append("Se ha seleccionado quitar el punto final, que estaba en la posición: "
							+ aux.toString() + "." + newline);
				} else {
					// Si coincide con el pto_inicial -> Lo borramos
					if (pto_inicial != null && pto_inicial.equals(aux)) {
						Interfaz.log.append("Se quita el punto inicial establecido." + newline);
						pto_inicial = null;
					} else if (obstaculos.contains(aux)) {
						Interfaz.log.append("Se elimina el obstáculo de la posición seleccionada." + newline);
						obstaculos.remove(aux);
					}

					Interfaz.log.append("Se cambia el punto final de la posición: " + pto_final.toString()
							+ " a la posición: " + aux.toString() + "." + newline);
					btn.setBackground(Color.RED);
					MatrizBotones[pto_final.getFila()][pto_final.getCol()]
							.setBackground(UIManager.getColor("Button.background"));
					pto_final = aux;
				}
				// Si no existe
			} else {
				// Si el pto_inicial no es null y coincide: Lo borramos
				Interfaz.log.append("Se establece el punto final en la posición: " + aux.toString() + "." + newline);
				if (pto_inicial != null && pto_inicial.equals(aux)) {
					Interfaz.log.append("Se quita el punto inicial establecido." + newline);
					pto_inicial = null;
				} else if (obstaculos.contains(aux)) {
					Interfaz.log.append("Se elimina el obstáculo de la posición seleccionada." + newline);
					obstaculos.remove(aux);
				}

				pto_final = aux;
				btn.setBackground(Color.RED);
			}
			break;
		case TIPO_OBSTACULO: // Obstáculo
			btn.setBackground(Color.BLACK);
			// Si la lista de obstaculos contiene el punto seleccionado
			if (obstaculos.contains(aux)) {
				Interfaz.log.append("Se quita un obstáculo de la posición: " + aux.toString() + "." + newline);
				obstaculos.remove(aux);
				btn.setBackground(UIManager.getColor("Button.background"));
			} else {
				// Si coincide con el pto inicial
				if (pto_inicial != null && pto_inicial.equals(aux)) {
					Interfaz.log.append("Se quita el punto inicial establecido." + newline);
					pto_inicial = null;
				} // Si coincide con el pto final
				else if (pto_final != null && pto_final.equals(aux)) {
					Interfaz.log.append("Se quita el punto final establecido." + newline);
					pto_final = null;
				}
				Interfaz.log.append("Se añade un obstáculo en la posición: " + aux.toString() + "." + newline);
				obstaculos.add(aux);
				btn.setBackground(Color.BLACK);
			}
			break;
		default:
			break;

		}
	}

	/**
	 * Función que determina si un String corresponde a un número
	 * 
	 * @param s
	 * @return
	 */
	private boolean isNumeric(String s) {
		boolean res = true;

		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			res = false;
		}

		return res;
	}

	/**
	 * Función que valida si los parámetros de entrada son válidos
	 * 
	 * @return
	 */
	private boolean validarDims() {
		// Vble de retorno
		boolean res = false;

		String cols = tbxDimX.getText();

		// Comprobamos que las filas y las columnas sean dadas en numérico
		if (isNumeric(cols)) {
			String fils = tbxDimY.getText();
			if (isNumeric(cols)) {
				// Cantidad de columnas
				dX = Integer.parseInt(cols);
				// Cantidad de filas
				dY = Integer.parseInt(fils);
				// Se verifica que las dimensiones sean mayores que 0
				if (dX > 0 && dY > 0)
					res = true;

			}
		}

		return res;
	}

	/**
	 * Igual que el anterior pero sin los parámetros de texto
	 * 
	 * @return
	 */
	private boolean validarDims2() {
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
	 * Ordena los obstáculos en el ArrayList de obstáculos
	 */
	public void ordenarObstaculos() {
		obstaculos.sort(new Comparator<Punto>() {
			@Override
			public int compare(Punto p1, Punto p2) {
				int v1 = (dY ^ p1.getFila() - 1) + p1.getCol();
				int v2 = (dY ^ p2.getFila() - 1) + p2.getCol();
				return v1 - v2;
			}
		});
	}

}

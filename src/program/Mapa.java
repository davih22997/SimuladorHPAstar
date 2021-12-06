package program;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class Mapa extends GroupLayout {

	// Enteros para controlar el tipo
	public static final int TIPO_CONSULTA = 0;
	public static final int TIPO_INICIAL = 1;
	public static final int TIPO_FINAL = 2;
	public static final int TIPO_OBSTACULO = 3;

	// Variable para controlar el tipo del mapa al pulsar un botón
	private int tipo;

	// Variables b�sicas del mapa:
	// Cantidad total de filas y de columnas
	int dY = 0; // Y -> fils
	int dX = 0; // X -> cols

	// Dimensiones de cada botón en el mapa
	int tamY = 0; // Y -> alto
	int tamX = 0; // X -> ancho

	// Dimensiones en píxeles del mapa
	static final int dimY = 500; // Y -> alto
	static final int dimX = 500; // X -> ancho

	// Elementos del mapa
	private static JPanel tablero = new JPanel();
	private GroupLayout tableroLayout;

	// Variables de gestión del mapa:
	private JButton btnCrear, btnDestruir;
	private JSeparator jSeparator1;
	private JLabel lblDimX, lblDimY;
	private JPanel pnlMenu;
	private JTextField tbxDimX, tbxDimY;

	// Matriz de botones
	JButton[][] MatrizBotones;
	
	public Mapa(int fils, int cols) {
		this(fils, cols, TIPO_CONSULTA);
	}

	
	public Mapa(int fils, int cols, int tipo) {
		super(tablero);
		dY = fils;
		dX = cols;
		setTipo(tipo);

		initComponents();
	}
	

	/**
	 * Define el tipo del mapa para gestionar la pulsación de botón
	 * 
	 * @param n
	 */
	public void setTipo(int n) {
		tipo = n;
	}

	private void initComponents() {
		tablero.setPreferredSize(new Dimension(dimY, dimX));

		tablero.setBackground(new Color(204, 204, 204));
		tablero.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
		tablero.setToolTipText("");
		
		
		this.setHorizontalGroup(
			this.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGap(0, 500, Short.MAX_VALUE)
		);
		this.setVerticalGroup(
			this.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGap(0, 500, Short.MAX_VALUE)
		);

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
		if (validarDims()) {
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
		switch (tipo) {
		case TIPO_CONSULTA: // Consulta
			JOptionPane.showMessageDialog(new JFrame(), "Boton en la posición: " + btn.getToolTipText());
			break;
		case TIPO_INICIAL: // Pto inicial
			btn.setBackground(Color.GREEN);
			break;
		case TIPO_FINAL: // Pto final
			btn.setBackground(Color.RED);
			break;
		case TIPO_OBSTACULO: // Obstáculo
			btn.setBackground(Color.BLACK);
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
	 * Función para obtener las dimensiones de cada botón
	 * 
	 * @param cX Cantidad de botones por fila
	 * @param cY cantidad de botones por columna
	 */
	private void getDimButtons(int cX, int cY) {
		tamX = dimX / cX;
		tamY = dimY / cY;
	}

}

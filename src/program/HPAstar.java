package program;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class HPAstar {

	// Constantes para el tamaño de los clusters:
	public static final int CLUSTER_10X10 = 1;

	// Constantes para los colores:
	// Color que usaremos (de momento) para todos los bordes
	private static final Color cBorder = Color.BLACK;
	// Color para pintar las entradas entre clusters
	private static final Color cEntrance = Color.LIGHT_GRAY;

	// Constantes para los bordes
	// El borde por defecto es:
	private static final Border defaultborder = UIManager.getBorder("Button.border");
	// Los que se van a usar:
	// Borde solo arriba
	private static final Border btop = BorderFactory.createMatteBorder(1, 0, 0, 0, cBorder);
	// Borde solo izda
	private static final Border bleft = BorderFactory.createMatteBorder(0, 1, 0, 0, cBorder);
	// Borde solo abajo
	private static final Border bbottom = BorderFactory.createMatteBorder(0, 0, 1, 0, cBorder);
	// Borde solo derecha
	private static final Border bright = BorderFactory.createMatteBorder(0, 0, 0, 1, cBorder);
	// Borde arriba izda
	private static final Border btopleft = BorderFactory.createMatteBorder(1, 1, 0, 0, cBorder);
	// Borde arriba derecha
	private static final Border btopright = BorderFactory.createMatteBorder(1, 0, 0, 1, cBorder);
	// Borde abajo izda
	private static final Border bbottomleft = BorderFactory.createMatteBorder(0, 1, 1, 0, cBorder);
	// Borde abajo derecha
	private static final Border bbottomright = BorderFactory.createMatteBorder(0, 0, 1, 1, cBorder);

	// Bordes que usaremos para pintar los botones
	private static final Border bctop = BorderFactory.createCompoundBorder(btop, defaultborder);
	private static final Border bcleft = BorderFactory.createCompoundBorder(bleft, defaultborder);
	private static final Border bcbottom = BorderFactory.createCompoundBorder(bbottom, defaultborder);
	private static final Border bcright = BorderFactory.createCompoundBorder(bright, defaultborder);
	private static final Border bctopleft = BorderFactory.createCompoundBorder(btopleft, defaultborder);
	private static final Border bctopright = BorderFactory.createCompoundBorder(btopright, defaultborder);
	private static final Border bcbottomleft = BorderFactory.createCompoundBorder(bbottomleft, defaultborder);
	private static final Border bcbottomright = BorderFactory.createCompoundBorder(bbottomright, defaultborder);

	// La lista que contendrá todos los clusters definidos
	protected static ArrayList<Cluster> clusters;

	// Constante para definir el umbral para ver cuántas entradas hay entre los
	// clústers
	protected static int umbral = 6;

	/**
	 * Método para definir el tamaño de los clusters dada una constante que
	 * represente su tamaño y pintarlos
	 * 
	 * @param mapa
	 * @param tam
	 */
	public static void definirClusters(Mapa mapa, int tam) {

		clusters = new ArrayList<>();
		switch (tam) {
		// Si se encuentra entre los tamaños definidos se hacen cosas
		case CLUSTER_10X10:
			Interfaz.btnStart2.setEnabled(false);
			crearClusters(mapa, 10, 10);
			Interfaz.btnStart2.setEnabled(true);
			// printClusters();

			break;
		// Si no, muestra mensaje de error
		default:
			JOptionPane.showMessageDialog(new JFrame(), "Tamaño de cluster no compatible con el tamaño del mapa");
			break;
		}

	}

	/**
	 * Método para crear y pintar los clusters del mapa, dadas sus dimensiones Se
	 * van incluyendo los clusters a la lista de clusters.
	 * 
	 * Como en el mapa debe visualizarse simultáneamente el coloreado, no se utiliza
	 * ningún objeto de la clase Timer
	 * 
	 * @param mapa
	 * @param fils
	 * @param cols
	 */
	public static void crearClusters(Mapa mapa, int fils, int cols) {
		// Recorremos todo el mapa para ir pintando bordes
		// Seguimos las siguientes reglas
		// 1. Si la fila es múltiplo del número de filas, pintamos arriba
		// 2. Si la fila + 1 es múltiplo del número de filas, pintamos abajo
		// 3. Si la columna es múltiplo del número de columnas, pintamos por la izda
		// 4. Si la columna + 1 es múltiplo del número de columnas, pintamos por la
		// derecha
		for (int f = 0; f < mapa.getFilas(); f++) {
			for (int c = 0; c < mapa.getCols(); c++) {
				// 1.
				if (f % fils == 0) {
					// además 3.
					if (c % cols == 0) {
						mapa.pintarBorde(bctopleft, f, c);
						// Añadimos el cluster cuando coincide con la casilla inicial
						clusters.add(new Cluster(fils, cols, f, c));
					}
					// además 4
					else if (c % cols == (cols - 1))
						mapa.pintarBorde(bctopright, f, c);
					else
						mapa.pintarBorde(bctop, f, c);
				}
				// 2.
				else if (f % fils == (fils - 1)) {
					// Además 3.
					if (c % cols == 0)
						mapa.pintarBorde(bcbottomleft, f, c);
					// Además 4.
					else if (c % cols == (cols - 1))
						mapa.pintarBorde(bcbottomright, f, c);
					else
						mapa.pintarBorde(bcbottom, f, c);
				} else {
					// 3.
					if (c % cols == 0)
						mapa.pintarBorde(bcleft, f, c);
					// 4.
					else if (c % cols == (cols - 1))
						mapa.pintarBorde(bcright, f, c);
				}

			}
		}
		// Finalmente, se ordena la lista de clusters
		Collections.sort(clusters);
		Interfaz.log.append("Se han creado los clústers.\n");
	}

	/**
	 * Método para imprimir la lista de clusters por consola (es meramente para
	 * comprobar que se ha creado correctamente).
	 */
	@SuppressWarnings("unused")
	private static void printClusters() {

		StringBuilder sb = new StringBuilder();

		sb.append("Los clusters son los siguientes:\n");

		int cont = 0;

		for (Cluster c : clusters) {
			sb.append("Cluster ").append(cont++).append(":\n");
			sb.append("Punto inicial: ").append(c.getPuntoInicial()).append("\n");
			sb.append("Punto final: ").append(c.getPuntoFinal()).append("\n\n");
		}

		System.out.println(sb.toString());

	}
}

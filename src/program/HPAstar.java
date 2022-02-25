package program;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

public class HPAstar {

	// Constantes para el tamaño de los clusters
	public static final int CLUSTER_10X10 = 1;

	// Color que usaremos (de momento) para todos los bordes
	public static final Color cBorder = Color.BLACK;

	// Constantes para los bordes
	// Los que se van a usar:
	// Borde solo arriba
	public static final Border btop = BorderFactory.createMatteBorder(5, 0, 0, 0, cBorder);
	// Borde solo izda
	public static final Border bleft = BorderFactory.createMatteBorder(0, 5, 0, 0, cBorder);
	// Borde solo abajo
	public static final Border bbottom = BorderFactory.createMatteBorder(0, 0, 5, 0, cBorder);
	// Borde solo derecha
	public static final Border bright = BorderFactory.createMatteBorder(0, 0, 0, 5, cBorder);
	// Borde arriba izda
	public static final Border btopleft = BorderFactory.createMatteBorder(5, 5, 0, 0, cBorder);
	// Borde arriba derecha
	public static final Border btopright = BorderFactory.createMatteBorder(5, 0, 0, 5, cBorder);
	// Borde abajo izda
	public static final Border bbottomleft = BorderFactory.createMatteBorder(0, 5, 5, 0, cBorder);
	// Borde abajo derecha
	public static final Border bbottomright = BorderFactory.createMatteBorder(0, 0, 5, 5, cBorder);

	// Los que seguramente no se utilicen:
	// Borde todos los lados
	public static final Border ball = BorderFactory.createMatteBorder(5, 5, 5, 5, cBorder);
	// Borde arriba abajo
	public static final Border btopbottom = BorderFactory.createMatteBorder(5, 0, 5, 0, cBorder);
	// Borde izda derecha
	public static final Border bleftright = BorderFactory.createMatteBorder(0, 5, 0, 5, cBorder);
	// Borde arriba izda abajo
	public static final Border btopleftbottom = BorderFactory.createMatteBorder(5, 5, 5, 0, cBorder);
	// Borde arriba izda derecha
	public static final Border btopleftright = BorderFactory.createMatteBorder(5, 5, 0, 5, cBorder);
	// Borde abajo izda derecha
	public static final Border bbottomleftright = BorderFactory.createMatteBorder(0, 5, 5, 5, cBorder);

	/**
	 * Método para definir el tamaño de los clusters dada una constante que
	 * represente su tamaño y pintarlos
	 * 
	 * @param mapa
	 * @param tam
	 */
	public static void definirCluster(Mapa mapa, int tam) {

		switch (tam) {
		// Si se encuentra entre los tamaños definidos se hacen cosas
		case CLUSTER_10X10:
			pintarCluster(mapa, 10, 10);

			break;
		// Si no, muestra mensaje de error
		default:
			JOptionPane.showMessageDialog(new JFrame(), "Tamaño de cluster no compatible con el tamaño del mapa");
			break;
		}

	}

	/**
	 * Método para pintar los clusters del mapa, dadas sus dimensiones
	 * 
	 * @param mapa
	 * @param fils
	 * @param cols
	 */
	public static void pintarCluster(Mapa mapa, int fils, int cols) {

	}

}

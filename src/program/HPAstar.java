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
	// (No se pone final porque puede variar según el tamaño de cluster).
	protected static int umbral;

	/**
	 * Método para definir el tamaño de los clusters dada una constante que
	 * represente su tamaño y pintarlos
	 * 
	 * @param mapa
	 * @param tam
	 */
	public static void definirClusters(Mapa mapa, int tam) {

		clusters = new ArrayList<>();
		// Iniciamos el umbral con valor 0
		umbral = 0;
		switch (tam) {
		// Si se encuentra entre los tamaños definidos se hacen cosas
		case CLUSTER_10X10:
			// Realizamos la creación de cluster
			crearClusters(mapa, 10, 10);
			// Definimos el valor del umbral
			umbral = 6;
			// printClusters();

			break;
		// Si no, muestra mensaje de error
		default:
			JOptionPane.showMessageDialog(new JFrame(), "Tamaño de cluster no compatible con el tamaño del mapa");
			break;
		}

	}

	/**
	 * Método para, una vez tenemos los clusters, definir las entradas (edges), dado
	 * el mapa (lo necesitamos para ver los obstáculos y los punto inicial y final
	 * (y no colorear encima)
	 * 
	 * @param mapa
	 */
	public static void definirEdges(Mapa mapa) {
		// Para no repetir 2 veces las comprobaciones, iremos recorriendo los cluster
		// comprobando siempre sus adyacentes derecho e inferior (si los tiene)

		// Vamos recorriendo los clusters generados siguiendo el orden (de izda a
		// derecha y al llegar al final se baja una fila):
		for (Cluster c : clusters) {
			// 1. Creamos los edge inferiores
			bottomEdge(c, clusters, mapa);

			// 2. Creamos los edges por la derecha
			rigthEdge(c, clusters, mapa);

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
	 * Dado el índice de un punto en una lista de puntos, te cuenta cuántos se
	 * encadenan
	 * 
	 * @param ptos
	 * @param index
	 * @return
	 */
	public static int contarPtosConsecutivos(ArrayList<Punto> ptos, int index) {

		// Creamos el contador
		int cont = 0;

		// Si el índice se encuentra dentro de la lista
		if (index >= 0 && index < ptos.size()) {
			// Cogemos el punto de referencia
			Punto p = ptos.get(index);
			// Incrementamos el contador, dado que ya hay un punto
			cont++;

			// Incrementamos el index cada vez que realizamos las comprobaciones
			while (++index < ptos.size() && p.adyacente(ptos.get(index))) {
				// Si es adyacente al siguiente, cogemos el nuevo punto e incrementamos el
				// contador
				p = ptos.get(index);
				cont++;
			}
		}

		// Devolvemos el contador
		return cont;
	}

	/**
	 * Método que realiza la creación de edges por la derecha de un cluster
	 * 
	 * @param c        El cluster a observar
	 * @param clusters La lista de todos los clusters
	 * @param mapa     El mapa que los contiene
	 */
	private static void rigthEdge(Cluster c, ArrayList<Cluster> clusters, Mapa mapa) {
		// Cogemos el cluster adyacente derecho
		Cluster cRight = c.rightAdyacent(clusters, mapa);
		// Si existe
		if (cRight != null) {
			// Cogemos el límite derecho del cluster
			ArrayList<Punto> lRigh = c.getRightLimit();
			// Cogemos el límite izquierdo del adyacente
			ArrayList<Punto> lLeft = cRight.getLeftLimit();

			// DEejamos que se haga el trabajo de creación de edges para los límites
			// izquierdo y derecho obtenidos
			workEdges(lRigh, lLeft, mapa);
		}
	}

	/**
	 * Método que realiza la creación de edges de un cluster hacia abajo
	 * 
	 * @param c        El cluster a observar
	 * @param clusters La lista de todos los clusters
	 * @param mapa     El mapa que los contiene
	 */
	private static void bottomEdge(Cluster c, ArrayList<Cluster> clusters, Mapa mapa) {
		// Cogemos el cluster adyacente inferior
		Cluster cInf = c.bottomAdyacent(clusters, mapa);
		// Si existe
		if (cInf != null) {
			// Cogemos el límite inferior del cluster
			ArrayList<Punto> lInf = c.getBottomLimit();
			// Cogemos el límite superior de su adyacente
			ArrayList<Punto> lSup = cInf.getTopLimit();

			// Dejamos que se haga el trabajo de creación de edges para los límites superior
			// e inferior obtenidos
			workEdges(lInf, lSup, mapa);
		}

	}

	/**
	 * Método que, dado 2 límites (que suponemos adyacentes) y el mapa, crea y pinta
	 * los Edges
	 * 
	 * @param l1
	 * @param l2
	 * @param mapa
	 */
	private static void workEdges(ArrayList<Punto> l1, ArrayList<Punto> l2, Mapa mapa) {
		// Cogemos la lista de obstáculos del mapa
		ArrayList<Punto> obs1 = (ArrayList<Punto>) mapa.obstaculos.clone();
		// Y nos quedamos solo con los puntos coincidentes
		obs1.retainAll(l1);

		// Creamos un ArrayList con los índices de los obstáculos en el primer límite
		ArrayList<Integer> i1 = new ArrayList<>();
		// Si hay elementos
		if (!obs1.isEmpty()) {
			// Se van añadiendo índices
			for (Punto obs : obs1)
				i1.add(l1.indexOf(obs));
		}

		// Los ordenamos en orden descendiente (ya que si eliminas el primer elemento,
		// el segundo pasa a ser el primero, pero no a la inversa):
		Collections.sort(i1, Collections.reverseOrder());

		// Borramos de los límites los obstáculos, ya que no vamos a poder hacer caminos
		// sobre ellos. También lo hacemos en los segundos límites
		for (Integer ind : i1) {
			l1.remove(ind.intValue());
			l2.remove(ind.intValue());
		}

		// Volvemos a coger la lista de obstáculos del mapa
		ArrayList<Punto> obs2 = (ArrayList<Punto>) mapa.obstaculos.clone();
		// Y nos quedamos con los coincidentes
		obs2.retainAll(l2);

		// Creamos un ArrayList con los índices de los obstáculos en el límite
		ArrayList<Integer> i2 = new ArrayList<>();
		// Si hay elementos
		if (!obs2.isEmpty()) {
			// Se van añadiendo índices
			for (Punto obs : obs2)
				i2.add(l2.indexOf(obs));
		}

		// Ordenamos la lista de índices en orden descendiente
		Collections.sort(i2, Collections.reverseOrder());

		// Borramos de ambos límites los obstáculos, ya que no vamos a poder hacer
		// caminos sobre ellos.
		for (Integer ind : i2) {
			l2.remove(ind.intValue());
			l1.remove(ind.intValue());
		}

		// Si no están vacíos los límites (comprobamos uno de ellos ya que ambos se
		// vacian de igual manera):
		if (!l1.isEmpty()) {

			// Creamos un array que te diga cuantos puntos van juntos
			ArrayList<Integer> juntos = new ArrayList<>();

			// También, creamos otro array que diga cuántos edges debería haber, teniendo en
			// cuenta el umbral
			ArrayList<Integer> cantidades = new ArrayList<>();

			// Un sumatorio para tener controlado el índice
			int index = 0;

			while (index < l1.size()) {
				// Vamos contando puntos consecutivos
				int n = contarPtosConsecutivos(l1, index);
				// Los añadimos a la lista
				juntos.add(n);
				// Vemos cuántos edges deberían crearse:
				if (n > umbral)
					cantidades.add(2);
				else
					cantidades.add(1);
				// Incrementamos el indice
				index += n;
			}

			// Una vez tenemos cuántos puntos hay consecutivos y la cantidad de edges que
			// debemos abrir, pintamos el mapa
			pintarEdges(l1, l2, juntos, cantidades, mapa);

		}
	}

	/**
	 * Método que pinta los edges entre los clusters. No se pintan los puntos de
	 * interés (ptos inicial, final u obstáculos)
	 * 
	 * @param l1
	 * @param l2
	 * @param juntos
	 * @param cantidades
	 * @param mapa
	 */
	private static void pintarEdges(ArrayList<Punto> l1, ArrayList<Punto> l2, ArrayList<Integer> juntos,
			ArrayList<Integer> cantidades, Mapa mapa) {

		// Creamos un contador, para tener localizado el índice
		int index = 0;

		// Obtenemos cada cantidad de puntos consecutivos
		for (int n : juntos) {
			// Cogemos los primeros puntos de ambas listas
			Punto pl1_1 = l1.get(index);
			Punto pl2_1 = l2.get(index);
			// Comprobamos si solo hay un punto, dado que en ese caso solo pintamos un punto
			// a cada lado
			// Si n es 1 simplemente se pinta un punto en cada lado
			if (n == 1) {
				// No se pintan los puntos de interés (no comparo si es obstáculos porque están
				// borrados)
				if (!pl1_1.equals(mapa.pto_final) && !pl1_1.equals(mapa.pto_inicial))
					mapa.pintarMapa(cEntrance, pl1_1);

				if (!pl2_1.equals(mapa.pto_final) && !pl2_1.equals(mapa.pto_final))
					mapa.pintarMapa(cEntrance, pl2_1);

			}
			// En caso de que haya más puntos consecutivos, comprobamos cuántos edges se van
			// a crear comparando con el umbral
			else {
				// Si iguala o supera al umbral, se crean 2 edges, a cada extremo
				int index2 = index + n - 1;
				if (n >= umbral) {
					// Cogemos el índice extremo
					Punto pl1_2 = l1.get(index2);
					Punto pl2_2 = l2.get(index2);

					// Comprobamos que cada punto que tenemos no sea de interés para colorearlo
					if (!pl1_1.equals(mapa.pto_final) && !pl1_1.equals(mapa.pto_inicial))
						mapa.pintarMapa(cEntrance, pl1_1);

					if (!pl2_1.equals(mapa.pto_final) && !pl2_1.equals(mapa.pto_final))
						mapa.pintarMapa(cEntrance, pl2_1);

					if (!pl1_2.equals(mapa.pto_final) && !pl1_2.equals(mapa.pto_inicial))
						mapa.pintarMapa(cEntrance, pl1_2);

					if (!pl2_2.equals(mapa.pto_final) && !pl2_2.equals(mapa.pto_final))
						mapa.pintarMapa(cEntrance, pl2_2);

				}
				// En caso contrario, se crea uno en medio
				else {
					// Establecemos el "punto de mira" en el pto intermedio
					index2 = (int) Math.round(index2 / 2.0);
					// Cogemos esos puntos intermedios
					pl1_1 = l1.get(index2);
					pl2_1 = l2.get(index2);

					// Comprobamos que no sean de interés, para así colorearlos
					if (!pl1_1.equals(mapa.pto_final) && !pl1_1.equals(mapa.pto_inicial))
						mapa.pintarMapa(cEntrance, pl1_1);

					if (!pl2_1.equals(mapa.pto_final) && !pl2_1.equals(mapa.pto_final))
						mapa.pintarMapa(cEntrance, pl2_1);
				}
			}
			index += n;
		}

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

		for (Cluster c : clusters)
			sb.append("Cluster ").append(cont++).append(":\n").append(c.toString());

		System.out.println(sb.toString() + "\n");

	}
}

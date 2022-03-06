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
	public static final int CLUSTER_5X5 = 2;

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
	private static int umbral;

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
			// Realizamos la creación de cluster
			crearClusters(mapa, 10, 10);
			break;
		case CLUSTER_5X5:
			// Realizamos la creación de cluster
			crearClusters(mapa, 5, 5);

			break;
		// Si no, muestra mensaje de error
		default:
			JOptionPane.showMessageDialog(new JFrame(), "Tamaño de cluster no compatible con el tamaño del mapa");
			break;
		}

	}

	/**
	 * Método para, una vez tenemos los clusters, definir las nodos y arcos (edges),
	 * dado el mapa (para pintarlo también). También, pasamos el valor del umbral a
	 * aplicar para la creación de los nodos.
	 * 
	 * @param mapa
	 * @param vumbral
	 */
	public static void definirEdges(Mapa mapa, int vumbral) {

		// Asignamos el valor al umbral
		umbral = vumbral;

		// Para no repetir 2 veces las comprobaciones, iremos recorriendo los cluster
		// comprobando siempre sus adyacentes derecho e inferior (si los tiene)

		// Vamos recorriendo los clusters generados siguiendo el orden (de izda a
		// derecha y al llegar al final se baja una fila):
		for (Cluster c : clusters) {
			// CREACIÓN DE ARCOS EXTERNOS
			// 1. Creamos los edge inferiores
			bottomEdge(c, clusters, mapa);

			// 2. Creamos los edges por la derecha
			rigthEdge(c, clusters, mapa);

			// CREACIÓN DE ARCOS INTERNOS
			// 3. Ordenamos los nodos de cada cluster tras finalizar con los arcos externos
			// (edges)
			Collections.sort(c.getNodos());

			// 4. Creamos los arcos internos
			internalEdges(c, mapa);
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
	private static void crearClusters(Mapa mapa, int fils, int cols) {
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
	private static int contarPtosConsecutivos(ArrayList<Punto> ptos, int index) {

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

			// Creamos un array con el cluster original (posición 0) y con el cluster vecino
			// (posición 1)
			Cluster[] cls = new Cluster[2];
			cls[0] = c;
			cls[1] = cRight;
			// Dejamos que se haga el trabajo de creación de edges para los límites
			// izquierdo y derecho obtenidos

			workEdges(lRigh, lLeft, cls, mapa);
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

			// Creamos un array con el cluster original (posición 0) y con el cluster vecino
			// (posición 1)
			Cluster[] cls = new Cluster[2];
			cls[0] = c;
			cls[1] = cInf;

			// Dejamos que se haga el trabajo de creación de edges para los límites superior
			// e inferior obtenidos
			workEdges(lInf, lSup, cls, mapa);
		}

	}

	/**
	 * Método que, dado 2 límites (que suponemos adyacentes), el par de clusters y
	 * el mapa, crea y pinta los Edges
	 * 
	 * @param l1
	 * @param l2
	 * @param cls
	 * @param mapa
	 */
	private static void workEdges(ArrayList<Punto> l1, ArrayList<Punto> l2, Cluster[] cls, Mapa mapa) {
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
			// Pintamos el mapa
			pintarEdges(l1, l2, cls, mapa);

		}
	}

	/**
	 * Método que pinta los edges entre los clusters. Se pintan en gris, o se
	 * oscurecen si son puntos inicial o final
	 * 
	 * @param l1
	 * @param l2
	 * @param mapa
	 */
	private static void pintarEdges(ArrayList<Punto> l1, ArrayList<Punto> l2, Cluster[] cls, Mapa mapa) {

		// Creamos un contador, para tener localizado el índice
		int index = 0;

		// Obtenemos cada cantidad de puntos consecutivos
		while (index < l1.size()) {
			int n = contarPtosConsecutivos(l1, index);
			// Cogemos los primeros puntos de ambas listas
			Punto pl1_1 = l1.get(index);
			Punto pl2_1 = l2.get(index);
			// Comprobamos si solo hay un punto, dado que en ese caso solo pintamos un punto
			// a cada lado
			// Si n es 1 simplemente se pinta un punto en cada lado
			if (n == 1) {
				// Creamos el arco externo
				pl1_1.addArcoExterno(pl2_1);

				// Pintamos en los nodos
				oscurecerMapa(pl1_1, mapa);
				oscurecerMapa(pl2_1, mapa);

				// Añadimos el punto a la lista de nodos del cluster (no ordenamos porque los
				// puntos ya vienen ordenados)
				// Al cluster original
				cls[0].addNodo(pl1_1, false);
				// Al cluster adyacente
				cls[1].addNodo(pl2_1, false);

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

					// Creamos los arcos externos
					pl1_1.addArcoExterno(pl2_1);
					pl1_2.addArcoExterno(pl2_2);

					// Pintamos en los nodos
					oscurecerMapa(pl1_1, mapa);
					oscurecerMapa(pl1_2, mapa);
					oscurecerMapa(pl2_1, mapa);
					oscurecerMapa(pl2_2, mapa);

					// Los añadimos a los respectivos clusters (no ordenamos porque los puntos ya
					// vienen ordenados)
					// Al cluster original
					cls[0].addNodo(pl1_1, false);
					cls[0].addNodo(pl1_2, false);
					// Al cluster adyacente
					cls[1].addNodo(pl2_1, false);
					cls[1].addNodo(pl2_2, false);

				}
				// En caso contrario, se crea uno en medio
				else {
					// Establecemos el "punto de mira" en el pto intermedio
					index2 = (int) Math.round(index2 / 2.0);
					// Cogemos esos puntos intermedios
					pl1_1 = l1.get(index2);
					pl2_1 = l2.get(index2);

					// Creamos el arco entre ambos puntos
					pl1_1.addArcoExterno(pl2_1);

					// Pintamos en los nodos
					oscurecerMapa(pl1_1, mapa);
					oscurecerMapa(pl2_1, mapa);

					// Los añadimos como nodos a cada cluster (no ordenamos porque los puntos ya
					// vienen ordenados)
					// Al cluster original
					cls[0].addNodo(pl1_1, false);
					// Al cluster adyacente
					cls[1].addNodo(pl2_1, false);
				}
			}
			index += n;
		}

	}

	/**
	 * Pinta de gris un punto dado o lo oscurece en caso de ser inicial o final
	 * 
	 * @param p
	 * @param mapa
	 */
	private static void oscurecerMapa(Punto p, Mapa mapa) {
		// Si no coincide con un punto inicial o final, pintamos en gris
		if (!p.equals(mapa.pto_final) && !p.equals(mapa.pto_inicial))
			mapa.pintarMapa(cEntrance, p);
		// En otro caso, oscurecemos
		else if (p.equals(mapa.pto_final))
			mapa.pintarMapa(Mapa.cFinal.darker(), p);
		else
			mapa.pintarMapa(Mapa.cInicial.darker(), p);
	}

	/**
	 * Método para crear los arcos internos, dado el cluster que contiene los nodos
	 * y el mapa
	 * 
	 * @param c
	 * @param mapa
	 */
	private static void internalEdges(Cluster c, Mapa mapa) {

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

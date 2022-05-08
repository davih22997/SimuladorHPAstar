package program;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

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
	protected static final Border defaultborder = UIManager.getBorder("Button.border");
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

	// Variable para definir el umbral para ver cuántas entradas hay entre los
	// clústers
	private static int umbral;

	// Variables para las tablas hash:
	protected static HashMap<Cluster, ArrayList<Punto>> nodos_cluster; // Nodos que hay en un clusters
	protected static HashMap<Punto, ArrayList<Punto>> sucesores; // Lista de sucesores de un punto
	protected static HashMap<Arco, Integer> costes; // Costes de cada arco
	protected static HashMap<Arco, ArrayList<Punto>> caminos; // Caminos de cada arco

	protected static HashMap<Punto, Cluster> ptos_interes; // Puntos de interés (ptos inicial y final, en caso de que no
															// estén entre los nodos)

	// Variables que vamos a obtener de la simulación de HPA*
	protected static int preiters; // Iteraciones realizadas en la fase de preprocesamiento
	protected static int prememoria; // Cantidad de nodos abiertos en la fase de preprocesamiento

	protected static int esiters; // Iteraciones realizadas al introducir los puntos inicial/final
	protected static int esmemoria; // Cantidad de nodos abiertos al introducir los puntos inicial/final

	protected static int refiters; // Iteraciones realizadas en la fase de refinado
	protected static int refmemoria; // Cantidad de nodos abiertos en la fase de refinado
	protected static int longitud; // Longitud de la solución final obtenida

	protected static int modo; // Variable que te indica si pintar o no

	/**
	 * Método para definir los clusters si no es un test
	 * 
	 * @param mapa
	 * @param tam
	 */
	public static void definirClusters(Mapa mapa, int tam) {
		definirClusters(mapa, tam, 0);
	}

	/**
	 * Método para definir el tamaño de los clusters dada una constante que
	 * represente su tamaño y pintarlos. El booleano test a true indica que no se va
	 * a pintar
	 * 
	 * @param mapa
	 * @param tam
	 * @param test
	 */
	public static void definirClusters(Mapa mapa, int tam, int mode) {

		// Inicializamos la lista de clusters
		clusters = new ArrayList<>();

		// Y las tablas hash
		sucesores = new HashMap<>();
		costes = new HashMap<>();
		caminos = new HashMap<>();
		nodos_cluster = new HashMap<>();
		ptos_interes = new HashMap<>();

		// Y las variables
		preiters = 0;
		prememoria = 0;
		refiters = 0;
		refmemoria = 0;
		longitud = 0;

		// Indicamos si pintamos
		modo = mode;

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
			ArrayList<Punto> nodos = nodos_cluster.get(c);
			Collections.sort(nodos);
			nodos_cluster.put(c, nodos);

			// 5. Creamos los arcos internos
			intraEdges(c, mapa);
		}

	}

	/**
	 * Método para meter los puntos inicial y final (E/S) en los clusters
	 * 
	 * @param mapa
	 */
	public static void meterES(Mapa mapa) {
		// Iniciamos las variables de introducción de puntos inicial y final
		esiters = 0;
		esmemoria = 0;
		// Llamamos a los métodos para introducir los puntos inicial y final
		meterPuntosInteres(mapa);
		ESEdges(mapa);
	}

	/**
	 * Método para meter los puntos de interés (puntos inicial y final) para luego
	 * considerarlos
	 * 
	 * @param mapa
	 */
	private static void meterPuntosInteres(Mapa mapa) {
		boolean p1 = false;
		boolean p2 = false;
		for (int i = 0; i < clusters.size() && (!p1 || !p2); i++) {
			Cluster c = clusters.get(i);
			if (!p1 && c.inCluster(mapa.pto_inicial)) {
				p1 = true;
				if (!nodos_cluster.get(c).contains(mapa.pto_inicial)) {
					// Lo metemos en la lista de puntos de interés y en la lista de nodos
					Punto p = mapa.pto_inicial.clone();
					ptos_interes.put(p, c);
					ArrayList<Punto> nodos = nodos_cluster.get(c);
					nodos.add(p);
					Collections.sort(nodos);
					nodos_cluster.put(c, nodos);

					if (modo != 1)
						oscurecerMapa(mapa.pto_inicial, mapa);
				}
			}
			if (!p2 && c.inCluster(mapa.pto_final)) {
				p2 = true;
				if (!nodos_cluster.get(c).contains(mapa.pto_final)) {
					Punto p = mapa.pto_final.clone();
					// Lo metemos en la lista de puntos de interés y en la lista de nodos
					ptos_interes.put(p, c);
					ArrayList<Punto> nodos = nodos_cluster.get(c);
					nodos.add(p);
					Collections.sort(nodos);
					nodos_cluster.put(c, nodos);

					if (modo != 1)
						oscurecerMapa(mapa.pto_final, mapa);
				}
			}
		}

	}

	/**
	 * Método para ver la tabla de los arcos internos del índice del cluster en la
	 * lista de clusters
	 * 
	 * @param index
	 */
	public static void verTabla(int index) {
		Cluster c = clusters.get(index);

		ArrayList<Punto> nodos = nodos_cluster.get(c);
		int tam = nodos.size();

		// Creamos el panel que va a contener todos los objetos
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.WHITE);

		if (tam >= 1) {
			// Creamos el título para mostrar la lista de nodos
			JLabel lnodos = new JLabel("Lista de nodos");
			lnodos.setAlignmentX(JFrame.LEFT_ALIGNMENT);
			lnodos.setFont(new Font("Verdana", Font.BOLD, 18));

			// Le introducimos un margen inferior
			Border marginbottom = new EmptyBorder(0, 0, 5, 0);
			lnodos.setBorder(new CompoundBorder(lnodos.getBorder(), marginbottom));

			// Creamos un array con los datos de cada nodo
			JLabel[] nodes = new JLabel[tam];

			// Vamos añadiendo
			for (int i = 0; i < tam; i++) {
				nodes[i] = new JLabel(new String("n" + index + "," + i + ": " + nodos.get(i)));
				nodes[i].setAlignmentX(JFrame.LEFT_ALIGNMENT);
			}

			nodes[tam - 1].setBorder(new CompoundBorder(nodes[tam - 1].getBorder(), marginbottom));

			// Creamos el título para la tabla
			JLabel ltabla = new JLabel("Arcos internos");
			ltabla.setAlignmentX(JFrame.LEFT_ALIGNMENT);
			ltabla.setFont(new Font("Verdana", Font.BOLD, 18));
			ltabla.setBorder(new CompoundBorder(ltabla.getBorder(), marginbottom));

			// Creamos una caja vertical para agrupar los datos:
			Box bNodos = Box.createVerticalBox();
			bNodos.add(lnodos);
			for (JLabel node : nodes)
				bNodos.add(node);

			bNodos.add(ltabla);

			// Añadimos los datos al panel
			panel.add(bNodos, BorderLayout.WEST);

			// Creamos los datos de la tabla
			Object[][] data = new Object[tam][tam + 1];
			String[] columns = new String[tam + 1];

			columns[0] = "";
			ArrayList<Punto> visitados = new ArrayList<>();

			for (int i = 0; i < nodos.size(); i++) {
				Punto p = nodos.get(i);
				visitados.add(p);
				String name = "n" + index + "," + i;
				columns[i + 1] = name;
				data[i][0] = name;

				ArrayList<Edge> edges = p.getArcosInternos();

				for (int j = 1; j <= i + 1; j++) {
					data[i][j] = "X";
				}
				int idx = i + 2;
				for (Edge edge : edges) {
					if (!visitados.contains(edge.pfin)) {
						if (edge.coste != Integer.MAX_VALUE)
							data[i][idx++] = edge.coste;
						else
							data[i][idx++] = "Infinito";
					}
				}

			}

			// Creamos la tabla con los datos
			JTable tabla = new JTable(data, columns);

			// Configuramos para que el tamaño sea suficiente para mostrar todos los datos
			tabla.setFillsViewportHeight(true);
			tabla.setPreferredScrollableViewportSize(tabla.getPreferredScrollableViewportSize());
			tabla.setFillsViewportHeight(true);
			// Cancelamos que se pueda modificar el tamaño de las columnas
			tabla.getTableHeader().setResizingAllowed(false);

			// Centramos el texto dentro de la tabla
			DefaultTableCellRenderer center = new DefaultTableCellRenderer();
			center.setHorizontalAlignment(SwingConstants.CENTER);
			tabla.setDefaultRenderer(Object.class, center);

			// Agrupamos la tabla y su título en una caja vertical
			Box bTabla = Box.createVerticalBox();
			// bTabla.add(ltabla);
			bTabla.add(tabla.getTableHeader());
			bTabla.add(tabla);
			bTabla.setAlignmentX(JFrame.CENTER_ALIGNMENT);

			// Añadimos la tabla al panel
			panel.add(bTabla, BorderLayout.SOUTH);

		} else {
			JLabel text = new JLabel("No hay nodos");
			text.setAlignmentX(JFrame.LEFT_ALIGNMENT);
			text.setFont(new Font("Verdana", Font.BOLD, 18));
			panel.add(text);
		}
		// Creamos la ventana
		JFrame frame = new JFrame("Nodos internos");
		frame.setResizable(false);

		// Le indicamos que no cierre la aplicación al cerrar la ventana
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Le añadimos el panel
		frame.setContentPane(panel);

		// Lo mostramos
		frame.pack();
		frame.setVisible(true);

		// Lo añadimos a la lista de frames a eliminar de la interfaz
		if (modo == 0) {
			Interfaz.addFrame(frame);

			// Se escribe la acción en el logger
			Interfaz.escribir("Mostrados los arcos y nodos internos del cluster " + index + ".\n");
			Interfaz.escribir("(0 es el primer cluster; " + (clusters.size() - 1) + " es el último.\n");
		}
	}

	/**
	 * Método para la visualización de todos los arcos de los nodos del mapa
	 * (visualización del grafo abstracto)
	 */
	public static void visualizarArcos(Mapa mapa) {
		// Creamos el panel con todo el contenido
		PanelArcos panel = new PanelArcos(mapa, clusters);

		// Creamos el frame
		JFrame frame = new JFrame();

		// Le ponemos el título
		frame.setTitle("Grafo abstracto");

		// Le indicamos que no cierre la aplicación completa al cerrar la ventana
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Le añadimos el panel
		frame.add(panel);

		// Hacemos que el tamaño sea siempre igual
		frame.setResizable(false);

		// Lo mostramos
		frame.pack();
		frame.setVisible(true);

		// Lo añadimos a la lista de frames a eliminar de la interfaz
		Interfaz.addFrame(frame);

		// Se escribe la acción en el logger
		Interfaz.escribir("Mostados todos los arcos.\n");

	}

	/**
	 * Método para crear las hashTables y aplicar A*
	 * 
	 * @param mapa
	 */
	public static void aplicarAstar(Mapa mapa) {

		// Iniciamos las varibales de la parte de búsqueda en grafo abstracto y
		// refinamiento
		refiters = 0;
		refmemoria = 0;
		longitud = 0;

		// Aplicamos A* para hallar el camino de menor coste
		if (modo != 1)
			Astar.busquedaEnHPAstar(mapa, Astar.VECINOS_8);
		else
			Astar.testEnHPAstar(mapa, Astar.VECINOS_8);

	}

	/**
	 * Borramos los ptos inicial y final
	 * 
	 * @param mapa
	 */
	public static void borrarES(Mapa mapa) {
		// 1. Punto inicial
		removePunto(mapa.pto_inicial);

		// 2. Punto final
		removePunto(mapa.pto_final);

		// 3. Reiniciamos las variables de iteraciones y memoria correspondientes a la
		// introducción de puntos inicial y final
		esiters = 0;
		esmemoria = 0;
	}

	/**
	 * Método para borrar un punto de interés dado
	 * 
	 * @param p
	 */
	private static void removePunto(Punto p) {
		if (ptos_interes.containsKey(p)) {
			Cluster c = ptos_interes.get(p);
			for (Punto nodo : sucesores.get(p)) {
				Arco a1 = new Arco(p, nodo);
				Arco a2 = new Arco(nodo, p);
				nodo.getArcosInternos().remove(nodo.getEdge(p));

				costes.remove(a1);
				costes.remove(a2);

				caminos.remove(a1);
				caminos.remove(a2);

				ArrayList<Punto> sucs = sucesores.get(nodo);
				sucs.remove(p);
				sucesores.put(nodo, sucs);
			}

			sucesores.remove(p);
			ArrayList<Punto> nods = nodos_cluster.get(c);
			nods.remove(p);
			nodos_cluster.put(c, nods);
			ptos_interes.remove(p);
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
				// Si queremos pintar
				if (modo == 0) {
					// 1.
					if (f % fils == 0) {
						// además 3.
						if (c % cols == 0) {
							mapa.pintarBorde(bctopleft, f, c);
							// Añadimos el cluster cuando coincide con la casilla inicial
							Cluster clust = new Cluster(fils, cols, f, c);
							clusters.add(clust);
							nodos_cluster.put(clust, new ArrayList<>());
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
				} // Si no queremos pintar
				else if (f % fils == 0 && c % cols == 0) {
					// Simplemente añadimos al cluster cuando coincide la casilla inicial
					Cluster clust = new Cluster(fils, cols, f, c);
					clusters.add(clust);
					nodos_cluster.put(clust, new ArrayList<>());
				}
			}
		}
		// Finalmente, se ordena la lista de clusters
		Collections.sort(clusters);
		if (modo == 0)
			Interfaz.escribir("Se han creado los clústers.\n");
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

		// Vamos eliminando los puntos que se encuentran entre los obstáculos
		// (recorremos la lista a la inversa para poder borrar sin que afecte al orden)
		for (int i = l1.size() - 1; i >= 0; i--) {
			if (mapa.obstaculos.contains(l1.get(i))) {
				l1.remove(i);
				l2.remove(i);
			}
		}

		// Aquí hacemos lo mismo, pero teniendo en cuenta el segundo límite
		for (int i = l2.size() - 1; i >= 0; i--) {
			if (mapa.obstaculos.contains(l2.get(i))) {
				l2.remove(i);
				l1.remove(i);
			}
		}

		// Si quedan nodos para unir, se sigue
		if (!l1.isEmpty())
			pintarEdges(l1, l2, cls, mapa);

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
				// Añadimos a la tabla
				ArrayList<Punto> c = new ArrayList<>();
				c.add(pl1_1);
				c.add(pl2_1);
				meterDatosHash(pl1_1, pl2_1, 100, c);

				if (modo != 1) {
					// Pintamos en los nodos
					oscurecerMapa(pl1_1, mapa);
					oscurecerMapa(pl2_1, mapa);
				}
				// Añadimos el punto a la lista de nodos del cluster (no ordenamos porque los
				// puntos ya vienen ordenados)
				// Al cluster original
				addNodo(cls[0], pl1_1, false);
				// Al cluster adyacente
				addNodo(cls[1], pl2_1, false);
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
					// Añadimos a la tabla
					ArrayList<Punto> c1 = new ArrayList<>();
					c1.add(pl1_1);
					c1.add(pl2_1);
					meterDatosHash(pl1_1, pl2_1, 100, c1);

					pl1_2.addArcoExterno(pl2_2);
					ArrayList<Punto> c2 = new ArrayList<>();
					c2.add(pl1_2);
					c2.add(pl2_2);
					meterDatosHash(pl1_2, pl2_2, 100, c2);

					if (modo != 1) {
						// Pintamos en los nodos
						oscurecerMapa(pl1_1, mapa);
						oscurecerMapa(pl1_2, mapa);
						oscurecerMapa(pl2_1, mapa);
						oscurecerMapa(pl2_2, mapa);
					}

					// Los añadimos a los respectivos clusters (no ordenamos porque los puntos ya
					// vienen ordenados)
					// Al cluster original
					addNodo(cls[0], pl1_1, false);
					addNodo(cls[0], pl1_2, false);

					// Al cluster adyacente
					addNodo(cls[1], pl2_1, false);
					addNodo(cls[1], pl2_2, false);
				}
				// En caso contrario, se crea uno en medio
				else {
					// Establecemos el "punto de mira" en el pto intermedio
					index2 = (index + index2) / 2;
					// Cogemos esos puntos intermedios
					pl1_1 = l1.get(index2);
					pl2_1 = l2.get(index2);

					// Creamos el arco entre ambos puntos
					pl1_1.addArcoExterno(pl2_1);

					ArrayList<Punto> c1 = new ArrayList<>();
					c1.add(pl1_1);
					c1.add(pl2_1);
					meterDatosHash(pl1_1, pl2_1, 100, c1);

					if (modo != 1) {
						// Pintamos en los nodos
						oscurecerMapa(pl1_1, mapa);
						oscurecerMapa(pl2_1, mapa);
					}

					// Los añadimos como nodos a cada cluster (no ordenamos porque los puntos ya
					// vienen ordenados)
					// Al cluster original
					addNodo(cls[0], pl1_1, false);
					// Al cluster adyacente
					addNodo(cls[1], pl2_1, false);

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
	private static void intraEdges(Cluster c, Mapa mapa) {
		// Creamos el submapa que contiene todos los puntos libres (sin obstáculos)
		ArrayList<Punto> submapa = c.getSubMapa(mapa);

		// Se van creando arcos entre cada par de nodos del cluster
		ArrayList<Punto> nodos = nodos_cluster.get(c);
		// Copiamos la lista
		ArrayList<Punto> nodos2 = (ArrayList<Punto>) nodos.clone();

		for (int i = 0; i < nodos.size() - 1; i++) {
			// Cogemos el nodo del punto i
			Punto p1 = nodos.get(i);
			// Vamos eliminando el elemento que se coge de la lista de nodos en la copia
			nodos2.remove(p1);
			for (int j = 0; j < nodos2.size(); j++) {
				// Cogemos el otro nodo de la lista de nodos original
				int idx = nodos.indexOf(nodos2.get(j));
				Punto p2 = nodos.get(idx);

				Edge edge = Dijkstra.intraedge(p1, p2, submapa);
				Edge symm = edge.symm();

				p1.addArcoInterno(edge);
				// Le añadimos el edge simétrico
				p2.addArcoInterno(symm);

				// Añadimos los datos a las HashTables
				meterDatosHash(p1, p2, edge.coste, edge.camino, symm.camino);
			}
		}
	}

	/**
	 * Método para introducir los puntos de interés
	 * 
	 * @param mapa
	 */
	private static void ESEdges(Mapa mapa) {
		// Cogemos los puntos de interés (pueden ser 0, 1 ó 2, según ya estuvieran entre
		// los nodos de los clusters o no)
		Set<Punto> ptos = ptos_interes.keySet();
		for (Punto p : ptos) {
			Cluster c = ptos_interes.get(p);

			// Creamos el submapa que contiene todos los puntos
			ArrayList<Punto> submapa = c.getSubMapa(mapa);

			// Vamos cogiendo cada nodo
			for (Punto nodo : nodos_cluster.get(c)) {
				if (!nodo.equals(p)) {
					Edge edge = Dijkstra.intraedge(p, nodo, submapa);
					Edge symm = edge.symm();

					p.addArcoInterno(edge);
					Collections.sort(p.getArcosInternos());
					nodo.addArcoInterno(symm);
					Collections.sort(nodo.getArcosInternos());

					// Añadimos los datos a las HashTables
					meterDatosHash(p, nodo, edge.coste, edge.camino, symm.camino);
				}
			}
		}
	}

	/**
	 * Realiza la parte de preprocesamiento de HPA* para el Test
	 * 
	 * @param mapa
	 * @param umb
	 * @param tcluster
	 */
	protected static void TestHPAstar(Mapa mapa, int umb, int tcluster) {
		// 1. Definimos los clusters
		definirClusters(mapa, tcluster, 1);
		// 2. Creamos los arcos (internos y externos)
		definirEdges(mapa, umb);
	}

	/**
	 * Realiza la parte de introducción de puntos inicial y final de HPA* para el
	 * Test
	 * 
	 * @param mapa
	 */
	protected static void TestHPAstar2(Mapa mapa) {
		// 3. Metemos los E/S
		meterES(mapa);
	}

	/**
	 * Realiza la parte de refinamiento de HPA* para el Test
	 * 
	 * @param mapa
	 */
	protected static void TestHPAstar3(Mapa mapa) {
		// 4. Aplicamos A*
		aplicarAstar(mapa);
	}

	/**
	 * Parte para probar puntos concretos en el Test de HPA*
	 * 
	 * @param mapa
	 * @param umb
	 * @param tcluster
	 */
	protected static void TestPruebaHPAstar(Mapa mapa, int umb, int tcluster) {
		// 1. Definimos los clusters
		definirClusters(mapa, tcluster, 2);
		// 2. Creamos los arcos (internos y externos)
		// a. Definimos los Edges
		definirEdges(mapa, umb);
		// b. Introducimos los E/S
		meterES(mapa);
		// 3. Aplicamos A*
		aplicarAstar(mapa);

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

	/**
	 * Método para añadir el nodo a la lista de nodos
	 * 
	 * @param c
	 * @param p
	 * @param ordenar
	 */
	private static void addNodo(Cluster c, Punto p, boolean ordenar) {

		ArrayList<Punto> nodos = nodos_cluster.get(c);

		// Comprobamos que el punto pertenece al cluster y que no está guardado
		// en la lista de nodos para meterlo en la lista
		if (c.inCluster(p) && !nodos.contains(p)) {
			nodos.add(p);
		}

		// Si está en la lista de nodos, añadimos arcos externos
		else if (nodos.contains(p)) {
			// Cogemos el índice del punto
			int index = nodos.indexOf(p);
			// Metemos los edges al punto que está entre los nodos
			for (Punto edge : p.getArcosExternos())
				nodos.get(index).addArcoExterno(edge);

		}

		// Ordenamos la lista de nodos si así lo indicamos
		if (ordenar)
			Collections.sort(nodos);

		nodos_cluster.put(c, nodos);
	}

	/**
	 * Método para rellenar datos en las tablas sucesores, costes y caminos
	 * 
	 * @param p1
	 * @param p2
	 * @param coste
	 * @param c1
	 * @param c2
	 */
	private static void meterDatosHash(Punto p1, Punto p2, int coste, ArrayList<Punto> c1, ArrayList<Punto> c2) {
		if (coste != Integer.MAX_VALUE) {
			// 1. Creamos los arcos
			Arco a1 = new Arco(p1, p2);
			Arco a2 = new Arco(p2, p1);

			// 2. Metemos los costes
			costes.put(a1, coste);
			costes.put(a2, coste);

			// 3. Metemos los caminos
			caminos.put(a1, c1);
			caminos.put(a2, c2);

			// 4. Metemos en la lista de sucesores
			meterSucesor(p1, p2);
			meterSucesor(p2, p1);
		}
	}

	/**
	 * Método para introducir los valores a las tablas hash
	 * 
	 * @param p1
	 * @param p2
	 * @param coste
	 * @param c1
	 */
	private static void meterDatosHash(Punto p1, Punto p2, int coste, ArrayList<Punto> c1) {
		// Copiamos el camino de p1 a p2
		ArrayList<Punto> c2 = (ArrayList<Punto>) c1.clone();
		// Y la invertimos para que sea de p2 a p1
		Collections.reverse(c2);
		meterDatosHash(p1, p2, coste, c1, c2);
	}

	/**
	 * Mete como sucesor de un Punto p1 dado otro Punto p2
	 * 
	 * @param p1
	 * @param p2
	 */
	private static void meterSucesor(Punto p1, Punto p2) {
		ArrayList<Punto> nodos = new ArrayList<>();
		if (sucesores.containsKey(p1)) {
			nodos = sucesores.get(p1);
			if (!nodos.contains(p2))
				nodos.add(p2);
		} else
			nodos.add(p2);

		Collections.sort(nodos);

		sucesores.put(p1, nodos);
	}

}

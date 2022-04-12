package program;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
	protected static HashMap<Cluster, ArrayList<Punto>> nodos_cluster;
	protected static HashMap<Punto, ArrayList<Punto>> sucesores;
	protected static HashMap<Arco, Integer> costes;
	protected static HashMap<Arco, ArrayList<Punto>> caminos;

	// Variables que vamos a obtener de la simulación de HPA*
	protected static int preiters; // Iteraciones realizadas en la fase de preprocesamiento
	protected static int prememoria; // Cantidad de nodos abiertos en la fase de preprocesamiento

	protected static int refiters; // Iteraciones realizadas en la fase de refinado
	protected static int refmemoria; // Cantidad de nodos abiertos en la fase de refinado
	protected static int longitud; // Longitud de la solución final obtenida

	private static int modo; // Variable que te indica si pintar o no

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
			nodos_cluster.put(c, new ArrayList<>());

			// CREACIÓN DE ARCOS EXTERNOS
			// 1. Creamos los edge inferiores
			bottomEdge(c, clusters, mapa);

			// 2. Creamos los edges por la derecha
			rigthEdge(c, clusters, mapa);

			// 3. Añadimos los puntos inicial y final en caso de no estar y corresponder con
			// el cluster:
			if (c.inCluster(mapa.pto_inicial) && !c.getNodos().contains(mapa.pto_inicial)) {
				c.addNodo(mapa.pto_inicial, false);
				if (modo != 1)
					oscurecerMapa(mapa.pto_inicial, mapa);
			}

			if (c.inCluster(mapa.pto_final) && !c.getNodos().contains(mapa.pto_final)) {
				c.addNodo(mapa.pto_final, false);
				if (modo != 1)
					oscurecerMapa(mapa.pto_final, mapa);
			}

			// CREACIÓN DE ARCOS INTERNOS
			// 4. Ordenamos los nodos de cada cluster tras finalizar con los arcos externos
			// (edges)
			Collections.sort(c.getNodos());

			// 5. Creamos los arcos internos
			intraEdges(c, mapa);
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
					if (edge.coste != Double.MAX_VALUE)
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
		Interfaz.addFrame(frame);

		// Se escribe la acción en el logger
		Interfaz.escribir("Mostrados los arcos y nodos internos del cluster " + index + ".\n");
		Interfaz.escribir("(0 es el primer cluster; " + (clusters.size() - 1) + " es el último.\n");
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
		// Creamos las hashTables:
		sucesores = new HashMap<>();
		costes = new HashMap<>();
		caminos = new HashMap<>();

		// Vamos cogiendo cluster por cluster
		for (Cluster c : clusters) {
			// Vamos cogiendo los nodos
			for (Punto p : c.getNodos()) {
				// Creamos la lista de sucesores del punto p
				ArrayList<Punto> sucs = new ArrayList<>();
				// Añadimos los arcos externos
				for (Punto p2 : p.getArcosExternos()) {
					// Añadimos el punto a la lista de sucesores
					sucs.add(p2);

					// Creamos el camino
					ArrayList<Punto> camino = new ArrayList<>();
					camino.add(p);
					camino.add(p2);

					// Vamos a rellenar las tablas:
					// 1. Añadimos el punto del arco externo a los sucesores de p
					// 2. Creamos un arco entre ambos puntos
					// 3. Añadimos el coste (1 en este caso).
					// 4. Añadimos el camino
					rellenarTablas(p, p2, 1, camino);
				}

				// Añadimos los arcos internos
				for (Edge edge : p.getArcosInternos()) {
					// Comprobamos que el camino sea mayor que 0 (si hay unión entre ambos puntos)
					if (edge.camino.size() > 0) {
						Punto p2 = edge.pfin;
						// 1. Añadimos el punto del arco interno a los sucesores de p
						sucs.add(p2);
						// Vamos a rellenar las tablas:
						// 2. Creamos un arco entre ambos puntos
						// 3. A ese arco le añadimos el coste
						// 4. A ese arco le añadimos el camino
						rellenarTablas(p, p2, edge.coste, edge.camino);
					}
				}

				// Finalmente, añadimos la lista de sucesores a la tabla
				sucesores.put(p, sucs);
			}
		}

		// 2. Aplicamos A* para hallar el camino de menor coste
		if (modo == 0)
			Astar.busquedaEnHPAstar(mapa, Astar.VECINOS_8);
		else
			Astar.testEnHPAstar(mapa, CLUSTER_10X10);

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
				} // Si no
				else {
					if (f % fils == 0 && c % cols == 0)
						// Simplemente añadimos al cluster cuando coincide la casilla inicial
						clusters.add(new Cluster(fils, cols, f, c));
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

				if (modo == 0) {
					// Pintamos en los nodos
					oscurecerMapa(pl1_1, mapa);
					oscurecerMapa(pl2_1, mapa);
				}
				// Añadimos el punto a la lista de nodos del cluster (no ordenamos porque los
				// puntos ya vienen ordenados)
				// Al cluster original
				cls[0].addNodo(pl1_1, false);
				addNodo(cls[0], pl1_1, false);
				// Al cluster adyacente
				cls[1].addNodo(pl2_1, false);
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
					pl1_2.addArcoExterno(pl2_2);

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
					cls[0].addNodo(pl1_1, false);
					cls[0].addNodo(pl1_2, false);
					addNodo(cls[0], pl1_1, false);
					addNodo(cls[0], pl1_2, false);
					// Al cluster adyacente
					cls[1].addNodo(pl2_1, false);
					cls[1].addNodo(pl2_2, false);
					addNodo(cls[1], pl2_1, false);
					addNodo(cls[1], pl2_2, false);

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

					if (modo == 0) {
						// Pintamos en los nodos
						oscurecerMapa(pl1_1, mapa);
						oscurecerMapa(pl2_1, mapa);
					}

					// Los añadimos como nodos a cada cluster (no ordenamos porque los puntos ya
					// vienen ordenados)
					// Al cluster original
					cls[0].addNodo(pl1_1, false);
					addNodo(cls[0], pl1_1, false);
					// Al cluster adyacente
					cls[1].addNodo(pl2_1, false);
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
		// Creamos el submapa que contiene todos los puntos
		ArrayList<Punto> submapa = c.getSubMapa();
		// Le quitamos los obstaculos
		submapa.removeAll(mapa.obstaculos);

		// Se van creando arcos entre cada par de nodos del cluster
		// ArrayList<Punto> nodos = c.getNodos();
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
			}

		}

	}

	/**
	 * Método para ir rellenando los hashmaps
	 * 
	 * @param p1     Punto inicial
	 * @param p2     Punto final
	 * @param coste  Coste para llegar de p1 a p2
	 * @param camino Camino para llegar de p1 a p2
	 */
	private static void rellenarTablas(Punto p1, Punto p2, int coste, ArrayList<Punto> camino) {
		// Creamos un arco entre ambos puntos
		Arco arco = new Arco(p1, p2);
		// Añadimos el coste
		costes.put(arco, coste);
		// Añadimos el camino
		caminos.put(arco, camino);
	}

	/**
	 * Realiza el test de HPA* dados el mapa, un umbral y el tamaño de cluster
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
		// 3. Aplicamos A*
		aplicarAstar(mapa);
	}

	protected static void TestPruebaHPAstar(Mapa mapa, int umb, int tcluster) {
		// 1. Definimos los clusters
		definirClusters(mapa, tcluster, 2);
		Test.pressAnyKeyToContinue();
		// 2. Creamos los arcos (internos y externos)
		// a. Definimos los Edges
		definirEdges(mapa, umb);
		// b. Introducimos los E/S
		Test.pressAnyKeyToContinue();
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
	}

}

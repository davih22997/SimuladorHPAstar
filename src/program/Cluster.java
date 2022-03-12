package program;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Cluster implements Comparator<Cluster>, Comparable<Cluster> {

	// Los atributos que necesitamos de los clústers son:
	// Los límites que delimitan al cluster
	// private ArrayList<Punto> limites;
	// Y sus dimensiones
	private int filas;
	private int columnas;
	// Punto de inicio del cluster (esquina superior izda)
	private Punto inicio;

	private ArrayList<Punto> nodos;

	/**
	 * Método para crear un clúster dado un punto inicial (esquina superior izda)
	 * 
	 * @param filas
	 * @param columnas
	 * @param p_inicial
	 */
	public Cluster(int filas, int columnas, Punto p_inicial) {
		// Cogemos las dimensiones
		this.filas = filas;
		this.columnas = columnas;
		// Y el punto de origen del cluster
		this.inicio = p_inicial;

		nodos = new ArrayList<>();
	}

	/**
	 * Método para crear un clúster dadas la fila y la columna iniciales (esquina
	 * superior izda)
	 * 
	 * @param filas
	 * @param columnas
	 * @param fila_inicio
	 * @param col_inicio
	 */
	public Cluster(int filas, int columnas, int fila_inicio, int col_inicio) {
		this(filas, columnas, new Punto(fila_inicio, col_inicio));
	}

	/**
	 * Método para crear un clúster si te dan el contorno (suponemos que el contorno
	 * viene ordenado)
	 * 
	 * @param limites
	 */
	public Cluster(ArrayList<Punto> limites) {
		this((1 + limites.get(limites.size() - 1).getFila() - limites.get(0).getFila()),
				(1 + limites.get(limites.size() - 1).getCol() - limites.get(0).getCol()), limites.get(0));
	}

	/**
	 * Método para devolver el contorno del cluster
	 * 
	 * @return
	 */
	public ArrayList<Punto> getLimit() {
		// Cogemos las coordenadas del punto inicial
		int fila_inicio = inicio.getFila();
		int col_inicio = inicio.getCol();

		// Y a partir de las coordenadas de la casilla inicial, creamos la lista de
		// los puntos límite:
		ArrayList<Punto> limit = new ArrayList<>();
		// 1. Añadimos los
		// límites superior e inferior
		for (int fila = fila_inicio; fila < (fila_inicio + filas); fila += (filas - 1))
			for (int col = col_inicio; col < (col_inicio + columnas); col++)
				limit.add(new Punto(fila, col));

		// 2. Añadimos los límites izda y derecha
		for (int col = col_inicio; col < (col_inicio + columnas); col += (columnas - 1))
			for (int fila = (fila_inicio + 1); fila < (fila_inicio + filas - 1); fila++)
				limit.add(new Punto(fila, col));

		// 3. Ordenamos los límites orden teniendo en cuenta primero la fila, después
		// la columna
		Collections.sort(limit);

		return limit;
	}

	/**
	 * Método para obtener el contorno por el lateral superior
	 * 
	 * @return
	 */
	public ArrayList<Punto> getTopLimit() {
		ArrayList<Punto> limit = new ArrayList<>();

		// Simplemente nos movemos por columnas desde la fila inicial
		for (int c = getColInicial(); c < (getColInicial() + columnas); c++)
			limit.add(new Punto(getFilaInicial(), c));

		// Devolvemos sin ordenar porque las hemos introducido ordenadas
		return limit;
	}

	/**
	 * Método para obtener el contorno por el lateral inferior
	 * 
	 * @return
	 */
	public ArrayList<Punto> getBottomLimit() {
		ArrayList<Punto> limit = new ArrayList<>();

		// Nos movemos por columnas desde la fila final
		for (int c = getColInicial(); c <= getColFinal(); c++)
			limit.add(new Punto(getFilaFinal(), c));

		// Devolvemos sin ordenar porque las hemos introducido ordenadas
		return limit;
	}

	/**
	 * Método para obtener el contorno por el lateral izquierdo
	 * 
	 * @return
	 */
	public ArrayList<Punto> getLeftLimit() {
		ArrayList<Punto> limit = new ArrayList<>();

		// Nos movemos por filas desde la columna inicial
		for (int f = getFilaInicial(); f <= getFilaFinal(); f++)
			limit.add(new Punto(f, getColInicial()));

		// Devolvemos sin ordenar porque las hemos introducido ordenadas
		return limit;
	}

	/**
	 * Método para obtener el contorno por el lateral derecho
	 * 
	 * @return
	 */
	public ArrayList<Punto> getRightLimit() {
		ArrayList<Punto> limit = new ArrayList<>();

		// Nos movemos por filas desde la columna final
		for (int f = getFilaInicial(); f <= getFilaFinal(); f++)
			limit.add(new Punto(f, getColFinal()));

		// Devolvemos sin ordenar porque las hemos introducido ordenadas
		return limit;
	}

	/**
	 * Método que devuelve los clusters adyacentes, dada una lista de clusters
	 * definida y completa; y también el mapa
	 * 
	 * @param clusters Lista completa de clusters (incluyendo a este)
	 * @param mapa
	 * @return
	 */
	public ArrayList<Cluster> getAdyacents(ArrayList<Cluster> clusters, Mapa mapa) {
		ArrayList<Cluster> adyacentes = new ArrayList<>();

		// Cogemos el índice de este elemento
		int index = clusters.indexOf(this);

		// Hallamos el número de clusters que hay como columnas en el mapa
		int cols = mapa.getCols() / columnas;

		// Comprobamos para añadir por arriba
		if (!isTop(mapa))
			adyacentes.add(clusters.get(index - cols));
		// Comprobamos para añadir la de la izda
		if (!isLeft(mapa))
			adyacentes.add(clusters.get(index - 1));
		// Comprobamos para añadir la de la derecha
		if (!isRight(mapa))
			adyacentes.add(clusters.get(index + 1));
		// Comprobamos para añadir por abajo
		if (!isBottom(mapa))
			adyacentes.add(clusters.get(index + cols));

		// No ordenamos porque lo hemos introducido ordenado
		return adyacentes;
	}

	/**
	 * Devuelve, de haberlo, el cluster adyacente superior dados la lista de
	 * clusters y el mapa
	 * 
	 * @param clusters
	 * @param mapa
	 * @return
	 */
	public Cluster topAdyacent(ArrayList<Cluster> clusters, Mapa mapa) {
		int index = clusters.indexOf(this);
		int cols = mapa.getCols() / columnas;

		return isTop(mapa) ? null : clusters.get(index - cols);
	}

	/**
	 * Devuelve, de haberlo, el cluster adyacente inferior dados la lista de
	 * clusters y el mapa
	 * 
	 * @param clusters
	 * @param mapa
	 * @return
	 */
	public Cluster bottomAdyacent(ArrayList<Cluster> clusters, Mapa mapa) {
		int index = clusters.indexOf(this);
		int cols = mapa.getCols() / columnas;

		return isBottom(mapa) ? null : clusters.get(index + cols);
	}

	/**
	 * Devuelve, de haberlo, el cluster adyacente izquierdo dados la lista de
	 * clusters y el mapa
	 * 
	 * @param clusters
	 * @param mapa
	 * @return
	 */
	public Cluster leftAdyacent(ArrayList<Cluster> clusters, Mapa mapa) {
		int index = clusters.indexOf(this);

		return isLeft(mapa) ? null : clusters.get(index - 1);
	}

	/**
	 * Devuelve, de haberlo, el cluster adyacente derecho dados la lista de clusters
	 * y el mapa
	 * 
	 * @param clusters
	 * @param mapa
	 * @return
	 */
	public Cluster rightAdyacent(ArrayList<Cluster> clusters, Mapa mapa) {
		int index = clusters.indexOf(this);

		return isRight(mapa) ? null : clusters.get(index + 1);
	}

	/**
	 * Método para averiguar si otro cluster es adyacente a este dado también el
	 * mapa que los engloba Suponemos que los clusters que se comparan han de estar
	 * en el mapa, ser distintos y tener el mismo tamaño
	 * 
	 * @param cluster
	 * @return
	 */
	public boolean adyacentes(Cluster cluster, Mapa mapa) {
		boolean res = false;

		// Miramos que coincida o la fila o la columna iniciales
		// 1. Si coinciden las filas
		if (this.getFilaInicial() == cluster.getFilaInicial()) {
			// Comprobamos que sean contiguas las columnas inicial y final de ambos
			if ((this.getColFinal() + 1) == cluster.getColInicial()
					|| (cluster.getColFinal() + 1 == this.getColInicial()))
				res = true;
		}
		// 2. Si coinciden las columnas
		else if (this.getColInicial() == cluster.getColInicial()) {
			// Hacemos la misma comprobación que en el caso anterior pero con las filas
			if ((this.getFilaFinal() + 1) == cluster.getFilaInicial()
					|| (cluster.getFilaFinal() + 1) == this.getFilaInicial())
				res = true;
		}
		return res;
	}

	/**
	 * Método que te indica si el clúster contiene la primera fila del mapa
	 * 
	 * @param mapa
	 * @return
	 */
	public boolean isTop(Mapa mapa) {
		return getFilaInicial() == 0;
	}

	/**
	 * Método que te indica si el cluster contiene la última fila del mapa
	 * 
	 * @param mapa
	 * @return
	 */
	public boolean isBottom(Mapa mapa) {
		return getFilaFinal() == (mapa.getFilas() - 1);
	}

	/**
	 * Método que te indica si el clúster contiene la primera columna del mapa
	 * 
	 * @param mapa
	 * @return
	 */
	public boolean isLeft(Mapa mapa) {
		return getColInicial() == 0;
	}

	/**
	 * Método que te indica si el cluster contiene la última columna del mapa
	 * 
	 * @param mapa
	 * @return
	 */
	public boolean isRight(Mapa mapa) {
		return getColFinal() == (mapa.getCols() - 1);
	}

	/**
	 * Devuelve la primera casilla del cluster
	 * 
	 * @return
	 */
	public Punto getPuntoInicial() {
		return inicio;
	}

	/**
	 * Devuelve la última casilla del cluster
	 * 
	 * @return
	 */
	public Punto getPuntoFinal() {

		return new Punto(getFilaFinal(), getColFinal());
	}

	/**
	 * Devuelve la fila inicial del cluster
	 * 
	 * @return
	 */
	public int getFilaInicial() {
		return inicio.getFila();
	}

	/**
	 * Devuelve la fila final del cluster
	 * 
	 * @return
	 */
	public int getFilaFinal() {
		return inicio.getFila() + filas - 1;
	}

	/**
	 * Devuelve la columna inicial del cluster
	 * 
	 * @return
	 */
	public int getColInicial() {
		return inicio.getCol();
	}

	/**
	 * Devuelve la columna final del cluster
	 * 
	 * @return
	 */
	public int getColFinal() {
		return inicio.getCol() + columnas - 1;
	}

	/**
	 * Método que indica si un punto dado está dentro del cluster.
	 * 
	 * @param p
	 * @return
	 */
	public boolean inCluster(Punto p) {
		int pfila = p.getFila();
		int pcol = p.getCol();

		int fini = this.getFilaInicial();
		int cini = this.getColInicial();

		int ffin = this.getFilaFinal();
		int cfin = this.getColFinal();

		return pfila >= fini && pfila <= ffin && pcol >= cini && pcol <= cfin;
	}

	/**
	 * Método para añadir un nodo a la lista de nodos del cluster
	 * 
	 * @param p
	 * @param ordenar
	 */
	public void addNodo(Punto p, boolean ordenar) {
		// Comprobamos que el punto pertenece al cluster y que no está guardado
		// en la lista de nodos para meterlo en la lista
		if (inCluster(p) && !nodos.contains(p))
			nodos.add(p);

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

	/**
	 * Método para añadir varios nodos a la vez a la lista de nodos
	 * 
	 * @param l
	 * @param ordenar
	 */
	public void addNodos(ArrayList<Punto> l, boolean ordenar) {
		// Para cada punto, comprobamos que pertenece al cluster y que no está guardado
		// en la lista de nodos para meterlo en la lista
		for (Punto p : l)
			if (inCluster(p) && !nodos.contains(p))
				nodos.add(p);

		// Ordenamos la lista de nodos si así lo indicamos
		if (ordenar)
			Collections.sort(nodos);

	}

	/**
	 * Método para crear la lista de puntos total que contiene el cluster
	 * 
	 * @param m
	 * @return
	 */
	public ArrayList<Punto> getSubMapa() {

		// Creamos la lista que va a contener los puntos
		ArrayList<Punto> sm = new ArrayList<>();
		// Recorremos por filas
		for (int f = 0; f < filas; f++)
			// Antes de pasar a la siguiente fila, recorremos las columnas
			for (int c = 0; c < columnas; c++) {
				// Creamos un punto nuevo, teniendo en cuenta las coordenadas del punto inicial
				int fil = inicio.getFila() + f;
				int col = inicio.getCol() + c;

				Punto p = new Punto(fil, col);
				// Si ese punto está contenido en la lista de nodos, añadimos el punto del nodo
				if (nodos.contains(p)) {
					int index = nodos.indexOf(p);
					p = nodos.get(index);
				}

				sm.add(p);
			}

		return sm;
	}

	/**
	 * Método para obtener la lista de nodos del cluster
	 * 
	 * @return
	 */
	public ArrayList<Punto> getNodos() {
		return nodos;
	}

	/**
	 * Método para averiguar si el cluster está dentro del mapa
	 * 
	 * @param mapa
	 * @return
	 */
	public boolean inMap(Mapa mapa) {
		return this.getFilaInicial() >= 0 && this.getColInicial() >= 0 && this.getFilaFinal() < mapa.getFilas()
				&& this.getColFinal() < mapa.getCols();
	}

	@Override
	public boolean equals(Object o) {
		// Para que dos clusters sean iguales, han de coincidir sus puntos inicial y
		// final
		// Comparamos primero que tengan las mismas dimensiones
		return o instanceof Cluster
				? (this.filas == ((Cluster) o).filas && this.columnas == ((Cluster) o).columnas)
						&& (this.getPuntoInicial().equals(((Cluster) o).getPuntoInicial())
								&& this.getPuntoFinal().equals(((Cluster) o).getPuntoFinal()))
				: false;
	}

	@Override
	public int hashCode() {
		return inicio.hashCode();
	}

	@Override
	public int compareTo(Cluster c) {
		// Los clusters se ordenan teniendo en cuenta su fila inicial, columna inicial
		int val = 0;

		if (this.getFilaInicial() == c.getFilaInicial())
			val = this.getColInicial() - c.getColInicial();
		else if (this.getFilaInicial() > c.getFilaInicial())
			val = 1;
		else
			val = -1;

		return val;
	}

	@Override
	public int compare(Cluster c1, Cluster c2) {
		// Los clusters se ordenan teniendo en cuenta su fila inicial, columna inicial
		int val = 0;

		if (c1.getFilaInicial() == c2.getFilaInicial())
			val = c1.getColInicial() - c2.getColInicial();
		else if (c1.getFilaInicial() > c2.getFilaInicial())
			val = 1;
		else
			val = -1;

		return val;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Dimensiones: ").append(filas).append("X").append(columnas).append("\n");
		sb.append("Punto inicial: ").append(getPuntoInicial()).append("\n");
		sb.append("Punto final: ").append(getPuntoFinal()).append("\n");

		return sb.toString();
	}

}

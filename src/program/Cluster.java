package program;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Cluster implements Comparator<Cluster>, Comparable<Cluster> {

	// Los atributos que necesitamos de los clústers son:
	// Los límites que delimitan al cluster
	private ArrayList<Punto> limites;
	// Y sus dimensiones
	private int filas;
	private int columnas;

	/**
	 * Método para crear un clúster dadas la fila y la columna iniciales (esquina
	 * superior izda)
	 * 
	 * @param filas
	 * @param columnas
	 * @param fila_inicio
	 * @param fila_fin
	 * @param col_inicio
	 * @param col_fin
	 */
	public Cluster(int filas, int columnas, int fila_inicio, int col_inicio) {
		// Cogemos las dimensiones
		this.filas = filas;
		this.columnas = columnas;

		// Y a partir de las coordenadas de la casilla inicial, creamos la lista de los
		// puntos límite:
		this.limites = new ArrayList<>();
		// 1. Añadimos los límites superior e inferior
		for (int fila = fila_inicio; fila < (fila_inicio + filas); fila += (filas - 1))
			for (int col = col_inicio; col < (col_inicio + columnas); col++)
				limites.add(new Punto(fila, col));

		// 2. Añadimos los límites izda y derecha
		for (int col = col_inicio; col < (col_inicio + columnas); col += (columnas - 1))
			for (int fila = (fila_inicio + 1); fila < (fila_inicio + filas - 1); fila++)
				limites.add(new Punto(fila, col));

		// 3. Ordenamos los límites orden teniendo en cuenta primero la fila, después la
		// columna
		Collections.sort(limites);

	}

	/**
	 * Método para crear un clúster dado un punto inicial (esquina superior izda)
	 * 
	 * @param filas
	 * @param columnas
	 * @param p_inicial
	 */
	public Cluster(int filas, int columnas, Punto p_inicial) {
		this(filas, columnas, p_inicial.getFila(), p_inicial.getCol());
	}

	/**
	 * Método para crear un clúster si ya tienes todos los datos
	 * 
	 * @param filas
	 * @param columnas
	 * @param limites
	 */
	public Cluster(int filas, int columnas, ArrayList<Punto> limites) {
		this.filas = filas;
		this.columnas = columnas;
		this.limites = limites;
	}

	/**
	 * Método que devuelve los clusters adyacentes dadas las dimensiones del mapa
	 *
	 * @param filas_mapa
	 * @param cols_mapa
	 * @return
	 */
	public ArrayList<Cluster> getAdyacentesMapa(int filas_mapa, int cols_mapa) {
		ArrayList<Cluster> adyacentes = new ArrayList<>();

		// Comprobamos primero en vertical
		// Comprobamos para añadir la de la fila superior
		if (getFilaInicial() != 0)
			adyacentes.add(new Cluster(filas, columnas, (getFilaInicial() - filas), getColInicial()));
		// Comprobamos para añadir la de la fila inferior
		if (getFilaFinal() != (filas_mapa - 1))
			adyacentes.add(new Cluster(filas, columnas, (getFilaInicial() + filas), getColInicial()));
		// Comprobamos ahora en horizontal
		// Comprobamos para añadir la de la izda
		if (getColInicial() != 0)
			adyacentes.add(new Cluster(filas, columnas, getFilaInicial(), (getColInicial() - columnas)));
		// Comprobamos para añadir la de la derecha
		if (getColFinal() != (cols_mapa - 1))
			adyacentes.add(new Cluster(filas, columnas, getFilaInicial(), (getColInicial() + columnas)));

		// Ordenamos
		Collections.sort(adyacentes);

		return adyacentes;
	}

	/**
	 * Método que devuelve los clusters adyacentes, dada una lista de clusters
	 * definida y completa
	 * 
	 * @param clusters Lista completa de clusters (incluye a este)
	 * @return
	 */
	public ArrayList<Cluster> getAdyacentes(ArrayList<Cluster> clusters) {
		ArrayList<Cluster> adyacentes = new ArrayList<>();

		// Cogemos el índice de este elemento
		int index = clusters.indexOf(this);

		// Los adyacentes se agrupan en 2 tipos:
		// 1. La de las columnas adyacentes
		// Comprobamos para añadir la de la izda
		if (index % columnas != 0)
			adyacentes.add(clusters.get(index - 1));
		// Comprobamos para añadir la de la derecha
		if ((index + 1) % columnas != 0)
			adyacentes.add(clusters.get(index + 1));
		// 2. La de las filas adyacentes
		// Comprobamos para añadir por arriba
		if (index >= columnas)
			adyacentes.add(clusters.get(index - columnas));
		// Comprobamos para añadir por abajo
		if ((index + columnas) < (filas * columnas))
			adyacentes.add(clusters.get(index + columnas));
		// Finalmente, ordenamos
		Collections.sort(adyacentes);

		return adyacentes;
	}

	/**
	 * Método para averiguar si otro cluster es adyacente a este dado también el
	 * mapa que los engloba
	 * 
	 * @param cluster
	 * @return
	 */
	public boolean adyacentes(Cluster cluster, Mapa mapa) {
		// Suponemos que todos los cluster que se comparan deben ser distintos y tener
		// el mismo tamaño
		if (this.filas != cluster.filas || this.columnas != cluster.columnas || this.equals(cluster))
			throw new RuntimeException(
					"Todos los clusters deben tener el mismo tamaño y han de ser distintos entre sí.");

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
	 * Devuelve la primera casilla del cluster
	 * 
	 * @return
	 */
	public Punto getPuntoInicial() {
		return limites.get(0);
	}

	/**
	 * Devuelve la última casilla del cluster
	 * 
	 * @return
	 */
	public Punto getPuntoFinal() {
		return limites.get((limites.size() - 1));
	}

	/**
	 * Devuelve la fila inicial del cluster
	 * 
	 * @return
	 */
	public int getFilaInicial() {
		return getPuntoInicial().getFila();
	}

	/**
	 * Devuelve la fila final del cluster
	 * 
	 * @return
	 */
	public int getFilaFinal() {
		return getPuntoFinal().getFila();
	}

	/**
	 * Devuelve la columna inicial del cluster
	 * 
	 * @return
	 */
	public int getColInicial() {
		return getPuntoInicial().getCol();
	}

	/**
	 * Devuelve la columna final del cluster
	 * 
	 * @return
	 */
	public int getColFinal() {
		return getPuntoFinal().getCol();
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
		return this.getPuntoInicial().hashCode();
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

}

package program;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;

// Clase para definir un punto dado por (fila, columna)
public class Punto implements Cloneable, Comparable<Punto>, Comparator<Punto> {

	// Coordenadas del punto (fila y columna)
	private int f;
	private int c;
	// Constante para el coste en diagonal
	protected static final Integer DIAGONAL = 141;

	// Arcos
	// Arcos externos (puede haber 2 si coincide con una esquina)
	private ArrayList<Punto> interedges;
	// Arcos internos (es una lista de puntos que puede ser vacía)
	private ArrayList<Edge> intraedges;

	/**
	 * Método para crear el punto, dadas sus coordenadas (fila, columna)
	 * 
	 * @param f
	 * @param c
	 */
	public Punto(int f, int c) {
		this.f = f;
		this.c = c;

		interedges = new ArrayList<>();
		intraedges = new ArrayList<>();
	}

	/**
	 * Cambia las coordenadas del punto
	 * 
	 * @param f fila
	 * @param c columna
	 */
	public void setPunto(int f, int c) {
		this.f = f;
		this.c = c;
	}

	/**
	 * Devuelve la fila
	 * 
	 * @return
	 */
	public int getFila() {
		return f;
	}

	/**
	 * Devuelve la columna
	 * 
	 * @return
	 */
	public int getCol() {
		return c;
	}

	/**
	 * Te genera un pto a partir de un String Formato: n1,n2
	 * 
	 * @param s
	 */
	public Punto(String s) {
		try (Scanner sc = new Scanner(s)) {
			sc.useDelimiter("\s*,\s*");
			int f = sc.nextInt();
			int c = sc.nextInt();
			this.c = c;
			this.f = f;
			this.interedges = new ArrayList<>();
			this.intraedges = new ArrayList<>();
		} catch (NumberFormatException e) {
			e.getStackTrace();
		} catch (Exception e) {
			e.getStackTrace();
		}

	}

	/**
	 * Calcula los vecinos dadas las dimensiones del mapa (para la opción de
	 * 4-vecinos)
	 * 
	 * @param filas
	 * @param columnas
	 * @return
	 */
	public ArrayList<Punto> vecinos(int filas, int columnas) {
		ArrayList<Punto> res = new ArrayList<>();

		int arriba = this.f - 1;
		int abajo = this.f + 1;
		int derecha = this.c + 1;
		int izda = this.c - 1;

		// Vamos a seguir el orden de los puntos:

		// Primero, añadimos al de arriba
		if (arriba >= 0)
			res.add(new Punto(arriba, c));
		// Segundo, al de la izda
		if (izda >= 0)
			res.add(new Punto(f, izda));
		// Tercero, al de la derecha
		if (derecha < columnas)
			res.add(new Punto(f, derecha));
		// Por último, al de abajo
		if (abajo < filas)
			res.add(new Punto(abajo, c));

		return res;
	}

	/**
	 * Calcula los vecinos dadas las dimensiones del mapa (para la opción de
	 * 8-vecinos)
	 * 
	 * @param filas
	 * @param columnas
	 * @return
	 */
	public ArrayList<Punto> vecinos_8(int filas, int columnas) {
		ArrayList<Punto> res = new ArrayList<>();

		int arriba = this.f - 1;
		int abajo = this.f + 1;
		int izda = this.c - 1;
		int derecha = this.c + 1;

		// Vamos a introducir los datos en el orden de los puntos
		// Primero, arriba izda, luego arriba y por último arriba derecha
		if (arriba >= 0) {
			// Arriba izda
			if (izda >= 0)
				res.add(new Punto(arriba, izda));

			// Arriba
			res.add(new Punto(arriba, c));
			// arriba derecha
			if (derecha < columnas)
				res.add(new Punto(arriba, derecha));

		}
		// Ahora izquierda
		if (izda >= 0)
			res.add(new Punto(f, izda));

		// Ahora derecha
		if (derecha < columnas)
			res.add(new Punto(f, derecha));

		// Ahora abajo izda, abajo y abajo derecha
		if (abajo < filas) {
			// Abajo izda
			if (izda >= 0)
				res.add(new Punto(abajo, izda));

			// Abajo
			res.add(new Punto(abajo, c));

			// Abajo derecha
			if (derecha < columnas)
				res.add(new Punto(abajo, derecha));
		}

		// Como hemos añadido los puntos de forma ordenada, no ordenamos la lista
		return res;
	}

	/**
	 * Método que te indica si otro punto es adyacente
	 * 
	 * @param p
	 * @return
	 */
	public boolean adyacente(Punto p) {
		return (this.c == p.c && (this.f == (p.f + 1) || this.f == (p.f - 1)))
				|| (this.f == p.f && (this.c == (p.c + 1) || this.c == (p.c - 1)));
	}

	/**
	 * Calcula el la distancia Manhattan con respecto a otro punto
	 * 
	 * @param p
	 * @return
	 */
	public int distManhattan(Punto p) {
		return (Math.abs(this.f - p.f) + Math.abs(this.c - p.c)) * 100;
	}

	/**
	 * Calcula la distancia octil con respecto a otro punto
	 * 
	 * @param p
	 * @return
	 */
	public int distOctil(Punto p) {
		int res = 0;

		int df = Math.abs(this.f - p.f);
		int dc = Math.abs(this.c - p.c);

		// Si hay más filas que columnas
		if (df > dc)
			res = DIAGONAL * dc + 100 * (df - dc);
		// Si coincide el número de filas y de columnas
		else if (df == dc)
			res = DIAGONAL * df;
		// Si hay menos filas que columnas
		else
			res = DIAGONAL * df + 100 * (dc - df);

		return res;
	}

	/**
	 * Método para añadir un arco externo (afecta tanto a este punto como al que se
	 * le pasa como parámetro)
	 * 
	 * @param p
	 */
	public void addArcoExterno(Punto p) {
		// Comprueba que son adyacentes y que no está en la lista; en caso contrario, no
		// lo añade
		if (adyacente(p) && !interedges.contains(p)) {
			// Añade el arco en ambos puntos
			interedges.add(p);
			p.addArcoExterno(this);
			// Ordena ambas listas
			Collections.sort(interedges);
		}
	}

	/**
	 * Método para obtener los arcos externos del punto
	 * 
	 * @return
	 */
	public ArrayList<Punto> getArcosExternos() {
		return interedges;
	}

	/**
	 * Método para añadir un arco interno
	 * 
	 * @param p
	 */
	public void addArcoInterno(Edge edge) {
		if (!intraedges.contains(edge))
			intraedges.add(edge);
	}

	/**
	 * Método para obtener los arcos internos del punto
	 * 
	 * @return
	 */
	public ArrayList<Edge> getArcosInternos() {
		return intraedges;
	}

	/**
	 * Devuelve el arco externo (se usa para depurar código)
	 */
	public String toStringEdges() {
		StringBuilder sb = new StringBuilder();

		int tam = interedges.size();
		sb.append("El punto " + this.toString() + " tiene " + tam);
		if (tam == 1)
			sb.append(" arco externo.\n");
		else
			sb.append(" arcos externos.\n");

		sb.append(this.toString()).append(" -> ");
		if (interedges.isEmpty())
			sb.append("[]");
		else {
			Iterator<Punto> it = interedges.iterator();
			while (it.hasNext()) {
				sb.append(it.next());
				if (it.hasNext())
					sb.append("\n\t-> ");
				else
					break;
			}

		}

		return sb.toString();
	}

	/**
	 * Te indica si otro punto se encuentra en la misma fila
	 * 
	 * @param p
	 * @return
	 */
	public boolean sameRow(Punto p) {
		return p.f == f;
	}

	/**
	 * Te indica si otro punto se encuentra en la misma columna
	 * 
	 * @param p
	 * @return
	 */
	public boolean sameColumn(Punto p) {
		return p.c == c;
	}

	@Override
	public String toString() {
		return "(" + f + ", " + c + ")";
	}

	@Override
	public Punto clone() {
		return new Punto(f, c);
	}

	@Override
	public int hashCode() {
		return Objects.hash(c, f);
	}

	@Override
	public boolean equals(Object o) {
		// Para que dos puntos sean iguales, ha de coincidir su fila y su columna
		return o instanceof Punto ? (this.f == ((Punto) o).f && this.c == ((Punto) o).c) : false;
	}

	@Override
	public int compareTo(Punto p) {
		// Los puntos van ordenados en primer lugar teniendo en cuenta su fila, y
		// después su columna
		int val = 0;

		if (this.getFila() == p.getFila())
			val = this.getCol() - p.getCol();
		else if (this.getFila() > p.getFila())
			val = 1;
		else
			val = -1;

		return val;
	}

	@Override
	public int compare(Punto p1, Punto p2) {
		// Los puntos van ordenados en primer lugar teniendo en cuenta su fila, y
		// después su columna
		int val = 0;

		if (p1.getFila() == p2.getFila())
			val = p1.getCol() - p2.getCol();
		else if (p1.getFila() > p2.getFila())
			val = 1;
		else
			val = -1;

		return val;
	}

}

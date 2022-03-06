package program;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;

// Clase para definir un punto dado por (fila, columna)
public class Punto implements Cloneable, Comparable<Punto>, Comparator<Punto> {

	// Coordenadas del punto (fila y columna)
	private int f;
	private int c;
	// Coste del punto (por defecto es 0)
	protected double coste;
	// Punto padre (por defecto es null)
	protected Punto padre;
	// Constante para el coste en diagonal
	protected static final Double DIAGONAL = Math.sqrt(2);

	// Arcos
	// Arcos externos (puede haber 2 si coincide con una esquina)
	private ArrayList<Punto> edges;
	// Arcos internos (es una lista de puntos que puede ser vacía)
	private ArrayList<Punto> intraedges;

	/**
	 * Método para crear el punto, dadas sus coordenadas (fila, columna)
	 * 
	 * @param f
	 * @param c
	 */
	public Punto(int f, int c) {
		this.f = f;
		this.c = c;

		coste = 0;
		padre = null;
		edges = new ArrayList<>();
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
			res.add(crearHijo(arriba, this.c, 1));
		// Segundo, al de la izda
		if (izda >= 0)
			res.add(crearHijo(this.f, izda, 1));
		// Tercero, al de la derecha
		if (derecha < columnas)
			res.add(crearHijo(this.f, derecha, 1));
		// Por último, al de abajo
		if (abajo < filas)
			res.add(crearHijo(abajo, this.c, 1));

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
				res.add(crearHijo(arriba, izda, DIAGONAL));

			// Arriba
			res.add(crearHijo(arriba, this.c, 1));
			// arriba derecha
			if (derecha < columnas)
				res.add(crearHijo(arriba, derecha, DIAGONAL));

		}
		// Ahora izquierda
		if (izda >= 0)
			res.add(crearHijo(this.f, izda, 1));

		// Ahora derecha
		if (derecha < columnas)
			res.add(crearHijo(this.f, derecha, 1));

		// Ahora abajo izda, abajo y abajo derecha
		if (abajo < filas) {
			// Abajo izda
			if (izda >= 0)
				res.add(crearHijo(abajo, izda, DIAGONAL));

			// Abajo
			res.add(crearHijo(abajo, this.c, 1));

			// Abajo derecha
			if (derecha < columnas)
				res.add(crearHijo(abajo, derecha, DIAGONAL));
		}

		// Como hemos añadido los puntos de forma ordenada, no ordenamos la lista
		return res;
	}

	/**
	 * Método para crear un hijo de este punto, teniendo en cuenta la fila, la
	 * columna y el coste a sumar
	 * 
	 * @param fila
	 * @param col
	 * @param extra
	 * @return
	 */
	private Punto crearHijo(int fila, int col, double extra) {
		Punto p = new Punto(fila, col);
		p.padre = this;
		p.coste = this.coste + extra;

		return p;
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
		return Math.abs(this.f - p.f) + Math.abs(this.c - p.c);
	}

	/**
	 * Calcula la distancia octil con respecto a otro punto
	 * 
	 * @param p
	 * @return
	 */
	public double distOctil(Punto p) {
		double res = 0;

		int df = Math.abs(this.f - p.f);
		int dc = Math.abs(this.c - p.c);

		// Si hay más filas que columnas
		if (df > dc)
			res = Math.sqrt(2) * dc + (df - dc);
		// Si coincide el número de filas y de columnas
		else if (df == dc)
			res = Math.sqrt(2) * df;
		// Si hay menos filas que columnas
		else
			res = Math.sqrt(2) * df + (dc - df);

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
		if (adyacente(p) && !edges.contains(p)) {
			// Añade el arco en ambos puntos
			edges.add(p);
			p.addArcoExterno(this);
			// Ordena ambas listas
			Collections.sort(edges);
		}
	}

	/**
	 * Método para obtener el arco externo que hay de un punto a otro
	 * 
	 * @return
	 */
	public ArrayList<Punto> getArcosExternos() {
		return edges;
	}

	/**
	 * Método para añadir un arco interno
	 * 
	 * @param p
	 */
	public void addArcoInterno(Punto p) {
		intraedges.add(p);
		p.intraedges.add(this);
	}

	/**
	 * Devuelve el arco externo (se usa para depurar código)
	 */
	public String toStringEdges() {
		StringBuilder sb = new StringBuilder();

		int tam = edges.size();
		sb.append("El punto " + this.toString() + " tiene " + tam);
		if (tam == 1)
			sb.append(" arco externo.\n");
		else
			sb.append(" arcos externos.\n");

		sb.append(this.toString()).append(" -> ");
		if (edges.isEmpty())
			sb.append("[]");
		else {
			Iterator<Punto> it = edges.iterator();
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

	@Override
	public String toString() {
		return "(" + f + ", " + c + ")";
	}

	@Override
	public boolean equals(Object o) {
		// Para que dos puntos sean iguales, ha de coincidir su fila y su columna
		return o instanceof Punto ? (this.f == ((Punto) o).f && this.c == ((Punto) o).c) : false;
	}

	@Override
	public int hashCode() {
		return f + c;
	}

	@Override
	public Punto clone() {
		return new Punto(this.f, this.c);
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

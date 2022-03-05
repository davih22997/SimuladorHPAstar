package program;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

// Clase para definir un punto dado por (fila, columna)
public class Punto implements Cloneable, Comparable<Punto>, Comparator<Punto> {

	// Coordenadas del punto (fila y columna)
	private int f;
	private int c;
	// Coste del punto
	double coste = 0;
	// Punto padre (por defecto es null)
	Punto padre = null;
	// Constante para el coste en diagonal
	static final Double DIAGONAL = Math.sqrt(2);

	/**
	 * Método para crear el punto, dadas sus coordenadas (fila, columna)
	 * 
	 * @param f
	 * @param c
	 */
	public Punto(int f, int c) {
		this.f = f;
		this.c = c;
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

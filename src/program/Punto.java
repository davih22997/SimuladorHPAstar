package program;

import java.util.ArrayList;
import java.util.Scanner;

// Clase para definir un punto dado por (fila, columna)
public class Punto implements Cloneable {

	private int f;
	private int c;
	int pasos = 0;
	Punto padre = null;

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
	 * Calcula los vecinos dadas las dimensiones del mapa
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

		// Primero, añadimos al de arriba
		if (arriba >= 0)
			res.add(new Punto(arriba, this.c));
		// Segundo, al de abajo
		if (abajo < filas)
			res.add(new Punto(abajo, this.c));
		// Tercero, al de la derecha
		if (derecha < columnas)
			res.add(new Punto(this.f, derecha));
		// Por último, al de la izda
		if (izda >= 0)
			res.add(new Punto(this.f, izda));

		for (Punto p : res) {
			p.padre = this;
			p.pasos = this.pasos + 1;
		}

		return res;
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

	@Override
	public String toString() {
		return "(" + f + ", " + c + ")";
	}

	@Override
	public boolean equals(Object o) {

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

}

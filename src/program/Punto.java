package program;

import java.util.Scanner;

// Clase para definir un punto dado por (fila, columna)
public class Punto implements Cloneable {

	private int f;
	private int c;

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
	 * Te genera un pto a partir de un String
	 * 
	 * @param s
	 */
	public Punto(String s) {
		try (Scanner sc = new Scanner(s)) {
			sc.useDelimiter(", ");
			int f = sc.nextInt();
			int c = sc.nextInt();
			this.c = c;
			this.f = f;
		}

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

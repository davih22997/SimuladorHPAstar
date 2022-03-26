package program;

import java.util.Objects;

// En esta clase se tiene en cuenta el punto inicial y el final (para representar un arco)
public class Arco {
	protected Punto p1;
	protected Punto p2;

	public Arco(Punto p1, Punto p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public int hashCode() {
		return Objects.hash(p1, p2);
	}

	@Override
	public boolean equals(Object o) {
		// Se comparan los dos puntos
		return o instanceof Arco ? Objects.equals(p1, ((Arco) o).p1) && Objects.equals(p2, ((Arco) o).p2) : false;
	}

	@Override
	public String toString() {
		return "(" + p1.toString() + ", " + p2.toString() + ")";
	}
}

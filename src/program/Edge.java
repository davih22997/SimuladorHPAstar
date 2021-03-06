package program;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

/**
 * Esta es la clase de los edges
 * 
 * @author david
 *
 */
public class Edge implements Cloneable, Comparable<Edge>, Comparator<Edge> {

	// Los puntos que se unen entre sí
	protected Punto pini;
	protected Punto pfin;

	// El coste entre un punto y otro
	protected int coste;

	// Camino (sucesión de puntos) que hay entre un punto y otro
	protected ArrayList<Punto> camino;

	public Edge() {
		pini = null;
		pfin = null;
		coste = 0;
		camino = new ArrayList<>();
	}

	/**
	 * Crea un interEdge (arco externo) entre un punto y otro
	 * 
	 * @param pini
	 * @param pfin
	 */
	private void interEdge(Punto pini, Punto pfin) {
		// Guarda punto inicial y final
		this.pini = pini;
		this.pfin = pfin;
		// El coste de llegar de un punto a otro será 1
		coste = 100;
		// El camino será los dos puntos
		camino.add(pini);
		camino.add(pfin);
	}

	/**
	 * Crea un intraedge (arco interno) una vez tenemos todos los datos
	 * 
	 * @param pini
	 * @param pfin
	 * @param camino
	 * @param coste
	 */
	public void intraEdge(Punto pini, Punto pfin, ArrayList<Punto> camino, int coste) {
		this.pini = pini;
		this.pfin = pfin;
		this.camino = camino;
		this.coste = coste;
	}

	/**
	 * Crea el edge (intraedge o interedge) simétrico.
	 * 
	 * @return
	 */
	public Edge symm() {
		Edge e = new Edge();
		e.pfin = pini;
		e.pini = pfin;
		e.coste = coste;

		for (int i = camino.size() - 1; i >= 0; i--) {
			e.camino.add(camino.get(i));
		}

		return e;
	}

	/**
	 * Método para imprimir edge (se va a usar como debug)
	 */
	protected void imprimirEdge() {
		StringBuilder sb = new StringBuilder();
		sb.append("Arco de " + this.pini + " hasta " + this.pfin + ":\n");
		sb.append("Coste: " + this.coste + "\n");
		sb.append("Camino: ");

		if (!camino.isEmpty()) {
			Iterator<Punto> iter = camino.iterator();
			Punto p = iter.next();
			sb.append(p);
			if (iter.hasNext())
				sb.append(" -> ");

			while (iter.hasNext()) {
				p = iter.next();
				sb.append(p);
				if (iter.hasNext())
					sb.append(" -> ");
			}

		}

		System.out.println(sb.toString());
	}

	public void intraEdge() {

	}

	@Override
	public int hashCode() {
		return Objects.hash(camino, coste, pfin, pini);
	}

	@Override
	public boolean equals(Object o) {

		boolean res = o instanceof Edge;

		// Si es otro edge
		if (res) {
			Edge e = (Edge) o;

			// Primero, comparamos el coste y el tamaño del camino
			res = e.coste == coste && e.camino.size() == camino.size();

			// Si los costes y la longitud del camino coinciden
			if (res) {

				// Si coinciden los puntos inicial y final
				if (e.pini.equals(pini) && e.pfin.equals(pfin))
					for (int i = 0; res && i < camino.size(); i++)
						res = e.camino.get(i).equals(camino.get(i));

				// Si no, comprobamos que coincida con el simétrico
				else {
					Edge sym = symm();
					res = e.pini.equals(sym.pini) && e.pfin.equals(sym.pfin);

					for (int i = 0; res && i < sym.camino.size(); i++)
						res = e.camino.get(i).equals(sym.camino.get(i));
				}

			}

		}

		return res;
	}

	@Override
	public int compareTo(Edge e) {
		return this.pfin.compareTo(e.pfin);
	}

	@Override
	public int compare(Edge e1, Edge e2) {
		// Los puntos van ordenados en primer lugar teniendo en cuenta su fila, y
		// después su columna
		int val = 0;
		Punto p1 = e1.pfin;
		Punto p2 = e2.pfin;

		if (p1.getFila() == p2.getFila())
			val = p1.getCol() - p2.getCol();
		else if (p1.getFila() > p2.getFila())
			val = 1;
		else
			val = -1;

		return val;
	}

}

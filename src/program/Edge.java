package program;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Esta es la clase de los edges
 * 
 * @author david
 *
 */
public class Edge implements Cloneable {

	// Los puntos que se unen entre sí
	protected Punto pini;
	protected Punto pfin;

	// El coste entre un punto y otro
	protected double coste;

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
		coste = 1;
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
	public void intraEdge(Punto pini, Punto pfin, ArrayList<Punto> camino, double coste) {
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
		e.pfin = pini.clone();
		e.pini = pfin.clone();
		e.coste = coste;

		e.camino = (ArrayList<Punto>) camino.clone();
		Collections.reverse(e.camino);

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

	@Override
	public boolean equals(Object o) {
		boolean res = o instanceof Edge;

		// Si es otro edge
		if (res) {
			Edge e = (Edge) o;

			// Primero, comparamos el coste
			res = e.coste == coste; // && e.camino.size() == camino.size();

			// Ahora, vemos si los caminos son null

			if (res)
				res = e.camino.size() == camino.size();

			// Si los costes y la longitud del camino coinciden
			if (res) {
				res = e.pini.equals(pini) && e.pfin.equals(pfin);

				// Si coinciden los puntos inicial y final
				if (res)
					for (int i = 0; res && i < camino.size(); i++)
						res = e.camino.get(i).equals(camino.get(i));

				// Si no, comprobamos que coincida con el simétrico
				else {
					Edge sym = symm();

					res = e.pini.equals(sym.pini) && e.pfin.equals(sym.pfin);

					for (int i = 0; res && i < sym.camino.size(); i++)
						res = e.camino.get(i).equals(camino.get(i));
				}

			}

		}

		return res;
	}

	@Override
	public int hashCode() {
		return pini.hashCode() + pfin.hashCode() + camino.size();
	}

	public void intraEdge() {

	}

}

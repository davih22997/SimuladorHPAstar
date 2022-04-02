package program;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Esta clase se utilizará para la creación de intraedges
 * 
 * @author david
 *
 */
public class Dijkstra {

	/**
	 * Clase que contiene las iteraciones, el punto, su coste y los datos del paso
	 * anterior
	 * 
	 * @author david
	 *
	 */
	static class Datos {
		int iteraciones;
		double coste;
		Punto p;
		Datos p_anterior;

		/**
		 * Crea los datos a partir del número de iteraciones, el coste y un punto
		 * 
		 * @param iter
		 * @param coste
		 * @param p
		 */
		public Datos(int iter, double coste, Punto p) {
			this(iter, coste, p, null);
		}

		/**
		 * Crea los datos a partir del número de iteraciones, el coste, un punto y los
		 * datos anteriores
		 * 
		 * @param iter
		 * @param coste
		 * @param p
		 * @param anterior
		 */
		public Datos(int iter, double coste, Punto p, Datos anterior) {
			iteraciones = iter;
			this.p = p;
			this.coste = coste;
			p_anterior = anterior;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Datos ? ((Datos) obj).p.equals(this.p) : false;
		}

		@Override
		public int hashCode() {
			return this.p.hashCode();
		}

		/**
		 * Crea la lista de vecinos teniendo el submapa
		 * 
		 * @param submapa
		 * @return
		 */
		public ArrayList<Datos> vecinosCluster(ArrayList<Punto> submapa) {
			// Creamos la lista a devolver
			ArrayList<Datos> res = new ArrayList<>();

			// Obtenemos las distintas coordenadas
			int arriba = this.p.getFila() - 1;
			int abajo = this.p.getFila() + 1;
			int izda = this.p.getCol() - 1;
			int derecha = this.p.getCol() + 1;

			// Vamos comprobando en orden de puntos (izda arriba, arriba, derecha arriba,
			// izda, derecha, izdaabajo, abajo, derecha abajo)
			// Arriba izda
			getPuntoLista(this.iteraciones + 1, coste + Punto.DIAGONAL, new Punto(arriba, izda), submapa, res);
			// Arriba
			getPuntoLista(this.iteraciones + 1, coste + 1, new Punto(arriba, this.p.getCol()), submapa, res);
			// Arriba derecha
			getPuntoLista(this.iteraciones + 1, coste + Punto.DIAGONAL, new Punto(arriba, derecha), submapa, res);
			// Izda
			getPuntoLista(this.iteraciones + 1, coste + 1, new Punto(this.p.getFila(), izda), submapa, res);
			// Derecha
			getPuntoLista(this.iteraciones + 1, coste + 1, new Punto(this.p.getFila(), derecha), submapa, res);
			// Abajo izda
			getPuntoLista(this.iteraciones + 1, coste + Punto.DIAGONAL, new Punto(abajo, izda), submapa, res);
			// Abajo
			getPuntoLista(this.iteraciones + 1, coste + 1, new Punto(abajo, this.p.getCol()), submapa, res);
			// Abajo derecha
			getPuntoLista(this.iteraciones + 1, coste + Punto.DIAGONAL, new Punto(abajo, derecha), submapa, res);

			// Devolvemos la lista (como hemos agregado los puntos de forma ordenada, no
			// necesitamos ordenarla)
			return res;
		}

		/**
		 * Obtiene los datos de un punto y si no es null lo añade a la lista de puntos
		 * 
		 * @param iter
		 * @param coste
		 * @param p
		 * @param lista
		 * @param ptos
		 * @return
		 */
		public void getPuntoLista(int iter, double coste, Punto p, ArrayList<Punto> lista, ArrayList<Datos> ptos) {
			if (lista.contains(p)) {
				int index = lista.indexOf(p);
				p = lista.get(index);
				ptos.add(new Datos(iter, coste, p, this));
			}
		}

	}

	/**
	 * Crea el intraedge (arco interno) entre dos puntos, dado el submapa
	 * 
	 * 
	 * @param p1
	 * @param p2
	 * @param submapa
	 * @return
	 */
	public static Edge intraedge(Punto p1, Punto p2, ArrayList<Punto> submapa) {

		// Creamos la lista de puntos ya analizados
		ArrayList<Datos> cerrados = new ArrayList<>();

		// Y la lista de puntos por analizar
		PriorityQueue<Datos> abiertos = new PriorityQueue<>(new Comparator<Datos>() {
			@Override
			public int compare(Datos p1, Datos p2) {
				// Primero, tenemos en cuenta el que menos coste tenga:
				double c1 = p1.coste;
				double c2 = p2.coste;

				int solucion = 0;
				if (c1 > c2)
					solucion = 1;
				else if (c1 < c2)
					solucion = -1;

				// Luego, tenemos en cuenta el que más pasos ha dado
				else {
					int it1 = p1.iteraciones;
					int it2 = p2.iteraciones;
					if (it1 > it2)
						solucion = -1;
					else if (it1 < it2)
						solucion = 1;
				}

				return solucion;
			}
		});

		// Añadimos el punto inicial
		Datos par = new Datos(0, 0, p1);

		abiertos.add(par);

		// Vamos analizando mientras tengamos puntos por analizar y no haya llegado al
		// punto final
		while (!abiertos.isEmpty() && !abiertos.peek().p.equals(p2)) {
			// Cogemos el punto que tenga menor coste entre los que hay
			Datos actual = abiertos.poll();

			// Lo añadimos a la lista de analizados
			cerrados.add(actual);

			// Cogemos los vecinos del punto (no contiene los obstáculos del mapa)
			ArrayList<Datos> vecinos = actual.vecinosCluster(submapa);
			// Eliminamos los puntos ya analizados
			vecinos.removeAll(cerrados);

			for (Datos p : vecinos) {
				// Si el punto no está en la lista de abiertos
				if ((!abiertos.contains(p)))
					// Lo añadimos
					abiertos.add(p);
				// Si está en la lista comprobamos que tenga menor coste
				else {
					Iterator<Datos> it = abiertos.iterator();
					Datos par2 = it.next();

					while (!par2.equals(p) && it.hasNext())
						par2 = it.next();

					// Si tiene menor coste, quitamos el anterior y ponemos este
					if (p.coste < par2.coste) {
						abiertos.remove(par2);
						abiertos.add(p);
					} 
				}
			}
		}

		Edge edge = new Edge();
		// Si encuentra el punto objetivo, añadimos los datos de todo lo recorrido
		if (!abiertos.isEmpty() && abiertos.peek().p.equals(p2)) {
			Datos pto = abiertos.poll();
			ArrayList<Punto> camino = crearCamino(pto);
			edge.intraEdge(p1, p2, camino, pto.coste);
		} // En caso contrario, añade un camino vacío con coste infinito
		else
			edge.intraEdge(p1, p2, new ArrayList<>(), Double.MAX_VALUE);

		return edge;
	}

	/**
	 * Método para obtener el camino
	 * 
	 * @param p
	 * @return
	 */
	private static ArrayList<Punto> crearCamino(Datos p) {
		ArrayList<Punto> camino = new ArrayList<>();

		while (p != null) {
			camino.add(p.p);
			p = p.p_anterior;
		}

		// Obtenemos el camino desde el final hasta el inicial, luego le damos la
		// vuelta, ya que el camino va desde el punto inicial hasta el final
		Collections.reverse(camino);

		return camino;
	}

}

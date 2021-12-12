package program;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
import java.awt.Color;

public class Astar {

	int memoria = 0;
	int iteraciones = 0;
	ArrayList<Punto> recorrido;
	

	public class Par<P, C> {
		P p;
		C c;

		public Par(P p, C c) {
			this.p = p;
			this.c = c;
		}

		@Override
		public boolean equals(Object o) {
			boolean res = false;

			if (o instanceof Par) {
				res = this.p.equals(((Par) o).p) && this.c.equals(((Par) o).c);
			}

			return res;
		}
	}

	// private int coste;
	// private Punto pto_inicial;
	// private ArrayList<Punto> nodosVisitados;
	private int mapa[][]; // Mapa con los nodos

	public Astar() {
		mapa = new int[Mapa.dY][Mapa.dX]; // Mapa donde se van guardando los costes
	}

	public void calcularCamino(int filas, int columnas, Punto pto_inicial, Punto pto_final,
			ArrayList<Punto> obstaculos) {
		Stack<Par<Punto, Integer>> camino = new Stack<>();
		Stack<Par<Punto, Integer>> candidatos = new Stack<>();
		ArrayList<Punto> visitados = new ArrayList<>();
		// Nodo sel -> Cabecera, parte de arriba de la pila
		// Lista de nodos -> Ver cómo (además hay que acumular los costes de forma que
		// sea coherente
		// Visitados -> ArrayList

		Punto actual = pto_inicial;
		memoria = 1;
		int pasos = 0;
		// Insertamos el coste en el mapa de los costes
		camino.add(new Par<Punto, Integer>(actual, actual.distManhattan(pto_final)));
		visitados.add(actual);

		while (!actual.equals(pto_final) && !camino.empty()) {
			// Cogemos los vecinos del punto
			ArrayList<Punto> vecinos = actual.vecinos(Mapa.dY, Mapa.dX);

			// Nunca se van a mirar los obstáculos ni los visitados
			vecinos.removeAll(obstaculos);
			vecinos.removeAll(visitados);

			// Sumamos la cantidad de memoria usada
			memoria += vecinos.size();

			Iterator<Punto> iter = vecinos.iterator();

			pasos++;
			Punto next = null;
			if (iter.hasNext()) {
				next = iter.next();
				int coste = pasos + next.distManhattan(pto_final);
				candidatos.add(new Par<Punto, Integer>(next, coste));
				mapa[next.getFila()][next.getCol()] = coste;
				if (!next.equals(pto_final)) {
					Mapa.MatrizBotones[next.getFila()][next.getCol()].setBackground(Color.CYAN);
					while (iter.hasNext()) {
						Punto aux = iter.next();
						int coste2 = pasos + aux.distManhattan(pto_final);
						candidatos.add(new Par<Punto, Integer>(aux, coste2));
						mapa[next.getFila()][next.getCol()] = coste2;
						
						if (!aux.equals(pto_final))
							Mapa.MatrizBotones[aux.getFila()][aux.getCol()].setBackground(Color.CYAN);
						
						if (coste2 < coste) {
							next = aux;
							coste = coste2;
						}
					}

				}

				candidatos.sort(new Comparator<Par<Punto, Integer>>() {
					@Override
					public int compare(Par<Punto, Integer> par1, Par<Punto, Integer> par2) {
						return par1.c - par2.c;
					}
				});

				if (camino.peek().c == coste) {
					Par nuevo = new Par<Punto, Integer>(next, coste);
					camino.add(nuevo);
					candidatos.remove(nuevo);
					visitados.add(next);
					actual = next;
				} else {
					Par<Punto, Integer> parsig = candidatos.pop();
					
					Par<Punto, Integer> cima = camino.peek();
					
					if (cima.p.vecinos(filas, columnas).contains(parsig.p)) {
						camino.add(parsig);
						
					} else {
						camino.pop();
						if (!camino.empty()) {
							actual = camino.peek().p;
						}
						pasos--;						
					}
							
				}

			} // Si no se encuentra un nodo a visitar, quitamos el actual de la cima de la
				// pila
			else {
				camino.pop();
				if (!camino.empty()) {
					actual = camino.peek().p;
					pasos--;
				}

			}

		}

		iteraciones = visitados.size() - 1;
		recorrido = new ArrayList<>();
		for (Punto p : visitados) {
			if (!p.equals(pto_inicial) && !p.equals(pto_final))
				Mapa.MatrizBotones[p.getFila()][p.getCol()].setBackground(Color.BLUE);
		}
		for (Par<Punto, Integer> par : camino) {
			Punto p = par.p;
			if (!p.equals(pto_inicial) && !p.equals(pto_final))
				Mapa.MatrizBotones[p.getFila()][p.getCol()].setBackground(Color.PINK);

			recorrido.add(p);
		}
		if (!recorrido.isEmpty())
			Collections.reverse(camino);

	}

}

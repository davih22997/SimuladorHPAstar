package program;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Astar {

	protected static int memoria = 1;
	protected static int iteraciones = 0;
	protected static boolean encontrada = false;

	public static void BusquedaAstar(int filas, int columnas, Punto pto_inicial, Punto pto_final,
			ArrayList<Punto> obstaculos) {
		memoria = 1;
		iteraciones = 0;
		encontrada = false;
		ArrayList<Punto> explorados = new ArrayList<>();

		// Se crea una lista de sucesores ordenada según el coste que tenga
		PriorityQueue<Punto> sucesores = new PriorityQueue<>(new Comparator<Punto>() {
			@Override
			public int compare(Punto p1, Punto p2) {
				int c1 = p1.pasos + p1.distManhattan(pto_final);
				int c2 = p2.pasos + p2.distManhattan(pto_final);

				int solucion = c1 - c2;

				if (solucion == 0) {
					solucion = p2.pasos - p1.pasos;
				}

				return solucion;
			}
		});

		// Inicialmente, añadimos el punto inicial
		sucesores.add(pto_inicial.clone());

		// Realizamos búsqueda hasta que no queden elementos o hasta que el elemento de
		// la cabeza de la lista de sucesores sea el pto final
		while ((!sucesores.isEmpty()) && !sucesores.peek().equals(pto_final)) {

			// El punto con el menor coste
			Punto actual = sucesores.poll();

			explorados.add(actual);
			// Pintamos el mapa según lo que vamos explorando (con excepción del pto_inicial
			// que se queda en verde
			if (!actual.equals(pto_inicial)) 
				Interfaz.mapa.MatrizBotones[actual.getFila()][actual.getCol()].setBackground(Color.BLUE);
			
			// Cogemos los vecinos del punto
			ArrayList<Punto> vecinos = actual.vecinos(filas, columnas);

			// Eliminamos los obstaculos
			vecinos.removeAll(obstaculos);

			// Comprobamos cada vecino del punto actual (sin contar obstaculos
			for (Punto p : vecinos) {
				// Si el punto vecino ha sido evaluado y tiene más pasos, pasamos.
				if (explorados.contains(p))
					continue;

				// Si el punto vecino no está en la cola o tiene menos pasos:
				else if ((!sucesores.contains(p))) {
					// Pintamos el mapa
					if (!p.equals(pto_inicial) && !p.equals(pto_final))
						Interfaz.mapa.MatrizBotones[p.getFila()][p.getCol()].setBackground(Color.CYAN);

					// Finalmente, lo añadimos
					sucesores.add(p);
					memoria++;
				}

			}

		}

		if (!sucesores.isEmpty() && sucesores.peek().equals(pto_final)) {
			Punto p = sucesores.poll();
			iteraciones = explorados.size();
			while (!p.padre.equals(pto_inicial)) {
				p = p.padre;
				Interfaz.mapa.MatrizBotones[p.getFila()][p.getCol()].setBackground(Color.PINK);
			}

			encontrada = true;

		}

	}

}

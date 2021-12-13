package program;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.Color;

public class Astar {

	private static int memoria = 0;
	private static int iteraciones = 0;

	public static void BusquedaAstar(int filas, int columnas, Punto pto_inicial, Punto pto_final,
			ArrayList<Punto> obstaculos) {
		ArrayList<Punto> explorados = new ArrayList<>();

		// Se crea una cola ordenada según el coste que tenga
		PriorityQueue<Punto> cola = new PriorityQueue<>(new Comparator<Punto>() {
			@Override
			public int compare(Punto p1, Punto p2) {
				return (p1.pasos + p1.distManhattan(pto_final)) - (p2.pasos + p2.distManhattan(pto_final));
			}
		});

		// Inicialmente, añadimos el punto inicial
		cola.add(pto_inicial.clone());

		// Realizamos búsqueda hasta que no queden elementos o hasta que el elemento de
		// la cabeza de la cola sea el pto final
		while ((!cola.isEmpty()) && !cola.peek().equals(pto_final)) {

			// El punto con el menor coste
			Punto actual = cola.poll();

			explorados.add(actual);
			if (!actual.equals(pto_inicial))
				Mapa.MatrizBotones[actual.getFila()][actual.getCol()].setBackground(Color.BLUE);

			// Cogemos los vecinos del punto
			ArrayList<Punto> vecinos = actual.vecinos(filas, columnas);

			// Eliminamos los obstaculos
			vecinos.removeAll(obstaculos);

			// Comprobamos cada vecino del punto actual (sin contar obstaculos
			for (Punto p : vecinos) {
				// Si el punto vecino ha sido evaluado y tiene más pasos, pasamos.
				if (explorados.contains(p) && explorados.get(explorados.indexOf(p)).pasos < p.pasos)
					continue;

				// Si el punto vecino no está en la cola o tiene menos pasos:
				else if ((!cola.contains(p))
						|| (explorados.contains(p) && p.pasos < explorados.get(explorados.indexOf(p)).pasos)) {

					if (!explorados.contains(p) && (!p.equals(pto_inicial) && !p.equals(pto_final)))
						Mapa.MatrizBotones[p.getFila()][p.getCol()].setBackground(Color.CYAN);

					// Eliminamos el anterior si la cola lo contiene
					if (cola.contains(p))
						cola.remove(p);

					cola.add(p);
				}

			}

		}

		if (!cola.isEmpty() && cola.peek().equals(pto_final)) {
			Punto p = cola.poll();
			iteraciones = p.pasos;
			while (!p.padre.equals(pto_inicial)) {
				p = p.padre;
				Mapa.MatrizBotones[p.getFila()][p.getCol()].setBackground(Color.PINK);
			}
			
			JOptionPane.showMessageDialog(new JFrame(), 
					"Se encontró solución.");
		} else 
			JOptionPane.showMessageDialog(new JFrame(),
					"No se encontró solución.");
		

	}

}

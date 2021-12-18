package program;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class Astar {

	private static String newline = "\n";

	protected static int memoria = 1;
	protected static int iteraciones = 0;
	protected static boolean encontrada = false;
	protected static Timer timer;

	public static void BusquedaAstar(Mapa mapa) {
		memoria = 1;
		iteraciones = 0;
		encontrada = false;
		ArrayList<Punto> explorados = new ArrayList<>();

		// Se crea una lista de sucesores ordenada según el coste que tenga
		PriorityQueue<Punto> sucesores = new PriorityQueue<>(new Comparator<Punto>() {
			@Override
			public int compare(Punto p1, Punto p2) {
				int c1 = p1.pasos + p1.distManhattan(mapa.pto_final);
				int c2 = p2.pasos + p2.distManhattan(mapa.pto_final);

				int solucion = c1 - c2;

				if (solucion == 0) {
					solucion = p2.pasos - p1.pasos;
				}

				return solucion;
			}
		});

		// Inicialmente, añadimos el punto inicial
		sucesores.add(mapa.pto_inicial.clone());

		timer = new Timer((int) (100 / (2 * Interfaz.v)), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				timer.setDelay((int) (100 / (2 * Interfaz.v)));

				if (Interfaz.btnStart.isEnabled())
					timer.stop();

				else if ((!sucesores.isEmpty()) && !sucesores.peek().equals(mapa.pto_final)) {
					// El punto con el menor coste
					Punto actual = sucesores.poll();

					explorados.add(actual);
					// Pintamos el mapa según lo que vamos explorando (con excepción del pto_inicial
					// que se queda en verde
					if (!actual.equals(mapa.pto_inicial)) {
						mapa.pintarMapa(Color.BLUE, 1000, actual.getFila(), actual.getCol());
						// Interfaz.mapa.MatrizBotones[actual.getFila()][actual.getCol()].setBackground(Color.BLUE);
					}

					// Cogemos los vecinos del punto
					ArrayList<Punto> vecinos = actual.vecinos(mapa.dY, mapa.dX);

					// Eliminamos los obstaculos
					vecinos.removeAll(mapa.obstaculos);
					vecinos.removeAll(explorados);
					vecinos.removeAll(sucesores);

					// Comprobamos cada vecino del punto actual (sin contar obstaculos
					for (Punto p : vecinos) {
						// Si el punto vecino ha sido evaluado y tiene más pasos, pasamos.
						if (explorados.contains(p))
							continue;

						// Si el punto vecino no está en la cola o tiene menos pasos:
						else if ((!sucesores.contains(p))) {
							// Pintamos el mapa
							if (!p.equals(mapa.pto_inicial) && !p.equals(mapa.pto_final)) {
								mapa.pintarMapa(Color.CYAN, 0, p.getFila(), p.getCol());
								// Interfaz.mapa.MatrizBotones[p.getFila()][p.getCol()].setBackground(Color.CYAN);
							}
							// Finalmente, lo añadimos
							sucesores.add(p);
							memoria++;
						}

					}

				}

				if (!Interfaz.btnStart.isEnabled() && !sucesores.isEmpty() && sucesores.peek().equals(mapa.pto_final)) {
					Punto p = sucesores.poll();
					iteraciones = explorados.size();
					while (!p.padre.equals(mapa.pto_inicial)) {
						p = p.padre;
						mapa.pintarMapa(Color.PINK, 0, p.getFila(), p.getCol());
						// Interfaz.mapa.MatrizBotones[p.getFila()][p.getCol()].setBackground(Color.PINK);
					}

					Interfaz.log.append("Memoria usada: " + Astar.memoria + "." + newline);
					Interfaz.log.append("Iteraciones: " + Astar.iteraciones + "." + newline);

					encontrada = true;
					timer.stop();
					JOptionPane.showMessageDialog(new JFrame(), "Se encontró solución.");
				}

				else if (!Interfaz.btnStart.isEnabled() && sucesores.isEmpty()) {
					JOptionPane.showMessageDialog(new JFrame(), "No se encontró solución.");
					timer.stop();
				}
			}

		});
		timer.start();

	}

}

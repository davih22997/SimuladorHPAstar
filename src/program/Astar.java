package program;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class Astar {

	//private static String newline = "\n";

	protected static int memoria = 0;
	protected static int iteraciones = 0;
	protected static Timer timer;

	public static void BusquedaAstar(Mapa mapa) {
		memoria = 1;
		iteraciones = 0;

		Interfaz.datosAstar
				.setText(new String("Memoria usada: ") + Astar.memoria + "    " + "Iteraciones: " + Astar.iteraciones);

		ArrayList<Punto> explorados = new ArrayList<>();

		// Se crea una lista de sucesores ordenada según el coste que tenga
		PriorityQueue<Punto> abiertos = new PriorityQueue<>(new Comparator<Punto>() {
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
		abiertos.add(mapa.pto_inicial.clone());

		// Y ejecutamos el algoritmo cada cierto tiempo (para mostrar paso a paso la
		// simulación)
		timer = new Timer((int) (250 / (2 * Interfaz.v)), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				timer.setDelay((int) (250 / (2 * Interfaz.v)));

				if (!Interfaz.btnStop.isEnabled())
					timer.stop();

				else if ((!abiertos.isEmpty()) && !abiertos.peek().equals(mapa.pto_final)) {
					// El punto con el menor coste
					Punto actual = abiertos.poll();

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
					// Eliminamos también los puntos ya analizados
					vecinos.removeAll(explorados);

					// Comprobamos cada vecino del punto actual (sin contar obstaculos)
					int cantMem = 0;
					for (Punto p : vecinos) {

						// Si el punto vecino no está en la cola:
						if ((!abiertos.contains(p))) {
							// Pintamos el mapa
							if (!p.equals(mapa.pto_inicial) && !p.equals(mapa.pto_final)) {
								mapa.pintarMapa(Color.CYAN, 0, p.getFila(), p.getCol());
								// Interfaz.mapa.MatrizBotones[p.getFila()][p.getCol()].setBackground(Color.CYAN);
							}
							// Finalmente, lo añadimos
							abiertos.add(p);
							// Si tenemos que añadir un nuevo nodo abierto, se incrementa la memoria usada
							cantMem++;
						}
						// O si está en la colacomprobamososia tiene menos pasos:
						else {
							Iterator<Punto> it = abiertos.iterator();
							Punto p2 = it.next();

							while (!p2.equals(p) && it.hasNext())
								p2 = it.next();

							// Si tiene menos pasos tachamos de abiertos el anterior y metemos el nuevo
							if (p.pasos < p2.pasos) {
								abiertos.remove(p2);
								abiertos.add(p);
							}
						}

					}

					// Sumamos una iteración y la cantidad de memoria usada
					iteraciones++;
					memoria += cantMem;

					Interfaz.datosAstar.setText(
							new String("Memoria usada: ") + Astar.memoria + "    " + "Iteraciones: " + Astar.iteraciones);

					if (Interfaz.btnStop.isEnabled() && !abiertos.isEmpty() && abiertos.peek().equals(mapa.pto_final)) {
						Punto p = abiertos.poll();
						// iteraciones = explorados.size();
						while (!p.padre.equals(mapa.pto_inicial)) {
							p = p.padre;
							mapa.pintarMapa(Color.PINK, 0, p.getFila(), p.getCol());
							// Interfaz.mapa.MatrizBotones[p.getFila()][p.getCol()].setBackground(Color.PINK);
						}

						// Interfaz.log.append("Memoria usada: " + Astar.memoria + "." + newline);
						// Interfaz.log.append("Iteraciones: " + Astar.iteraciones + "." + newline);

						timer.stop();
						Interfaz.btnStart.setEnabled(false);
						JOptionPane.showMessageDialog(new JFrame(), "Se encontró solución.");
					}

					else if (Interfaz.btnStop.isEnabled() && abiertos.isEmpty()) {
						timer.stop();
						Interfaz.btnStart.setEnabled(false);
						JOptionPane.showMessageDialog(new JFrame(), "No se encontró solución.");
					}
				}
			}

		});
		timer.start();

	}

}

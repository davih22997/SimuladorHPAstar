package program;

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

	// private static String newline = "\n";

	// Variables que vamos a usar para la simulación de A*
	// Datos de la simulación
	protected static int memoria = 0;
	protected static int iteraciones = 0;

	// Temporizador con el que se irá coloreando el mapa
	protected static Timer timer;

	/**
	 * Método que realiza la búsqueda de A*
	 * 
	 * Utilizamos un Timer porque si no, el UI mostraría todo el coloreado de una
	 * vez, en lugar de ir coloreando paso a paso
	 * 
	 * 
	 * @param mapa
	 */
	public static void BusquedaAstar(Mapa mapa) {
		memoria = 1;
		iteraciones = 0;

		Interfaz.datosAstar
				.setText(new String("Memoria usada: ") + Astar.memoria + "    " + "Iteraciones: " + Astar.iteraciones);

		ArrayList<Punto> cerrados = new ArrayList<>();

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
		// simulación).
		// Definimos, pues el temporizador con el algoritmo A*:
		timer = new Timer((int) (250 / (2 * Interfaz.v)), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Tenemos en cuenta la variable de velocidad (v) de la clase Interfaz:
				timer.setDelay((int) (250 / (2 * Interfaz.v)));

				// Si pulsamos el botón de stop, detenemos la simulación
				if (!Interfaz.btnStop.isEnabled())
					timer.stop();

				else if ((!abiertos.isEmpty()) && !abiertos.peek().equals(mapa.pto_final)) {
					// El punto con el menor coste
					Punto actual = abiertos.poll();

					cerrados.add(actual);
					// Pintamos el mapa según lo que vamos explorando (con excepción del pto_inicial
					// que se queda en verde
					if (!actual.equals(mapa.pto_inicial)) {
						mapa.pintarMapa(Mapa.cCerrado, actual.getFila(), actual.getCol());
					}

					// Cogemos los vecinos del punto
					ArrayList<Punto> vecinos = actual.vecinos(mapa.getFilas(), mapa.getCols());

					// Descartamos los nodos que no van a tenerse en cuenta para ahorrar coste
					// computacional
					// Eliminamos los obstaculos
					vecinos.removeAll(mapa.obstaculos);
					// Eliminamos también los puntos ya analizados
					vecinos.removeAll(cerrados);

					// Comprobamos cada vecino del punto actual (sin contar obstaculos)
					int cantMem = 0;
					for (Punto p : vecinos) {

						// Si el punto vecino no está en la cola:
						if ((!abiertos.contains(p))) {
							// Pintamos el mapa
							if (!p.equals(mapa.pto_inicial) && !p.equals(mapa.pto_final)) {
								mapa.pintarMapa(Mapa.cAbierto, p.getFila(), p.getCol());
							}
							// Finalmente, lo añadimos
							abiertos.add(p);
							// Si tenemos que añadir un nuevo nodo abierto, se incrementa la memoria usada
							cantMem++;
						}
						// O si está en la cola comprobamos si ya tiene menos pasos:
						else {
							Iterator<Punto> it = abiertos.iterator();
							Punto p2 = it.next();

							while (!p2.equals(p) && it.hasNext())
								p2 = it.next();

							// Si tiene menos pasos "tachamos" de abiertos el anterior y metemos el nuevo en
							// la lista
							if (p.pasos < p2.pasos) {
								abiertos.remove(p2);
								abiertos.add(p);
							}
						}

					}

					// Sumamos una iteración y la cantidad de memoria usada
					iteraciones++;
					memoria += cantMem;

					// Y vamos mostrando esos datos
					Interfaz.datosAstar.setText(new String("Memoria usada: ") + Astar.memoria + "    " + "Iteraciones: "
							+ Astar.iteraciones);

					// Comprobamos si, finalmente encuentra la solución (y ya se detiene la
					// simulación)
					// 1. Si la encuentra: Muestra el camino y te lo indica.
					if (Interfaz.btnStop.isEnabled() && !abiertos.isEmpty() && abiertos.peek().equals(mapa.pto_final)) {
						Punto p = abiertos.poll();
						while (!p.padre.equals(mapa.pto_inicial)) {
							p = p.padre;
							mapa.pintarMapa(Mapa.cRecorrido, p.getFila(), p.getCol());
						}

						timer.stop();
						Interfaz.btnStart.setEnabled(false);
						JOptionPane.showMessageDialog(new JFrame(), "Se encontró solución.");
					}

					// 2. Si no la encuentra: simplemente lo indica
					else if (Interfaz.btnStop.isEnabled() && abiertos.isEmpty()) {
						timer.stop();
						Interfaz.btnStart.setEnabled(false);
						JOptionPane.showMessageDialog(new JFrame(), "No se encontró solución.");
					}
				}
			}

		});

		// Una vez definido el temporizador, lo iniciamos
		timer.start();

	}

}

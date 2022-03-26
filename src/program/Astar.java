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

	/**
	 * Clase que contiene el número de pasos dado hasta llegar a un punto, el punto
	 * y los datos del paso anterior (de haberlo)
	 * 
	 * @author david
	 *
	 */
	static class Datos {
		double coste;
		Punto p;
		Datos p_anterior;

		/**
		 * Crea los datos iniciales (sin datos anteriores)
		 * 
		 * @param coste
		 * @param p
		 */
		public Datos(double coste, Punto p) {
			this(coste, p, null);
		}

		/**
		 * Crea los datos a partir del número de pasos dado, del coste, del punto y de
		 * los datos anteriores
		 * 
		 * @param coste
		 * @param p
		 * @param anterior
		 */
		public Datos(double coste, Punto p, Datos anterior) {
			this.coste = coste;
			this.p = p;
			p_anterior = anterior;
		}

		@Override
		public boolean equals(Object obj) {
			boolean res = false;

			// Vemos si se trata de otro objeto tipo Datos
			if (obj instanceof Datos)
				res = ((Datos) obj).p.equals(p);
			// Si no, vemos si se trata de un objeto tipo Punto
			else if (obj instanceof Punto)
				res = ((Punto) obj).equals(p);

			return res;
		}

		@Override
		public int hashCode() {
			return p.hashCode();
		}
		
		@Override
		public String toString() {
			return this.p.toString();
		}

	}

	// Variables que vamos a usar para la simulación de A*
	// Datos de la simulación
	protected static int memoria = 0;
	protected static int iteraciones = 0;

	// Temporizador con el que se irá coloreando el mapa
	protected static Timer timer;

	// Constantes para la seleccion del algoritmo en cuestión
	public static final int VECINOS_4 = 1;
	public static final int VECINOS_8 = 2;

	/**
	 * Método que realiza la búsqueda de A*
	 * 
	 * Utilizamos un Timer porque si no, el UI mostraría todo el coloreado de una
	 * vez, en lugar de ir coloreando paso a paso
	 * 
	 * 
	 * @param mapa
	 */
	public static void BusquedaAstar(Mapa mapa, int modo) {
		memoria = 1;
		iteraciones = 0;

		Interfaz.datosAstar
				.setText(new String("Memoria usada: ") + Astar.memoria + "    " + "Iteraciones: " + Astar.iteraciones);

		// Se crea la lista de puntos cerrados (ya visitados)
		// ArrayList<Punto> cerrados = new ArrayList<>();
		ArrayList<Datos> cerrados = new ArrayList<>();

		// Se crea una lista de sucesores ordenada según el coste que tenga
		PriorityQueue<Datos> abiertos = new PriorityQueue<>(new Comparator<Datos>() {
			@Override
			public int compare(Datos d1, Datos d2) {
				// Primero, se tiene en cuenta e lque menos coste tiene
				double c1 = d1.coste;
				double c2 = d2.coste;

				Punto p1 = d1.p;
				Punto p2 = d2.p;

				// Si hemos seleccionado 4-vecinos
				if (modo == VECINOS_4) {
					c1 += p1.distManhattan(mapa.pto_final);
					c2 += p2.distManhattan(mapa.pto_final);
				}
				// Si hemos seleccionado 8-vecinos
				else if (modo == VECINOS_8) {
					c1 += p1.distOctil(mapa.pto_final);
					c2 += p2.distOctil(mapa.pto_final);
				}

				// Los comparamos
				int solucion = 0;

				if (c1 > c2)
					solucion = 1;
				else if (c1 < c2)
					solucion = -1;
				// En caso de empate, ordenamos según el que más pasos haya dado
				else {
					if (d2.coste > d1.coste)
						solucion = 1;
					else if (d2.coste < d1.coste)
						solucion = -1;
				}

				return solucion;
			}
		});
		/*
		 * PriorityQueue<Punto> abiertos = new PriorityQueue<>(new Comparator<Punto>() {
		 * 
		 * @Override public int compare(Punto p1, Punto p2) { // Primero se tiene en
		 * cuenta el que menos coste tiene // Creamos los costes de cada punto: double
		 * c1 = p1.coste; double c2 = p2.coste;
		 * 
		 * // Si hemos seleccionado 4-vecinos if (modo == VECINOS_4) { c1 +=
		 * p1.distManhattan(mapa.pto_final); c2 += p2.distManhattan(mapa.pto_final); }
		 * // Si hemos seleccionado 8-vecinos else if (modo == VECINOS_8) { c1 +=
		 * p1.distOctil(mapa.pto_final); c2 += p2.distOctil(mapa.pto_final); }
		 * 
		 * // Los comparamos int solucion = 0; if (c1 > c2) solucion = 1; else if (c1 <
		 * c2) solucion = -1; // En caso de empate, ordenamos según el que más pasos
		 * haya dado else { if (p2.coste > p1.coste) solucion = 1; else if (p2.coste <
		 * p1.coste) solucion = -1; }
		 * 
		 * return solucion; } });^
		 */

		// Inicialmente, añadimos el punto inicial
		// abiertos.add(mapa.pto_inicial.clone());
		abiertos.add(new Datos(0, mapa.pto_inicial));

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

				else if ((!abiertos.isEmpty()) && !abiertos.peek().p.equals(mapa.pto_final)) {
					// El punto con el menor coste
					Datos actual = abiertos.poll();

					cerrados.add(actual);
					// Pintamos el mapa según lo que vamos explorando (con excepción del pto_inicial
					// que se queda en verde
					if (!actual.p.equals(mapa.pto_inicial)) {
						mapa.pintarMapa(Mapa.cCerrado, actual.p.getFila(), actual.p.getCol());
					}

					// Cogemos los vecinos del punto
					ArrayList<Punto> vecinos = new ArrayList<>();
					if (modo == VECINOS_4)
						vecinos = actual.p.vecinos(mapa.getFilas(), mapa.getCols());
					else if (modo == VECINOS_8)
						vecinos = actual.p.vecinos_8(mapa.getFilas(), mapa.getCols());

					// Descartamos los nodos que no van a tenerse en cuenta para ahorrar coste
					// computacional
					// Eliminamos los obstaculos
					vecinos.removeAll(mapa.obstaculos);
					// Eliminamos también los puntos ya analizados
					for (Datos d : cerrados)
						vecinos.remove(d.p);
					//vecinos.removeAll(cerrados);

					// Comprobamos cada vecino del punto actual (sin contar obstaculos)
					int cantMem = 0;
					for (Punto p : vecinos) {

						// Calculamos la distancia para añadirla al coste
						double distancia = 1;
						// Si el modo es VECINOS_4, la distancia con sus vecinos va a ser siempre 1; sin
						// embargo, si es VECINOS_8, la distancia puede ser 1 ó raíz de 2 con respecto
						// al punto actual
						if (modo == VECINOS_8)
							distancia = p.distOctil(actual.p);
						
						// A la distancia le sumamos el coste acumulado
						distancia += actual.coste;

						// Creamos una estructura tipo datos para el punto
						Datos d = new Datos (distancia, p, actual);
						
						// Si el punto vecino no está en la cola:
						if ((!abiertos.contains(d))) {
							// Pintamos el mapa
							if (!p.equals(mapa.pto_inicial) && !p.equals(mapa.pto_final)) {
								mapa.pintarMapa(Mapa.cAbierto, p.getFila(), p.getCol());
							}

							// Finalmente, lo añadimos (el coste es la distancia + el coste acumulado)
							abiertos.add(d);
							// Si tenemos que añadir un nuevo nodo abierto, se incrementa la memoria usada
							cantMem++;
						}
						// O si está en la cola comprobamos si ya tiene menos pasos:
						else {
							Iterator<Datos> it = abiertos.iterator();
							Datos d2 = it.next();

							while (!d2.p.equals(p) && it.hasNext())
								d2 = it.next();

							// Si tiene menos pasos "tachamos" de abiertos el anterior y metemos el nuevo en
							// la lista
							if (distancia< d2.coste) {
								abiertos.remove(d2);
								abiertos.add(d);
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
					if (Interfaz.btnStop.isEnabled() && !abiertos.isEmpty() && abiertos.peek().p.equals(mapa.pto_final)) {
						Datos d = abiertos.poll();
						while (!d.p_anterior.p.equals(mapa.pto_inicial)) {
							d = d.p_anterior;
							mapa.pintarMapa(Mapa.cRecorrido, d.p.getFila(), d.p.getCol());
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

	/**
	 * Método que será llamado en HPAstar
	 */
	public static void busquedaEnHPAstar() {

	}

}

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
		int coste;
		Punto p;
		Datos p_anterior;
		// Longitud del camino
		int longitud;

		/**
		 * Crea los datos iniciales (sin datos anteriores)
		 * 
		 * @param coste
		 * @param p
		 */
		public Datos(int coste, Punto p) {
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
		public Datos(int coste, Punto p, Datos anterior) {
			this.coste = coste;
			this.p = p;
			p_anterior = anterior;
			longitud = anterior != null ? anterior.longitud + 1 : 1;
		}

		public Datos(int coste, Punto p, int longitud, Datos anterior) {
			this.coste = coste;
			this.p = p;
			this.longitud = anterior.longitud + longitud;
			p_anterior = anterior;
		}

		@Override
		public boolean equals(Object obj) {
			// Solamente comparamos que los puntos sean iguales
			return obj instanceof Datos ? ((Datos) obj).p.equals(p) : false;
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
	// Cantidad de nodos abiertos
	protected static int memoria = 0;
	// Cantidad de iteraciones
	protected static int iteraciones = 0;
	// Longitud de la solución (de haberla)
	protected static int longitud = 0;

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
	public static void busquedaAstar(Mapa mapa, int modo) {
		// Inicializamos la memoria y las iteraciones
		memoria = 1;
		iteraciones = 0;

		// Lo mostramos
		Interfaz.datosAstar
				.setText(new String("Memoria usada: ") + Astar.memoria + "    " + "Iteraciones: " + Astar.iteraciones);

		// Se crea la lista de puntos cerrados (ya visitados)
		ArrayList<Datos> cerrados = new ArrayList<>();

		// Se crea una lista de sucesores ordenada según el coste que tenga
		PriorityQueue<Datos> abiertos = crearAbiertos(mapa, modo);

		// Inicialmente, añadimos el punto inicial
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

					// Lo añadimos a los puntos ya visitados (cerrados)
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

					// Descartamos los nodos que no van a tenerse en cuenta
					// Eliminamos los obstaculos
					vecinos.removeAll(mapa.obstaculos);
					// Eliminamos también los puntos ya analizados
					for (Datos d : cerrados)
						vecinos.remove(d.p);

					// Comprobamos cada vecino del punto actual (sin contar obstaculos)
					int cantMem = 0;
					for (Punto p : vecinos) {
						// Calculamos la distancia para añadirla al coste
						int distancia = 100;
						// Si el modo es VECINOS_4, la distancia con sus vecinos va a ser siempre 1; sin
						// embargo, si es VECINOS_8, la distancia puede ser 1 ó raíz de 2 con respecto
						// al punto actual
						if (modo == VECINOS_8)
							distancia = p.distOctil(actual.p);

						// A la distancia le sumamos el coste acumulado
						distancia += actual.coste;

						// Creamos una estructura tipo datos para el punto
						Datos d = new Datos(distancia, p, actual);

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
							if (distancia < d2.coste) {
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
					if (Interfaz.btnStop.isEnabled() && !abiertos.isEmpty()
							&& abiertos.peek().p.equals(mapa.pto_final)) {
						Datos d = abiertos.poll();
						longitud = d.longitud;
						while (!d.p_anterior.p.equals(mapa.pto_inicial)) {
							d = d.p_anterior;
							mapa.pintarMapa(Mapa.cRecorrido, d.p.getFila(), d.p.getCol());
						}

						timer.stop();
						Interfaz.btnStart.setEnabled(false);
						JOptionPane.showMessageDialog(new JFrame(),
								"Se encontró solución. Su longitud es de " + longitud);
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
	 * Método que será llamado en HPAstar para aplicar A* una vez que tenemos
	 * definidos los nodos y arcos
	 */
	public static void busquedaEnHPAstar(Mapa mapa, int modo) {

		// Inicializamos memoria e iteraciones
		HPAstar.refmemoria = 1;
		HPAstar.refiters = 0;

		// Lo mostramos
		if (HPAstar.modo == 0)
			Interfaz.datosAstar.setText(
					new String("Memoria usada: ") + HPAstar.refmemoria + "    " + "Iteraciones: " + HPAstar.refiters);

		// Se crea la lista de puntos cerrados (ya visitados)
		ArrayList<Datos> cerrados = new ArrayList<>();

		// Se crea una lista de sucesores ordenada según el coste que tenga
		PriorityQueue<Datos> abiertos = crearAbiertos(mapa, modo);

		// Inicialmente, añadimos el punto inicial
		abiertos.add(new Datos(0, mapa.pto_inicial));

		// Si estamos en modo Test, no vemos paso a paso
		int delay = 0;

		// En cambio, si estamos con la interfaz, sí
		if (HPAstar.modo == 0)
			delay = 125;

		// Generamos un temporizador para ver paso por paso
		timer = new Timer(delay, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Si pulsamos el botón de stop, detenemos la simulación
				if (HPAstar.modo == 0 && !Interfaz.btnStop2.isEnabled())
					timer.stop();

				else if (!abiertos.isEmpty() && !abiertos.peek().p.equals(mapa.pto_final)) {
					// El punto con el menor coste
					Datos actual = abiertos.poll();

					// Lo añadimos a los puntos ya visitados (cerrados)
					cerrados.add(actual);

					if (!actual.p.equals(mapa.pto_inicial))
						mapa.pintarMapa(Mapa.cCerrado, actual.p.getFila(), actual.p.getCol());

					// Cogemos los sucesores del punto
					ArrayList<Punto> sucesores = new ArrayList<>();
					sucesores = (ArrayList<Punto>) HPAstar.sucesores.get(actual.p).clone();

					// Descartamos los puntos ya analizados
					for (Datos d : cerrados)
						sucesores.remove(d.p);

					// Vamos comprobando los sucesores
					for (Punto p : sucesores) {
						// Creamos el arco entre los dos puntos
						Arco arco = new Arco(actual.p, p);

						// Calculamos la distancia (coste) con respecto al punto
						int dist = HPAstar.costes.get(arco);

						// Además, le añadimos el coste acumulado
						dist += actual.coste;

						// Calculamos la longitud (restamos 1 porque ya hemos visitado el punto)
						int longi = HPAstar.caminos.get(arco).size() - 1;

						// Creamos una estructura tipo datos para el punto
						Datos d = new Datos(dist, p, longi, actual);

						// Si el punto vecino no está en la cola:
						if ((!abiertos.contains(d))) {
							// Pintamos el mapa
							if (!p.equals(mapa.pto_inicial) && !p.equals(mapa.pto_final)) {
								mapa.pintarMapa(Mapa.cAbierto, p.getFila(), p.getCol());
							}
							// Finalmente, lo añadimos (el coste es la distancia + el coste acumulado)
							abiertos.add(d);
							// Incrementamos la cantidad de nodos expandidos
							HPAstar.refmemoria++;
						}
						// O si está en la cola comprobamos si ya tiene menos pasos:
						else {
							Iterator<Datos> it = abiertos.iterator();
							Datos d2 = it.next();

							while (!d2.p.equals(p) && it.hasNext())
								d2 = it.next();

							// Si tiene menos pasos "tachamos" de abiertos el anterior y metemos el nuevo en
							// la lista
							if (dist < d2.coste) {
								abiertos.remove(d2);
								abiertos.add(d);
							}
						}
					}
					// Incrementamos las iteraciones
					HPAstar.refiters++;

					// Y vamos mostrando esos datos
					if (HPAstar.modo == 0)
						Interfaz.datosAstar.setText(new String("Memoria usada: ") + HPAstar.refmemoria + "    "
								+ "Iteraciones: " + HPAstar.refiters);
				}

				// Imprimimos el resultado
				// 1. Si la encuentra: Muestra el camino y te lo indica.
				if (!abiertos.isEmpty() && abiertos.peek().p.equals(mapa.pto_final)) {
					Datos d = abiertos.poll();
					HPAstar.longitud = d.longitud;

					while (d.p_anterior != null) {
						// Vamos a pintar todo el recorrido
						// 1. Cogemos el punto anterior
						Punto p = d.p;
						// Cogemos los datos anteriores para sacar el siguiente punto
						d = d.p_anterior;

						// Cogemos cada punto del camino existente entre el punto anterior y su
						// siguiente
						for (Punto pt : HPAstar.caminos.get(new Arco(p, d.p))) {
							// Coloreamos de rosa si no es un nodo
							if (!HPAstar.sucesores.containsKey(pt)) {
								mapa.pintarMapa(Mapa.cRecorrido, pt.getFila(), pt.getCol());
							}

							// Si es un nodo y no es punto inicial ni final, pintamos de rosa oscuro
							if (HPAstar.sucesores.keySet().contains(pt)
									&& !(pt.equals(mapa.pto_inicial) || pt.equals(mapa.pto_final)))
								mapa.pintarMapa(Mapa.cRecorrido.darker(), pt);

						}
					}
					timer.stop();
					if (HPAstar.modo == 0)
						Interfaz.btnStart2.setEnabled(false);
					JOptionPane.showMessageDialog(new JFrame(),
							"Se encontró solución. Su longitud es de " + HPAstar.longitud);
				}

				// 2. Si no la encuentra: simplemente lo indica
				else if (abiertos.isEmpty()) {
					timer.stop();
					if (HPAstar.modo == 0)
						Interfaz.btnStart2.setEnabled(false);
					JOptionPane.showMessageDialog(new JFrame(), "No se encontró solución.");
				}
			}

		});

		// Una vez definido el temporizador, lo iniciamos
		timer.start();
	}

	/**
	 * Método para aplicar Astar desde la clase Test (sin Timers) y sin dibujar
	 * 
	 * @param mapa
	 */
	protected static void testAstar(Mapa mapa, int modo, boolean debug) {
		// Se crea la lista de puntos cerrados (ya visitados)
		ArrayList<Datos> cerrados = new ArrayList<>();

		// Se crea una lista de sucesores ordenada según el coste que tenga
		PriorityQueue<Datos> abiertos = crearAbiertos(mapa, modo);

		// Inicialmente, añadimos el punto inicial
		abiertos.add(new Datos(0, mapa.pto_inicial));

		iteraciones = 0;
		memoria = 1;

		// Vamos recorriendo hasta que nos quedemos sin nodos por recorrer o hasta que
		// encontremos el punto final
		while (!abiertos.isEmpty() && !abiertos.peek().p.equals(mapa.pto_final)) {
			// El punto con el menor coste
			Datos actual = abiertos.poll();

			// Lo añadimos a los puntos ya visitados (cerrados)
			cerrados.add(actual);

			// Pintamos el mapa según lo que vamos explorando (con excepción del pto_inicial
			// que se queda en verde
			if (debug && !actual.p.equals(mapa.pto_inicial)) {
				mapa.pintarMapa(Mapa.cCerrado, actual.p.getFila(), actual.p.getCol());
			}

			// Cogemos los sucesores del punto
			ArrayList<Punto> sucesores = actual.p.vecinos_8(mapa.getFilas(), mapa.getCols());

			sucesores.removeAll(mapa.obstaculos);
			// Descartamos los puntos ya analizados
			for (Datos d : cerrados)
				sucesores.remove(d.p);

			// Creamos un contador de memoria
			int cantMem = 0;
			// Vamos comprobando los sucesores
			for (Punto p : sucesores) {
				// Calculamos la distancia (coste) con respecto al punto
				int dist = p.distOctil(actual.p);

				// Además, le añadimos el coste acumulado
				dist += actual.coste;

				// Creamos una estructura tipo datos para el punto
				Datos d = new Datos(dist, p, actual);

				// Si el punto vecino no está en la cola (se abre un nuevo nodo)
				if ((!abiertos.contains(d))) {
					// Pintamos el mapa
					if (debug && !p.equals(mapa.pto_inicial) && !p.equals(mapa.pto_final)) {
						mapa.pintarMapa(Mapa.cAbierto, p.getFila(), p.getCol());
					}
					// Finalmente, lo añadimos (el coste es la distancia + el coste acumulado)
					abiertos.add(d);
					// Incrementamos el contador de nodos abiertos
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
					if (dist < d2.coste) {
						abiertos.remove(d2);
						abiertos.add(d);
					}
				}

			}
			// Sumamos los nodos abiertos a la memoria usada
			memoria += cantMem;
			iteraciones++;
		}

		// Recogemos la longitud del camino
		Datos d = abiertos.peek();
		longitud = d != null ? d.longitud : 0;

		if (debug && d != null) {
			d = abiertos.poll();
			longitud = d.longitud;
			while (!d.p_anterior.p.equals(mapa.pto_inicial)) {
				d = d.p_anterior;
				mapa.pintarMapa(Mapa.cRecorrido, d.p.getFila(), d.p.getCol());
			}
			JOptionPane.showMessageDialog(new JFrame(), "Se encontró solución. Su longitud es de " + longitud);
		}

	}

	/**
	 * Método que será llamado en HPAstar para aplicar A* en Test una vez que
	 * tenemos definidos los nodos y arcos
	 * 
	 * @param mapa
	 * @param modo
	 */
	protected static void testEnHPAstar(Mapa mapa, int modo) {
		// Se crea la lista de puntos cerrados (ya visitados)
		ArrayList<Datos> cerrados = new ArrayList<>();

		// Se crea una lista de sucesores ordenada según el coste que tenga
		PriorityQueue<Datos> abiertos = crearAbiertos(mapa, modo);

		// Inicialmente, añadimos el punto inicial
		abiertos.add(new Datos(0, mapa.pto_inicial));

		// Vamos recorriendo hasta que nos quedemos sin nodos por recorrer o hasta que
		// encontremos el punto final
		while (!abiertos.isEmpty() && !abiertos.peek().p.equals(mapa.pto_final)) {
			// El punto con el menor coste
			Datos actual = abiertos.poll();

			// Lo añadimos a los puntos ya visitados (cerrados)
			cerrados.add(actual);

			// Cogemos los sucesores del punto
			ArrayList<Punto> sucesores = new ArrayList<>();
			sucesores = (ArrayList<Punto>) HPAstar.sucesores.get(actual.p).clone();

			// Descartamos los puntos ya analizados
			for (Datos d : cerrados)
				sucesores.remove(d.p);

			// Vamos comprobando los sucesores
			for (Punto p : sucesores) {
				// Creamos el arco entre los dos puntos
				Arco arco = new Arco(actual.p, p);
				// Calculamos la distancia (coste) con respecto al punto
				int dist = HPAstar.costes.get(arco);

				// Además, le añadimos el coste acumulado
				dist += actual.coste;

				// Calculamos la longitud (restamos 1 porque ya hemos visitado el punto)
				int longi = HPAstar.caminos.get(arco).size() - 1;

				// Creamos una estructura tipo datos para el punto
				Datos d = new Datos(dist, p, longi, actual);

				// Si el punto vecino no está en la cola:
				if ((!abiertos.contains(d))) {
					// Finalmente, lo añadimos (el coste es la distancia + el coste acumulado)
					abiertos.add(d);
					// Incrementamos la cantidad de nodos expandidos
					HPAstar.refmemoria++;
				}

				// O si está en la cola comprobamos si ya tiene menos pasos:
				else {
					Iterator<Datos> it = abiertos.iterator();
					Datos d2 = it.next();

					while (!d2.p.equals(p) && it.hasNext())
						d2 = it.next();

					// Si tiene menos pasos "tachamos" de abiertos el anterior y metemos el nuevo en
					// la lista
					if (dist < d2.coste) {
						abiertos.remove(d2);
						abiertos.add(d);
					}
				}

			}

			// Incrementamos las iteraciones
			HPAstar.refiters++;
		}

		// Recogemos la longitud del camino
		Datos d = abiertos.peek();
		HPAstar.longitud = d != null ? d.longitud : 0;
	}

	/**
	 * Método para crear la lista de puntos abiertos
	 * 
	 * @param mapa
	 * @param modo
	 * @return
	 */
	private static PriorityQueue<Datos> crearAbiertos(Mapa mapa, int modo) {
		// Se crea una lista de sucesores ordenada según el coste que tenga
		PriorityQueue<Datos> abiertos = new PriorityQueue<>(new Comparator<Datos>() {
			@Override
			public int compare(Datos d1, Datos d2) {
				// Primero, se tiene en cuenta el que menos coste tiene
				int c1 = d1.coste;
				int c2 = d2.coste;

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
				// En caso de empate, ordenamos según el que más pasos haya dado (a más pasos
				// más coste)
				else {
					if (d2.coste > d1.coste)
						solucion = 1;
					else if (d2.coste < d1.coste)
						solucion = -1;
				}

				return solucion;
			}
		});

		return abiertos;
	}

}

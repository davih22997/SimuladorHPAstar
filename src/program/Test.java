package program;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test {

	// Variables estáticas para definir el modo de operación
	private static final int MODO_GRAFICAS = 0; // Modo de creación de gráficas
	private static final int MODO_ERROR_Astar = 1; // Modo para probar puntos que dan error en A*
	private static final int MODO_ERROR_HPAstar = 2; // Modo para probar puntos que dan error en HPA*

	// Cogemos el mapa de 320x320, que vamos a usar para la prueba
	private static final String map = Direccion.maps[0];
	// Cogemos el número de pruebas que hacemos
	private static final Integer NPRUEBAS = 200;
	// Datos predefinidos del mapa
	private Mapa mapa;
	private int height; // Altura (num filas)
	private int width; // Anchura (num columnas)
	private ArrayList<Punto> obstaculos = new ArrayList<>(); // Lista de obstaculos
	// Datos que generamos para el mapa
	private Punto[] iniciales = new Punto[NPRUEBAS]; // Array de NPRUEBAS puntos iniciales
	private Punto[] finales = new Punto[NPRUEBAS]; // Array de NPRUEBAS puntos finales

	// Datos que recogemos de A*:
	// Datos de tiempo
	private double[] timeAstar = new double[NPRUEBAS]; // Array de NPRUEBAS muestras de tiempo de ejecución de A*
	// Datos de longitud de la solución
	private int[] longAstar = new int[NPRUEBAS]; // Array de NPRUEBAS muestras de coste de A*
	// Datos de nodos expandidos (iteraciones)
	private int[] itAstar = new int[NPRUEBAS]; // Array de NPRUEBAS muestras de memoria usada por A*

	// Datos predefinidos de HPA*
	private int umbral = 6;
	private int dCluster = HPAstar.CLUSTER_10X10;
	// Datos que recogemos de HPA*:
	// Datos de tiempo
	private double[] timeESHPAstar = new double[NPRUEBAS]; // Array de NPRUEBAS muestras de tiempo de introducción de
															// los puntos inicial y final en HPA*
	private double[] timeHPAstar = new double[NPRUEBAS]; // Array de NPRUEBAS muestras de tiempo de refinamiento de HPA*
	// Datos de longitud de la solución (coste)
	private int[] longHPAstar = new int[NPRUEBAS]; // Array de NPRUEBAS muestras de coste de HPA*
	// Datos de nodos expandidos (iteraciones)
	private int[] itHPAAstar = new int[NPRUEBAS]; // Array de NPRUEBAS muestras de memoria usada por HPA*
	// Datos de la calidad de la solución (porcentaje de éxito):
	// calidad(%) = costAstar/costHPAStar * 100
	// Dato sobre el tiempo
	private double pretime; // Tiempo de preprocesamiento

	// Dimensiones de las graficas
	private static final int ANCHO = 1200;
	private static final int ALTO = 1000;

	// Nombres de los archivos generados
	// Ficheros con las muestras y sus resultados
	private static final String results = "datos.txt";

	// Constante para imprimir una nueva línea
	private static final String newline = "\n";
	// Constante para un espacio
	private static final String space = " ";
	// Constante NaN
	private static final String nan = "NaN";

	public Test() {
		new Test(MODO_GRAFICAS, null, null);
	}

	public Test(int modo, Punto ini, Punto fin) {
		// Modo indica si es para generar datos, o si es para testear Datos incorrectos
		switch (modo) {
		case MODO_GRAFICAS: // Modo creación de datos
			// 1. Tratar el mapa:
			// Paso 1: Leer el fichero y convertirlo en mapa
			leerFichero();

			// Paso 2: Generar 100 parejas de puntos inicial y final para ejecutar pruebas
			// (y guardarlo para posteriori)
			generarParejas();

			// Paso 3: Crear el mapa
			mapa = new Mapa(height, width);
			mapa.obstaculos = obstaculos;

			// 2. Ejecución. Para cada pareja de puntos (y el mismo mapa):

			// Paso 1: Utilizar A* y medir tiempo
			// Creamos un bucle
			for (int i = 0; i < NPRUEBAS; i++) {
				// Asignamos los puntos de inicio y de fin al mapa
				mapa.pto_inicial = iniciales[i];
				mapa.pto_final = finales[i];

				// Cogemos el tiempo de inicio
				long start = System.nanoTime();

				// Aplicamos A*
				Astar.testAstar(mapa, Astar.VECINOS_8, false);

				// Guardamos los resultados obtenidos
				// Tiempo: Para medir el tiempo, cogemos el tiempo actual y restamos el tiempo
				// de inicio
				long tfin = System.nanoTime() - start;
				// Lo pasamos a segundos
				timeAstar[i] = ((double) tfin) / 10E9;

				// Coste: La longitud de la solución
				longAstar[i] = Astar.longitud;
				// Nodos expandidos: Iteraciones
				itAstar[i] = Astar.iteraciones;

				// Reseteamos el mapa
				mapa.pto_inicial = null;
				mapa.pto_final = null;
			}

			// Paso 2: Utilizar HPA* y medir tiempo
			long tst = System.nanoTime();
			HPAstar.TestHPAstar(mapa, umbral, dCluster);
			long tf = System.nanoTime() - tst;
			pretime = ((double) tf) / 10E9;

			// Creamos un bucle
			for (int i = 0; i < NPRUEBAS; i++) {
				// Asignamos los puntos de inicio y de fin al mapa
				mapa.pto_inicial = iniciales[i];
				mapa.pto_final = finales[i];

				// Cogemos el tiempo de inicio
				long start = System.nanoTime();

				// Introducimos los puntos inicial y final en HPA*
				HPAstar.TestHPAstar2(mapa);

				// Guardamos los resultados obtenidos
				// Tiempo: Para medir el tiempo, cogemos el tiempo actual y restamos el tiempo
				// de inicio
				long tfin = System.nanoTime() - start;
				// Lo pasamos a segundos
				timeESHPAstar[i] = ((double) tfin) / 10E9;

				// Cogemos el tiempo de inicio
				start = System.nanoTime();

				// Aplicamos la parte A* en HPA*
				HPAstar.TestHPAstar3(mapa);

				// Guardamos los resultados obtenidos
				// Tiempo: Para medir el tiempo, cogemos el tiempo actual y restamos el tiempo
				// de inicio
				tfin = System.nanoTime() - start;
				timeHPAstar[i] = ((double) tfin) / 10E9;

				// La longitud de la solucion
				longHPAstar[i] = HPAstar.longitud;
				// Nodos expandidos: La memoria usada
				itHPAAstar[i] = HPAstar.refiters;

				// Borramos puntos de interés
				HPAstar.borrarES(mapa);
				// Reseteamos el mapa
				mapa.pto_inicial = null;
				mapa.pto_final = null;
			}

			// 3. Gráficas:
			// Son 2 gráficas. En ambas debe obtenerse:
			// -> La longitud de la solución (se puede usar el coste total)
			// Gráfica 1: Comparativa entre el número de nodos expandidos y la longitud de
			// la solución
			// Debe obtenerse:
			// -> El número de nodos expandidos (memoria)
			// -> Para HPA*, deben sumarse el preprocesado y el refinamiento (sería el
			// primer nivel)

			// Gráfica 2: Compara los tiempos de CPU
			// Debe obtenerse:
			// -> El tiempo de ejecución
			// -> Para HPA*, deben sumarse el preprocesado y el refinamiento (sería el
			// primer nivel)

			// Gráfica 3: Calidad de la solución
			// Debe obtenerse:
			// -> El porcentaje de error

			// 3. Mostrar gráficas -> JFreeChart
			// mostrarGraficas();
			// 4. Guardar en fichero los resultados
			// guardarFichero();

			// Guardamos el fichero con formato para MatLab
			guardarResultados();
			break;
		case MODO_ERROR_Astar: // Modo para testear con datos concretos
			// 1. Tratar el mapa:
			// Paso 1: Leer el fichero y convertirlo en mapa
			leerFichero();

			// Paso 2: Crear y mostrar el mapa
			crearMostrarMapa(ini, fin);

			// Paso 3: Aplicar algoritmo
			// A*
			Astar.testAstar(mapa, Astar.VECINOS_8, true);

			break;
		case MODO_ERROR_HPAstar:
			// 1. Tratar el mapa:
			// Paso 1: Leer el fichero y convertirlo en mapa
			leerFichero();

			// Paso 2: Crear y mostrar el mapa
			crearMostrarMapa(ini, fin);

			// Cambiamos el tipo del mapa
			mapa.setTipo(Mapa.TIPO_TEST);

			// Paso 3: Aplicar algoritmo
			// HPA*
			HPAstar.TestPruebaHPAstar(mapa, umbral, dCluster);

			break;
		}

	}

	/**
	 * Método para coger los datos del fichero .map
	 */
	private void leerFichero() {

		// FORMATO:

		// type octile -> No nos sirve
		// height num -> Altura
		// width num -> Anchura
		// map -> El mapa
		// @ -> Obstáculo
		// . -> Punto libre

		// 1. Cargamos el fichero (para que cargue de un package debe invocarse a
		// getClass())
		File file = new File(getClass().getResource(map).getFile());
		// 2. Escaneamos
		try (Scanner sc = new Scanner(file)) {
			// La primera línea no nos sirve
			sc.nextLine();
			// Segundo: Altura
			try (Scanner s = new Scanner(sc.nextLine())) {
				s.useDelimiter("height ");
				height = Integer.parseInt(s.next());
			}
			// Tercero: Anchura
			try (Scanner s = new Scanner(sc.nextLine())) {
				s.useDelimiter("width ");
				width = Integer.parseInt(s.next());
			}

			// La siguiente línea no nos sirve
			sc.nextLine();

			// Las siguientes líneas son para el mapa
			// Creamos un entero para la fila
			int f = 0;
			while (sc.hasNextLine()) {
				// Cogemos la línea
				char[] linea = sc.nextLine().toCharArray();
				// La recorremos caracter a caracter
				for (int c = 0; c < linea.length; c++)
					// Si se trata de un obstaculo
					if (linea[c] == '@')
						// Lo añadimos a la lista de obstaculos
						obstaculos.add(new Punto(f, c));
				// Una vez recorrida la línea, incrementamos la fila
				f++;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Método para generar las parejas de puntos
	 */
	private void generarParejas() {
		// Creamos NPRUEBAS pares de puntos
		for (int n = 0; n < NPRUEBAS; n++) {
			Punto ini;
			// Vemos que no esté repetido en la lista de iniciales
			while (contienePunto(iniciales, ini = generarPuntoAleatorio()))
				continue;
			Punto fin;
			// Vemos que sea distinto del punto inicial y que no esté repetido en la lista
			// de puntos
			// finales
			while (ini.equals(fin = generarPuntoAleatorio()) && contienePunto(finales, fin))
				continue;

			iniciales[n] = ini;
			finales[n] = fin;
		}
	}

	/**
	 * Método para generar un punto aleatorio que no esté en la lista de obstáculos
	 * 
	 * @return
	 */
	private Punto generarPuntoAleatorio() {
		// Creamos un objeto random
		Random rd = new Random();
		// Generamos fila y columna
		int fil = rd.nextInt(height);
		int col = rd.nextInt(width);
		Punto p;
		// Generamos más hasta que no esté en la lista de obstaculos
		while (obstaculos.contains(p = new Punto(fil, col))) {
			fil = rd.nextInt(height);
			col = rd.nextInt(width);
		}

		return p;
	}

	/**
	 * Método que te indica si un Punto está en un array de Punto
	 * 
	 * @return
	 */
	private boolean contienePunto(Punto[] array, Punto point) {
		// Vemos si algún punto del array coincide con el punto dado
		return Arrays.stream(array).anyMatch(i -> i != null && i.equals(point));
	}

	/**
	 * Función para guardar los resultados en el archivo "datos.txt", el cual será
	 * pasado a MatLab para la creación de las gráficas
	 */
	private void guardarResultados() {
		File file = new File(results);

		try (FileWriter fw = new FileWriter(file)) {
			StringBuilder sb = new StringBuilder();

			int ndatos = 0;

			// 1. Punto inicial
			sb.append("Pinicial");
			sb.append(space);
			// 2. Punto final
			sb.append("Pfinal");
			sb.append(space);
			// 3. Longitud de la solución óptima:
			sb.append("Longitud_A*");
			sb.append(space);
			ndatos++;
			// 4. Longitud de HPA*
			sb.append("Longitud_HPA*");
			sb.append(space);
			ndatos++;
			// 5. Tiempo de A*:
			sb.append("Tiempo_A*");
			sb.append(space);
			ndatos++;
			// 6. Tiempo de preprocesamiento en HPA*
			sb.append("Tiempo_preprocesamiento_HPA*");
			sb.append(space);
			ndatos++;
			// 7. Tiempo de introducción de puntos inicial y final en HPA*
			sb.append("Tiempo_ES_HPA*");
			sb.append(space);
			ndatos++;
			// 8. Tiempo de refinamiento HPA*:
			sb.append("Tiempo_refinamiento_HPA*");
			sb.append(space);
			ndatos++;
			// 9. Las iteraciones (o nº de nodos) de A*:
			sb.append("Nodos_A*");
			sb.append(space);
			ndatos++;
			// 10. Las iteraciones (o nº de nodos) de HPA*:
			sb.append("Nodos_HPA*");
			sb.append(space);
			ndatos++;
			// 11. El porcentaje de error
			sb.append("%error");
			sb.append(newline);
			ndatos++;

			// Introducimos los datos
			for (int i = 0; i < NPRUEBAS; i++) {

				boolean nulo = longAstar[i] == 0;

				sb.append(iniciales[i]);
				sb.append(space);
				sb.append(finales[i]);
				sb.append(space);
				if (!nulo) {
					sb.append(longAstar[i]);
					sb.append(space);
					if (longHPAstar[i] == 0) {
						sb.append(nan);
						sb.append(space);
					} else {
						sb.append(longHPAstar[i]);
						sb.append(space);
					}
					sb.append(timeAstar[i]);
					sb.append(space);
					sb.append(pretime);
					sb.append(space);
					sb.append(timeESHPAstar[i]);
					sb.append(space);
					sb.append(timeHPAstar[i]);
					sb.append(space);
					sb.append(itAstar[i]);
					sb.append(space);
					sb.append(itHPAAstar[i]);
					sb.append(space);
					if (longHPAstar[i] == 0)
						sb.append(nan);
					else {
						double d = (double) longHPAstar[i] - (double) longAstar[i];
						d /= (double) longAstar[i];
						d = Math.floor(d * 10000) / 100;
						sb.append(d);
					}
				} else {
					if (longHPAstar[i] != 0)
						System.out.println("Algo falla en A*. Puntos: " + iniciales[i] + ", " + finales[i]);

					for (int j = 0; j < ndatos; j++) {
						sb.append(nan);
						if (j < (ndatos - 1))
							sb.append(space);
					}

				}

				if (i < (NPRUEBAS - 1)) {
					sb.append(space);
					sb.append(newline);
				}

			}

			fw.write(sb.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Crea y muestra el mapa para hacer el test
	 * 
	 * @param ini
	 * @param fin
	 */
	private void crearMostrarMapa(Punto ini, Punto fin) {
		// Frame
		JFrame frame = new JFrame();
		frame.setTitle("Prueba");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		frame.setContentPane(panel);
		panel.setPreferredSize(new Dimension(ANCHO + 50, ALTO + 50));

		mapa = new Mapa(height, width);
		mapa.setSize(ALTO, ANCHO);

		// Asigno los puntos de interés:
		mapa.obstaculos = obstaculos;
		mapa.pto_final = fin;
		mapa.pto_inicial = ini;

		// Y los pinto en el mapa
		for (Punto o : obstaculos)
			mapa.pintarMapa(Mapa.cObs, o);

		mapa.pintarMapa(Mapa.cInicial, ini);
		mapa.pintarMapa(Mapa.cFinal, fin);

		panel.add(mapa.tablero, BorderLayout.NORTH);

		// Se muestra
		frame.add(mapa.tablero);

		frame.pack();
		frame.setVisible(true);
	}

}

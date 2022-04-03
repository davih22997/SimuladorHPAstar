package program;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Test {

	// Cogemos el mapa de 320x320, que vamos a usar para la prueba
	private static final String map = Direccion.maps[0];
	// Cogemos el número de pruebas que hacemos
	private static final Integer NPRUEBAS = 100;
	// Datos predefinidos del mapa
	private int height; // Altura (num filas)
	private int width; // Anchura (num columnas)
	private ArrayList<Punto> obstaculos = new ArrayList<>(); // Lista de obstaculos
	// Datos que generamos para el mapa
	private Punto[] iniciales = new Punto[NPRUEBAS]; // Array de 100 puntos iniciales
	private Punto[] finales = new Punto[NPRUEBAS]; // Array de 100 puntos finales

	// Datos que recogemos de A*:
	// Datos de tiempo
	private long[] timeAstar = new long[NPRUEBAS]; // Array de 100 muestras de tiempo de ejecución de A*
	// Datos de longitud de la solución (coste)
	private double[] costAstar = new double[NPRUEBAS]; // Array de 100 muestras de coste de A*
	// Datos de nodos expandidos (memoria)
	private int[] memAstar = new int[NPRUEBAS]; // Array de 100 muestras de memoria usada por A*

	// Datos que recogemos de HPA*:
	// Datos de tiempo
	private long[] timeHPAstar = new long[NPRUEBAS]; // Array de 100 muestras de tiempo de ejecución de HPA*
	// Datos de longitud de la solución (coste)
	private double[] costHPAstar = new double[NPRUEBAS]; // Array de 100 muestras de coste de HPA*
	// Datos de nodos expandidos (memoria)
	private int[] memHPAAstar = new int[NPRUEBAS]; // Array de 100 muestras de memoria usada por HPA*

	public Test() {
		// 1. Tratar el mapa:
		// Paso 1: Leer el fichero y convertirlo en mapa
		leerFichero();

		// Paso 2: Generar 100 parejas de puntos inicial y final para ejecutar pruebas
		// (y guardarlo para posteriori)
		generarParejas();

		// Paso 3: Crear el mapa
		Mapa mapa = new Mapa(height, width);
		mapa.obstaculos = obstaculos;

		// Paso 4: ¿Mostrar mapa?
		// Se hablará con el tutor

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
			Astar.TestAstar(mapa, Astar.VECINOS_8);

			// Guardamos los resultados obtenidos
			// Tiempo:Para medir el tiempo, cogemos el tiempo actual y restamos el tiempo de
			// inicio
			timeAstar[i] = System.nanoTime() - start;
			// Coste: La longitud de la solución
			costAstar[i] = Astar.coste;
			// Nodos expandidos: La memoria usada
			memAstar[i] = Astar.memoria;

			// Reseteamos el mapa (de momento, ponemos el mapa a null; si tuviéramos que
			// representarlo, hacemos reinicio)
			mapa.pto_inicial = null;
			mapa.pto_final = null;
		}

		// Paso 2: Utilizar HPA* y medir tiempo

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

		// 3. Mostrar gráficas

		// 4. Imprimir en fichero

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

}

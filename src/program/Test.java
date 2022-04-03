package program;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

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
	private double[] timeAstar = new double[NPRUEBAS]; // Array de 100 muestras de tiempo de ejecución de A*
	// Datos de longitud de la solución (coste)
	private double[] costAstar = new double[NPRUEBAS]; // Array de 100 muestras de coste de A*
	// Datos de nodos expandidos (memoria)
	private int[] memAstar = new int[NPRUEBAS]; // Array de 100 muestras de memoria usada por A*

	// Datos que recogemos de HPA*:
	// Datos de tiempo
	private double[] timeHPAstar = new double[NPRUEBAS]; // Array de 100 muestras de tiempo de ejecución de HPA*
	// Datos de longitud de la solución (coste)
	private double[] costHPAstar = new double[NPRUEBAS]; // Array de 100 muestras de coste de HPA*
	// Datos de nodos expandidos (memoria)
	private int[] memHPAAstar = new int[NPRUEBAS]; // Array de 100 muestras de memoria usada por HPA*

	// Colores para las graficas
	private static final Color COLOR_A = Color.RED;
	private static final Color COLOR_HPA = Color.ORANGE;
	private static final Color COLOR_FONDO = Color.WHITE;
	private static final Color COLOR_RECUADROS = Color.BLACK;

	// Dimensiones de las graficas
	private static final int ANCHO = 1000;
	private static final int ALTO = 500;

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
			long fin = System.nanoTime() - start;
			// Lo pasamos a segundos
			timeAstar[i] = ((double) fin) / 10E9;

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

		// 3. Mostrar gráficas -> JFreeChart
		mostrarGraficas();
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
	 * Método para mostrar las gráficas (usa librerías externas JFreeChart y
	 * JCommon)
	 */
	private void mostrarGraficas() {
		// Gráficas del tiempo:
		// A*
		XYSeries serieA1 = new XYSeries("A*");
		// HPA*
		XYSeries serieHPA1 = new XYSeries("HPA*");

		// Gráficas de los nodos abiertos
		XYSeries serieA2 = new XYSeries("A*");
		XYSeries serieHPA2 = new XYSeries("HPA*");

		for (int i = 0; i < NPRUEBAS; i++) {
			serieA1.add(costAstar[i], timeAstar[i]);
			serieA2.add(costAstar[i], memAstar[i]);
		}

		// Añadimos a una colección
		// Tiempo:
		XYSeriesCollection collection1 = new XYSeriesCollection();
		collection1.addSeries(serieA1);
		collection1.addSeries(serieHPA1);

		// Nodos abiertos:
		XYSeriesCollection collection2 = new XYSeriesCollection();
		collection2.addSeries(serieA2);
		collection2.addSeries(serieHPA2);

		// Guardamos las gráficas en un archivo.png
		try {
			// Gráfica del tiempo de ejecución
			JFreeChart grafica1 = crearGrafica(collection1, "Tiempo de CPU", "Longitud de la solución",
					"Tiempo total de CPU (segundos)");
			// La exportamos a un PNG
			ChartUtils.saveChartAsPNG(new File("CPU_Time.png"), grafica1, ANCHO, ALTO);

			// Gráfica de los nodos abiertos
			JFreeChart grafica2 = crearGrafica(collection2, "Total de nodos expandidos", "Longitud de la solución",
					"Número de nodos");
			// La exportamos a un PNG
			ChartUtils.saveChartAsPNG(new File("Expanded_Nodes.png"), grafica2, ANCHO, ALTO);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public JFreeChart crearGrafica(XYSeriesCollection dataset, String titulo, String ejeX, String ejeY) {

		final JFreeChart chart = ChartFactory.createXYLineChart(titulo, ejeX, ejeY, dataset, PlotOrientation.VERTICAL,
				true, // uso de leyenda
				false, // uso de tooltips
				false // uso de urls
		);
		// color de fondo de la gráfica
		chart.setBackgroundPaint(COLOR_FONDO);

		final XYPlot plot = (XYPlot) chart.getPlot();
		configurarPlot(plot);

		final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		configurarDomainAxis(domainAxis);

		// final NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
		// configurarRangeAxis(rangeAxis);

		final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		configurarRendered(renderer);

		return chart;
	}

	// configuramos el contenido del gráfico (damos un color a las líneas que sirven
	// de guía)
	private void configurarPlot(XYPlot plot) {
		plot.setDomainGridlinePaint(COLOR_RECUADROS);
		plot.setRangeGridlinePaint(COLOR_RECUADROS);
	}

	// configuramos el eje X de la gráfica (se muestran números enteros y de uno en
	// uno)
	private void configurarDomainAxis(NumberAxis domainAxis) {
		domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		domainAxis.setTickUnit(new NumberTickUnit(100));
	}

	// configuramos el eje y de la gráfica (números enteros de dos en dos y rango
	// entre 120 y 135)
	private void configurarRangeAxis(NumberAxis rangeAxis) {
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setTickUnit(new NumberTickUnit(0.1));
		rangeAxis.setRange(0, 0.9);
	}

	// configuramos las líneas de las series (añadimos un círculo en los puntos y
	// asignamos el color de cada serie)
	private void configurarRendered(XYLineAndShapeRenderer renderer) {
		renderer.setSeriesShapesVisible(0, true);
		renderer.setSeriesShapesVisible(1, true);
		renderer.setSeriesPaint(0, COLOR_A);
		renderer.setSeriesPaint(1, COLOR_HPA);
	}

}

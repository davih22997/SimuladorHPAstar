package program;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

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
	private Punto[] iniciales = new Punto[NPRUEBAS]; // Array de NPRUEBAS puntos iniciales
	private Punto[] finales = new Punto[NPRUEBAS]; // Array de NPRUEBAS puntos finales

	// Datos que recogemos de A*:
	// Datos de tiempo
	private double[] timeAstar = new double[NPRUEBAS]; // Array de NPRUEBAS muestras de tiempo de ejecución de A*
	// Datos de longitud de la solución (coste)
	private double[] costAstar = new double[NPRUEBAS]; // Array de NPRUEBAS muestras de coste de A*
	// Datos de nodos expandidos (memoria)
	private int[] memAstar = new int[NPRUEBAS]; // Array de NPRUEBAS muestras de memoria usada por A*

	// Datos que recogemos de HPA*:
	// Datos de tiempo
	private double[] timeHPAstar = new double[NPRUEBAS]; // Array de NPRUEBAS muestras de tiempo de ejecución de HPA*
	// Datos de longitud de la solución (coste)
	private double[] costHPAstar = new double[NPRUEBAS]; // Array de NPRUEBAS muestras de coste de HPA*
	// Datos de nodos expandidos (memoria)
	private int[] memHPAAstar = new int[NPRUEBAS]; // Array de NPRUEBAS muestras de memoria usada por HPA*
	// Datos de la calidad de la solución (porcentaje de éxito):
	// costAstar/costHPAStar * 100
	private double[] calidadHPAstar = new double[NPRUEBAS]; // Array de NPRUEBAS muestras del porcentaje de acierto

	// Colores para las graficas
	private static final Color COLOR_A = Color.RED; // Color para A*
	private static final Color COLOR_HPA = Color.ORANGE; // Color para HPA*
	private static final Color COLOR_FONDO = Color.WHITE; // Color de fondo
	private static final Color COLOR_RECUADROS = Color.BLACK; // Color para las líneas guía

	// Dimensiones de las graficas
	private static final int ANCHO = 1500;
	private static final int ALTO = 750;

	// Nombres de los archivos generados
	// Imágenes:
	private static final String imagen1 = "CPU_Time.png";
	private static final String imagen2 = "Expanded_Nodes.png";
	// Ficheros con las muestras y sus resultados
	private static final String fichero = "results.txt";

	// Constante para imprimir una nueva línea
	private static final String newline = "\n";

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

		// Gráfica 3: Calidad de la solución
		// Debe obtenerse:
		// -> El porcentaje de error

		// 3. Mostrar gráficas -> JFreeChart
		mostrarGraficas();
		// 4. Guardar en fichero los resultados
		guardarFichero();

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
			ChartUtils.saveChartAsPNG(new File(imagen1), grafica1, ANCHO, ALTO);

			// Gráfica de los nodos abiertos
			JFreeChart grafica2 = crearGrafica(collection2, "Total de nodos expandidos", "Longitud de la solución",
					"Número de nodos");
			// La exportamos a un PNG
			ChartUtils.saveChartAsPNG(new File(imagen2), grafica2, ANCHO, ALTO);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Método para crear la gráfica
	 * 
	 * @param dataset
	 * @param titulo
	 * @param ejeX
	 * @param ejeY
	 * @return
	 */
	private JFreeChart crearGrafica(XYSeriesCollection dataset, String titulo, String ejeX, String ejeY) {

		// Creamos la gráfica de líneas
		final JFreeChart chart = ChartFactory.createXYLineChart(titulo, ejeX, ejeY, dataset, PlotOrientation.VERTICAL,
				true, // uso de leyenda
				false, // uso de tooltips
				false // uso de urls
		);
		// Establecemos el color de fondo
		chart.setBackgroundPaint(COLOR_FONDO);

		// Configuramos el contenido
		XYPlot plot = (XYPlot) chart.getPlot();
		configurarPlot(plot);

		// Configuramos el eje X
		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		configurarDomainAxis(domainAxis);

		// final NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
		// configurarRangeAxis(rangeAxis);

		// Configuramos la manera de imprimir las líneas
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		configurarRendered(renderer);

		return chart;
	}

	/**
	 * Configuración del contenido del gráfico (se le da un color a las líneas guía)
	 * 
	 * @param plot
	 */
	private void configurarPlot(XYPlot plot) {
		plot.setDomainGridlinePaint(COLOR_RECUADROS);
		plot.setRangeGridlinePaint(COLOR_RECUADROS);
	}

	/**
	 * Configuración del eje X de la gráfica (se muestran números enteros de 100 en
	 * 100)
	 * 
	 * @param domainAxis
	 */
	private void configurarDomainAxis(NumberAxis domainAxis) {
		domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		domainAxis.setTickUnit(new NumberTickUnit(100));
	}

	/**
	 * Configuración del eje Y de la gráfica (no se usa)
	 * 
	 * @param rangeAxis
	 */
	private void configurarRangeAxis(NumberAxis rangeAxis) {
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setTickUnit(new NumberTickUnit(0.1));
		rangeAxis.setRange(0, 0.9);
	}

	/**
	 * Método de configuración de las líneas de las series. Se añaden los círculos
	 * en los puntos y se asignan los colores para cada serie
	 * 
	 * @param renderer
	 */
	private void configurarRendered(XYLineAndShapeRenderer renderer) {
		renderer.setSeriesShapesVisible(0, true);
		renderer.setSeriesShapesVisible(1, true);
		renderer.setSeriesPaint(0, COLOR_A);
		renderer.setSeriesPaint(1, COLOR_HPA);
	}

	/**
	 * Método para guardar los resultados en un fichero
	 */
	private void guardarFichero() {
		File file = new File(fichero);

		try (FileWriter fw = new FileWriter(file)) {
			StringBuilder sb = new StringBuilder();

			sb.append("Mapa empleado: " + map + newline);
			sb.append("Dimensiones del mapa: " + height + "x" + width + newline);
			sb.append("Muestras generadas: " + NPRUEBAS + " pares de puntos (punto inicial y punto final)" + newline);
			sb.append("Puntos iniciales: {");

			StringBuilder finals = new StringBuilder();
			finals.append("Puntos finales: {");

			// Datos para A*
			StringBuilder times_A = new StringBuilder();
			times_A.append("Valores obtenidos para A*:" + newline);
			times_A.append("Tiempo de ejecución: {");
			StringBuilder costs_A = new StringBuilder();
			costs_A.append("Longitud de la solución: {");
			StringBuilder mems_A = new StringBuilder();
			mems_A.append("Nodos expandidos: {");

			// Datos para HPA*
			StringBuilder times_HPA = new StringBuilder();
			times_HPA.append("Valores obtenidos para HPA*:" + newline);
			times_HPA.append("Tiempo de ejecución: {");
			StringBuilder costs_HPA = new StringBuilder();
			costs_HPA.append("Longitud de la solución: {");
			StringBuilder mems_HPA = new StringBuilder();
			mems_HPA.append("Nodos expandidos: {");
			StringBuilder q = new StringBuilder();
			q.append("Porcentaje de acierto con respecto al camino óptimo: {");

			for (int i = 0; i < NPRUEBAS; i++) {
				// Datos generados
				sb.append(iniciales[i]);
				finals.append(finales[i]);
				// Datos obtenidos por A*
				times_A.append(timeAstar[i]);
				costs_A.append(costAstar[i]);
				mems_A.append(memAstar[i]);
				// Datos obtenidos por HPA*
				times_HPA.append(timeHPAstar[i]);
				costs_HPA.append(costHPAstar[i]);
				mems_HPA.append(memHPAAstar[i]);
				q.append(calidadHPAstar[i] + "%");
				if (i < NPRUEBAS - 1) {
					sb.append(", ");
					finals.append(", ");
					times_A.append(", ");
					costs_A.append(", ");
					mems_A.append(", ");
					times_HPA.append(", ");
					costs_HPA.append(", ");
					mems_HPA.append(", ");
					q.append(", ");
				} else {
					sb.append("}" + newline);
					finals.append("}" + newline);
					times_A.append("}" + newline);
					costs_A.append("}" + newline);
					mems_A.append("}" + newline);
					times_HPA.append("}" + newline);
					costs_HPA.append("}" + newline);
					mems_HPA.append("}" + newline);
					q.append("}" + newline);
				}
			}

			sb.append(finals);
			sb.append(times_A);
			sb.append(costs_A);
			sb.append(mems_A);
			sb.append(times_HPA);
			sb.append(costs_HPA);
			sb.append(mems_HPA);
			sb.append(q);

			fw.write(sb.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

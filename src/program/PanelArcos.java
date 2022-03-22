package program;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

// Creamos un panel similar al mapa, pero con espacios (márgenes) entre cada cluster para crear el grafo abstracto

public class PanelArcos extends JPanel {

	// Matriz con los botones (igual que en mapa)
	private JButton[][] matrizBotones;

	// Copia de la lista de clusters, cuyo contenido se irá modificando
	private ArrayList<Cluster> clusters;

	public PanelArcos(Mapa mapa, ArrayList<Cluster> clusters) {
		// 1. Copiamos la lista de clusters
		this.clusters = (ArrayList<Cluster>) clusters.clone();
		// 1. Vamos a crear todo el mapa
		crearMapa(mapa);

	}

	private void crearMapa(Mapa mapa) {
		// Obtenemos el total de filas y de columnas en el mapa
		int total_filas = mapa.getFilas();
		int total_cols = mapa.getCols();

		// Obtenemos el total de filas y de columnas por cluster
		int cfilas = clusters.get(0).getFilas();
		int ccols = clusters.get(0).getCols();

		matrizBotones = new JButton[total_filas][total_cols];

		// Obtenemos las dimensiones de los botones
		Dimension dbt = mapa.getDimsButton();

		// Definimos como Layout del panel un GridBagLayout (al igual que en mapa)
		this.setLayout(new GridBagLayout());

		for (int i = 0; i < total_filas; i++) {

			for (int j = 0; j < total_cols; j++) {
				// Creamos un nuevo botón
				JButton newButton = crearBoton(dbt);
				// Lo añadimos a la matriz de botones
				matrizBotones[i][j] = newButton;
				// Definimos la posición en el panel
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = j;
				gbc.gridy = i;

				// Si es la última fila y columna de un cluster pero no del mapa
				if ((i != (total_filas - 1) && (i + 1) % cfilas == 0)
						&& (j != (total_cols - 1) && (j + 1) % ccols == 0))
					// Añadimos margen hacia abajo y hacia la derecha
					gbc.insets = new Insets(0, 0, (int) dbt.getHeight(), (int) dbt.getWidth());

				// Si es la última fila de un cluster pero no del mapa
				else if (i != (total_filas - 1) && (i + 1) % cfilas == 0)
					// Añadimos margen hacia abajo
					gbc.insets = new Insets(0, 0, (int) dbt.getHeight(), 0);

				// Si es la última columna de un cluster pero no del mapa
				else if (j != (total_cols - 1) && (j + 1) % ccols == 0)
					// Añadimos un margen a la derecha
					gbc.insets = new Insets(0, 0, 0, (int) dbt.getWidth());

				// Lo añadimos todo al panel
				add(matrizBotones[i][j], gbc);
			}
		}

	}

	/**
	 * Método para crear un botón para el panel
	 * 
	 * @param d -> Dimensión del botón
	 * @return
	 */
	private JButton crearBoton(Dimension d) {
		// Creamos el botón
		JButton button = new JButton();
		// Definimos sus dimensiones
		button.setPreferredSize(d);
		// Lo inhabilitamos
		button.setEnabled(false);
		// Lo ponemos transparente para poder pintar sobre él más adelante
		button.setContentAreaFilled(false);

		return button;
	}

	/**
	 * Método para pintar los distintos arcos
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Vamos cogiendo los clusters uno a uno
		for (Cluster c : clusters) {
			// Y dentro de él, los nodos uno a uno
			for (Punto p : c.getNodos()) {
				// 1. Pintamos los nodos en negro
				pintarNodo(p, g);

				// 2. Pintamos los arcos
				pintarArcos(p, g, c);
			}
		}

	}

	/**
	 * Método para pintar de negro un nodo dado
	 * 
	 * @param p
	 * @param g
	 */
	private void pintarNodo(Punto p, Graphics g) {
		// Cogemos el botón que corresponde al nodo
		JButton b = matrizBotones[p.getFila()][p.getCol()];

		// De él, cogemos todo el rectángulo
		Rectangle r1 = b.getBounds();
		Rectangle2D r2d = r1.getBounds2D();

		// Y pintamos de negro su interior
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(Color.BLACK);
		g2.fill(r2d);
	}

	/**
	 * Método para pintar los arcos de un nodo dado
	 * 
	 * @param p
	 * @param g
	 */
	private void pintarArcos(Punto p, Graphics g, Cluster c) {
		// Definimos el color negro para los arcos
		g.setColor(Color.BLACK);

		// Cogemos el botón que corresponde con el punto
		JButton b1 = matrizBotones[p.getFila()][p.getCol()];

		// Cogemos las coordenadas del centro del botón
		Point p1 = centroBoton(b1);

		// Vamos cogiendo los arcos externos
		for (Punto p_2 : p.getArcosExternos()) {
			// Cogemos el botón que corresponde con el punto
			JButton b2 = matrizBotones[p_2.getFila()][p_2.getCol()];

			// Cogemos las coordenadas del centro del botón
			Point p2 = centroBoton(b2);

			// Dibujamos la línea
			g.drawLine(p1.x, p1.y, p2.x, p2.y);

			// Borramos de la lista de arcos externos el punto p1 del punto p_2 (para no
			// repetir)
			p_2.getArcosExternos().remove(p);
		}

		// Cogemos el total de filas que hay
		int filas = c.getFilas();
		int cols = c.getCols();
		// Cogemos el punto intermedio
		Punto pm = new Punto(c.getPuntoInicial().getFila() + (int) Math.round((double) filas / 2) - 1,
				c.getColInicial() + (int) Math.round((double) cols / 2) - 1);

		int nfils = 0;
		int ncols = 0;

		// Cogemos ahora los arcos internos
		for (Edge edge : p.getArcosInternos()) {
			// Cogemos el punto final del edge
			Punto p_2 = edge.pfin;
			// Miramos que exista un camino entre los arcos
			if (edge.camino.size() > 0) {

				// Cogemos el botón que corresponde
				JButton b2 = matrizBotones[p_2.getFila()][p_2.getCol()];

				// Cogemos las coordenadas del botón
				Point p2 = centroBoton(b2);

				// Comprobamos si tenemos que curvar los arcos
				boolean curvar = false;

				// Comprobamos si se encuentra en la misma fila
				if (p.sameRow(p_2)) {
					// Si ya hay más de uno repetido, curvamos
					if (nfils++ > 0)
						curvar = true;
				}

				// Comprobamos si se encuentra en la misma columna
				if (p.sameColumn(p_2)) {
					// Si ya hay más de uno repetido, curvamos
					if (ncols++ > 0)
						curvar = true;
				}

				// Si tenemos que curvar:
				if (curvar) {
					// Cogemos el punto intermedio del cluster
					Point pmid = centroBoton(matrizBotones[pm.getFila()][pm.getCol()]);

					/*
					 * // Prueba para ver cómo funcionan las posiciones
					 * 
					 * System.out.println("Punto: " + p + " -> " + p1);
					 * System.out.println("Punto intermedio " + pm + " -> " + pmid);
					 * System.out.println("Punto: " + p_2 + " -> " + p2);
					 */

					// Si la columna se encuentra en la primera mitad
					if (p1.x <= pmid.x) {
						pmid.x += pmid.x / 10;
					} else {
						pmid.x -= pmid.x / 10;
					}

					// Si la fila se encuentra en la primera mitad
					if (p1.y <= pmid.y) {
						pmid.y += pmid.y / 10;
					} else {
						pmid.y -= pmid.y / 10;
					}

					// Definimos la línea curva
					QuadCurve2D.Double quad = new QuadCurve2D.Double();
					quad.setCurve(p1, pmid, p2);

					// Y pintamos de negro la línea curva
					Graphics2D g2 = (Graphics2D) g;
					g2.setPaint(Color.BLACK);
					g2.draw(quad);

				} // En caso contrario -> Línea recta
				else {
					// Dibujamos la línea recta
					g.drawLine(p1.x, p1.y, p2.x, p2.y);
				}

			}
			// Borramos del punto final este edge para no repetir
			p_2.getArcosInternos().remove(edge);
		}

	}

	private Point centroBoton(JButton b) {
		// Cogemos las coordenadas del botón
		Point p = b.getLocation();

		// Apuntamos al centro
		p.x += b.getWidth() / 2;
		p.y += b.getHeight() / 2;

		return p;
	}

}

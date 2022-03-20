package program;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

// Creamos un panel similar al mapa, pero con espacios (márgenes) entre cada cluster para crear el grafo abstracto

public class PanelArcos extends JPanel {

	// Matriz con los botones (igual que en mapa)
	private JButton[][] matrizBotones;

	public PanelArcos(Mapa mapa, ArrayList<Cluster> clusters) {
		// 1. Vamos a crear todo el mapa
		crearMapa(mapa, clusters);

	}

	private void crearMapa(Mapa mapa, ArrayList<Cluster> clusters) {
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

}

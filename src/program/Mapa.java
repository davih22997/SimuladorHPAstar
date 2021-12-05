package program;

import java.awt.*;
import javax.swing.*;

public class Mapa extends JFrame {

	// Variables b�sicas del mapa:
	// Cantidad total de filas y de columnas
	int dX = 0; // X -> cols
	int dY = 0; // Y -> fils

	// Dimensiones de cada bot�n en el mapa
	int tamX = 0; // X -> ancho
	int tamY = 0; // Y -> alto

	// Dimensiones en p�xeles del mapa
	static final int dimX = 500; // X -> ancho
	static final int dimY = 500; // Y -> alto

	// Variables de gesti�n del mapa:
	private JButton btnCrear, btnDestruir;
	private JSeparator jSeparator1;
	private JLabel lblDimX, lblDimY;
	private JPanel pnlMenu, pnlTablero;
	private JTextField tbxDimX, tbxDimY;

	// Matriz de botones
	JButton[][] MatrizBotones;

	public Mapa(int fils, int cols) {
		dY = fils;
		dX = cols;
	}

	public void paint(Graphics g) {
		super.paint(g);

		//
	}

	/**
	 * Función que determina si un String corresponde a un número
	 * 
	 * @param s
	 * @return
	 */
	private boolean isNumeric(String s) {
		boolean res = true;

		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			res = false;
		}

		return res;
	}

	/**
	 * Función que valida si los parámetros de entrada son válidos
	 * 
	 * @return
	 */
	private boolean validarDims() {
		// Vble de retorno
		boolean res = false;

		String cols = tbxDimX.getText();

		// Comprobamos que las filas y las columnas sean dadas en numérico
		if (isNumeric(cols)) {
			String fils = tbxDimY.getText();
			if (isNumeric(cols)) {
				// Cantidad de columnas
				dX = Integer.parseInt(cols);
				// Cantidad de filas
				dY = Integer.parseInt(fils);
				// Se verifica que las dimensiones sean mayores que 0
				if (dX > 0 && dY > 0)
					res = true;

			}
		}

		return res;
	}

	/**
	 * Función para obtener las dimensiones de cada botón
	 * 
	 * @param cX Cantidad de botones por fila
	 * @param cY cantidad de botones por columna
	 */
	private void getDimButtons(int cX, int cY) {
		tamX = dimX / cX;
		tamY = dimY / cY;
	}

}

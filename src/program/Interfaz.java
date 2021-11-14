package program;

import javax.swing.*;
import java.awt.*;

// Clase para crear la interfaz gráfica del simulador
public class Interfaz {

	// Rutas de las imágenes que se usarán como iconos
	private final static String start = "/images/start.png";
	private final static String play = "/images/play.png";
	private final static String pause = "/images/pause.png";
	private final static String shutdown = "/images/shutdown.png";
	private final static String stop = "/images/stop.jpg";

	public Interfaz() {
		// ImageIcon st = new ImageIcon(getClass().getResource("/images/play.png"));
		JFrame f = new JFrame("Simulador");
		JButton b = new JButton();

		b.setBounds(100, 100, 100, 40);
		b.setIcon(this.createIcon(stop, b));

		f.add(b);
		f.setSize(300, 400);

		f.setLayout(null);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Método para obtener una imagen que se adapte al tamaño del botón
	 * 
	 * @param url    URL de la imagen
	 * @param button Botón al que se adapta la imagen
	 * @return La imagen adaptada
	 */
	protected ImageIcon createIcon(String url, JButton button) {
		
		return this.setIcon(new ImageIcon(getClass().getResource(url)), button);
	}

	// Modifica un icono para adaptarlo a un botón
	private ImageIcon setIcon(ImageIcon icon, JButton button) {
		// Recoge el ancho y el alto del botón
		int width = button.getWidth();
		int height = button.getHeight();

		// Devuelve la imagen reescalada
		return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
	}

}

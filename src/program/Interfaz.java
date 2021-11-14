package program;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.*;

import java.awt.*;

// Clase para crear la interfaz gráfica del simulador
public class Interfaz extends JFrame {

	// private JPanel contentPane;

	// Rutas de las imágenes que se usarán como iconos
	private final static String start = "/images/start.png";
	private final static String play = "/images/play.png";
	private final static String pause = "/images/pause.png";
	private final static String shutdown = "/images/shutdown.png";
	private final static String stop = "/images/stop.jpg";

	public Interfaz() {
		// ImageIcon st = new ImageIcon(getClass().getResource("/images/play.png"));
		// Definimos los parámetros de la aplicación (título, tamaño...):
		this.setTitle("Simulador");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setBounds(100, 100, 600, 350);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(contentPane);

		// Definimos el primer submenú (ALGORITMO):
		Box vB1 = Box.createVerticalBox();
		vB1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		JLabel lbl1 = new JLabel("Algoritmo");
		lbl1.setAlignmentX(Component.CENTER_ALIGNMENT);
		vB1.add(lbl1);

		JComboBox<String> cb1 = new JComboBox<>();
		cb1.addItem("A*");
		cb1.addItem("HPA*");
		cb1.setToolTipText("");
		vB1.add(cb1);

		// Definimos el segundo submenú (HEURÍSTICO):
		Box vB2 = Box.createVerticalBox();
		vB2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		JLabel lbl2 = new JLabel("Heurístico");
		lbl2.setAlignmentX(Component.CENTER_ALIGNMENT);
		vB2.add(lbl2);

		JComboBox<String> cb2 = new JComboBox<>();
		cb2.addItem("DIST-MANHATAN");
		cb2.addItem("DIST-EUCLIDEA");
		cb2.addItem("H=0");
		vB2.add(cb2);

		// Definimos el tercer menú (CONTROL SIMULACIÓN):
		Box vB3 = Box.createVerticalBox();
		vB3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		JLabel lbl3 = new JLabel("Control simulación");
		lbl3.setAlignmentX(Component.CENTER_ALIGNMENT);
		vB3.add(lbl3);

		JPanel p1 = new JPanel();
		vB3.add(p1);

		JSplitPane sp1 = new JSplitPane();
		p1.add(sp1);

		JButton b1 = new JButton("Play");
		sp1.setLeftComponent(b1);

		JButton b2 = new JButton("Stop");
		sp1.setRightComponent(b2);

		JLabel jl1 = new JLabel("Velocidad");
		p1.add(jl1);

		JSplitPane sp2 = new JSplitPane();
		p1.add(sp2);

		JButton b3 = new JButton("-");
		sp2.setLeftComponent(b3);

		JButton b4 = new JButton("+");
		sp2.setRightComponent(b4);

		// Lo añadimos todo al mismo GroupLayout
		GroupLayout gl = new GroupLayout(contentPane);
		gl.setHorizontalGroup(gl.createParallelGroup(Alignment.LEADING).addGroup(gl.createSequentialGroup()
				.addContainerGap()
				.addGroup(gl.createParallelGroup(Alignment.LEADING).addGroup(gl.createSequentialGroup()
						.addComponent(vB1, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE).addGap(165)
						.addComponent(vB2, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)).addComponent(
								vB3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		gl.setVerticalGroup(gl.createParallelGroup(Alignment.LEADING)
				.addGroup(gl.createSequentialGroup()
						.addGroup(gl.createParallelGroup(Alignment.LEADING)
								.addComponent(vB2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(vB1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(vB3, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(204)));
		// Lo añadimos como contenido de la aplicación:
		contentPane.setLayout(gl);

		/*
		 * JButton b = new JButton();
		 * 
		 * b.setBounds(100, 100, 100, 40); b.setIcon(this.createIcon(stop, b));
		 * 
		 * this.add(b); this.setSize(300, 400);
		 * 
		 * this.setLayout(null); this.setVisible(true);
		 */
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

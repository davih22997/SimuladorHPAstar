package program;

import java.awt.EventQueue;

public class Simulador {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					new Interfaz();
					//new Test(1, new Punto(129, 206), new Punto(193, 99));
					//new Test();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}

}

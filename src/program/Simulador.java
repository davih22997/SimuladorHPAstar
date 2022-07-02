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
					// new Test();
					// new Test(Test.MODO_ERROR_Astar, new Punto(35, 148), new Punto(267, 86));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}

}

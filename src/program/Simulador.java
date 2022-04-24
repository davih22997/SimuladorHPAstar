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
					//System.out.println("Se va a realizar A*");
					//new Test(Test.MODO_ERROR_Astar, new Punto(98,233), new Punto(173,84));
					//System.out.println("Se va a realizar HPA*");
					//new Test(Test.MODO_ERROR_HPAstar, new Punto(206, 110), new Punto(173,84));
					//new Test();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}

}

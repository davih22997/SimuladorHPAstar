package program;

import java.awt.*;
import javax.swing.*;

public class Simulador {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Interfaz i = new Interfaz();
					i.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}

}

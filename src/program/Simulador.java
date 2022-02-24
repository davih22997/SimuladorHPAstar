package program;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Simulador {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Interfaz i = new Interfaz();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}

}

package com.chancho.catan;
import java.awt.EventQueue;

import javax.swing.JFrame;

public class Main extends JFrame{
	private static final long serialVersionUID = 4648172894076113183L;

	public Main() {
		initUI();
	}
	private void initUI() {
		add(new Board());
		setResizable(false);
		pack();
		
		setTitle("Settlers of Catan");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			Main catan = new Main();
			catan.setVisible(true);
		});
	}
}

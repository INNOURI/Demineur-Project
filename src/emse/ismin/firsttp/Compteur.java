package emse.ismin.firsttp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Compteur extends JPanel implements Runnable {
	private int cpt = 0;
	private static int WIDTH = 60;
	private static int HEIGHT = 20;
	private Thread th;
	
	Compteur () {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}
	
	public int getVal() {
		return cpt;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(th != null) {
			try {
				Thread.sleep(1000);
				cpt++;
				repaint();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void paintComponent(Graphics gc) {
		super.paintComponent(gc);
		gc.setColor(new Color (255, 87, 34)); //material orange
		gc.setFont(new Font("papyrus", Font.BOLD,12));
		gc.drawString(String.valueOf(cpt), getWidth()/2, getHeight()/2);
		gc.setColor( new Color(0, 150, 136)); //material orange
		gc.drawRect(0, 0, getWidth()-1, getHeight()-1);
	}
	
	/**
	 * démarrage du compt
	 */
	public void stratCpt() {
		cpt = 0;
		th = new Thread(this);
		th.start();
	}
	
	/**
	 * stop du compt
	 */
	public void stopCpt() {
		th = null;
	}

}

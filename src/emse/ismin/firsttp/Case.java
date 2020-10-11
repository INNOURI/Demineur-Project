package emse.ismin.firsttp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Case extends JPanel implements MouseListener 
{
	private int x;
	private int y;
	private Demineur demin;
	private boolean clicked;
	private int whoClicked;
	
	private final static int SIZE=30 ;
	
	public Case(int x, int y , Demineur demin) {
		setPreferredSize(new Dimension(SIZE, SIZE));
		addMouseListener(this);
		this.x = x;
		this.y = y;
		this.demin = demin;
		whoClicked = this.demin.getIndex();
	}
	
	public void setClicked(boolean bool) {
		this.clicked = bool;
	}
	
	public boolean getClicked() {
		return clicked;
	}
	
	public void setWhoClicked(int index) {
		whoClicked = index;
	}
	
	/**
	 * 
	 */
	public void paintComponent(Graphics gc) { 
		super.paintComponent(gc); 
		
		if (!clicked) {
			//case grise au depart
			gc.setColor(new Color(158,158,158));
			gc.fillRect(1, 1, getWidth(), getHeight());
		} else {
			if (demin.getChamp().isMin(x, y)) {
				try {
					BufferedImage image= ImageIO.read(( new File("img/bomb.png")));
					gc.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);		
				} catch(IOException e) {
					e.printStackTrace();		
				}  
			} else {
				if (demin.getChamp().nbMinesAutour(x, y) == 0) {
					gc.setColor(Demineur.colorList[whoClicked]);
					gc.fillRect(1, 1, getWidth(), getHeight());
				} else {
					gc.setColor(Demineur.colorList[whoClicked]);
					gc.fillRect(0, 0, getWidth(),getHeight());
					gc.setColor(new Color(255, 255, 255));
					gc.drawString(
						String.valueOf(demin.getChamp().nbMinesAutour(x, y))
						,getWidth()/2
						,getHeight()/2
					);
				}
			}
		}
	}

	@Override
	public void mouseClicked (MouseEvent arg0) {
		//inc le nb de mines decouvertes 
		if (!clicked && !demin.getChamp().isMin(x, y) && !demin.isLost() ) {
			demin.setMesCasesDecouvertes(demin.getMesCasesDecouvertes() + 1);
			demin.getGui().getScoreField().setText("Score : " + demin.getMesCasesDecouvertes());
		}

		if (!clicked) {
			//partie pas perdue
			if (!demin.isLost()) {
				clicked = true;
				if (this.demin.sock != null) {
					this.demin.sendCase(x, y);
				}
				
				repaint();
				
				//tombe sur une mine
				if (demin.getChamp().isMin(x, y)) {
					demin.setLost(true);
					demin.getGui().getCompteur().stopCpt();
					if (this.demin.sock != null) {
						this.demin.lostPartie();
					}
					JOptionPane.showMessageDialog(null,"You loose!!");
					Thread th = new Thread(() -> {
						demin.saveLevel();
					});
					th.start();
				}
			}
		}
		
		//victoire!!
		if (demin.isWin()) {
			demin.getGui().getCompteur().stopCpt();
			JOptionPane.showMessageDialog(null, "Partie terminée!!\n Score :" + demin.getMesCasesDecouvertes());
			Thread th = new Thread(() -> {
				demin.saveLevel();
			});
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * reinitialisation de la case
	 */
	
	public void newPartie() {
		// TODO Auto-generated method stub
		clicked = false;
		repaint();
	}
	
}

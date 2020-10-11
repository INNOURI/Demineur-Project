package emse.ismin.firsttp;

import java.awt.Color;
import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Demineur extends JFrame implements Runnable {
	public static Color[] colorList = {Color.red, Color.blue, Color.green, Color.black, Color.yellow, Color.pink, Color.orange};
	private static final String FILENAME = "scores.txt";
	private static final String LAST_LEVEL = "level.txt";
	public static final int PORT = 10000;

	public static final String HOSTNAME = "LOCALHOST";
	public static final int MSG = 0;
	public static final int POS = 1;
	public static final int START = 2;
	public static final int END = 3;
	public static final int NEW = 4;
	public static final int QUIT = 5;
	public static final int LOST = 6;
	
	private int index;
	private String pseudo = "Joueur";
	private int nbCasesDecouvertes = 0;
	private int mesCasesDecouvertes = 0;
	private boolean started;
	private boolean lost;
	
	Socket sock = null;
	DataInputStream in;
	DataOutputStream out;

	private Gui gui;
	
	private Champ champ;
	
	Thread process = new Thread (this);
	
	/**
	 * construction du tout
	 * 
	 */
	public Demineur() {
		super("Demineur de la mort qui tue");
		Level level = getLevel();
		champ = new Champ(level);
		
		//champ.placeMine();
		
		gui = new Gui(this);
		 
		//Case case = new Case(this);
		setContentPane(gui);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		pack();
		setVisible(true);
	}
	
	/**
	 * return if the party is started
	 * @return
	 */
	public boolean isStrated() {
		return started;
	}
	
	/**
	 * 
	 * @param started
	 */
	public void setStarted(boolean started) {
		this.started = started;	
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getPseudo() {
		return pseudo;
	}
	
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	
	public Gui getGui() {
		return gui;
	}

	public Champ getChamp() {
		return champ;
	}

	public void setChamp(Champ champ) {
		this.champ = champ;
	}
	
	public int getMesCasesDecouvertes() {
		return mesCasesDecouvertes;
	}
	
	public void setMesCasesDecouvertes(int mesCasesDecouvertes) {
		this.mesCasesDecouvertes = mesCasesDecouvertes;
	}
	
	public int getNbCasesDecouvertes() {
		return nbCasesDecouvertes;
	}
	
	public void setNbCasesDecouvertes(int nbCasesDecouvertes) {
		this.nbCasesDecouvertes = nbCasesDecouvertes;
	}
	
	public boolean isLost() {
		return lost;
	}
	
	public void setLost(boolean lost) {
		this.lost = lost;
	}
	
	public void newPartie() {
		 setStarted(false);
		 setLost(false);
		 nbCasesDecouvertes=0; 
	}
	
	public boolean isWin() {
		boolean win = getMesCasesDecouvertes()+champ.getNbMines() == champ.getDimX()*champ.getDimY();
		if (win && sock != null) {
			endPartie();
		}
		return win;
	}
	
	public void endPartie() {
		try {
			out.writeInt(END);
		} catch (UnknownHostException e) {
			System.out.println("Le serveur est inconnu");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public Level getLevel() {
		Path path= Paths.get(LAST_LEVEL);
		if (Files.exists(path)) {
			try {
				String s_level = new String(Files.readAllBytes(path));
				return Level.valueOf(s_level);
			} catch(IOException e) {
				System.out.println("SCORE NOT REGISTRED");
				e.printStackTrace();
				return Level.MEDIUM;
			}
		} else {
			return Level.MEDIUM;
		}
	}
	
	public void saveLevel() {
		Path path= Paths.get(LAST_LEVEL);
		String score = new String(this.getChamp().getLevel().toString());
		byte [] tabBytes = score.getBytes();
		try {
			Files.write(path, tabBytes);
		} catch(IOException e) {
			System.out.println("SCORE NOT REGISTRED");
			e.printStackTrace();
		}
	}
	
	public void sendCase(int x, int y) {
		try {
			out.writeInt(POS);
			out.writeInt(x);
			out.writeInt(y);
			out.writeInt(mesCasesDecouvertes);
		} catch (UnknownHostException e) {
			System.out.println("Le serveur est inconnu");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void quitterPartie() {
		try {
			out.writeInt(QUIT);
		} catch (UnknownHostException e) {
			System.out.println("Le serveur est inconnu");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void lostPartie() {
		try {
			out.writeInt(LOST);
		} catch (UnknownHostException e) {
			System.out.println("Le serveur est inconnu");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Demineur();
	}
	
	/**
	 * 
	 * @param hostname
	 * @param port
	 * @param pseudo
	 */
	public  void connect2Server(String hostname, int port, String pseudo){
		System.out.println("try to connect:"+hostname+","+port);
		gui.addMsg("try to connect:"+hostname+","+port);
		
		try {
			sock = new Socket(hostname, port);
			gui.addMsg("connexion ok ");
			
			in = new DataInputStream(sock.getInputStream());
			out = new DataOutputStream(sock.getOutputStream());
			out.writeUTF(pseudo);
			out.writeInt(champ.getNbMines());
			out.writeInt(champ.getDimX());
			out.writeInt(champ.getDimY());
			
			index = in.readInt();
			
			process.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			gui.addMsg("connexion impossible");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//boucle attente des 
	public void run() {
		//boucle infinie
		while (process != null) {
			int cmd;
			try {
				cmd = in.readInt();
				if(cmd == MSG) {
					String msg = in.readUTF();
					gui.addMsg("\n"+msg);
				} else if (cmd == POS) {
					int x = in.readInt();
					int y = in.readInt();
					int ind = in.readInt();
					setNbCasesDecouvertes(getNbCasesDecouvertes() + 1);
					this.getGui().getTabCases()[x][y].setClicked(true);
					this.getGui().getTabCases()[x][y].setWhoClicked(ind);
					this.getGui().getTabCases()[x][y].repaint();
				} else if (cmd == START) {
					champ.getRandomList().clear();
					for (int i=0 ; i < (champ.getNbMines() * 2) ; i++) {
						int pos = in.readInt();
						champ.getRandomList().add(pos);
					}
					Level level = getLevel();
					this.getChamp().newPartie(level);
					getGui().getCompteur().stratCpt();
					setStarted(true);
					setLost(false);
				} else if (cmd == NEW) {
					String clientPseudo = in.readUTF();
					gui.addMsg("\n" + clientPseudo + " vient de se connecter!");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//en fct de ce que je lis , j'affiche les mines,num, fin partie	
		}
	}
	
}

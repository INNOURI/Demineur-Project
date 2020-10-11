package emse.ismin.firsttp;
import javax.swing.JPanel;
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.GridLayout;
import java.awt.event.* ;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.* ; 
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Graphics;
import java.awt.GridBagLayout;

public class Serveur extends JFrame
{
	public static final String SCORES = "scores.txt";
	public static final int MSG = 0;
	public static final int POS = 1;
	public static final int START = 2;
	public static final int END = 3;
	public static final int NEW = 4;
	public static final int QUIT = 5;
	public static final int LOST = 6;
	
	public static int maxScore = 0;
	public static String winner;
	public static int nbCasesDecouvertes = 0;
	
	private Random alea = new Random ();
	public List<Integer> randomListEasy = new ArrayList<>();
	public List<Integer> randomListMedium = new ArrayList<>();
	public List<Integer> randomListHard = new ArrayList<>();
	private int nbMines;
	private int dimensionX;
	private int dimensionY;
	
	private static ServeurGui gui;
	private ServerSocket serverSocket;
	
	public DataInputStream in;
	public DataOutputStream out;
	
	Set<ServeurThread> threads = new HashSet<>();
	public static int clients = 0;
	
	Serveur () {
		//ServerSocket serverSocket;
		System.out.println("DÃ©marrage du Serveur");
		gui = new ServeurGui(this);
		setContentPane(gui);
		pack();//Redimentionner la frame
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		serve();
	}
	
	public static void main(String[] args) {	
		new Serveur();
	}
	
	void partieMines(int nbMines, int dimension) {
		
		for (int i=0 ; i< (nbMines*2) ; i++) {
			if (nbMines == 3) {
				randomListEasy.add(alea.nextInt(dimension));
			} else if (nbMines == 10) {
				randomListMedium.add(alea.nextInt(dimension));
			} else if (nbMines == 50) {
				randomListHard.add(alea.nextInt(dimension));
			}
		}
	}

	public void serve() {
		gui.addMsg("Attente des clients");
		partieMines(3, 3);
		partieMines(10, 10);
		partieMines(50, 20);
		try {
			serverSocket = new ServerSocket(Demineur.PORT);
			while (true) {
				Socket socket = serverSocket.accept();
	            DataInputStream in = new DataInputStream(socket.getInputStream());
	            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	            String pseudo = in.readUTF();
	            nbMines = in.readInt();
	            dimensionX = in.readInt();
	            dimensionY = in.readInt();

	            ServeurThread thread = new ServeurThread(socket, pseudo, threads.size());
	            threads.add(thread);
	            clients += 1;
	            gui.clientsPanel.setText("Nombre de clients: " + Serveur.clients);
	            out.writeInt(threads.size() - 1);
	            
	            for (ServeurThread th : threads) {
					DataOutputStream threadOut = new DataOutputStream(th.getSocket().getOutputStream());
					threadOut.writeInt(NEW);
					threadOut.writeUTF(thread.getPseudo());
				}
	            thread.start();
	            gui.addMsg("\nNew client: " + threads.size());
	        }

		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void lancerPartie() {
		try {
			for (ServeurThread thread : threads) {
				DataOutputStream threadOut = new DataOutputStream(thread.getSocket().getOutputStream());
				threadOut.writeInt(START);
				for (int i=0 ; i<(nbMines * 2) ; i++) {
					if (nbMines == 3) {
						threadOut.writeInt(randomListEasy.get(i).intValue());
					} else if (nbMines == 10) {
						threadOut.writeInt(randomListMedium.get(i).intValue());
					} else if (nbMines == 50) {
						threadOut.writeInt(randomListHard.get(i).intValue());
					}
				}
				threadOut.writeInt(MSG);
				threadOut.writeUTF("La partie a commencée!");
			}
			gui.addMsg("\nLa partie a commencée!");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public boolean isWin() {
		boolean win = nbCasesDecouvertes+nbMines == dimensionX * dimensionY;
		return win;
	}
	
	public void saveLine(int index, String pseudo, int score) {
		Path path = Paths.get(SCORES);
		String line = new String(index + " - " + pseudo + " , Score : " + score + System.lineSeparator());
		byte [] tabBytes = line.getBytes();
		try {
			Files.write(path, tabBytes, StandardOpenOption.APPEND);
		} catch(IOException e) {
			System.out.println("SCORE NOT REGISTRED");
			e.printStackTrace();
		}
	}
	
	public void partieTerminee() throws IOException {
		for (ServeurThread thread : threads) {
			DataOutputStream threadOut = new DataOutputStream(thread.getSocket().getOutputStream());
			threadOut.writeInt(MSG);
			threadOut.writeUTF(winner + " est le vainqueur de cette partie avec le score: " + maxScore);
		}
		gui.addMsg("\n" + winner + " est le vainqueur de cette partie avec le score: " + maxScore);
		
		// Enregistrer dans le fichier
		Path path = Paths.get(SCORES);
		String vide = "";
		byte [] tabBytes = vide.getBytes();
		Files.write(path, tabBytes);
		for (ServeurThread thread : threads) {
			saveLine(thread.getIndex(), thread.getPseudo(), thread.getScore());
		}
	}
	
	public class ServeurThread extends Thread {
		private String pseudo;
		private int index;
		private Socket socket;
		private int score;
		
		public ServeurThread(Socket socket, String pseudo, int index) {        
            this.socket = socket;      
            this.index = index;
            if (pseudo.equals("Joueur")) {
            	this.pseudo = pseudo + " " + index;
            } else {
                this.pseudo = pseudo;
            }
        }
		
		public Socket getSocket() {
			return socket;
		}
		
		public int getIndex() {
			return index;
		}
		
		public String getPseudo() {
			return pseudo;
		}
		
		public int getScore() {
			return score;
		}

		@Override
		public void run() {
			DataInputStream in = null;
			DataOutputStream out = null;
			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (true) {
                try {
                    int cmd = in.readInt();
                    if (cmd == POS) {
						int x = in.readInt();
						int y = in.readInt();
						score = in.readInt();
						nbCasesDecouvertes += 1;
						if (score > maxScore) {
							maxScore = score;
							winner = pseudo;
						}
						for (ServeurThread thread : threads) {
							DataOutputStream threadOut = new DataOutputStream(thread.getSocket().getOutputStream());
							threadOut.writeInt(POS);
							threadOut.writeInt(x);
							threadOut.writeInt(y);
							threadOut.writeInt(index);

							threadOut.writeInt(MSG);
							threadOut.writeUTF(pseudo + " a cliqué sur la case " + x + " , " + y);
						}
						gui.addMsg("\n" + pseudo + " a cliqué sur la case " + x + " , " + y);
						if (isWin()) {
							partieTerminee();
						}
					} else if (cmd == END) {
						partieTerminee();
					} else if (cmd == QUIT) {
						for (ServeurThread thread : threads) {
							DataOutputStream threadOut = new DataOutputStream(thread.getSocket().getOutputStream());
							threadOut.writeInt(MSG);
							threadOut.writeUTF(pseudo + " a quitté la partie!");
						}
						gui.addMsg("\n" + pseudo + " a quitté la partie!");
						Serveur.clients -= 1;
						if (clients == 0) {
							partieTerminee();
						}
					} else if (cmd == LOST) {
						for (ServeurThread thread : threads) {
							DataOutputStream threadOut = new DataOutputStream(thread.getSocket().getOutputStream());
							threadOut.writeInt(MSG);
							threadOut.writeUTF(pseudo + " a perdu!");
						}
						gui.addMsg("\n" + pseudo + " a perdu!");
						Serveur.clients -= 1;
						if (clients == 0) {
							partieTerminee();
						}
					}
                } catch (Exception e) {
                    break;
                }
            }
		}
		
	}
}

package emse.ismin.firsttp;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import emse.ismin.firsttp.Serveur.ServeurThread;

public class Champ 
{
	public final static int NBMINESEASY = 3;
	public final static int NBMINESMEDIUM = 10;
	public final static int NBMINESHARD = 50;
	private final static int DIMEASY = 3;
	private final static int DIMMEDIUM = 10;
	private final static int DIMHARD = 20;
	private int nbMines= NBMINESEASY;
	private Level level;
	
	private final static int NBMINES = 3;
	private boolean [ ] [ ] tabMines;
	private Random alea = new Random ();
	private int[] randomArray = {1, 0, 0, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8, 0, 9, 0};
	
	public List<Integer> randomList = new ArrayList<>();
	
	public Champ() {
		this(Level.EASY);
	}
	
	public Champ(Level level) {
		this.level = level;
		newPartie(level);
	}
	
	public Level getLevel() {
		return this.level;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	public int getDimX() {
		return tabMines.length;
	}
	
	public int getDimY() {
		return tabMines[0].length;
	}
	
	public int getNbMines() {
		return nbMines;
	}
	
	public List<Integer> getRandomList() {
		return this.randomList;
	}
	
	public boolean isMin(int x, int y) {
		return tabMines[x][y];
	}
		
	private void initChamp(int x , int y) {
		//creation du tableau 
		tabMines= new boolean [x][y];
		//placement des mines 
		if (randomList.size() == 0) {
			placeMine();
			System.out.println("Demineur mode solo:");
			afftext();
		}else {
			placeMinesServeur();
			System.out.println("Demineur mode partie:");
			afftext();
		}
	}
	
	void placeMinesServeur( ) {
		int i = 0;
		int nbMinesRestantes= nbMines;
		while (nbMinesRestantes > 0) {
			int x = randomList.get(i).intValue(); 
			int y = randomList.get(i+1).intValue(); 
			tabMines[x][y]= true;
			i += 2;
			nbMinesRestantes--;
		}
	}
	
	void placeMine() {
		
		for (int nbMinesRestantes= nbMines ; nbMinesRestantes > 0;) {
			int x = alea.nextInt(tabMines.length) ; 
			int y = alea.nextInt(tabMines[0].length) ; 
			if(!tabMines[x][y]) {
				tabMines[x][y]= true;
				nbMinesRestantes--;
			}
		}
	}
	
	/**
	 * calcul le nombre de mines autour de la position x,y
	 * @param x
	 * @param y
	 * @return
	 */
	public int nbMinesAutour(int x,int y) {
		int nbMines=0;
		int borneMinI, borneMinJ, borneMaxI, borneMaxJ;
		borneMinI = (x == 0) ? 0 : x-1;
		borneMinJ = (y == 0) ? 0 : y-1;
		borneMaxI = (x == tabMines.length-1) ? tabMines.length : x+2;
		borneMaxJ = (y == tabMines[0].length-1) ? tabMines[0].length : y+2;
		for(int i = borneMinI ; i<borneMaxI ; i++)
			for(int j = borneMinJ ; j<borneMaxJ ; j++) {
				if(!(i==x && j==y ) && tabMines[i][j])
					nbMines++;
			}
		return nbMines; 
	}
	
	public void afftext() {
		for (int i=0 ; i<tabMines.length ; i++) {
			for (int j=0 ; j<tabMines[0].length ; j++) 
				if (tabMines[i][j] == true)
					System.out.print("x");
				else
					System.out.print(nbMinesAutour(i,j));
			System.out.println();
		}
		/**
		 * retourn s'il y a une mines 
		 * @parm x
		 * @parm y
		 * return mines or not
		 */
	}
	
	public void newPartie(Level level) {
		if (level == Level.EASY ) {
			nbMines=NBMINESEASY;
			initChamp(DIMEASY,DIMEASY);
		}
			
		else if (level == Level.MEDIUM) {
				nbMines=NBMINESMEDIUM;
				initChamp(DIMMEDIUM,DIMMEDIUM);
			}
		else if (level == Level.HARD) {
				nbMines=NBMINESHARD;
				initChamp(DIMHARD,DIMHARD);
			}
	}
}


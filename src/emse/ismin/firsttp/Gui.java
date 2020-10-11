package emse.ismin.firsttp;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Gui extends JPanel implements ActionListener 
{
	private JButton butQuit = new JButton("Quit") ;//Les mettre en attributs pour qu'ils soient accessible dans la classe
	private JMenuItem mQuit = new JMenuItem("Quitter",KeyEvent.VK_Q);//
	private JMenuItem mNew = new JMenuItem("Nouvelle Partie",KeyEvent.VK_Q);//
	private JMenuItem mEasy = new JMenuItem("Easy",KeyEvent.VK_E);
	private JMenuItem mMedium = new JMenuItem("Medium",KeyEvent.VK_E);
	private JMenuItem mHard = new JMenuItem("Hard",KeyEvent.VK_E);
	private JPanel panelMines = new JPanel();
	private JPanel panelConnexion = new JPanel();
	private JTextField hostField = new JTextField(Demineur.HOSTNAME,10);
	private JTextField portField = new JTextField(String.valueOf(Demineur.PORT),10);
	private JTextField pseudoField = new JTextField("",15);
	private JTextField scoreField = new JTextField("Score : 0",15);
	private JButton connexionBut = new JButton("Connexion") ;

	private Demineur Demin;
	
	private Case [][] tabCases;
	private Compteur compteur;
	private JPanel panelSouth;
	private JTextArea msgArea;
	private JScrollPane scroll;
	
	public Compteur getCompteur() {
		return compteur;
	}
	
	public JTextField getScoreField() {
		return scoreField;
	}
	
	public Case[][] getTabCases() {
		return tabCases;
	}


//private Graphics gc=new Graphics();
	//private JButton mQuit=new JButton("Quitter");
/** constructeuraddActionListener
* @param la classe contenant les traitements
*/
	Gui (Demineur demin) {
		setLayout(new BorderLayout());
		setBackground(Color.cyan);
		
		JPanel panelNorth = new JPanel();
		add(panelNorth,BorderLayout.NORTH);
		
		JLabel title = new JLabel("Welcome on board");
		title.setForeground(Color.DARK_GRAY);
		title.setFont(new Font ("papyrus", Font.ITALIC, 20));
		panelNorth.add(title, BorderLayout.NORTH);
		compteur = new Compteur();
		panelNorth.add(compteur, BorderLayout.CENTER);
		
		scoreField.setEditable(false);
		
		panelConnexion.add(new JLabel("serveur"));
		panelNorth.add(panelConnexion, BorderLayout.SOUTH);
		panelConnexion.add(hostField);
		panelConnexion.add(portField);
		panelConnexion.add(pseudoField);
		panelConnexion.add(scoreField);
		panelConnexion.add(connexionBut);
		connexionBut.addActionListener(this);
		
		//pseudoField.add(connexionBut);
		panelNorth.revalidate();
		
		title.setHorizontalTextPosition(JLabel.CENTER);
		//Case case_demineur=new Case();
		
		//case_demineur.paintComponent(gc);
		this.Demin=demin;
		placeCases();
		
		pseudoField.setText(demin.getPseudo());
		 
		add(panelMines,BorderLayout.CENTER);
		//add(title,BorderLayout.NORTH);
		panelSouth= new JPanel(new BorderLayout());
		
		msgArea = new JTextArea(10,20);
		msgArea.append("bonne partie");

		JScrollPane scr = new JScrollPane(msgArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		msgArea.setBounds(5, 35, 385, 330);
		msgArea.setLineWrap(true);
		msgArea.setWrapStyleWord(true);
        scr.setBounds(20, 30, 100, 40);
        panelSouth.add(scr);
		
		butQuit.setForeground(Color.DARK_GRAY);
		butQuit.setFont(new Font("Papyrus", Font.PLAIN,18));
		butQuit.addActionListener(this);
		
		panelSouth.add(butQuit,BorderLayout.SOUTH);
		add(panelSouth,BorderLayout.SOUTH);
		//add(butQuit,BorderLayout.SOUTH);
		
		JMenuBar menuBar=new JMenuBar();
		
		//Le menu Partie
		JMenu menuPartie=new JMenu("Partie");
		menuBar.add(menuPartie);
		JMenuItem mAide=new JMenuItem("Aide",KeyEvent.VK_Q);
		//menuPartie.add(mQuit);
		menuBar.add(Box.createGlue());//To add between the 2 adds
		
		mQuit.addActionListener(this);
		mNew.addActionListener(this);
		
		menuPartie.add(mQuit);
		menuPartie.add(mNew);
		Demin.setJMenuBar(menuBar);
		JMenu menuHelp=new JMenu("Aide");
		menuBar.add(menuHelp);
		menuHelp.add(mAide);
		//Raccourci clavier  à partir du menu
		JMenuItem itemExit= new JMenuItem("Exit",KeyEvent.VK_E);
		//Raccourci clavier  à partir de la fenêtre
		
		mQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,ActionEvent.CTRL_MASK));
		
		//Activation désactivation du menu
		mQuit.setEnabled(true);//Pour activer désactiver
		mQuit.addActionListener(this);
		mEasy.addActionListener(this);
		mMedium.addActionListener(this);
		mHard.addActionListener(this);
		
		//Level
		JMenu menuLevel=new JMenu("Niveaux");
		menuLevel.add(mEasy);
		menuLevel.add(mMedium);
		menuLevel.add(mHard);
		
		menuPartie.add(menuLevel);
		
		//JMenuItem mnewpartie=new JMenuItem("Nouvelle partie",KeyEvent.VK_Q);
		menuPartie.add(mQuit);
	}
	 
	 
	public void actionPerformed(ActionEvent e) {
		//Level lev=Level();
		if (e.getSource()==butQuit  || e.getSource()==mQuit) {
			int reponse=JOptionPane.showConfirmDialog(null, "êtes-vous sûrs?","Bye-Bye",JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE);
			if( reponse==JOptionPane.YES_OPTION) {
				//Si l'utilisteur a appuyé sur oui
				if (Demin.sock != null)
					Demin.quitterPartie();
				System.exit(0);
			}
				
		} else if(e.getSource()==mNew) {
			Demin.getChamp().placeMine();
			newPartie();
		} else if(e.getSource()==mEasy) {
			Demin.getChamp().setLevel(Level.EASY);
			Demin.getChamp().newPartie(Level.EASY);
			newPartie(Level.EASY);
		} else if(e.getSource()==mMedium) {
			Demin.getChamp().setLevel(Level.MEDIUM);
			Demin.getChamp().newPartie(Level.MEDIUM);
			newPartie(Level.MEDIUM);				
		} else if(e.getSource()==mHard) {
			Demin.getChamp().setLevel(Level.HARD);
			Demin.getChamp().newPartie(Level.HARD);
			newPartie(Level.HARD);
		} else if(e.getSource()==connexionBut) {
			Demin.setPseudo(pseudoField.getText());
			Demin.connect2Server(hostField.getText(),Integer.parseInt(portField.getText()),pseudoField.getText());
		} 
	}
	 
	private void newPartie() {
		 for(int i=0;i<Demin.getChamp().getDimX();i++)
			 	for(int j=0;j<Demin.getChamp().getDimY();j++)
			 		tabCases[i][j].newPartie();
	}
	 
	private void newPartie(Level l) {
		panelMines.removeAll();
		placeCases();
	 	Demin.pack();
	 	compteur.stopCpt();
		Demin.newPartie();
	 	compteur.stopCpt();
		Demin.setStarted(false);
		Demin.setLost(false);
	}
	 
	private void  placeCases() {
		int X = Demin.getChamp().getDimX();
		int Y = Demin.getChamp().getDimY();
		panelMines.setLayout(new GridLayout(X,Y));
	
		tabCases = new Case [Demin.getChamp().getDimX()] [Demin.getChamp().getDimY()];
		for (int j=0 ; j<Y ; j++) {	
			for (int i=0 ; i<X ; i++) {
				tabCases[i][j]=new Case(i,j,Demin);
				panelMines.add(tabCases[i][j]);
			}
		}
	}
	
	public void addMsg(String str) {
		msgArea.append(str);	
	}
	 
}
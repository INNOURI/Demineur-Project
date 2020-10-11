package emse.ismin.firsttp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ServerSocket;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServeurGui extends JPanel implements ActionListener
{
	private JButton startBut = new JButton("start");
	private Serveur serveur;
	private JPanel header = new JPanel();
	private JTextArea msgArea = new JTextArea(20,20);
	public JTextField clientsPanel = new JTextField("Nombre de clients: " + Serveur.clients, 20);
	
	/**
	 * 
	 * @param serveur
	 */

	ServeurGui (Serveur serveur) {
		this.serveur = serveur;
		
		setLayout( new BorderLayout());
		header.add(new JLabel("Serveur Demineur 2019"), BorderLayout.NORTH);
		clientsPanel.setEditable(false);
		header.add(clientsPanel, BorderLayout.SOUTH);
		add(header, BorderLayout.NORTH);
		msgArea.setEditable(false);
		JScrollPane scroll = new JScrollPane (msgArea, 
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(scroll, BorderLayout.CENTER);
		// add(msgArea,BorderLayout.CENTER);
		add(startBut,BorderLayout.SOUTH);
		startBut.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		//Level lev=Level();
		if (e.getSource() == startBut) {
			serveur.lancerPartie();
		}
	}
	
	public void addMsg (String str) {
		msgArea.append(str);
	}
	
}

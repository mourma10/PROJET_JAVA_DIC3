package client;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import chatroom.*;
import chatroom.Serveur;

public class Client {
	private String title = "Logiciel de discussion en ligne";
    private String pseudo = null;

    private JFrame window = new JFrame(this.title);
    private JTextArea txtOutput = new JTextArea();
    private JTextField txtMessage = new JTextField();
    private JButton btnSend = new JButton("Envoyer");
    String txt;
    Serveur serveur;
    public Client() {
    	serveur = this.initServeur();
        this.createIHM();
        this.requestPseudo();
    }
    
    public Serveur initServeur() {
    	ServeurServiceLocator ssl = new ServeurServiceLocator();
    	Serveur serveur = null;
    	try {
    	    serveur = ssl.getServeur();
    	    return serveur;
    	}catch(Exception ex) {
    	    ex.printStackTrace();
    	}
    	return serveur;
    }

    public void createIHM() {
        // Assemblage des composants
        JPanel panel = (JPanel)this.window.getContentPane();
        JScrollPane sclPane = new JScrollPane(txtOutput);
        panel.add(sclPane, BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(this.txtMessage, BorderLayout.CENTER);
        southPanel.add(this.btnSend, BorderLayout.EAST);
        panel.add(southPanel, BorderLayout.SOUTH);

        // Gestion des évènements
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                deconnecter(e);

            }
        });
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnSend_actionPerformed(e);
            }
        });
        txtMessage.addKeyListener(new KeyAdapter() {
        public void keyReleased(KeyEvent event) {
        if (event.getKeyChar() == '\n')
            btnSend_actionPerformed(null);
        }
    });

        // Initialisation des attributs
        this.txtOutput.setBackground(new Color(220,220,220));
        this.txtOutput.setEditable(false);
        this.window.setSize(500,400);
        this.window.setVisible(true);
        this.txtMessage.requestFocus();
    }
 
    public void requestPseudo() {
        do{
            this.pseudo = JOptionPane.showInputDialog(
                    this.window, "Entrez votre pseudo : ",
                    this.title,  JOptionPane.OK_OPTION
            );
            if (this.pseudo == null) System.exit(0);
            try {
            	
            	String result = serveur.subscribe(this.pseudo);
                txt = result;
                if(result.equals("pseudo non disponible")){
                    JOptionPane.showMessageDialog(
                    this.window, "Pseudo deja pris",
                    this.title,  JOptionPane.INFORMATION_MESSAGE);
                }else {
                	this.printMessage(result);
                }
            }catch (Exception exception) { System.err.println("RequestPseudo: " + exception); }
        }while(txt.equals("pseudo non disponible"));
        this.requestPostedMessage();
    }

    public void requestPostedMessage(){
        try {
            while (true){
                String resultat = serveur.requestMessage(this.pseudo);
                if(resultat != null)
                	txtOutput.setText(resultat);
            }

        }catch (Exception exception) { System.err.println("RequestMessage: " + exception); }
    }

    public void btnSend_actionPerformed(ActionEvent e) {
        try {
            String resultat = serveur.postMessage(this.pseudo, this.txtMessage.getText());
        }catch (Exception exception) { System.err.println("PostMessage: " + exception); }
        this.txtMessage.setText("");
        this.txtMessage.requestFocus();
    }
    public void deconnecter (WindowEvent e) {

        try{
            String result = serveur.unsubscribe(this.pseudo);
            }catch(Exception ex){
            ex.printStackTrace();
        }
        System.exit(0);
    }

    public void printMessage(String msg){
        String text = txtOutput.getText();
        txtOutput.setText(text+msg+"\n");
    }

    public static void main(String[] args) {
        new Client();

       
}

}

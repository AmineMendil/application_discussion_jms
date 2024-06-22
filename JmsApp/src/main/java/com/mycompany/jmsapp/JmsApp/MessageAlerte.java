package com.mycompany.jmsapp.JmsApp;

import javax.swing.JOptionPane;

public class MessageAlerte {
	
	
	public static void Erreur(String message) {
        JOptionPane.showMessageDialog(null, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

}

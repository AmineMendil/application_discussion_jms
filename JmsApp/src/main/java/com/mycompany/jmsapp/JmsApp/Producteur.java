package com.mycompany.jmsapp.JmsApp;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Scanner;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.ejb.access.EjbAccessException;

public class Producteur extends JFrame {
	private JTextArea area;
	private JTextField field;
	private JLabel labeTitre, labelNumPort;
	private JButton envoie, connect;
	private JScrollPane scrollPane;
	private JPanel panelSouth, panelNorth, panelL_north;

	private Connection connexion;

	public void seConnecter() {

	}

	public Producteur() throws JMSException {
		super("Producteur");

		// Création des composants
		area = new JTextArea(10, 30);
		area.setEditable(false);
		field = new JTextField(30);
		envoie = new JButton("Envoyer");
		connect = new JButton("Se connecter");
		labeTitre = new JLabel("Numéro de port :");
		labelNumPort = new JLabel("61616");

		//Ajout des deux label dans le panel "panelL_north"
		panelL_north = new JPanel();
		this.panelL_north.add(labeTitre);
		this.panelL_north.add(labelNumPort);

		//Ahout de panelL_North et le boutton connect dans le panel "panelNorth".
		panelNorth = new JPanel();
		this.panelNorth.add(panelL_north);
		this.panelNorth.add(connect);

		// Création de panel qui vas regrouper le JTextField et le JButton
		panelSouth = new JPanel();
		this.panelSouth.add(field);
		this.panelSouth.add(envoie);

		// Création de ScrollPane
		scrollPane = new JScrollPane(area);

		// Création du gestionaire de disposition
		setLayout(new BorderLayout());

		// Ajout des composant à la fenêtre
		add(panelNorth, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(panelSouth, BorderLayout.SOUTH);

		
			// Création d'une instance spécifique  à activeMQ (localhost:61616)
			ConnectionFactory connectionfact = new ActiveMQConnectionFactory("tcp://localhost:61616");
			//établissement d'une connexion à activeMQ  
			connexion = connectionfact.createConnection();
			//demarage de la connexion à activeMQ
			connexion.start();
			
			/*
			 * Création d'une session pour indiquer que les message accusés de réception
			 * après leur réception par le consommateur
			 */
			Session session = connexion.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Creéation d'une destiantion (topic) nommée "AppJMS"
			Destination destination1 = session.createQueue("AppJMS_PC");

			/* Création d'un producteur de message pour la destination spécifiée. */
			MessageProducer producteurEn = session.createProducer(destination1);

			/*
			 * Permet d'undiquer au système que les message ne seront pas sauvgardés sur le
			 * disque
			 */
			producteurEn.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			TextMessage textMessage = session.createTextMessage();
		

		// Action à effectuer lorsque on clique sur le boutton envoie.
		envoie.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					String message = field.getText();
					textMessage.setText(message);
					
					if (message.isEmpty()) {
						MessageAlerte.Erreur("Veuillez saisir un message");
					} else {
						area.append("<Producteur>:  " + message + "\n");
						field.setText("");
						producteurEn.send(textMessage);
						

					}

				} catch (JMSException e2) {
					// TODO: handle exception
				}

			}
		});

		// Action à effectuer lorsque on clique sur le boutton envoie.
		connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// Création de'une connection à activeMQ
					ConnectionFactory connectionfact = new ActiveMQConnectionFactory("tcp://localhost:61616");
					connexion = connectionfact.createConnection();
					connexion.start();
					
					/*
					 * Création d'une session pour indiquer que les message accusés de réception
					 * après leur réception par le consommateur
					 */
					Session session = connexion.createSession(false, Session.AUTO_ACKNOWLEDGE);

					/*Creéation d'une destiantion (topic) nommée "AppJMS_CP" pour l'écoute 
					 * de la fil d'attente ou le consommateur envoie*/
					Destination destination2 = session.createQueue("AppJMS_CP");

					// Création du consomateur de message
					MessageConsumer ProducteurCn = session.createConsumer(destination2);
					
					ProducteurCn.setMessageListener(new MessageListener() {

						@Override
						public void onMessage(Message message) {
							try {
								if (message instanceof TextMessage) {
									TextMessage textMessage = (TextMessage) message;
									String txt = textMessage.getText();
									area.append("<Consomateur>  " + txt + "\n");
								}
							} catch (JMSException e2) {

							}

						}
					});
				} catch (Exception e2) {
					// TODO: handle exception
				}

			}
		});
					
					

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new Producteur();
				} catch (JMSException e) {
					// TODO: handle exception
				}

			}
		});
	}
}

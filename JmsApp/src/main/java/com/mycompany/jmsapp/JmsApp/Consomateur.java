package com.mycompany.jmsapp.JmsApp;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.swing.*;

import org.apache.activemq.ActiveMQConnectionFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Consomateur extends JFrame {

	private JTextArea area;
	private JTextField field;
	private JLabel labeTitre, labelNumPort;
	private JButton envoie, connect;
	private JScrollPane scrollPane;
	private JPanel panelSouth, panelNorthC, panelL_north;

	private Connection connexion;

	public Consomateur() throws JMSException {

		super("Consomateur");

		// Création des composants
		area = new JTextArea(10, 30);
		area.setEditable(false);
		field = new JTextField(30);
		envoie = new JButton("Envoyer");
		connect = new JButton("Se connecter");

		labeTitre = new JLabel("Numéro de port :");
		labelNumPort = new JLabel("61616");

		panelL_north = new JPanel();
		this.panelL_north.add(labeTitre);
		this.panelL_north.add(labelNumPort);

		panelNorthC = new JPanel();
		this.panelNorthC.add(panelL_north);
		this.panelNorthC.add(connect);

		// Création de panel qui vas regrouper le JTextField et le JButton
		panelSouth = new JPanel();
		this.panelSouth.add(field);
		this.panelSouth.add(envoie);

		// Création de ScrollPane
		scrollPane = new JScrollPane(area);

		// Création du gestionaire de disposition
		setLayout(new BorderLayout());

		// Ajout des composant à la fenêtre
		add(panelNorthC, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(panelSouth, BorderLayout.SOUTH);

		
			// Créationde'une connection à activeMQ
			ConnectionFactory connectionfact = new ActiveMQConnectionFactory("tcp://localhost:61616");
			connexion = connectionfact.createConnection();
			connexion.start();
			
			/*
			 * Création d'une session pour indiquer que les message accusés de réception
			 * après leur réception par le consommateur
			 */
			Session session = connexion.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Creéation d'une destiantion (topic) nommée "AppJMS"
			Destination destination1 = session.createQueue("AppJMS_CP");

			/* Création d'un producteur de message pour la destination spécifiée. */
			MessageProducer consomateurEn = session.createProducer(destination1);
			
			/*
			 * Permet d'undiquer au système que les message ne seront pas sauvgardés sur le
			 * disque
			 */
			consomateurEn.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
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
							area.append("<Consomateur>:  " + message + "\n");
							field.setText("");
							
							consomateurEn.send(textMessage);
							

						}

					} catch (Exception e2) {
						// TODO: handle exception
					}

				}
			});

		

		connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					ConnectionFactory connectionfact = new ActiveMQConnectionFactory("tcp://localhost:61616");
					connexion = connectionfact.createConnection();
					connexion.start();
					// Création de la session
					Session session = connexion.createSession(false, Session.AUTO_ACKNOWLEDGE);

					// Création de la destination (TOPIC)
					Destination destination2 = session.createQueue("AppJMS_PC");

					// Création du consomateur de message
					MessageConsumer consommateurCn = session.createConsumer(destination2);

					consommateurCn.setMessageListener(new MessageListener() {

						@Override
						public void onMessage(Message message) {
							try {
								if (message instanceof TextMessage) {
									TextMessage textMessage = (TextMessage) message;
									String txt = textMessage.getText();
									area.append("<Producteur>  " + txt + "\n");
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
					new Consomateur();
				} catch (JMSException e) {
					// TODO: handle exception
				}

			}
		});

	}

}

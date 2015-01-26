package de.hsrm.cs.wwwvs.primzahlenzwilling;

import java.util.Date;
import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/*
 * Consumer
 */
public class Consumer {
	private ConnectionFactory factory = null;
	private Connection connection = null;
	private Session session = null;
	private Destination destination = null;
	private MessageConsumer consumer = null;

	/*
	 * Constructor
	 */
	public Consumer() {
	}

	public void receiveMessage() {
		try {
			//connection stuff
			factory = new ActiveMQConnectionFactory(
					ActiveMQConnection.DEFAULT_BROKER_URL);
			connection = factory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createTopic("Primzahlenzwillinge");
			consumer = session.createConsumer(destination);
			
			int prim1 = 0, prim2 = 0, counter = 0;
			float time = 0;
			
			//receive forever
			while (true) {
				Message message = consumer.receive();
				//get Properties
				@SuppressWarnings("unchecked")
				Enumeration<String> probs = (Enumeration<String>)message.getPropertyNames(); 
				//get timestamp and cast to readable Date
				Date date = new java.util.Date(message.getJMSTimestamp());
				
				/*
				 * Print Message 
				 */

				while (probs.hasMoreElements()) {
					String name = probs.nextElement().toString();
					Object obj = message.getObjectProperty(name);
					
					switch (name) {
					case "Primzahl1":
						prim1 = Integer.parseInt(obj.toString());
						break;
					case "Primzahl2":
						prim2 = Integer.parseInt(obj.toString());
						break;
					case "Anzahl":
						counter = Integer.parseInt(obj.toString());
						break;
					case "Berechnungszeit":
						time = Float.parseFloat(obj.toString());
						break;
					default:
						break;
					}
				}
				System.out.println("[" + date + "] " + counter + ". Primzahlenzwilling: ‘" + prim2 + ", " + prim1 + "‘ - Berechnung in " + time + " Millisekunden.");
				System.out.println("");
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * MAIN
	 */
	public static void main(String[] args) {
		Consumer consumer = new Consumer();
		consumer.receiveMessage();
	}

}


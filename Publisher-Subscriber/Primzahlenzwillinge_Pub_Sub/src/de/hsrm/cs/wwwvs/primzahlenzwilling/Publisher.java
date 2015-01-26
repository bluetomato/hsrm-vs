package de.hsrm.cs.wwwvs.primzahlenzwilling;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import java.util.Enumeration;

public class Publisher {
	int prim1, prim2;
	float time1, time2;
	int counter = 0;
	ConnectionFactory factory, factorySend;
	Connection connection, connectionSend;
	Session sessionRecv, sessionSend;
	Destination destinationRecv, destinationSend;
	MessageProducer producer;
	TextMessage message;
	MessageConsumer consumer = null;
	
	public Publisher() {
		try {
			//do connection-stuff
			//for recv messages
			this.factory = new ActiveMQConnectionFactory(
					ActiveMQConnection.DEFAULT_BROKER_URL);
			this.connection = this.factory.createConnection();
			this.connection.start();
			this.sessionRecv = this.connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			this.destinationRecv = this.sessionRecv.createTopic("Primzahlen");
			this.consumer = this.sessionRecv.createConsumer(destinationRecv);
			
			//for sending message
			this.factorySend = new ActiveMQConnectionFactory(
					ActiveMQConnection.DEFAULT_BROKER_URL);
			this.connectionSend = this.factorySend.createConnection();
			this.sessionSend = this.connectionSend.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			this.destinationSend = this.sessionSend.createTopic("Primzahlenzwillinge");
			this.producer = this.sessionSend.createProducer(destinationSend);
			this.message = this.sessionSend.createTextMessage();
			
		} catch (JMSException e) {
			System.out.println("Fehler beim Erstellen des Publisher-Service: " + e.getErrorCode());
		}
	}

	/*
	 * Receive Message
	 */
	public void recvMessage() {
		prim1 = 0;
		prim2 = 0;
		time1 = 0;
		time2 = 0;

		try {
			// receive forever
			while (true) {
				Message message = consumer.receive();
				// get Properties
				@SuppressWarnings("unchecked")
				Enumeration<String> probs = (Enumeration<String>)message.getPropertyNames(); 
				// get timestamp and cast to readable Date
				// Date date = new java.util.Date(message.getJMSTimestamp());

				/*
				 * Print Message
				 */
				
				//while there are more elements
				while (probs.hasMoreElements()) {
					String name = probs.nextElement().toString();
					Object obj = message.getObjectProperty(name);

					//switch for Element
					switch (name) {
					case "Primzahl":
						prim2 = prim1;
						prim1 = Integer.parseInt(obj.toString());
						break;
					case "Berechnungszeit":
						time2 = time1;
						time1 = Float.parseFloat(obj.toString());
						break;
					default:
						break;
					}

				}
				
				//if there's a different of 2 between prim1 and prim2: it's a twin
				if (2 == prim1 - prim2 && prim2 != 0) {
					counter++;
					//set properties
					this.message.setFloatProperty("Berechnungszeit", time1+time2);
					this.message.setIntProperty("Primzahl1", prim1);
					this.message.setIntProperty("Primzahl2", prim2);
					this.message.setIntProperty("Anzahl", counter);

					//set JMSTimestamp
					this.message.setJMSTimestamp(System.currentTimeMillis());
					
					//send message
					this.producer.send(this.message);
				}
			}
		} catch (JMSException e) {
			System.out.println("Fehler beim Erstellen des Publisher-Service");
		}
	}


	/*
	 * MAIN
	 */
	public static void main(String[] args) {
		// init publisher
		Publisher publisher = new Publisher();
		publisher.recvMessage();

	}
}
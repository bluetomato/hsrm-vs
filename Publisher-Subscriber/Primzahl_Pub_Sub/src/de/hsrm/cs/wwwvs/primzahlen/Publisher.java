package de.hsrm.cs.wwwvs.primzahlen;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread;

/*
 * @author Stephanie Scholl
 * 
 */
public class Publisher {
	static int sleeptime;
	
	/*
	 * Constructor
	 */
	public Publisher() {
	}

	/*
	 * calc prime and send
	 */
	public void sendMessage() {
		int max = Integer.MAX_VALUE; //max prime
		long count = 0; //number of prime
		int number; //number to calc with
		int counter;
		boolean prime;
		long timeTmp;
		float time = 0;
		
		try {
			//do connection-stuff
			ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
			Connection connection = factory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createTopic("Primzahlen");
			MessageProducer producer = session.createProducer(destination);
			TextMessage message = session.createTextMessage();

			/*
			 * Primzahl
			 */

			// get actual time in seconds (start of calculation)
			timeTmp = System.currentTimeMillis();
			
			for (number = 2; number < max; number++) {
				Thread.sleep(sleeptime);
				timeTmp += (sleeptime); //minus sleeptime
				
				prime = true;

				//test if number is prime
				for (counter = 2; counter <= number / 2; counter++) {
					if (0 == number % counter) {
						prime = false;
						break;
					}
				}
				
				//if it's a prime: build message for topic
				if (prime) {
					//Calculation finished, calc duration
					time = (System.currentTimeMillis() - timeTmp); //Sekunden: /1000
					
					count++; // primzahl ausgeben
					
					//set properties
					message.setFloatProperty("Berechnungszeit", time);
					message.setIntProperty("Primzahl", number);
					message.setLongProperty("Anzahl", count);
					
					//set JMSTimestamp
					message.setJMSTimestamp(System.currentTimeMillis());
					
					//send message
					producer.send(message);
					
					//what's the current time?
					timeTmp = System.currentTimeMillis();
				}
				
			}

		} catch (JMSException | InterruptedException e) {
			System.err.println("Fehler: " + e.getMessage());
		}

	}
	
	/*
	 * MAIN
	 */

	public static void main(String[] args) {
		
		/*
		 *Read Sleeptime (Int) from console 
		 */
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Bitte die Sleep Time in Sekunden eingeben: ");

		String in = "1";
		try {
			in = console.readLine();
		} catch (IOException e) {
			System.err.println("Fehler beim lesen von der Kommandozeile: " + e.getMessage());
			System.err.println("Es wird der Defaultwert von einer Sekunde verwendet.");
		}
		
		//parse string to int
		sleeptime = Integer.parseInt(in);
		sleeptime = sleeptime * 1000; //calc Milliseconds because Thread.sleep needs milliseconds
		
		//create publisher
		Publisher publisher = new Publisher();
		//calc and send
		publisher.sendMessage();

	}
}
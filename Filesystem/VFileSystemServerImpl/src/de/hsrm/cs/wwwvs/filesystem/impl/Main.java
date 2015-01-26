		package de.hsrm.cs.wwwvs.filesystem.impl;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.*;

import de.hsrm.cs.wwwvs.filesystem.webservice.FileSystemWS;

//import de.hsrm.cs.wwwvs.filesystem.cli.ClientCLI;

public class Main {
	
	/*
	 * Die Klasse "Main" ist ausführbar und muss um den Code zum
	 * Verbindungsaufbau zum Server vervollständigt werden. Anschließend erzeugt
	 * sie mit dem Socket eine neue Instanz der Klasse "Connection“, unter deren
	 * Nutzung wiederum eine Instanz von "FileSystemImpl" erzeugt und dem
	 * Kommandozeilen-Interface übergeben wird.
	 */
	public static void main(String[] args) throws IOException {

		if (args.length != 3) {
			System.err.println("Falsche Anzahl Argumente: <host> <Cport> <WSport>");
			System.exit(-1);
		}

		// store hostname and port
		String hostname = args[0];
		int port = 0;
		String portWeb;

		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.err.println("Port ist keine Zahl");
			System.exit(-1);
		}
	    
		portWeb = args[2];
		Socket socket;
		Connection con = null;

		/**
		 * Connect to Server
		 */

		try {
			// create Clientsocket, connect to Server
			socket = new Socket(hostname, port);
			con = new Connection(socket);

		} catch (UnknownHostException e) {
			System.out.println("Unknown Host: " + e.getMessage());
			System.exit(-1);

		} catch (IOException e) {
			System.err.println("Fehler beim Verbindungsaufbau: " + e);
			System.out.println("Wahrscheinlich läuft der C Server nicht ;)");
			System.exit(-1);
		}
		
		/*
		 * Webservice
		 */
		
		System.out.println("Starting Server");
		FileSystemWS implementor = new FileSystemWSImpl(con);
		JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
		svrFactory.setServiceClass(FileSystemWS.class);
		svrFactory.setAddress("http://"+hostname+":"+portWeb+"/FileSystemWS");
		svrFactory.setServiceBean(implementor);
		//svrFactory.getInInterceptors().add(new LoggingInInterceptor());
		//svrFactory.getOutInterceptors().add(new LoggingOutInterceptor());
		svrFactory.create();
		
		System.out.println("Server ready...");
		
		try {
			Thread.sleep(10 * 60 * 1000);
		} catch (InterruptedException e) {
			System.out.println("Fehler während der Ausführung des Threads: " + e.getMessage());
		}
        System.out.println("Server exiting");
        System.exit(0);

	}
}

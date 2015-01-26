package de.hsrm.cs.wwwvs.filesystem.webservice.client;

import javax.xml.namespace.QName;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import de.hsrm.cs.wwwvs.filesystem.Filesystem;
import de.hsrm.cs.wwwvs.filesystem.cli.ClientCLI;
import de.hsrm.cs.wwwvs.filesystem.webservice.FileSystemWS;
import de.hsrm.cs.wwwvs.filesystem.webservice.client.FileSystemWSImpl;

public class Main {
	private final static QName SERVICE_NAME = new QName("http://webservice.filesystem.wwwvs.cs.hsrm.de/",
			"FileSystemWS");
	private final static QName PORT_NAME = new QName("http://webservice.filesystem.wwwvs.cs.hsrm.de/",
			"FileSystemWSPort");

	public static void main(String[] args) {
		JaxWsProxyFactoryBean factory;
		FileSystemWS client = null;

		if (args.length != 2) {
			System.err.println("Falsche Anzahl Argumente: <host> <WSport>");
			System.exit(-1);
		}

		String hostname = args[0];
		int port = 0;

		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.err.println("Port ist keine Zahl");
			System.exit(-1);
		}

		// TODO Über den Webservice eine Verbindung zum Server aufbauen
		try {
			factory = new JaxWsProxyFactoryBean();
			//factory.getInInterceptors().add(new LoggingInInterceptor());
			//factory.getOutInterceptors().add(new LoggingOutInterceptor());
			factory.setServiceClass(FileSystemWS.class);
			factory.setAddress("http://" + hostname + ":" + port + "/FileSystemWS");
			client = (FileSystemWS) factory.create();

		} catch (Exception e) {
			System.out.println("Fehler beim Erstellen der Factory: " + e.getMessage());
			System.exit(-1);
		}

		FileSystemWSImpl fsimpl = new FileSystemWSImpl(client);
		Filesystem fs = (Filesystem) new FileSystemImpl(fsimpl);
		System.out.println("Client wurde vollständig Initialisiert.");
		new ClientCLI(fs);
	}

}

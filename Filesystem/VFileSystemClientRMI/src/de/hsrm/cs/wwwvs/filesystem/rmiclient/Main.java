package de.hsrm.cs.wwwvs.filesystem.rmiclient;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;

import de.hsrm.cs.wwwvs.filesystem.Filesystem;
import de.hsrm.cs.wwwvs.filesystem.cli.ClientCLI;

public class Main {

	public static void main(String[] args) {

		if (args.length != 2) {
			System.err.println("Falsche Anzahl Argumente: <host> <port>");
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

		try {
			LocateRegistry.getRegistry(hostname); //get Registry with host with default port
			Filesystem fs;
			fs = (Filesystem) Naming.lookup("Filesystem");
			
			new ClientCLI(fs); //start
			
		} catch (RemoteException | NotBoundException | MalformedURLException e) {
			System.err.println("Fehler RMI Client: " + e);
		}		
	}
}

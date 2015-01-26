package de.hsrm.cs.wwwvs.filesystem.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import de.hsrm.cs.wwwvs.filesystem.impl.Marshaller.MarshallingException;
import de.hsrm.cs.wwwvs.filesystem.messages.FileServerMessage;
/*
 * Die Klasse "Connection" verwaltet die Socket-Verbindung. Darin muss die Methode 
 "remoteOperation" implementiert werden, die eine "FileServerMessage“ 
 entgegennimmt, diese mit Hilfe des Marshallers serialisiert, über den Socket an den 
 Server schickt, die Antwort empfängt, diese mit dem Marshaller deserialisiert und das 
 Ergebnis an den Aufrufenden zurück gibt.
 */
public class Connection{
	private final InputStream input;
	private final OutputStream output;

	public Connection(Socket socket) throws IOException {
		this.input = socket.getInputStream();
		this.output = socket.getOutputStream();
		
	}

	public FileServerMessage remoteOperation(FileServerMessage request)
			throws IOException, MarshallingException {
		
		byte[] buffer;
		
		//serialize request
		buffer = Marshaller.marshall(request);
		
		//send buffer to Server
		sendMsg(buffer);
		//receive FileServerMessage from Server
		FileServerMessage response = recvMsg();
	
		return response;
	}
	
	/**
	 * Write Message to Server
	 */
	public void sendMsg(byte[] buffer){
		try {
			//get size of message we want to send
			int sizeOfBuffer = buffer.length;
			//build new bytebuffer for sending message
			ByteBuffer toSend = ByteBuffer.allocate(Integer.BYTES + sizeOfBuffer);
			//add message length and message to buffer
			toSend.putInt(sizeOfBuffer);
			toSend.put(buffer);
			
			//send message
			this.output.write(toSend.array());
		} catch (IOException e) {

			System.out.println("Fehler beim Senden der Nachricht: " + e);
		}
	}
	
	/**
	 * Get Message from Server
	 */
	public FileServerMessage recvMsg(){
		FileServerMessage response = null;
		byte[] message;
		byte[] buff;
		int size;
		
		try {
			
			//Buffer for message length
			buff = new byte[4];
			this.input.read(buff);
			//fill buffer
			
			//message size is in index 3
			size = ByteBuffer.wrap(buff).getInt();
			
			//fill byte [] with message
			message = new byte[size];
			this.input.read(message);
			//deserialize message
			 response = Marshaller.unmarshall(message);
			 
		} catch (IOException | MarshallingException e) {
			System.out.println("Fehler beim Empfangen der Nachricht: " + e);
		}
		return response;
	}
}
	
	
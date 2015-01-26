package de.hsrm.cs.wwwvs.filesystem.impl;

import de.hsrm.cs.wwwvs.filesystem.Directory;
import de.hsrm.cs.wwwvs.filesystem.Filesystem;

/*
 * Die Klassen "FileSystemImpl", "DirectoryImpl" und "FileImpl", in deren Methoden 
jeweils eine entsprechende Nachricht erzeugt werden muss, die dann über die 
"Connection" an den Server geschickt wird. Anschließend muss die resultierende 
Nachricht betrachtet und der Rückgabewert der Methode extrahiert werden.
 */
public class FileSystemImpl implements Filesystem {

	private final Directory root;

	public FileSystemImpl(Connection con){
		root = new DirectoryImpl(0, con);
	}

	@Override
	public Directory getRoot(){
		return root;

	}
}

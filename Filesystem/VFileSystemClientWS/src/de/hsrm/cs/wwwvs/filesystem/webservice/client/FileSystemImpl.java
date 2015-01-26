package de.hsrm.cs.wwwvs.filesystem.webservice.client;

import de.hsrm.cs.wwwvs.filesystem.Directory;
import de.hsrm.cs.wwwvs.filesystem.Filesystem;

public class FileSystemImpl implements Filesystem{
	
	private final Directory root;

	public FileSystemImpl(FileSystemWSImpl fsimpl){
		root = new DirectoryImpl(0, fsimpl);
	}

	@Override
	public Directory getRoot(){
		return root;

	}

}

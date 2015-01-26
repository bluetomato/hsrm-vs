package de.hsrm.cs.wwwvs.filesystem;

import java.rmi.RemoteException;

/**
 * An object of this class represents a filesystem which has exactly one root
 * directory.
 * 
 * @author Sascha Riedl
 *
 */
public interface Filesystem{
	/**
	 * Returns the root directory of the filesystem.
	 * 
	 * @return Root directory of the filesystem
	 * @throws RemoteException 
	 */
	public Directory getRoot();
}

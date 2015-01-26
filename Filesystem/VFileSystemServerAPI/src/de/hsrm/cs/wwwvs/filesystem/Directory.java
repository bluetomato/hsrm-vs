package de.hsrm.cs.wwwvs.filesystem;

import java.rmi.RemoteException;
import java.util.List;

/**
 * An object of this class represents a directory in a filesystem. A directory
 * can contain directories (called subdirectories) and files.
 * 
 * @author Sascha Riedl
 *
 */
public interface Directory{
	/**
	 * Returns the name of this directory.
	 * 
	 * @return Name of this directory
	 * @throws RemoteException 
	 */
	public String getName();

	/**
	 * Deletes the directory
	 * @throws RemoteException 
	 */
	public void delete();

	/**
	 * Returns the parent directory, or null, if it is the root.
	 * 
	 * @return Parent directory
	 * @throws RemoteException 
	 */
	public Directory getParent();

	/**
	 * Returns a list of all subdirectories.
	 * 
	 * @return List of all subdirectories; Empty list if directory contains no
	 *         subdirectories
	 * @throws RemoteException 
	 */
	public List<Directory> listDirectories();

	/**
	 * Returns a list with all files in the directory.
	 * 
	 * @return List of all files in the directory; Empty list if directory
	 *         contains no files
	 * @throws RemoteException 
	 */
	public List<File> listFiles();

	/**
	 * Creates a new subdirectory in this directory. If the directory name
	 * already exists, no new directory will be created.
	 * 
	 * @param name
	 *            Name of the new subdirectory
	 * @throws RemoteException 
	 */
	public Directory createDirectory(String name);

	/**
	 * Creates a new file in this directory. If the file name already exists, no
	 * new file will be created. The new file will also be bound to the RMI
	 * registry.
	 * 
	 * @param name
	 *            Name of the new file
	 * @throws RemoteException 
	 */
	public File createFile(String name);
}

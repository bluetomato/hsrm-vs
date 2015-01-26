package de.hsrm.cs.wwwvs.filesystem.webservice;

import java.nio.ByteBuffer;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * An object of this class represents a filesystem which has exactly one root
 * directory.
 *
 */

@WebService/*(targetNamespace = "de.hsrm.cs.wwwvs.filesystem.webservice",
			wsdlLocation = "http://localhost:9000/VSFileSystemWS"/*,
			serviceName = "FileSystemWS")*/
public interface FileSystemWS {
	
	/*
	 * Creates new File
	 * 
	 * @param parent parentfolder of new file
	 * @name name of new file
	 * 
	 * @return int fileid of new file
	 */
	@WebMethod
	/*@WebResult(name = "CreateNewFile", partName = "CreateNewFile")*/
	public int newFile(
			@WebParam( name = "parent", partName = "parent") int parent, 
			@WebParam( name = "name", partName = "name") String name);

	/*
	 * Creates new Folder
	 * 
	 * @param parent parentfolder of new folder
	 * @param name of new folder
	 * 
	 * @return int folderid of new folder
	 */
	@WebMethod
	//@WebResult(name = "CreateNewFolder", partName = "CreateNewFolder")
	public int newFolder(
			@WebParam( name = "parent", partName = "parent") int parent, 
			@WebParam( name = "name", partName = "name") String name);

	/*
	 * Deletes file with given fileid
	 * 
	 * @param file id of file we want to be deleted
	 * 
	 */
	@WebMethod
	//@WebResult(name = "DeleteFile", partName = "DeleteFile")
	public void deleteFile(
			@WebParam( name = "fileid", partName = "fileid") int fileid);

	/*
	 * Deletes folder with given folderid
	 * 
	 * @param folder id of folder we want to be deleted
	 * 
	 */
	@WebMethod
	//@WebResult(name = "DeleteFolder", partName = "DeleteFolder")
	public void deleteFolder(
			@WebParam( name = "folderid", partName = "folderid") int folderid);
	
	/*
	 * Returns Fileinfo of file with given ID
	 * 
	 * @param fileid id of file we want the information from
	 * 
	 * @return FileInfo
	 */
	/*@WebMethod
	@WebResult(name = "GetFileInfo", partName = "GetFileInfo")
	public FileInfo getFileInfo(
			@WebParam( name = "fileid", partName = "fileid") int fileid);
*/
	@WebMethod
	//@WebResult(name = "GetFileParent", partName = "GetFileParent")
	public int getFileParent(@WebParam( name = "fileid", partName = "fileid") int fileid);
	
	@WebMethod
	//@WebResult(name = "GetFileSize", partName = "GetFileSize")
	public int getFileSize(@WebParam( name = "fileid", partName = "fileid") int fileid);
	
	@WebMethod
	//@WebResult(name = "GetFileName", partName = "GetFileName")
	public String getFileName(@WebParam( name = "fileid", partName = "fileid") int fileid);
	
	/*
	 * Returns Folderinfo of folder with given ID
	 * 
	 * @param folderid id of folder we want the information from
	 * 
	 * @return FolderInfo
	 */
	/*@WebMethod
	@WebResult(name = "GetFolderInfo", partName = "GetFolderInfo")
	public FolderInfo getFolderInfo(
			@WebParam( name = "folderid", partName = "folderid") int folderid);
*/
	
	@WebMethod
	//@WebResult(name = "GetFolderParent", partName = "GetFileParent")
	public int getFolderParent(@WebParam( name = "folderid", partName = "folderid") int folderid);
	
	@WebMethod
	//@WebResult(name = "GetFolderName", partName = "GetFileName")
	public String getFolderName(@WebParam( name = "folderid", partName = "folderid") int folderid);
	
	@WebMethod
	//@WebResult(name = "GetFiles", partName = "GetFiles")
	public byte[] getFiles(@WebParam( name = "folderid", partName = "folderid") int folderid);
	
	@WebMethod
	//@WebResult(name = "GetFolders", partName = "GetFolders")
	public byte[] getFolders(@WebParam( name = "folderid", partName = "folderid") int folderid);
	/*
	 * Writes Data (from Offset with length) to an File
	 * 
	 * @param fileid File we want to be written
	 * @param offset Byte we want to start
	 * @param length Length of Data we want to be written
	 * @param data Data we want to write
	 */
	@WebMethod
	//@WebResult(name = "WriteFile", partName = "WriteFile")
	public void writeFile(
			@WebParam( name = "fileid", partName = "fileid") int fileid, 
			@WebParam( name = "offset", partName = "offset") int offset, 
			@WebParam( name = "length", partName = "length") int length, 
			@WebParam( name = "data", partName = "data") ByteBuffer data);

	/*
	 * Reads File in ByteBuffer
	 * 
	 * @param fileid id of file we want to read
	 * @param offset byte we want to start reading
	 * @param length length of message we want to read from file
	 * 
	 * @return ByteBuffer with Data
	 */
	@WebMethod
	//@WebResult(name = "ReadFile", partName = "ReadFile")
	public ByteBuffer readFile(
			@WebParam( name = "fileid", partName = "fileid") int fileid, 
			@WebParam( name = "offset", partName = "offset") int offset, 
			@WebParam( name = "length", partName = "length") int length);

}

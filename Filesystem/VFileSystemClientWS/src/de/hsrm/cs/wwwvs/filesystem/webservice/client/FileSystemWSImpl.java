package de.hsrm.cs.wwwvs.filesystem.webservice.client;

import java.nio.ByteBuffer;

import javax.xml.ws.soap.SOAPFaultException;

import de.hsrm.cs.wwwvs.filesystem.webservice.FileSystemWS;

public class FileSystemWSImpl implements FileSystemWS{
	private final FileSystemWS fsws;

	public FileSystemWSImpl(FileSystemWS fsws) {
		this.fsws = fsws;
	}
	
	@Override
	public int newFile(int parent, String name) {
		System.out.println("Neues File ");
		return this.fsws.newFile(parent, name);

	}

	@Override
	public int newFolder(int parent, String name) {
		System.out.println("Neuer Ordner");
		return this.fsws.newFolder(parent, name);
	}

	@Override
	public void deleteFile(int fileid) {
		this.fsws.deleteFile(fileid);
	}

	@Override
	public void deleteFolder(int folderid) {
		this.fsws.deleteFolder(folderid);
	}

	@Override
	public int getFileParent(int fileid) {
		System.out.println("FileParent holen");
		return this.fsws.getFileParent(fileid);
	}

	@Override
	public int getFileSize(int fileid) {
		System.out.println("Größe von ID: " + fileid);
		//try{
		return this.fsws.getFileSize(fileid);
		//} catch (SOAPFaultException e){
		//	System.out.println("Fehler beim Holen der FileSize: " + e.getMessage());
		//}
		//return 0;
	}

	@Override
	public String getFileName(int fileid) {
		System.out.println("Name von ID File: " +fileid);
		return this.fsws.getFileName(fileid);
	}

	@Override
	public int getFolderParent(int folderid) {
		System.out.println("FolderParent holen");
		return this.fsws.getFolderParent(folderid);
	}

	@Override
	public String getFolderName(int folderid) {
		System.out.println("Name von ID Folder: " +folderid);
		return this.fsws.getFolderName(folderid);
	}

	@Override
	public byte[] getFiles(int folderid) {
		System.out.println("Files holen");
		byte[] f = this.fsws.getFiles(folderid);
		//System.out.println("Files: " + f.length);
		return f;
	}

	@Override
	public byte[] getFolders(int folderid) {
		System.out.println("Folders holen");
		byte[] f = this.fsws.getFolders(folderid);
		//System.out.println("Folders: " + f.length);
		return f;
	}

	@Override
	public void writeFile(int fileid, int offset, int length, ByteBuffer data) {
		this.fsws.writeFile(fileid, offset, length, data);
	}

	@Override
	public ByteBuffer readFile(int fileid, int offset, int length) {
		return this.fsws.readFile(fileid, offset, length);
	}

}

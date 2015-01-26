package de.hsrm.cs.wwwvs.filesystem.impl;

import java.nio.ByteBuffer;

import javax.jws.WebService;

import de.hsrm.cs.wwwvs.filesystem.impl.DirectoryImpl;
import de.hsrm.cs.wwwvs.filesystem.impl.FileImpl;
import de.hsrm.cs.wwwvs.filesystem.webservice.FileSystemWS;

@WebService(endpointInterface = "de.hsrm.cs.wwwvs.filesystem.webservice.FileSystemWS", 
			serviceName = "FileSystemWS")
public class FileSystemWSImpl implements FileSystemWS {

	Connection con;
	
	public FileSystemWSImpl(Connection con){
		this.con = con;
	}

	@Override
	public int newFile(int parent, String name) {
		FileImpl file = (FileImpl) new DirectoryImpl(parent, this.con).createFile(name);
		System.out.println("Neues File mit id: " + file.getFileId());
		return file.getFileId();
	}

	@Override
	public int newFolder(int parent, String name) {
		return ((DirectoryImpl) new DirectoryImpl(parent, this.con).createDirectory(name)).getFolderId();

	}

	@Override
	public void deleteFile(int fileid) {
		new FileImpl(fileid, this.con).delete();

	}

	@Override
	public void deleteFolder(int folderid) {
		new DirectoryImpl(folderid, this.con).delete();

	}
	
	@Override
	public int getFileParent(int fileid) {
		return new FileImpl(fileid, this.con).getParentId();
	}

	@Override
	public int getFileSize(int fileid) {
		return new FileImpl(fileid, this.con).getSize();
	}

	@Override
	public String getFileName(int fileid) {
		return new FileImpl(fileid, this.con).getName();
	}
	
	@Override
	public void writeFile(int fileid, int offset, int length, ByteBuffer data) {
		new FileImpl(fileid, this.con).write(offset, data.array());

	}

	@Override
	public ByteBuffer readFile(int fileid, int offset, int length) {
		return ByteBuffer.wrap(new FileImpl(fileid, this.con).read(offset,
				length));
	}

	@Override
	public int getFolderParent(int folderid) {
		return new DirectoryImpl(folderid, this.con).getParentId();
	}

	@Override
	public String getFolderName(int folderid) {
		return new DirectoryImpl(folderid, this.con).getName();
	}

	@Override
	public byte[] getFiles(int folderid) {
		return new DirectoryImpl(folderid, this.con).getFiles();
	}

	@Override
	public byte[] getFolders(int folderid) {
		return new DirectoryImpl(folderid, this.con).getFolders();
	}

}

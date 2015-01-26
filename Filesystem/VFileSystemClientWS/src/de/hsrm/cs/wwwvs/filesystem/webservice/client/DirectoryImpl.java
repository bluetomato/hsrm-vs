package de.hsrm.cs.wwwvs.filesystem.webservice.client;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import de.hsrm.cs.wwwvs.filesystem.Directory;
import de.hsrm.cs.wwwvs.filesystem.File;
import de.hsrm.cs.wwwvs.filesystem.webservice.client.FileImpl;
import de.hsrm.cs.wwwvs.filesystem.messages.FolderInfoResponse;
import de.hsrm.cs.wwwvs.filesystem.webservice.FileSystemWS;

public class DirectoryImpl implements Directory {

	private final FileSystemWS fsws;
	private final int folderId;
	private List<File> listFiles;
	private List<Directory> listFolders;

	/* constructor */
	public DirectoryImpl(int folderId, FileSystemWS fsws) {
		this.folderId = folderId;
		this.fsws = fsws;
		this.listFiles = new ArrayList<File>();
		this.listFolders = new ArrayList<Directory>();
	}

	@Override
	public String getName() {
		return this.fsws.getFolderName(this.folderId);

	}

	@Override
	public void delete() {
		this.fsws.deleteFolder(this.folderId);
	}

	@Override
	public Directory getParent() {
		//is folder root he doesn't have a parent
		if(0 == folderId){
			return null;
		}
		int id = this.fsws.getFolderParent(this.folderId);
		return new DirectoryImpl(id, this.fsws);
	}

	@Override
	public List<Directory> listDirectories() {
		byte[] dirs = this.fsws.getFolders(this.folderId);
		System.out.println("Dirs: " + dirs.length);
		listFolders = new ArrayList<Directory>();
		
		if(dirs != null){
			for(int i = 0; i < dirs.length; i++){
				listFolders.add(new DirectoryImpl(dirs[i], this.fsws));
			}
		}
		return listFolders;
	}

	@Override
	public List<File> listFiles() {
		byte[] files = this.fsws.getFiles(this.folderId);
		listFiles = new ArrayList<File>();
		
		if(files != null){
			for(int i = 0; i < files.length; i++){
				listFiles.add(new FileImpl(files[i], this.fsws));
			}
		}
		return listFiles;
	}

	@Override
	public Directory createDirectory(String name) {
		int id = this.fsws.newFolder(this.folderId, name);
		System.out.println("ID VOM neuen folder: " + id);
		return new DirectoryImpl(id, this.fsws);
	}

	@Override
	public File createFile(String name) {
		int id = this.fsws.newFile(this.folderId, name);
		return new FileImpl(id, this.fsws);
	}

	public FolderInfoResponse getFolderInfo() {
		int parent = this.fsws.getFolderParent(this.folderId);
		
		String name = this.fsws.getFolderName(this.folderId);
		
		byte[] filesb = this.fsws.getFiles(this.folderId);
		IntBuffer intBuf = ByteBuffer.wrap(filesb).asIntBuffer();
		int[] files = new int[intBuf.remaining()];
		intBuf.get(files);
		
		byte[] foldersb = this.fsws.getFiles(this.folderId);
		intBuf = ByteBuffer.wrap(foldersb).asIntBuffer();
		int[] folders = new int[intBuf.remaining()];
		intBuf.get(folders);
		
		FolderInfoResponse res = new FolderInfoResponse(parent, name.getBytes(), files, folders);
		return res;
	}

	public int getFolderId() {
		return this.folderId;
	}
/*
	public int getFolderParent() {
		FolderInfoResponse res = getFolderInfo();
		return res.getParent();
	}*/
/*
	public byte[] getFiles() {
		try {
			List<File> list = listFiles();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(list);
			return bos.toByteArray();
		} catch (IOException e) {
			System.out.println("Fehler beim Erstellen des Files-Bytearrays: " + e.getMessage());
		}
		return null;
	}

	public byte[] getFolders() {
		try {
			List<Directory> list = listDirectories();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(list);
			return bos.toByteArray();
		} catch (IOException e) {
			System.out.println("Fehler beim Erstellen des Folders-Bytearrays: " + e.getMessage());
		}
		return null;
	}
*/
}

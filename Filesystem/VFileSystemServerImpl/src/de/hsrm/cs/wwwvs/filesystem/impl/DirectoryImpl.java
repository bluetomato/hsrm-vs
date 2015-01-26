package de.hsrm.cs.wwwvs.filesystem.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import de.hsrm.cs.wwwvs.filesystem.Directory;
import de.hsrm.cs.wwwvs.filesystem.File;
import de.hsrm.cs.wwwvs.filesystem.impl.Marshaller.MarshallingException;
import de.hsrm.cs.wwwvs.filesystem.messages.DeleteFolderRequest;
import de.hsrm.cs.wwwvs.filesystem.messages.ErrorResponse;
import de.hsrm.cs.wwwvs.filesystem.messages.FileServerMessage;
import de.hsrm.cs.wwwvs.filesystem.messages.FolderInfoRequest;
import de.hsrm.cs.wwwvs.filesystem.messages.FolderInfoResponse;
import de.hsrm.cs.wwwvs.filesystem.messages.NewFileRequest;
import de.hsrm.cs.wwwvs.filesystem.messages.NewFileResponse;
import de.hsrm.cs.wwwvs.filesystem.messages.NewFolderRequest;
import de.hsrm.cs.wwwvs.filesystem.messages.NewFolderResponse;
import de.hsrm.cs.wwwvs.filesystem.messages.PayloadType;

public class DirectoryImpl implements Directory {
	private final int folderId;
	private final Connection con;
	private List<File> listFiles;
	private List<Directory> listFolders;
	private static final String charsetName = "US-ASCII";

	public DirectoryImpl(int folderId, Connection con) {
		this.folderId = folderId;
		this.con = con;
		this.listFiles = new ArrayList<File>();
		this.listFolders = new ArrayList<Directory>();
	}

	@Override
	public String getName() {
		try {
			System.out.println("Hole Name von Folder mit ID " + this.folderId);
			// create Request with given fileId
			FolderInfoRequest fiReq = new FolderInfoRequest(this.folderId);
			// create FileServerMessage with Payloadtype and Request as Payload
			FileServerMessage fsReq = new FileServerMessage(
					PayloadType.FOLDER_INFO_REQUEST, fiReq);

			// send FileServerMessage to Server and receive Respone as
			// FileServerMessage
			FileServerMessage fsRes = this.con.remoteOperation(fsReq);

			// if err isn't null: folder was not deleted: print error
			if (fsRes.getPayloadType() == PayloadType.ERROR_RESPONSE) {
				ErrorResponse er = (ErrorResponse) fsRes.getPayload();
				String erMsg = new String(er.getMsg(),
						Charset.forName(charsetName));
				System.out.println("Fehler: " + erMsg);
				System.out.println("Name konnte nicht geholt werden.");
			}

			// cast Payload to Response
			FolderInfoResponse fiRes = (FolderInfoResponse) fsRes.getPayload();

			return new String(fiRes.getName(), Charset.forName(charsetName));

		} catch (MarshallingException | IOException e) {
			System.out.println("Fehler getName: " + e);
		}
		return null;
	}

	@Override
	public void delete() {
		try {
			// create Request with given fileId
			DeleteFolderRequest dfReq = new DeleteFolderRequest(this.folderId);
			// create FileServerMessage with Payloadtype and Request as Payload
			FileServerMessage fsReq = new FileServerMessage(
					PayloadType.DELETE_FOLDER_REQUEST, dfReq);

			// send FileServerMessage to Server and receive Response
			FileServerMessage err = this.con.remoteOperation(fsReq);
			// if err isn't null: folder was not deleted: print error
			if (err.getPayloadType() == PayloadType.ERROR_RESPONSE) {
				ErrorResponse er = (ErrorResponse) err.getPayload();
				String erMsg = new String(er.getMsg(),
						Charset.forName(charsetName));
				System.out.println("Fehler: " + erMsg);
				System.out
						.println("Ordner mit Inhalt können nicht gelöscht werden.");
			}

		} catch (IOException | MarshallingException e) {
			System.out.println("Fehler delete: " + e);
		}
	}

	@Override
	public Directory getParent() {
		try {
			// create Request with given fileId
			FolderInfoRequest fiReq = new FolderInfoRequest(this.folderId);
			// create FileServerMessage with Payloadtype and Request as Payload
			FileServerMessage fsReq = new FileServerMessage(
					PayloadType.FOLDER_INFO_REQUEST, fiReq);

			// send FileServerMessage to Server and receive Respone as
			// FileServerMessage
			FileServerMessage fsRes = this.con.remoteOperation(fsReq);
			// cast Payload to Response
			FolderInfoResponse fiRes = (FolderInfoResponse) fsRes.getPayload();

			if (0 == fiRes.getParent()) {
				return null;
			}

			return new DirectoryImpl(fiRes.getParent(), con);

		} catch (MarshallingException | IOException e) {
			System.out.println("Fehler getParent: " + e);
		}

		return null;
	}

	@Override
	public List<Directory> listDirectories() {

		try {
			// create Request with given fileId
			FolderInfoRequest fiReq = new FolderInfoRequest(this.folderId);
			// create FileServerMessage with Payloadtype and Request as Payload
			FileServerMessage fsReq = new FileServerMessage(PayloadType.FOLDER_INFO_REQUEST, fiReq);

			// send FileServerMessage to Server and receive Respone as
			// FileServerMessage
			FileServerMessage fsRes = this.con.remoteOperation(fsReq);

			// if fsRes isn't null: there are no folders
			if (fsRes.getPayloadType() == PayloadType.ERROR_RESPONSE) {
				ErrorResponse er = (ErrorResponse) fsRes.getPayload();
				String erMsg = new String(er.getMsg(),
						Charset.forName(charsetName));
				System.out.println("Fehler: " + erMsg);
				return null;
			}

			// cast Payload to Response
			FolderInfoResponse fiRes = (FolderInfoResponse) fsRes.getPayload();

			int[] folders = fiRes.getFolders();
			// create List with Folders
			listFolders = new ArrayList<Directory>();
			if (folders.length != 0) {
				for (int i = 0; i < folders.length; i++) {
					listFolders.add(new DirectoryImpl(folders[i], con));
					System.out.println("--- listDirectories Ordnernamen " + folders[i]);
				}
			}
			System.out.println("Es sind Folder da: " + folders.length);
			return listFolders;

		} catch (MarshallingException | IOException e) {
			System.out.println("Fehler listDirectories: " + e);
		}
		return null;
	}

	@Override
	public List<File> listFiles() {
		try {
			// create Request with given fileId
			FolderInfoRequest fiReq = new FolderInfoRequest(this.folderId);
			// create FileServerMessage with Payloadtype and Request as Payload
			FileServerMessage fsReq = new FileServerMessage(
					PayloadType.FOLDER_INFO_REQUEST, fiReq);

			// send FileServerMessage to Server and receive Respone as
			// FileServerMessage
			FileServerMessage fsRes = this.con.remoteOperation(fsReq);
			// cast Payload to Response
			FolderInfoResponse fiRes = (FolderInfoResponse) fsRes.getPayload();
			int[] files = fiRes.getFiles();

			// create List with Files
			listFiles = new ArrayList<File>();
			if (files.length != 0) {
				for (int i = 0; i < files.length; i++) {
					listFiles.add(new FileImpl(files[i], con));
				}
			}
			return listFiles;

		} catch (MarshallingException | IOException e) {
			System.out.println("Fehler listFiles: " + e);
		}
		return null;
	}

	@Override
	public Directory createDirectory(String name) {
		try {
			// does a directory with this exat name exist? - return null
			System.out.println("Erstelle Ordner mit Name: " + name);
			List<Directory> folderList = new ArrayList<Directory>();
			folderList = listDirectories();
			for (Directory i : folderList) {
				if (i.getName().equals(name)) {
					System.out
							.println("Es ist schon ein Ordner mit diesem Namen vorhanden.");
					return null;
				}
			}
			// create Request with given fileId
			NewFolderRequest nfReq = new NewFolderRequest(this.folderId,
					name.getBytes(Charset.forName(charsetName)));
			// create FileServerMessage with Payloadtype and Request as Payload
			FileServerMessage fsReq = new FileServerMessage(
					PayloadType.NEW_FOLDER_REQUEST, nfReq);

			// send FileServerMessage to Server and receive Respone as
			// FileServerMessage
			FileServerMessage fsRes = this.con.remoteOperation(fsReq);

			// if fsRes isn't null: there are no folders
			if (fsRes.getPayloadType() == PayloadType.ERROR_RESPONSE) {
				ErrorResponse er = (ErrorResponse) fsRes.getPayload();
				String erMsg = new String(er.getMsg(),
						Charset.forName(charsetName));
				System.out
						.println("Fehler, Ordner konnte nicht erstellt werden: "
								+ erMsg);
				return null;
			}
			// cast Payload to Response
			NewFolderResponse nfRes = (NewFolderResponse) fsRes.getPayload();

			return new DirectoryImpl(nfRes.getHandle(), con);

		} catch (MarshallingException | IOException e) {
			System.out.println("Fehler createDirectory: " + e);
		}

		return null;
	}

	@Override
	public File createFile(String name) {
		try {

			// does a file with this exat name exist?
			List<File> fileList = new ArrayList<File>();
			fileList = listFiles();
			for (File i : fileList) {
				if (i.getName().equals(name)) {
					System.out
							.println("Es ist schon eine Datei mit diesem Namen enthaltenls.");
					return null;
				}
			}

			// create Request with given fileId
			NewFileRequest nfReq = new NewFileRequest(this.folderId,
					name.getBytes(Charset.forName(charsetName)));
			System.out.println("File 1");
			// create File Server Message with new Payloadtype and
			// NewFileRequest as Payload
			FileServerMessage fsReq = new FileServerMessage(
					PayloadType.NEW_FILE_REQUEST, nfReq);
			System.out.println("File 2");
			// send FileServerMessage to Server and receive Respone as
			// FileServerMessage
			FileServerMessage fsRes = this.con.remoteOperation(fsReq);
			System.out.println("File 3");
			// cast Payload to Response
			NewFileResponse nfRes = (NewFileResponse) fsRes.getPayload();
			System.out.println("File 4");

			return new FileImpl(nfRes.getHandle(), con);

		} catch (MarshallingException | IOException e) {
			System.out.println("Fehler createFile: " + e);
		}
		return null;
	}

	public int getFolderId() {
		return this.folderId;
	}

	public FolderInfoResponse getFolderInfo() {
		try {
			FolderInfoRequest foi_req = new FolderInfoRequest(this.folderId); // build
			// request
			FileServerMessage foireq_msg = new FileServerMessage(
					PayloadType.FOLDER_INFO_REQUEST, foi_req);

			FileServerMessage foiresp_msg = this.con
					.remoteOperation(foireq_msg);

			// if fsRes isn't null: there are no folders
			if (foiresp_msg.getPayloadType() == PayloadType.ERROR_RESPONSE) {
				ErrorResponse er = (ErrorResponse) foiresp_msg.getPayload();
				String erMsg = new String(er.getMsg(),
						Charset.forName(charsetName));
				System.out
						.println("Fehler, Ordner konnte nicht erstellt werden: "
								+ erMsg);
				return null;
			}
			FolderInfoResponse foi_resp = (FolderInfoResponse) foiresp_msg
					.getPayload();

			return foi_resp;
		} catch (MarshallingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public int getParentId() {
		FolderInfoResponse fires = getFolderInfo();
		return fires.getParent();
	}

	public byte[] getFiles() {
		try {
			List<File> files = listFiles();
			System.out.println("Directories getFiles DirectoryImpl: "
					+ files.size());
			byte[] bytes = new byte[files.size()];

			if (0 != files.size()) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(files);
				bytes = bos.toByteArray();
			}

			return bytes;
		} catch (IOException e) {
			System.out
					.println("Fehler beim erstellen des Byte Arrays für die Files: "
							+ e.getMessage());
		}
		return null;
	}

	public byte[] getFolders() {
		try {
			System.out.println("1");
			List<Directory> dir = listDirectories();
			System.out.println("2");
			System.out.println("Directories getFolders DirectoryImpl: "
					+ dir.size());
			byte[] bytes = new byte[dir.size()];

			if (dir.size() != 0) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(dir);
				bytes = bos.toByteArray();
				return bytes;
			}

		} catch (IOException e) {
			System.out
					.println("Fehler beim erstellen des Byte Arrays für die Folders: "
							+ e.getMessage());
		}
		return null;
	}

}

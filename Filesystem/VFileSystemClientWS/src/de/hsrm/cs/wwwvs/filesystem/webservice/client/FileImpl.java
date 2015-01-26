package de.hsrm.cs.wwwvs.filesystem.webservice.client;

import java.nio.ByteBuffer;
import de.hsrm.cs.wwwvs.filesystem.File;
import de.hsrm.cs.wwwvs.filesystem.messages.FileInfoResponse;
import de.hsrm.cs.wwwvs.filesystem.webservice.FileSystemWS;

public class FileImpl implements File {
	private final int fileId;
	private final FileSystemWS fsws;

	public FileImpl(int fileid, FileSystemWS fsws) {
		this.fileId = fileid;
		this.fsws = fsws;
	}

	public int getParent() {
		return this.fsws.getFileParent(this.fileId);
	}

	@Override
	public String getName() {
		return this.fsws.getFileName(this.fileId);
	}

	@Override
	public void delete() {
		this.fsws.deleteFile(this.fileId);

	}

	@Override
	public byte[] read(int offset, int length) {
		ByteBuffer buffer = this.fsws.readFile(this.fileId, offset, length);
		return buffer.array();
	}

	@Override
	public void write(int offset, byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		this.fsws.writeFile(this.fileId, offset, data.length, buffer);
	}

	@Override
	public int getSize() {
		return this.fsws.getFileSize(this.fileId);
	}

	public int getFileId() {
		return this.fileId;
	}
	
	public FileInfoResponse getFileInfo() {
		int parent = this.fsws.getFolderParent(this.fileId);
		
		String name = this.fsws.getFolderName(this.fileId);
		
		int size = this.fsws.getFileSize(this.fileId);
		
		FileInfoResponse res = new FileInfoResponse(parent, size, name.getBytes());
		return res;
	}

}

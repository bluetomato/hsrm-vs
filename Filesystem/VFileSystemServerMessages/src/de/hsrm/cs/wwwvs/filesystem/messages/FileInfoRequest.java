package de.hsrm.cs.wwwvs.filesystem.messages;

public class FileInfoRequest implements Payload {
	private final int handle;

	public FileInfoRequest(int handle) {
		super();
		this.handle = handle;
	}

	public int getHandle() {
		return handle;
	}
}
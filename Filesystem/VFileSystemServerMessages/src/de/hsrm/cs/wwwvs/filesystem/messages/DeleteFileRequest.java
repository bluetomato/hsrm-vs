package de.hsrm.cs.wwwvs.filesystem.messages;

public class DeleteFileRequest implements Payload {
	private final int handle;

	public DeleteFileRequest(int handle) {
		super();
		this.handle = handle;
	}

	public int getHandle() {
		return handle;
	}
}
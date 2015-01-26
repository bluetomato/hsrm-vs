package de.hsrm.cs.wwwvs.filesystem.messages;

public class NewFolderResponse implements Payload {
	private final int handle;

	public NewFolderResponse(int handle) {
		super();
		this.handle = handle;
	}

	public int getHandle() {
		return handle;
	}
}
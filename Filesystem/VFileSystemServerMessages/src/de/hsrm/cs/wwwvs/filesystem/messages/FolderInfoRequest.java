package de.hsrm.cs.wwwvs.filesystem.messages;

public class FolderInfoRequest implements Payload {
	private final int handle;

	public FolderInfoRequest(int handle) {
		super();
		this.handle = handle;
	}

	public int getHandle() {
		return handle;
	}
}
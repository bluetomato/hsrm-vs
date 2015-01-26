package de.hsrm.cs.wwwvs.filesystem.messages;

public class DeleteFolderRequest implements Payload {
	private final int handle;

	public DeleteFolderRequest(int handle) {
		super();
		this.handle = handle;
	}

	public int getHandle() {
		return handle;
	}
}
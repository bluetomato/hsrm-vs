package de.hsrm.cs.wwwvs.filesystem.messages;

public class NewFileResponse implements Payload {
	private final int handle;

	public NewFileResponse(int handle) {
		super();
		this.handle = handle;
	}

	public int getHandle() {
		return handle;
	}
}
package de.hsrm.cs.wwwvs.filesystem.messages;

public class NewFileRequest implements Payload {
	private final int parent;
	private final byte[] name;

	public NewFileRequest(int parent, byte[] name) {
		super();
		this.parent = parent;
		this.name = name;
	}

	public int getParent() {
		return parent;
	}

	public byte[] getName() {
		return name;
	}

}

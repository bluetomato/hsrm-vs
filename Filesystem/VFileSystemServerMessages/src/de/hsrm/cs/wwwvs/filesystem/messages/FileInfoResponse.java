package de.hsrm.cs.wwwvs.filesystem.messages;

public class FileInfoResponse implements Payload {
	private final int parent;
	private final int size;
	private final byte[] name;

	public FileInfoResponse(int parent, int size, byte[] name) {
		super();
		this.parent = parent;
		this.size = size;
		this.name = name;
	}

	public int getParent() {
		return parent;
	}

	public int getSize() {
		return size;
	}

	public byte[] getName() {
		return name;
	}

}
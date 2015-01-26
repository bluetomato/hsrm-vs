package de.hsrm.cs.wwwvs.filesystem.messages;

public class WriteFileRequest implements Payload {
	private final int handle;
	private final int offset;
	private final byte[] data;

	public WriteFileRequest(int handle, int offset, byte[] data) {
		super();
		this.handle = handle;
		this.offset = offset;
		this.data = data;
	}

	public int getHandle() {
		return handle;
	}

	public int getOffset() {
		return offset;
	}

	public byte[] getData() {
		return data;
	}

}
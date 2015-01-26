package de.hsrm.cs.wwwvs.filesystem.messages;

public class ReadFileRequest implements Payload {
	private final int handle;
	private final int offset;
	private final int length;

	public ReadFileRequest(int handle, int offset, int length) {
		super();
		this.handle = handle;
		this.offset = offset;
		this.length = length;
	}

	public int getHandle() {
		return handle;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

}
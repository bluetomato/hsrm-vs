package de.hsrm.cs.wwwvs.filesystem.messages;

public class ReadFileResponse implements Payload {
	private final byte[] data;

	public byte[] getData() {
		return data;
	}

	public ReadFileResponse(byte[] data) {
		super();
		this.data = data;
	}

}
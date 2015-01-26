package de.hsrm.cs.wwwvs.filesystem.messages;

public class ErrorResponse implements Payload {
	private final byte errorCode;
	private final byte[] msg;

	public ErrorResponse(byte errorCode, byte[] msg) {
		super();
		this.errorCode = errorCode;
		this.msg = msg;
	}

	public byte getErrorCode() {
		return errorCode;
	}

	public byte[] getMsg() {
		return msg;
	}

}
package de.hsrm.cs.wwwvs.filesystem.messages;

public enum PayloadType {
	NEW_FILE_REQUEST((byte) 1), NEW_FILE_RESPONSE((byte) 2), NEW_FOLDER_REQUEST(
			(byte) 3), NEW_FOLDER_RESPONSE((byte) 4), DELETE_FILE_REQUEST(
			(byte) 5), DELETE_FOLDER_REQUEST((byte) 6), FILE_INFO_REQUEST(
			(byte) 7), FILE_INFO_RESPONSE((byte) 8), FOLDER_INFO_REQUEST(
			(byte) 9), FOLDER_INFO_RESPONSE((byte) 10), WRITE_FILE_REQUEST(
			(byte) 11), READ_FILE_REQUEST((byte) 12), READ_FILE_RESPONSE(
			(byte) 13), DELETE_FILE_RESPONSE((byte) 14), DELETE_FOLDER_RESPONSE(
			(byte) 15), WRITE_FILE_RESPONSE((byte) 16), ERROR_RESPONSE(
			(byte) 17);

	private final byte id;

	private PayloadType(byte id) {
		this.id = id;
	}

	public byte getId() {
		return id;
	}
}
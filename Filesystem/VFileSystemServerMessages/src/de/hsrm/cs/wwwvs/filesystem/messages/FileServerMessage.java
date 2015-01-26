package de.hsrm.cs.wwwvs.filesystem.messages;

public class FileServerMessage {
	private final PayloadType payloadType;
	private final Payload payload;

	public FileServerMessage(PayloadType payloadType, Payload payload) {
		super();
		this.payloadType = payloadType;
		this.payload = payload;
	}

	public PayloadType getPayloadType() {
		return payloadType;
	}

	public Payload getPayload() {
		return payload;
	}
}
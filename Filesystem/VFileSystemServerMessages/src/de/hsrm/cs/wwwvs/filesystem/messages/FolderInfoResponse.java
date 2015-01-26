package de.hsrm.cs.wwwvs.filesystem.messages;

public class FolderInfoResponse implements Payload {
	private final int parent;
	private final byte[] name;
	private final int[] files;
	private final int[] folders;

	public FolderInfoResponse(int parent, byte[] name, int[] files,
			int[] folders) {
		super();
		this.parent = parent;
		this.name = name;
		this.files = files;
		this.folders = folders;
	}

	public int getParent() {
		return parent;
	}

	public byte[] getName() {
		return name;
	}

	public int[] getFiles() {
		return files;
	}

	public int[] getFolders() {
		return folders;
	}

}
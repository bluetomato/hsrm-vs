package de.hsrm.cs.wwwvs.filesystem.impl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.hsrm.cs.wwwvs.filesystem.messages.DeleteFileRequest;
import de.hsrm.cs.wwwvs.filesystem.messages.DeleteFolderRequest;
import de.hsrm.cs.wwwvs.filesystem.messages.ErrorResponse;
import de.hsrm.cs.wwwvs.filesystem.messages.FileInfoRequest;
import de.hsrm.cs.wwwvs.filesystem.messages.FileInfoResponse;
import de.hsrm.cs.wwwvs.filesystem.messages.FileServerMessage;
import de.hsrm.cs.wwwvs.filesystem.messages.FolderInfoRequest;
import de.hsrm.cs.wwwvs.filesystem.messages.FolderInfoResponse;
import de.hsrm.cs.wwwvs.filesystem.messages.NewFileRequest;
import de.hsrm.cs.wwwvs.filesystem.messages.NewFileResponse;
import de.hsrm.cs.wwwvs.filesystem.messages.NewFolderRequest;
import de.hsrm.cs.wwwvs.filesystem.messages.NewFolderResponse;
import de.hsrm.cs.wwwvs.filesystem.messages.PayloadType;
import de.hsrm.cs.wwwvs.filesystem.messages.ReadFileRequest;
import de.hsrm.cs.wwwvs.filesystem.messages.ReadFileResponse;
import de.hsrm.cs.wwwvs.filesystem.messages.WriteFileRequest;

/*
 * Die Klasse "Marshaller", in der die Methoden zum Verpacken, bzw. Entpacken von 
"FileServerMessages“ implementiert werden müssen. Nutzen Sie dafür die Klasse 
"java.nio.ByteBuffer“, die bereits Methoden zur Serialisierung von primitiven 
Datentypen bereitstellt.
 */
public class Marshaller {

	static class MarshallingException extends Exception {
		private static final long serialVersionUID = 1L;

		public MarshallingException(String error) {
			super(error);
		}

		public MarshallingException(String error, Throwable t) {
			super(error, t);
		}
	}

	/**
	 * Deserialisiert eine FileServerMessage aus einem Datensegment.
	 * @param data
	 *            der Datenpuffer
	 * @return die deserialisierte Nachricht
	 * @throws MarshallingException
	 */
	public static FileServerMessage unmarshall(byte[] data) throws MarshallingException {
		FileServerMessage msg = null;
		
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int payloadtype = buffer.get();

		if (payloadtype == PayloadType.FILE_INFO_RESPONSE.getId()) {
			int parent = buffer.getInt();
			int size = buffer.getInt();
			int name_length = buffer.get();
			byte[] name = new byte[name_length];
			buffer.get(name);
			
			FileInfoResponse fiiRes = new FileInfoResponse(parent, size, name);
			msg = new FileServerMessage(PayloadType.FILE_INFO_RESPONSE, fiiRes);	
			
		}else if (payloadtype == PayloadType.NEW_FILE_RESPONSE.getId()){
			NewFileResponse nfiRes = new NewFileResponse(buffer.getInt());
			msg = new FileServerMessage(PayloadType.NEW_FILE_RESPONSE, nfiRes);
			
		}else if (payloadtype == PayloadType.NEW_FOLDER_RESPONSE.getId()){
			NewFolderResponse nfoRes = new NewFolderResponse(buffer.getInt());
			msg = new FileServerMessage(PayloadType.NEW_FOLDER_RESPONSE, nfoRes);
			
		}else if (payloadtype == PayloadType.FOLDER_INFO_RESPONSE.getId()){
			int parent = buffer.getInt();
			int name_length = buffer.get();
			byte[] name = new byte[name_length];
			buffer.get(name);
			int file_count = buffer.getInt();
			//System.out.println("Files: " + file_count);
			int[] files = new int[file_count];
			//byte[] files = new byte[file_count];
			for(int i = 0; i < file_count; i++){
				files[i] = buffer.getInt();
			}
			//buffer.get(files);
			int folder_count = buffer.getInt();
			//byte[] folders = null;
			int[] folders = new int[folder_count];
			//System.out.println("Folders: " + folder_count);
			/*if(folder_count > 0){
				folders = new byte[folder_count];
				buffer.get(folders);
			}*/
			for(int i = 0; i < folder_count; i++){
				folders[i] = buffer.getInt();
			}
			
			FolderInfoResponse foiRes = new FolderInfoResponse(parent, name, files, folders);
			msg = new FileServerMessage(PayloadType.FOLDER_INFO_RESPONSE, foiRes);
			
		}else if (payloadtype == PayloadType.READ_FILE_RESPONSE.getId()){
			int size = buffer.getInt();
			byte[] datas = new byte[size];
			buffer.get(datas);
			
			ReadFileResponse rfRes = new ReadFileResponse(datas);
			msg = new FileServerMessage(PayloadType.READ_FILE_RESPONSE, rfRes);
			
		}else if (payloadtype == PayloadType.DELETE_FILE_RESPONSE.getId()){
			msg = new FileServerMessage(PayloadType.DELETE_FILE_RESPONSE, null);
			
		}else if (payloadtype == PayloadType.DELETE_FOLDER_RESPONSE.getId()){
			msg = new FileServerMessage(PayloadType.DELETE_FOLDER_RESPONSE, null);
			
		}else if (payloadtype == PayloadType.WRITE_FILE_RESPONSE.getId()){
			msg = new FileServerMessage(PayloadType.WRITE_FILE_RESPONSE, null);
			
		}else if (payloadtype == PayloadType.ERROR_RESPONSE.getId()){
			byte error_code = buffer.get();
			int msg_length = buffer.getInt();
			byte[] mesg = null;
			if(msg_length > 0){
				mesg = new byte[msg_length];
				buffer.get(mesg);
			}
			ErrorResponse eRes = new ErrorResponse(error_code, mesg);
			msg = new FileServerMessage(PayloadType.ERROR_RESPONSE, eRes);
		}else{
			System.out.println("Nachricht hatte falschen Payloadtypen.");
		}
		
		return msg;
	}

	/**
	 * Serialisiert eine FileServerMessage in ein Datensegment.
	 * 
	 * @param msg
	 *            die zu serialisierende Nachricht
	 * @return das erzeugte Datensegment
	 * @throws MarshallingException
	 */
	public static byte[] marshall(FileServerMessage msg) throws MarshallingException {
		ByteBuffer buffer = null;
		
		switch(msg.getPayloadType()){
		case DELETE_FILE_REQUEST:
			DeleteFileRequest dfiReq = (DeleteFileRequest) msg.getPayload();
			buffer = ByteBuffer.allocate(Integer.BYTES + Byte.BYTES);
			
			//add id of payloadtype
			buffer.put(PayloadType.DELETE_FILE_REQUEST.getId());
			//add fileid of file we want to be deleted
			buffer.putInt(dfiReq.getHandle());
			break;
			
		case DELETE_FOLDER_REQUEST:
			DeleteFolderRequest dfoReq = (DeleteFolderRequest) msg.getPayload();
			buffer = ByteBuffer.allocate(Integer.BYTES + Byte.BYTES);
			
			//add id of payloadtype
			buffer.put(PayloadType.DELETE_FOLDER_REQUEST.getId());
			//add folder id of folder we want to be deleted
			buffer.putInt(dfoReq.getHandle());
			break;
			
		case FILE_INFO_REQUEST:
			FileInfoRequest fiiReq = (FileInfoRequest) msg.getPayload();
			buffer = ByteBuffer.allocate(Integer.BYTES + Byte.BYTES);
			
			//add id of payloadtype
			buffer.put(PayloadType.FILE_INFO_REQUEST.getId());
			//add id of file 
			buffer.putInt(fiiReq.getHandle());
			break;
			
		case FOLDER_INFO_REQUEST:
			FolderInfoRequest foiReq = (FolderInfoRequest) msg.getPayload();
			buffer = ByteBuffer.allocate(Integer.BYTES + Byte.BYTES);
			
			//add id of payloadtype
			buffer.put(PayloadType.FOLDER_INFO_REQUEST.getId());
			//add id of folder
			buffer.putInt(foiReq.getHandle());
			break;
			
		case NEW_FILE_REQUEST:
			//TODO existiert file schon?
			NewFileRequest nfReq = (NewFileRequest) msg.getPayload();
			
			//laufe files durch
			//für jedes file eine fileinfo
			//testen ob der name schon enthalten ist
			//wenn nicht:
			
			
			
			buffer = ByteBuffer.allocate(Integer.BYTES + (Byte.BYTES * 2) + (byte)nfReq.getName().length );
			
			//add id of payloadtype
			buffer.put(PayloadType.NEW_FILE_REQUEST.getId());
			//add parent
			buffer.putInt(nfReq.getParent());
			//add name_size
			buffer.put((byte) nfReq.getName().length);
			//add name
			buffer.put(nfReq.getName());
			break;
			
		case NEW_FOLDER_REQUEST:
			//TODO existiert folder schon?
			NewFolderRequest nfoReq = (NewFolderRequest) msg.getPayload();
			buffer = ByteBuffer.allocate(Integer.BYTES + (Byte.BYTES * 2) + nfoReq.getName().length);
			
			//add id of payloadtype
			buffer.put(PayloadType.NEW_FOLDER_REQUEST.getId());
			//add parent
			buffer.putInt(nfoReq.getParent());
			//add name size
			buffer.put((byte)nfoReq.getName().length);
			//add name
			buffer.put(nfoReq.getName());
			break;
			
		case READ_FILE_REQUEST:
			ReadFileRequest rfReq = (ReadFileRequest) msg.getPayload();
			buffer = ByteBuffer.allocate((3 * Integer.BYTES) + Byte.BYTES);
			
			//add id of payloadtype
			buffer.put(PayloadType.READ_FILE_REQUEST.getId());
			//add file id
			buffer.putInt(rfReq.getHandle());
			//add offset to start from
			buffer.putInt(rfReq.getOffset());
			//add length to write
			buffer.putInt(rfReq.getLength());
			break;
			
		case WRITE_FILE_REQUEST:
			WriteFileRequest wfReq = (WriteFileRequest) msg.getPayload();
			buffer = ByteBuffer.allocate((3 * Integer.BYTES) + Byte.BYTES + (byte)wfReq.getData().length);
			
			//add id of payloadtype
			buffer.put(PayloadType.WRITE_FILE_REQUEST.getId());
			//add file id we want to write to
			buffer.putInt(wfReq.getHandle());
			//add offset we want to start our writing
			buffer.putInt(wfReq.getOffset());
			//add length of data
			buffer.putInt(wfReq.getData().length);
			//add data
			buffer.put(wfReq.getData());
			break;
			
		default:
			System.out.println("Nachricht hatte falschen Payloadtypen.");
			
			buffer = null;
			break;
		}
		//return buffer as byte array
		return buffer.array();
	}
	
	public static int[] toIntArray(byte[] barr) { 
       if(barr == null){
    	   return null;
       }
       //Pad the size to multiple of 4 
       int size = (barr.length / 4) + ((barr.length % 4 == 0) ? 0 : 1);      

       ByteBuffer bb = ByteBuffer.allocate(size *4); 
       bb.put(barr); 

       //Java uses Big Endian. Network program uses Little Endian. 
       bb.order(ByteOrder.LITTLE_ENDIAN); 
       
       int[] result = new int[size]; 
       bb.rewind(); 
       while (bb.remaining() > 0) { 
    	   result[bb.position()/4] =bb.getInt(); 
       } 

       return result; 
	}
}

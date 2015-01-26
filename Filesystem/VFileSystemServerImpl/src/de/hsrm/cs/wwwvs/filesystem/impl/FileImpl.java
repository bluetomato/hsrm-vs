package de.hsrm.cs.wwwvs.filesystem.impl;

import java.io.IOException;
import java.nio.charset.Charset;

import de.hsrm.cs.wwwvs.filesystem.File;
import de.hsrm.cs.wwwvs.filesystem.impl.Marshaller.MarshallingException;
import de.hsrm.cs.wwwvs.filesystem.messages.DeleteFileRequest;
import de.hsrm.cs.wwwvs.filesystem.messages.ErrorResponse;
import de.hsrm.cs.wwwvs.filesystem.messages.FileInfoRequest;
import de.hsrm.cs.wwwvs.filesystem.messages.FileInfoResponse;
import de.hsrm.cs.wwwvs.filesystem.messages.FileServerMessage;
import de.hsrm.cs.wwwvs.filesystem.messages.PayloadType;
import de.hsrm.cs.wwwvs.filesystem.messages.ReadFileRequest;
import de.hsrm.cs.wwwvs.filesystem.messages.ReadFileResponse;
import de.hsrm.cs.wwwvs.filesystem.messages.WriteFileRequest;

public class FileImpl implements File {
	private final int fileId;
	private final Connection con;
	private static final String charsetName = "US-ASCII";

	public FileImpl(int fileId, Connection con){
		this.fileId = fileId;
		this.con = con;
	}

	@Override
	public String getName() {
		try {
			//create FileInfoRequest with given fileId
			FileInfoRequest fiReq = new FileInfoRequest(this.fileId);
			//create FileServerMessage with Payloadtype and Request as Payload
			FileServerMessage fsReq = new FileServerMessage(PayloadType.FILE_INFO_REQUEST, fiReq);
			
			//send FileServerMessage to Server and receive Respone as FileServerMessage
			FileServerMessage fsRes = this.con.remoteOperation(fsReq);
			//cast Payload to Response
			FileInfoResponse fiRes = (FileInfoResponse) fsRes.getPayload();
			
			//return name as String
			return new String(fiRes.getName(), Charset.forName(charsetName));
			
		} catch (MarshallingException | IOException e) {
			System.out.println("Fehler: " + e);
		}
		
		return null;		
	}

	@Override
	public void delete() {
		try {
			//create DeleteInfoRequest with given fileId
			DeleteFileRequest dfReq = new DeleteFileRequest(this.fileId);
			//create FileServerMessage with Payloadtype and Request as Payload
			FileServerMessage fsReq = new FileServerMessage(PayloadType.DELETE_FILE_REQUEST, dfReq);
			
			//send FileServerMessage to Server and receive Response
			FileServerMessage err = this.con.remoteOperation(fsReq);
			//if err isn't null: file was not deleted: print error
			if(null != err){
				ErrorResponse er = (ErrorResponse) err.getPayload();
				String erMsg = new String(er.getMsg(), Charset.forName(charsetName));
				System.out.println("Fehler: " + erMsg);
			}
			
		} catch (MarshallingException | IOException e) {
			System.out.println("Fehler: " + e);
		}

	}

	@Override
	public byte[] read(int offset, int length) {
		try {
			//create Request
			ReadFileRequest rfReq = new ReadFileRequest(this.fileId, offset, length);
			//create FileServerMessage with Payloadtype and Request as Payload
			FileServerMessage fsReq = new FileServerMessage(PayloadType.READ_FILE_REQUEST, rfReq);
			
			//send FileServerMessage to Server and receive Respone as FileServerMessage
			FileServerMessage fsRes = this.con.remoteOperation(fsReq);
			//cast Payload to Response
			ReadFileResponse rfRes = (ReadFileResponse) fsRes.getPayload();
			
			//if data-length is smaller as offset+length OR offset a negative number: return empty buffer[]
			if(offset + length > rfRes.getData().length || offset < 0){
				return new byte[0];
			}
			
			//create String out of data
			String read = new String(rfRes.getData(), Charset.forName(charsetName));
			return read.getBytes();//buffer.array();
			
		} catch (MarshallingException | IOException e) {
			System.out.println("Fehler: " + e);
		}
		
		return null;
	}

	@Override
	public void write(int offset, byte[] data) {
		try {
			if(offset >= 0){
				//create Request
				WriteFileRequest wfReq = new WriteFileRequest(this.fileId, offset, data);
				//create FileServerMessage with Payloadtype and Request as Payload
				FileServerMessage fsReq = new FileServerMessage(PayloadType.WRITE_FILE_REQUEST, wfReq);
				
				//send FileServerMessage to Server
				FileServerMessage err = this.con.remoteOperation(fsReq);
				
				//if err isn't null: file couln't be wrote: print error
				if(null != err){
					ErrorResponse er = (ErrorResponse) err.getPayload();
					String erMsg = new String(er.getMsg(), Charset.forName(charsetName));
					System.out.println("Fehler: " + erMsg);
				}
			}
			
		} catch (MarshallingException | IOException e) {
			System.out.println("Fehler: " + e);
		}

	}

	@Override
	public int getSize() {
		try {
			//create FileInfoRequest with given fileId
			FileInfoRequest fiReq = new FileInfoRequest(this.fileId);
			//create FileServerMessage with Payloadtype and Request as Payload
			FileServerMessage fsReq = new FileServerMessage(PayloadType.FILE_INFO_REQUEST, fiReq);
			
			//send FileServerMessage to Server and receive Respone as FileServerMessage
			FileServerMessage fsRes = this.con.remoteOperation(fsReq);
			
			//if err isn't null: file was not deleted: print error
			if(fsRes.getPayloadType() == PayloadType.ERROR_RESPONSE){
				ErrorResponse er = (ErrorResponse) fsRes.getPayload();
				String erMsg = new String(er.getMsg(), Charset.forName(charsetName));
				System.out.println("Fehler: " + erMsg);
				return 0;
			}
			
			//cast Payload to Response
			FileInfoResponse fiRes = (FileInfoResponse) fsRes.getPayload();
			
			//return size
			return fiRes.getSize();
			
		} catch (MarshallingException | IOException e) {
			System.out.println("Fehler: " + e);
		}
		return 0;
	}

	public int getFileId() {
		return this.fileId;
	}

	public FileInfoResponse getFileInfo() {
		FileInfoRequest fi_req = new FileInfoRequest(this.fileId); //build request
		FileServerMessage fireq_msg = new FileServerMessage(		//build FSM with resquest
				PayloadType.FILE_INFO_REQUEST, fi_req);

		try
		{
			FileServerMessage firesp_msg = this.con.remoteOperation(fireq_msg); //send req -> get resp
			FileInfoResponse fi_resp = (FileInfoResponse) firesp_msg
					.getPayload();

			return fi_resp;
		} catch (MarshallingException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public int getParentId() {
		FileInfoResponse fires = getFileInfo();
		return fires.getParent();
	}

}

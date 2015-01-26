/*
 * Stephanie Scholl
 * Matnr: 979993
 *
 */

#include <vfilesystem_server_messages.h>
#include <vfilesystem_server_marshaller.h>
#include <string.h>
#include <stdio.h>
#include <netinet/in.h>

int32_t unmarshall(uint8_t *data, uint32_t size, FileServerMessage **msg_out) {
	*msg_out = malloc(sizeof(FileServerMessage));
	//msg type is in data[0]
	uint8_t msgType = data[0];
	int further = 1;
	//switch msgType: what does the client wants me to do?
	switch (msgType) {
	case NEW_FILE_REQUEST: {
		NewFileRequest *r1 = malloc(sizeof(NewFileRequest)); //we need a file request

		//store parent
		uint32_t parent;
		memcpy(&parent, data + further, sizeof(parent));
		parent = ntohl(parent);
		r1->parent = parent;
		further += sizeof(parent);

		//store name size
		uint8_t name_size;
		memcpy(&name_size, data + further, sizeof(name_size));
		r1->name_size = name_size;
		further += sizeof(name_size);

		//store name
		uint8_t *name = malloc(name_size);
		memcpy(name, data + 6, name_size);
		r1->name = name;

		//init msg_out with new payload-type and payload
		(*msg_out)->payload_type = msgType;
		(*msg_out)->payload = (uint8_t *) r1;
		break;
	}
	case NEW_FOLDER_REQUEST: {
		NewFolderRequest *r3 = malloc(sizeof(NewFolderRequest));

		//store parent
		uint32_t parent;
		memcpy(&parent, data + further, sizeof(parent));
		parent = ntohl(parent);
		r3->parent = parent;
		further += sizeof(parent);

		//store name size
		uint8_t name_size;
		memcpy(&name_size, data + further, sizeof(name_size));
		r3->name_size = name_size;
		further += sizeof(name_size);

		//store name
		uint8_t *name = malloc(name_size);
		memcpy(name, data + 6, name_size);
		r3->name = name;

		//init msg_out with new payload-type and payload
		(*msg_out)->payload_type = msgType;
		(*msg_out)->payload = (uint8_t *) r3;
		break;
	}
	case DELETE_FILE_REQUEST: {
		DeleteFileRequest *r5 = malloc(sizeof(DeleteFileRequest));

		//store handle (fileid)
		uint32_t handle;
		memcpy(&handle, data + further, sizeof(handle));
		r5->handle = handle;

		//init msg_out with new payload-type and payload
		(*msg_out)->payload_type = msgType;
		(*msg_out)->payload = (uint8_t *) r5;
		break;
	}
	case DELETE_FOLDER_REQUEST: {
		DeleteFolderRequest *r6 = malloc(sizeof(DeleteFolderRequest));

		//store handle (folderid)
		uint32_t handle;
		memcpy(&handle, data + further, sizeof(handle));
		handle = ntohl(handle);
		r6->handle = handle;

		//init msg_out with new payload-type and payload
		(*msg_out)->payload_type = msgType;
		(*msg_out)->payload = (uint8_t *) r6;
		break;
	}
	case FILE_INFO_REQUEST: {
		FileInfoRequest *r7 = malloc(sizeof(FileInfoRequest));

		//store handle (fileid)
		uint32_t handle;
		memcpy(&handle, data + further, sizeof(handle));
		handle = ntohl(handle);
		r7->handle = handle;

		//init msg_out with new payload-type and payload
		(*msg_out)->payload_type = msgType;
		(*msg_out)->payload = (uint8_t *) r7;
		break;
	}
	case FOLDER_INFO_REQUEST: {
		FolderInfoRequest *r9 = malloc(sizeof(FolderInfoRequest));

		//payload to var handle (folderid)
		uint32_t handle;
		memcpy(&handle, data + further, sizeof(handle));
		handle = ntohl(handle);
		r9->handle = handle;

		//init msg_out with new payload-type and payload
		(*msg_out)->payload_type = msgType;
		(*msg_out)->payload = (uint8_t *) r9;
		break;
	}
	case WRITE_FILE_REQUEST: {
		WriteFileRequest *r11 = malloc(sizeof(WriteFileRequest));

		//store fileid
		uint32_t handle;
		memcpy(&handle, data + further, sizeof(handle));
		handle = ntohl(handle);
		r11->handle = handle;
		further += sizeof(handle);

		//store offset
		uint32_t offset;
		memcpy(&offset, data + further, sizeof(offset));
		offset = ntohl(offset);
		r11->offset = offset;
		further += sizeof(offset);

		//store length
		uint32_t length;
		memcpy(&length, data + further, sizeof(length));
		length = ntohl(length);
		r11->length = length;

		//store data we want to write
		uint8_t *datas = malloc(length);
		memcpy(datas, data + 13, length);
		r11->data = datas;

		//init msg_out with new payload-type and payload
		(*msg_out)->payload_type = msgType;
		(*msg_out)->payload = (uint8_t *) r11;
		break;
	}
	case READ_FILE_REQUEST: {
		ReadFileRequest *r12 = malloc(sizeof(ReadFileRequest));

		//store file id
		uint32_t handle;
		memcpy(&handle, data + further, sizeof(handle));
		handle = ntohl(handle);
		r12->handle = handle;
		further += sizeof(handle);

		//store offset
		uint32_t offset;
		memcpy(&offset, data + further, sizeof(offset));
		offset = ntohl(offset);
		r12->offset = offset;
		further += sizeof(offset);

		//store length we want to read
		uint32_t length;
		memcpy(&length, data + further, sizeof(length));
		length = ntohl(length);
		r12->length = length;

		//init msg_out with new payload-type and payload
		(*msg_out)->payload_type = msgType;
		(*msg_out)->payload = (uint8_t *) r12;
		break;
	}
	default: //return -1 if theres no such payload-type
		return -1;
	}
	return 0;
}

int32_t marshall(uint8_t *data, uint32_t size, FileServerMessage *msg) {
	int further = 1;
	data[0] = msg->payload_type;

	switch (msg->payload_type) {
	case NEW_FILE_RESPONSE: {
		NewFileResponse *r2 = (NewFileResponse *) msg->payload;

		//copy fileid of new file in r2
		r2->handle = htonl(r2->handle);
		memcpy(data + further, &r2->handle, sizeof(r2->handle));

		break;
	}
	case NEW_FOLDER_RESPONSE:{
		NewFolderResponse *r4 = (NewFolderResponse *) msg->payload;

		//store new folderid in r4
		r4->handle = htonl(r4->handle);
		memcpy(data + further, &r4->handle, sizeof(r4->handle));

		break;}
	case FILE_INFO_RESPONSE: {
		FileInfoResponse *r8 = (FileInfoResponse *) msg->payload;

		//store parent (in networkbyteorder)
		r8->parent = htonl(r8->parent);
		memcpy(data + further, &r8->parent, sizeof(r8->parent));
		further += sizeof(r8->parent);

		//store size (in networkbyteorder)
		r8->size = htonl(r8->size);
		memcpy(data + further, &r8->size, sizeof(r8->size));
		further += sizeof(r8->size);

		//store name length
		memcpy(data + further, &r8->name_length, sizeof(r8->name_length));
		further += sizeof(r8->name_length);

		//store name
		memcpy(data + further, r8->name, r8->name_length);
		break;
	}
	case FOLDER_INFO_RESPONSE: {
		FolderInfoResponse *r10 = (FolderInfoResponse *) msg->payload;
		//hold data with my own byteorder
		uint32_t tmp_folder_count = r10->folder_count;
		uint32_t tmp_file_count = r10->file_count;

		//store parent (in networkbyteorder)
		r10->parent = htonl(r10->parent);
		memcpy(data + further, &r10->parent, sizeof(r10->parent));
		further += sizeof(r10->parent);

		//store name length
		memcpy(data + further, &r10->name_length, sizeof(r10->name_length));
		further += sizeof(r10->name_length);

		//store name
		memcpy(data + further, r10->name, r10->name_length);
		further += r10->name_length;

		//store file counter (in networkbyteorder)
		r10->file_count = htonl(r10->file_count);
		memcpy(data + further, &r10->file_count, sizeof(r10->file_count));
		further += sizeof(r10->file_count);

		//store every file id (in networkbyteorder)
		int i;
		for (i = 0; i < tmp_file_count; i++) {
			r10->files[i] = htonl(r10->files[i]);
			memcpy(data + further, &r10->files[i], sizeof(uint32_t));
			further += sizeof(uint32_t);
		}

		//store folder counter (in networkbyteorder)
		r10->folder_count = htonl(r10->folder_count);
		memcpy(data + further, &r10->folder_count, sizeof(r10->folder_count));
		further += sizeof(r10->folder_count);

		//store every folderid (in networkbyteorder)
		for (i = 0; i < tmp_folder_count; i++) {
			r10->folders[i] = htonl(r10->folders[i]);
			memcpy(data + further, &r10->folders[i], sizeof(uint32_t));
			further += sizeof(uint32_t);
		}

		break;
	}
	case READ_FILE_RESPONSE: {
		ReadFileResponse *r13 = (ReadFileResponse *) msg->payload;

		//We need this size in hostbyteorder
		uint32_t tmp_size = r13->size;
		//store size (in networkbyteorder)
		r13->size = htonl(r13->size);
		memcpy(data + further, &r13->size, sizeof(r13->size));
		further += sizeof(r13->size);

		//store data with length tmp_size (size in hostbyteorder)
		memcpy(data + further, r13->data, tmp_size);

		break;
	}
	case DELETE_FILE_RESPONSE: //nothing to do here
		break;
	case DELETE_FOLDER_RESPONSE: //nothing to do here
		break;
	case WRITE_FILE_RESPONSE: //nothing to do here
		break;
	case ERROR_RESPONSE: {
		printf("fehler\n");
		ErrorResponse *r17 = (ErrorResponse *) msg->payload;

		//store error code
		memcpy(data + further, &r17->error_code, sizeof(r17->error_code));
		further += sizeof(r17->error_code);

		//store errormessage length (in networkbyteorder)
		r17->msg_length = htonl(r17->msg_length);
		memcpy(data + further, &r17->msg_length, sizeof(r17->msg_length));
		further += sizeof(r17->msg_length);

		//store errormessage
		memcpy(data + further, &r17->msg, sizeof(r17->msg));
		
		break;
	}
	default: //return -1 if theres no such payload-type
		return -1;
	}
	return 0;
}

void free_message(FileServerMessage *msg) {
	free(msg);
	msg = NULL;
}

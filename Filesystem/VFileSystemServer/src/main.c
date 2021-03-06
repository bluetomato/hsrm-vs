/*
 * Stephanie Scholl
 * Matnr: 979993
 *
 */

#include "vfilesystem_api.h"
#include <vfilesystem_server_messages.h>
#include <vfilesystem_server_marshaller.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <netdb.h>
#include <unistd.h>
#include <sys/signal.h>
#include <time.h>
#include <stdlib.h>
//posix
#include <pthread.h>

//maximum size of pending connections queue
#define MAX_THREADS 10
pthread_t thread;
pthread_mutex_t mymutex;

/**
 * Connection handler
 */
void* connection_handler(void* fd) {
	//get socket descriptor
	int connfd = *(int*) fd;

	uint32_t msgLength;
	uint8_t *msgData;

	//lock while thread is working with this stuff
	//pthread_mutex_lock(&mymutex);
	/*
	 * Receive Data from any Client
	 * while we get a new message length
	 */
	while (0 < recv(connfd, &msgLength, 4, 0)) {
		//number in hostbyteorder
		msgLength = ntohl(msgLength);

		//get message type and data
		msgData = malloc(msgLength);
		int recvd = recv(connfd, msgData, msgLength, 0);
		if (-1 == recvd) {
			printf("Fehler beim Empfangen der restlichen Nachricht.\n");
			exit(-1);
		}

		FileServerMessage *msg_in; //to work with
		FileServerMessage *msg_out; //to send back
		uint32_t size;

		//deserialize msgData
		if (-1 == unmarshall(msgData, msgLength, &msg_in)) {
			printf("Fehler beim entpacken.\n");
			exit(-1);
		}
		//search for payloadtype
		switch (msg_in->payload_type) {
			case NEW_FILE_REQUEST: {
				NewFileRequest *req = (NewFileRequest *) msg_in->payload; //parse msg payload, so that we can use it

				//try to create a new file
				FileID ret = fs_new_file((char *) req->name, req->parent);
				//if this goes wrong (file exists, etc)
				if (0 > ret) {
					//create Error Response
					char *message = "Die Datei konnte nicht erstellt werden.\n";
					int message_length = strlen(message);

					ErrorResponse *err = malloc(sizeof(ErrorResponse));
					err->error_code = ERROR_NEWFILE;
					err->msg = malloc(message_length + 1);
					err->msg = (uint8_t *) message;
					err->msg_length = message_length;

					size = sizeof(uint8_t)
							+ sizeof(err->error_code)
							+ sizeof(err->msg_length)
							+ err->msg_length;

					msg_out = malloc(sizeof(FileServerMessage));
					msg_out->payload_type = ERROR_RESPONSE;
					msg_out->payload = (uint8_t *) err;
					break;
				}

				//otherwise build a File Server Message with stuff we need to send back to the client
				msg_out = malloc(sizeof(FileServerMessage));
				msg_out->payload_type = NEW_FILE_RESPONSE; //payloadtype changes to an response

				//create a new File Response
				NewFileResponse *nfr = malloc(sizeof(NewFileResponse));
				nfr->handle = ret;

				//get size of the message we need to transfer
				size = sizeof(uint8_t) + sizeof(nfr->handle); //size of payloadtype + size of fileId we got
				msg_out->payload = (uint8_t *) nfr; //new payload of file server message is our file response
				break;
			}
			case NEW_FOLDER_REQUEST: {
				NewFolderRequest *req = (NewFolderRequest *) msg_in->payload; //parse msg payload, so that we can use it

				//try to create a new folder
				FolderID ret = fs_new_folder((char *) req->name, req->parent);
				//if this fails (folder exists, etc)
				if (-1 == ret) {
					//create Error Message
					char *message = "Der Ordner konnte nicht erstellt werden.\n";
					int message_length = strlen(message);

					ErrorResponse *err = malloc(sizeof(ErrorResponse));
					err->error_code = ERROR_NEWFOLDER;
					err->msg = malloc(message_length + 1);
					err->msg = (uint8_t *) message;
					err->msg_length = message_length;

					size = sizeof(uint8_t)
							+ sizeof(err->error_code)
							+ sizeof(err->msg_length)
							+ err->msg_length;

					msg_out = malloc(sizeof(FileServerMessage));
					msg_out->payload_type = ERROR_RESPONSE;
					msg_out->payload = (uint8_t *) err;
					break;
				}

				//otherwise build a File Server Message with stuff we need to send back to the client
				msg_out = malloc(sizeof(FileServerMessage));
				msg_out->payload_type = NEW_FOLDER_RESPONSE; //change to response

				//response with folderid of new folder
				NewFolderResponse *nfr = malloc(sizeof(NewFolderResponse));
				nfr->handle = ret;

				//size of message: payload + sizeof folderid
				size = sizeof(uint8_t) + sizeof(nfr->handle);
				msg_out->payload = (uint8_t *) nfr; //payload is our response
				break;
			}
			case DELETE_FILE_REQUEST: {
				DeleteFileRequest *req = (DeleteFileRequest *) msg_in->payload; //parse msg payload, so that we can use it

				//try to delete file
				int32_t ret = fs_delete_file(req->handle);
				//if this doesn't work
				if (-1 == ret) {
					//create error message
					char *message = "Die Datei konnte nicht gelöscht werden.\n";
					int message_length = strlen(message);

					ErrorResponse *err = malloc(sizeof(ErrorResponse));
					err->error_code = ERROR_DELETEFILE;
					err->msg = malloc(message_length + 1);
					err->msg = (uint8_t *) message;
					err->msg_length = message_length;

					size = sizeof(uint8_t)
							+ sizeof(err->error_code)
							+ sizeof(err->msg_length)
							+ err->msg_length;

					msg_out = malloc(sizeof(FileServerMessage));
					msg_out->payload_type = ERROR_RESPONSE;
					msg_out->payload = (uint8_t *) err;
					break;
				}

				//otherwise build a File Server Message with stuff we need to send back to the client
				msg_out = malloc(sizeof(FileServerMessage));
				msg_out->payload_type = DELETE_FILE_RESPONSE; //change to response

				//we only send the new payload type; so we need only space for that, payload is null
				size = sizeof(uint8_t);
				msg_out->payload = NULL;
				break;
			}
			case DELETE_FOLDER_REQUEST: {
				DeleteFolderRequest *req = (DeleteFolderRequest *) msg_in->payload; //parse msg payload, so that we can use it

				//try to delete folder
				int32_t ret = fs_delete_folder(req->handle);
				//if this goes wrong
				if (-1 == ret) {
					//create new ErrorMessage
					char *message = "Der Ordner konnte nicht gelöscht werden.\n";
					int message_length = strlen(message);

					ErrorResponse *err = malloc(sizeof(ErrorResponse));
					err->error_code = ERROR_DELETEFOLDER;
					err->msg = malloc(message_length + 1);
					err->msg = (uint8_t *) message;
					err->msg_length = message_length;

					size = sizeof(uint8_t)
							+ sizeof(err->error_code)
							+ sizeof(err->msg_length)
							+ err->msg_length;

					msg_out = malloc(sizeof(FileServerMessage));
					msg_out->payload_type = ERROR_RESPONSE;
					msg_out->payload = (uint8_t *) err;
					break;
				}

				//new file server message with response type
				msg_out = malloc(sizeof(FileServerMessage));
				msg_out->payload_type = DELETE_FOLDER_RESPONSE;

				//we only send the new payload type; so we need only space for that, payload is null
				size = sizeof(uint8_t);
				msg_out->payload = NULL;
				break;
			}
			case FILE_INFO_REQUEST: {
				FileInfoRequest *req = (FileInfoRequest *) msg_in->payload; //parse msg payload, so that we can use it
				//try to get info of file(id)
				FileInfo *ret = fs_file_info(req->handle);
				//we don't get informations (because there's no such file, etc)
				if (NULL == ret) {
					//create new error message
					char *message =	"Die Dateiinformationen konnten nicht gelesen werden.\n";
					int message_length = strlen(message);

					ErrorResponse *err = malloc(sizeof(ErrorResponse));
					err->error_code = ERROR_FILEINFO;
					err->msg = malloc(message_length + 1);
					err->msg = (uint8_t *) message;
					err->msg_length = message_length;

					size = sizeof(uint8_t)
							+ sizeof(err->error_code)
							+ sizeof(err->msg_length)
							+ err->msg_length;

					msg_out = malloc(sizeof(FileServerMessage));
					msg_out->payload_type = ERROR_RESPONSE;
					msg_out->payload = (uint8_t *) err;
					break;
				}

				//otherwise build a File Server Message with stuff we need to send back to the client
				msg_out = malloc(sizeof(FileServerMessage));
				msg_out->payload_type = FILE_INFO_RESPONSE; //new type is a response

				//create a file info response
				FileInfoResponse *fip = malloc(sizeof(FileInfoResponse));

				//fill file info response with data we got (FileInfo)
				fip->parent = ret->parent;
				fip->size = ret->size;
				fip->name_length = ret->name_length;
				fip->name = malloc(ret->name_length);
				memcpy(fip->name, ret->name, ret->name_length);

				//calculate size of message we need to send back
				size = sizeof(fip->parent)
						+ sizeof(size)
						+ fip->name_length
						+ sizeof(fip->name_length)
						+ sizeof(uint8_t);

				//payload is the file info response
				msg_out->payload = (uint8_t *) fip;
				break;
			}
			case FOLDER_INFO_REQUEST: {
				FolderInfoRequest *req = (FolderInfoRequest *) msg_in->payload; //parse msg payload, so that we can use it
				//try to get the folder info we want
				FolderInfo *ret = fs_folder_info(req->handle);
				//if this goes wrong
				if (NULL == ret){//NULL == ret) {
					//create an error message
					char *message =	"Die Ordnerinformationen konnte nicht gelesen werden.\n";
					int message_length = strlen(message);

					ErrorResponse *err = malloc(sizeof(ErrorResponse));
					err->error_code = ERROR_FOLDERINFO;
					err->msg = malloc(message_length + 1);
					err->msg = (uint8_t *) message;
					err->msg_length = message_length;

					size = sizeof(uint8_t)
							+ sizeof(err->error_code)
							+ sizeof(err->msg_length)
							+ err->msg_length;

					msg_out = malloc(sizeof(FileServerMessage));
					msg_out->payload_type = ERROR_RESPONSE;
					msg_out->payload = (uint8_t *) err;
					break;
				}

				//otherwise build a File Server Message with stuff we need to send back to the client
				msg_out = malloc(sizeof(FileServerMessage));
				msg_out->payload_type = FOLDER_INFO_RESPONSE;

				FolderInfoResponse *fip = malloc(sizeof(FolderInfoResponse));
				//fill folderinforesponse with folderinfo details we've got in ret

				fip->file_count = ret->file_count;
				fip->files = malloc(ret->file_count);
				//memcpy(fip->files, ret->files, ret->file_count);

				int i;
				for(i = 0; i < ret->file_count; i++){
					fip->files[i] = ret->files[i];
				}

				fip->folder_count = ret->folder_count;

				fip->folders = malloc(ret->folder_count);
				//memcpy(fip->folders, ret->folders, ret->folder_count);

				for(i = 0; i < ret->folder_count; i++){
					fip->folders[i] = ret->folders[i];
				}
				fip->name = malloc(ret->name_length);
				memcpy(fip->name, ret->name, ret->name_length);

				fip->name_length = ret->name_length;

				fip->parent = ret->parent;

				size = sizeof(fip->parent)
						+ sizeof(fip->name_length)
						+ fip->name_length
						+ sizeof(fip->file_count)
						+ (sizeof(uint32_t) * fip->file_count)
						+ sizeof(fip->folder_count)
						+ (sizeof(uint32_t) * fip->folder_count)
						+ sizeof(uint8_t); //plus payloadtype-space

				msg_out->payload = (uint8_t *) fip;
				break;
			}
			case WRITE_FILE_REQUEST: {
				WriteFileRequest *req = (WriteFileRequest *) msg_in->payload; //parse msg payload, so that we can use it

				int32_t ret = fs_write_file(req->handle, req->offset, req->length, req->data);

				if (-1 == ret) {
					char *message = "Die Datei konnte nicht geschrieben werden.\n";
					int message_length = strlen(message);

					ErrorResponse *err = malloc(sizeof(ErrorResponse));
					err->error_code = ERROR_WRITEFILE;
					err->msg = malloc(message_length + 1);
					err->msg = (uint8_t *) message;
					err->msg_length = message_length;

					size = sizeof(uint8_t)
							+ sizeof(err->error_code)
							+ sizeof(err->msg_length)
							+ err->msg_length;
					msg_out = malloc(sizeof(FileServerMessage));
					msg_out->payload_type = ERROR_RESPONSE;
					msg_out->payload = (uint8_t *) err;
					break;
				}

				msg_out = malloc(sizeof(FileServerMessage));
				msg_out->payload_type = WRITE_FILE_RESPONSE;

				size = sizeof(uint8_t);
				msg_out->payload = NULL;
				break;
			}
			case READ_FILE_REQUEST: {
				ReadFileRequest *req = (ReadFileRequest *) msg_in->payload; //parse msg payload, so that we can use it

				uint8_t *data = malloc(req->length);

				int32_t ret = fs_read_file(req->handle, req->offset, req->length, data);

				if (-1 == ret) {
					char *message = "Die Datei konnte nicht gelesen werden.\n";
					int message_length = strlen(message);

					ErrorResponse *err = malloc(sizeof(ErrorResponse));
					err->error_code = ERROR_READFILE;
					err->msg = malloc(message_length + 1);
					err->msg = (uint8_t *) message;
					err->msg_length = message_length;

					size = sizeof(uint8_t)
							+ sizeof(err->error_code)
							+ sizeof(err->msg_length)
							+ err->msg_length;

					msg_out = malloc(sizeof(FileServerMessage));
					msg_out->payload_type = ERROR_RESPONSE;
					msg_out->payload = (uint8_t *) err;
					break;
				}

				ReadFileResponse *rfr = malloc(sizeof(ReadFileResponse));

				rfr->size = req->length;

				rfr->data = malloc(req->length);
				memcpy(rfr->data, data, req->length);

				//otherwise build a File Server Message with stuff we need to send back to the client
				msg_out = malloc(sizeof(FileServerMessage));
				msg_out->payload_type = READ_FILE_RESPONSE;

				size = sizeof(uint8_t) + sizeof(rfr->size) + rfr->size;
				msg_out->payload = (uint8_t *) rfr;
				break;
			}
		}
		//pthread_mutex_unlock(&mymutex);
		//data we can send back to the client
		uint8_t *send_out = malloc(size);
		marshall(send_out, size, msg_out);

		/*
		 * Send response back to the Client
		 */
		int tmp_size = size;
		size = htonl(size);
		//first size of message in network byteorder
		send(connfd, &size, sizeof(size), 0);
		//second rest of message (payload-type and payload)
		int sent;
		sent = send(connfd, send_out, tmp_size, 0);
		//printf("ZU SENDEN: %d, GESENDET %d\n", tmp_size, sent);
	}
	//close connection
	close(connfd);
	return 0;
}

/*
 * MAIN
 */

int main(int argc, char **args) {
	if (argc != 2) {
		printf("Falsche Anzahl Argumente: ./fileserver <port>");
		exit(-1);
	}

	//store port as integer
	int port = atoi(args[1]);

	printf("Starting VDiskServer\n");

	//initialize filesystem to work with
	if (-1 == fs_init()) {
		printf("Fehler beim Erstellen des Filesystems.\n");
		exit(-1);
	}
	printf("Filesytem wurde erfolgreich initialisiert.\n");

	/*
	 * Server Code
	 */
	int listenfd, connfd, *new_sock;
	struct sockaddr_in server;

	/*
	 * Set up the Server
	 */
	memset(&server, '0', sizeof(struct sockaddr_in));

	//builds internetaddress
	server.sin_family = AF_INET;
	//everyone is allowed to connect to server
	server.sin_addr.s_addr = htonl(INADDR_ANY); /* akzept. jeden */
	server.sin_port = htons(port);

	/*
	 * Open TCP/IP Stream Socket
	 */

	listenfd = socket(AF_INET, SOCK_STREAM, 0);
	if (listenfd < 0) {
		printf("Fehler beim Erstellen eines Sockets.\n");
		exit(-1);
	}
	//bind and register socket to address
	if (0 > bind(listenfd, (struct sockaddr*) &server, sizeof(struct sockaddr_in))) {
		printf("Fehler beim Binden des Sockets.\n");
		exit(-1);
	}

	//listen to socket
	if (0 > listen(listenfd, MAX_THREADS)) {
		printf("Fehler beim Zuhören.\n");
		exit(-1);
	}

	//forever (if theres an accepted connection)
	while ((connfd = accept(listenfd, (struct sockaddr*) NULL, (socklen_t*) sizeof(struct sockaddr_in)))) {
		if (connfd < 0) {
			printf("Fehler beim Accept.\n");
			exit(-1);
		}

		new_sock = (int*) malloc(sizeof(int)); //TODO
		*new_sock = connfd;

		//create a thread and jump to connection_handler
		if (0 > (pthread_create(&thread, NULL, connection_handler, (void*) new_sock))) {
			printf("Es konnte kein neuer Thread erstellt werden.\n");
			exit(-1);

		}
	}
	if (connfd < 0) {
		printf("Accept fehlgeschlagen..\n");
		exit(-1);
	}

	//destroy filesystem
	fs_destroy();

	return 0;
}

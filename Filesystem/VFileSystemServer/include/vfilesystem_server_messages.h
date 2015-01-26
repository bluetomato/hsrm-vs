
#ifndef FILESERVERMESSAGES_H_
#define FILESERVERMESSAGES_H_

#include <stdint.h>

#define NEW_FILE_REQUEST 1
#define NEW_FILE_RESPONSE 2
#define NEW_FOLDER_REQUEST 3
#define NEW_FOLDER_RESPONSE 4
#define DELETE_FILE_REQUEST 5
#define DELETE_FOLDER_REQUEST 6
#define FILE_INFO_REQUEST 7
#define FILE_INFO_RESPONSE 8
#define FOLDER_INFO_REQUEST 9
#define FOLDER_INFO_RESPONSE 10
#define WRITE_FILE_REQUEST 11
#define READ_FILE_REQUEST 12
#define READ_FILE_RESPONSE 13
#define DELETE_FILE_RESPONSE 14
#define DELETE_FOLDER_RESPONSE 15
#define WRITE_FILE_RESPONSE 16
#define ERROR_RESPONSE 17


#define ERROR_NEWFILE 1
#define ERROR_NEWFOLDER 2
#define ERROR_DELETEFILE 3
#define ERROR_DELETEFOLDER 4
#define ERROR_FILEINFO 5
#define ERROR_FOLDERINFO 6
#define ERROR_WRITEFILE 7
#define ERROR_READFILE 8

typedef struct {
	uint8_t payload_type;
	uint8_t *payload;
} FileServerMessage;

typedef struct {
	uint32_t parent;
	uint8_t name_size;
	uint8_t *name;
} NewFileRequest;

typedef struct {
	uint32_t handle;
} NewFileResponse;

typedef struct {
	uint32_t parent;
	uint8_t name_size;
	uint8_t *name;
} NewFolderRequest;

typedef struct {
	uint32_t handle;
} NewFolderResponse;

typedef struct {
	uint32_t handle;
} DeleteFileRequest;

typedef struct {
	uint32_t handle;
} DeleteFolderRequest;

typedef struct {
	uint32_t handle;
} FileInfoRequest;

typedef struct {
	uint32_t parent;
	uint32_t size;
	uint8_t name_length;
	uint8_t *name;
} FileInfoResponse;

typedef struct {
	uint32_t handle;
} FolderInfoRequest;

typedef struct {
	uint32_t parent;
	uint8_t name_length;
	uint8_t *name;
	uint32_t file_count;
	uint32_t *files;
	uint32_t folder_count;
	uint32_t *folders;

} FolderInfoResponse;

typedef struct {
	uint32_t handle;
	uint32_t offset;
	uint32_t length;
	uint8_t *data;
} WriteFileRequest;

typedef struct {
	uint32_t handle;
	uint32_t offset;
	uint32_t length;
} ReadFileRequest;

typedef struct {
	uint32_t size;
	uint8_t *data;
} ReadFileResponse;

typedef struct {
	int8_t error_code;
	uint32_t msg_length;
	uint8_t *msg;
} ErrorResponse;

#endif /* FILESERVERMESSAGES_H_ */

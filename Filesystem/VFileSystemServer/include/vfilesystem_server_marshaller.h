#ifndef MARSHALLER_H_
#define MARSHALLER_H_

#include <stdlib.h>
#include <vfilesystem_server_messages.h>

/**
 * Creates a FileServerMessage from raw data
 * @param data - pointer to the raw data
 * @param size - size of the raw data segment in bytes
 * @param msg_out - pointer to assign the created FileServerMessage to
 * @return 0 on success, negative value on error
 */
int32_t unmarshall(uint8_t *data, uint32_t size, FileServerMessage **msg_out);

/**
 * Frees a FileServerMessage
 * @param msg - the FileServerMessage to free
 */
void free_message(FileServerMessage *msg);

/**
 * Serializes a FileServerMessage into raw data
 * @param data - pointer to the raw data
 * @param size - size of the raw data segment in bytes
 * @param msg - pointer to the FileServerMessage to serialize
 * @return 0 on success, negative value on error
 */
int32_t marshall(uint8_t *data, uint32_t size, FileServerMessage *msg);

/*
 * Handles Connection for every thread
 */
void* connection_handler(void* fd);

#endif /* MARSHALLER_H_ */

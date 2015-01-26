CC      	= /usr/bin/gcc
CFLAGS  	= -Wall -g -O0 
LDFLAGS		= -L../VFileSystem -lvfilesystem
INCLUDES 	= -Iinclude -I../VFileSystem/include

all: fileserver 

help: 
	@echo "\n################################################################################"
	@echo "# Targets:\t\t\t\t\t\t\t\t\t\t\t\t#"
	@echo "# fileserver \t- creates the file system library and the test executeable \t#"
	@echo "# clean \t- removes the objects \t\t\t\t\t\t#"
	@echo "# cleanall \t- removes the objects and the binaries \t\t\t\t#"
	@echo "# run \t\t- runs executeable \t\t\t\t\t\t#"
	@echo "################################################################################\n"
	
	
fileserver: vfilesystem_server_marshaller.o main.o
	$(MAKE) -C ../VFileSystem libvfilesystem.a
	$(CC) $^ -lpthread -o $@ $(LDFLAGS)

run:
	./fileserver 8080
	
%.o: ./src/%.c
	$(CC) $(INCLUDES) $(CFLAGS) -c $< -o $@

clean:
	@rm -rf fileserver vfilesystem_server_marshaller.o main.o

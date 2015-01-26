package de.hsrm.cs.wwwvs.filesystem.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

import de.hsrm.cs.wwwvs.filesystem.Directory;
import de.hsrm.cs.wwwvs.filesystem.File;
import de.hsrm.cs.wwwvs.filesystem.Filesystem;

/**
 * Einfacher CLI Client für FileServer-Aufgabe
 * 
 * @author Andreas Textor
 */
public class ClientCLI {
	private static Filesystem fs;
	private static Directory current;

	private interface ArgRunnable {
		void run(String arg) throws Throwable;
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	enum Command {
		ls("ls", "Listet Dateien und Verzeichnisse", new ArgRunnable() {
			public void run(String arg) throws Throwable {
				final Directory c = ClientCLI.current;
				int items = c.listDirectories().size() + c.listFiles().size();
				System.out.printf("drw-r--r- %4d %s\n", items, ".");

				Directory parent = c.getParent();
				if (parent != null) {
					items = parent.listDirectories().size() + parent.listFiles().size();
					System.out.printf("drw-r--r- %4d %s\n", items, "..");
				}

				for (Directory d : c.listDirectories()) {
					items = d.listDirectories().size() + d.listFiles().size();
					System.out.printf("drw-r--r- %4d %s\n", items, d.getName());
				}
				for (File f : c.listFiles()) {
					System.out.printf(" rw-r--r- %4d %s\n", f.getSize(), f.getName());
				}
			}
		}), pwd("pwd", "Gibt das aktuelle Verzeichnis aus", new ArgRunnable() {
			public void run(String arg) throws Throwable {
				System.out.println(getFullPath(ClientCLI.current));
			}
		}), mkdir("mkdir", "Legt ein neues Verzeichnis an", new ArgRunnable() {
			public void run(String arg) throws Throwable {
				ClientCLI.current.createDirectory(arg);
			}
		}), touch("touch", "Legt eine neue Datei an", new ArgRunnable() {
			public void run(String arg) throws Throwable {
				ClientCLI.current.createFile(arg);
			}
		}), cd("cd", "Wechselt in ein Verzeichnis", new ArgRunnable() {
			public void run(String arg) throws Throwable {
				if (arg == null) {
					current = fs.getRoot();
				} else if (arg.equals("..")) {
					Directory parent = current.getParent();
					if (parent != null)
						current = parent;
				} else {
					final Directory d = getDirectory(current, arg);

					if (d == null) {
						System.out.printf("Verzeichnis nicht gefunden: %s\n", arg);
					} else {
						current = d;
					}
				}
			}
		}), cat("cat", "Gibt den Inhalt einer Datei aus", new ArgRunnable() {
			public void run(String arg) throws Throwable {
				final File f = getFile(current, arg);
				if (f == null) {
					System.out.printf("Datei nicht gefunden: %s\n", arg);
				} else {
					byte[] b = f.read(0, f.getSize());
					System.out.println(new String(b));
				}
			}
		}), cathex("cathex", "Gibt den Inhalt einer Datei in hex aus", new ArgRunnable() {
			public void run(String arg) throws Throwable {
				final File f = getFile(current, arg);
				if (f == null) {
					System.out.printf("Datei nicht gefunden: %s\n", arg);
				} else {
					byte[] b = f.read(0, f.getSize());
					System.out.println(bytesToHex(b));
				}
			}
		}), edit("edit",
				"Editiert eine Datei an einem Offset. Nutzung: edit datei offset inhalt, z.B.: edit foo 0 Hallo Welt",
				new ArgRunnable() {
					public void run(String arg) throws Throwable {
						final String[] a = arg.split(" ", 3);
						final String filename = a[0];
						final int offset = Integer.parseInt(a[1]);
						final String content = a[2];
						final File f = getFile(current, filename);

						if (f == null) {
							System.out.printf("Datei nicht gefunden: %s\n", filename);
						} else {
							f.write(offset, content.getBytes());
						}
					}
				}), echo("echo", "Schreibt in eine Datei. Nutzung: echo datei inhalt, z.B.: echo foo Hallo Welt",
				new ArgRunnable() {
					public void run(String arg) throws Throwable {
						final String[] a = arg.split(" ", 2);
						if (a == null || a.length < 2) {
							System.out.println(Command.echo.helpString);
							return;
						}

						final String fileName = a[0];
						File f = getFile(current, fileName);
						if (f == null) {
							ClientCLI.current.createFile(fileName);
							f = getFile(current, fileName);
						}

						f.write(0, a[1].getBytes());
					}
				}), rm("rm", "Löscht eine Datei", new ArgRunnable() {
			public void run(String arg) throws Throwable {
				final File f = getFile(current, arg);
				if (f != null)
					f.delete();
			}
		}), rmdir("rmdir", "Löscht ein Verzeichnis", new ArgRunnable() {
			public void run(String arg) throws Throwable {
				final Directory d = getDirectory(current, arg);
				if (d != null)
					d.delete();
			}
		}), help("help", "Gibt die möglichen Befehle aus", new ArgRunnable() {
			public void run(String arg) {
				for (Command c : Command.values()) {
					System.out.printf("%10s - %s\n", c, c.helpString);
				}
			}
		});

		private final String cmd;
		private final String helpString;
		private final ArgRunnable runner;

		Command(final String cmd, final String helpString, final ArgRunnable runner) {
			this.cmd = cmd;
			this.helpString = helpString;
			this.runner = runner;
		}

		void run(final String arg) {
			try {
				this.runner.run(arg);
			} catch (final Throwable t) {
				System.out.printf("Fehler beim Ausführen von Kommando %s: %s: %s\n", this,
						t.getClass().getSimpleName(), t.getMessage());
				t.printStackTrace();
			}
		}
	}

	public static Directory getDirectory(Directory parent, String name) throws RemoteException {
		for (Directory dir : parent.listDirectories()) {
			if (dir.getName().equals(name))
				return dir;
		}

		return null;
	}

	public static File getFile(Directory parent, String name) throws RemoteException {
		for (File file : parent.listFiles()) {
			if (file.getName().equals(name))
				return file;
		}

		return null;
	}

	public static String getFullPath(Directory current) throws RemoteException {
		String path = current.getName();
		Directory parent = current.getParent();
		while (parent != null) {
			path = parent.getName() + "/" + path;
			parent = parent.getParent();
		}

		return path;
	}

	public ClientCLI(final Filesystem filesystem) {
		fs = filesystem;
		try {
			current = fs.getRoot();
		} catch (Throwable t) {
			System.out.println("Konnte Wurzelverzeichnis nicht lesen");
			t.printStackTrace();
			return;
		}

		final BufferedReader d = new BufferedReader(new InputStreamReader(System.in));

		for (;;) {
			try {

				System.out.printf("%s > ", getFullPath(current)); 
			} catch (Throwable t) {
				System.out.printf("<ERROR: %s> >", t.getMessage());
				t.printStackTrace();
			}

			String s;

			try {
				s = d.readLine();
			} catch (IOException e) {
				System.out.println("Fehler bei Eingabe");
				return;
			}

			Command cmd = null;

			final String[] inp = s.split(" ", 2);
			for (Command c : Command.values()) {
				if (c.cmd.equals(inp[0]))
					cmd = c;
			}

			if (cmd == null) {
				System.out.println("Ungültiger Befehl.");
				Command.help.run(null);
				continue;
			}

			String arg = inp.length > 1 ? inp[1] : null;

			try {
				cmd.run(arg);
			} catch (Throwable t) {
				System.out.printf("Fehler bei Aufruf von Befehl: %s: %s: %s\n", arg, t.getClass().getSimpleName(),
						t.getMessage());
				t.printStackTrace();
			}
		}
	}
}

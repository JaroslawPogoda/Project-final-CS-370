import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Server extends UnicastRemoteObject implements Interface {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private static String address;

	protected Server(int port) throws RemoteException {
		super(port);
	}

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.out.println("Syntax - java Server <host port>");
			System.exit(1);
		}

		address = InetAddress.getLocalHost().getHostAddress();

		int port = Integer.parseInt(args[0]);

		LocateRegistry.createRegistry(port);
		Server server = new Server(port);

		Naming.rebind("rmi://" + address + "/RMI", server);

		System.out.println("Server running...");

		final Registry registry = LocateRegistry.getRegistry(address, port);
		final String[] boundNames = registry.list();
		System.out.println("Names bound to RMI registry at host " + address + " and port " + port + ":");
		for (final String name : boundNames) {
			System.out.println(name);
		}
	}

	public String getAddress() {
		return address;
	}

	@Override
	public byte[] fileContents(String fileName) throws IOException {
		String path = ".." + File.separator + "src" + File.separator + fileName;

		InputStream input = new FileInputStream(path);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];

		for (int length = 0; (length = input.read(buffer)) > 0;) {
			output.write(buffer, 0, length);
		}

		byte[] bytes = output.toByteArray();

		input.close();
		return bytes;
	}

	@Override
	public void readFile(String srcFileName, String socketIP, int socketPort, String remoteFileName)
			throws RemoteException {

		Socket socket;
		PrintWriter pw = null;
		File fileName = new File(srcFileName);
		BufferedReader brf = null;

		try {
			System.out.println("File Name's Path is " + fileName.getAbsolutePath());
			brf = new BufferedReader(new InputStreamReader(new FileInputStream(fileName.getAbsolutePath())));

		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

		try {
			try {
				System.out.println("Server address connected to is " + socketIP + " and port is " + socketPort);

				socket = new Socket(socketIP, socketPort);
				pw = new PrintWriter(socket.getOutputStream(), true);

				String line = brf.readLine();
				while (line != null) {
					System.out.println(line);
					pw.println(line);
					line = brf.readLine();
				}

				brf.close();
				socket.close();

			} catch (UnknownHostException e) {
				System.err.println("Don't know about host.");
				System.exit(1);
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Couldn't get I/O for the connection to server.");
				System.exit(1);
				e.printStackTrace();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<File> getFiles(String extension) throws RemoteException {
		List<File> results = new ArrayList<File>();
		System.out.println("Returning list of files");

		File[] files = new File(".." + File.separator + "src" + File.separator).listFiles();

		for (File file : files) {
			if (file.isFile() && getFileExtension(file).equals(extension)) {
				results.add(file);
				System.out.println(file.getName());
			}
		}

		return results;
	}

	private String getFileExtension(File file) {
		String name = file.getName();
		try {
			return name.substring(name.lastIndexOf(".") + 1);
		} catch (Exception e) {
			return "";
		}
	}

	@Override
	public void sendFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));

		File file1 = new File(file.getName());

		if (!file1.exists()) {
			file1.createNewFile();
		}

		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter writer = new BufferedWriter(fileWriter);

		String line;
		while ((line = reader.readLine()) != null) {
			writer.write(line + "\r\n");
		}

		reader.close();
		writer.close();

		System.out.print(file1.getAbsolutePath());
	}

	@Override
	public void toServer(byte[] entireFile, String fileName, String fileName2, String mergeType) throws IOException {
		String path = ".." + File.separator + "src" + File.separator + fileName;
		File file = new File(path);
		file.createNewFile();

		FileOutputStream fos = new FileOutputStream(path);
		fos.write(entireFile);
		System.out.println("File: " + fileName + " Has been uploaded!");
		fos.close();

		new Merge();
		Merge.mergeXML(fileName, fileName2);
		// myMerge(fileName, fileName2, mergeType);
	}

	
	
	public static void myMerge(String fName1, String fName2, String mergeType) {
		System.out.println("Merge option: " + mergeType);

		List<String> mergedFileContents = new ArrayList<>();
		String path = ".." + File.separator + "src" + File.separator;

		File file1 = null;
		File file2 = null;

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy-h-mm-ssa");
		String formattedDate = sdf.format(date);

		File file3 = new File(path + fName1.substring(0, fName1.lastIndexOf(".")) + "&"
				+ fName2.substring(0, fName2.lastIndexOf(".")) + "-MERGED-" + formattedDate + ".xml");

		try {
			file1 = new File(path + fName1);
			if (!file1.exists()) {
				file1.createNewFile();
			}

			file2 = new File(path + fName2);
			if (!file2.exists()) {
				file2.createNewFile();
			}

			// get contents of first file
			for (String line : Files.readAllLines(Paths.get(file1.getAbsolutePath()))) {
				for (String part : line.split("\\n+")) {
					mergedFileContents.add(part);
				}
			}

			// get contents of second file
			for (String line : Files.readAllLines(Paths.get(file2.getAbsolutePath()))) {
				for (String part : line.split("\\n+")) {
					mergedFileContents.add(part);
				}
			}

			if (mergeType.equals("1")) {
				Collections.sort(mergedFileContents);
			} else if (mergeType.equals("2")) {
				Collections.sort(mergedFileContents);
				Set<String> set = new HashSet<String>(mergedFileContents);

				mergedFileContents.clear();
				mergedFileContents.addAll(set);

				Collections.sort(mergedFileContents);
			} else if (mergeType.equals("3")) {
				Collections.sort(mergedFileContents);
			}

			// print list to new merged file
			PrintWriter pw = new PrintWriter(file3, "UTF-8");
			for (String line : mergedFileContents) {
				pw.println(line);
			}

			pw.close();

		} catch (IOException e) {
			System.err.println(e.getMessage());
		} finally {
			deleteFile(file1);

			System.out.println(mergedFileContents);

			System.out.println(fName1.substring(0, fName1.lastIndexOf(".")) + " has merged with "
					+ fName2.substring(0, fName2.lastIndexOf(".")) + " to --> " + file3.getAbsolutePath());
		}
	}

	private static void deleteFile(File f) {
		// Make sure the file or directory exists and isn't write protected
		if (!f.exists())
			throw new IllegalArgumentException("Delete: no such file or directory: " + f.getName());

		if (!f.canWrite())
			throw new IllegalArgumentException("Delete: write protected: " + f.getName());

		// If it is a directory, make sure it is empty
		if (f.isDirectory()) {
			String[] files = f.list();
			if (files.length > 0)
				throw new IllegalArgumentException("Delete: directory not empty: " + f.getName());
		}

		// Attempt to delete it
		boolean success = f.delete();

		if (!success)
			throw new IllegalArgumentException("Delete: deletion failed");
	}
}
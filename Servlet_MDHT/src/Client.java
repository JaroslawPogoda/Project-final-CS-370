import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.List;

import javax.servlet.http.Part;

public class Client {

	private List<File> files;

	public Client(int port, String hostIP, Part filePart) throws IOException {

		try {
			String url = new String("rmi://" + hostIP + "/RMI");
			Interface service = (Interface) Naming.lookup(url);

			System.out.println("Atempting to send file to server.");

			System.out.println(filePart.getSubmittedFileName());
			service.sendFile(partToFile(filePart));

			System.out.println("Success!");
		} catch (NotBoundException e) {
			System.err.println("Naming lookup failed!");
		}
	}

	public Client(int port, String hostIP, Part filePart, String fileName, String mergeType) throws IOException {

		try {
			String url = new String("rmi://" + hostIP + "/RMI");
			Interface service = (Interface) Naming.lookup(url);

			System.out.println("Atempting to send file to server.");

			System.out.println(filePart.getSubmittedFileName());

			Path p1 = Paths.get(filePart.getSubmittedFileName());

			System.out.println(p1.getFileName().toString());

			service.toServer(fileContents(partToFile(filePart)), p1.getFileName().toString(), fileName, mergeType);

			System.out.println("Success!");
		} catch (NotBoundException e) {
			System.err.println("Naming lookup failed!");
		}
	}

	public Client(int port, String hostIP, String fileExtension) throws IOException {

		try {
			String url = new String("rmi://" + hostIP + "/RMI");
			Interface service = (Interface) Naming.lookup(url);

			System.out.println("Atempting to get list of files with extension '" + fileExtension + "'");

			files = service.getFiles(fileExtension);

			for (File file : files) {
				System.out.println(file);
			}

			System.out.println("Success!");

		} catch (Exception e) {
			System.err.println("Naming lookup failed!");
		}
	}

	public List<File> getFiles() {
		return files;
	}

	public File partToFile(Part filePart) throws IOException {
		InputStream fileContent = filePart.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent));

		File file = new File(filePart.getSubmittedFileName());

		FileWriter fileWritter = new FileWriter(file);
		BufferedWriter writter = new BufferedWriter(fileWritter);

		String line;
		while ((line = reader.readLine()) != null) {
			writter.write(line + "\r\n");
		}

		writter.close();

		return file;
	}

	public byte[] fileContents(File file) throws IOException {

		InputStream input = new FileInputStream(file);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];

		for (int length = 0; (length = input.read(buffer)) > 0;) {
			output.write(buffer, 0, length);
		}

		byte[] bytes = output.toByteArray();

		input.close();
		return bytes;
	}

}
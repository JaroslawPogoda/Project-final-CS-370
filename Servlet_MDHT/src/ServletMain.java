import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/ServletMain")
@MultipartConfig
public class ServletMain extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ServletMain() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");

		List<File> files = callServer();

		for (File file : files) {
			System.out.println(file.getName());
		}

		String url = "/index.jsp";
		ServletContext sc = getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher(url);

		request.setAttribute("hidden", "hidden");
		request.setAttribute("files", files);
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("text/html");

		PrintWriter pw = response.getWriter();

		List<File> files = callServer();

		for (File file : files) {
			System.out.println(file.getName());
		}

		request.setAttribute("files", files);

		String fileFromServer = request.getParameter("file_from_server");
		String mergeMethod = request.getParameter("optionsRadios");
		Part fileFromClient;

		fileFromClient = request.getPart("file_from_client");
		sendToServer(fileFromClient, fileFromServer, mergeMethod);
		pw.append(fileFromServer);

		request.setAttribute("hidden", "visible");
		request.setAttribute("file1", getFileName(fileFromClient));
		request.setAttribute("file2", fileFromServer);

		RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
		dispatcher.forward(request, response);

	}

	private List<File> callServer() throws IOException {
		System.out.println("Yo, Client!");

		Client client = new Client(2099, "192.168.0.10", "xml");

		List<File> files = client.getFiles();
		return files;
	}

	private String getFileName(Part part) {
		String name = part.getSubmittedFileName();
		try {
			return name.substring(name.lastIndexOf(File.separator) + 1);
		} catch (Exception e) {
			return "";
		}
	}

	private void sendToServer(Part filePart, String fileFromServer, String mergeType) throws IOException {
		new Client(2099, "192.168.0.10", filePart, fileFromServer, mergeType);
	}
}
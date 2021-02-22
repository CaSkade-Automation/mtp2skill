package sparkapi;

import static spark.Spark.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

import mapping.Mapping;
import spark.Request;

public class SparkAPI {

	static Mapping mapping = new Mapping();

	public static void main(String[] args) {

		port(8080);
		File uploadDir = new File("upload");
		uploadDir.mkdir(); // create the upload directory if it doesn't exist

		staticFiles.externalLocation("upload");

		get("/", (req, res) -> "<form method='post' enctype='multipart/form-data'>" // note the enctype
				// make sure to call getPart using the same "name" in the post
				+ "    <input type='file' name='uploaded_file' accept='.txt'>" + "    <button>Upload file</button>"
				+ "</form>");

		post("/", (request, response) -> {

			Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");

			request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

			// getPart needs to use same "name" as input field in form
			try (InputStream input = request.raw().getPart("uploaded_file").getInputStream()) {
				Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
			}

			logInfo(request, tempFile);
			Thread thread = new Thread() {
				public void run() {
					changeXmlFile(tempFile);
				}
			};
			thread.start();

			return "<h1>File uploaded<h1>";
		});
	}

	// methods used for logging
	private static void logInfo(Request req, Path tempFile) throws IOException, ServletException {
		System.out.println("Uploaded file '" + getFileName(req.raw().getPart("uploaded_file")) + "' saved as '"
				+ tempFile.toAbsolutePath() + "'");
	}

	private static String getFileName(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}

	public static void changeXmlFile(Path tempFile) {
		String path = tempFile.toString();
		mapping.executeMapping(path);
	}
}

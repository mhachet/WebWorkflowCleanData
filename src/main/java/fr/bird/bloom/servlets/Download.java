package fr.bird.bloom.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * Servlet implementation class Download
 */
@WebServlet("/Download")
public class Download extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_BUFFER_SIZE = 100240; // 10 ko

	public Download() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* Read 'downloadpath' parameter in Download servlet thanks declaration in web.xml */
		String downloadpath = this.getServletConfig().getInitParameter("downloadpath");
		/* Get filepath asked inside url request*/
		String requestFile = request.getPathInfo();

		/* Check a file is given */
		if ( requestFile == null || "/".equals( requestFile ) ) {
			/* Else 404 error, ressource doesn't exist */
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		/* Decode filename retrieved, may contain space or others specials caracters. Prepare File object */
		requestFile = URLDecoder.decode( requestFile, "UTF-8");
		File file = new File( downloadpath, requestFile );
		System.out.println(file.getAbsolutePath());
		/*Check file exist */
		if ( !file.exists() ) {
			/* Else 404 error, ressource doesn't exist */
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		/* Retrieve file type */
		String type = getServletContext().getMimeType( file.getName() );

		/* If file type is unknown, so initialize it by default */
		if ( type == null ) {
			type = "application/octet-stream";
		}

		/* Initialise HTTP answer */
		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
		response.setContentType( type );
		response.setHeader( "Content-Length", String.valueOf( file.length() ) );
		response.setHeader( "Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" );

		/* Prepare flows */
		BufferedInputStream entry = null;
		BufferedOutputStream exit = null;
		try {
			/* Open flows */
			entry = new BufferedInputStream( new FileInputStream( file ), DEFAULT_BUFFER_SIZE );
			exit = new BufferedOutputStream( response.getOutputStream(), DEFAULT_BUFFER_SIZE );
			/* Read file and write the content in the HTTP answer */
			byte[] tampon = new byte[DEFAULT_BUFFER_SIZE];
			int longueur;
			while ( ( longueur = entry.read( tampon ) ) > 0 ) {
				exit.write(tampon, 0, longueur);
			}
		} finally {
			try {
				exit.close();
			} catch ( IOException ignore ) {
			}
			try {
				entry.close();
			} catch ( IOException ignore ) {
			}
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

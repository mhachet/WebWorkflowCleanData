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
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Download() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* Lecture du paramètre 'chemin' passé à la servlet via la déclaration dans le web.xml */
		String chemin = this.getServletConfig().getInitParameter("chemin");
		/* Récupération du chemin du fichier demandé au sein de l'URL de la requête */
		String fichierRequis = request.getPathInfo();

		/* Vérifie qu'un fichier a bien été fourni */
		if ( fichierRequis == null || "/".equals( fichierRequis ) ) {
			/* Si non, alors on envoie une erreur 404, qui signifie que la ressource demandée n'existe pas */
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		/* Décode le nom de fichier récupéré, susceptible de contenir des espaces et autres caractères spéciaux, et prépare l'objet File */
		fichierRequis = URLDecoder.decode( fichierRequis, "UTF-8");
		File fichier = new File( chemin, fichierRequis );
		//System.out.println(fichier.getAbsolutePath());
		/* Vérifie que le fichier existe bien */
		if ( !fichier.exists() ) {
			/* Si non, alors on envoie une erreur 404, qui signifie que la ressource demandée n'existe pas */
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		/* Récupère le type du fichier */
		String type = getServletContext().getMimeType( fichier.getName() );

		/* Si le type de fichier est inconnu, alors on initialise un type par défaut */
		if ( type == null ) {
			type = "application/octet-stream";
		}

		/* Initialise la réponse HTTP */
		response.reset();
		response.setBufferSize( DEFAULT_BUFFER_SIZE );
		response.setContentType( type );
		response.setHeader( "Content-Length", String.valueOf( fichier.length() ) );
		response.setHeader( "Content-Disposition", "attachment; filename=\"" + fichier.getName() + "\"" );

		/* Prépare les flux */
		BufferedInputStream entree = null;
		BufferedOutputStream sortie = null;
		try {
			/* Ouvre les flux */
			entree = new BufferedInputStream( new FileInputStream( fichier ), DEFAULT_BUFFER_SIZE );
			sortie = new BufferedOutputStream( response.getOutputStream(), DEFAULT_BUFFER_SIZE );
			/* Lit le fichier et écrit son contenu dans la réponse HTTP */
			byte[] tampon = new byte[DEFAULT_BUFFER_SIZE];
			int longueur;
			while ( ( longueur = entree.read( tampon ) ) > 0 ) {
				sortie.write( tampon, 0, longueur );
			}
		} finally {
			try {
				sortie.close();
			} catch ( IOException ignore ) {
			}
			try {
				entree.close();
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

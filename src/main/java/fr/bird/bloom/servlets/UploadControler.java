package fr.bird.bloom.servlets;

import fr.bird.bloom.utils.BloomConfig;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@WebServlet(name = "UploadControler")
public class UploadControler  extends HttpServlet{


	private String directoryPath;
	private String resourcesPath;

	private String getDirectoryPath() {
		if (directoryPath == null) {
			directoryPath = getServletContext().getRealPath(BloomConfig.getProperty("directory.path"));
		}
		return directoryPath;
	}

//	private String getResourcesPath() {
//		if (resourcesPath == null) {
//			resourcesPath = this.getClass().getClassLoader().getResource(BloomConfig.getProperty("resource.folder")).getPath();
//		}
//		return resourcesPath;
//	}


	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {		
		int count = 0;
		String uuid = "";
		String nbInput = "";
		String action = "";

		String firstline = "";		

		List<FileItem> items = null;
		try {
			items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}            
		for (FileItem item : items) {
			DiskFileItem itemFile = (DiskFileItem) item;
			String fileExtensionName = itemFile.getName();
			fileExtensionName = FilenameUtils.getExtension(fileExtensionName);

			if(count == 0){
				uuid = itemFile.getString();
				if(!new File(getDirectoryPath() + "temp/").exists()){
					new File(getDirectoryPath() + "temp/").mkdirs();
				}
				if(!new File(getDirectoryPath() + "temp/" + uuid + "/").exists()){
					new File(getDirectoryPath() + "temp/" + uuid + "/").mkdirs();
				}
				if(!new File(getDirectoryPath() + "temp/" + uuid + "/data/").exists()){
					new File(getDirectoryPath() + "temp/" + uuid + "/data/").mkdirs();
				}
				System.out.println(uuid);
			}
			else if(count == 1 ){
				nbInput = itemFile.getString();
				System.out.println(nbInput);
			}
			else if(count == 2){
				action = itemFile.getString();
				System.out.println(action);
			}
			else if(count == 3){
				System.out.println("format : " + fileExtensionName);
				ArrayList<String> compressedFormat = new ArrayList<>();
				compressedFormat.add("zip");
				compressedFormat.add("rar");
				compressedFormat.add("tar.gz");
				
				File file = new File(getDirectoryPath() + "temp/" + uuid + "/data/input_" + nbInput + "_" + uuid + "." + fileExtensionName);
				if(action.equals("upload")){
					try {
						itemFile.write(file);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(fileExtensionName.equals("zip")){
						String dezipFile = this.unzip(file, getDirectoryPath() + "temp/" + uuid + "/data/");
						System.out.println("dezipfile : " + dezipFile);
					}
					else{
						firstline = this.getFirstLine(file);
						//String fileContained = this.getFileContained(file);
						System.out.println(firstline);
						response.setContentType("application/text");
						response.setCharacterEncoding("UTF-8");
						response.getWriter().write(firstline);
					}
				}
				else if(action.equals("cancel")){

					file.delete();
					response.getWriter().write("cancelDone");
				}
			}

			count ++;			
		}


	}


	/**
	 * get the file contained
	 * 
	 * @param file
	 * @return String
	 */
	protected String getFileContained(File file){
		String contained = "";

		InputStream inputStreamReference;
		try {
			inputStreamReference = new FileInputStream(file);
			InputStreamReader inputStreamReaderReference = new InputStreamReader(inputStreamReference);
			BufferedReader readerReference = new BufferedReader(inputStreamReaderReference);
			String line = "";
			while ((line = readerReference.readLine()) != null){
				contained += line + "\n";
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return contained;
	}


	/**
	 * get the first line of the file
	 * @param file
	 * @return String
	 */
	protected String getFirstLine(File file){
		String firstLine = "";

		InputStream inputStreamReference;
		try {
			inputStreamReference = new FileInputStream(file);
			InputStreamReader inputStreamReaderReference = new InputStreamReader(inputStreamReference);
			BufferedReader readerReference = new BufferedReader(inputStreamReaderReference);
			String line = "";
			int countLine = 0;
			while ((line = readerReference.readLine()) != null){
				if(countLine == 0){
					firstLine = line;
					break;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 



		return firstLine;
	}


	/**
	 * unzip zip file
	 * @param zipfile
	 * @param folder
	 * @return String filename of unzip file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String unzip(File zipfile, String folder) throws FileNotFoundException, IOException{
		
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipfile.getCanonicalFile())));
		String zipFilename = zipfile.getName().split("/")[zipfile.getName().split("/").length - 1];
		String dezipFilename = "";
		ZipEntry ze = null;
		
		try {
			while((ze = zis.getNextEntry()) != null){
				//dezipFilename = ze.toString();
				dezipFilename = zipFilename.split("\\.")[0] + ".csv";
				File f = new File(folder,dezipFilename );
				if (ze.isDirectory()) {
					f.mkdirs();
					continue;
				}
				

				f.getParentFile().mkdirs();
				OutputStream fos = new BufferedOutputStream(new FileOutputStream(f));

				try {
					try {
						final byte[] buf = new byte[8192];
						int bytesRead;
						while (-1 != (bytesRead = zis.read(buf)))
							fos.write(buf, 0, bytesRead);
					}
					finally {
						fos.close();
					}
				}
				catch (final IOException ioe) {
					f.delete();
					throw ioe;
				}
			}
		}
		finally {
			zis.close();
			zipfile.delete();
		}
		
		return dezipFilename;
	}


}


/*
 ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipfile.getCanonicalFile())));
		String zipFilename = zipfile.getName().split("/")[zipfile.getName().split("/").length - 1];
		String dezipFilename = "";
		ZipEntry ze = null;
		
		try {
			while((ze = zis.getNextEntry()) != null){

				File f = new File(folder, zipFilename);
				System.out.println(f.getAbsolutePath() + "  " + f.getName());
				if (ze.isDirectory()) {
					f.mkdirs();
					continue;
				}
				dezipFilename = zipFilename.split("\\.")[0];
				*/

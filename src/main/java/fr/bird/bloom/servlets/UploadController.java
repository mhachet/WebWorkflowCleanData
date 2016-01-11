package fr.bird.bloom.servlets;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;
import com.opencsv.CSVReader;
import fr.bird.bloom.utils.BloomConfig;
import fr.bird.bloom.utils.BloomUtils;
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
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.GZIPInputStream;

@WebServlet(name = "UploadController")
public class UploadController extends HttpServlet{


	private String getDirectoryPath() {
		if (BloomConfig.getDirectoryPath() == null) {
			BloomConfig.initializeDirectoryPath(getServletContext().getRealPath("/"));
		}
		return BloomConfig.getDirectoryPath();
	}


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
		System.out.println("getDirectoryPath : " + getDirectoryPath());
		for (FileItem item : items) {
			DiskFileItem itemFile = (DiskFileItem) item;
			String filename = itemFile.getName();
			String fileExtensionName = FilenameUtils.getExtension(filename);
			if(count == 0){
				uuid = itemFile.getString();
				if(!new File(getDirectoryPath() + "temp/").exists()){
					BloomUtils.createDirectory(getDirectoryPath() + "temp/");
				}
				if(!new File(getDirectoryPath() + "temp/" + uuid + "/").exists()){
					BloomUtils.createDirectory(getDirectoryPath() + "temp/" + uuid + "/");
				}
				if(!new File(getDirectoryPath() + "temp/" + uuid + "/data/").exists()){
					BloomUtils.createDirectory(getDirectoryPath() + "temp/" + uuid + "/data/");
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
				List<String> compressedFormat = new ArrayList<>();
				compressedFormat.add("zip");
				compressedFormat.add("rar");
				boolean errorFormat = false;

				File file = null;
				if(fileExtensionName != ""){
					file = new File(getDirectoryPath() + "temp/" + uuid + "/data/input_" + nbInput + "_" + uuid + "." + "csv");
				}
				else{
					file = new File(getDirectoryPath() + "temp/" + uuid + "/data/input_" + nbInput + "_" + uuid + ".csv");
				}


				if("upload".equals(action)){
					if("xls".equals(fileExtensionName) || "xlsx".equals(fileExtensionName) || "doc".equals(fileExtensionName) || "docx".equals(fileExtensionName)){
						errorFormat = true;
					}
					else {
						try {
							itemFile.write(file);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if("zip".equals(fileExtensionName)){
							ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file.getCanonicalFile())));
							String dezipExtensionName = FilenameUtils.getExtension(zis.getNextEntry().getName());
							System.out.println("dezipExtensionName : " + dezipExtensionName);
							if("xls".equals(dezipExtensionName) || "xlsx".equals(dezipExtensionName) || "doc".equals(dezipExtensionName) || "docx".equals(dezipExtensionName)){
								file.delete();
								errorFormat = true;
							}
							else {
								File dezipFile = this.unzip(file, getDirectoryPath() + "temp/" + uuid + "/data/");
								System.out.println("dezipfile : " + dezipFile.getAbsolutePath());
								file = dezipFile;
							}
						}
						else if("rar".equals(fileExtensionName)){
							File unrarFile = this.unrar(file, getDirectoryPath() + "temp/" + uuid + "/data/");
							if(unrarFile == null){
								errorFormat = true;
								System.out.println("Error format in unrarFile");
							}
							else{
								System.out.println("unrarFile : " + unrarFile.getAbsolutePath());
								file = unrarFile;
							}

						}

						response.setContentType("application/text");
						response.setCharacterEncoding("UTF-8");

						if(!errorFormat){
							firstline = this.getFirstLine(file);
							System.out.println("filename : " + file.getAbsolutePath());
							System.out.println(firstline);
							response.getWriter().write(firstline);
						} else {
							System.out.println("error format : " + fileExtensionName);
							response.getWriter().write("formatError");
						}
					}
				}
				else if("cancel".equals(action)){

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
			try{
				inputStreamReaderReference.close();
				inputStreamReference.close();
				readerReference.close();
			}
			catch (IOException e){
				e.printStackTrace();
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
					firstLine = line.replaceAll("\"", "").replaceAll("\'", "");
					break;
				}
			}
			try{
				inputStreamReaderReference.close();
				inputStreamReference.close();
				readerReference.close();
			}
			catch (IOException e){
				e.printStackTrace();
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
	public File unzip(File zipfile, String folder) throws FileNotFoundException, IOException{

		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipfile.getCanonicalFile())));
		String zipFilename = zipfile.getName().split("/")[zipfile.getName().split("/").length - 1];
		String dezipFilename = "";
		ZipEntry ze = null;
		File dezipFile = null;
		try {
			while((ze = zis.getNextEntry()) != null){
				//dezipFilename = ze.toString();
				dezipFilename = zipFilename.split("\\.")[0] + ".csv";
				dezipFile = new File(folder,dezipFilename);
				if (ze.isDirectory()) {
					dezipFile.mkdirs();
					continue;
				}


				dezipFile.getParentFile().mkdirs();
				OutputStream fos = new BufferedOutputStream(new FileOutputStream(dezipFile));

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
					dezipFile.delete();
					throw ioe;
				}
			}
		}
		finally {
			zis.close();
			zipfile.delete();
		}

		return dezipFile;
	}


	public File unrar(File rarfile, String folder){
		Archive rarArchive = null;
		File unrarFile = null;
		boolean errorFormat = false;
		String fileExtensionName = "";
		try {
			rarArchive = new Archive(new FileVolumeManager(rarfile));
		} catch (RarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (rarArchive != null) {
			rarArchive.getMainHeader().print();
			FileHeader fileHeader = rarArchive.nextFileHeader();
			while (fileHeader != null) {
				try {
					unrarFile = new File(folder + FilenameUtils.getBaseName(rarfile.getAbsolutePath()) + ".csv"); //rarfile.getName().split("\\.")[0] + ".csv");
					fileExtensionName = FilenameUtils.getExtension(fileHeader.getFileNameString());

					if("xls".equals(fileExtensionName) || "xlsx".equals(fileExtensionName) || "doc".equals(fileExtensionName) || "docx".equals(fileExtensionName)){
						errorFormat = true;
						System.out.println("Error Format in unrarFileOriginal : " + fileHeader.getFileNameString());
						rarfile.delete();
						return null;
					}
					else{
						FileOutputStream fileOutputStream = new FileOutputStream(unrarFile);
						rarArchive.extractFile(fileHeader, fileOutputStream);
						fileOutputStream.close();
					}


				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RarException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fileHeader = rarArchive.nextFileHeader();
			}
		}

		return unrarFile;
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

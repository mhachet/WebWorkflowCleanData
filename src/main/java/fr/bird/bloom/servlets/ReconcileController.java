package fr.bird.bloom.servlets;

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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "ReconcileController")
public class ReconcileController extends HttpServlet {

	private String getDirectoryPath() {
		if (BloomConfig.getDirectoryPath() == null) {
			BloomConfig.initializeDirectoryPath(getServletContext().getRealPath("/"));
		}
		return BloomConfig.getDirectoryPath();
	}

	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		
		
		int count = 0;
		String firstLine = "";
		
		String action = ""; //prepation or reconciliation
		String uuid = "";
		String nbInput = "";
		String checkingColumns = ""; // null if preparation
		String separator = "";
		
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
				action = itemFile.getString();	
				System.out.println(action);
			}
			else if(count == 1){
				uuid = itemFile.getString();
				System.out.println(uuid);
			}
			else if(count == 2){
				nbInput = itemFile.getString();
				System.out.println(nbInput);
			}
			else if(count == 3){
				checkingColumns = itemFile.getString();
				System.out.println(checkingColumns);
			}
			else if(count == 4){
				separator = itemFile.getString();
				System.out.println(separator);
			}
		
			count ++;
		}
		if(!new File(getDirectoryPath() + "temp/").exists()){
			BloomUtils.createDirectory(getDirectoryPath() + "temp/");
		}
		if(!new File(getDirectoryPath() + "temp/" + uuid).exists()){
			new File(getDirectoryPath() + "temp/" + uuid);
		}
		if(!new File(getDirectoryPath() + "temp/" + uuid + "/data/").exists()){
			BloomUtils.createDirectory(getDirectoryPath() + "temp/" + uuid + "/data/");
		}
		if(!new File(getDirectoryPath() + "temp/" + uuid + "/wrong/").exists()){
			BloomUtils.createDirectory(getDirectoryPath() + "temp/" + uuid + "/wrong/");
		}
		if(!new File(getDirectoryPath() + "temp/" + uuid + "/final_results/").exists()){
			BloomUtils.createDirectory(getDirectoryPath() + "temp/" + uuid + "/final_results/");
		}

		String extension = this.getExtension(nbInput, uuid);
		System.out.println("extension : " + extension);
		if("preparation".equals(action)){

			File file = new File(getDirectoryPath() + "temp/" + uuid + "/data/input_" + nbInput + "_" + uuid + "." + extension);
			firstLine = this.getFirstLine(file);

			response.setContentType("application/text");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(firstLine);
		}
		else if("reconciliation".equals(action)){
			FileReader reader = new FileReader(getDirectoryPath() + "temp/" + uuid + "/data/input_" + nbInput + "_" + uuid + "." + extension);
			BufferedReader br = null;
			List<String> lines = new ArrayList<>();
			int countLines = 0;
			try {
				String sCurrentLine;
				FileReader fileReader = new FileReader(getDirectoryPath() + "temp/" + uuid + "/data/input_" + nbInput + "_" + uuid + "." + extension);

				br = new BufferedReader(fileReader);
				
				while ((sCurrentLine = br.readLine()) != null) {
					if(countLines == 0){
						firstLine = sCurrentLine;
					}
					
					lines.add(sCurrentLine);
					countLines ++;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			List<Integer> idColumnsCheck = this.getColumnsIdReconcile(firstLine, separator, checkingColumns);
			String selectedLines = this.getSelectedLines(idColumnsCheck, lines, separator);
			/*
			JSONArray array = null;
			try {
				array = CDL.toJSONArray(selectedLines);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println(array.toString());
			*/
			response.setContentType("application/text");
			response.setCharacterEncoding("UTF-8");
			//response.getWriter().write(array.toString());
			response.getWriter().print(selectedLines);	

		}



	}

	/**
	 * Find extension between csv or txt only
	 * @param nbInput
	 * @param uuid
	 * @return String
	 */
	protected String getExtension(String nbInput, String uuid){
		String extension = "";
		File csvFile = new File(getDirectoryPath() + "temp/" + uuid + "/data/input_" + nbInput + "_" + uuid + ".csv");
		File txtFile = new File(getDirectoryPath() + "temp/" + uuid + "/data/input_" + nbInput + "_" + uuid + ".txt");
		
		if(csvFile.exists()){
			extension = "csv";
		}
		else if(txtFile.exists()){
			extension = "txt";
		}
		
		return extension;
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
	 * get id of all checked columns
	 * 
	 * @param firstline
	 * @param separator
	 * @param columnsCheck
	 * @return ArrayList<Integer>
	 */
	public List<Integer> getColumnsIdReconcile(String firstline, String separator, String columnsCheck){
		List<Integer> columnsReconcile = new ArrayList<>();
		List<String> columns = new ArrayList(Arrays.asList(firstline.split(separator)));
		List<String> arrayColumnsCheck = new ArrayList(Arrays.asList(columnsCheck.split(",")));
		
		for(int i = 0; i < columns.size(); i++){
			String column = columns.get(i);
			if(arrayColumnsCheck.contains(column)){
				columnsReconcile.add(i);
			}
		}
		

		return columnsReconcile;
	}
	
	/**
	 * get columns and line from id columns
	 * 
	 * @param idColumns
	 * @param contentFile
	 * @param separator
	 * @return String
	 */
	public String getSelectedLines(List<Integer> idColumns, List<String> contentFile, String separator){
		String newContentFile = "";
		
		for(int i = 0; i < contentFile.size(); i++){
			String line [] = contentFile.get(i).split(separator);
			String newLine = "";
			
			for(int j = 0 ; j < idColumns.size(); j++){
				
				int idColumn = idColumns.get(j);
				newLine += line[idColumn] + separator;
			}
			
			newLine = newLine.substring(0, newLine.length() - 1);
			newContentFile += newLine + "\n";
		}
		return newContentFile;
		
	}
}

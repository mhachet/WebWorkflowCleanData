package src.servlets;

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
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "ReconcileControler")
public class ReconcileControler extends HttpServlet {

	private String DIRECTORY_PATH = "/home/mhachet/workspace/WebWorkflowCleanData/WebContent/output/"; 
	private String RESSOURCES_PATH = "/home/mhachet/workspace/WebWorkflowCleanData/src/resources/";


	public ReconcileControler() {

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
		if(!new File(DIRECTORY_PATH + "temp/").exists()){
			new File(DIRECTORY_PATH + "temp/").mkdirs();
		}
		if(!new File(DIRECTORY_PATH + "temp/" + uuid).exists()){
			new File(DIRECTORY_PATH + "temp/" + uuid);
		}
		if(!new File(DIRECTORY_PATH + "temp/" + uuid + "/data/").exists()){
			new File(DIRECTORY_PATH + "temp/" + uuid + "/data/").mkdirs();
		}
		if(!new File(DIRECTORY_PATH + "temp/" + uuid + "/wrong/").exists()){
			new File(DIRECTORY_PATH + "temp/" + uuid + "/wrong/").mkdirs();
		}
		if(!new File(DIRECTORY_PATH + "temp/" + uuid + "/final_results/").exists()){
			new File(DIRECTORY_PATH + "temp/" + uuid + "/final_results/").mkdirs();
		}

		String extension = this.getExtension(nbInput, uuid);
		System.out.println("extension : " + extension);
		if(action.equals("preparation")){

			File file = new File(DIRECTORY_PATH + "temp/" + uuid + "/data/input_" + nbInput + "_" + uuid + "." + extension);
			firstLine = this.getFirstLine(file);

			response.setContentType("application/text");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(firstLine);
		}
		else if(action.equals("reconciliation")){
			FileReader reader = new FileReader(DIRECTORY_PATH + "temp/" + uuid + "/data/input_" + nbInput + "_" + uuid + "." + extension);
			BufferedReader br = null;
			ArrayList<String> lines = new ArrayList<>();
			int countLines = 0;
			try {
				String sCurrentLine;
				FileReader fileReader = new FileReader(DIRECTORY_PATH + "temp/" + uuid + "/data/input_" + nbInput + "_" + uuid + "." + extension);

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
			
			ArrayList<Integer> idColumnsCheck = this.getColumnsIdReconcile(firstLine, separator, checkingColumns);
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

	protected String getExtension(String nbInput, String uuid){
		String extension = "";
		File csvFile = new File(DIRECTORY_PATH + "temp/" + uuid + "/data/input_" + nbInput + "_" + uuid + ".csv");
		File txtFile = new File(DIRECTORY_PATH + "temp/" + uuid + "/data/input_" + nbInput + "_" + uuid + ".txt");
		
		if(csvFile.exists()){
			extension = "csv";
		}
		else if(txtFile.exists()){
			extension = "txt";
		}
		
		return extension;
	}
	
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
	
	public ArrayList<Integer> getColumnsIdReconcile(String firstline, String separator, String columnsCheck){
		ArrayList<Integer> columnsReconcile = new ArrayList<>();
		ArrayList<String> columns = new ArrayList(Arrays.asList(firstline.split(separator)));
		ArrayList<String> arrayColumnsCheck = new ArrayList(Arrays.asList(columnsCheck.split(",")));
		
		for(int i = 0; i < columns.size(); i++){
			String column = columns.get(i);
			if(arrayColumnsCheck.contains(column)){
				columnsReconcile.add(i);
			}
		}
		

		return columnsReconcile;
	}
	
	public String getSelectedLines(ArrayList<Integer> idColumns, ArrayList<String> contentFile, String separator){
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

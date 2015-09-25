package src.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;

@WebServlet(name = "UploadControler")
public class UploadControler  extends HttpServlet{


	private String DIRECTORY_PATH = "/home/mhachet/workspace/WebWorkflowCleanData/WebContent/output/"; 
	private String RESSOURCES_PATH = "/home/mhachet/workspace/WebWorkflowCleanData/src/resources/";


	public UploadControler(){


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
		for (FileItem item : items) {
			DiskFileItem itemFile = (DiskFileItem) item;
			String fileExtensionName = itemFile.getName();
			fileExtensionName = FilenameUtils.getExtension(fileExtensionName);
			
			if(count == 0){
				uuid = itemFile.getString();
				if(!new File(DIRECTORY_PATH + "temp/").exists()){
					new File(DIRECTORY_PATH + "temp/").mkdirs();
				}
				if(!new File(DIRECTORY_PATH + "temp/" + uuid + "/").exists()){
					new File(DIRECTORY_PATH + "temp/" + uuid + "/").mkdirs();
				}
				if(!new File(DIRECTORY_PATH + "temp/" + uuid + "/data/").exists()){
					new File(DIRECTORY_PATH + "temp/" + uuid + "/data/").mkdirs();
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
				File file = new File(DIRECTORY_PATH + "temp/" + uuid + "/data/input_" + nbInput + "_" + uuid + ".csv");
				if(action.equals("upload")){
					try {
						itemFile.write(file);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					firstline = this.getFirstLine(file);
					//String fileContained = this.getFileContained(file);
					System.out.println(firstline);
					response.setContentType("application/text");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write(firstline);
				}
				else if(action.equals("cancel")){
					
					file.delete();
					response.getWriter().write("cancelDone");
				}
			}
			
			count ++;			
		}
		

	}
	

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



}

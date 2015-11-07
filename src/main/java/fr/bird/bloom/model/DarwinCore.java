/**
 * @author mhachet
 */
package fr.bird.bloom.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * package model
 * 
 * DarwinCore
 */
public class DarwinCore extends CSVFile{

	private int idFile_;
	private String nbSessionRandom;
	private HashMap<String, ArrayList<String>> idAssoData;
	private ArrayList<String> darwinLines;
	private File darwinCoreFileTemp;
	private String DIRECTORY_PATH;

	/**
	 * 
	 * src.model
	 * DarwinCore
	 * 
	 * @param file
	 */
	public DarwinCore(File file){
		super(file);
	}   

	/**
	 * 
	 * src.model
	 * DarwinCore
	 * 
	 * @param file
	 * @param idFile
	 */
	public DarwinCore(File file, int idFile, String nbSessionRandom){
		super(file);
		this.idFile_ = idFile;
		//this.darwinLines = super.getLines();
		this.nbSessionRandom = nbSessionRandom;
	}

	/**
	 * Connect id of the line to values
	 * 
	 * @return void
	 */
	public void associateIdData(){
		idAssoData = new HashMap<>();
		DatabaseTreatment newConnection = null;
		ArrayList<String> messages = new ArrayList<String>();
		//ConnectionDatabase newConnection = new ConnectionDatabase();
		messages.add("\n--- Select id line from clean ---\n");
		Statement statement = null;
		try {
			statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			newConnection = new DatabaseTreatment(statement);
			String sqlID = "SELECT * FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE UUID_=\"" + this.getNbSessionRandom() + "\";";
			messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlID));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		//System.out.println(sqlID);
		//--- Create DarwinCoreInput table ---inputFile_
		//messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlID));
		ArrayList<String> resultats = newConnection.getResultatSelect();

		for(int i = 0 ; i < resultats.size() ; i++){
			String id_ = resultats.get(i).split(",")[0];
			String line [] = resultats.get(i).split(",");
			ArrayList<String> infos = new ArrayList<>();
			for(int j = 1 ; j < line.length ; j++){
				infos.add(line[j]);
			}
			idAssoData.put(id_, infos);
		}
	}

	/**
	 * Modified input file to format it.
	 * 
	 * @return List<String>
	 */
	public File readDarwinCoreFile(String separator) throws IOException {

		if(!new File(DIRECTORY_PATH + "temp/").exists()){
			new File(DIRECTORY_PATH + "temp/").mkdirs();
		}
		if(!new File(DIRECTORY_PATH + "temp/" + this.getNbSessionRandom()).exists()){
			new File(DIRECTORY_PATH + "temp/" + this.getNbSessionRandom());
		}
		if(!new File(DIRECTORY_PATH + "temp/" + this.getNbSessionRandom() + "/data/").exists()){
			new File(DIRECTORY_PATH + "temp/" + this.getNbSessionRandom() + "/data/").mkdirs();
		}
		
		File tempFile = new File(DIRECTORY_PATH + "temp/" + this.getNbSessionRandom() + "/data/inputFile_" + Integer.toString(this.getIdFile_()) + ".csv");
		FileWriter writer = null;
		File darwinCoreFile = super.getCsvFile();
		super.setSeparator(Separator.fromString(separator));
		//System.out.println("separator DarwinCore : " + separator + "  " + darwinCoreFile.getAbsolutePath());
		//String firstLine = darwinLines.get(0);
		String firstLine = super.getFirstLine();
		String firstNewLine = "";
		List<String> tags = Arrays.asList(firstLine.replaceAll("\"", "").replaceAll("\'", "").split(separator));
		for(int i = 0 ; i < tags.size() ; i++){
			String newTag = tags.get(i) + "_,";
			firstNewLine += newTag;
		}

		firstNewLine += "idFile_,id_,UUID_\n";
		try{
			writer = new FileWriter(tempFile);
			writer.write(firstNewLine);
			
			int count = 0;
			BufferedReader br = null;
			InputStream in = new FileInputStream(darwinCoreFile);
			try{
				br = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = br.readLine() ) != null ){
					if(count != 0){
						String lineSplit [] = line.replaceAll("\"", "").replaceAll("\'", "").split(separator, -1);
						String newLine = "";
						for(int j = 0 ; j < lineSplit.length ; j++){
							newLine += "\"" + lineSplit[j] + "\",";
						}
						newLine += Integer.toString(idFile_) + ",0,\"" + this.getNbSessionRandom() + "\"\n";
						
						writer.write(newLine);
					}
					
					count ++;
				}
				
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
			finally{
				if(writer != null){
					writer.close();
				}
				if ( br != null ){
					try{
						br.close();
					}catch(Exception e){}
				}
			}
		}
		catch(IOException ex){
			ex.printStackTrace();
		}finally{
			if(writer != null){
				writer.close();
			}
		}
		
		return tempFile;
	}

	
	/**
	 * Modified initial files in order to fill in table DarwinCoreInput
	 * @param List<String> linesInputModified
	 * @param int nbFile
	 * @throws IOException
	 * @return File temporary
	 */
	public File createTemporaryFile(List<String> linesInputModified, int nbFile) throws IOException{


		File tempFile = new File(DIRECTORY_PATH + "temp/" + this.getNbSessionRandom() + "/data/inputFile_" + Integer.toString(nbFile) + ".csv");
		FileWriter writer = null;
		try{
			writer = new FileWriter(tempFile);
			for(int i = 0 ; i < linesInputModified.size() ; i++){
				//System.out
				writer.write(linesInputModified.get(i) + "\n");
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			if(writer != null){
				writer.close();
			}
		}
		return tempFile;
	}

	
	/**
	 * Get all latitude coordinates clean from input file.
	 *  
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getDecimalLatitudeClean(){
		DatabaseTreatment newConnection = null;
		ArrayList<String> messages = new ArrayList<String>();
		//ConnectionDatabase newConnection = new ConnectionDatabase();
		messages.add("\n--- Select decimal latitude from coordinates ---");
		Statement statement = null;
		try {
			statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			newConnection = new DatabaseTreatment(statement);
			String sqlLatitude = "SELECT decimalLatitude_ FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE UUID_=\"" + this.getNbSessionRandom() + "\";";
			messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlLatitude));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		ArrayList<String> resultatLatitude = newConnection.getResultatSelect();

		return resultatLatitude;
	}

	/**
	 * Get all longitude coordinates clean from input file.
	 *  
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getDecimalLongitudeClean(){
		
		DatabaseTreatment newConnection = null;
		ArrayList<String> messages = new ArrayList<String>();
		//ConnectionDatabase newConnection = new ConnectionDatabase();
		messages.add("\n--- Select decimal longitude from coordinates ---");
		Statement statement = null;
		try {
			statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			newConnection = new DatabaseTreatment(statement);
			String sqlLongitude = "SELECT decimalLongitude_ FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE UUID_=\"" + this.getNbSessionRandom() + "\";";
			messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlLongitude));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<String> resultatLongitude = newConnection.getResultatSelect();

		return resultatLongitude;
	}

	/**
	 * Get id from table Clean 
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getIDClean(){
		DatabaseTreatment newConnection = null;
		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select id line from clean ---\n");
		Statement statement = null;
		try {
			statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			newConnection = new DatabaseTreatment(statement);
			String sqlID= "SELECT id_ FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE UUID_=\"" + this.getNbSessionRandom() + "\";";
			messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlID));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<String> resultatID = newConnection.getResultatSelect();

		return resultatID;
	}

	/**
	 * Get gbifID from Clean table
	 *  
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getGbifIDClean(){
		DatabaseTreatment newConnection = null;
		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select id line from clean ---\n");
		Statement statement = null;
		try {
			statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			newConnection = new DatabaseTreatment(statement);
			String sqlID= "SELECT gbifID_ FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE=UUID_=\"" + this.getNbSessionRandom() + "\";";
			messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlID));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<String> resultatGbifID = newConnection.getResultatSelect();

		return resultatGbifID;
	}

	/**
	 * Get id from DarwinCoreInput table (not Clean)
	 *  
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getID(){
		DatabaseTreatment newConnection = null;
		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select id line from DarwinCoreInput ---\n");
		Statement statement = null;
		try {
			statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			newConnection = new DatabaseTreatment(statement);
			String sqlID= "SELECT id_ FROM Workflow.DarwinCoreInput WHERE=UUID_=\"" + this.getNbSessionRandom() + "\";";
			messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlID));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<String> resultatID = newConnection.getResultatSelect();

		return resultatID;
	}

	/**
	 * Get all iso2 code clean (countryCode_) from input file. 
	 * 
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getIso2Clean(){
		
		DatabaseTreatment newConnection = null;
		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select iso2 code ---");
		Statement statement = null;
		try {
			statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			newConnection = new DatabaseTreatment(statement);
			String sqlISO2 = "SELECT countryCode_ FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE=UUID_=\"" + this.getNbSessionRandom() + "\";";
			messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlISO2));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<String> resultatISO2 = newConnection.getResultatSelect();

		return resultatISO2;
	}


	/**
	 * Found indice corresponding to tag name
	 * 
	 * @param tagName
	 * @return int
	 */
	public int getIndiceFromTag(String tagName){

		HashMap<String, ArrayList<String>> idAssoData = this.getIdAssoData();

		for(String id_ : idAssoData.keySet()){
			if(id_.equals("id_")){
				ArrayList<String> tagsList = idAssoData.get(id_);
				for(int i = 0 ; i < tagsList.size() ; i++){
					if(tagsList.get(i).equals(tagName)){
						return i;
					}
				}
			}
		}
		return 0;
	}

	/**
	 * Get file id
	 * 
	 * @return int
	 */
	public int getIdFile_() {
		return idFile_;
	}

	/**
	 * set id file
	 * 
	 * @return void
	 */
	public void setIdFile_(int idFile_) {
		this.idFile_ = idFile_;
	}

	/**
	 * 
	 * @return HashMap<String,ArrayList<String>>
	 */
	public HashMap<String, ArrayList<String>> getIdAssoData() {
		return idAssoData;
	}

	/**
	 * 
	 * @param idAssoData
	 * @return void
	 */
	public void setIdAssoData(HashMap<String, ArrayList<String>> idAssoData) {
		this.idAssoData = idAssoData;
	}

	/**
	 * 
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getDarwinLines() {
		return darwinLines;
	}

	/**
	 * 
	 * @param darwinLines
	 * @return void
	 */
	public void setDarwinLines(ArrayList<String> darwinLines) {
		this.darwinLines = darwinLines;
	}

	public String getNbSessionRandom() {
		return nbSessionRandom;
	}

	public void setNbSessionRandom(String nbSessionRandom) {
		this.nbSessionRandom = nbSessionRandom;
	}

	public File getDarwinCoreFileTemp() {
		return darwinCoreFileTemp;
	}

	public void setDarwinCoreFileTemp(File darwinCoreFileTemp) {
		this.darwinCoreFileTemp = darwinCoreFileTemp;
	}

	public String getDIRECTORY_PATH() {
		return DIRECTORY_PATH;
	}

	public void setDIRECTORY_PATH(String dIRECTORY_PATH) {
		DIRECTORY_PATH = dIRECTORY_PATH;
	}
	
	
}

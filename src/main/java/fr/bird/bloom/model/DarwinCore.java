/**
 * @author mhachet
 */
package fr.bird.bloom.model;

import com.opencsv.CSVReader;
import fr.bird.bloom.utils.BloomConfig;
import fr.bird.bloom.utils.BloomUtils;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * package model
 * 
 * DarwinCore
 */
public class DarwinCore extends CSVFile{

	private int idFile_;
	private String uuid;
	private Map<String, List<String>> idAssoData;
	private File darwinCoreFileTemp;

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
	 * unused
	 * src.model
	 * DarwinCore
	 * 
	 * @param file
	 * @param idFile
	 */
	public DarwinCore(File file, int idFile, String uuid){
		super(file);
		this.idFile_ = idFile;
		//this.darwinLines = super.getLines();
		this.uuid = uuid;
	}

	/**
	 * Create darwinCore file from input file in order to insert in database.
	 *
	 * @param separator
	 * @return
	 */
	public File readDarwinCoreFile(String separator){
		if(!new File(BloomConfig.getDirectoryPath() + "temp/").exists()){
			BloomUtils.createDirectory(BloomConfig.getDirectoryPath() + "temp/");
		}
		if(!new File(BloomConfig.getDirectoryPath() + "temp/" + this.getUuid()).exists()){
			new File(BloomConfig.getDirectoryPath() + "temp/" + this.getUuid());
		}
		if(!new File(BloomConfig.getDirectoryPath() + "temp/" + this.getUuid() + "/data/").exists()){
			BloomUtils.createDirectory(BloomConfig.getDirectoryPath() + "temp/" + this.getUuid() + "/data/");
		}

		File tempFile = new File(BloomConfig.getDirectoryPath() + "temp/" + this.getUuid() + "/data/inputFile_" + Integer.toString(this.getIdFile_()) + ".csv");
		FileWriter writer = null;
		File darwinCoreFile = super.getCsvFile();
		super.setSeparator(Separator.fromString(separator));

		int countLine = 0;
		String firstLine = super.getFirstLine();
		String firstNewLine = "";

		int decimalLatitudeID = 0 ;
		int decimalLongitudeID = 0 ;

		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(darwinCoreFile), separator.charAt(0));
			String [] contentLine;
			while ((contentLine = reader.readNext()) != null) {
				if(countLine == 0){
					for(int i = 0 ; i < contentLine.length ; i++){
						String tagName = contentLine[i];
						firstNewLine += tagName.replaceAll("\"", "").replaceAll("\'", "") + "_,";

						if(tagName.equals("decimalLatitude")){
							decimalLatitudeID = i;
						}
						else if(tagName.equals("decimalLongitude")){
							decimalLongitudeID = i;
						}
					}
					firstNewLine += "idFile_,id_,UUID_\n";

					writer = new FileWriter(tempFile);
					writer.write(firstNewLine);
				}
				else{
					String newLine = "";
					for(int j = 0 ; j < contentLine.length ; j++){
						if(j == decimalLatitudeID || j == decimalLongitudeID){
							String checkedLatLong = "";
							String noCheckedLatLong = contentLine[j];
							checkedLatLong = noCheckedLatLong.replace(",", ".");
							newLine += "\"" + checkedLatLong + "\",";
						}
						else {
							//System.out.println("------\n" + contentLine[j]);
							if(contentLine[j].contains("\"")){

								String newContent = "\"" + contentLine[j].replaceAll("\"", "\\\\\"\\\\\"") + "\",";
								//System.out.println("------\n" + contentLine[j] + "     " + newContent);
								newLine += newContent;
							}
							else{
								newLine += "\"" + contentLine[j] + "\",";
							}
						}


					}
					newLine += Integer.toString(idFile_) + ",0,\"" + this.getUuid() + "\"\n";
					//System.out.println(newLine);
					writer.write(newLine);
				}

				countLine ++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			writer.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tempFile;
	}

	/**
	 * Get all latitude coordinates clean from input file.
	 *  
	 * @return ArrayList<String>
	 */
	public List<String> getDecimalLatitudeClean(){
		DatabaseTreatment newConnection = null;
		List<String> messages = new ArrayList<>();
		//ConnectionDatabase newConnection = new ConnectionDatabase();
		messages.add("\n--- Select decimal latitude from coordinates ---");
		Statement statement = null;
		try {
			statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			newConnection = new DatabaseTreatment(statement);
			String sqlLatitude = "SELECT decimalLatitude_ FROM Workflow.Clean_" + this.getUuid() + " WHERE UUID_=\"" + this.getUuid() + "\";";
			messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlLatitude));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		List<String> resultatLatitude = newConnection.getResultatSelect();

		try {
			if(statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return resultatLatitude;
	}

	/**
	 * Get all longitude coordinates clean from input file.
	 *  
	 * @return ArrayList<String>
	 */
	public List<String> getDecimalLongitudeClean(){
		
		DatabaseTreatment newConnection = null;
		List<String> messages = new ArrayList<>();
		//ConnectionDatabase newConnection = new ConnectionDatabase();
		messages.add("\n--- Select decimal longitude from coordinates ---");
		Statement statement = null;
		try {
			statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			newConnection = new DatabaseTreatment(statement);
			String sqlLongitude = "SELECT decimalLongitude_ FROM Workflow.Clean_" + this.getUuid() + " WHERE UUID_=\"" + this.getUuid() + "\";";
			messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlLongitude));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> resultatLongitude = newConnection.getResultatSelect();

		try {
			if(statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return resultatLongitude;
	}

	/**
	 * Get id from table Clean 
	 * @return ArrayList<String>
	 */
	public List<String> getIDClean(){

		DatabaseTreatment newConnection = null;
		List<String> messages = new ArrayList<>();
		try {
			String sqlID= "SELECT id_ FROM Workflow.Clean_" + this.getUuid() + " WHERE UUID_=\"" + this.getUuid() + "\";";
			Statement statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			newConnection = new DatabaseTreatment(statement);
			messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlID));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		messages.add("\n--- Select id line from clean ---\n");


		List<String> resultatID = newConnection.getResultatSelect();

		return resultatID;
	}

	/**
	 * Get gbifID from Clean table
	 *  
	 * @return ArrayList<String>
	 */
	public List<String> getGbifIDClean(){
		DatabaseTreatment newConnection = null;
		List<String> messages = new ArrayList<>();
		messages.add("\n--- Select id line from clean ---\n");
		Statement statement = null;
		try {
			statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			newConnection = new DatabaseTreatment(statement);
			String sqlID= "SELECT gbifID_ FROM Workflow.Clean_" + this.getUuid() + " WHERE UUID_=\"" + this.getUuid() + "\";";
			messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlID));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> resultatGbifID = newConnection.getResultatSelect();

		return resultatGbifID;
	}

	/**
	 * Get id from DarwinCoreInput table (not Clean)
	 *  
	 * @return ArrayList<String>
	 */
	public List<String> getID(){
		DatabaseTreatment newConnection = null;
		List<String> messages = new ArrayList<>();
		messages.add("\n--- Select id line from DarwinCoreInput ---\n");
		Statement statement = null;
		try {
			statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			newConnection = new DatabaseTreatment(statement);
			String sqlID= "SELECT id_ FROM Workflow.DarwinCoreInput WHERE UUID_=\"" + this.getUuid() + "\";";
			messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlID));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> resultatID = newConnection.getResultatSelect();

		return resultatID;
	}

	/**
	 * Get all iso2 code clean (countryCode_) from input file. 
	 * 
	 * @return ArrayList<String>
	 */
	public List<String> getIso2Clean(){
		
		DatabaseTreatment newConnection = null;
		List<String> messages = new ArrayList<>();
		messages.add("\n--- Select iso2 code ---");
		Statement statement = null;
		try {
			statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			newConnection = new DatabaseTreatment(statement);
			String sqlISO2 = "SELECT countryCode_ FROM Workflow.Clean_" + this.getUuid() + " WHERE UUID_=\"" + this.getUuid() + "\";";
			messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlISO2));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> resultatISO2 = newConnection.getResultatSelect();

		return resultatISO2;
	}


	public String getValueFromColumn(String columnName, String idOccurrence){
		String value = "";

		DatabaseTreatment newConnection = null;
		List<String> messages = new ArrayList<>();
		messages.add("\n--- Select value " + columnName + " from id " + idOccurrence + " ---");
		Statement statement = null;
		try {
			statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			newConnection = new DatabaseTreatment(statement);
			String sql = "SELECT " + columnName + " FROM Workflow.DarwinCoreInput WHERE UUID_=\"" + this.getUuid() + "\" AND id_=\"" + idOccurrence + "\";";
			messages.addAll(newConnection.executeSQLcommand("executeQuery", sql));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> values = newConnection.getResultatSelect();
		if(values.size() == 2){
			value = values.get(1);
		}
		else{
			value = "error";
		}
		return value;
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


	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public File getDarwinCoreFileTemp() {
		return darwinCoreFileTemp;
	}

	public void setDarwinCoreFileTemp(File darwinCoreFileTemp) {
		this.darwinCoreFileTemp = darwinCoreFileTemp;
	}

}

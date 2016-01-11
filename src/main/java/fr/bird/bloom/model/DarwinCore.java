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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private ArrayList<String> darwinLines;
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
	 * 
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

	public void associateIdData() {
		idAssoData = new HashMap<>();
		DatabaseTreatment newConnection = null;
		List<String> messages = new ArrayList<>();
		List<String> idList = getIDClean();
		int countLine = 0;

		for (int i = 0; i < idList.size(); i++) {
			Statement statement = null;
			try {
				statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				newConnection = new DatabaseTreatment(statement);
				String sqlID = "SELECT * FROM Workflow.Clean_" + this.getUuid() + " WHERE UUID_=\"" + this.getUuid() + "\" AND id_=" + idList.get(i) + ";";
				messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlID));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<String> resultats = newConnection.getResultatSelect();

			try {
				if(statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			List<String> splitedLine = new ArrayList<>();
			//System.out.println("i : " + i);
			if(i == 0){
				List<String> firstLine = new ArrayList<>();
				firstLine.addAll(Arrays.asList(resultats.get(i).split(",")));
				firstLine.remove(0);
				//System.out.println("firstLine : " + firstLine);
				idAssoData.put(idList.get(i), firstLine);
			}
			else {
				splitedLine.addAll(Arrays.asList(resultats.get(1).split(",")));
				splitedLine.remove(0);
				//System.out.println("splitedLine : " + splitedLine);
				idAssoData.put(idList.get(i), splitedLine);
			}




			countLine ++;
		}
		//System.out.println(idAssoData);
	}

	/**
	 * Connect id of the line to values
	 * 
	 * @return void
	 */
	public void associateIdDataV1(){
		idAssoData = new HashMap<>();
		DatabaseTreatment newConnection = null;
		List<String> messages = new ArrayList<>();
		//ConnectionDatabase newConnection = new ConnectionDatabase();
		messages.add("\n--- Select id line from clean ---\n");
		Statement statement = null;
		try {
			statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			newConnection = new DatabaseTreatment(statement);
			String sqlID = "SELECT * FROM Workflow.Clean_" + this.getUuid() + " WHERE UUID_=\"" + this.getUuid() + "\";";
			messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlID));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		//System.out.println(sqlID);
		//--- Create DarwinCoreInput table ---
		//messages.addAll(newConnection.executeSQLcommand("executeQuery", sqlID));
		List<String> resultats = newConnection.getResultatSelect();

		try {
			if(statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for(int i = 0 ; i < resultats.size() ; i++){
			String id_ = resultats.get(i).split(",")[0];
			String line [] = resultats.get(i).split(",");
			List<String> infos = new ArrayList<>();
			for(int j = 1 ; j < line.length ; j++){
				infos.add(line[j]);
			}
			idAssoData.put(id_, infos);

		}
		//System.out.println(idAssoData);
	}


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
						//System.out.println(lineSplit.get(j));
						if(j == decimalLatitudeID || j == decimalLongitudeID){
							String checkedLatLong = "";
							String noCheckedLatLong = contentLine[j];
							checkedLatLong = noCheckedLatLong.replace(",", ".");
							newLine += "\"" + checkedLatLong + "\",";
						}
						else {
							if(contentLine[j].contains("\"")){

								String newContent = "\"" + contentLine[j].replaceAll("\"", "\\\\\"") + "\",";
								//System.out.println(contentLine[j] + "     " + newContent);
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
	 * Modified input file to format it.
	 * 
	 * @return List<String>
	 */
	public File readDarwinCoreFileV1(String separator) throws IOException {

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
			int decimalLatitudeID = 0 ;
			int decimalLongitudeID = 0 ;
			try{
				br = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = br.readLine() ) != null ){
					List<String> lineSplit = this.getSplitedLine(separator, line);//line.replaceAll("\"", "").replaceAll("\'", "").split(separator, -1);
					if(count == 0){
						for(int k = 0 ; k < lineSplit.size() ; k ++){
							String tagName = lineSplit.get(k);
							if(tagName.equals("decimalLatitude")){
								decimalLatitudeID = k;
							}
							else if(tagName.equals("decimalLongitude")){
								decimalLongitudeID = k;
							}
						}
					}
					else{
						String newLine = "";
						for(int j = 0 ; j < lineSplit.size() ; j++){
							//System.out.println(lineSplit.get(j));
							if(j == decimalLatitudeID || j == decimalLongitudeID){
								String checkedLatLong = "";
								String noCheckedLatLong = lineSplit.get(j);
								checkedLatLong = noCheckedLatLong.replace(",", ".");
								newLine += "\"" + checkedLatLong + "\",";
							}
							else {
								newLine += "\"" + lineSplit.get(j) + "\",";
							}


						}
						newLine += Integer.toString(idFile_) + ",0,\"" + this.getUuid() + "\"\n";
						//System.out.println(newLine);
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

	public List<String> getSplitedLine(String separator, String line){



		List<String> splitedLine = new ArrayList<>();
		String regex = "(^|(?<=" + separator + "))([^\"" + separator + "])*((?=" + separator + ")|$)|((?<=^\")|(?<=" + separator + "\"))([^\"]|\"\")*((?=\"" + separator + ")|(?=\"$))";

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		//System.out.println("******");
		//System.out.println(line);
		while (m.find()){
			splitedLine.add(m.group());
			//System.out.println(m.group());
		}

		//System.out.println("******");
		return splitedLine;
	}

	
	/**
	 * Modified initial files in order to fill in table DarwinCoreInput
	 * @param List<String> linesInputModified
	 * @param int nbFile
	 * @throws IOException
	 * @return File temporary
	 */
	public File createTemporaryFile(List<String> linesInputModified, int nbFile) throws IOException{


		File tempFile = new File(BloomConfig.getDirectoryPath() + "temp/" + this.getUuid() + "/data/inputFile_" + Integer.toString(nbFile) + ".csv");
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


	/**
	 * Found indice corresponding to tag name
	 * 
	 * @param tagName
	 * @return int
	 */
	public int getIndiceFromTag(String tagName){

		Map<String, List<String>> idAssoData = this.getIdAssoData();

		for(String id_ : idAssoData.keySet()){
			System.out.println("id_ : " + id_ + "\t" + idAssoData.values());
			if("id_".equals(id_)){

				List<String> tagsList = idAssoData.get(id_);
				for(int i = 0 ; i < tagsList.size() ; i++){
					System.out.println("i : " + i + "\t" + tagsList.get(i));
					if(tagsList.get(i).equals(tagName)){
						return i;
					}
				}
			}
		}
		return 0;
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

	/**
	 * 
	 * @return HashMap<String,ArrayList<String>>
	 */
	public Map<String, List<String>> getIdAssoData() {
		return idAssoData;
	}

	/**
	 * 
	 * @param idAssoData
	 * @return void
	 */
	public void setIdAssoData(HashMap<String, List<String>> idAssoData) {
		this.idAssoData = idAssoData;
	}

	/**
	 * 
	 * @return ArrayList<String>
	 */
	public List<String> getDarwinLines() {
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

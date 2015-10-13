/**
 * @author mhachet
 */
package src.model;

import java.io.File;
import java.io.IOException;
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
		this.darwinLines = super.getLines();
		this.nbSessionRandom = nbSessionRandom;
	}

	/**
	 * Connect id of the line to values
	 * 
	 * @return void
	 */
	public void associateIdData(){
		idAssoData = new HashMap<>();

		ConnectionDatabase newConnection = new ConnectionDatabase();

		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select id line from clean ---\n");

		String sqlID= "SELECT * FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE UUID_=\"" + this.getNbSessionRandom() + "\";";
		System.out.println(sqlID);
		//--- Create DarwinCoreInput table ---inputFile_
		messages.addAll(newConnection.newConnection("executeQuery", sqlID));
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
	public void readDarwinCoreFile() throws IOException {

		String separator = super.getSeparator();

		String firstLine = darwinLines.get(0);
		String firstNewLine = "";
		List<String> tags = Arrays.asList(firstLine.split(separator));

		for(int i = 0 ; i < tags.size() ; i++){
			String newTag = tags.get(i) + "_,";
			firstNewLine += newTag;
		}

		firstNewLine += "idFile_,id_,UUID_";
		darwinLines.set(0, firstNewLine);

		// don't add number file for the first line
		for(int l = 1; l < darwinLines.size() ; l++){
			String line [] = darwinLines.get(l).split(separator, -1);
			String newLine = "";
			for(int j = 0 ; j < line.length ; j++){
				newLine += "\"" + line[j] + "\",";
			}
			newLine += Integer.toString(idFile_) + ",0," + this.getNbSessionRandom() ;
			darwinLines.set(l, newLine);
		}

	}

	/**
	 * Get all latitude coordinates clean from input file.
	 *  
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getDecimalLatitudeClean(){
		ConnectionDatabase newConnection = new ConnectionDatabase();

		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select decimal latitude from coordinates ---");

		String sqlLatitude = "SELECT decimalLatitude_ FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE UUID_=\"" + this.getNbSessionRandom() + "\";";

		messages.addAll(newConnection.newConnection("executeQuery", sqlLatitude));
		ArrayList<String> resultatLatitude = newConnection.getResultatSelect();

		return resultatLatitude;
	}

	/**
	 * Get all longitude coordinates clean from input file.
	 *  
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getDecimalLongitudeClean(){
		ConnectionDatabase newConnection = new ConnectionDatabase();

		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select decimal longitude from coordinates ---");

		String sqlLongitude = "SELECT decimalLongitude_ FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE UUID_=\"" + this.getNbSessionRandom() + "\";";

		messages.addAll(newConnection.newConnection("executeQuery", sqlLongitude));
		ArrayList<String> resultatLongitude = newConnection.getResultatSelect();

		return resultatLongitude;
	}

	/**
	 * Get id from table Clean 
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getIDClean(){
		ConnectionDatabase newConnection = new ConnectionDatabase();

		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select id line from clean ---\n");

		String sqlID= "SELECT id_ FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE UUID_=\"" + this.getNbSessionRandom() + "\";";

		messages.addAll(newConnection.newConnection("executeQuery", sqlID));
		ArrayList<String> resultatID = newConnection.getResultatSelect();

		return resultatID;
	}

	/**
	 * Get gbifID from Clean table
	 *  
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getGbifIDClean(){
		ConnectionDatabase newConnection = new ConnectionDatabase();

		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select id line from clean ---\n");

		String sqlID= "SELECT gbifID_ FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE=UUID_=\"" + this.getNbSessionRandom() + "\";";

		messages.addAll(newConnection.newConnection("executeQuery", sqlID));
		ArrayList<String> resultatGbifID = newConnection.getResultatSelect();

		return resultatGbifID;
	}

	/**
	 * Get id from DarwinCoreInput table (not Clean)
	 *  
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getID(){
		ConnectionDatabase newConnection = new ConnectionDatabase();

		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select id line from DarwinCoreInput ---\n");

		String sqlID= "SELECT id_ FROM Workflow.DarwinCoreInput WHERE=UUID_=\"" + this.getNbSessionRandom() + "\";";

		messages.addAll(newConnection.newConnection("executeQuery", sqlID));
		ArrayList<String> resultatID = newConnection.getResultatSelect();

		return resultatID;
	}

	/**
	 * Get all iso2 code clean (countryCode_) from input file. 
	 * 
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getIso2Clean(){
		ConnectionDatabase newConnection = new ConnectionDatabase();

		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select iso2 code ---");

		String sqlISO2 = "SELECT countryCode_ FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE=UUID_=\"" + this.getNbSessionRandom() + "\";";

		messages.addAll(newConnection.newConnection("executeQuery", sqlISO2));
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

}

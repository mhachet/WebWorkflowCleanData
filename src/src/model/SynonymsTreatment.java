/**
 * 
 */
package src.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * model
 * 
 * SynonymsTreatment.java
 * SynonymsTreatment
 */
public class SynonymsTreatment {

	private File synonymsFile;
	private String DIRECTORY_PATH = "";
	private String RESSOURCES_PATH = "";
	private ArrayList<String> tagsList;
	private int nbSynonymInvolved;
	private String nbSessionRandom;

	public SynonymsTreatment (File fileSynonyms){
		this.synonymsFile = fileSynonyms;
	}

	public SynonymsTreatment(){

	}

	public ArrayList<String> getTagsSynonymsTempTable(){
		FileReader fr = null;
		try {
			fr = new FileReader(this.synonymsFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		String tags [] = null;
		try {
			int count = 0;

			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if(count == 0){
					tags = line.split(",");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for(int i = 0 ; i < tags.length ; i++){
			tagsList.add(tags[i]);
		}


		return tagsList;
	}

	/**
	 * 
	 * @param tableNameReference by default is Taxon. If user has changed, it's SynonymTemp
	 * @return void
	 */
	public void updateClean(){
		ConnectionDatabase newConnection = new ConnectionDatabase();

		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select to include synonyms ---");

		String sqlRetrieveSynonyms = "SELECT Clean_" + this.getNbSessionRandom() + ".*,Taxon.taxonID_ ,Taxon.acceptedNameUsageID_,Taxon.acceptedNameUsage_," +
				"Taxon.taxonomicStatus_,Taxon.scientificNameProper_ " +
				"FROM Workflow.Clean_" + this.getNbSessionRandom() + ",Workflow.Taxon WHERE Clean_" + this.getNbSessionRandom() + ".scientificName_=Taxon.scientificNameProper_"; 
		messages.addAll(newConnection.newConnection("executeQuery", sqlRetrieveSynonyms));
		ArrayList<String> resultatSelect = newConnection.getResultatSelect();
		if(resultatSelect != null){
			messages.add("nb lignes affectées : " + Integer.toString(resultatSelect.size() - 1));
			//this.createFileCsv(resultatSelect, "test");
			this.setNbSynonymInvolved(resultatSelect.size() - 1);
		}

		for(int i = 1 ; i < resultatSelect.size() ; i++){

			ConnectionDatabase newConnectionUpdate = new ConnectionDatabase();

			ArrayList<String> messagesUpdate = new ArrayList<String>();
			messagesUpdate.add("\n--- Update to include synonyms ---");

			String [] data = resultatSelect.get(i).split(",");
			int id_ = Integer.parseInt(data[0].replace("\"", ""));


			String taxonID_ = data[273];
			String acceptedNameUsageID_ = data[274];
			String acceptedNameUsage_ = data[275];
			String taxonomicStatus_ = data[276];

			String sqlUpdateClean = "UPDATE Workflow.Clean_" + this.getNbSessionRandom() + " SET Clean_" + this.getNbSessionRandom() + ".taxonID_=" + taxonID_ + ",Clean_" + this.getNbSessionRandom() + ".acceptedNameUsageID_=" 
					+ acceptedNameUsageID_ + ",Clean_" + this.getNbSessionRandom() + ".acceptedNameUsage_=" + acceptedNameUsage_ + ",Clean_" + this.getNbSessionRandom() + ".taxonomicStatus_=" + taxonomicStatus_ 
					+ " WHERE Clean.id_=" + id_ + ";"; 
			messagesUpdate.add(sqlUpdateClean);
			messagesUpdate.addAll(newConnectionUpdate.newConnection("executeUpdate", sqlUpdateClean));
			ArrayList<String> resultatUpdate = newConnectionUpdate.getResultatSelect();
			if(resultatUpdate != null){
				messagesUpdate.add("nb lignes affectées : " + Integer.toString(resultatUpdate.size() - 1));
			}			

			for(int j = 0 ; j < messagesUpdate.size() ; j++){
				System.out.println(messagesUpdate.get(j));
			}

		}
	}

	public void updateCleanFromSynonymTemp(){

		ConnectionDatabase newConnection = new ConnectionDatabase();

		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select to include synonyms ---");

		String sqlRetrieveSynonyms = "SELECT Clean_" + this.getNbSessionRandom() + ".*,SynonymTemp_" + this.getNbSessionRandom() + ".* " +
				"FROM Workflow.Clean_" + this.getNbSessionRandom() + ",Workflow.SynonymTemp_" + this.getNbSessionRandom() + " WHERE Clean_" + this.getNbSessionRandom() + ".scientificName_=SynonymTemp_" + this.getNbSessionRandom() + ".scientificNameProper_"; 
		messages.addAll(newConnection.newConnection("executeQuery", sqlRetrieveSynonyms));
		ArrayList<String> resultatSelect = newConnection.getResultatSelect();
		if(resultatSelect != null){
			messages.add("nb lignes affectées : " + Integer.toString(resultatSelect.size() - 1));
			//this.createFileCsv(resultatSelect, "test");
		}


		for(int i = 0 ; i < messages.size() ; i++){
			System.out.println(messages.get(i));
		}
	}

	public void createSynonymTempTable(){
		ArrayList<String> linesIncludeSynonyms = new ArrayList<>();
		FileReader fr = null;
		try {
			fr = new FileReader(synonymsFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);

		try {
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				linesIncludeSynonyms.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String type = "LONGTEXT default NULL";

		String [] tags = linesIncludeSynonyms.get(0).replace(",","_,").split(",");
		String sqlCreateSynonymTemp = "";

		for(int i = 0 ; i < tags.length ; i++){
			if(i == 0){
				sqlCreateSynonymTemp += "CREATE TABLE Workflow.SynonymTemp_" + this.getNbSessionRandom() + " (id_ BIGINT NOT NULL AUTO_INCREMENT PRIMARY_KEY,";
			}
			else if(i < tags.length - 1){
				sqlCreateSynonymTemp += tags[i] + " " + type + ",";
			}
			else{
				sqlCreateSynonymTemp += tags[i] + " " + type + ") ENGINE=INNODB;";
			}

		}
		ConnectionDatabase newConnection = new ConnectionDatabase();
		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select to include synonyms with user input file ---");

		messages.addAll(newConnection.newConnection("executeUpdate", sqlCreateSynonymTemp));
		ArrayList<String> resultatSelect = newConnection.getResultatSelect();
		if(resultatSelect != null){
			messages.add("nb lignes affectées : " + Integer.toString(resultatSelect.size() - 1));
			//this.createFileCsv(resultatSelect, "test");
		}

	}

	public File getSynonymsFile() {
		return synonymsFile;
	}

	public void setSynonymsFile(File synonymsFile) {
		this.synonymsFile = synonymsFile;
	}

	public ArrayList<String> getTagsList() {
		return tagsList;
	}

	public void setTagsList(ArrayList<String> tagsList) {
		this.tagsList = tagsList;
	}

	public int getNbSynonymInvolved() {
		return nbSynonymInvolved;
	}

	public void setNbSynonymInvolved(int nbSynonymInvolved) {
		this.nbSynonymInvolved = nbSynonymInvolved;
	}

	public String getNbSessionRandom() {
		return nbSessionRandom;
	}

	public void setNbSessionRandom(String nbSessionRandom) {
		this.nbSessionRandom = nbSessionRandom;
	}

	public String getDIRECTORY_PATH() {
		return DIRECTORY_PATH;
	}

	public void setDIRECTORY_PATH(String dIRECTORY_PATH) {
		DIRECTORY_PATH = dIRECTORY_PATH;
	}

	public String getRESSOURCES_PATH() {
		return RESSOURCES_PATH;
	}

	public void setRESSOURCES_PATH(String rESSOURCES_PATH) {
		RESSOURCES_PATH = rESSOURCES_PATH;
	}


}

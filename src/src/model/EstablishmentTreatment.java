/**
 * src.model
 * EstablishmentTreatment
 */
package src.model;

import java.io.File;
import java.util.ArrayList;

/**
 * src.model
 * 
 * EstablishmentTreatment.java
 * EstablishmentTreatment
 */
public class EstablishmentTreatment {

	private String DIRECTORY_PATH = "";
	private String RESSOURCES_PATH = "";
	private String nbSessionRandom;
	private ArrayList<String> listEstablishmentChecked;
	private ArrayList<String> inverseEstablishmentList;
	private ArrayList<String> noEstablishmentList;
	private File wrongEstablishmentMeansFile;

	public EstablishmentTreatment(ArrayList<String> listCheckedEstablishment){
		this.listEstablishmentChecked = listCheckedEstablishment;
	}

	/**
	 * start establishmentMeans option
	 */
	public void establishmentMeansTreatment(){
		ArrayList<String> inverseEstablishment = this.inverseEstablishmentList();
		this.setInverseEstablishmentList(inverseEstablishment);

		ArrayList<String> noEstablishment = this.filterOnEstablishmentMeans();
		this.setNoEstablishmentList(noEstablishment);

	}

	/**
	 * Retrieve establishmentMeans to delete
	 * 
	 * @return void
	 */
	public ArrayList<String> inverseEstablishmentList(){

		ArrayList<String> allEstablishmentMeans = new ArrayList<>();
		allEstablishmentMeans.add("native");
		allEstablishmentMeans.add("introduced");
		allEstablishmentMeans.add("naturalised");
		allEstablishmentMeans.add("invasive");
		allEstablishmentMeans.add("managed");
		allEstablishmentMeans.add("uncertain");
		allEstablishmentMeans.add("others");

		ArrayList<String> inverseEstablishmentList = new ArrayList<>();
		for(int i = 0 ; i < allEstablishmentMeans.size() ; i++){
			if(!listEstablishmentChecked.contains(allEstablishmentMeans.get(i))){
				inverseEstablishmentList.add(allEstablishmentMeans.get(i));
			}
		}

		return inverseEstablishmentList;

	}


	/**
	 * Filter on establishmentMeans
	 * 
	 * @param establishmentList
	 * @return void
	 */
	public ArrayList<String> filterOnEstablishmentMeans(){

		// list containing tags "establishmentMeans" to delete
		// inversed list of the begining (user want to keep the others) 
		ArrayList<String> noEstablishment = new ArrayList<>();

		for(int i = 0; i < this.getInverseEstablishmentList().size() ; i++){
			if(this.getInverseEstablishmentList().get(i).equals("others")){
				ConnectionDatabase newConnectionOthers = new ConnectionDatabase();
				ArrayList<String> messagesOthers = new ArrayList<String>();

				String sqlOthers = "SELECT * FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE Clean_" + this.getNbSessionRandom() + ".establishmentMeans_!=\"native\" && " +
						"Clean_" + this.getNbSessionRandom() + ".establishmentMeans_!=\"introduced\" && " +
						"Clean_" + this.getNbSessionRandom() + ".establishmentMeans_!=\"naturalised\" && " +
						"Clean_" + this.getNbSessionRandom() + ".establishmentMeans_!=\"invasive\" && " +
						"Clean_" + this.getNbSessionRandom() + ".establishmentMeans_!=\"managed\" && " +
						"Clean_" + this.getNbSessionRandom() + ".establishmentMeans_!=\"uncertain\";" ;
				messagesOthers.addAll(newConnectionOthers.newConnection("executeQuery", sqlOthers));

				ArrayList<String> othersResults = newConnectionOthers.getResultatSelect();
				if(othersResults.size() > 1){
					for(int m = 0 ; m < othersResults.size() ; m++){
						if(!noEstablishment.contains(othersResults.get(m))){
							//System.out.println("if " + othersResults.get(m));
							noEstablishment.add(othersResults.get(m));
						}
					}

				}

				for(int l = 0; l < messagesOthers.size() ; l++){
					System.out.println(messagesOthers.get(l));
				}

			}
			else{

				ConnectionDatabase newConnectionSelect = new ConnectionDatabase();
				ArrayList<String> messagesSelect = new ArrayList<String>();
				messagesSelect.add("\n--- Select no establishment Means ---\n");
				String sqlSelectNoEstablishment = "SELECT * FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE Clean_" + this.getNbSessionRandom() + ".establishmentMeans_=\"" + this.getInverseEstablishmentList().get(i) + "\";";
				messagesSelect.addAll(newConnectionSelect.newConnection("executeQuery", sqlSelectNoEstablishment));

				ArrayList<String> establishmentResults = newConnectionSelect.getResultatSelect();
				if(establishmentResults.size() > 1){
					for(int m = 0 ; m < establishmentResults.size() ; m++){
						if(!noEstablishment.contains(establishmentResults.get(m))){
							//System.out.println("else " + establishmentResults.get(m));
							noEstablishment.add(establishmentResults.get(m));
						}
					}
				}

				for(int k = 0; k < messagesSelect.size() ; k++){
					System.out.println(messagesSelect.get(k));
				}

				ConnectionDatabase newConnection = new ConnectionDatabase();
				ArrayList<String> messagesDelete = new ArrayList<String>();
				messagesDelete.add("\n--- establishment Means ---\n");
				String sqlDeleteEstablishment = "DELETE FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE Clean_" + this.getNbSessionRandom() + ".establishmentMeans_=\"" + this.getInverseEstablishmentList().get(i) + "\";";
				messagesDelete.addAll(newConnection.newConnection("executeUpdate", sqlDeleteEstablishment));

				for(int j = 0; j < messagesDelete.size() ; j++){
					System.out.println(messagesDelete.get(j));
				}
			}
		}
		/*
		for(int k = 0; k < noEstablishment.size() ; k++){
		    System.out.println(noEstablishment.get(k));
		}*/



		return noEstablishment;

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

	public String getNbSessionRandom() {
		return nbSessionRandom;
	}

	public void setNbSessionRandom(String nbSessionRandom) {
		this.nbSessionRandom = nbSessionRandom;
	}

	public ArrayList<String> getListEstablishmentChecked() {
		return listEstablishmentChecked;
	}

	public void setListEstablishmentChecked(
			ArrayList<String> listEstablishmentChecked) {
		this.listEstablishmentChecked = listEstablishmentChecked;
	}

	public ArrayList<String> getInverseEstablishmentList() {
		return inverseEstablishmentList;
	}

	public void setInverseEstablishmentList(
			ArrayList<String> inverseEstablishmentList) {
		this.inverseEstablishmentList = inverseEstablishmentList;
	}

	public ArrayList<String> getNoEstablishmentList() {
		return noEstablishmentList;
	}

	public void setNoEstablishmentList(ArrayList<String> noEstablishmentList) {
		this.noEstablishmentList = noEstablishmentList;
	}

	public File getWrongEstablishmentMeansFile() {
		return wrongEstablishmentMeansFile;
	}

	public void setWrongEstablishmentMeansFile(File wrongEstablishmentMeansFile) {
		this.wrongEstablishmentMeansFile = wrongEstablishmentMeansFile;
	}

}

/**
 * src.model
 * EstablishmentTreatment
 */
package fr.bird.bloom.model;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * src.model
 * 
 * EstablishmentTreatment.java
 * EstablishmentTreatment
 */
public class EstablishmentTreatment {

	private String uuid;
	private List<String> listEstablishmentChecked;
	private List<String> inverseEstablishmentList;
	private List<String> noEstablishmentList;
	private File wrongEstablishmentMeansFile;

	public EstablishmentTreatment(List<String> listCheckedEstablishment){
		this.listEstablishmentChecked = listCheckedEstablishment;
	}

	/**
	 * start establishmentMeans option
	 */
	public void establishmentMeansTreatment(){
		List<String> inverseEstablishment = this.inverseEstablishmentList();
		this.setInverseEstablishmentList(inverseEstablishment);

		List<String> noEstablishment = this.filterOnEstablishmentMeans();
		this.setNoEstablishmentList(noEstablishment);

	}

	/**
	 * Retrieve establishmentMeans to delete
	 * 
	 * @return void
	 */
	public List<String> inverseEstablishmentList(){

		List<String> allEstablishmentMeans = new ArrayList<>();
		allEstablishmentMeans.add("native");
		allEstablishmentMeans.add("introduced");
		allEstablishmentMeans.add("naturalised");
		allEstablishmentMeans.add("invasive");
		allEstablishmentMeans.add("managed");
		allEstablishmentMeans.add("uncertain");
		allEstablishmentMeans.add("others");

		List<String> inverseEstablishmentList = new ArrayList<>();
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
	public List<String> filterOnEstablishmentMeans(){

		// list containing tags "establishmentMeans" to delete
		// inversed list of the begining (user want to keep the others) 
		List<String> noEstablishment = new ArrayList<>();

		for(int i = 0; i < this.getInverseEstablishmentList().size() ; i++){
			if(this.getInverseEstablishmentList().get(i).equals("others")){
				Statement statement = null;
				try {
					statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DatabaseTreatment newConnectionOthers = new DatabaseTreatment(statement);
				List<String> messagesOthers = new ArrayList<String>();

				String sqlOthers = "SELECT * FROM Workflow.Clean_" + this.getUuid() + " WHERE Clean_" + this.getUuid() + ".establishmentMeans_!=\"native\" && " +
						"Clean_" + this.getUuid() + ".establishmentMeans_!=\"introduced\" && " +
						"Clean_" + this.getUuid() + ".establishmentMeans_!=\"naturalised\" && " +
						"Clean_" + this.getUuid() + ".establishmentMeans_!=\"invasive\" && " +
						"Clean_" + this.getUuid() + ".establishmentMeans_!=\"managed\" && " +
						"Clean_" + this.getUuid() + ".establishmentMeans_!=\"uncertain\";" ;
				messagesOthers.addAll(newConnectionOthers.executeSQLcommand("executeQuery", sqlOthers));

				List<String> othersResults = newConnectionOthers.getResultatSelect();
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

				Statement statementSelect = null;
				try {
					statementSelect = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DatabaseTreatment newConnectionSelect = new DatabaseTreatment(statementSelect);
				List<String> messagesSelect = new ArrayList<String>();
				messagesSelect.add("\n--- Select no establishment Means ---\n");
				String sqlSelectNoEstablishment = "SELECT * FROM Workflow.Clean_" + this.getUuid() + " WHERE Clean_" + this.getUuid() + ".establishmentMeans_=\"" + this.getInverseEstablishmentList().get(i) + "\";";
				messagesSelect.addAll(newConnectionSelect.executeSQLcommand("executeQuery", sqlSelectNoEstablishment));

				List<String> establishmentResults = newConnectionSelect.getResultatSelect();
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

				Statement statement = null;
				try {
					statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DatabaseTreatment newConnection = new DatabaseTreatment(statement);
				List<String> messagesDelete = new ArrayList<String>();
				messagesDelete.add("\n--- establishment Means ---\n");
				String sqlDeleteEstablishment = "DELETE FROM Workflow.Clean_" + this.getUuid() + " WHERE Clean_" + this.getUuid() + ".establishmentMeans_=\"" + this.getInverseEstablishmentList().get(i) + "\";";
				messagesDelete.addAll(newConnection.executeSQLcommand("executeUpdate", sqlDeleteEstablishment));

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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public List<String> getListEstablishmentChecked() {
		return listEstablishmentChecked;
	}

	public void setListEstablishmentChecked(
			ArrayList<String> listEstablishmentChecked) {
		this.listEstablishmentChecked = listEstablishmentChecked;
	}

	public List<String> getInverseEstablishmentList() {
		return inverseEstablishmentList;
	}

	public void setInverseEstablishmentList(
			List<String> inverseEstablishmentList) {
		this.inverseEstablishmentList = inverseEstablishmentList;
	}

	public List<String> getNoEstablishmentList() {
		return noEstablishmentList;
	}

	public void setNoEstablishmentList(List<String> noEstablishmentList) {
		this.noEstablishmentList = noEstablishmentList;
	}

	public File getWrongEstablishmentMeansFile() {
		return wrongEstablishmentMeansFile;
	}

	public void setWrongEstablishmentMeansFile(File wrongEstablishmentMeansFile) {
		this.wrongEstablishmentMeansFile = wrongEstablishmentMeansFile;
	}

}

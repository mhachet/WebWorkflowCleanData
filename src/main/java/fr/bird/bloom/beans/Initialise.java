/**
 * @author mhachet
 */
package fr.bird.bloom.beans;

import java.io.File;
import java.util.ArrayList;

import fr.bird.bloom.model.MappingReconcilePreparation;

/**
 * src.beans
 * 
 * Initiliaze
 */
public class Initialise {

    private ArrayList<File> inputMappedFilesList = new ArrayList<>();
    private ArrayList<File> inputRastersList = new ArrayList<>();
    private ArrayList<File> headerRasterList = new ArrayList<>();
    private ArrayList<File> inputSynonymsList = new ArrayList<>();
    private ArrayList<String> establishmentList = new ArrayList<>();
    private ArrayList<MappingReconcilePreparation> listMappingReconcileFiles = new ArrayList<>();
    
    private String DIRECTORY_PATH = "";
    private String RESSOURCES_PATH = "";
    
    private boolean synonym;
    private boolean tdwg4Code;
    private boolean raster;
    private boolean establishment;    
    private String nbSessionRandom;
    private int nbInput;
    private String emailUser;
    private boolean sendEmail;
    
    /**
     * 
     * src.beans
     * Initialise
     */
    public Initialise(){
	
    }
    
    /**
     * 
     * @return ArrayList<MappingReconcilePreparation>
     */
    public ArrayList<MappingReconcilePreparation> getListMappingReconcileFiles() {
        return listMappingReconcileFiles;
    }

    /**
     * 
     * @param listMappingReconcileFiles
     * @return void
     */
    public void setListMappingReconcileFiles(
    	ArrayList<MappingReconcilePreparation> listMappingReconcileFiles) {
        this.listMappingReconcileFiles = listMappingReconcileFiles;
    }

    /**
     * 
     * @return ArrayList<File>
     */
    public ArrayList<File> getInputMappedFilesList() {
        return inputMappedFilesList;
    }

    /**
     * 
     * @param inputMappedFilesList
     * @return void
     */
    public void setInputMappedFilesList(ArrayList<File> inputMappedFilesList) {
        this.inputMappedFilesList = inputMappedFilesList;
    }

    /**
     * 
     * @return ArrayList<File>
     */
    public ArrayList<File> getInputRastersList() {
        return inputRastersList;
    }

    /**
     * 
     * @param inputRastersList
     * @return void
     */
    public void setInputRastersList(ArrayList<File> inputRastersList) {
        this.inputRastersList = inputRastersList;
    }
    
    /**
     * 
     * @return ArrayList<File>
     */
    public ArrayList<File> getHeaderRasterList() {
        return headerRasterList;
    }

    /**
     * 
     * @param headerRasterList
     * @return void
     */
    public void setHeaderRasterList(ArrayList<File> headerRasterList) {
        this.headerRasterList = headerRasterList;
    }

    /**
     * 
     * @return ArrayList<File>
     */
    public ArrayList<File> getInputSynonymsList() {
        return inputSynonymsList;
    }

    /**
     * 
     * @param inputSynonymsList
     * @return void
     */
    public void setInputSynonymsList(ArrayList<File> inputSynonymsList) {
        this.inputSynonymsList = inputSynonymsList;
    }

    /**
     * 
     * @return boolean
     */
    public boolean isSynonym() {
        return synonym;
    }

    /**
     * 
     * @param synonym
     * @return void
     */
    public void setSynonym(boolean synonym) {
        this.synonym = synonym;
    }

    /**
     * 
     * @return boolean
     */
    public boolean isTdwg4Code() {
        return tdwg4Code;
    }

    /**
     * 
     * @param tdwg4Code
     * @return void
     */
    public void setTdwg4Code(boolean tdwg4Code) {
        this.tdwg4Code = tdwg4Code;
    }

    /**
     * 
     * @return ArrayList<String>
     */
    public ArrayList<String> getEstablishmentList() {
        return establishmentList;
    }

    /**
     * 
     * @param establishmentList
     * @return void
     */
    public void setEstablishmentList(ArrayList<String> establishmentList) {
        this.establishmentList = establishmentList;
    }

    /**
     * 
     * @return boolean
     */
    public boolean isEstablishment() {
        return establishment;
    }

    /**
     * 
     * @param establishment
     * @return void
     */
    public void setEstablishment(boolean establishment) {
        this.establishment = establishment;
    }    

    /**
     * 
     * @return boolean
     */
    public boolean isRaster() {
        return raster;
    }

    /**
     * 
     * @param raster
     * @return void
     */
    public void setRaster(boolean raster) {
        this.raster = raster;
    }

    /**
     * 
     * @return int
     */
    public String getNbSessionRandom() {
        return nbSessionRandom;
    }

    /**
     * 
     * @param nbSessionRandom
     * @return void
     */
    public void setNbSessionRandom(String nbSessionRandom) {
        this.nbSessionRandom = nbSessionRandom;
    }
    
    public int getNbFiles(){
	int nbFiles = this.getListMappingReconcileFiles().size();
	
	return nbFiles;
    }

    public String getDIRECTORY_PATH() {
        return this.DIRECTORY_PATH;
    }

    public void setDIRECTORY_PATH(String DIRECTORY_PATH) {
        this.DIRECTORY_PATH = DIRECTORY_PATH;
    }

    public String getRESSOURCES_PATH() {
        return this.RESSOURCES_PATH;
    }

    public void setRESSOURCES_PATH(String RESSOURCES_PATH) {
        this.RESSOURCES_PATH = RESSOURCES_PATH;
    }

	public int getNbInput() {
		return nbInput;
	}

	public void setNbInput(int nbInput) {
		this.nbInput = nbInput;
	}

	public String getEmailUser() {
		return emailUser;
	}

	public void setEmailUser(String emailUser) {
		this.emailUser = emailUser;
	}

	public boolean isSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(boolean sendEmail) {
		this.sendEmail = sendEmail;
	}
	
    
}

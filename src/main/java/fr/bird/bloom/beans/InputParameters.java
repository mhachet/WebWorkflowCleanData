/**
 * @author mhachet
 */
package fr.bird.bloom.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.bird.bloom.model.MappingReconcilePreparation;

/**
 * src.beans
 * <p>
 * Initiliaze
 */
public class InputParameters {

    private List<File> inputMappedFilesList = new ArrayList<>();
    private List<File> inputRastersList = new ArrayList<>();
    private List<File> headerRasterList = new ArrayList<>();
    private List<File> inputSynonymsList = new ArrayList<>();
    private List<String> establishmentList = new ArrayList<>();
    private List<MappingReconcilePreparation> listMappingReconcileFiles = new ArrayList<>();

    private boolean synonym;
    private boolean tdwg4Code;
    private boolean raster;
    private boolean establishment;
    private String uuid;
    private int nbInput;
    private String emailUser;
    private boolean sendEmail;

    /**
     * @return ArrayList<MappingReconcilePreparation>
     */
    public List<MappingReconcilePreparation> getListMappingReconcileFiles() {
        return listMappingReconcileFiles;
    }

    /**
     * @param listMappingReconcileFiles
     * @return void
     */
    public void setListMappingReconcileFiles(List<MappingReconcilePreparation> listMappingReconcileFiles) {
        this.listMappingReconcileFiles = listMappingReconcileFiles;
    }

    /**
     * @return ArrayList<File>
     */
    public List<File> getInputMappedFilesList() {
        return inputMappedFilesList;
    }

    /**
     * @param inputMappedFilesList
     * @return void
     */
    public void setInputMappedFilesList(ArrayList<File> inputMappedFilesList) {
        this.inputMappedFilesList = inputMappedFilesList;
    }

    /**
     * @return ArrayList<File>
     */
    public List<File> getInputRastersList() {
        return inputRastersList;
    }

    /**
     * @param inputRastersList
     * @return void
     */
    public void setInputRastersList(ArrayList<File> inputRastersList) {
        this.inputRastersList = inputRastersList;
    }

    /**
     * @return ArrayList<File>
     */
    public List<File> getHeaderRasterList() {
        return headerRasterList;
    }

    /**
     * @param headerRasterList
     * @return void
     */
    public void setHeaderRasterList(ArrayList<File> headerRasterList) {
        this.headerRasterList = headerRasterList;
    }

    /**
     * @return ArrayList<File>
     */
    public List<File> getInputSynonymsList() {
        return inputSynonymsList;
    }

    /**
     * @param inputSynonymsList
     * @return void
     */
    public void setInputSynonymsList(ArrayList<File> inputSynonymsList) {
        this.inputSynonymsList = inputSynonymsList;
    }

    /**
     * @return boolean
     */
    public boolean isSynonym() {
        return synonym;
    }

    /**
     * @param synonym
     * @return void
     */
    public void setSynonym(boolean synonym) {
        this.synonym = synonym;
    }

    /**
     * @return boolean
     */
    public boolean isTdwg4Code() {
        return tdwg4Code;
    }

    /**
     * @param tdwg4Code
     * @return void
     */
    public void setTdwg4Code(boolean tdwg4Code) {
        this.tdwg4Code = tdwg4Code;
    }

    /**
     * @return ArrayList<String>
     */
    public List<String> getEstablishmentList() {
        return establishmentList;
    }

    /**
     * @param establishmentList
     * @return void
     */
    public void setEstablishmentList(List<String> establishmentList) {
        this.establishmentList = establishmentList;
    }

    /**
     * @return boolean
     */
    public boolean isEstablishment() {
        return establishment;
    }

    /**
     * @param establishment
     * @return void
     */
    public void setEstablishment(boolean establishment) {
        this.establishment = establishment;
    }

    /**
     * @return boolean
     */
    public boolean isRaster() {
        return raster;
    }

    /**
     * @param raster
     * @return void
     */
    public void setRaster(boolean raster) {
        this.raster = raster;
    }

    /**
     * @return int
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid
     * @return void
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getNbFiles() {
        int nbFiles = this.getListMappingReconcileFiles().size();

        return nbFiles;
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

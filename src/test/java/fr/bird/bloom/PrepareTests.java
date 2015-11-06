/**
 * src.test
 * ExampleWebTestCase
 */
package fr.bird.bloom;

/**
 * src.test
 * 
 * ExampleWebTestCase.java
 * ExampleWebTestCase
 */

import org.junit.Ignore;
import org.junit.Test;
import fr.bird.bloom.beans.Initialise;
import fr.bird.bloom.model.CSVFile;
import fr.bird.bloom.model.MappingDwC;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

@Ignore
public class PrepareTests {


    private String DIRECTORY_PATH = "/home/mhachet/workspace/WebWorkflowCleanData/src/resources/test/";
    private String RESSOURCES_PATH = "/home/mhachet/workspace/WebWorkflowCleanData/src/resources/";

    private Initialise initialisation;
    public String nbSessionRandom;
    private ArrayList<File> inputFilesList;
    private ArrayList<File> inputsRasterList;
    private ArrayList<File> inputHeaderList;
    private ArrayList<MappingDwC> listMappedDWC;
    
    @Test
    public void prepare() {


	initialisation = new Initialise();
	inputFilesList = new ArrayList<>();
	inputHeaderList = new ArrayList<>();
	inputsRasterList = new ArrayList<>();

	initialisation.setDIRECTORY_PATH(DIRECTORY_PATH);
	initialisation.setRESSOURCES_PATH(RESSOURCES_PATH);

	setNbSessionRandom(generateRandomKey());
	prepareInputFiles();

	initialisation.setRaster(true);
	prepareInputsRaster();
	prepareInputsHeader();

	initialisation.setEstablishment(true);
	prepareEstablishment();

	initialisation.setSynonym(true);
	initialisation.setTdwg4Code(true);

	System.out.println("beforeclass 1 ");
	//setBaseUrl("http://localhost:8080/WebWorkflowCleanData/HomePage.jsp");	
    }
 
    
    public void prepareInputFiles(){

	if(!new File(DIRECTORY_PATH + "temp/").exists()){
	    new File(DIRECTORY_PATH + "temp/").mkdirs();
	}

	listMappedDWC = new ArrayList<>();
	
	inputFilesList.add(new File(DIRECTORY_PATH + "inputs_data/test1_noDWC.csv"));
	inputFilesList.add(new File(DIRECTORY_PATH + "inputs_data/test2_DWC.csv"));
	inputFilesList.add(new File(DIRECTORY_PATH + "inputs_data/test3_DWC.csv"));

	for(int i = 0 ; i < inputFilesList.size() ; i++){
	    File file = new File(DIRECTORY_PATH + "temp/noMappedDWC_" + getNbSessionRandom() + "_" + (i + 1 ) + ".csv");
	    FileWriter writer = null;
	    try {
		writer = new FileWriter(file.getAbsoluteFile());

		BufferedWriter output = new BufferedWriter(writer);
		ArrayList<String> linesFile = getLinesFile(inputFilesList.get(i));

		for(int j = 0 ; j < linesFile.size() ; j++){
		    output.write(linesFile.get(j) + "\n");
		    output.flush();

		}
		writer.close();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    CSVFile csvFile = new CSVFile(file);
	    MappingDwC newMappingDWC = new MappingDwC(csvFile, Boolean.toString(false));
	    //newMappingDWC.setOriginalName(inputFilesList.get(i).getName());
	    String nameFile = inputFilesList.get(i).getName();
	    //newMappingDWC.setOriginalExtension(nameFile.substring(nameFile.indexOf('.')+1,nameFile.length()));
	    listMappedDWC.add(newMappingDWC);
	    newMappingDWC.initialiseMapping(getNbSessionRandom());
	    HashMap<String, String> connectionTags = new HashMap<>();
	    ArrayList<String> tagsNoMapped = newMappingDWC.getTagsListNoMapped();
	    for(int k = 0 ; k < tagsNoMapped.size() ; k++){
		connectionTags.put(tagsNoMapped.get(k) + "_" + k, "");
	    }
	    newMappingDWC.setConnectionTags(connectionTags);
	    newMappingDWC.getNoMappedFile().setCsvName(file.getName());
	    //initialisation.getInputFilesList().add(csvFile.getCsvFile());

	    boolean doMapping = newMappingDWC.doMapping();
	    newMappingDWC.setMappingInvolved(Boolean.toString(doMapping));
	    if(doMapping){
		ArrayList<String> presentTags = newMappingDWC.getPresentTags();
		for(Entry<String, String> entry : connectionTags.entrySet()) {
		    String [] tableKey = entry.getKey().split("_");
		    String idKey = tableKey[0];
		    if(presentTags.contains(idKey)){
			connectionTags.put(entry.getKey(), idKey);
		    }else{
			if(idKey.equals("isoCode")){
			  connectionTags.put(entry.getKey(), "countryCode");
			}
			
		    }
		}
	    }
	    

	}
	
	//initialisation.setListMappingFiles(listMappedDWC);
    }

    public void prepareInputsRaster(){

	initialisation.setRaster(true);

	inputsRasterList.add(new File(DIRECTORY_PATH + "inputs_data/alt_22.bil"));
	inputsRasterList.add(new File(DIRECTORY_PATH + "inputs_data/prec1_46.bil"));
	inputsRasterList.add(new File(DIRECTORY_PATH + "inputs_data/tmean1.bil"));

	initialisation.setInputRastersList(inputsRasterList);
    }

    public void prepareInputsHeader(){
	inputHeaderList.add(new File(DIRECTORY_PATH + "inputs_data/alt_22.hdr"));
	inputHeaderList.add(new File(DIRECTORY_PATH + "inputs_data/prec1_46.hdr"));
	inputHeaderList.add(new File(DIRECTORY_PATH + "inputs_data/tmean1.hdr"));

	initialisation.setHeaderRasterList(inputHeaderList);

    }

    public void prepareEstablishment(){
	ArrayList<String> listEstablishment = new ArrayList<>();
	listEstablishment.add("introduced");
	listEstablishment.add("invasive");
	listEstablishment.add("native");
	listEstablishment.add("managed");

	initialisation.setEstablishmentList(listEstablishment);
    }

    public ArrayList<String> getLinesFile(File file){
	ArrayList<String> linesFile = new ArrayList<>();
	try{
	    InputStream ips = new FileInputStream(file.getAbsoluteFile()); 
	    InputStreamReader ipsr = new InputStreamReader(ips);
	    BufferedReader br = new BufferedReader(ipsr);
	    String line;
	    while ((line = br.readLine()) != null){
		linesFile.add(line);
	    }
	    br.close(); 
	}		
	catch (Exception e){
	    System.out.println(e.toString());
	}

	return linesFile;

    }

    /**
     * @return int
     */
    private String generateRandomKey() {
	String nbSessionRandom = UUID.randomUUID().toString().replace("-","_");
	return nbSessionRandom;
    }

    public String getDIRECTORY_PATH() {
	return DIRECTORY_PATH;
    }

    public void setDIRECTORY_PATH(String dIRECTORY_PATH) {
	DIRECTORY_PATH = dIRECTORY_PATH;
    }

    public Initialise getInitialisation() {
	return initialisation;
    }

    public void setInitialisation(Initialise initialise) {
	initialisation = initialise;
    }

    public String getNbSessionRandom() {
	return nbSessionRandom;
    }

    public void setNbSessionRandom(String nbSession) {
	nbSessionRandom = nbSession;
    }


    public String getRESSOURCES_PATH() {
        return RESSOURCES_PATH;
    }


    public void setRESSOURCES_PATH(String rESSOURCES_PATH) {
        RESSOURCES_PATH = rESSOURCES_PATH;
    }


    public ArrayList<File> getInputFilesList() {
        return inputFilesList;
    }


    public void setInputFilesList(ArrayList<File> inputFilesList) {
        this.inputFilesList = inputFilesList;
    }


    public ArrayList<File> getInputsRasterList() {
        return inputsRasterList;
    }


    public void setInputsRasterList(ArrayList<File> inputsRasterList) {
        this.inputsRasterList = inputsRasterList;
    }


    public ArrayList<File> getInputHeaderList() {
        return inputHeaderList;
    }


    public void setInputHeaderList(ArrayList<File> inputHeaderList) {
        this.inputHeaderList = inputHeaderList;
    }


    public ArrayList<MappingDwC> getListMappedDWC() {
        return listMappedDWC;
    }


    public void setListMappedDWC(ArrayList<MappingDwC> listMappedDWC) {
        this.listMappedDWC = listMappedDWC;
    }
    
}
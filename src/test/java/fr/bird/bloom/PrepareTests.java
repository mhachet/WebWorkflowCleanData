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

import fr.bird.bloom.beans.InputParameters;
import org.junit.Ignore;
import org.junit.Test;
import fr.bird.bloom.model.CSVFile;
import fr.bird.bloom.model.MappingDwC;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

@Ignore
public class PrepareTests {


    private String DIRECTORY_PATH = "/home/mhachet/workspace/WebWorkflowCleanData/src/resources/test/";
    private String RESSOURCES_PATH = "/home/mhachet/workspace/WebWorkflowCleanData/src/resources/";

    private InputParameters inputParameters;
    public String uuid;
    private ArrayList<File> inputFilesList;
    private ArrayList<File> inputsRasterList;
    private ArrayList<File> inputHeaderList;
    private ArrayList<MappingDwC> listMappedDWC;
    
    @Test
    public void prepare() {


		inputParameters = new InputParameters();
		inputFilesList = new ArrayList<>();
		inputHeaderList = new ArrayList<>();
		inputsRasterList = new ArrayList<>();

		setUuid(generateRandomKey());
		prepareInputFiles();

		inputParameters.setRaster(true);
		prepareInputsRaster();
		prepareInputsHeader();

		inputParameters.setEstablishment(true);
		prepareEstablishment();

		inputParameters.setSynonym(true);
		inputParameters.setTdwg4Code(true);

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
			File file = new File(DIRECTORY_PATH + "temp/noMappedDWC_" + getUuid() + "_" + (i + 1 ) + ".csv");
			FileWriter writer = null;
			try {
			writer = new FileWriter(file.getAbsoluteFile());

			BufferedWriter output = new BufferedWriter(writer);
			List<String> linesFile = getLinesFile(inputFilesList.get(i));

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
			MappingDwC newMappingDWC = new MappingDwC(csvFile, false);
			//newMappingDWC.setOriginalName(inputFilesList.get(i).getName());
			String nameFile = inputFilesList.get(i).getName();
			//newMappingDWC.setOriginalExtension(nameFile.substring(nameFile.indexOf('.')+1,nameFile.length()));
			listMappedDWC.add(newMappingDWC);
			newMappingDWC.initialiseMapping(getUuid());
			HashMap<String, String> connectionTags = new HashMap<>();
			List<String> tagsNoMapped = newMappingDWC.getTagsListNoMapped();
			for(int k = 0 ; k < tagsNoMapped.size() ; k++){
			connectionTags.put(tagsNoMapped.get(k) + "_" + k, "");
			}
			newMappingDWC.setConnectionTags(connectionTags);
			newMappingDWC.getNoMappedFile().setCsvName(file.getName());
			//inputParameters.getInputFilesList().add(csvFile.getCsvFile());

			boolean doMapping = newMappingDWC.doMapping();
			newMappingDWC.setMappingInvolved(doMapping);
			if(doMapping){
			List<String> presentTags = newMappingDWC.getPresentTags();
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

		//inputParameters.setListMappingFiles(listMappedDWC);
    }

    public void prepareInputsRaster(){

		inputParameters.setRaster(true);

		inputsRasterList.add(new File(DIRECTORY_PATH + "inputs_data/alt_22.bil"));
		inputsRasterList.add(new File(DIRECTORY_PATH + "inputs_data/prec1_46.bil"));
		inputsRasterList.add(new File(DIRECTORY_PATH + "inputs_data/tmean1.bil"));

		inputParameters.setInputRastersList(inputsRasterList);
    }

    public void prepareInputsHeader(){
		inputHeaderList.add(new File(DIRECTORY_PATH + "inputs_data/alt_22.hdr"));
		inputHeaderList.add(new File(DIRECTORY_PATH + "inputs_data/prec1_46.hdr"));
		inputHeaderList.add(new File(DIRECTORY_PATH + "inputs_data/tmean1.hdr"));

		inputParameters.setHeaderRasterList(inputHeaderList);

    }

    public void prepareEstablishment(){
		List<String> listEstablishment = new ArrayList<>();
		listEstablishment.add("introduced");
		listEstablishment.add("invasive");
		listEstablishment.add("native");
		listEstablishment.add("managed");

		inputParameters.setEstablishmentList(listEstablishment);
    }

    public List<String> getLinesFile(File file){
		List<String> linesFile = new ArrayList<>();
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
		String uuid = UUID.randomUUID().toString().replace("-","_");
		return uuid;
    }

    public String getDIRECTORY_PATH() {
	return DIRECTORY_PATH;
    }

    public void setDIRECTORY_PATH(String dIRECTORY_PATH) {
	DIRECTORY_PATH = dIRECTORY_PATH;
    }

    public InputParameters getInputParameters() {
	return inputParameters;
    }

    public void setInputParameters(InputParameters inputParameters) {
	this.inputParameters = inputParameters;
    }

    public String getUuid() {
	return uuid;
    }

    public void setUuid(String nbSession) {
	uuid = nbSession;
    }


    public String getRESSOURCES_PATH() {
        return RESSOURCES_PATH;
    }


    public void setRESSOURCES_PATH(String rESSOURCES_PATH) {
        RESSOURCES_PATH = rESSOURCES_PATH;
    }


    public List<File> getInputFilesList() {
        return inputFilesList;
    }


    public void setInputFilesList(ArrayList<File> inputFilesList) {
        this.inputFilesList = inputFilesList;
    }


    public List<File> getInputsRasterList() {
        return inputsRasterList;
    }


    public void setInputsRasterList(ArrayList<File> inputsRasterList) {
        this.inputsRasterList = inputsRasterList;
    }


    public List<File> getInputHeaderList() {
        return inputHeaderList;
    }


    public void setInputHeaderList(ArrayList<File> inputHeaderList) {
        this.inputHeaderList = inputHeaderList;
    }


    public List<MappingDwC> getListMappedDWC() {
        return listMappedDWC;
    }


    public void setListMappedDWC(ArrayList<MappingDwC> listMappedDWC) {
        this.listMappedDWC = listMappedDWC;
    }
    
}
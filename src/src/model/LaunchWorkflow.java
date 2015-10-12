/**
 * src.model
 * LaunchWorkflow
 * TODO
 */
package src.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import src.beans.Finalisation;
import src.beans.Initialise;
import src.beans.Step1_MappingDwc;
import src.beans.Step2_ReconciliationService;
import src.beans.Step3_CheckCoordinates;
import src.beans.Step4_CheckGeoIssue;
import src.beans.Step5_IncludeSynonym;
import src.beans.Step6_CheckTDWG;
import src.beans.Step7_CheckISo2Coordinates;
import src.beans.Step8_CheckCoordinatesRaster;
import src.beans.Step9_EstablishmentMeans;

/**
 * src.model
 * 
 * LaunchWorkflow.java
 */
public class LaunchWorkflow {

	private Treatment dataTreatment;
	private Initialise initialisation;
	private Finalisation finalisation;
	//private String DIRECTORY_PATH = "/home/mhachet/workspace/WebWorkflowCleanData/WebContent/output/";

	private Step1_MappingDwc step1;
	private Step2_ReconciliationService step2;
	private Step3_CheckCoordinates step3;
	private Step4_CheckGeoIssue step4;
	private Step5_IncludeSynonym step5;
	private Step6_CheckTDWG step6;
	private Step7_CheckISo2Coordinates step7;
	private Step8_CheckCoordinatesRaster step8;
	private Step9_EstablishmentMeans step9;

	/**
	 * 
	 * src.model
	 * LaunchWorkflow
	 */
	public LaunchWorkflow(Initialise initialise){
		this.initialisation = initialise;
	}

	/**
	 * Call steps of the workflow
	 * 
	 * @throws IOException
	 * @return void
	 */
	public void initialiseLaunchWorkflow() throws IOException{
		File repCourant = new java.io.File(new java.io.File("").getAbsolutePath());
		//System.out.println("repCourant : "  + repCourant);
		this.dataTreatment = new Treatment();
		this.dataTreatment.setNbSessionRandom(initialisation.getNbSessionRandom());
		this.dataTreatment.setDIRECTORY_PATH(initialisation.getDIRECTORY_PATH());
		this.dataTreatment.setRESSOURCES_PATH(initialisation.getRESSOURCES_PATH());

		finalisation = new Finalisation();
		step1 = new Step1_MappingDwc();
		step2 = new Step2_ReconciliationService();
		step3 = new Step3_CheckCoordinates();
		step4 = new Step4_CheckGeoIssue();
		step5 = new Step5_IncludeSynonym();
		step6 = new Step6_CheckTDWG();
		step7 = new Step7_CheckISo2Coordinates();
		step8 = new Step8_CheckCoordinatesRaster();
		step9 = new Step9_EstablishmentMeans();

		this.isValidInputFiles();

		this.launchWorkflow();
		step3.setInvolved(true);
		step4.setInvolved(true);
		step7.setInvolved(true);

		if(this.initialisation.isSynonym()){

			boolean synonymFileIsValid = this.isValidSynonymFile();
			this.launchSynonymOption(synonymFileIsValid);
			step5.setInvolved(true);
		}

		if(this.initialisation.isTdwg4Code()){
			boolean sucessTdwgTreatment = dataTreatment.tdwgCodeOption();
			step6.setStep6_ok(sucessTdwgTreatment);
			step6.setInvolved(true);
		}

		if(this.initialisation.isRaster()){
			step8.setInvolved(true);
			boolean rasterFilesIsValid = this.isValidRasterFiles();
			if(rasterFilesIsValid){
				this.launchRasterOption();	
				step8.setStep8_ok(true);
			}
			else{
				step8.setStep8_ok(false);
			}
		}

		//System.out.println("establishment : " + this.initialisation.getEstablishmentList());accessRight
		//keep introduced data
		if(this.initialisation.isEstablishment()){
			this.launchEstablishmentMeansOption();
			step9.setInvolved(true);
		}

		this.writeFinalOutput();

		//this.dataTreatment.deleteTables();
	}

	/**
	 * Call main steps of the workflow
	 * 
	 * @throws IOException
	 * @return void
	 */
	public void launchWorkflow() throws IOException{

		ArrayList<MappingReconcilePreparation> listMappingReconcileDWC = this.initialisation.getListMappingReconcileFiles();
		HashMap<Integer, ReconciliationService> reconcilePath = step2.getInfos_reconcile();
		HashMap<Integer, MappingDwC> hashMapStep1 = step1.getInfos_mapping();
		
		for(int i = 0 ; i < listMappingReconcileDWC.size() ; i++){

			MappingDwC mappingDwc = listMappingReconcileDWC.get(i).getMappingDWC();
			MappingReconcilePreparation preparation = listMappingReconcileDWC.get(i);

			int idFile = listMappingReconcileDWC.get(i).getIdFile();
			mappingDwc.setIdFile(idFile);
			
			String originalName = listMappingReconcileDWC.get(i).getOriginalName();
			mappingDwc.setFilename(originalName);

			boolean isMapping = Boolean.parseBoolean(mappingDwc.getMappingInvolved());

			boolean isValid = preparation.isValid();

			if(isMapping && isValid){
				step1.setInvolved(isMapping);
				
				this.dataTreatment.mappingDwC(mappingDwc, idFile);
				String pathMappedFile = mappingDwc.getMappedFile().getAbsolutePath().replace(initialisation.getDIRECTORY_PATH(),"");

				mappingDwc.setFilepath(pathMappedFile);
			}
			hashMapStep1.put(idFile, mappingDwc);
			
			ReconciliationService reconcileService = listMappingReconcileDWC.get(i).getReconcileDWC();
			boolean reconcile = reconcileService.isReconcile();
			if(reconcile && isValid){
				step2.setInvolved(reconcile);
				if(isMapping){
					CSVFile csvMappedFile = new CSVFile(mappingDwc.getMappedFile());
					this.dataTreatment.reconcileService(reconcileService, csvMappedFile, idFile);
				}
				else{
					this.dataTreatment.reconcileService(reconcileService, mappingDwc.getNoMappedFile(), idFile);
				}
				String pathReconcileFile = reconcileService.getReconcileFile().getAbsolutePath().replace(initialisation.getDIRECTORY_PATH(),"");
				reconcileService.setFilepath(pathReconcileFile);
				
				
			}
			reconcilePath.put(idFile, reconcileService);
		}


		for(int i = 0 ; i < this.initialisation.getListMappingReconcileFiles().size() ; i++){
			MappingReconcilePreparation mappingReconcilePrep = this.initialisation.getListMappingReconcileFiles().get(i);
			int idFile = mappingReconcilePrep.getIdFile();
			List<String> linesInputFile = null;
			MappingDwC mappingFile = mappingReconcilePrep.getMappingDWC();
			ReconciliationService reconcileFile = mappingReconcilePrep.getReconcileDWC();
			boolean isValid = mappingReconcilePrep.isValid();

			if(isValid){
				if(reconcileFile.isReconcile()){
					linesInputFile = this.dataTreatment.initialiseFile(reconcileFile.getReconcileFile(), idFile);
					
				}
				else if(Boolean.parseBoolean(mappingFile.getMappingInvolved())){
					
					linesInputFile = this.dataTreatment.initialiseFile(mappingFile.getMappedFile(), idFile);
					
				}
				else{
					linesInputFile = this.dataTreatment.initialiseFile(mappingFile.getNoMappedFile().getCsvFile(), idFile);
					
				}
				File inputFileModified = this.dataTreatment.createTemporaryFile(linesInputFile, idFile);
				String sqlInsert = this.dataTreatment.createSQLInsert(inputFileModified, linesInputFile);

				this.dataTreatment.createTableDarwinCoreInput(sqlInsert);
			}

		}	

		GeographicTreatment geoTreatment = this.dataTreatment.checkGeographicOption();

		File wrongCoordinatesFile = geoTreatment.getWrongCoordinatesFile();
		finalisation.setWrongCoordinatesFile(wrongCoordinatesFile);
		finalisation.setPathWrongCoordinatesFile(wrongCoordinatesFile.getAbsolutePath().replace(initialisation.getDIRECTORY_PATH(), ""));	
		step3.setNbFound(geoTreatment.getNbWrongCoordinates());
		step3.setPathWrongCoordinates(finalisation.getPathWrongCoordinatesFile());

		File wrongGeospatial = geoTreatment.getWrongGeoFile();
		finalisation.setWrongGeospatial(wrongGeospatial);
		finalisation.setPathWrongGeospatial(wrongGeospatial.getAbsolutePath().replace(initialisation.getDIRECTORY_PATH(), ""));
		step4.setNbFound(geoTreatment.getNbWrongGeospatialIssues());
		step4.setPathWrongGeoIssue(finalisation.getPathWrongGeospatial());

		File wrongPolygon = geoTreatment.getWrongPolygonFile();
		finalisation.setWrongPolygon(wrongPolygon);
		finalisation.setPathWrongPolygon(wrongPolygon.getAbsolutePath().replace(initialisation.getDIRECTORY_PATH(), ""));
		step7.setNbFound(geoTreatment.getNbWrongIso2());
		step7.setPathWrongIso2(finalisation.getPathWrongPolygon());
	}

	/**
	 * Check if data (from input files) are valid 
	 * 
	 * @return boolean
	 */
	public void isValidInputFiles(){

		ArrayList<MappingReconcilePreparation> listMappingReconcileFiles = this.initialisation.getListMappingReconcileFiles();
		
		for(int i = 0; i < listMappingReconcileFiles.size(); i++){

			MappingReconcilePreparation mappingReconcilePrep = listMappingReconcileFiles.get(i);
			//ArrayList<String> listColumnsToDelete = new ArrayList<>();

			MappingDwC mappingFile = mappingReconcilePrep.getMappingDWC();
			ReconciliationService reconciliationService = mappingReconcilePrep.getReconcileDWC();
			CSVFile csvFileNoMapped = mappingFile.getNoMappedFile();
			
			if(csvFileNoMapped.getSeparator().equals("-1")){
				mappingFile.setSuccessMapping(Boolean.toString(false));
				reconciliationService.setSuccessReconcile(Boolean.toString(false));
			}
			else{
				if(!Boolean.parseBoolean(mappingReconcilePrep.getMappingDWC().getMappingInvolved())){
					mappingFile.setSuccessMapping(Boolean.toString(true));
					String [] listTagsInput = csvFileNoMapped.getLines().get(0).split(csvFileNoMapped.getSeparator());
					ArrayList<String> tagsDwcOfficial = mappingFile.getTagsListDwC();

					for(int j = 0; j < listTagsInput.length; j++){

						String tagInput = listTagsInput[j];

						if(!tagsDwcOfficial.contains(tagInput)){
							mappingFile.setSuccessMapping(Boolean.toString(false));
						}				
					}
				}
				else{
					mappingFile.setSuccessMapping(Boolean.toString(true));
				}
			}
			
		}

	}

	/**
	 * Check if raster files are valid
	 * 
	 * @return boolean
	 */
	public boolean isValidRasterFiles(){
		//System.out.println("size raster : " + this.initialisation.getInputRastersList().size());
		//System.out.println("size header : " + this.initialisation.getHeaderRasterList().size());
		boolean isValid = true;
		if(this.initialisation.getInputRastersList().size() == this.initialisation.getHeaderRasterList().size()){
			if(this.initialisation.getInputRastersList().size() == 0){
				System.err.println("You have to put a raster file (format : bil, ...) if you desire to match your point and cells data.");
				isValid = false;
			}
		}
		else{
			System.err.println("You have to put a raster file AND its header file (format : hdr).");
			isValid = false;
		}

		for(int i = 0 ; i < this.initialisation.getInputRastersList().size() ; i++){
			File raster = this.initialisation.getInputRastersList().get(i);
			String extensionRaster = raster.getName().substring(raster.getName().lastIndexOf("."));
			String [] extensionsRaster = {".bil", ".grd", ".asc", ".sdat", ".rsc", ".nc", ".cdf", ".bsq", ".bip", ".adf"};
			ArrayList<String> extensionsRasterList = new ArrayList(Arrays.asList(extensionsRaster));

			if(!extensionsRasterList.contains(extensionRaster)){
				isValid = false;
			}

		}
		for(int i = 0 ; i < this.initialisation.getHeaderRasterList().size() ; i++){
			File header = this.initialisation.getHeaderRasterList().get(i);
			String extensionHeader = header.getName().substring(header.getName().lastIndexOf("."));
			String headerName = header.getName();

			/*if(!headerName.contains("hdr")){
		System.out.println("false 2 " + headerName);
		isValid = false;
	    }
	    else if(!extensionHeader.equals(".hdr")){
		System.out.println("false 3");
		isValid = false;
	    }*/



		}

		//System.out.println("raster valid : " + isValid);
		return isValid;
	}

	/**
	 * Check if synonym file is valid
	 * 
	 * @return boolean
	 */
	public boolean isValidSynonymFile(){
		//System.out.println("size synonym : " + this.initialisation.getInputSynonymsList());
		if(this.initialisation.getInputSynonymsList().size() != 0){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Launch synonym option
	 * 
	 * @param isValidSynonymFile
	 * @return void
	 */
	public void launchSynonymOption(boolean isValidSynonymFile){
		if(isValidSynonymFile){
			this.dataTreatment.includeSynonyms(this.initialisation.getInputSynonymsList().get(0));
		}
		else{
			this.dataTreatment.includeSynonyms(null);
		}

		step5.setNbFound(this.dataTreatment.getNbSynonymInvolved());
	}

	/**
	 * Launch raster option
	 * 
	 * @return void
	 */
	public void launchRasterOption(){

		RasterTreatment rasterTreatment = this.dataTreatment.checkWorldClimCell(this.initialisation.getInputRastersList());
		finalisation.setMatrixFileValidCells(rasterTreatment.getMatrixFileValidCells());
		finalisation.setPathMatrixFile(rasterTreatment.getMatrixFileValidCells().getAbsolutePath().replace(initialisation.getDIRECTORY_PATH(), ""));
		step8.setPathWrongRaster(rasterTreatment.getWrongRasterFile().getAbsolutePath().replace(initialisation.getDIRECTORY_PATH(), ""));
		step8.setPathMatrixResultRaster(rasterTreatment.getMatrixFileValidCells().getAbsolutePath().replace(initialisation.getDIRECTORY_PATH(), ""));
		System.out.println("pathWrongRaster : " + step8.getPathWrongRaster());
		System.out.println("pathmatrixresult : " + step8.getPathMatrixResultRaster());
		step8.setNbFound(rasterTreatment.getNbWrongOccurrences());
	}//wrong_raster

	/**
	 * Launch establishmentMeans option
	 * 
	 * @return void
	 */
	public void launchEstablishmentMeansOption(){
		if(this.initialisation.getEstablishmentList().size() != 0){
			EstablishmentTreatment establishTreatment = this.dataTreatment.establishmentMeansOption(this.initialisation.getEstablishmentList());
			ArrayList<String> noEstablishment = establishTreatment.getNoEstablishmentList();
			step9.setNbFound(noEstablishment.size());
			File wrongEstablishmentMeans = establishTreatment.getWrongEstablishmentMeansFile();
			finalisation.setWrongEstablishmentMeans(wrongEstablishmentMeans);
			finalisation.setPathWrongEstablishmentMeans(wrongEstablishmentMeans.getAbsolutePath().replace(initialisation.getDIRECTORY_PATH(), ""));
			step9.setStep9_ok(true);
			step9.setPathWrongEstablishmentMeans(wrongEstablishmentMeans.getAbsolutePath().replace(initialisation.getDIRECTORY_PATH(), ""));
		}
		/*else{
	    step9.setStep9_ok(false);
	}*/
	}


	public void writeFinalOutput(){
		ArrayList<File> listFinalOutput = new ArrayList<>();
		ArrayList<String> listPathsOutput = new ArrayList<>();

		if(!new File(initialisation.getDIRECTORY_PATH() + "temp/" + initialisation.getNbSessionRandom() + "/final_results/").exists()){
			new File(initialisation.getDIRECTORY_PATH() + "temp/" + initialisation.getNbSessionRandom() + "/final_results/").mkdir();
		}

		int nbFiles = this.initialisation.getNbFiles();
		for(int i = 0 ; i < nbFiles ; i++){
			int idFile = this.initialisation.getListMappingReconcileFiles().get(i).getIdFile();
			String originalName = this.initialisation.getListMappingReconcileFiles().get(i).getOriginalName();
			String originalExtension = this.initialisation.getListMappingReconcileFiles().get(i).getOriginalExtension();

			ConnectionDatabase newConnection = new ConnectionDatabase();
			ArrayList<String > resultCleanTable = newConnection.getCleanTableFromIdFile(idFile, initialisation.getNbSessionRandom());
			String nameFile = originalName.replace("." + originalExtension, "") + "_" + initialisation.getNbSessionRandom() + "_clean.csv";
			File cleanOutput = this.dataTreatment.createFileCsv(resultCleanTable, nameFile, "final_results");

			listFinalOutput.add(cleanOutput);
			String pathFile = cleanOutput.getAbsolutePath().replace(initialisation.getDIRECTORY_PATH(),"");
			listPathsOutput.add(pathFile);
		}

		finalisation.setListPathsOutputFiles(listPathsOutput);
		finalisation.setFinalOutputFiles(listFinalOutput);
	}

	/**
	 *  
	 * @return TreatmentData
	 */
	public Treatment getDataTreatment() {
		return dataTreatment;
	}

	/**
	 * 
	 * @param dataTreatment
	 * @return void
	 */
	public void setDataTreatment(Treatment dataTreatment) {
		this.dataTreatment = dataTreatment;
	}

	public Initialise getInitialisation() {
		return initialisation;
	}

	public void setInitialisation(Initialise initialisation) {
		this.initialisation = initialisation;
	}

	public Finalisation getFinalisation() {
		return finalisation;
	}

	public void setFinalisation(Finalisation finalisation) {
		this.finalisation = finalisation;
	}

	public Step1_MappingDwc getStep1() {
		return step1;
	}

	public void setStep1(Step1_MappingDwc step1) {
		this.step1 = step1;
	}

	public Step2_ReconciliationService getStep2() {
		return step2;
	}

	public void setStep2(Step2_ReconciliationService step2) {
		this.step2 = step2;
	}

	public Step3_CheckCoordinates getStep3() {
		return step3;
	}

	public void setStep3(Step3_CheckCoordinates step3) {
		this.step3 = step3;
	}

	public Step4_CheckGeoIssue getStep4() {
		return step4;
	}

	public void setStep4(Step4_CheckGeoIssue step4) {
		this.step4 = step4;
	}

	public Step5_IncludeSynonym getStep5() {
		return step5;
	}

	public void setStep5(Step5_IncludeSynonym step5) {
		this.step5 = step5;
	}

	public Step6_CheckTDWG getStep6() {
		return step6;
	}

	public void setStep6(Step6_CheckTDWG step6) {
		this.step6 = step6;
	}

	public Step7_CheckISo2Coordinates getStep7() {
		return step7;
	}

	public void setStep7(Step7_CheckISo2Coordinates step7) {
		this.step7 = step7;
	}

	public Step8_CheckCoordinatesRaster getStep8() {
		return step8;
	}

	public void setStep8(Step8_CheckCoordinatesRaster step8) {
		this.step8 = step8;
	}

	public Step9_EstablishmentMeans getStep9() {
		return step9;
	}

	public void setStep9(Step9_EstablishmentMeans step9) {
		this.step9 = step9;
	}

}

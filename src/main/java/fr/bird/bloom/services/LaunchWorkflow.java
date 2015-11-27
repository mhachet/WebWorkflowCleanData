/**
 * src.model
 * LaunchWorkflow
 * TODO
 */
package fr.bird.bloom.services;

import fr.bird.bloom.beans.Finalisation;
import fr.bird.bloom.beans.InputParameters;
import fr.bird.bloom.model.CSVFile;
import fr.bird.bloom.model.ConnectionDatabase;
import fr.bird.bloom.model.DarwinCore;
import fr.bird.bloom.model.DatabaseTreatment;
import fr.bird.bloom.model.EstablishmentTreatment;
import fr.bird.bloom.model.GeographicTreatment;
import fr.bird.bloom.model.MappingDwC;
import fr.bird.bloom.model.MappingReconcilePreparation;
import fr.bird.bloom.model.RasterTreatment;
import fr.bird.bloom.model.ReconciliationService;
import fr.bird.bloom.model.SendMail;
import fr.bird.bloom.model.Treatment;
import fr.bird.bloom.stepresults.Step1_MappingDwc;
import fr.bird.bloom.stepresults.Step2_ReconciliationService;
import fr.bird.bloom.stepresults.Step3_CheckCoordinates;
import fr.bird.bloom.stepresults.Step4_CheckGeoIssue;
import fr.bird.bloom.stepresults.Step5_IncludeSynonym;
import fr.bird.bloom.stepresults.Step6_CheckTDWG;
import fr.bird.bloom.stepresults.Step7_CheckISo2Coordinates;
import fr.bird.bloom.stepresults.Step8_CheckCoordinatesRaster;
import fr.bird.bloom.stepresults.Step9_EstablishmentMeans;
import fr.bird.bloom.utils.BloomConfig;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Map.Entry;

/**
 * src.model
 *
 * LaunchWorkflow.java
 */
public class LaunchWorkflow {

	private Treatment dataTreatment;
	private InputParameters inputParameters;
	private Finalisation finalisation;

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
	public LaunchWorkflow(InputParameters inputParameters){
		this.inputParameters = inputParameters;
	}


	/**
	 * Call steps of the workflow
	 *
	 * @throws IOException
	 * @return void
	 */
	public void executeWorkflow() throws IOException{
		//System.out.println("repCourant : "  + repCourant);
		this.dataTreatment = new Treatment();
		this.dataTreatment.setUuid(inputParameters.getUuid());

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

		Map<Integer, Boolean> validInputFiles = this.isValidInputFiles();
		if(validInputFiles.containsValue(true)) {

			this.launchWorkflow();
			step3.setInvolved(true);
			step4.setInvolved(true);
			step7.setInvolved(true);

			if (this.inputParameters.isSynonym()) {

				boolean synonymFileIsValid = this.isValidSynonymFile();
				this.launchSynonymOption(synonymFileIsValid);
				step5.setInvolved(true);
			}

			if (this.inputParameters.isTdwg4Code()) {
				boolean sucessTdwgTreatment = dataTreatment.tdwgCodeOption();
				step6.setStep6_ok(sucessTdwgTreatment);
				step6.setInvolved(true);
			}

			if (this.inputParameters.isRaster()) {
				step8.setInvolved(true);
				boolean rasterFilesIsValid = this.isValidRasterFiles();
				if (rasterFilesIsValid) {
					this.launchRasterOption();
				} else {
					final String supportFiles = BloomConfig.getResourcePath();
					File defaultRaster = new File(supportFiles + "tmean1.bil");
					this.inputParameters.getInputRastersList().add(defaultRaster);
					File defaultHeader = new File(supportFiles + "tmean1.hdr");
					this.inputParameters.getHeaderRasterList().add(defaultHeader);
					this.launchRasterOption();
					//step8.setStep8_ok(false);
				}

			}

			//System.out.println("establishment : " + this.inputParameters.getEstablishmentList());accessRight
			//keep introduced data
			if (this.inputParameters.isEstablishment()) {
				this.launchEstablishmentMeansOption();
				step9.setInvolved(true);
			}

			this.writeFinalOutput();

			if (inputParameters.isSendEmail()) {
				System.out.println("email option : " + inputParameters.isSendEmail());
				SendMail mail = new SendMail();
				try {
					mail.setStep1(step1);
					mail.setStep2(step2);
					mail.setStep3(step3);
					mail.setStep4(step4);
					mail.setStep5(step5);
					mail.setStep6(step6);
					mail.setStep7(step7);
					mail.setStep8(step8);
					mail.setStep9(step9);
					mail.setFinalisation(finalisation);
					mail.sendMessage(inputParameters.getEmailUser());
				} catch (MessagingException e) {
					e.printStackTrace();
				}

			}

			this.dataTreatment.deleteTables();
		}
		else{

			//if(!step1.isInvolved()){
			List<MappingReconcilePreparation> listMappingReconcileFiles =  inputParameters.getListMappingReconcileFiles();
			Map<Integer, MappingDwC> infos_mapping = step1.getInfos_mapping();
			inputParameters.setNbInputs(listMappingReconcileFiles.size());
			for(int i = 0 ; i < listMappingReconcileFiles.size() ; i ++){
				int idFile = listMappingReconcileFiles.get(i).getIdFile();
				MappingDwC mappingDwC = listMappingReconcileFiles.get(i).getMappingDWC();
				mappingDwC.setFilename(listMappingReconcileFiles.get(i).getOriginalName());
				infos_mapping.put(idFile, mappingDwC);
			}

			step1.setNbInputs(listMappingReconcileFiles.size());
			step3.setStep3_ok(false);
			step4.setStep4_ok(false);
			step7.setStep7_ok(false);

			if(step5.isInvolved()){
				step5.setStep5_ok(false);
			}

			if(step6.isInvolved()){
				step6.setStep6_ok(false);
			}

			if(step8.isInvolved()){
				step8.setStep8_ok(false);
			}

			if(step9.isInvolved()){
				step9.setStep9_ok(false);
			}
		}

	}


	/**
	 * Call main steps of the workflow
	 *
	 * @throws IOException
	 * @return void
	 */
	private void launchWorkflow() throws IOException{

		List<MappingReconcilePreparation> listMappingReconcileDWC = this.inputParameters.getListMappingReconcileFiles();
		Map<Integer, ReconciliationService> reconcilePath = step2.getInfos_reconcile();
		Map<Integer, MappingDwC> hashMapStep1 = step1.getInfos_mapping();
		step1.setNbInputs(listMappingReconcileDWC.size());

		/**
		 * pre-treatment for mapping and reconcile
		 */
		for(int i = 0 ; i < listMappingReconcileDWC.size() ; i++){

			MappingDwC mappingDwc = listMappingReconcileDWC.get(i).getMappingDWC();
			MappingReconcilePreparation preparation = listMappingReconcileDWC.get(i);

			int idFile = listMappingReconcileDWC.get(i).getIdFile();
			mappingDwc.setIdFile(idFile);

			String originalName = listMappingReconcileDWC.get(i).getOriginalName();
			mappingDwc.setFilename(originalName);

			boolean isMapping = mappingDwc.getMappingInvolved();

			boolean isValid = preparation.isValid();

			if(isMapping && isValid){
				step1.setInvolved(isMapping);

				this.dataTreatment.mappingDwC(mappingDwc, idFile);
				String pathMappedFile = mappingDwc.getMappedFile().getAbsolutePath().replace(BloomConfig.getDirectoryPath(),"output/"); //change to 'output/'

				mappingDwc.setFilepath(pathMappedFile);
			}
			hashMapStep1.put(idFile, mappingDwc);

			ReconciliationService reconcileService = listMappingReconcileDWC.get(i).getReconcileDWC();
			boolean reconcile = reconcileService.isReconcile();
			if(reconcile && isValid){
				step2.setInvolved(reconcile);
				if(isMapping){
					//System.out.println("new CSVFile isMapping true line 203 LaunchWorkflow");
					CSVFile csvMappedFile = new CSVFile(mappingDwc.getMappedFile());
					csvMappedFile.setSeparator(mappingDwc.getNoMappedFile().getSeparator());
					this.dataTreatment.reconcileService(reconcileService, csvMappedFile, idFile);
				}
				else{
					//System.out.println("new CSVFile isMapping false line 208 LaunchWorkflow");
					this.dataTreatment.reconcileService(reconcileService, mappingDwc.getNoMappedFile(), idFile);
				}
				String pathReconcileFile = reconcileService.getReconcileFile().getAbsolutePath().replace(BloomConfig.getDirectoryPath(),"output/"); //change to 'output/'
				reconcileService.setFilepath(pathReconcileFile);


			}

			reconcilePath.put(idFile, reconcileService);
		}
		//System.out.println("new CSVFile launchWorkflow line 205 LaunchWorkflow");

		/**
		 * pre-treatment for any input (mapping or not, reconcile or not)
		 */
		for(int i = 0; i < this.inputParameters.getListMappingReconcileFiles().size() ; i++){
			MappingReconcilePreparation mappingReconcilePrep = this.inputParameters.getListMappingReconcileFiles().get(i);
			int idFile = mappingReconcilePrep.getIdFile();
			DarwinCore darwinCoreModified = null;
			MappingDwC mappingFile = mappingReconcilePrep.getMappingDWC();
			ReconciliationService reconcileFile = mappingReconcilePrep.getReconcileDWC();
			boolean isValid = mappingReconcilePrep.isValid();
			String separator = mappingFile.getNoMappedFile().getSeparator().getSymbol();
			if(isValid){
				if(reconcileFile.isReconcile()){
					darwinCoreModified = this.dataTreatment.initialiseFile(reconcileFile.getReconcileFile(), idFile, separator);

				}
				else if(mappingFile.getMappingInvolved()){

					darwinCoreModified = this.dataTreatment.initialiseFile(mappingFile.getMappedFile(), idFile, separator);

				}
				else{
					darwinCoreModified = this.dataTreatment.initialiseFile(mappingFile.getNoMappedFile().getCsvFile(), idFile, separator);

				}
				//File inputFileModified = this.dataTreatment.createTemporaryFile(linesInputFile, idFile);
				//String sqlInsert = this.dataTreatment.createSQLInsert(inputFileModified);
				//System.out.println("new CSVFile launchWorkflow line 231 LaunchWorkflow");
				this.dataTreatment.createTableDarwinCoreInput(darwinCoreModified);
				//this.dataTreatment.createTableDarwinCoreInput(sqlInsert);
			}

		}

		GeographicTreatment geoTreatment = this.dataTreatment.checkGeographicOption();

		File wrongCoordinatesFile = geoTreatment.getWrongCoordinatesFile();
		finalisation.setWrongCoordinatesFile(wrongCoordinatesFile);
		final String pathWrongCoordinatesFile = wrongCoordinatesFile.getAbsolutePath().replace(BloomConfig.getDirectoryPath(), "output/");
		finalisation.setPathWrongCoordinatesFile(pathWrongCoordinatesFile); //change to 'output/'
		step3.setNbFound(geoTreatment.getNbWrongCoordinates());
		step3.setPathWrongCoordinates(finalisation.getPathWrongCoordinatesFile());

		File wrongGeospatial = geoTreatment.getWrongGeoFile();
		finalisation.setWrongGeospatial(wrongGeospatial);
		finalisation.setPathWrongGeospatial(wrongGeospatial.getAbsolutePath().replace(BloomConfig.getDirectoryPath(), "output/")); //change to 'output/'
		step4.setNbFound(geoTreatment.getNbWrongGeospatialIssues());
		step4.setPathWrongGeoIssue(finalisation.getPathWrongGeospatial());

		File wrongPolygon = geoTreatment.getWrongPolygonFile();
		finalisation.setWrongPolygon(wrongPolygon);
		finalisation.setPathWrongPolygon(wrongPolygon.getAbsolutePath().replace(BloomConfig.getDirectoryPath(), "output/")); //change to 'output/'
		step7.setNbFound(geoTreatment.getNbWrongIso2());
		step7.setPathWrongIso2(finalisation.getPathWrongPolygon());
	}

	/**
	 * Check if data (from input files) are valid
	 *
	 * @return boolean
	 */
	private Map <Integer, Boolean> isValidInputFiles(){
		Map <Integer, Boolean> validInputFiles = new HashMap<>();

		List<MappingReconcilePreparation> listMappingReconcileFiles = this.inputParameters.getListMappingReconcileFiles();

		for(int i = 0; i < listMappingReconcileFiles.size(); i++){

			MappingReconcilePreparation mappingReconcilePrep = listMappingReconcileFiles.get(i);
			//List<String> listColumnsToDelete = new ArrayList<>();

			MappingDwC mappingFile = mappingReconcilePrep.getMappingDWC();
			ReconciliationService reconciliationService = mappingReconcilePrep.getReconcileDWC();
			CSVFile csvFileNoMapped = mappingFile.getNoMappedFile();

			if(csvFileNoMapped.getSeparator() == CSVFile.Separator.INCONSISTENT || csvFileNoMapped.getSeparator() == CSVFile.Separator.UNKNOWN){
				//System.out.println("separator false : " + csvFileNoMapped.getSeparator());
				mappingFile.setSuccessMapping(Boolean.toString(false));
				reconciliationService.setSuccessReconcile(Boolean.toString(false));
				validInputFiles.put(mappingReconcilePrep.getIdFile(), false);
				System.out.println(mappingReconcilePrep.getIdFile() + " => false");
			}
			else{
				//System.out.println("separator true : " + csvFileNoMapped.getSeparator());
				if(!mappingReconcilePrep.getMappingDWC().getMappingInvolved()){
					mappingFile.setSuccessMapping(Boolean.toString(true));
					String [] listTagsInput = csvFileNoMapped.getFirstLine().split(csvFileNoMapped.getSeparator().getSymbol());
					List<String> tagsDwcOfficial = mappingFile.getTagsListDwC();
					boolean validFile = true;

					for(int j = 0; j < listTagsInput.length; j++){
						String tagInput = listTagsInput[j];

						if(!tagsDwcOfficial.contains(tagInput)){
							mappingFile.setSuccessMapping(Boolean.toString(false));
							validFile = false;
						}
					}
					validInputFiles.put(mappingReconcilePrep.getIdFile(), validFile);
					System.out.println(mappingReconcilePrep.getIdFile() + " => " + validFile);
				}
				else{
					//System.out.println("separator true true : " + csvFileNoMapped.getSeparator());
					mappingFile.setSuccessMapping(Boolean.toString(true));
					validInputFiles.put(mappingReconcilePrep.getIdFile(), true);
					System.out.println(mappingReconcilePrep.getIdFile() + " => true");
				}
			}

		}


		return validInputFiles;

	}

	/**
	 * Check if raster files are valid
	 *
	 * @return boolean
	 */
	private boolean isValidRasterFiles(){
		//System.out.println("size raster : " + this.inputParameters.getInputRastersList().size());
		//System.out.println("size header : " + this.inputParameters.getHeaderRasterList().size());
		boolean isValid = true;
		if(this.inputParameters.getInputRastersList().size() == this.inputParameters.getHeaderRasterList().size()){
			if(this.inputParameters.getInputRastersList().size() == 0){
				System.err.println("You have to put a raster file (format : bil, ...) if you desire to match your point and cells data.");
				isValid = false;
			}
		}
		else{
			System.err.println("You have to put a raster file AND its header file (format : hdr).");
			isValid = false;
		}

		for(int i = 0; i < this.inputParameters.getInputRastersList().size() ; i++){
			File raster = this.inputParameters.getInputRastersList().get(i);
			String extensionRaster = raster.getName().substring(raster.getName().lastIndexOf("."));
			String [] extensionsRaster = {".bil", ".grd", ".asc", ".sdat", ".rsc", ".nc", ".cdf", ".bsq", ".bip", ".adf"};
			List<String> extensionsRasterList = new ArrayList(Arrays.asList(extensionsRaster));

			if(!extensionsRasterList.contains(extensionRaster)){
				isValid = false;
			}

		}
		for(int i = 0; i < this.inputParameters.getHeaderRasterList().size() ; i++){
			File header = this.inputParameters.getHeaderRasterList().get(i);
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
	private boolean isValidSynonymFile(){
		//System.out.println("size synonym : " + this.inputParameters.getInputSynonymsList());
		if(this.inputParameters.getInputSynonymsList().size() != 0){
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
	private void launchSynonymOption(boolean isValidSynonymFile){
		if(isValidSynonymFile){
			this.dataTreatment.includeSynonyms(this.inputParameters.getInputSynonymsList().get(0));
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
	private void launchRasterOption(){

		RasterTreatment rasterTreatment = this.dataTreatment.checkWorldClimCell(inputParameters.getInputRastersList());
		finalisation.setMatrixFileValidCells(rasterTreatment.getMatrixFileValidCells());
		finalisation.setPathMatrixFile(rasterTreatment.getMatrixFileValidCells().getAbsolutePath().replace(BloomConfig.getDirectoryPath(), "output/")); //change to 'output/'
		Map<String, Boolean> errorProcessRaster = rasterTreatment.getCheckProcess();
		step8.setProcessRaster(errorProcessRaster);
		for(Entry<String, Boolean> entry : errorProcessRaster.entrySet()) {
			String filenameRaster = entry.getKey();
		    boolean errorProcess = entry.getValue();
		    if(errorProcess){
		    	step8.setStep8_ok(false);
		    }
		    else{
		    	step8.setStep8_ok(true);
		    }
		}
		step8.setPathWrongRaster(rasterTreatment.getWrongRasterFile().getAbsolutePath().replace(BloomConfig.getDirectoryPath(), "output/")); //change to 'output/'
		step8.setPathMatrixResultRaster(rasterTreatment.getMatrixFileValidCells().getAbsolutePath().replace(BloomConfig.getDirectoryPath(), "output/")); //change to 'output/'
		//System.out.println("pathWrongRaster : " + step8.getPathWrongRaster());
		//System.out.println("pathmatrixresult : " + step8.getPathMatrixResultRaster());
		step8.setNbFound(rasterTreatment.getNbWrongOccurrences());
	}

	/**
	 * Launch establishmentMeans option
	 *
	 * @return void
	 */
	private void launchEstablishmentMeansOption(){
		if(this.inputParameters.getEstablishmentList().size() != 0){
			EstablishmentTreatment establishTreatment = this.dataTreatment.establishmentMeansOption(this.inputParameters.getEstablishmentList());
			List<String> noEstablishment = establishTreatment.getNoEstablishmentList();
			step9.setNbFound(noEstablishment.size());
			File wrongEstablishmentMeans = establishTreatment.getWrongEstablishmentMeansFile();
			finalisation.setWrongEstablishmentMeans(wrongEstablishmentMeans);
			finalisation.setPathWrongEstablishmentMeans(wrongEstablishmentMeans.getAbsolutePath().replace(BloomConfig.getDirectoryPath(), "output/")); //change to 'output/'
			step9.setStep9_ok(true);
			step9.setPathWrongEstablishmentMeans(wrongEstablishmentMeans.getAbsolutePath().replace(BloomConfig.getDirectoryPath(), "output/")); //change to 'output/'
		}
		/*else{
	    step9.setStep9_ok(false);
	}*/
	}

	/**
	 * Write clean file(s)
	 */
	private void writeFinalOutput(){
		List<File> listFinalOutput = new ArrayList<>();
		List<String> listPathsOutput = new ArrayList<>();

		if(!new File(BloomConfig.getDirectoryPath() + "temp/" + inputParameters.getUuid() + "/final_results/").exists()){
			new File(BloomConfig.getDirectoryPath() + "temp/" + inputParameters.getUuid() + "/final_results/").mkdir();
		}

		int nbFiles = this.inputParameters.getNbFiles();
		for(int i = 0 ; i < nbFiles ; i++){
			int idFile = this.inputParameters.getListMappingReconcileFiles().get(i).getIdFile();
			String originalName = this.inputParameters.getListMappingReconcileFiles().get(i).getOriginalName();
			String originalExtension = this.inputParameters.getListMappingReconcileFiles().get(i).getOriginalExtension();

			Statement statement = null;
			try {
				statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DatabaseTreatment newConnection = new DatabaseTreatment(statement);

			List<String > resultCleanTable = newConnection.getCleanTableFromIdFile(idFile, inputParameters.getUuid());
			String nameFile = originalName.replace("." + originalExtension, "") + "_" + inputParameters.getUuid() + "_clean.csv";
			File cleanOutput = this.dataTreatment.createFileCsv(resultCleanTable, nameFile, "final_results");

			listFinalOutput.add(cleanOutput);
			String pathFile = cleanOutput.getAbsolutePath().replace(BloomConfig.getDirectoryPath(),"output/"); //change to 'output/'
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

	public Finalisation getFinalisation() {
		return finalisation;
	}

	public Step1_MappingDwc getStep1() {
		return step1;
	}


	public Step2_ReconciliationService getStep2() {
		return step2;
	}


	public Step3_CheckCoordinates getStep3() {
		return step3;
	}


	public Step4_CheckGeoIssue getStep4() {
		return step4;
	}


	public Step5_IncludeSynonym getStep5() {
		return step5;
	}


	public Step6_CheckTDWG getStep6() {
		return step6;
	}


	public Step7_CheckISo2Coordinates getStep7() {
		return step7;
	}


	public Step8_CheckCoordinatesRaster getStep8() {
		return step8;
	}


	public Step9_EstablishmentMeans getStep9() {
		return step9;
	}


}

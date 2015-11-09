/**
 * 
 */
package fr.bird.bloom.model;

import fr.bird.bloom.utils.BloomConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * model
 * 
 * RasterTreatment.java
 * RasterTreatment
 */
public class RasterTreatment {

	private List<File> rasterFiles;
	private Map<Integer, HashMap<String, Boolean>> hashMapValidOrNot;
	private Treatment dataTreatment;
	private int nbWrongOccurrences;
	private File matrixFileValidCells;
	private File wrongRasterFile;
	private Map<String, Boolean> checkProcess;
	
	public RasterTreatment(List<File> rasterFiles, Treatment dataTreatment){
		this.rasterFiles = rasterFiles;
		this.dataTreatment = dataTreatment;
	}

	/**
	 * All treatment steps for raster analysis
	 * 
	 * @return File matrix with raster results
	 */
	public File treatmentRaster(){
		// retrieve all data (included or not) from Clean table
		List<String> listAllData = dataTreatment.getFileDarwinCore().getIDClean();
		//System.out.println("all Data : " + listAllData);

		// initialise raster file and hash map
		this.initialiseRasterFiles(rasterFiles, listAllData);

		// retrieve all data included in a cell
		List<Integer> listValidData = this.getValidData();
		//System.out.println("valid Data : " + listValidData);

		// create a matrix file : for each point and for each raster file,
		// indicate if point is included in a cell
		File matrixFileValidCells = this.writeMatrixReport();

		// retrieve all data not included in a cell
		List<Integer> listNotValidData = this.getIDdelete(listValidData, listAllData);
		//System.out.println("not valid : " + listNotValidData);

		// delete from Clean table, all data not included
		this.deleteWrongCellsFromClean(listNotValidData);



		this.setNbWrongOccurrences(listNotValidData.size());

		// remove temporary files bind to the raster analysis
		//dataTreatment.deleteDirectory(new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/"));
		//cells_proba_raster_

		return matrixFileValidCells;
	}
	
	/**
	 * 
	 * Check the number of raster file, necessary 1
	 * 
	 * @param ArrayList<File> raster file
	 * @param rasterFiles
	 *@param listAllID @return void
	 */
	public void initialiseRasterFiles(List<File> rasterFiles, List<String> listAllID){


		hashMapValidOrNot = new HashMap<Integer, HashMap<String,Boolean>>();

		for(int i = 1 ; i < listAllID.size() ; i++){
			int id = Integer.parseInt(listAllID.get(i).replace("\"",""));
			HashMap<String, Boolean> booleanRasterFiles = new HashMap<>();
			for(int j = 0 ; j < rasterFiles.size() ; j++){
				booleanRasterFiles.put(rasterFiles.get(j).getName(), false);
				hashMapValidOrNot.put(id, booleanRasterFiles);
			}
		}
		
		checkProcess = new HashMap<>();
	}

	/**
	 * Retrieve all "id_" included in a raster cell
	 * Use R script thanks to "raster" package 
	 * 
	 * @return ArrayList<Integer> list of all "id_" which are included in a raster cell
	 */
	public List<Integer> getValidData(){

		/************************************* FORMATS ************************************************
		File type 		Long name 					default extension 	Multiband support
		 *	raster 			’Native’ raster package format 			.grd 				Yes
		 *	ascii 			ESRI Ascii 					.asc 				No
		 *	SAGA GIS		System for Automated Geoscientific Analyses	.sdat 				No
		 *	IDRISI 			IDRISI 						.rst 				No
		 *	CDF 			netCDF (requires ncdf) 				.nc or .cdf			Yes
		 *	BIL 			Band Interleaved by Line (ESRIr BIL)		.bil 				Yes
		 *	BSQ			Band Sequential (BSQ) Image File		.bsq				NA
		 *	BIP			Band Interleaved by Pixel (ESRI BIP)		.bip				NA
		 ************************************************************************************************/
		String scriptRaster = BloomConfig.getResourcePath() + "raster.R";
		if(!new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/").exists())
		{
			new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/").mkdirs();
		}
		File dataInputFileRaster = new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/dataInputFileRaster.csv");
		//System.out.println("dataInputFileRaster : " + dataInputFileRaster.getAbsolutePath());
		List<Integer> listValidData = new ArrayList<>();
		List<Integer> idForOneRaster = new ArrayList<>();

		for(int i = 0 ; i < rasterFiles.size() ; i++){
			idForOneRaster = this.rasterScript(scriptRaster, rasterFiles.get(i), dataInputFileRaster);
			listValidData.addAll(idForOneRaster);

			String rasterFileName = rasterFiles.get(i).getName();
			Set<Integer> setHashMap = hashMapValidOrNot.keySet();
			Iterator<Integer> iteratorMap = setHashMap.iterator();
			while(iteratorMap.hasNext()){
				int id = (int)iteratorMap.next();
				HashMap<String, Boolean> booleanRasterFiles = hashMapValidOrNot.get(id);
				if(idForOneRaster.contains(id)){
					booleanRasterFiles.put(rasterFileName, true);
					hashMapValidOrNot.put(id, booleanRasterFiles);
				}
			}

		}
		//System.out.println(hashMapValidOrNot);

		return listValidData;
	}

	/**
	 * Call R script to found coordinates included in a raster cell  
	 * 
	 * @param String scriptRaster path of raster script
	 * @param File dataRasterFile raster file for the analysis
	 * @param File dataInputFileRaster input data 
	 * 
	 * @return ArrayList<Integer>
	 */
	public List<Integer> rasterScript(String scriptRaster, File dataRasterFile, File dataInputFileRaster){

		List<String> decimalLatitude = dataTreatment.getFileDarwinCore().getDecimalLatitudeClean();
		List<String> decimalLongitude = dataTreatment.getFileDarwinCore().getDecimalLongitudeClean();
		List<String> idLine = dataTreatment.getFileDarwinCore().getIDClean();

		if(!new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/").exists())
		{
			new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/").mkdirs();
		}
		File validRaster = new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/validRaster.txt");
		File errorRaster = new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/errorRaster.txt");
		try {

			FileWriter dataInputWriterTemp = new FileWriter(dataInputFileRaster, false);
			BufferedWriter dataWriter = new BufferedWriter(dataInputWriterTemp);

			for(int i = 0 ; i < decimalLatitude.size() ; i++){
				dataWriter.write(idLine.get(i) + "," + decimalLongitude.get(i) + "," + decimalLatitude.get(i) + "\n");
			}

			dataWriter.flush();
			dataWriter.close();

			FileOutputStream fos = new FileOutputStream(validRaster);
			FileOutputStream fosError = new FileOutputStream(errorRaster);
			Runtime rt = Runtime.getRuntime();
			String [] cmdarray = {"Rscript", scriptRaster, BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/", dataRasterFile.getAbsolutePath(), dataInputFileRaster.getAbsolutePath()};
			System.out.println("Rscript " +  scriptRaster + " " + BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/ " + dataRasterFile.getAbsolutePath() + " " + dataInputFileRaster.getAbsolutePath());
			Process proc = rt.exec(cmdarray);
			// any error message?
			// any streamGobble is a thread
			StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR", fosError);            
			
			// any output?
			// new thread
			StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT", fos);

			// kick them off
			errorGobbler.start();
			outputGobbler.start();

			// any error???
			int exitVal;
			exitVal = proc.waitFor();

		} catch (Throwable t){
			t.printStackTrace();
		}
		boolean errorProcess = this.errorDuringProcess(errorRaster);
		//System.out.println("error process : " + errorProcess);
		
		this.getCheckProcess().put(dataRasterFile.getName(), errorProcess);
		
		List<Integer> listValidData= null;
		
		if(!errorProcess){
			listValidData = this.getValidIDCells(validRaster);
		}
		else{
			listValidData = new ArrayList<>();
		}
		
		
		return listValidData;
	}

	/**
	 * Check if a error happened during R script process
	 * 
	 * @param errorRaster
	 * @return boolean
	 */
	public boolean errorDuringProcess(File errorRaster){
		String errorInfo = "aucun package nommé";
		String errorInfoBis = "Exécution arrêtée";
		boolean error = false;
		InputStream ips = null;
		try {
			ips = new FileInputStream(errorRaster);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		InputStreamReader ipsr = new InputStreamReader(ips);
		BufferedReader br = new BufferedReader(ipsr);
		String line;
		String [] arraySplit;
		try {
			while ((line = br.readLine()) != null){
				if(line.contains(errorInfo)){
					error = true;
				}
				else if(line.contains(errorInfoBis)){
					error = true;
				}
			}
			br.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return error;
		
	}
	
	/**
	 * 
	 * Retrieve all valid "id_" (included in a raster cell)
	 * 
	 * @param File temporary file with all valid "id_" (included in raster cell) and others informations contained in raster file
	 * @return ArrayList<Integer> list of valid "id_"
	 */
	public List<Integer> getValidIDCells(File validRaster){
		List<Integer> listValidData = new ArrayList<Integer>();
		InputStream ips = null;
		try {
			ips = new FileInputStream(validRaster);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		File outputRaster = new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/outputRaster.csv");
		InputStreamReader ipsr=new InputStreamReader(ips);
		BufferedReader br=new BufferedReader(ipsr);
		String ligne;
		String [] arraySplit;
		int count = 0;
		try {
			while ((ligne=br.readLine())!=null){
				//System.out.println(count + "   line : " + ligne);
				if(count > 0){
					arraySplit = ligne.split(" ");
					if(ligne.contains("<0 rows>")){
						//System.out.println("array : " + ligne);
					}
					else{
						/*for(int i=0; i < arraySplit.length; i++){
							System.out.println("array : " + arraySplit[i]);
						}*/
						
						listValidData.add(Integer.parseInt(arraySplit[1]));
					}
				}
				count++;


			}
			br.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return listValidData;
	}


	/**
	 * Write a matrix file with raster results
	 * 
	 * @return File
	 */
	public File writeMatrixReport(){
		if(!new File(BloomConfig.getDirectoryPath() + "temp/").exists()){
			new File(BloomConfig.getDirectoryPath() + "temp/").mkdirs();
		}
		if(!new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom()).exists()){
			new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom());
		}
		if(!new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/data/").exists()){
			new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/data/").mkdirs();
		}
		if(!new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/").exists()){
			new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/").mkdirs();
		}
		File matrix = new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/cells_proba_raster_" + dataTreatment.getNbSessionRandom() + ".csv");
		FileWriter writer = null;
		try {
			writer = new FileWriter(matrix);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String heading = "";
		boolean firstLine = true;
		//System.out.println("raster files size : " + rasterFiles.size());
		for (Entry<Integer, HashMap<String, Boolean>> entry : hashMapValidOrNot.entrySet()) {

			HashMap<String, Boolean> proba = entry.getValue();
			
			if(firstLine){
				int nbRasterFile = 1;
				for (Entry<String, Boolean> probaEntry : proba.entrySet()){
					
					String rasterName = probaEntry.getKey();
					//System.out.println("nbRasterFile : " + proba.size() + " " + rasterName);
					if(nbRasterFile == 1){
						heading += "abstract,acceptedNameUsage,acceptedNameUsageID,accessRights,accrualMethod,accrualPeriodicity,accrualPolicy,alternative,associatedMedia,associatedOccurrences,associatedOrganisms,associatedReferences,associatedSequences,associatedTaxa,audience,available,basisOfRecord,bed,behavior,bibliographicCitation,catalogNumber,class,classKey,collectionCode,collectionID,conformsTo,continent,contributor,coordinateAccuracy,coordinatePrecision,coordinateUncertaintyInMeters,country,countryCode,county,coverage,created,creator,dataGeneralizations,datasetID,datasetKey,datasetName,date,dateAccepted,dateCopyrighted,dateIdentified,dateSubmitted,day,decimalLatitude,decimalLongitude,depth,depthAccuracy,description,disposition,distanceAboveSurface,distanceAboveSurfaceAccuracy,dynamicProperties,earliestAgeOrLowestStage,earliestEonOrLowestEonothem,earliestEpochOrLowestSeries,earliestEraOrLowestErathem,earliestPeriodOrLowestSystem,educationLevel,elevation,elevationAccuracy,endDayOfYear,establishmentMeans,event,eventDate,eventID,eventRemarks,eventTime,extent,family,familyKey,fieldNotes,fieldNumber,footprintSpatialFit,footprintSRS,footprintWKT,format,formation,gbifID,genericName,genus,genusKey,geodeticDatum,geologicalContext,geologicalContextID,georeferencedBy,georeferencedDate,georeferenceProtocol,georeferenceRemarks,georeferenceSources,georeferenceVerificationStatus,group,habitat,hasCoordinate,hasFormat,hasGeospatialIssues,hasPart,hasVersion,higherClassification,higherGeography,higherGeographyID,highestBiostratigraphicZone,identification,identificationID,identificationQualifier,identificationReferences,identificationRemarks,identificationVerificationStatus,identifiedBy,identifier,idFile,individualCount,individualID,informationWithheld,infraspecificEpithet,institutionCode,institutionID,instructionalMethod,isFormatOf,island,islandGroup,isPartOf,isReferencedBy,isReplacedBy,isRequiredBy,issue,issued,isVersionOf,kingdom,kingdomKey,language,lastCrawled,lastInterpreted,lastParsed,latestAgeOrHighestStage,latestEonOrHighestEonothem,latestEpochOrHighestSeries,latestEraOrHighestErathem,latestPeriodOrHighestSystem,license,lifeStage,lithostratigraphicTerms,livingSpecimen,locality,locationAccordingTo,locationID,locationRemarks,lowestBiostratigraphicZone,machineObservation,materialSample,materialSampleID,maximumDepthinMeters,maximumDistanceAboveSurfaceInMeters,maximumElevationInMeters,measurementAccuracy,measurementDeterminedBy,measurementDeterminedDate,measurementID,measurementMethod,measurementOrFact,measurementRemarks,measurementType,measurementUnit,mediator,mediaType,medium,member,minimumDepthinMeters,minimumDistanceAboveSurfaceInMeters,minimumElevationInMeters,modified,month,municipality,nameAccordingTo,nameAccordingToID,namePublishedIn,namePublishedInID,namePublishedInYear,nomenclaturalCode,nomenclaturalStatus,occurrence,occurrenceDetails,occurrenceID,occurrenceRemarks,occurrenceStatus,order,orderKey,organism,organismID,organismName,organismRemarks,organismScope,originalNameUsage,originalNameUsageID,otherCatalogNumbers,ownerInstitutionCode,parentNameUsage,parentNameUsageID,phylum,phylumKey,pointRadiusSpatialFit,preparations,preservedSpecimen,previousIdentifications,protocol,provenance,publisher,publishingCountry,recordedBy,recordNumber,references,relatedResourceID,relationshipAccordingTo,relationshipEstablishedDate,relationshipRemarks,relation,replaces,reproductiveCondition,requires,resourceID,resourceRelationship,resourceRelationshipID,rights,rightsHolder,samplingEffort,samplingProtocol,scientificName,scientificNameAuthorship,scientificNameID,sex,source,spatial,species,speciesKey,specificEpithet,startDayOfYear,stateProvince,subgenus,subgenusKey,subject,tableOfContents,taxon,taxonConceptID,taxonID,taxonKey,taxonomicStatus,taxonRank,taxonRemarks,temporal,title,type,typeStatus,typifiedName,valid,verbatimCoordinates,verbatimCoordinateSystem,verbatimDate,verbatimDepth,verbatimElevation,verbatimEventDate,verbatimLatitude,verbatimLocality,verbatimLongitude,verbatimSRS,verbatimTaxonRank,vernacularName,waterBody,year," + rasterName + ",";
						//System.out.println("if 1 heading : " + heading);
					}
					else if(nbRasterFile < proba.size()){
						heading += rasterName + ",";
						//System.out.println("if 2 heading : " + heading);
					}
					if(proba.size() == 1){
						heading += "\n";
						//System.out.println("else heading : " + heading);
					}
					else if(nbRasterFile == proba.size()){
						heading += rasterName + "\n";
						//System.out.println("else heading : " + heading);
					}
					nbRasterFile ++;
				}
				firstLine = false;

			}
		}

		try {
			writer.write(heading);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Entry<Integer, HashMap<String, Boolean>> entry : hashMapValidOrNot.entrySet()) {
			int id = entry.getKey();			
			int nbRasterFile = 1;
			String line = "";
			HashMap<String, Boolean> probaBis = entry.getValue();
			String sqlSelectId = "SELECT abstract_,acceptedNameUsage_,acceptedNameUsageID_,accessRights_,accrualMethod_,accrualPeriodicity_,accrualPolicy_,alternative_,associatedMedia_,associatedOccurrences_,associatedOrganisms_,associatedReferences_,associatedSequences_,associatedTaxa_,audience_,available_,basisOfRecord_,bed_,behavior_,bibliographicCitation_,catalogNumber_,class_,classKey_,collectionCode_,collectionID_,conformsTo_,continent_,contributor_,coordinateAccuracy_,coordinatePrecision_,coordinateUncertaintyInMeters_,country_,countryCode_,county_,coverage_,created_,creator_,dataGeneralizations_,datasetID_,datasetKey_,datasetName_,date_,dateAccepted_,dateCopyrighted_,dateIdentified_,dateSubmitted_,day_,decimalLatitude_,decimalLongitude_,depth_,depthAccuracy_,description_,disposition_,distanceAboveSurface_,distanceAboveSurfaceAccuracy_,dynamicProperties_,earliestAgeOrLowestStage_,earliestEonOrLowestEonothem_,earliestEpochOrLowestSeries_,earliestEraOrLowestErathem_,earliestPeriodOrLowestSystem_,educationLevel_,elevation_,elevationAccuracy_,endDayOfYear_,establishmentMeans_,event_,eventDate_,eventID_,eventRemarks_,eventTime_,extent_,family_,familyKey_,fieldNotes_,fieldNumber_,footprintSpatialFit_,footprintSRS_,footprintWKT_,format_,formation_,gbifID_,genericName_,genus_,genusKey_,geodeticDatum_,geologicalContext_,geologicalContextID_,georeferencedBy_,georeferencedDate_,georeferenceProtocol_,georeferenceRemarks_,georeferenceSources_,georeferenceVerificationStatus_,group_,habitat_,hasCoordinate_,hasFormat_,hasGeospatialIssues_,hasPart_,hasVersion_,higherClassification_,higherGeography_,higherGeographyID_,highestBiostratigraphicZone_,identification_,identificationID_,identificationQualifier_,identificationReferences_,identificationRemarks_,identificationVerificationStatus_,identifiedBy_,identifier_,idFile_,individualCount_,individualID_,informationWithheld_,infraspecificEpithet_,institutionCode_,institutionID_,instructionalMethod_,isFormatOf_,island_,islandGroup_,isPartOf_,isReferencedBy_,isReplacedBy_,isRequiredBy_,issue_,issued_,isVersionOf_,kingdom_,kingdomKey_,language_,lastCrawled_,lastInterpreted_,lastParsed_,latestAgeOrHighestStage_,latestEonOrHighestEonothem_,latestEpochOrHighestSeries_,latestEraOrHighestErathem_,latestPeriodOrHighestSystem_,license_,lifeStage_,lithostratigraphicTerms_,livingSpecimen_,locality_,locationAccordingTo_,locationID_,locationRemarks_,lowestBiostratigraphicZone_,machineObservation_,materialSample_,materialSampleID_,maximumDepthinMeters_,maximumDistanceAboveSurfaceInMeters_,maximumElevationInMeters_,measurementAccuracy_,measurementDeterminedBy_,measurementDeterminedDate_,measurementID_,measurementMethod_,measurementOrFact_,measurementRemarks_,measurementType_,measurementUnit_,mediator_,mediaType_,medium_,member_,minimumDepthinMeters_,minimumDistanceAboveSurfaceInMeters_,minimumElevationInMeters_,modified_,month_,municipality_,nameAccordingTo_,nameAccordingToID_,namePublishedIn_,namePublishedInID_,namePublishedInYear_,nomenclaturalCode_,nomenclaturalStatus_,occurrence_,occurrenceDetails_,occurrenceID_,occurrenceRemarks_,occurrenceStatus_,order_,orderKey_,organism_,organismID_,organismName_,organismRemarks_,organismScope_,originalNameUsage_,originalNameUsageID_,otherCatalogNumbers_,ownerInstitutionCode_,parentNameUsage_,parentNameUsageID_,phylum_,phylumKey_,pointRadiusSpatialFit_,preparations_,preservedSpecimen_,previousIdentifications_,protocol_,provenance_,publisher_,publishingCountry_,recordedBy_,recordNumber_,references_,relatedResourceID_,relationshipAccordingTo_,relationshipEstablishedDate_,relationshipRemarks_,relation_,replaces_,reproductiveCondition_,requires_,resourceID_,resourceRelationship_,resourceRelationshipID_,rights_,rightsHolder_,samplingEffort_,samplingProtocol_,scientificName_,scientificNameAuthorship_,scientificNameID_,sex_,source_,spatial_,species_,speciesKey_,specificEpithet_,startDayOfYear_,stateProvince_,subgenus_,subgenusKey_,subject_,tableOfContents_,taxon_,taxonConceptID_,taxonID_,taxonKey_,taxonomicStatus_,taxonRank_,taxonRemarks_,temporal_,title_,type_,typeStatus_,typifiedName_,valid_,verbatimCoordinates_,verbatimCoordinateSystem_,verbatimDate_,verbatimDepth_,verbatimElevation_,verbatimEventDate_,verbatimLatitude_,verbatimLocality_,verbatimLongitude_,verbatimSRS_,verbatimTaxonRank_,vernacularName_,waterBody_,year_ FROM Workflow.Clean_" + this.getDataTreatment().getNbSessionRandom() + " WHERE id_=" + id + ";";
			Statement statement = null;
			try {
				statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DatabaseTreatment newConnectionSelect = new DatabaseTreatment(statement);
			newConnectionSelect.executeSQLcommand("executeQuery", sqlSelectId);
			List<String> resultatSelect = newConnectionSelect.getResultatSelect();

			if(resultatSelect != null){
				for(int i = 0; i < resultatSelect.size() ; i++){
					if(i != 0){
						line += resultatSelect.get(i).replace("_", "");
					}

				}
			}
			for (Entry<String, Boolean> probaBisEntry : probaBis.entrySet()){

				// TRUE : id valide dans le raster ou FALSE : id non valide dans le raster
				boolean value = probaBisEntry.getValue();
				//System.out.println(nbRasterFile + " / " + probaBisEntry.getKey());
				if(nbRasterFile == 1){
					line += "," + value + ",";
					//System.out.println("if 1 line : " + line);
				}
				else if(nbRasterFile < probaBis.size()){
					line += value + ",";
					//System.out.println("if 2 line : " + line);
				}
				if(probaBis.size() == 1){
					line += "\n";
					//System.out.println("else heading : " + heading);
				}
				else if(nbRasterFile == probaBis.size()){
					line += value + "\n";
					//System.out.println("else line : " + line);
				}
				nbRasterFile ++;
				//System.out.println("line : " + line);
			}
			try {
				//System.out.println("line : " + line);
				writer.write(line);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return matrix;
	}

	/**
	 * 
	 * From valid list and all data, retrieve not valid data
	 * Not valid data aren't included in a raster cell
	 * 
	 * @param ArrayList<Integer> list of valid data "id_"
	 * @param ArrayList<String> list of valid and not valid data "id_" 
	 * @return ArrayList<Integer> list of not valid data "id_" 
	 */
	public List<Integer> getIDdelete(List<Integer> validData, List<String> validAndNotData){
		List<Integer> dataToDelete = new ArrayList<Integer>();

		for(int i = 1 ; i < validAndNotData.size() ; i++){
			int id_ = Integer.parseInt(validAndNotData.get(i).replace("\"", ""));
			if(!(validData.contains(id_))){
				dataToDelete.add(id_);
			}
		}

		return dataToDelete;
	}

	/**
	 * 
	 * From "id_" not valid, delete it to Clean table
	 * 
	 * @param ArrayList<Integer> list of not valid data "id_"
	 * @return void
	 */
	public void deleteWrongCellsFromClean(List<Integer> notValidData){

		/*
		 * First, retrieve wrong data, not included in a raster cell.
		 */
		if(notValidData.size() > 0){


			String sqlIdDelete = "SELECT * FROM Workflow.Clean_" + this.getDataTreatment().getNbSessionRandom() + " WHERE (";
			for(int j = 0 ; j < notValidData.size() ; j++){
				int id_ = notValidData.get(j);
				String CleanTableId = "Clean_" + this.getDataTreatment().getNbSessionRandom() + ".id_=";
				if(j == 0){
					sqlIdDelete += CleanTableId + id_;
				}
				else{
					sqlIdDelete += " OR " + CleanTableId + id_;
				}
			}
			sqlIdDelete += ") AND UUID_=\"" + this.getDataTreatment().getNbSessionRandom() + "\";";



			// retrieve data aren't in cells in a csv file
			Statement statement = null;
			try {
				statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DatabaseTreatment newConnectionSelect = new DatabaseTreatment(statement);

			List<String> messagesSelect = new ArrayList<String>();
			messagesSelect.add("\n--- Select point aren't included in cells ---");
			messagesSelect.add(sqlIdDelete);
			messagesSelect.addAll(newConnectionSelect.executeSQLcommand("executeQuery", sqlIdDelete));
			List<String> resultatSelect = newConnectionSelect.getResultatSelect();

			if(resultatSelect != null){
				messagesSelect.add("nb lignes affectées :" + Integer.toString(resultatSelect.size() - 1));
				if(!new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/wrong/").exists())
				{
					new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/wrong/").mkdirs();
				}
				File wrongRasterFile = dataTreatment.createFileCsv(resultatSelect, "/wrong_raster_" + this.dataTreatment.getNbSessionRandom() + ".csv", "wrong");
				this.setWrongRasterFile(wrongRasterFile);
			}

			for(int j = 0 ; j < messagesSelect.size() ; j++){
				System.out.println(messagesSelect.get(j));
			}
		}
		else{
			if(!new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/wrong/").exists())
			{
				new File(BloomConfig.getDirectoryPath() + "temp/" + dataTreatment.getNbSessionRandom() + "/wrong/").mkdirs();
			}
			File wrongRasterFile = dataTreatment.createFileCsv(new ArrayList<String>(), "/wrong_raster_" + this.dataTreatment.getNbSessionRandom() + ".csv", "wrong");

			this.setWrongRasterFile(wrongRasterFile);
		}

		/* 
		 * Second part, delete data in Clean table aren't included in cells
		 */
		for(int i = 0 ; i < notValidData.size() ; i++){
			int id_ = notValidData.get(i);
			Statement statement = null;
			try {
				statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DatabaseTreatment newConnectionDelete = new DatabaseTreatment(statement);

			List<String> messagesDelete = new ArrayList<String>();
			messagesDelete.add("\n--- Delete points not in cells ---");

			String sqlDeleteCell = "DELETE FROM Clean_" + this.getDataTreatment().getNbSessionRandom() + " WHERE Clean_" + this.getDataTreatment().getNbSessionRandom() + ".id_=" + id_ + " AND UUID_=\"" + this.getDataTreatment().getNbSessionRandom() + "\";";

			messagesDelete.addAll(newConnectionDelete.executeSQLcommand("executeUpdate", sqlDeleteCell));
			List<String> resultatDelete = newConnectionDelete.getResultatSelect();
			if(resultatDelete != null){
				messagesDelete.add("nb lignes affectées :" + Integer.toString(resultatDelete.size() - 1));
			}



			for(int j = 0 ; j < messagesDelete.size() ; j++){
				System.out.println(messagesDelete.get(j));
			}
		}

	}

	/**
	 * 
	 * @return ArrayList<File>
	 */
	public List<File> getRasterFiles() {
		return rasterFiles;
	}

	/**
	 * 
	 * @param rasterFiles
	 */
	public void setRasterFiles(ArrayList<File> rasterFiles) {
		this.rasterFiles = rasterFiles;
	}

	/**
	 * 
	 * @return HashMap<Integer, HashMap<String, Boolean>>
	 */
	public Map<Integer, HashMap<String, Boolean>> getHashMapValidOrNot() {
		return hashMapValidOrNot;
	}

	/**
	 * 
	 * @param hashMapValidOrNot
	 */
	public void setHashMapValidOrNot(
			HashMap<Integer, HashMap<String, Boolean>> hashMapValidOrNot) {
		this.hashMapValidOrNot = hashMapValidOrNot;
	}

	/**
	 * 
	 * @return Treatment
	 */
	public Treatment getDataTreatment() {
		return dataTreatment;
	}

	/**
	 * 
	 * @param dataTreatment
	 */
	public void setDataTreatment(Treatment dataTreatment) {
		this.dataTreatment = dataTreatment;
	}


	/**
	 * 
	 * @return int
	 */
	public int getNbWrongOccurrences() {
		return nbWrongOccurrences;
	}

	/**
	 * 
	 * @param nbWrongOccurrences
	 */
	public void setNbWrongOccurrences(int nbWrongOccurrences) {
		this.nbWrongOccurrences = nbWrongOccurrences;
	}

	/**
	 * 
	 * @return File
	 */
	public File getMatrixFileValidCells() {
		return matrixFileValidCells;
	}

	/**
	 * 
	 * @param matrixFileValidCells
	 */
	public void setMatrixFileValidCells(File matrixFileValidCells) {
		this.matrixFileValidCells = matrixFileValidCells;
	}

	/**
	 * 
	 * @return File
	 */
	public File getWrongRasterFile() {
		return wrongRasterFile;
	}

	/**
	 * 
	 * @param wrongRasterFile
	 */
	public void setWrongRasterFile(File wrongRasterFile) {
		this.wrongRasterFile = wrongRasterFile;
	}

	/**
	 * 
	 * @return HashMap<String, Boolean>
	 */
	public Map<String, Boolean> getCheckProcess() {
		return checkProcess;
	}

	/**
	 * 
	 * @param checkProcess
	 */
	public void setCheckProcess(HashMap<String, Boolean> checkProcess) {
		this.checkProcess = checkProcess;
	}
	
}

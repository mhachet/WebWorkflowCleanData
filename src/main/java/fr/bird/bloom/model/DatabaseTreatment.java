package fr.bird.bloom.model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatabaseTreatment {

	private ArrayList<String> resultatSelect;
	private Statement statement;
	private boolean resultat;
	private ResultSet resultSet;
	private int i;
	
	public DatabaseTreatment(Statement statement){
		this.statement = statement;
	}
	/**
	 * Do a connection to the database
	 * @param String choiceStatement : execute, executeQuery or executeUpdate
	 * @param String sql : request
	 * @return ArrayList<String>
	 */
	public List<String> executeSQLcommand(String choiceStatement, String sql){

		//this.getRessourcesMysql();

		List<String > messages = new ArrayList<>();

		try {
			messages.add( "\nChargement du driver..." );
			Class.forName( "com.mysql.jdbc.Driver" );
			messages.add( "Driver chargé !" );
		} catch ( ClassNotFoundException e ) {
			messages.add( "Erreur lors du chargement : le driver n'a pas été trouvé dans le classpath ! <br/>"
					+ e.getMessage() );
		}

		try {
			messages.add("Connexion à la base de données ...");
			//connexion = DriverManager.getConnection( url, user, password );
			messages.add("Connexion réussie !");

			/* Create managing object of request */
			//statement = connexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			messages.add( "Objet requête créé !" );

			//global method for any SQL script - return a boolean : true if the instruction return ResultSet, false else
			if(Objects.equals(choiceStatement, "execute")){
				this.setResultat(statement.execute(sql));
				messages.add(sql);
				//messages.add(resultat.toString());
			}
			// SELECT - return ResultSet, with results : TDWG=
			else if(Objects.equals(choiceStatement, "executeQuery")){
				this.resultSet = statement.executeQuery(sql);
				messages.add(sql);
				ResultSetMetaData resultMeta = this.resultSet.getMetaData();

				setResultatSelect(resultMeta);
				this.resultSet.close();
			}
			/* writing or deleting on DB (for INSERT, UPDATE, DELETE, ...)
			 * give lines number edited by INSERT, UPDATE et DELETE
			 * or 0 for no return methods like CREATE
			 */
			else if(Objects.equals(choiceStatement, "executeUpdate")){
				i = statement.executeUpdate(sql);
				messages.add(sql);
				messages.add("nb lignes affectées => " + Integer.toString(i));
			}

			statement.close();

		} catch ( SQLException e ) {
			messages.add( "Connection error : " + e.getMessage() );
		}

		return messages;
	}
	
	/**
	 * Get lines of Clean table from idFile
	 * 
	 * @param idFile
	 * @param uuid
	 * @return ArrayList<String>
	 */
	public List<String> getCleanTableFromIdFile(int idFile, String uuid){
		this.executeSQLcommand("executeQuery", "SELECT abstract_,acceptedNameUsage_,acceptedNameUsageID_,accessRights_,accrualMethod_,accrualPeriodicity_,accrualPolicy_,alternative_,associatedMedia_,associatedOccurrences_,associatedOrganisms_,associatedReferences_,associatedSequences_,associatedTaxa_,audience_,available_,basisOfRecord_,bed_,behavior_,bibliographicCitation_,catalogNumber_,class_,classKey_,collectionCode_,collectionID_,conformsTo_,continent_,contributor_,coordinateAccuracy_,coordinatePrecision_,coordinateUncertaintyInMeters_,country_,countryCode_,county_,coverage_,created_,creator_,dataGeneralizations_,datasetID_,datasetKey_,datasetName_,date_,dateAccepted_,dateCopyrighted_,dateIdentified_,dateSubmitted_,day_,decimalLatitude_,decimalLongitude_,depth_,depthAccuracy_,description_,disposition_,distanceAboveSurface_,distanceAboveSurfaceAccuracy_,dynamicProperties_,earliestAgeOrLowestStage_,earliestEonOrLowestEonothem_,earliestEpochOrLowestSeries_,earliestEraOrLowestErathem_,earliestPeriodOrLowestSystem_,educationLevel_,elevation_,elevationAccuracy_,endDayOfYear_,establishmentMeans_,event_,eventDate_,eventID_,eventRemarks_,eventTime_,extent_,family_,familyKey_,fieldNotes_,fieldNumber_,footprintSpatialFit_,footprintSRS_,footprintWKT_,format_,formation_,gbifID_,genericName_,genus_,genusKey_,geodeticDatum_,geologicalContext_,geologicalContextID_,georeferencedBy_,georeferencedDate_,georeferenceProtocol_,georeferenceRemarks_,georeferenceSources_,georeferenceVerificationStatus_,group_,habitat_,hasCoordinate_,hasFormat_,hasGeospatialIssues_,hasPart_,hasVersion_,higherClassification_,higherGeography_,higherGeographyID_,highestBiostratigraphicZone_,identification_,identificationID_,identificationQualifier_,identificationReferences_,identificationRemarks_,identificationVerificationStatus_,identifiedBy_,identifier_,idFile_,individualCount_,individualID_,informationWithheld_,infraspecificEpithet_,institutionCode_,institutionID_,instructionalMethod_,isFormatOf_,island_,islandGroup_,isPartOf_,isReferencedBy_,isReplacedBy_,isRequiredBy_,issue_,issued_,isVersionOf_,kingdom_,kingdomKey_,language_,lastCrawled_,lastInterpreted_,lastParsed_,latestAgeOrHighestStage_,latestEonOrHighestEonothem_,latestEpochOrHighestSeries_,latestEraOrHighestErathem_,latestPeriodOrHighestSystem_,license_,lifeStage_,lithostratigraphicTerms_,livingSpecimen_,locality_,locationAccordingTo_,locationID_,locationRemarks_,lowestBiostratigraphicZone_,machineObservation_,materialSample_,materialSampleID_,maximumDepthinMeters_,maximumDistanceAboveSurfaceInMeters_,maximumElevationInMeters_,measurementAccuracy_,measurementDeterminedBy_,measurementDeterminedDate_,measurementID_,measurementMethod_,measurementOrFact_,measurementRemarks_,measurementType_,measurementUnit_,mediator_,mediaType_,medium_,member_,minimumDepthinMeters_,minimumDistanceAboveSurfaceInMeters_,minimumElevationInMeters_,modified_,month_,municipality_,nameAccordingTo_,nameAccordingToID_,namePublishedIn_,namePublishedInID_,namePublishedInYear_,nomenclaturalCode_,nomenclaturalStatus_,occurrence_,occurrenceDetails_,occurrenceID_,occurrenceRemarks_,occurrenceStatus_,order_,orderKey_,organism_,organismID_,organismName_,organismRemarks_,organismScope_,originalNameUsage_,originalNameUsageID_,otherCatalogNumbers_,ownerInstitutionCode_,parentNameUsage_,parentNameUsageID_,phylum_,phylumKey_,pointRadiusSpatialFit_,preparations_,preservedSpecimen_,previousIdentifications_,protocol_,provenance_,publisher_,publishingCountry_,recordedBy_,recordNumber_,references_,relatedResourceID_,relationshipAccordingTo_,relationshipEstablishedDate_,relationshipRemarks_,relation_,replaces_,reproductiveCondition_,requires_,resourceID_,resourceRelationship_,resourceRelationshipID_,rights_,rightsHolder_,samplingEffort_,samplingProtocol_,scientificName_,scientificNameAuthorship_,scientificNameID_,sex_,source_,spatial_,species_,speciesKey_,specificEpithet_,startDayOfYear_,stateProvince_,subgenus_,subgenusKey_,subject_,tableOfContents_,taxon_,taxonConceptID_,taxonID_,taxonKey_,taxonomicStatus_,taxonRank_,taxonRemarks_,temporal_,title_,type_,typeStatus_,typifiedName_,valid_,verbatimCoordinates_,verbatimCoordinateSystem_,verbatimDate_,verbatimDepth_,verbatimElevation_,verbatimEventDate_,verbatimLatitude_,verbatimLocality_,verbatimLongitude_,verbatimSRS_,verbatimTaxonRank_,vernacularName_,waterBody_,year_ FROM Workflow.Clean_" + uuid + " WHERE idFile_=" + idFile + " AND UUID_=\"" + uuid + "\";");

		List<String> resultatCleantableFromidFile = this.getResultatSelect();
		String lineClean = resultatCleantableFromidFile.get(0);
		lineClean = lineClean.replace("_", "");

		resultatCleantableFromidFile.remove(0);
		resultatCleantableFromidFile.add(0, lineClean);

		return resultatCleantableFromidFile;
	}


	/**
	 * Delete table of the database
	 * 
	 * @param String tableName
	 * @return ArrayList<String> messages list
	 */
	public List<String> deleteTable(String tableName, String UUIDcondition){
		List<String> messages = this.executeSQLcommand("executeUpdate", "DELETE FROM " + tableName + " WHERE UUID_=\"" + UUIDcondition +"\";");
		return messages;
	}

	/**
	 * Drop table of the database
	 * 
	 * @param String tableName
	 * @return ArrayList<String> messages list
	 */
	public List<String> dropTable(String tableName){
		List<String> messages = this.executeSQLcommand("executeUpdate", "DROP TABLE IF EXISTS " + tableName + ";");
		return messages;
	}

	/**
	 * Format request result
	 * 
	 * @param resultMeta
	 * @throws SQLException
	 * @return void
	 */
	public void setResultatSelect(ResultSetMetaData resultMeta) throws SQLException{
		resultatSelect = new ArrayList<>();

		String line = "";
		for(int i = 1; i <= resultMeta.getColumnCount(); i++){
			line += resultMeta.getColumnName(i) + ",";
		}
		line = line.substring(0, line.length()-1);
		resultatSelect.add(line);
		line = "";
		while(resultSet.next()){         
			for(int i = 1; i <= resultMeta.getColumnCount(); i++){

				try{
					line += "\"" + resultSet.getObject(i) + "\"" + ",";
				}
				catch (Exception e) {
					line += "NULL,";
				}

			}
			line = line.substring(0, line.length()-1);
			resultatSelect.add(line);
			line = "";

		}
	}
	
	/**
	 * Get all result come from request
	 * 
	 * @return  ArrayList<String>
	 */
	public List<String> getResultatSelect() {
		return resultatSelect;
	}
	
	
	/**
	 * 
	 * @return boolean
	 */
	public boolean isResultat() {
		return resultat;
	}

	/**
	 * 
	 * @param resultat
	 * @return void
	 */
	public void setResultat(boolean newResult) {
		resultat = newResult;
	}

	/**
	 * 
	 * @return ResultSet
	 */
	public ResultSet getResultSet() {
		return resultSet;
	}

	/**
	 * 
	 * @param resultSet
	 */
	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	/**
	 * 
	 * @param statement
	 */
	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	/**
	 * 
	 * @return Statement
	 */
	public Statement getStatement() {
		return statement;
	}
	
	



}



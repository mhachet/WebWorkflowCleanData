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
import java.util.logging.*;
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
	private int nbWrongOccurrences;

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
		this.setNbWrongOccurrences(noEstablishment.size() - 1);

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
				//System.out.println("inverse : "  + allEstablishmentMeans.get(i));
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
				System.out.println("others : " + this.getInverseEstablishmentList().get(i));
				Statement statement = null;
				try {
					statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DatabaseTreatment newConnectionOthers = new DatabaseTreatment(statement);
				List<String> messagesOthers = new ArrayList<>();

				String sqlOthers = "SELECT abstract_,acceptedNameUsage_,acceptedNameUsageID_,accessRights_,accrualMethod_,accrualPeriodicity_,accrualPolicy_,alternative_,associatedMedia_,associatedOccurrences_,associatedOrganisms_,associatedReferences_,associatedSequences_,associatedTaxa_,audience_,available_,basisOfRecord_,bed_,behavior_,bibliographicCitation_,catalogNumber_,class_,classKey_,collectionCode_,collectionID_,conformsTo_,continent_,contributor_,coordinateAccuracy_,coordinatePrecision_,coordinateUncertaintyInMeters_,country_,countryCode_,county_,coverage_,created_,creator_,dataGeneralizations_,datasetID_,datasetKey_,datasetName_,date_,dateAccepted_,dateCopyrighted_,dateIdentified_,dateSubmitted_,day_,decimalLatitude_,decimalLongitude_,depth_,depthAccuracy_,description_,disposition_,distanceAboveSurface_,distanceAboveSurfaceAccuracy_,dynamicProperties_,earliestAgeOrLowestStage_,earliestEonOrLowestEonothem_,earliestEpochOrLowestSeries_,earliestEraOrLowestErathem_,earliestPeriodOrLowestSystem_,educationLevel_,elevation_,elevationAccuracy_,endDayOfYear_,establishmentMeans_,event_,eventDate_,eventID_,eventRemarks_,eventTime_,extent_,family_,familyKey_,fieldNotes_,fieldNumber_,footprintSpatialFit_,footprintSRS_,footprintWKT_,format_,formation_,gbifID_,genericName_,genus_,genusKey_,geodeticDatum_,geologicalContext_,geologicalContextID_,georeferencedBy_,georeferencedDate_,georeferenceProtocol_,georeferenceRemarks_,georeferenceSources_,georeferenceVerificationStatus_,group_,habitat_,hasCoordinate_,hasFormat_,hasGeospatialIssues_,hasPart_,hasVersion_,higherClassification_,higherGeography_,higherGeographyID_,highestBiostratigraphicZone_,identification_,identificationID_,identificationQualifier_,identificationReferences_,identificationRemarks_,identificationVerificationStatus_,identifiedBy_,identifier_,idFile_,individualCount_,individualID_,informationWithheld_,infraspecificEpithet_,institutionCode_,institutionID_,instructionalMethod_,isFormatOf_,island_,islandGroup_,isPartOf_,isReferencedBy_,isReplacedBy_,isRequiredBy_,issue_,issued_,isVersionOf_,kingdom_,kingdomKey_,language_,lastCrawled_,lastInterpreted_,lastParsed_,latestAgeOrHighestStage_,latestEonOrHighestEonothem_,latestEpochOrHighestSeries_,latestEraOrHighestErathem_,latestPeriodOrHighestSystem_,license_,lifeStage_,lithostratigraphicTerms_,livingSpecimen_,locality_,locationAccordingTo_,locationID_,locationRemarks_,lowestBiostratigraphicZone_,machineObservation_,materialSample_,materialSampleID_,maximumDepthinMeters_,maximumDistanceAboveSurfaceInMeters_,maximumElevationInMeters_,measurementAccuracy_,measurementDeterminedBy_,measurementDeterminedDate_,measurementID_,measurementMethod_,measurementOrFact_,measurementRemarks_,measurementType_,measurementUnit_,mediator_,mediaType_,medium_,member_,minimumDepthinMeters_,minimumDistanceAboveSurfaceInMeters_,minimumElevationInMeters_,modified_,month_,municipality_,nameAccordingTo_,nameAccordingToID_,namePublishedIn_,namePublishedInID_,namePublishedInYear_,nomenclaturalCode_,nomenclaturalStatus_,occurrence_,occurrenceDetails_,occurrenceID_,occurrenceRemarks_,occurrenceStatus_,order_,orderKey_,organism_,organismID_,organismName_,organismRemarks_,organismScope_,originalNameUsage_,originalNameUsageID_,otherCatalogNumbers_,ownerInstitutionCode_,parentNameUsage_,parentNameUsageID_,phylum_,phylumKey_,pointRadiusSpatialFit_,preparations_,preservedSpecimen_,previousIdentifications_,protocol_,provenance_,publisher_,publishingCountry_,recordedBy_,recordNumber_,references_,relatedResourceID_,relationshipAccordingTo_,relationshipEstablishedDate_,relationshipRemarks_,relation_,replaces_,reproductiveCondition_,requires_,resourceID_,resourceRelationship_,resourceRelationshipID_,rights_,rightsHolder_,samplingEffort_,samplingProtocol_,scientificName_,scientificNameAuthorship_,scientificNameID_,sex_,source_,spatial_,species_,speciesKey_,specificEpithet_,startDayOfYear_,stateProvince_,subgenus_,subgenusKey_,subject_,tableOfContents_,taxon_,taxonConceptID_,taxonID_,taxonKey_,taxonomicStatus_,taxonRank_,taxonRemarks_,temporal_,title_,type_,typeStatus_,typifiedName_,valid_,verbatimCoordinates_,verbatimCoordinateSystem_,verbatimDate_,verbatimDepth_,verbatimElevation_,verbatimEventDate_,verbatimLatitude_,verbatimLocality_,verbatimLongitude_,verbatimSRS_,verbatimTaxonRank_,vernacularName_,waterBody_,year_ FROM Workflow.Clean_" + this.getUuid() + " " +
						"WHERE Clean_" + this.getUuid() + ".establishmentMeans_!=\"native\" && " +
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
							noEstablishment.add(othersResults.get(m));
						}
					}
				}

				for(int l = 0; l < messagesOthers.size() ; l++){
					System.out.println(messagesOthers.get(l));
				}

				Statement statementDeleteOthers = null;
				try {
					statementDeleteOthers = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DatabaseTreatment newConnectionDeleteOthers = new DatabaseTreatment(statementDeleteOthers);
				List<String> messagesDeleteOthers = new ArrayList<>();
				messagesDeleteOthers.add("\n--- establishment Means ---\n");
				String sqlDeleteEstablishment = "DELETE FROM Workflow.Clean_" + this.getUuid() +
						" WHERE Clean_" + this.getUuid() + ".establishmentMeans_!=\"native\" && " +
						"Clean_" + this.getUuid() + ".establishmentMeans_!=\"introduced\" && " +
						"Clean_" + this.getUuid() + ".establishmentMeans_!=\"naturalised\" && " +
						"Clean_" + this.getUuid() + ".establishmentMeans_!=\"invasive\" && " +
						"Clean_" + this.getUuid() + ".establishmentMeans_!=\"managed\" && " +
						"Clean_" + this.getUuid() + ".establishmentMeans_!=\"uncertain\";" ;
				messagesDeleteOthers.addAll(newConnectionDeleteOthers.executeSQLcommand("executeUpdate", sqlDeleteEstablishment));

				for(int j = 0; j < messagesDeleteOthers.size() ; j++){
					System.out.println(messagesDeleteOthers.get(j));
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
				List<String> messagesSelect = new ArrayList<>();
				messagesSelect.add("\n--- Select no establishment Means ---\n");
				String sqlSelectNoEstablishment = "SELECT abstract_,acceptedNameUsage_,acceptedNameUsageID_,accessRights_,accrualMethod_,accrualPeriodicity_,accrualPolicy_,alternative_,associatedMedia_,associatedOccurrences_,associatedOrganisms_,associatedReferences_,associatedSequences_,associatedTaxa_,audience_,available_,basisOfRecord_,bed_,behavior_,bibliographicCitation_,catalogNumber_,class_,classKey_,collectionCode_,collectionID_,conformsTo_,continent_,contributor_,coordinateAccuracy_,coordinatePrecision_,coordinateUncertaintyInMeters_,country_,countryCode_,county_,coverage_,created_,creator_,dataGeneralizations_,datasetID_,datasetKey_,datasetName_,date_,dateAccepted_,dateCopyrighted_,dateIdentified_,dateSubmitted_,day_,decimalLatitude_,decimalLongitude_,depth_,depthAccuracy_,description_,disposition_,distanceAboveSurface_,distanceAboveSurfaceAccuracy_,dynamicProperties_,earliestAgeOrLowestStage_,earliestEonOrLowestEonothem_,earliestEpochOrLowestSeries_,earliestEraOrLowestErathem_,earliestPeriodOrLowestSystem_,educationLevel_,elevation_,elevationAccuracy_,endDayOfYear_,establishmentMeans_,event_,eventDate_,eventID_,eventRemarks_,eventTime_,extent_,family_,familyKey_,fieldNotes_,fieldNumber_,footprintSpatialFit_,footprintSRS_,footprintWKT_,format_,formation_,gbifID_,genericName_,genus_,genusKey_,geodeticDatum_,geologicalContext_,geologicalContextID_,georeferencedBy_,georeferencedDate_,georeferenceProtocol_,georeferenceRemarks_,georeferenceSources_,georeferenceVerificationStatus_,group_,habitat_,hasCoordinate_,hasFormat_,hasGeospatialIssues_,hasPart_,hasVersion_,higherClassification_,higherGeography_,higherGeographyID_,highestBiostratigraphicZone_,identification_,identificationID_,identificationQualifier_,identificationReferences_,identificationRemarks_,identificationVerificationStatus_,identifiedBy_,identifier_,idFile_,individualCount_,individualID_,informationWithheld_,infraspecificEpithet_,institutionCode_,institutionID_,instructionalMethod_,isFormatOf_,island_,islandGroup_,isPartOf_,isReferencedBy_,isReplacedBy_,isRequiredBy_,issue_,issued_,isVersionOf_,kingdom_,kingdomKey_,language_,lastCrawled_,lastInterpreted_,lastParsed_,latestAgeOrHighestStage_,latestEonOrHighestEonothem_,latestEpochOrHighestSeries_,latestEraOrHighestErathem_,latestPeriodOrHighestSystem_,license_,lifeStage_,lithostratigraphicTerms_,livingSpecimen_,locality_,locationAccordingTo_,locationID_,locationRemarks_,lowestBiostratigraphicZone_,machineObservation_,materialSample_,materialSampleID_,maximumDepthinMeters_,maximumDistanceAboveSurfaceInMeters_,maximumElevationInMeters_,measurementAccuracy_,measurementDeterminedBy_,measurementDeterminedDate_,measurementID_,measurementMethod_,measurementOrFact_,measurementRemarks_,measurementType_,measurementUnit_,mediator_,mediaType_,medium_,member_,minimumDepthinMeters_,minimumDistanceAboveSurfaceInMeters_,minimumElevationInMeters_,modified_,month_,municipality_,nameAccordingTo_,nameAccordingToID_,namePublishedIn_,namePublishedInID_,namePublishedInYear_,nomenclaturalCode_,nomenclaturalStatus_,occurrence_,occurrenceDetails_,occurrenceID_,occurrenceRemarks_,occurrenceStatus_,order_,orderKey_,organism_,organismID_,organismName_,organismRemarks_,organismScope_,originalNameUsage_,originalNameUsageID_,otherCatalogNumbers_,ownerInstitutionCode_,parentNameUsage_,parentNameUsageID_,phylum_,phylumKey_,pointRadiusSpatialFit_,preparations_,preservedSpecimen_,previousIdentifications_,protocol_,provenance_,publisher_,publishingCountry_,recordedBy_,recordNumber_,references_,relatedResourceID_,relationshipAccordingTo_,relationshipEstablishedDate_,relationshipRemarks_,relation_,replaces_,reproductiveCondition_,requires_,resourceID_,resourceRelationship_,resourceRelationshipID_,rights_,rightsHolder_,samplingEffort_,samplingProtocol_,scientificName_,scientificNameAuthorship_,scientificNameID_,sex_,source_,spatial_,species_,speciesKey_,specificEpithet_,startDayOfYear_,stateProvince_,subgenus_,subgenusKey_,subject_,tableOfContents_,taxon_,taxonConceptID_,taxonID_,taxonKey_,taxonomicStatus_,taxonRank_,taxonRemarks_,temporal_,title_,type_,typeStatus_,typifiedName_,valid_,verbatimCoordinates_,verbatimCoordinateSystem_,verbatimDate_,verbatimDepth_,verbatimElevation_,verbatimEventDate_,verbatimLatitude_,verbatimLocality_,verbatimLongitude_,verbatimSRS_,verbatimTaxonRank_,vernacularName_,waterBody_,year_ "
						+ "FROM Workflow.Clean_" + this.getUuid()
						+ " WHERE Clean_" + this.getUuid() + ".establishmentMeans_=\"" + this.getInverseEstablishmentList().get(i) + "\";";
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
				List<String> messagesDelete = new ArrayList<>();
				messagesDelete.add("\n--- establishment Means ---\n");
				String sqlDeleteEstablishment = "DELETE FROM Workflow.Clean_" + this.getUuid() + " WHERE Clean_" + this.getUuid() + ".establishmentMeans_=\"" + this.getInverseEstablishmentList().get(i) + "\";";
				messagesDelete.addAll(newConnection.executeSQLcommand("executeUpdate", sqlDeleteEstablishment));

				for(int j = 0; j < messagesDelete.size() ; j++){
					System.out.println(messagesDelete.get(j));
				}
			}
		}

		return noEstablishment;

	}

	/**
	 *
	 * @return String
	 */
	public String getUuid() {

		return uuid;
	}

	/**
	 *
	 * @param uuid
	 */
	public void setUuid(String uuid) {

		this.uuid = uuid;
	}

	/**
	 *
	 * @return List<String>
	 */
	public List<String> getListEstablishmentChecked() {

		return listEstablishmentChecked;
	}

	/**
	 *
	 * @param listEstablishmentChecked
	 */
	public void setListEstablishmentChecked(ArrayList<String> listEstablishmentChecked) {
		this.listEstablishmentChecked = listEstablishmentChecked;
	}

	/**
	 *
	 * @return List<String>
	 */
	public List<String> getInverseEstablishmentList() {

		return inverseEstablishmentList;
	}

	/**
	 *
	 * @param inverseEstablishmentList
	 */
	public void setInverseEstablishmentList(List<String> inverseEstablishmentList) {
		this.inverseEstablishmentList = inverseEstablishmentList;
	}

	/**
	 *
	 * @return List<String>
	 */
	public List<String> getNoEstablishmentList() {

		return noEstablishmentList;
	}

	/**
	 *
	 * @param noEstablishmentList
	 */
	public void setNoEstablishmentList(List<String> noEstablishmentList) {
		this.noEstablishmentList = noEstablishmentList;
	}

	/**
	 *
	 * @return File
	 */
	public File getWrongEstablishmentMeansFile() {

		return wrongEstablishmentMeansFile;
	}

	/**
	 *
	 * @param wrongEstablishmentMeansFile
	 */
	public void setWrongEstablishmentMeansFile(File wrongEstablishmentMeansFile) {
		this.wrongEstablishmentMeansFile = wrongEstablishmentMeansFile;
	}

	/**
	 *
	 * @param listEstablishmentChecked
	 */
	public void setListEstablishmentChecked(List<String> listEstablishmentChecked) {
		this.listEstablishmentChecked = listEstablishmentChecked;
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
}

/**
 * src.model
 * GeographicTreatment
 */
package src.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.geotools.geojson.geom.GeometryJSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * src.model
 * 
 * GeographicTreatment.java
 * GeographicTreatment
 */
public class GeographicTreatment {

	private String DIRECTORY_PATH = "";
	private String RESSOURCES_PATH = "";
	private String nbSessionRandom;
	private int nbWrongGeospatialIssues = 0;
	private int nbWrongCoordinates = 0;
	private int nbWrongIso2 = 0;
	private DarwinCore darwinCore;
	private ArrayList<String> wrongGeoList;
	private ArrayList<String> wrongCoordinatesList;
	private ArrayList<String> wrongPolygonList;
	private File wrongGeoFile;
	private File wrongCoordinatesFile;
	private File wrongPolygonFile;

	public GeographicTreatment(DarwinCore darwinCore){
		this.darwinCore = darwinCore;
	}

	public ArrayList<String> geoGraphicTreatment(){
		ArrayList<String> infosSummary = new ArrayList<>();

		this.deleteWrongIso2();
		this.createTableClean();

		ArrayList<String> wrongCoordinates = this.deleteWrongCoordinates();
		this.setWrongCoordinatesList(wrongCoordinates);
		infosSummary.add("error number : " + Integer.toString(wrongCoordinates.size()));

		ArrayList<String> wrongGeoSpatial = this.deleteWrongGeospatial();
		this.setWrongGeoList(wrongGeoSpatial);

		ArrayList<String> wrongPolygon = this.checkCoordinatesIso2Code();
		this.setWrongPolygonList(wrongPolygon);

		return infosSummary;
	}

	/**
	 * Check if coordinates (latitude and longitude) are included in the country indicated by the iso2 code
	 * 
	 * @return void
	 */
	public ArrayList<String> checkCoordinatesIso2Code(){


		this.getDarwinCore().associateIdData();

		ArrayList<String> listToDelete = new ArrayList<>();

		HashMap<String, ArrayList<String>> idAssoData = this.getDarwinCore().getIdAssoData(); 

		int iLatitude = this.getDarwinCore().getIndiceFromTag("decimalLatitude_");
		int iLongitude = this.getDarwinCore().getIndiceFromTag("decimalLongitude_");
		int iIso2 = this.getDarwinCore().getIndiceFromTag("countryCode_");
		int iGbifID = this.getDarwinCore().getIndiceFromTag("gbifID_");
		int nbWrongIso2 = 0;

		for (String id_ : idAssoData.keySet()) {
			if(!id_ .equals("id_")){
				ArrayList<String> listInfos = idAssoData.get(id_);

				float latitude = 0;
				float longitude = 0;
				String iso2 = "";
				String iso3 = "";
				String gbifId_ = "";

				latitude = Float.parseFloat(listInfos.get(iLatitude).replace("\"", ""));
				longitude = Float.parseFloat(listInfos.get(iLongitude).replace("\"", ""));
				iso2 = listInfos.get(iIso2);
				iso3 = this.convertIso2ToIso3(iso2);
				gbifId_ = listInfos.get(iGbifID);

				File geoJsonFile = new File(RESSOURCES_PATH + "gadm_json/" + iso3.toUpperCase() + "_adm0.json");
				GeometryFactory geometryFactory = new GeometryFactory();
				Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
				/*System.out.println("--------------------------------------------------------------");
		System.out.println("------------------ Check point in polygon --------------------");
		System.out.println("Lat : " + latitude + "\tLong : " +  longitude);
		System.out.println("id_ : " + id_ + "\tgbifID : " + gbifId_ + "\tIso3 : " + iso3 + "\tiso2 : " + iso2);
				 */
				boolean isContained = this.polygonContainedPoint(point, geoJsonFile);
				/*
		System.out.println("The point is contained in the polygone : " + isContained);
		System.out.println("--------------------------------------------------------------\n");
				 */
				if(!isContained){
					nbWrongIso2 ++;
					ConnectionDatabase newConnectionSelectID = new ConnectionDatabase();
					ArrayList<String> messagesSelectID = new ArrayList<String>();
					String sqlSelectID = "SELECT abstract_,acceptedNameUsage_,acceptedNameUsageID_,accessRights_,accrualMethod_,accrualPeriodicity_,accrualPolicy_,alternative_,associatedMedia_,associatedOccurrences_,associatedOrganisms_,associatedReferences_,associatedSequences_,associatedTaxa_,audience_,available_,basisOfRecord_,bed_,behavior_,bibliographicCitation_,catalogNumber_,class_,classKey_,collectionCode_,collectionID_,conformsTo_,continent_,contributor_,coordinateAccuracy_,coordinatePrecision_,coordinateUncertaintyInMeters_,country_,countryCode_,county_,coverage_,created_,creator_,dataGeneralizations_,datasetID_,datasetKey_,datasetName_,date_,dateAccepted_,dateCopyrighted_,dateIdentified_,dateSubmitted_,day_,decimalLatitude_,decimalLongitude_,depth_,depthAccuracy_,description_,disposition_,distanceAboveSurface_,distanceAboveSurfaceAccuracy_,dynamicProperties_,earliestAgeOrLowestStage_,earliestEonOrLowestEonothem_,earliestEpochOrLowestSeries_,earliestEraOrLowestErathem_,earliestPeriodOrLowestSystem_,educationLevel_,elevation_,elevationAccuracy_,endDayOfYear_,establishmentMeans_,event_,eventDate_,eventID_,eventRemarks_,eventTime_,extent_,family_,familyKey_,fieldNotes_,fieldNumber_,footprintSpatialFit_,footprintSRS_,footprintWKT_,format_,formation_,gbifID_,genericName_,genus_,genusKey_,geodeticDatum_,geologicalContext_,geologicalContextID_,georeferencedBy_,georeferencedDate_,georeferenceProtocol_,georeferenceRemarks_,georeferenceSources_,georeferenceVerificationStatus_,group_,habitat_,hasCoordinate_,hasFormat_,hasGeospatialIssues_,hasPart_,hasVersion_,higherClassification_,higherGeography_,higherGeographyID_,highestBiostratigraphicZone_,identification_,identificationID_,identificationQualifier_,identificationReferences_,identificationRemarks_,identificationVerificationStatus_,identifiedBy_,identifier_,idFile_,individualCount_,individualID_,informationWithheld_,infraspecificEpithet_,institutionCode_,institutionID_,instructionalMethod_,isFormatOf_,island_,islandGroup_,isPartOf_,isReferencedBy_,isReplacedBy_,isRequiredBy_,issue_,issued_,isVersionOf_,kingdom_,kingdomKey_,language_,lastCrawled_,lastInterpreted_,lastParsed_,latestAgeOrHighestStage_,latestEonOrHighestEonothem_,latestEpochOrHighestSeries_,latestEraOrHighestErathem_,latestPeriodOrHighestSystem_,license_,lifeStage_,lithostratigraphicTerms_,livingSpecimen_,locality_,locationAccordingTo_,locationID_,locationRemarks_,lowestBiostratigraphicZone_,machineObservation_,materialSample_,materialSampleID_,maximumDepthinMeters_,maximumDistanceAboveSurfaceInMeters_,maximumElevationInMeters_,measurementAccuracy_,measurementDeterminedBy_,measurementDeterminedDate_,measurementID_,measurementMethod_,measurementOrFact_,measurementRemarks_,measurementType_,measurementUnit_,mediator_,mediaType_,medium_,member_,minimumDepthinMeters_,minimumDistanceAboveSurfaceInMeters_,minimumElevationInMeters_,modified_,month_,municipality_,nameAccordingTo_,nameAccordingToID_,namePublishedIn_,namePublishedInID_,namePublishedInYear_,nomenclaturalCode_,nomenclaturalStatus_,occurrence_,occurrenceDetails_,occurrenceID_,occurrenceRemarks_,occurrenceStatus_,order_,orderKey_,organism_,organismID_,organismName_,organismRemarks_,organismScope_,originalNameUsage_,originalNameUsageID_,otherCatalogNumbers_,ownerInstitutionCode_,parentNameUsage_,parentNameUsageID_,phylum_,phylumKey_,pointRadiusSpatialFit_,preparations_,preservedSpecimen_,previousIdentifications_,protocol_,provenance_,publisher_,publishingCountry_,recordedBy_,recordNumber_,references_,relatedResourceID_,relationshipAccordingTo_,relationshipEstablishedDate_,relationshipRemarks_,relation_,replaces_,reproductiveCondition_,requires_,resourceID_,resourceRelationship_,resourceRelationshipID_,rights_,rightsHolder_,samplingEffort_,samplingProtocol_,scientificName_,scientificNameAuthorship_,scientificNameID_,sex_,source_,spatial_,species_,speciesKey_,specificEpithet_,startDayOfYear_,stateProvince_,subgenus_,subgenusKey_,subject_,tableOfContents_,taxon_,taxonConceptID_,taxonID_,taxonKey_,taxonomicStatus_,taxonRank_,taxonRemarks_,temporal_,title_,type_,typeStatus_,typifiedName_,valid_,verbatimCoordinates_,verbatimCoordinateSystem_,verbatimDate_,verbatimDepth_,verbatimElevation_,verbatimEventDate_,verbatimLatitude_,verbatimLocality_,verbatimLongitude_,verbatimSRS_,verbatimTaxonRank_,vernacularName_,waterBody_,year_ FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE Clean_" + this.getNbSessionRandom() + ".id_=" + id_ + ";";
					messagesSelectID.addAll(newConnectionSelectID.newConnection("executeQuery", sqlSelectID));
					ArrayList<String> selectIDResults = newConnectionSelectID.getResultatSelect();

					for(int j = 0 ; j < messagesSelectID.size() ; j++){
						System.out.println(messagesSelectID.get(j));
					}

					for(int k = 0 ; k < selectIDResults.size() ; k++){
						if(!listToDelete.contains(selectIDResults.get(k))){
							listToDelete.add(selectIDResults.get(k));
						}
					}


					ConnectionDatabase newConnectionDeleteID = new ConnectionDatabase();
					ArrayList<String> messagesDeleteID = new ArrayList<String>();
					String sqlDeleteID = "DELETE FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE id_=" + id_ + ";";
					messagesDeleteID.addAll(newConnectionDeleteID.newConnection("executeUpdate", sqlDeleteID));

					for(int i = 0 ; i < messagesDeleteID.size() ; i++){
						System.out.println(messagesDeleteID.get(i));
					}
				}

			}
		}

		this.setNbWrongIso2(nbWrongIso2);

		return listToDelete;
	}

	/**
	 * Convert the iso2 code (2 letters) to iso3 code (3 letters)
	 * 
	 * @param String iso2
	 * @return String iso3
	 */
	public String convertIso2ToIso3(String iso2){
		String iso3 = "";

		ConnectionDatabase newConnection = new ConnectionDatabase();

		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Convert iso2 code to iso3 code ---");
		String sqlConvertIso2Iso3 = "SELECT iso3_ FROM Workflow.IsoCode WHERE iso2_ = \"" + iso2.replaceAll("\"", "") + "\";";
		messages.addAll(newConnection.newConnection("executeQuery", sqlConvertIso2Iso3));

		ArrayList<String> resultatConvert = newConnection.getResultatSelect();
		if(resultatConvert.size() != 2){
			System.err.println("Several iso2");
		}
		else{
			iso3 = resultatConvert.get(1).replaceAll("\"", "");
		}
		return iso3;
	}

	/**
	 * Check if a geospatial point is in a country (polygon or multipolygon)
	 * 
	 * @param Point geoPoint (com.vividsolutions.jts.geom.Point;)
	 * @param File geoJsonFile (format Json)
	 * 
	 * @return boolean
	 */
	public boolean polygonContainedPoint(Point geoPoint, File geoJsonFile){
		String polygonType = this.getPolygoneType(geoJsonFile);
		FileInputStream jsonInput = null;
		boolean isContained = false;

		try {
			jsonInput = new FileInputStream(geoJsonFile);
		} catch (FileNotFoundException e1) {
			System.out.println(geoJsonFile.getAbsolutePath());
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		GeometryJSON geometryJSON = new GeometryJSON();
		if(polygonType.equals("Polygon")){
			try {
				Polygon polygon = geometryJSON.readPolygon(jsonInput);
				isContained = polygon.contains(geoPoint);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (polygonType.equals("MultiPolygon")){
			try {
				MultiPolygon multipolygon = geometryJSON.readMultiPolygon(jsonInput);
				isContained = multipolygon.contains(geoPoint);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return isContained;
	}

	/**
	 * Get the polygon type : multipolygon or simple polygon
	 * 
	 * @param File geoJsonFile
	 * 
	 * @return String
	 */
	public String getPolygoneType(File geoJsonFile){
		String typePolygon = "";
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(geoJsonFile.getAbsoluteFile()));
			JSONObject jsonObjectFeatures = (JSONObject) obj;
			JSONArray features = (JSONArray) jsonObjectFeatures.get("features");
			JSONObject firstFeature = (JSONObject) features.get(0);
			JSONObject geometry = (JSONObject) firstFeature.get("geometry");
			typePolygon = (String) geometry.get("type");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return typePolygon;
	}

	//Vérifier que le code iso2 existe et qu'il est bien inscrit dans la table IsoCode !!!
	/**
	 * Create temporary table "temp" with only correct iso2 code in DarwinCoreInput table.
	 * Iso2 code (countryCode_) is correct if it's contained in IsoCode table (iso2_).
	 * 
	 * @return void
	 */
	public void deleteWrongIso2() {
		ConnectionDatabase newConnectionTemp = new ConnectionDatabase();
		ArrayList<String> messages = new ArrayList<String>();
		String choiceStatement = "executeUpdate";
		messages.add("\n--- Create temporary table with correct ISO2 ---");
		String sqlCreateTemp = "CREATE TABLE Workflow.temp_" + this.getNbSessionRandom() + " AS SELECT DarwinCoreInput.* FROM Workflow.DarwinCoreInput,Workflow.IsoCode WHERE countryCode_=IsoCode.iso2_;";
		System.out.println(sqlCreateTemp);
		messages.addAll(newConnectionTemp.newConnection(choiceStatement, sqlCreateTemp));

		for(int i = 0 ; i < messages.size() ; i++){
			System.out.println(messages.get(i));
		}
	}

	/**
	 * From temp table, create a Clean table with correct geospatial coordinates :
	 * -90 >= latitude > 0
	 *  0 < latitude <= 90
	 *  
	 *  -180 >= longitude > 0
	 *   0 < longitude <= 180
	 *   locationID_
	 *   tag "hasGeospatialIssues" = false
	 *   
	 *  @return void
	 */
	public void createTableClean(){
		ConnectionDatabase newConnectionClean = new ConnectionDatabase();
		ArrayList<String> messages = new ArrayList<String>();
		String choiceStatement = "executeUpdate";
		messages.add("\n--- Create Table Clean from temporary table ---");
		String sqlCreateClean = "CREATE TABLE Workflow.Clean_" + this.getNbSessionRandom() + " AS SELECT * FROM Workflow.temp_" + this.getNbSessionRandom() + " WHERE " +
				"(decimalLatitude_!=0 AND decimalLatitude_<90 AND decimalLatitude_>-90 AND decimalLongitude_!=0 " +
				"AND decimalLongitude_>-180 AND decimalLongitude_<180) AND (hasGeospatialIssues_!='true');";
		messages.addAll(newConnectionClean.newConnection(choiceStatement, sqlCreateClean));

		for(int i = 0 ; i < messages.size() ; i++){
			System.out.println(messages.get(i));
		}
	}

	/**
	 * Select wrong coordinates and write in a file:
	 * latitude = 0 ; <-90 ; >90
	 * longitude = 0 ; <-180 ; >180
	 * 
	 * @return File wrong coordinates
	 */
	public ArrayList<String> deleteWrongCoordinates(){
		ConnectionDatabase newConnection = new ConnectionDatabase();

		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select wrong coordinates ---");

		String sqlRetrieveWrongCoord = "SELECT abstract_,acceptedNameUsage_,acceptedNameUsageID_,accessRights_,accrualMethod_,accrualPeriodicity_,accrualPolicy_,alternative_,associatedMedia_,associatedOccurrences_,associatedOrganisms_,associatedReferences_,associatedSequences_,associatedTaxa_,audience_,available_,basisOfRecord_,bed_,behavior_,bibliographicCitation_,catalogNumber_,class_,classKey_,collectionCode_,collectionID_,conformsTo_,continent_,contributor_,coordinateAccuracy_,coordinatePrecision_,coordinateUncertaintyInMeters_,country_,countryCode_,county_,coverage_,created_,creator_,dataGeneralizations_,datasetID_,datasetKey_,datasetName_,date_,dateAccepted_,dateCopyrighted_,dateIdentified_,dateSubmitted_,day_,decimalLatitude_,decimalLongitude_,depth_,depthAccuracy_,description_,disposition_,distanceAboveSurface_,distanceAboveSurfaceAccuracy_,dynamicProperties_,earliestAgeOrLowestStage_,earliestEonOrLowestEonothem_,earliestEpochOrLowestSeries_,earliestEraOrLowestErathem_,earliestPeriodOrLowestSystem_,educationLevel_,elevation_,elevationAccuracy_,endDayOfYear_,establishmentMeans_,event_,eventDate_,eventID_,eventRemarks_,eventTime_,extent_,family_,familyKey_,fieldNotes_,fieldNumber_,footprintSpatialFit_,footprintSRS_,footprintWKT_,format_,formation_,gbifID_,genericName_,genus_,genusKey_,geodeticDatum_,geologicalContext_,geologicalContextID_,georeferencedBy_,georeferencedDate_,georeferenceProtocol_,georeferenceRemarks_,georeferenceSources_,georeferenceVerificationStatus_,group_,habitat_,hasCoordinate_,hasFormat_,hasGeospatialIssues_,hasPart_,hasVersion_,higherClassification_,higherGeography_,higherGeographyID_,highestBiostratigraphicZone_,identification_,identificationID_,identificationQualifier_,identificationReferences_,identificationRemarks_,identificationVerificationStatus_,identifiedBy_,identifier_,idFile_,individualCount_,individualID_,informationWithheld_,infraspecificEpithet_,institutionCode_,institutionID_,instructionalMethod_,isFormatOf_,island_,islandGroup_,isPartOf_,isReferencedBy_,isReplacedBy_,isRequiredBy_,issue_,issued_,isVersionOf_,kingdom_,kingdomKey_,language_,lastCrawled_,lastInterpreted_,lastParsed_,latestAgeOrHighestStage_,latestEonOrHighestEonothem_,latestEpochOrHighestSeries_,latestEraOrHighestErathem_,latestPeriodOrHighestSystem_,license_,lifeStage_,lithostratigraphicTerms_,livingSpecimen_,locality_,locationAccordingTo_,locationID_,locationRemarks_,lowestBiostratigraphicZone_,machineObservation_,materialSample_,materialSampleID_,maximumDepthinMeters_,maximumDistanceAboveSurfaceInMeters_,maximumElevationInMeters_,measurementAccuracy_,measurementDeterminedBy_,measurementDeterminedDate_,measurementID_,measurementMethod_,measurementOrFact_,measurementRemarks_,measurementType_,measurementUnit_,mediator_,mediaType_,medium_,member_,minimumDepthinMeters_,minimumDistanceAboveSurfaceInMeters_,minimumElevationInMeters_,modified_,month_,municipality_,nameAccordingTo_,nameAccordingToID_,namePublishedIn_,namePublishedInID_,namePublishedInYear_,nomenclaturalCode_,nomenclaturalStatus_,occurrence_,occurrenceDetails_,occurrenceID_,occurrenceRemarks_,occurrenceStatus_,order_,orderKey_,organism_,organismID_,organismName_,organismRemarks_,organismScope_,originalNameUsage_,originalNameUsageID_,otherCatalogNumbers_,ownerInstitutionCode_,parentNameUsage_,parentNameUsageID_,phylum_,phylumKey_,pointRadiusSpatialFit_,preparations_,preservedSpecimen_,previousIdentifications_,protocol_,provenance_,publisher_,publishingCountry_,recordedBy_,recordNumber_,references_,relatedResourceID_,relationshipAccordingTo_,relationshipEstablishedDate_,relationshipRemarks_,relation_,replaces_,reproductiveCondition_,requires_,resourceID_,resourceRelationship_,resourceRelationshipID_,rights_,rightsHolder_,samplingEffort_,samplingProtocol_,scientificName_,scientificNameAuthorship_,scientificNameID_,sex_,source_,spatial_,species_,speciesKey_,specificEpithet_,startDayOfYear_,stateProvince_,subgenus_,subgenusKey_,subject_,tableOfContents_,taxon_,taxonConceptID_,taxonID_,taxonKey_,taxonomicStatus_,taxonRank_,taxonRemarks_,temporal_,title_,type_,typeStatus_,typifiedName_,valid_,verbatimCoordinates_,verbatimCoordinateSystem_,verbatimDate_,verbatimDepth_,verbatimElevation_,verbatimEventDate_,verbatimLatitude_,verbatimLocality_,verbatimLongitude_,verbatimSRS_,verbatimTaxonRank_,vernacularName_,waterBody_,year_ FROM Workflow.DarwinCoreInput WHERE (UUID_=\"" + this.getNbSessionRandom() + "\") AND (decimalLatitude_=0 OR decimalLatitude_>90 OR decimalLatitude_<-90 OR decimalLongitude_=0 OR decimalLongitude_>180 OR decimalLongitude_<-180);";
		messages.addAll(newConnection.newConnection("executeQuery", sqlRetrieveWrongCoord));
		ArrayList<String> resultatSelect = newConnection.getResultatSelect();
		messages.add("nb lignes affectées :" + Integer.toString(resultatSelect.size() - 1));



		if(!new File(DIRECTORY_PATH + "temp/" + this.getNbSessionRandom() + "/wrong/").exists())
		{
			new File(DIRECTORY_PATH + "temp/" + this.getNbSessionRandom() + "/wrong/").mkdirs();
		}




		for(int j = 0 ; j < messages.size() ; j++){
			if(messages.get(j).contains("nb lignes affectées")){
				this.setNbWrongCoordinates(Integer.parseInt(messages.get(j).split(":")[1]));
			}
			System.out.println(messages.get(j));
		}

		this.setNbWrongCoordinates(resultatSelect.size()-1);

		return resultatSelect;
	}


	/**
	 * Select wrong geospatial and write in a file :
	 * tag "hasGeospatialIssues_" = true
	 * 
	 * @return File wrong geospatial
	 */
	public ArrayList<String> deleteWrongGeospatial(){
		ConnectionDatabase newConnection = new ConnectionDatabase();

		ArrayList<String> messages = new ArrayList<String>();
		messages.add("\n--- Select wrong geospatialIssues ---");

		String sqlRetrieveWrongGeo = "SELECT abstract_,acceptedNameUsage_,acceptedNameUsageID_,accessRights_,accrualMethod_,accrualPeriodicity_,accrualPolicy_,alternative_,associatedMedia_,associatedOccurrences_,associatedOrganisms_,associatedReferences_,associatedSequences_,associatedTaxa_,audience_,available_,basisOfRecord_,bed_,behavior_,bibliographicCitation_,catalogNumber_,class_,classKey_,collectionCode_,collectionID_,conformsTo_,continent_,contributor_,coordinateAccuracy_,coordinatePrecision_,coordinateUncertaintyInMeters_,country_,countryCode_,county_,coverage_,created_,creator_,dataGeneralizations_,datasetID_,datasetKey_,datasetName_,date_,dateAccepted_,dateCopyrighted_,dateIdentified_,dateSubmitted_,day_,decimalLatitude_,decimalLongitude_,depth_,depthAccuracy_,description_,disposition_,distanceAboveSurface_,distanceAboveSurfaceAccuracy_,dynamicProperties_,earliestAgeOrLowestStage_,earliestEonOrLowestEonothem_,earliestEpochOrLowestSeries_,earliestEraOrLowestErathem_,earliestPeriodOrLowestSystem_,educationLevel_,elevation_,elevationAccuracy_,endDayOfYear_,establishmentMeans_,event_,eventDate_,eventID_,eventRemarks_,eventTime_,extent_,family_,familyKey_,fieldNotes_,fieldNumber_,footprintSpatialFit_,footprintSRS_,footprintWKT_,format_,formation_,gbifID_,genericName_,genus_,genusKey_,geodeticDatum_,geologicalContext_,geologicalContextID_,georeferencedBy_,georeferencedDate_,georeferenceProtocol_,georeferenceRemarks_,georeferenceSources_,georeferenceVerificationStatus_,group_,habitat_,hasCoordinate_,hasFormat_,hasGeospatialIssues_,hasPart_,hasVersion_,higherClassification_,higherGeography_,higherGeographyID_,highestBiostratigraphicZone_,identification_,identificationID_,identificationQualifier_,identificationReferences_,identificationRemarks_,identificationVerificationStatus_,identifiedBy_,identifier_,idFile_,individualCount_,individualID_,informationWithheld_,infraspecificEpithet_,institutionCode_,institutionID_,instructionalMethod_,isFormatOf_,island_,islandGroup_,isPartOf_,isReferencedBy_,isReplacedBy_,isRequiredBy_,issue_,issued_,isVersionOf_,kingdom_,kingdomKey_,language_,lastCrawled_,lastInterpreted_,lastParsed_,latestAgeOrHighestStage_,latestEonOrHighestEonothem_,latestEpochOrHighestSeries_,latestEraOrHighestErathem_,latestPeriodOrHighestSystem_,license_,lifeStage_,lithostratigraphicTerms_,livingSpecimen_,locality_,locationAccordingTo_,locationID_,locationRemarks_,lowestBiostratigraphicZone_,machineObservation_,materialSample_,materialSampleID_,maximumDepthinMeters_,maximumDistanceAboveSurfaceInMeters_,maximumElevationInMeters_,measurementAccuracy_,measurementDeterminedBy_,measurementDeterminedDate_,measurementID_,measurementMethod_,measurementOrFact_,measurementRemarks_,measurementType_,measurementUnit_,mediator_,mediaType_,medium_,member_,minimumDepthinMeters_,minimumDistanceAboveSurfaceInMeters_,minimumElevationInMeters_,modified_,month_,municipality_,nameAccordingTo_,nameAccordingToID_,namePublishedIn_,namePublishedInID_,namePublishedInYear_,nomenclaturalCode_,nomenclaturalStatus_,occurrence_,occurrenceDetails_,occurrenceID_,occurrenceRemarks_,occurrenceStatus_,order_,orderKey_,organism_,organismID_,organismName_,organismRemarks_,organismScope_,originalNameUsage_,originalNameUsageID_,otherCatalogNumbers_,ownerInstitutionCode_,parentNameUsage_,parentNameUsageID_,phylum_,phylumKey_,pointRadiusSpatialFit_,preparations_,preservedSpecimen_,previousIdentifications_,protocol_,provenance_,publisher_,publishingCountry_,recordedBy_,recordNumber_,references_,relatedResourceID_,relationshipAccordingTo_,relationshipEstablishedDate_,relationshipRemarks_,relation_,replaces_,reproductiveCondition_,requires_,resourceID_,resourceRelationship_,resourceRelationshipID_,rights_,rightsHolder_,samplingEffort_,samplingProtocol_,scientificName_,scientificNameAuthorship_,scientificNameID_,sex_,source_,spatial_,species_,speciesKey_,specificEpithet_,startDayOfYear_,stateProvince_,subgenus_,subgenusKey_,subject_,tableOfContents_,taxon_,taxonConceptID_,taxonID_,taxonKey_,taxonomicStatus_,taxonRank_,taxonRemarks_,temporal_,title_,type_,typeStatus_,typifiedName_,valid_,verbatimCoordinates_,verbatimCoordinateSystem_,verbatimDate_,verbatimDepth_,verbatimElevation_,verbatimEventDate_,verbatimLatitude_,verbatimLocality_,verbatimLongitude_,verbatimSRS_,verbatimTaxonRank_,vernacularName_,waterBody_,year_ FROM Workflow.DarwinCoreInput WHERE hasGeospatialIssues_='true' AND !(decimalLatitude_=0 OR decimalLatitude_>90 OR decimalLatitude_<-90 OR decimalLongitude_=0 OR decimalLongitude_>180 OR decimalLongitude_<-180);";
		messages.addAll(newConnection.newConnection("executeQuery", sqlRetrieveWrongGeo));

		ArrayList<String> resultatSelect = newConnection.getResultatSelect();

		messages.add("nb lignes affectées : " + Integer.toString(resultatSelect.size() - 1));

		this.setNbWrongGeospatialIssues(resultatSelect.size() - 1 );

		if(!new File(DIRECTORY_PATH + "temp/" + this.getNbSessionRandom() + "/wrong/").exists())
		{
			new File(DIRECTORY_PATH + "temp/" + this.getNbSessionRandom() + "/wrong/").mkdirs();
		}



		for(int j = 0 ; j < messages.size() ; j++){
			System.out.println(messages.get(j));
		}

		this.setNbWrongGeospatialIssues(resultatSelect.size()-1);

		return resultatSelect;
	}


	public String getDIRECTORY_PATH() {
		return DIRECTORY_PATH;
	}

	public void setDIRECTORY_PATH(String dIRECTORY_PATH) {
		DIRECTORY_PATH = dIRECTORY_PATH;
	}

	public String getRESSOURCES_PATH() {
		return RESSOURCES_PATH;
	}

	public void setRESSOURCES_PATH(String rESSOURCES_PATH) {
		RESSOURCES_PATH = rESSOURCES_PATH;
	}

	public String getNbSessionRandom() {
		return nbSessionRandom;
	}

	public void setNbSessionRandom(String nbSessionRandom) {
		this.nbSessionRandom = nbSessionRandom;
	}

	public int getNbWrongGeospatialIssues() {
		return nbWrongGeospatialIssues;
	}

	public void setNbWrongGeospatialIssues(int nbWrongGeospatialIssues) {
		this.nbWrongGeospatialIssues = nbWrongGeospatialIssues;
	}

	public int getNbWrongCoordinates() {
		return nbWrongCoordinates;
	}

	public void setNbWrongCoordinates(int nbWrongCoordinates) {
		this.nbWrongCoordinates = nbWrongCoordinates;
	}

	public int getNbWrongIso2() {
		return nbWrongIso2;
	}

	public void setNbWrongIso2(int nbWrongIso2) {
		this.nbWrongIso2 = nbWrongIso2;
	}

	public DarwinCore getDarwinCore() {
		return darwinCore;
	}

	public void setDarwinCore(DarwinCore darwinCore) {
		this.darwinCore = darwinCore;
	}

	public ArrayList<String> getWrongGeoList() {
		return wrongGeoList;
	}

	public void setWrongGeoList(ArrayList<String> wrongGeoList) {
		this.wrongGeoList = wrongGeoList;
	}

	public ArrayList<String> getWrongCoordinatesList() {
		return wrongCoordinatesList;
	}

	public void setWrongCoordinatesList(ArrayList<String> wrongCoordinatesList) {
		this.wrongCoordinatesList = wrongCoordinatesList;
	}

	public ArrayList<String> getWrongPolygonList() {
		return wrongPolygonList;
	}

	public void setWrongPolygonList(ArrayList<String> wrongPolygonList) {
		this.wrongPolygonList = wrongPolygonList;
	}

	public File getWrongGeoFile() {
		return wrongGeoFile;
	}

	public void setWrongGeoFile(File wrongGeoFile) {
		this.wrongGeoFile = wrongGeoFile;
	}

	public File getWrongCoordinatesFile() {
		return wrongCoordinatesFile;
	}

	public void setWrongCoordinatesFile(File wrongCoordinatesFile) {
		this.wrongCoordinatesFile = wrongCoordinatesFile;
	}

	public File getWrongPolygonFile() {
		return wrongPolygonFile;
	}

	public void setWrongPolygonFile(File wrongPolygonFile) {
		this.wrongPolygonFile = wrongPolygonFile;
	}


}

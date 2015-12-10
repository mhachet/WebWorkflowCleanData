/**
 * @author mhachet
 */
package fr.bird.bloom.model;

import fr.bird.bloom.utils.BloomConfig;
import fr.bird.bloom.utils.BloomUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 
 * src.model
 * 
 * TreatmentData.java
 */
public class Treatment {

	private DarwinCore fileDarwinCore;
	private ArrayList<File> rasterFiles;
	private HashMap<Integer, HashMap<String, Boolean>> hashMapValidOrNot;
	private String uuid;
	private int nbSynonymInvolved = 0;


	/**
	 * 
	 * package model
	 * TreatmentData
	 */
	public Treatment(){

	}

	/**
	 * Delete Clean and temp tables. 
	 * Delete DarwinCoreInput table.
	 * 
	 * @return void
	 */
	public void deleteTables(){
		Statement statementDeleteClean = null;
		try {
			statementDeleteClean = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DatabaseTreatment newConnectionDeleteClean = new DatabaseTreatment(statementDeleteClean);
		List<String> messagesClean = new ArrayList<String>();
		messagesClean.add("\n--- Delete Clean table ---");
		messagesClean.addAll(newConnectionDeleteClean.dropTable("Workflow.Clean_" + this.getUuid()));

		for(int i = 0 ; i < messagesClean.size() ; i++){
			System.out.println(messagesClean.get(i));
		}


		Statement statementDeleteDarwin = null;
		try {
			statementDeleteDarwin = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DatabaseTreatment newConnectionDeleteDarwin = new DatabaseTreatment(statementDeleteDarwin);
		List<String> messagesDarwin = new ArrayList<String>();
		messagesDarwin.add("\n--- Delete DarwinCoreInput table ---");
		messagesDarwin.addAll(newConnectionDeleteDarwin.deleteTable("Workflow.DarwinCoreInput", this.getUuid()));

		for(int i = 0 ; i < messagesDarwin.size() ; i++){
			System.out.println(messagesDarwin.get(i));
		}


		Statement statementDeleteTemp = null;
		try {
			statementDeleteTemp = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DatabaseTreatment newConnectionDeleteTemp = new DatabaseTreatment(statementDeleteTemp);
		List<String> messagesTemp = new ArrayList<String>();
		messagesTemp.add("\n--- Delete temp table ---");
		messagesTemp.addAll(newConnectionDeleteTemp.dropTable("Workflow.temp_" + this.getUuid()));

		for(int i = 0 ; i < messagesTemp.size() ; i++){
			System.out.println(messagesTemp.get(i));
		}


	}

	/**
	 * Convert input file (not DwC) to DwC format
	 * 
	 * @param mappingDWC
	 * @throws IOException
	 * @return void
	 */
	public void mappingDwC(MappingDwC mappingDWC, int idFile) throws IOException{

		mappingDWC.setConnectionValuesTags(mappingDWC.doConnectionValuesTags());
		mappingDWC.findInvalidColumns();
		File mappedFile = mappingDWC.createNewDwcFile(this.getUuid(), idFile);

		mappingDWC.setMappedFile(mappedFile);
	}

	/**
	 * change name from reconcile service
	 * 
	 * @param reconcileService
	 * @param referenceFileReconcile
	 * @param idFile
	 */
	public void reconcileService(ReconciliationService reconcileService, CSVFile referenceFileReconcile, int idFile){

		String tagReconcile = reconcileService.getReconcileTagBased();
		int tagReconcileColumn = 0;
		Map<Integer, String> linesConnectedNewName = reconcileService.getLinesConnectedNewName();
		List<String> listLinesReconciled = new ArrayList<>();
		try{
			InputStream inputStreamReference = new FileInputStream(referenceFileReconcile.getCsvFile());
			InputStreamReader inputStreamReaderReference = new InputStreamReader(inputStreamReference);
			BufferedReader readerReference = new BufferedReader(inputStreamReaderReference);
			String line = "";
			int countLine = 0;
			while ((line = readerReference.readLine()) != null){
				//System.out.println("separator : " + referenceFileReconcile.getSeparator());
				String [] lineSplit = line.split(referenceFileReconcile.getSeparator().getSymbol(), -1);

				if(countLine == 0){
					for(int i = 0; i < lineSplit.length; i++){
						if(lineSplit[i].equals(tagReconcile)){
							tagReconcileColumn = i;
						}
					}

				}
				else{
					for(Entry<Integer, String> entry : linesConnectedNewName.entrySet()){
						//System.out.println(entry.getKey() + " - " + entry.getValue());
						if(entry.getKey() + 1 == countLine){
							lineSplit[tagReconcileColumn] = entry.getValue();
						}

					}
				}
				String newLine = StringUtils.join(lineSplit, ",");
				listLinesReconciled.add(newLine);
				countLine++;
			}
			System.err.println("reconcile true");
			reconcileService.setSuccessReconcile(Boolean.toString(true));

		}
		catch(Exception e){
			System.err.println("error false : " + e);
			reconcileService.setSuccessReconcile(Boolean.toString(false));
			
		}
		File reconcileFile = this.createFileCsv(listLinesReconciled, "reconcile_" + this.getUuid() + "_" + idFile + ".csv", "data");
		reconcileService.setReconcileFile(reconcileFile);
	}

	/**
	 * Create a DarwinCore class for each file.
	 * Initial file will be modified thanks to readFile()
	 * 
	 * @param inputFile
	 * @param nbFile
	 * @throws IOException
	 * @return List<String>
	 */
	public DarwinCore initialiseFile(File inputFile, int nbFile, String separator) throws IOException{
		System.out.println("dwc filename : " + inputFile.getAbsolutePath());
		fileDarwinCore = new DarwinCore(inputFile, nbFile, this.getUuid());
		File darwinCoreFileTemp = fileDarwinCore.readDarwinCoreFile(separator);
		fileDarwinCore.setDarwinCoreFileTemp(darwinCoreFileTemp);
		System.out.println("filename : " + darwinCoreFileTemp.getAbsolutePath());
		return fileDarwinCore;
	}


	/**
	 * Create sql request to insert input file modified (temporary) in DarwinCoreInput table
	 * 
	 * @param File inputDarwinCoreModified
	 * @param List<String> linesInputFile
	 * @return String sql request
	 */
	public String createSQLInsert(File inputDarwinCoreModified){
		String sql = "";
		BufferedReader br = null;
		InputStream in = null;
		String firstLine = "";
		int count = 0;
		try{
			in = new FileInputStream(inputDarwinCoreModified);
			br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = br.readLine() ) != null ){
				if(count == 0){
					firstLine = line.replace("\'","").replace("\"", "").replace("\t", "_,");
					break;
				}
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
		finally{
			if ( br != null ){
				try{
					br.close();
				}catch(Exception e){}
			}
		}
		sql = "LOAD DATA LOCAL INFILE '" + inputDarwinCoreModified.getAbsolutePath() + "' INTO TABLE Workflow.DarwinCoreInput FIELDS TERMINATED BY ',' ENCLOSED BY '\"' IGNORE 1 LINES (" + firstLine + ");";
		System.out.println("mistake : " + sql);
		return sql;
	}

	/**
	 * Create DarwinCoreInput table in the database from input file(s)
	 * 
	 * @param String insertFileSQL
	 * @return void
	 */
	public void createTableDarwinCoreInput(DarwinCore darwinCoreModified){
		File darwinCoreFile = darwinCoreModified.getDarwinCoreFileTemp();
		BufferedReader buff = null;
		
		try {
			buff = new BufferedReader(new FileReader(darwinCoreFile));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int countLine = 0;
		String firstLine = ""; 
		String line;
		String lines = "";
		boolean largeFile = false;
		try {
			while ((line = buff.readLine()) != null) {
				if(countLine == 0){
					firstLine = line;
				}
				else {
					lines += "(" + line + "),";
				}
				if(countLine % 5000 == 0 && countLine != 0){
					largeFile = true;
					lines = lines.substring(0,lines.length()-1);

					//String sqlInsert = "INSERT INTO Workflow.DarwinCoreInput (" + firstLine + ") VALUES (" + line + ");";
					String sqlInsert = "INSERT INTO Workflow.DarwinCoreInput (" + firstLine + ") VALUES " + lines + ";";
					//System.out.println("large : " + sqlInsert);
					Statement statement = null;
					try {
						statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DatabaseTreatment newConnection = new DatabaseTreatment(statement);
					String choiceStatement = "execute";
					List<String> messages = new ArrayList<String>();
					messages.add("\n--- Insert line " + countLine + " in DarwinCoreInput table ---");
					messages.add(countLine + " => " + sqlInsert);
					messages.addAll(newConnection.executeSQLcommand(choiceStatement, sqlInsert));
					/*for(int i = 0 ; i < messages.size() ; i++){
						writer.write(messages.get(i));
					}*/
					lines = "";
				}
				/*if(countLine % 100000 == 0){
					//System.out.println(countLine + " insert");
				}*/
				countLine ++;
			}
			lines = lines.substring(0,lines.length()-1);
			String sqlInsert = "INSERT INTO Workflow.DarwinCoreInput (" + firstLine + ") VALUES " + lines + ";";
			//System.out.println("small : " + sqlInsert);
			Statement statement = null;
			try {
				statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DatabaseTreatment newConnection = new DatabaseTreatment(statement);
			String choiceStatement = "execute";
			ArrayList<String> messages = new ArrayList<String>();
			messages.add("\n--- Insert line " + countLine + " in DarwinCoreInput table ---");
			messages.add(countLine + " => " + sqlInsert);
			messages.addAll(newConnection.executeSQLcommand(choiceStatement, sqlInsert));
			//System.out.println("insertFileSQL : " + sqlInsert);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				buff.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/*

		ConnectionDatabase newConnection = new ConnectionDatabase();
		String choiceStatement = "execute";
		List<String> messages = new ArrayList<String>();
		messages.add("\n--- Create DarwinCoreInput table ---");
		//messages.addAll(newConnection.newConnection(choiceStatement, insertFileSQL));

		for(int i = 0 ; i < messages.size() ; i++){
			System.out.println(messages.get(i));
		}*/
	} 

	/**
	 * Add synonyms for each taxon (if exist)
	 * 
	 * @param includeSynonyms
	 * @return void
	 */
	public void includeSynonyms(File includeSynonyms){
		SynonymsTreatment treatmentSynonyms = null;

		if(includeSynonyms != null){
			treatmentSynonyms = new SynonymsTreatment(includeSynonyms);
			treatmentSynonyms.setUuid(this.getUuid());
			//treatmentSynonyms.getTagsSynonymsTempTable();
			treatmentSynonyms.createSynonymTempTable();
			treatmentSynonyms.updateCleanFromSynonymTemp();
		}
		else{
			treatmentSynonyms = new SynonymsTreatment();
			treatmentSynonyms.setUuid(this.getUuid());
			treatmentSynonyms.updateClean();

		}

		this.setNbSynonymInvolved(treatmentSynonyms.getNbSynonymInvolved());
	}

	/**
	 * Find tdwg level 4 for occurrences
	 *
	 * @return boolean
	 */
	public boolean tdwgCodeOption(){
		TdwgTreatment tdwg4Treatment = new TdwgTreatment();
		tdwg4Treatment.setUuid(this.getUuid());

		tdwg4Treatment.checkIsoTdwgCode(fileDarwinCore);

		boolean sucessTdwgTreatment = tdwg4Treatment.isSucessTdwgTreatment();

		return sucessTdwgTreatment;
	}

	/**
	 * process of geo checking
	 *
	 * @return GeographicTreatment
	 */
	public GeographicTreatment checkGeographicOption(){
		GeographicTreatment geoTreatment = new GeographicTreatment(this.getFileDarwinCore());

		geoTreatment.setUuid(this.getUuid());

		geoTreatment.geoGraphicTreatment();

		File wrongGeo = this.createFileCsv(geoTreatment.getWrongGeoList(), "wrong_geospatialIssues_" + this.getUuid() + ".csv", "wrong");
		geoTreatment.setWrongGeoFile(wrongGeo);

		File wrongCoord = this.createFileCsv(geoTreatment.getWrongCoordinatesList(), "wrong_coordinates_" + this.getUuid() + ".csv", "wrong");
		geoTreatment.setWrongCoordinatesFile(wrongCoord);

		File wrongPolygon = this.createFileCsv(geoTreatment.getWrongPolygonList(), "wrong_polygon_" + this.getUuid() + ".csv", "wrong");
		geoTreatment.setWrongPolygonFile(wrongPolygon);

		return geoTreatment;
	}


	/**
	 *
	 * Check if coordinates are included in raster cells
	 *
	 * @param ArrayList<File> raster file
	 * @param rasterFiles
	 * @return void
	 */
	public RasterTreatment checkWorldClimCell(List<File> rasterFiles) {

		RasterTreatment rasterTreatment = new RasterTreatment(rasterFiles, this);

		File matrixFileValidCells = rasterTreatment.treatmentRaster();
		rasterTreatment.setMatrixFileValidCells(matrixFileValidCells);

		return rasterTreatment;
	}


	/**
	 * process of establishmentMeans option
	 *
	 * @param listEstablishmentChecked
	 * @return EstablishmentTreatment
	 */
	public EstablishmentTreatment establishmentMeansOption(List<String> listEstablishmentChecked){
		EstablishmentTreatment establishTreatment = new EstablishmentTreatment(listEstablishmentChecked);
		establishTreatment.setUuid(this.getUuid());

		establishTreatment.establishmentMeansTreatment();
		List<String> noEstablishment = establishTreatment.getNoEstablishmentList();
		File wrongEstablishmentMeans = this.createFileCsv(noEstablishment, "noEstablishmentMeans_" + this.getUuid() + ".csv", "wrong");
		establishTreatment.setWrongEstablishmentMeansFile(wrongEstablishmentMeans);

		return establishTreatment;
	}

	/**
	 * Create a new csv file from lines
	 *
	 * @param ArrayList<String> linesFile
	 * @param String fileName
	 * @param linesFile
	 * @return File
	 */
	public File createFileCsv(List<String> linesFile, String fileName, String category){
		if(!new File(BloomConfig.getDirectoryPath() + "temp/").exists()){
			BloomUtils.createDirectory(BloomConfig.getDirectoryPath() + "temp/");
		}
		if(!new File(BloomConfig.getDirectoryPath() + "temp/" + this.getUuid()).exists()){
			new File(BloomConfig.getDirectoryPath() + "temp/" + this.getUuid());
		}
		if(!new File(BloomConfig.getDirectoryPath() + "temp/" + this.getUuid() + "/data/").exists()){
			BloomUtils.createDirectory(BloomConfig.getDirectoryPath() + "temp/" + this.getUuid() + "/data/");
		}
		if(!new File(BloomConfig.getDirectoryPath() + "temp/" + this.getUuid() + "/wrong/").exists()){
			BloomUtils.createDirectory(BloomConfig.getDirectoryPath() + "temp/" + this.getUuid() + "/wrong/");
		}
		if(!new File(BloomConfig.getDirectoryPath() + "temp/" + this.getUuid() + "/final_results/").exists()){
			BloomUtils.createDirectory(BloomConfig.getDirectoryPath() + "temp/" + this.getUuid() + "/final_results/");
		}

		//String fileRename = fileName + "_" + nbFileRandom + ".csv";
		File newFile = new File(BloomConfig.getDirectoryPath() + "temp/" + this.getUuid() + "/" + category +"/" + fileName);
		FileWriter writer = null;
		try {
			writer = new FileWriter(newFile);
			for(int i = 0 ; i < linesFile.size() ; i++){
				writer.write(linesFile.get(i) + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(fileName + " written !!!");
		return newFile;
	}

	/**
	 * Delete a directory
	 *
	 * @param path
	 * @return boolean
	 */
	public boolean deleteDirectory(File path){

		if(path.isDirectory()){

			String [] filesChild = path.list();

			for(int i = 0 ; i < filesChild.length ; i ++){
				System.out.println("delete : " + filesChild[i]);
				boolean succes = this.deleteDirectory(new File(path + "/" + filesChild[i]));
				if(!succes){
					return false;
				}
			}
		}

		return path.delete();

	}

	/**
	 *
	 * @return DarwinCore
	 */
	public DarwinCore getFileDarwinCore() {
		return fileDarwinCore;
	}

	/**
	 *
	 * @param fileDarwinCore
	 * @return void
	 */
	public void setFileDarwinCore(DarwinCore fileDarwinCore) {
		this.fileDarwinCore = fileDarwinCore;
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
	 * @return void
	 */
	public void setRasterFiles(ArrayList<File> rasterFiles) {
		this.rasterFiles = rasterFiles;
	}

	/**
	 *
	 * @return HashMap<Integer,HashMap<String,Boolean>>
	 */
	public HashMap<Integer, HashMap<String, Boolean>> getHashMapValidOrNot() {
		return hashMapValidOrNot;
	}

	/**
	 *
	 * @param hashMapValidOrNot
	 * @return void
	 */
	public void setHashMapValidOrNot(
			HashMap<Integer, HashMap<String, Boolean>> hashMapValidOrNot) {
		this.hashMapValidOrNot = hashMapValidOrNot;
	}

	/**
	 *
	 * @return int
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 *
	 * @param uuid
	 * @return void
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getNbSynonymInvolved() {
		return nbSynonymInvolved;
	}

	public void setNbSynonymInvolved(int nbSynonymInvolved) {
		this.nbSynonymInvolved = nbSynonymInvolved;
	}


}


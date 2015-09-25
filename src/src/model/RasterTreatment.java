/**
 * 
 */
package src.model;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import src.model.Treatment;

/**
 * model
 * 
 * RasterTreatment.java
 * RasterTreatment
 */
public class RasterTreatment {

	private ArrayList<File> rasterFiles;
	private HashMap<Integer, HashMap<String, Boolean>> hashMapValidOrNot;
	private Treatment dataTreatment;
	private String DIRECTORY_PATH = "";
	private String RESSOURCES_PATH = "";
	private int nbWrongOccurrences;
	private File matrixFileValidCells;
	private File wrongRasterFile;

	public RasterTreatment(ArrayList<File> rasterFiles, Treatment dataTreatment){
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
		ArrayList<String> listAllData = dataTreatment.getFileDarwinCore().getIDClean();
		//System.out.println("all Data : " + listAllData);

		// initialise raster file and hash map
		this.initialiseRasterFiles(rasterFiles, listAllData);

		// retrieve all data included in a cell
		ArrayList<Integer> listValidData = this.getValidData();
		//System.out.println("valid Data : " + listValidData);

		// create a matrix file : for each point and for each raster file,
		// indicate if point is included in a cell
		File matrixFileValidCells = this.writeMatrixReport();

		// retrieve all data not included in a cell
		ArrayList<Integer> listNotValidData = this.getIDdelete(listValidData, listAllData);
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
	 * @return void
	 */
	public void initialiseRasterFiles(ArrayList<File> rasterFiles, ArrayList<String> listAllID){


		hashMapValidOrNot = new HashMap<Integer, HashMap<String,Boolean>>();

		for(int i = 1 ; i < listAllID.size() ; i++){
			int id = Integer.parseInt(listAllID.get(i).replace("\"",""));
			HashMap<String, Boolean> booleanRasterFiles = new HashMap<>();
			for(int j = 0 ; j < rasterFiles.size() ; j++){
				booleanRasterFiles.put(rasterFiles.get(j).getName(), false);
				hashMapValidOrNot.put(id, booleanRasterFiles);
			}
		}
	}

	/**
	 * Retrieve all "id_" included in a raster cell
	 * Use R script thanks to "raster" package 
	 * 
	 * @return ArrayList<Integer> list of all "id_" which are included in a raster cell
	 */
	public ArrayList<Integer> getValidData(){

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
		String scriptRaster = RESSOURCES_PATH + "raster.R";
		if(!new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/").exists())
		{
			new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/").mkdirs();
		}
		File dataInputFileRaster = new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/dataInputFileRaster.csv");			
		System.out.println("dataInputFileRaster : " + dataInputFileRaster.getAbsolutePath());
		ArrayList<Integer> listValidData = new ArrayList<>();
		ArrayList<Integer> idForOneRaster = new ArrayList<>();

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
	public ArrayList<Integer> rasterScript(String scriptRaster, File dataRasterFile, File dataInputFileRaster){

		ArrayList<String> decimalLatitude = dataTreatment.getFileDarwinCore().getDecimalLatitudeClean();
		ArrayList<String> decimalLongitude = dataTreatment.getFileDarwinCore().getDecimalLongitudeClean();
		ArrayList<String> idLine = dataTreatment.getFileDarwinCore().getIDClean();

		if(!new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/").exists())
		{
			new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/").mkdirs();
		}
		File validRaster = new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/validRaster.txt");
		try {

			FileWriter dataInputWriterTemp = new FileWriter(dataInputFileRaster, false);
			BufferedWriter dataWriter = new BufferedWriter(dataInputWriterTemp);

			for(int i = 0 ; i < decimalLatitude.size() ; i++){
				dataWriter.write(idLine.get(i) + "," + decimalLongitude.get(i) + "," + decimalLatitude.get(i) + "\n");
			}

			dataWriter.flush();
			dataWriter.close();

			FileOutputStream fos = new FileOutputStream(validRaster);
			Runtime rt = Runtime.getRuntime();
			String [] cmdarray = {"Rscript", scriptRaster, DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/", dataRasterFile.getAbsolutePath(), dataInputFileRaster.getAbsolutePath()};

			Process proc = rt.exec(cmdarray);
			// any error message?
			// any streamGobble is a thread
			StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");            

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

		ArrayList<Integer> listValidData = this.getValidIDCells(validRaster);

		return listValidData;
	}

	/**
	 * 
	 * Retrieve all valid "id_" (included in a raster cell)
	 * 
	 * @param File temporary file with all valid "id_" (included in raster cell) and others informations contained in raster file
	 * @return ArrayList<Integer> list of valid "id_"
	 */
	public ArrayList<Integer> getValidIDCells(File validRaster){
		ArrayList<Integer> listValidData = new ArrayList<Integer>();
		InputStream ips = null;
		try {
			ips = new FileInputStream(validRaster);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
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
						System.out.println("array : " + ligne);
					}
					else{
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
		if(!new File(DIRECTORY_PATH + "temp/").exists()){
			new File(DIRECTORY_PATH + "temp/").mkdirs();
		}
		if(!new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom()).exists()){
			new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom());
		}
		if(!new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/data/").exists()){
			new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/data/").mkdirs();
		}
		if(!new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/data/rasterAnalyse/").exists()){
			new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/data/rasterAnalyse/").mkdirs();
		}
		File matrix = new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/rasterAnalyse/cells_proba_raster_" + dataTreatment.getNbSessionRandom() + ".csv");
		FileWriter writer = null;
		try {
			writer = new FileWriter(matrix);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String heading = "idPoint,";
		boolean firstLine = true;
		for (Entry<Integer, HashMap<String, Boolean>> entry : hashMapValidOrNot.entrySet()) {

			HashMap<String, Boolean> proba = entry.getValue();

			if(firstLine){
				int nbRasterFile = 1;
				for (Entry<String, Boolean> probaEntry : proba.entrySet()){
					// nom du fichier raster
					String rasterName = probaEntry.getKey();

					if(nbRasterFile < rasterFiles.size()){
						heading += rasterName + ",";
					}
					else{
						heading += rasterName + "\n";
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
			String line = id + ",";
			HashMap<String, Boolean> probaBis = entry.getValue();
			for (Entry<String, Boolean> probaBisEntry : probaBis.entrySet()){
				// TRUE : id valide dans le raster ou FALSE : id non valide dans le raster
				boolean value = probaBisEntry.getValue();
				if(nbRasterFile < rasterFiles.size()){
					line += value + ",";
				}
				else{
					line += value + "\n";
				}
				nbRasterFile ++;
			}
			try {
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
	public ArrayList<Integer> getIDdelete(ArrayList<Integer> validData, ArrayList<String> validAndNotData){
		ArrayList<Integer> dataToDelete = new ArrayList<Integer>();

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
	public void deleteWrongCellsFromClean(ArrayList<Integer> notValidData){

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
			ConnectionDatabase newConnectionSelect = new ConnectionDatabase();
	
			ArrayList<String> messagesSelect = new ArrayList<String>();
			messagesSelect.add("\n--- Select point aren't included in cells ---");
			messagesSelect.add(sqlIdDelete);
			messagesSelect.addAll(newConnectionSelect.newConnection("executeQuery", sqlIdDelete));
			ArrayList<String> resultatSelect = newConnectionSelect.getResultatSelect();

			if(resultatSelect != null){
				messagesSelect.add("nb lignes affectées :" + Integer.toString(resultatSelect.size() - 1));
				if(!new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/wrong/").exists())
				{
					new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/wrong/").mkdirs();
				}
				File wrongRasterFile = dataTreatment.createFileCsv(resultatSelect, "/wrong_raster_" + this.dataTreatment.getNbSessionRandom() + ".csv", "wrong");
				this.setWrongRasterFile(wrongRasterFile);
			}
	
			for(int j = 0 ; j < messagesSelect.size() ; j++){
				System.out.println(messagesSelect.get(j));
			}
		}
		else{
			if(!new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/wrong/").exists())
			{
				new File(DIRECTORY_PATH + "temp/" + dataTreatment.getNbSessionRandom() + "/wrong/").mkdirs();
			}
			File wrongRasterFile = dataTreatment.createFileCsv(new ArrayList<String>(), "/wrong_raster_" + this.dataTreatment.getNbSessionRandom() + ".csv", "wrong");
			
			this.setWrongRasterFile(wrongRasterFile);
		}

		/* 
		 * Second part, delete data in Clean table aren't included in cells
		 */
		for(int i = 0 ; i < notValidData.size() ; i++){
			int id_ = notValidData.get(i);
			ConnectionDatabase newConnectionDelete = new ConnectionDatabase();

			ArrayList<String> messagesDelete = new ArrayList<String>();
			messagesDelete.add("\n--- Delete points not in cells ---");

			String sqlDeleteCell = "DELETE FROM Clean_" + this.getDataTreatment().getNbSessionRandom() + " WHERE Clean_" + this.getDataTreatment().getNbSessionRandom() + ".id_=" + id_ + " AND UUID_=\"" + this.getDataTreatment().getNbSessionRandom() + "\";";

			messagesDelete.addAll(newConnectionDelete.newConnection("executeUpdate", sqlDeleteCell));
			ArrayList<String> resultatDelete = newConnectionDelete.getResultatSelect();
			if(resultatDelete != null){
				messagesDelete.add("nb lignes affectées :" + Integer.toString(resultatDelete.size() - 1));
			}



			for(int j = 0 ; j < messagesDelete.size() ; j++){
				System.out.println(messagesDelete.get(j));
			}
		}

	}

	public ArrayList<File> getRasterFiles() {
		return rasterFiles;
	}

	public void setRasterFiles(ArrayList<File> rasterFiles) {
		this.rasterFiles = rasterFiles;
	}

	public HashMap<Integer, HashMap<String, Boolean>> getHashMapValidOrNot() {
		return hashMapValidOrNot;
	}

	public void setHashMapValidOrNot(
			HashMap<Integer, HashMap<String, Boolean>> hashMapValidOrNot) {
		this.hashMapValidOrNot = hashMapValidOrNot;
	}

	public Treatment getDataTreatment() {
		return dataTreatment;
	}

	public void setDataTreatment(Treatment dataTreatment) {
		this.dataTreatment = dataTreatment;
	}

	public String getDIRECTORY_PATH() {
		return DIRECTORY_PATH;
	}

	public void setDIRECTORY_PATH(String dIRECTORY_PATH) {
		DIRECTORY_PATH = dIRECTORY_PATH;
	}

	public int getNbWrongOccurrences() {
		return nbWrongOccurrences;
	}

	public void setNbWrongOccurrences(int nbWrongOccurrences) {
		this.nbWrongOccurrences = nbWrongOccurrences;
	}

	public String getRESSOURCES_PATH() {
		return RESSOURCES_PATH;
	}

	public void setRESSOURCES_PATH(String rESSOURCES_PATH) {
		RESSOURCES_PATH = rESSOURCES_PATH;
	}

	public File getMatrixFileValidCells() {
		return matrixFileValidCells;
	}

	public void setMatrixFileValidCells(File matrixFileValidCells) {
		this.matrixFileValidCells = matrixFileValidCells;
	}

	public File getWrongRasterFile() {
		return wrongRasterFile;
	}

	public void setWrongRasterFile(File wrongRasterFile) {
		this.wrongRasterFile = wrongRasterFile;
	}

}

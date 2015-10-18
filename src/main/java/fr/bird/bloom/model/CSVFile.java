/**
 * @author mhachet
 */
package fr.bird.bloom.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * fr.bird.bloom.model
 * 
 * CSVFile.java
 * CSVFile
 */
public class CSVFile {

	public final static List<String> AVAILABLE_SEPARATORS = Collections.unmodifiableList(new ArrayList<String>(Arrays.asList(",", ";", "\t")));
	protected String separator;
	protected String csvName;
	protected File csvFile;
	protected ArrayList<String> lines;

	/**
	 * 
	 * fr.bird.bloom.model
	 * CSVFile
	 * 
	 * @param file
	 */
	public CSVFile(File file) {
		this.csvFile = file;

		try {
			this.readCsvFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.csvName = file.getName();
		//this.findSeparator();
		this.setSeparator(",");
	}

	/**
	 * 
	 * @return String
	 */
	public String getSeparator(){
		return this.separator;
	}

	/**
	 * 
	 * @param separator
	 * @return void
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * 
	 * @return String
	 */
	public String getCsvName() {
		return csvName;
	}

	/**
	 * 
	 * @param csvName
	 * @return void
	 */
	public void setCsvName(String csvName) {
		this.csvName = csvName;
	}

	/**
	 * 
	 * @return File
	 */
	public File getCsvFile() {
		return csvFile;
	}

	/**
	 * 
	 * @param csvFile
	 * @return void
	 */
	public void setCsvFile(File csvFile) {
		this.csvFile = csvFile;
	}

	/**
	 * 
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getLines() {
		return lines;
	}

	/**
	 * 
	 * @param lines
	 * @return void
	 */
	public void setLines(ArrayList<String> lines) {
		this.lines = lines;
	}   

	/**
	 * Find the separator of csv file ("," "\t" or ";") 
	 * 
	 * @return void
	 */
	public void findSeparator(){
		int previous = 0;
		List<String> reste = new ArrayList<String>();

		boolean isGoodCandidate = false;

		for (String sep : AVAILABLE_SEPARATORS) {
			for(int i = 0 ; i < lines.size() ; i++){
				String line = lines.get(i);

				int compte = this.countSeparators(line, sep);
				if (compte == 0) {
					// no separator in this line
					isGoodCandidate = false;
					break;
				}
				if (compte != previous && previous != 0) {
					// not the same number that the line before
					isGoodCandidate = false;
					break;
				}

				previous = compte;
				isGoodCandidate = true;
			}
			if (isGoodCandidate) {
				reste.add(sep);
			}
		}

		if (reste.isEmpty()) {
			// no one separator found
			System.out.println("No separator found !");
			this.setSeparator("-1");
			this.separator = this.getSeparator();
		}

		else if (reste.size() > 1) {
			// too many separators found
			this.setSeparator("-1");
			this.separator = this.getSeparator();
		}	
		else{
			this.setSeparator(reste.get(0));
			this.separator = this.getSeparator();
		}
	}

	/**
	 * Read csv file and stock lines
	 * 
	 * @throws IOException
	 * @return void
	 */
	public void readCsvFile() throws IOException{
		lines = new ArrayList<String>();
		FileReader fr = new FileReader(this.csvFile);
		BufferedReader br = new BufferedReader(fr);
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			lines.add(line);
		}

		br.close();
		fr.close();
	}

	/**
	 * Count number of separators 
	 * 
	 * @param line
	 * @param separator
	 * @return int
	 */
	public int countSeparators(String line, String separator) {
		int number = 0;

		int pos = line.indexOf(separator);
		while (pos != -1) {
			number++;
			line = line.substring(pos + 1);
			pos = line.indexOf(separator);
		}
		return number;
	}
}

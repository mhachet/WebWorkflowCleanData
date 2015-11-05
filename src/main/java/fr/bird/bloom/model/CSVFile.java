/**
 * @author mhachet
 */
package fr.bird.bloom.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * src.model
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
	 * src.model
	 * CSVFile
	 * 
	 * @param file
	 */
	public CSVFile(File file) {
		this.csvFile = file;
		/*
		try {
			this.readCsvFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 */
		this.csvName = file.getName();
		//this.findSeparator();
		//this.setSeparator(",");
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
		/*byte[] searchString = {'\n'};
		int count = 0;
		long position = 1;
		try (FileInputStream file = new FileInputStream(this.csvFile)) {
			byte read[] = new byte[1];
			String line = "";
			outerLoop: while (-1 < file.read(read, 0, 1)) {
				position++;
				if (read[0] == searchString[0]) {
					int matches = 1;
					for (int i = 1; i < searchString.length; i++) {
						if (-1 > file.read(read, 0, 1)) {
							break outerLoop;
						}
						position++;
						if (read[0] == searchString[i]) {
							matches++;
						} else {
							break;
						}
					}
					count ++;
					lines.add(line);

					line = "";
				}
				else{
					line += (char)read[0];
				}

			}
			System.out.println("nblines : " + count);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 */
		BufferedReader br = null;
		InputStream in = new FileInputStream(this.csvFile);
		int count = 0;
		try{
			br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			//br.lines().forEachOrdered(line ->System.out.println("Output:"+line));
			while ((line = br.readLine() ) != null ){
				//lines.add(line);
				count ++;
				/*if(count > 13450000 ){
					System.out.println(count + line);
				}*/
			}
		}
		catch(IOException io){
			throw io;
		}finally{
			if ( br != null ){
				try{
					br.close();
				}
				catch(Exception e){

				}
			}
		}

		System.out.println("nbLines : " + count);
		/*FileReader f = new FileReader(this.csvFile);
		BufferedReader bf = new BufferedReader(f);
		Scanner scanner = new Scanner(bf);

		// On boucle sur chaque champ detecté
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			//System.out.println(line);
			lines.add(line);
			//faites ici votre traitement
		}

		scanner.close();*/
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


	public String getFirstLine(){
		BufferedReader br = null;
		InputStream in = null;
		try {
			in = new FileInputStream(this.csvFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int countLine = 0;
		String firstLine = "";
		br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		//br.lines().forEachOrdered(line ->System.out.println("Output:"+line));
		try {
			while ((line = br.readLine() ) != null ){
				if(countLine == 0){
					firstLine = line.replaceAll("\"", "").replaceAll("\'", "");
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				br.close();
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return firstLine;
	}
}

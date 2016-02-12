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
import java.util.List;

/**
 * src.model
 * <p>
 * CSVFile.java
 * CSVFile
 */
public class CSVFile {

    protected Separator separator = Separator.COMMA;
    protected String csvName;
    protected File csvFile;
    protected List<String> lines;


    public enum Separator {
        COMMA(","),
        SEMICOLON(";"),
        TAB("\t"),
        UNKNOWN("-1"),
        INCONSISTENT("-1");

        private final String symbol;

        Separator(String symbol) {

            this.symbol = symbol;
        }

        public String getSymbol() {

            return symbol;
        }

        public static Separator fromString(String s) {
            if (s != null) {
                for (Separator sep : values()) {
                    if (sep.getSymbol().equals(s)) {
                        return sep;
                    }
                }
            }
            return UNKNOWN;
        }

    }
    /**
     * src.model
     * CSVFile
     *
     * @param file
     */
    public CSVFile(File file) {
        this.csvFile = file;

        this.csvName = file.getName();
    }

    public void setSeparator(Separator separator) {

        this.separator = separator;
    }

    /**
     * @return String
     */
    public Separator getSeparator() {

        return this.separator;
    }


    /**
     * @return String
     */
    public String getCsvName() {

        return csvName;
    }

    /**
     * @param csvName
     * @return void
     */
    public void setCsvName(String csvName) {

        this.csvName = csvName;
    }

    /**
     * @return File
     */
    public File getCsvFile() {

        return csvFile;
    }

    /**
     * @param csvFile
     * @return void
     */
    public void setCsvFile(File csvFile) {

        this.csvFile = csvFile;
    }

    /**
     * @return ArrayList<String>
     */
    public List<String> getLines() {

        return lines;
    }

    /**
     * @param lines
     * @return void
     */
    public void setLines(ArrayList<String> lines) {

        this.lines = lines;
    }

    /**
     * Find the separator of csv file ("," "\t" or ";")
     * unused
     *
     * @return void
     */
    public void findSeparator() {
        int previous = 0;
        List<String> reste = new ArrayList<String>();

        boolean isGoodCandidate = false;

        for (Separator sep : Separator.values()) {
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);

                int compte = this.countSeparators(line, sep.getSymbol());
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
                reste.add(sep.getSymbol());
            }
        }

        if (reste.isEmpty()) {
            // no one separator found
            System.out.println("No separator found !");
            this.separator = Separator.UNKNOWN;
        } else if (reste.size() > 1) {
            // too many separators found
            this.separator = Separator.INCONSISTENT;
        } else {
            this.separator = Separator.fromString(reste.get(0));
        }
    }


    /**
     * Count number of separators in a line
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


    /**
     * get the first line of CSV file
     *
     * @return String
     */
    public String getFirstLine() {
        BufferedReader br = null;
        InputStream in = null;
        try {
            in = new FileInputStream(this.csvFile);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        int countLine = 0;
        String firstLine = "";
        br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                if (countLine == 0) {
                    firstLine = line.replaceAll("\"", "").replaceAll("\'", "");
                    break;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
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

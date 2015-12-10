/**
 * @author mhachet
 */
package fr.bird.bloom.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * src.model
 * <p>
 * MappingDwC.java
 */
public class MappingDwC {

    private CSVFile noMappedFile;
    private File mappedFile;
    private List<String> presentTags;
    private List<String> tagsListNoMapped;
    private List<String> tagsListDwC;
    private Map<String, String> connectionTags;
    private Map<String, List<String>> connectionValuesTags;
    private boolean mappingInvolved;
    private List<Integer> listInvalidColumns;
    private String successMapping;
    private String filename;
    private String filepath;
    private int idFile;
    private List<String> lines;

    /**
     * src.model
     * MappingDwC
     */
    public MappingDwC(CSVFile noMappedFile, boolean mappingInvolved) {
        this.noMappedFile = noMappedFile;
        this.mappingInvolved = mappingInvolved;
    }

    /**
     * Initialise mapping to DarwinCore format
     * set correct DwC tags
     * set all tags in input file
     * set present tags in both
     *
     * @return void
     */
    public void initialiseMapping(String uuid) {
        this.setTagsListDwC(this.initialiseDwCTags(uuid));
        this.setTagsListNoMapped(this.initialiseNoMappedTags());
        this.setPresentTags(this.initialisePresentTags());
    }

    /**
     * Create the mapped DwC file
     * noMappedDWC_
     *
     * @param nbFileRandom
     * @return File
     * @throws IOException
     */
    public File createNewDwcFile(String nbFileRandom, int idFile) throws IOException {
        String mappedFilename = noMappedFile.getCsvFile().getParent() + "/mappedDWC_" + nbFileRandom + "_" + idFile + ".csv";
        File mappedFile = new File(mappedFilename);

        FileWriter writerMappedFile = new FileWriter(mappedFile);
        Map<String, String> connectionTags = this.getConnectionTags();
        Map<String, List<String>> connectionValuesTags = this.getConnectionValuesTags();
        List<Integer> listInvalidColumns = this.getListInvalidColumns();

        String firstNewLine = "";
        int nbCol = connectionTags.size();
        int countTags = 0;
        //System.out.println("value " + connectionTags);
        //System.out.println("valuesTags : " + connectionValuesTags);

        int idLatitudeDwcTag = 0;
        int idLongitudeDwcTag = 0;
        for (Entry<String, String> entryDwC : connectionTags.entrySet()) {
            String[] splitKey = entryDwC.getKey().split("_");
            int idColumn = Integer.parseInt(splitKey[splitKey.length - 1]);

            if (!listInvalidColumns.contains(idColumn)) {
                String valueNoMapped = entryDwC.getValue();
                if (!" ".equals(valueNoMapped)) {
                    firstNewLine += valueNoMapped + ",";
                }
                if(valueNoMapped.equals("decimalLatitude")){
                    idLatitudeDwcTag = idColumn;
                }
                if(valueNoMapped.equals("decimalLongitude")){
                    idLongitudeDwcTag = idColumn;
                }
                countTags++;
            }

        }
        firstNewLine = firstNewLine.substring(0, firstNewLine.length() - 1) + "\n";
        //System.out.println(firstNewLine);
        writerMappedFile.write(firstNewLine);
        int countLines = 0;
        int nbLines = this.getNbLines(noMappedFile.getCsvFile());
        int countCol = 1;
        while (countLines < nbLines - 1) {
            String lineValues = "";
            countCol = 1;
            for (Entry<String, List<String>> entryValuesTags : connectionValuesTags.entrySet()) {
                List<String> listValues = entryValuesTags.getValue();
                String[] splitKey = entryValuesTags.getKey().split("_");
                //System.out.println("key : " + entryValuesTags.getKey());
               // System.out.println("separator  : " + noMappedFile.getCsvFile().get);
                Integer entryKey = Integer.parseInt(splitKey[splitKey.length - 1]);
                if (!this.getListInvalidColumns().contains(entryKey)) {
                    if(entryKey.equals(idLatitudeDwcTag) || entryKey.equals(idLongitudeDwcTag)){
                        String coordinate = listValues.get(countLines);
                        coordinate = coordinate.replace(",", ".");
                        lineValues += coordinate;
                        //System.out.println(entryKey + "  " + coordinate);
                    }

                    else if(listValues.get(countLines).contains(noMappedFile.getSeparator().getSymbol())){
                        lineValues += "\"" + listValues.get(countLines) + "\"";
                    }
                    else {
                        lineValues += listValues.get(countLines);
                    }

                    if(countCol == countTags){
                        lineValues += "\n";
                    }
                    else{
                        lineValues += ",";
                    }

                }

                countCol++;
            }
            //System.out.println(lineValues);
            writerMappedFile.write(lineValues);
            countLines++;
        }
        writerMappedFile.close();
        return mappedFile;
    }


    /**
     * Found all DwC tags
     *
     * @return ArrayList<String>
     */
    public List<String> initialiseDwCTags(String uuid) {

        List<String> tagsListDwCInit = new ArrayList<>();
        List<String> tempList = new ArrayList<>();
        String choiceStatement = "executeQuery";
        String getColumnsName = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='DarwinCoreInput';";
        List<String> messages = new ArrayList<>();
        DatabaseTreatment columnsNameDwC = null;

        try {
            Statement statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            columnsNameDwC = new DatabaseTreatment(statement);
            messages.addAll(columnsNameDwC.executeSQLcommand(choiceStatement, getColumnsName));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        messages.add("\n--- Get columns name of DwC format ---");

        //messages.addAll(columnsNameDwC.newConnection(choiceStatement, getColumnsName));

        tempList = columnsNameDwC.getResultatSelect();
        // delete "COLUMN_NAME" and "id_"filename :
        tempList.remove(0);
        tempList.remove(0);

        for (int i = 0; i < messages.size(); i++) {
            System.out.println(messages.get(i));
        }

        for (int i = 0; i < tempList.size(); i++) {
            String[] tag = tempList.get(i).split("_");
            String tagRename = tag[0].replace("\"", "");
            tagsListDwCInit.add(tagRename);
        }

        return tagsListDwCInit;
    }

    /**
     * Found all tags in input file
     *
     * @return ArrayList<String>
     * @throws IOException
     */
    public List<String> initialiseNoMappedTags() {
        /*CSVFile csvNoMapped = new CSVFile(this.getNoMappedFile().getCsvFile());
		List<String> linesCSV = csvNoMapped.getLines();
		String [] firstLine = linesCSV.get(0).split(csvNoMapped.getSeparator());
		*/
        //List<String> linesCSV = this.getNoMappedFile().getLines();
        final String separatorRegex = this.getNoMappedFile().getSeparator().getSymbol();

        final String firstLineString = this.getNoMappedFile().getFirstLine();

        String [] firstLine = firstLineString.split(separatorRegex);
        //System.out.print("separatorregex : " + separatorRegex);

        List<String> tagsListNoMappedInit = new ArrayList(Arrays.asList(firstLine));
        return tagsListNoMappedInit;
    }

    /**
     * Found all tags already DwC in input file
     *
     * @return ArrayList<String>
     */
    public List<String> initialisePresentTags() {
        List<String> presentTagsInit = new ArrayList<>();
        List<String> noMappedTags = this.getTagsListNoMapped();
        List<String> DwCtags = this.getTagsListDwC();

        for (int i = 0; i < noMappedTags.size(); i++) {
            String noMappedTag = noMappedTags.get(i);
            if (DwCtags.contains(noMappedTag)) {
                presentTagsInit.add(noMappedTag);
            }
        }
        return presentTagsInit;
    }

    public void findInvalidColumns() {
        List<Integer> invalidColumns = new ArrayList<>();

        for (Entry<String, String> entryDwC : this.getConnectionTags().entrySet()) {
            String valueNoMapped = entryDwC.getValue();
            if (" ".equals(valueNoMapped)) {
                String[] splitKey = entryDwC.getKey().split("_");
                int idColumn = Integer.parseInt(splitKey[splitKey.length - 1]);
                invalidColumns.add(idColumn);
            }
        }

        this.setListInvalidColumns(invalidColumns);
    }

    public List<String> getSplitedLine(String separator, String line){
        List<String> splitedLine = new ArrayList<>();
        //String regex = "(^|(?<=,))([^\",])*((?=,)|$)|((?<=^\")|(?<=,\"))([^\"]|\"\")*((?=\",)|(?=\"$))";
        String regex= "(^|(?<=" + separator + "))([^\"" + separator + "])*((?=" + separator + ")|$)|((?<=^\")|(?<=" + separator + "\"))([^\"]|\"\")*((?=\"" + separator + ")|(?=\"$))";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(line);
       // System.out.println("******");
        while (m.find()) {
            splitedLine.add(m.group());
            //System.out.println(m.group());
        }

       // System.out.println("******");
        return splitedLine;
    }

    /**
     * Connect tags (from input file) to value (from input file)
     *
     * @return HashMap<String,ArrayList<String>>
     */
    public Map<String, List<String>> doConnectionValuesTags() {
        Map<String, List<String>> connectionValuesTags = new HashMap<>();
        CSVFile noMappedFile = this.getNoMappedFile();
        try {
            InputStream inputStreamNoMapped = new FileInputStream(noMappedFile.getCsvFile());
            InputStreamReader inputStreamReaderNoMapped = new InputStreamReader(inputStreamNoMapped);
            BufferedReader readerNoMapped = new BufferedReader(inputStreamReaderNoMapped);
            String line = "";
            int countLine = 0;
            while ((line = readerNoMapped.readLine()) != null) {
                List<String> splitedLine = this.getSplitedLine(noMappedFile.getSeparator().getSymbol(), line);
                //String[] lineSplit = line.split(noMappedFile.getSeparator().getSymbol(), -1);
                /*for(int i = 0 ; i < splitedLine.size() ; i++){
                    System.out.println(splitedLine.get(i));
                }*/

                List<String> listValuesMap = new ArrayList<>();
                for (int i = 0; i < splitedLine.size(); i++) {
                    String id = "";
                    if (countLine == 0) {
                        id = splitedLine.get(i) + "_" + i;
                        List<String> values = new ArrayList<>();
                        connectionValuesTags.put(id, values);
                        //System.out.println(id + "  " + values);
                    } else {
                        for (Entry<String, List<String>> entry : connectionValuesTags.entrySet()) {
                            String[] idKeyTable = entry.getKey().split("_");
                            String idKey = idKeyTable[idKeyTable.length - 1];
                            if (Integer.parseInt(idKey) == i) {
                                listValuesMap = entry.getValue();
                                id = entry.getKey();
                                if (!splitedLine.get(i).isEmpty()) {
                                    listValuesMap.add(splitedLine.get(i));
                                } else {
                                    listValuesMap.add(" ");
                                }


                            }
                        }
                        //System.out.println(id + " => " + listValuesMap);
                        connectionValuesTags.put(id, listValuesMap);
                    }

                }

                countLine++;
            }
            readerNoMapped.close();
        } catch (Exception e) {
            System.err.println(e);
        }
        //System.out.println(connectionValuesTags);
        return connectionValuesTags;
    }

    public boolean doMapping() {

        List<String> listNoMapped = this.getTagsListNoMapped();
        List<String> listDwCTags = this.getTagsListDwC();

        for (int i = 0; i < listNoMapped.size(); i++) {
            String tag = listNoMapped.get(i);
            if (!listDwCTags.contains(tag)) {
                //System.out.println(tag);

                return true;
            } else {

            }
        }

        return false;
    }


    public int getNbLines(File file) {
        int nbLines = 0;

        BufferedReader br = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = br.readLine()) != null) {
                nbLines++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {

                }
            }
        }

        return nbLines;
    }

    /**
     * @return CSVFile
     */
    public CSVFile getNoMappedFile() {
        return noMappedFile;
    }

    /**
     * @param noMappedFile
     * @return void
     */
    public void setNoMappedFile(CSVFile noMappedFile) {
        this.noMappedFile = noMappedFile;
    }

    /**
     * @return File
     */
    public File getMappedFile() {
        return mappedFile;
    }

    /**
     * @param mappedFile
     * @return void
     */
    public void setMappedFile(File mappedFile) {
        this.mappedFile = mappedFile;
    }


    /**
     * @return ArrayList<String>
     */
    public List<String> getPresentTags() {
        return presentTags;
    }

    /**
     * @param presentTags
     * @return void
     */
    public void setPresentTags(List<String> presentTags) {
        this.presentTags = presentTags;
    }

    /**
     * @return ArrayList<String>
     */
    public List<String> getTagsListNoMapped() {
        return tagsListNoMapped;
    }

    /**
     * @param tagsListNoMapped
     * @return void
     */
    public void setTagsListNoMapped(List<String> tagsListNoMapped) {
        this.tagsListNoMapped = tagsListNoMapped;
    }

    /**
     * @return ArrayList<String>
     */
    public List<String> getTagsListDwC() {
        return tagsListDwC;
    }

    /**
     * @param tagsListDwC
     * @return void
     */
    public void setTagsListDwC(List<String> tagsListDwC) {
        this.tagsListDwC = tagsListDwC;
    }


    /**
     * @return HashMap<String,String>
     */
    public Map<String, String> getConnectionTags() {
        return connectionTags;
    }

    /**
     * @param connectionTags
     * @return void
     */
    public void setConnectionTags(Map<String, String> connectionTags) {
        this.connectionTags = connectionTags;
    }

    /**
     * @return HashMap<String,ArrayList<String>>
     */
    public Map<String, List<String>> getConnectionValuesTags() {
        return connectionValuesTags;
    }

    /**
     * @param connectionValuesTags
     * @return void
     */
    public void setConnectionValuesTags(Map<String, List<String>> connectionValuesTags) {
        this.connectionValuesTags = connectionValuesTags;
    }

    public List<Integer> getListInvalidColumns() {
        return listInvalidColumns;
    }

    public void setListInvalidColumns(List<Integer> listInvalidColumns) {
        this.listInvalidColumns = listInvalidColumns;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getIdFile() {
        return idFile;
    }

    public void setIdFile(int idFile) {
        this.idFile = idFile;
    }

    public String getSuccessMapping() {
        return successMapping;
    }

    public void setSuccessMapping(String successMapping) {
        this.successMapping = successMapping;
    }

    public boolean getMappingInvolved() {
        return mappingInvolved;
    }

    public void setMappingInvolved(boolean mappingInvolved) {
        this.mappingInvolved = mappingInvolved;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(ArrayList<String> lines) {
        this.lines = lines;
    }

}
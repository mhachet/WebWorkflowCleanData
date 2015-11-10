/**
 * fr.bird.bloom.servlets
 * LaunchWorkflow
 * TODO
 */
package fr.bird.bloom.servlets;

import fr.bird.bloom.beans.Finalisation;
import fr.bird.bloom.beans.Initialise;
import fr.bird.bloom.stepresults.Step1_MappingDwc;
import fr.bird.bloom.stepresults.Step2_ReconciliationService;
import fr.bird.bloom.stepresults.Step3_CheckCoordinates;
import fr.bird.bloom.stepresults.Step4_CheckGeoIssue;
import fr.bird.bloom.stepresults.Step5_IncludeSynonym;
import fr.bird.bloom.stepresults.Step6_CheckTDWG;
import fr.bird.bloom.stepresults.Step7_CheckISo2Coordinates;
import fr.bird.bloom.stepresults.Step8_CheckCoordinatesRaster;
import fr.bird.bloom.stepresults.Step9_EstablishmentMeans;
import fr.bird.bloom.model.CSVFile;
import fr.bird.bloom.model.LaunchWorkflow;
import fr.bird.bloom.model.MappingDwC;
import fr.bird.bloom.model.MappingReconcilePreparation;
import fr.bird.bloom.model.ReconciliationService;
import fr.bird.bloom.utils.BloomConfig;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;

/**
 * fr.bird.bloom.servlets
 * <p>
 * LaunchWorkflow
 */

@WebServlet(name = "MainControler")
public class MainControler extends HttpServlet {

    private Initialise initialisation;
    private String uuid;
    private Finalisation finalisation;


    // TODO FIX THESE INSTANCE VARIABLES, THEY ARE NOT THREAD SAFE !
    private Step1_MappingDwc step1;
    private Step2_ReconciliationService step2;
    private Step3_CheckCoordinates step3;
    private Step4_CheckGeoIssue step4;
    private Step5_IncludeSynonym step5;
    private Step6_CheckTDWG step6;
    private Step7_CheckISo2Coordinates step7;
    private Step8_CheckCoordinatesRaster step8;
    private Step9_EstablishmentMeans step9;

    private String getDirectoryPath() {
        if (BloomConfig.getDirectoryPath() == null) {
            BloomConfig.initializeDirectoryPath(getServletContext().getRealPath("/"));
        }
        return BloomConfig.getDirectoryPath();
    }

    /**
     * @param request
     * @param response
     * @return void
     * @throws IOException
     * @throws ServletException
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/plain");
        initialisation = new Initialise();

        //this.setUuid(this.generateRandomKey());
        //this.initialisation.setUuid(this.getUuid());

        List<FileItem> listFileItems = getMultipartRequestParameters(request);

        initialiseParameters(listFileItems, response, request);


        request.setAttribute("initialise", initialisation);

        LaunchWorkflow newLaunch = new LaunchWorkflow(this.initialisation);

        newLaunch.initialiseLaunchWorkflow();

        finalisation = newLaunch.getFinalisation();
        request.setAttribute("finalisation", finalisation);

        step1 = newLaunch.getStep1();
        request.setAttribute("step1", step1);
        step2 = newLaunch.getStep2();
        request.setAttribute("step2", step2);
        step3 = newLaunch.getStep3();
        request.setAttribute("step3", step3);
        step4 = newLaunch.getStep4();
        request.setAttribute("step4", step4);
        step5 = newLaunch.getStep5();
        request.setAttribute("step5", step5);
        step6 = newLaunch.getStep6();
        request.setAttribute("step6", step6);
        step7 = newLaunch.getStep7();
        request.setAttribute("step7", step7);
        step8 = newLaunch.getStep8();
        request.setAttribute("step8", step8);
        step9 = newLaunch.getStep9();
        request.setAttribute("step9", step9);

        this.getServletContext().getRequestDispatcher("/finalWorkflow.jsp").forward(request, response);
    }

    /**
     * Retrieve all request from the formulary
     *
     * @param request
     * @return List<FileItem>
     */
    private List<FileItem> getMultipartRequestParameters(HttpServletRequest request) {


        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
        List<FileItem> items = null;
        try {
            items = (List<FileItem>) uploadHandler.parseRequest(request);
        } catch (FileUploadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Initialise parameters/options
     *
     * @param fileItems
     * @param response
     * @return void
     * @throws IOException
     */
    private void initialiseParameters(List<FileItem> fileItems, HttpServletResponse response, HttpServletRequest request) throws IOException {

        response.setContentType("text/html");
        response.addHeader("Access-Control-Allow-Origin", "*");

        Iterator<FileItem> iterator = fileItems.iterator();
        List<MappingDwC> listMappingFiles = new ArrayList<>();
        List<ReconciliationService> listReconcileFiles = new ArrayList<>();
        List<MappingReconcilePreparation> listMappingReconcileDWC = new ArrayList<>();

        int nbFilesInput = 0;
        int nbFilesRaster = 0;
        int nbFilesHeader = 0;
        int nbFilesSynonyms = 0;
        int nbMappingInput = 0;


        while (iterator.hasNext()) {
            // DiskFileItem item = (DiskFileItem) iterator.next();
            FileItem item = iterator.next();
            String input = "inp_" + nbFilesInput;
            String raster = "raster_" + nbFilesRaster;
            String headerRaster = "header_" + nbFilesHeader;
            String synonyms = "synonyms";
            String mapping = "mappingActive_";
            String reconcileTable = "reconcileTable_";
            String reconcileActive = "reconcileActive_";
            String tableReconcile = "tableReconcile_";


            String fieldName = item.getFieldName();
            //System.out.println("fieldName : " + fieldName + " item : " + item.getString());
            if (fieldName.contains("formulaire")) {
                this.setUuid(item.getString());
                this.initialisation.setUuid(this.getUuid());
                if (!new File(getDirectoryPath() + "temp/").exists()) {
                    new File(getDirectoryPath() + "temp/").mkdirs();
                }
                if (!new File(getDirectoryPath() + "temp/" + this.getUuid()).exists()) {
                    new File(getDirectoryPath() + "temp/" + this.getUuid());
                }
                if (!new File(getDirectoryPath() + "temp/" + this.getUuid() + "/data/").exists()) {
                    new File(getDirectoryPath() + "temp/" + this.getUuid() + "/data/").mkdirs();
                }
                if (!new File(getDirectoryPath() + "temp/" + this.getUuid() + "/wrong/").exists()) {
                    new File(getDirectoryPath() + "temp/" + this.getUuid() + "/wrong/").mkdirs();
                }
                if (!new File(getDirectoryPath() + "temp/" + this.getUuid() + "/final_results/").exists()) {
                    new File(getDirectoryPath() + "temp/" + this.getUuid() + "/final_results/").mkdirs();
                }
            } else if (fieldName.equals(input)) {
                DiskFileItem itemFile = (DiskFileItem) item;
                String fileExtensionName = itemFile.getName();
                fileExtensionName = FilenameUtils.getExtension(fileExtensionName);
                File file = new File(getDirectoryPath() + "temp/" + this.getUuid() + "/data/input_" + nbFilesInput + "_" + this.getUuid() + ".csv");
                if (!file.exists()) {
                    try {
                        System.out.println("writing");
                        //itemFile.write(file);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                CSVFile csvFile = new CSVFile(file);
                MappingDwC newMappingDWC = new MappingDwC(csvFile, false);

                listMappingFiles.add(newMappingDWC);

                newMappingDWC.initialiseMapping(this.getUuid());
                HashMap<String, String> connectionTags = new HashMap<>();
                List<String> tagsNoMapped = newMappingDWC.getTagsListNoMapped();
                for (int i = 0; i < tagsNoMapped.size(); i++) {
                    connectionTags.put(tagsNoMapped.get(i) + "_" + i, "");
                }
                //System.out.println("connectionTagsControler : " + connectionTags);
                newMappingDWC.setConnectionTags(connectionTags);
                newMappingDWC.getNoMappedFile().setCsvName(file.getName());
                //initialisation.getInputFilesList().add(csvFile.getCsvFile());
                //newMappingDWC.setFilename(itemFile.getName());
                ReconciliationService reconciliationService = new ReconciliationService();
                listReconcileFiles.add(reconciliationService);

                MappingReconcilePreparation mappingReconcileDWC = new MappingReconcilePreparation(newMappingDWC, reconciliationService, nbFilesInput);
                mappingReconcileDWC.setOriginalName(itemFile.getName());
                mappingReconcileDWC.setOriginalExtension(fileExtensionName);
                listMappingReconcileDWC.add(mappingReconcileDWC);

                nbFilesInput++;
            } else if (fieldName.equals(raster)) {
                System.out.println("if raster : " + item);
                initialisation.setRaster(true);

                String fileExtensionName = item.getName();
                fileExtensionName = FilenameUtils.getExtension(fileExtensionName);
                String fileName = item.getName();
                if (!Objects.equals(fileName, "")) {
                    File file = new File(getDirectoryPath() + "temp/" + this.getUuid() + "/data/" + fileName);
                    try {
                        item.write(file);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    this.initialisation.getInputRastersList().add(file);
                    nbFilesRaster++;
                }

            } else if (fieldName.equals(headerRaster)) {
                System.out.println("if header : " + item);

                String fileExtensionName = item.getName();
                fileExtensionName = FilenameUtils.getExtension(fileExtensionName);
                String fileName = item.getName();
                if (!Objects.equals(fileName, "")) {
                    File file = new File(getDirectoryPath() + "temp/" + this.getUuid() + "/data/" + fileName);
                    try {
                        item.write(file);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    this.initialisation.getHeaderRasterList().add(file);
                    nbFilesHeader++;
                }
            } else if ("raster".equals(fieldName)) {
                initialisation.setRaster(true);
            } else if (synonyms.equals(fieldName)) {
                initialisation.setSynonym(true);
            } else if ("tdwg4".equals(fieldName)) {
                initialisation.setTdwg4Code(true);
            } else if ("establishment".equals(fieldName)) {
                initialisation.setEstablishment(true);
            } else if (fieldName.contains("dropdownDwC_")) {
                //System.out.println("fieldName : " + fieldName);
                String valueDropdown = item.getString();
                String[] tableauField = fieldName.split("_");
                String idDropdown = tableauField[tableauField.length - 1];
                for (int i = 0; i < listMappingReconcileDWC.size(); i++) {
                    Map<String, String> connectionTags = listMappingReconcileDWC.get(i).getMappingDWC().getConnectionTags();
                    for (Entry<String, String> entry : connectionTags.entrySet()) {
                        String[] tableKey = entry.getKey().split("_");
                        String idKey = tableKey[tableKey.length - 1];
                        if (idDropdown.equals(idKey)) {
                            connectionTags.put(entry.getKey(), valueDropdown);
                        }
                    }
                    //System.out.println("connectionTags : " + connectionTags);
                }

            } else if (fieldName.contains(mapping)) {
                //System.out.println("fieldName : " + fieldName);
                int idMapping = Integer.parseInt(fieldName.split("_")[1]);

                for (int i = 0; i < listMappingReconcileDWC.size(); i++) {
                    int idFile = listMappingReconcileDWC.get(i).getIdFile();
                    if (idFile == (idMapping)) {
                        MappingDwC mappingDWC = listMappingReconcileDWC.get(i).getMappingDWC();
                        if (item.getString().equals("true")) {
                            mappingDWC.setMappingInvolved(true);
                        } else {
                            mappingDWC.setMappingInvolved(false);
                        }
                    }
                }

                nbMappingInput++;
            } else if (fieldName.contains(reconcileActive)) {
                String[] tableauField = fieldName.split("_");
                int idReconcile = Integer.parseInt(tableauField[tableauField.length - 1]);
                //System.out.println("fieldName : " + fieldName + "  " + reconcileActive + " value : " + item.getString());
                for (int i = 0; i < listMappingReconcileDWC.size(); i++) {
                    int idFile = listMappingReconcileDWC.get(i).getIdFile();
                    if (idFile == (idReconcile)) {
                        ReconciliationService reconciliationService = listMappingReconcileDWC.get(i).getReconcileDWC();
                        if (item.getString().equals("true")) {
                            reconciliationService.setReconcile(true);
                            HashMap<Integer, String> linesConnectedNewName = new HashMap<Integer, String>();
                            reconciliationService.setLinesConnectedNewName(linesConnectedNewName);
                            reconciliationService.setFilename(listMappingReconcileDWC.get(i).getOriginalName());
                            listReconcileFiles.add(reconciliationService);
                            //System.out.println("in reconcileActive : " + reconciliationService.getLinesConnectedNewName());
                        } else {
                            reconciliationService.setReconcile(false);
                        }
                    }
                }

            } else if (fieldName.contains("dropdownReconcile_")) {
                //System.out.println("fieldName : " + fieldName);
                String[] tableauField = fieldName.split("_");

                int idDropdown = Integer.parseInt(tableauField[tableauField.length - 1]);
                int idFile = Integer.parseInt(tableauField[tableauField.length - 2]);
                //System.out.println("dropdownReconcile : " + fieldName + "  " + idFile + "  " + listReconcileFiles.size());
                ReconciliationService reconciliationService = listReconcileFiles.get(idFile);
                if (idDropdown == 0) {
                    String tag = item.getString();
                    reconciliationService.setReconcileTagBased(tag);
                }
            } else if (fieldName.contains("group_")) {
                //System.out.println("fieldName : " + fieldName);
                String[] tableauField = fieldName.split("_");
                String value = item.getString();
                for (int t = 0; t < tableauField.length; t++) {
                    System.out.println("tableau : " + tableauField[t]);
                }

                //System.out.println("valueradio : " + value);
                int idFile = Integer.parseInt(tableauField[tableauField.length - 2]);
                int idLine = Integer.parseInt(tableauField[tableauField.length - 1]);
                //System.out.println(idLine);
                ReconciliationService reconciliationService = listReconcileFiles.get(idFile);
                if (reconciliationService.isReconcile()) {
                    HashMap<Integer, String> linesConnnectedNewName = reconciliationService.getLinesConnectedNewName();
                    //System.out.println("in group : " + linesConnnectedNewName);
                    linesConnnectedNewName.put(idLine, value);
                }

            } else if (fieldName.contains("csvDropdown_")) {
                //System.out.println("fieldName : " + fieldName);
                int idInput = Integer.parseInt(fieldName.split("_")[1]);
                String separator = item.getString();
                if ("comma".equals(separator)) {
                    separator = ",";
                } else if ("semiComma".equals(separator)) {
                    separator = ";";
                } else {
                    separator = "\t";
                }
                for (int i = 0; i < listMappingReconcileDWC.size(); i++) {
                    int idFile = listMappingReconcileDWC.get(i).getIdFile();
                    if (idFile == (idInput)) {
                        MappingDwC mappingDWC = listMappingReconcileDWC.get(i).getMappingDWC();
                        mappingDWC.getNoMappedFile().setSeparator(CSVFile.Separator.fromString(separator));
                        //System.out.println("separator : " + item.getString());
                    }
                }

            } else {
                //System.out.println("fieldName : " + fieldName);
            }


            if (initialisation.isEstablishment()) {
                String param = item.getFieldName();
                //System.out.println(param);
                switch (param) {
                    case "native":
                        this.initialisation.getEstablishmentList().add("native");
                        break;
                    case "introduced":
                        this.initialisation.getEstablishmentList().add("introduced");
                        break;
                    case "naturalised":
                        this.initialisation.getEstablishmentList().add("naturalised");
                        break;
                    case "invasive":
                        this.initialisation.getEstablishmentList().add("invasive");
                        break;
                    case "managed":
                        this.initialisation.getEstablishmentList().add("managed");
                        break;
                    case "uncertain":
                        this.initialisation.getEstablishmentList().add("uncertain");
                        break;
                    case "others":
                        this.initialisation.getEstablishmentList().add("others");
                        break;
                }
            }
        }

        this.initialisation.setNbInput(nbFilesInput);

        this.initialisation.setListMappingReconcileFiles(listMappingReconcileDWC);


    }

    /**
     *
     */
    public void destroy() {
        // do nothing.
    }

    /**
     * @return Initialise
     */
    public Initialise getInitialisation() {
        return initialisation;
    }

    /**
     * @param initialisation
     * @return void
     */
    public void setInitialisation(Initialise initialisation) {
        this.initialisation = initialisation;
    }

    /**
     * @return int
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid
     * @return void
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return int
     */
    public String generateRandomKey() {
        return UUID.randomUUID().toString().replace("-", "_");
    }

}

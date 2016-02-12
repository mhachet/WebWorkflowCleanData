/**
 * fr.bird.bloom.servlets
 * LaunchWorkflow
 * TODO
 */
package fr.bird.bloom.servlets;

import fr.bird.bloom.beans.Finalisation;
import fr.bird.bloom.beans.InputParameters;
import fr.bird.bloom.model.*;
import fr.bird.bloom.stepresults.Step1_MappingDwc;
import fr.bird.bloom.stepresults.Step2_ReconciliationService;
import fr.bird.bloom.stepresults.Step3_CheckCoordinates;
import fr.bird.bloom.stepresults.Step4_CheckGeoIssue;
import fr.bird.bloom.stepresults.Step5_IncludeSynonym;
import fr.bird.bloom.stepresults.Step6_CheckTDWG;
import fr.bird.bloom.stepresults.Step7_CheckISo2Coordinates;
import fr.bird.bloom.stepresults.Step8_CheckCoordinatesRaster;
import fr.bird.bloom.stepresults.Step9_EstablishmentMeans;
import fr.bird.bloom.services.LaunchWorkflow;
import fr.bird.bloom.utils.BloomConfig;
import fr.bird.bloom.utils.BloomUtils;
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
import java.util.logging.*;
/**
 * fr.bird.bloom.servlets
 * <p>
 * LaunchWorkflow
 */

@WebServlet(name = "MainController")
public class MainController extends HttpServlet {

    /**
     * @param request
     * @param response
     * @return void
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response){

        response.setContentType("text/plain");

        List<FileItem> listFileItems = getMultipartRequestParameters(request);

        InputParameters inputParameters = initialiseParameters(listFileItems, response);

        request.setAttribute("initialise", inputParameters);

        LaunchWorkflow workflow = new LaunchWorkflow(inputParameters);

        try {
            workflow.executeWorkflow();
        }
        catch (Exception e){
            e.printStackTrace();

            if(e.getCause() != null){

                if (inputParameters.isSendEmail()) {
                    System.out.println("error workflow + email option : " + inputParameters.isSendEmail());
                    SendMail mail = new SendMail("errorMessage");
                    mail.setInputParameters(inputParameters);
                    mail.sendMessage(inputParameters.getEmailUser());
                }

                System.err.println("***************************************************");
                System.err.println("An error occurred during workflow - Delete temporary tables");
                System.err.println(e);
                System.err.println(e.getCause());
                System.err.println(e.getLocalizedMessage());
                System.err.println("***************************************************");
            }
        }

        Finalisation finalisation = workflow.getFinalisation();
        request.setAttribute("finalisation", finalisation);

        Step1_MappingDwc step1 = workflow.getStep1();
        request.setAttribute("step1", step1);
        Step2_ReconciliationService step2 = workflow.getStep2();
        request.setAttribute("step2", step2);
        Step3_CheckCoordinates step3 = workflow.getStep3();
        request.setAttribute("step3", step3);
        Step4_CheckGeoIssue step4 = workflow.getStep4();
        request.setAttribute("step4", step4);
        Step5_IncludeSynonym step5 = workflow.getStep5();
        request.setAttribute("step5", step5);
        Step6_CheckTDWG step6 = workflow.getStep6();
        request.setAttribute("step6", step6);
        Step7_CheckISo2Coordinates step7 = workflow.getStep7();
        request.setAttribute("step7", step7);
        Step8_CheckCoordinatesRaster step8 = workflow.getStep8();
        request.setAttribute("step8", step8);
        Step9_EstablishmentMeans step9 = workflow.getStep9();
        request.setAttribute("step9", step9);

        try {
            this.getServletContext().getRequestDispatcher("/finalWorkflow.jsp").forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get the directory path
     * @return String
     */
    private String getDirectoryPath() {

        if (BloomConfig.getDirectoryPath() == null) {
            BloomConfig.initializeDirectoryPath(getServletContext().getRealPath("/"));
        }

        return BloomConfig.getDirectoryPath();
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
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Initialise parameters/options from user interface
     *
     * @param fileItems
     * @param response
     * @return void
     */
    private InputParameters initialiseParameters(List<FileItem> fileItems, HttpServletResponse response) {

        InputParameters inputParameters = new InputParameters();
        response.setContentType("text/html");
        response.addHeader("Access-Control-Allow-Origin", "*");

        Iterator<FileItem> iterator = fileItems.iterator();
        List<MappingDwC> listMappingFiles = new ArrayList<>();
        List<ReconciliationService> listReconcileFiles = new ArrayList<>();
        List<MappingReconcilePreparation> listMappingReconcileDWC = new ArrayList<>();

        int nbFilesInput = 0;
        int nbFilesRaster = 0;
        int nbFilesHeader = 0;
        int nbMappingInput = 0;

        String uuid = "";

        while (iterator.hasNext()) {

            FileItem item = iterator.next();
            String input = "inp_" + nbFilesInput;
            String raster = "raster_" + nbFilesRaster;
            String headerRaster = "header_" + nbFilesHeader;
            String synonyms = "synonyms";
            String mapping = "mappingActive_";
            String reconcileActive = "reconcileActive_";

            String fieldName = item.getFieldName();

            //System.out.println("fieldName : " + fieldName + " item : " + item.getString());

            if (fieldName.contains("formulaire")) {
                uuid = item.getString();

                //this.createOutputLogsFile(uuid);

                this.createOutputDirectories(uuid);

                inputParameters.setUuid(uuid);

            } else if (fieldName.equals(input)) { // retrieving input file

                DiskFileItem itemFile = (DiskFileItem) item;
                String fileExtensionName = itemFile.getName();
                fileExtensionName = FilenameUtils.getExtension(fileExtensionName);
                File file = new File(getDirectoryPath() + "temp/" + uuid + "/data/input_" + nbFilesInput + "_" + uuid + ".csv");

                if (!file.exists()) {
                    try {
                        System.out.println("writing");
                        //itemFile.write(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                CSVFile csvFile = new CSVFile(file);
                MappingDwC newMappingDWC = new MappingDwC(csvFile, false);

                listMappingFiles.add(newMappingDWC);

                newMappingDWC.getNoMappedFile().setCsvName(file.getName());

                ReconciliationService reconciliationService = new ReconciliationService();
                listReconcileFiles.add(reconciliationService);

                MappingReconcilePreparation mappingReconcileDWC = new MappingReconcilePreparation(newMappingDWC, reconciliationService, nbFilesInput);
                mappingReconcileDWC.setOriginalName(itemFile.getName());
                mappingReconcileDWC.setOriginalExtension(fileExtensionName);
                listMappingReconcileDWC.add(mappingReconcileDWC);

                nbFilesInput++;

            } else if (fieldName.equals(raster)) {
                System.out.println("if raster : " + item);

                inputParameters.setRaster(true);

                String fileExtensionName = item.getName();
                fileExtensionName = FilenameUtils.getExtension(fileExtensionName);
                String fileName = item.getName();
                if (!Objects.equals(fileName, "")) {
                    File file = new File(getDirectoryPath() + "temp/" + uuid + "/data/" + fileName);
                    try {
                        item.write(file);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    inputParameters.getInputRastersList().add(file);
                    nbFilesRaster++;
                }

            } else if (fieldName.equals(headerRaster)) {
                System.out.println("if header : " + item);

                String fileExtensionName = item.getName();
                fileExtensionName = FilenameUtils.getExtension(fileExtensionName);
                String fileName = item.getName();

                if (!Objects.equals(fileName, "")) {
                    File file = new File(getDirectoryPath() + "temp/" + uuid + "/data/" + fileName);
                    try {
                        item.write(file);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    inputParameters.getHeaderRasterList().add(file);
                    nbFilesHeader++;
                }

            } else if ("raster".equals(fieldName)) {

                inputParameters.setRaster(true);

            } else if (synonyms.equals(fieldName)) {

                inputParameters.setSynonym(true);

            } else if ("tdwg4".equals(fieldName)) {

                inputParameters.setTdwg4Code(true);

            } else if ("establishment".equals(fieldName)) {

                inputParameters.setEstablishment(true);

            } else if (fieldName.contains("dropdownDwC_")) {

                String valueDropdown = item.getString();
                String[] tableauField = fieldName.split("_");
                String idDropdown = tableauField[tableauField.length - 1];

                for (int i = 0; i < listMappingReconcileDWC.size(); i++) {

                    Map<String, String> connectionTags = listMappingReconcileDWC.get(i).getMappingDWC().getConnectionTags();

                    for (Entry<String, String> entry : connectionTags.entrySet()) {
                        String[] tableKey = entry.getKey().split("_");
                        String idKey = tableKey[tableKey.length - 1];
                        if(idDropdown.equals(idKey)){
                            connectionTags.put(entry.getKey(), valueDropdown);
                        }
                    }
                }

            } else if (fieldName.contains(mapping)) {

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

            } else if (fieldName.contains(reconcileActive)) { // retrieving whether taxonomic validation should be done

                String[] tableauField = fieldName.split("_");
                int idReconcile = Integer.parseInt(tableauField[tableauField.length - 1]);

                for (int i = 0; i < listMappingReconcileDWC.size(); i++) {

                    int idFile = listMappingReconcileDWC.get(i).getIdFile();

                    if (idFile == (idReconcile)) {

                        ReconciliationService reconciliationService = listMappingReconcileDWC.get(i).getReconcileDWC();

                        if (item.getString().equals("true")) {

                            reconciliationService.setReconcile(true);
                            HashMap<Integer, String> linesConnectedNewName = new HashMap<>();
                            reconciliationService.setLinesConnectedNewName(linesConnectedNewName);
                            reconciliationService.setFilename(listMappingReconcileDWC.get(i).getOriginalName());

                        } else {
                            reconciliationService.setReconcile(false);
                        }
                    }
                }

            } else if (fieldName.contains("dropdownReconcile_")) {

                String[] tableauField = fieldName.split("_");

                int idDropdown = Integer.parseInt(tableauField[tableauField.length - 1]);
                int idFile = Integer.parseInt(tableauField[tableauField.length - 2]);

                ReconciliationService reconciliationService = listReconcileFiles.get(idFile);

                if (idDropdown == 0) {

                    String tag = item.getString();
                    reconciliationService.setReconcileTagBased(tag);

                }

            } else if (fieldName.contains("group_")) {

                String[] tableauField = fieldName.split("_");
                String value = item.getString();

                int idFile = Integer.parseInt(tableauField[tableauField.length - 2]);
                int idLine = Integer.parseInt(tableauField[tableauField.length - 1]);

                ReconciliationService reconciliationService = listReconcileFiles.get(idFile);

                if (reconciliationService.isReconcile()) {

                    Map<Integer, String> linesConnnectedNewName = reconciliationService.getLinesConnectedNewName();
                    linesConnnectedNewName.put(idLine, value);

                }

            } else if (fieldName.contains("csvDropdown_")) { //retrieving CSV separator

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

                        mappingDWC.initialiseMapping(uuid);

                        Map<String, String> connectionTags = new HashMap<>();
                        List<String> tagsNoMapped = mappingDWC.getTagsListNoMapped();

                        for (int j = 0; j < tagsNoMapped.size(); j++) {
                            connectionTags.put(tagsNoMapped.get(j) + "_" + j, "");
                        }

                        mappingDWC.setConnectionTags(connectionTags);
                    }
                }

            }else if(fieldName.contains("email")){

                if(item.getString().contains("@")){

                    inputParameters.setEmailUser(item.getString());
                    inputParameters.setSendEmail(true);
                    System.out.println("is mail : " + inputParameters.isSendEmail());

                }
            }
            else if (inputParameters.isEstablishment()) {

                String param = item.getFieldName();

                switch (param) {
                    case "native":
                        inputParameters.getEstablishmentList().add("native");
                        break;
                    case "introduced":
                        inputParameters.getEstablishmentList().add("introduced");
                        break;
                    case "naturalised":
                        inputParameters.getEstablishmentList().add("naturalised");
                        break;
                    case "invasive":
                        inputParameters.getEstablishmentList().add("invasive");
                        break;
                    case "managed":
                        inputParameters.getEstablishmentList().add("managed");
                        break;
                    case "uncertain":
                        inputParameters.getEstablishmentList().add("uncertain");
                        break;
                    case "others":
                        inputParameters.getEstablishmentList().add("others");
                        break;
                }
            }
        }

        inputParameters.setNbInputs(nbFilesInput);

        inputParameters.setListMappingReconcileFiles(listMappingReconcileDWC);

        return inputParameters;
    }

    /**
     * unused
     * @param uuid
     */
    public void createOutputLogsFile(String uuid){

        if(!new File(BloomConfig.getDirectoryPath() + "logs/").exists()){
            BloomUtils.createDirectory(BloomConfig.getDirectoryPath() + "logs/");
        }
        File logsFile = new File(BloomConfig.getDirectoryPath() + "logs/" + uuid + ".log");

    }

    /**
     * create temp, data, wrong and final_results directory.
     * @param uuid
     * @return
     */
    public boolean createOutputDirectories(String uuid){

        boolean checkDirecotoriesValidity = true;

        if (!new File(getDirectoryPath() + "temp/").exists()) {
            checkDirecotoriesValidity = false;
        }
        if (!new File(getDirectoryPath() + "temp/" + uuid).exists()) {
            checkDirecotoriesValidity = false;
        }
        if (!new File(getDirectoryPath() + "temp/" + uuid + "/data/").exists()) {
            checkDirecotoriesValidity = false;
        }
        if (!new File(getDirectoryPath() + "temp/" + uuid + "/wrong/").exists()) {
            BloomUtils.createDirectory(getDirectoryPath() + "temp/" + uuid + "/wrong/");

        }
        if (!new File(getDirectoryPath() + "temp/" + uuid + "/final_results/").exists()) {
            BloomUtils.createDirectory(getDirectoryPath() + "temp/" + uuid + "/final_results/");
        }

        return checkDirecotoriesValidity;
    }

    /**
     *
     */
    public void destroy() {
        // do nothing.
    }


}

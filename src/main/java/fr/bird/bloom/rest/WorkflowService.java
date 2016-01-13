package fr.bird.bloom.rest;


import fr.bird.bloom.beans.InputParameters;
import fr.bird.bloom.dto.ServiceInput;
import fr.bird.bloom.dto.WorkflowResults;
import fr.bird.bloom.model.*;
import fr.bird.bloom.services.LaunchWorkflow;
import fr.bird.bloom.utils.BloomUtils;
import org.apache.commons.io.FilenameUtils;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/workflow")
public class WorkflowService {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String landing() {

        return "it works over GET";
    }

    //
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkflowResults process(ServiceInput input) throws IOException {
        final String uuid = BloomUtils.generateUUID();

        InputParameters inputParameters = initParameters(input, uuid);

        LaunchWorkflow workflow = new LaunchWorkflow(inputParameters);

        workflow.executeWorkflow();
        return WorkflowResults.builder()
                .setStep1Result(workflow.getStep1())
                .setStep2Result(workflow.getStep2())
                .setStep3Result(workflow.getStep3())
                .setStep4Result(workflow.getStep4())
                .setStep5Result(workflow.getStep5())
                .setStep6Result(workflow.getStep6())
                .setStep7Result(workflow.getStep7())
                .setStep8Result(workflow.getStep8())
                .setStep9Result(workflow.getStep9())
                .setFinalisation(workflow.getFinalisation())
                .build();
    }

    private InputParameters initParameters(ServiceInput input, String uuid) {
        // TODO fix this method, still Q&D

        InputParameters inputParameters = new InputParameters();
        inputParameters.setSynonym(input.isSynonym());
        inputParameters.setTdwg4Code(input.isTdwg4Code());
        inputParameters.setRaster(input.isRaster());
        inputParameters.setEstablishment(input.isEstablishment());
        inputParameters.setUuid(uuid);
        inputParameters.setNbInputs(input.getNbInput());
        inputParameters.setEmailUser(input.getUserEmail());
        inputParameters.setSendEmail(input.isSendEmail());
        inputParameters.setMapping(input.isMapping());
        inputParameters.setCsvHeaderToDarwinCoreHeaderMapping(input.getCsvHeaderToDarwinCoreHeaderMapping());
        //System.out.println("ismapping : " + inputParameters.isMapping());

        File file = FileManagementService.storeInputFile(input.getInputFileUrl(), uuid);

        List<MappingReconcilePreparation> listMappingReconcileDWC = new ArrayList<>();

        CSVFile csvFile = new CSVFile(file);
        MappingDwC newMappingDWC = new MappingDwC(csvFile, false);
        newMappingDWC.getNoMappedFile().setSeparator(input.getSeparator());
        newMappingDWC.setMappingInvolved(inputParameters.isMapping());

        newMappingDWC.initialiseMapping(uuid);
        Map<String, String> connectionTags = new HashMap<>();
        List<String> tagsNoMapped = newMappingDWC.getTagsListNoMapped();
        Map <String, DwcHeaders> connectHeaders = inputParameters.getCsvHeaderToDarwinCoreHeaderMapping();

        int i = 0;
        for(Entry <String, DwcHeaders> entryDwC : connectHeaders.entrySet()) {
            String header = entryDwC.getKey();
            DwcHeaders headerDWC = entryDwC.getValue();
            connectionTags.put(header + "_" + i, headerDWC.getHeaderValue());
            i ++;
        }
        /*for (int i = 0; i < tagsNoMapped.size(); i++) {
            System.out.println(tagsNoMapped.get(i));
            connectionTags.put(tagsNoMapped.get(i) + "_" + i, "");
        }*/

        //System.out.println("connectionTagsControler : " + connectionTags);

        newMappingDWC.setConnectionTags(connectionTags);
        newMappingDWC.getNoMappedFile().setCsvName(file.getName());
        //inputParameters.getInputFilesList().add(csvFile.getCsvFile());
        //newMappingDWC.setFilename(itemFile.getName());
        ReconciliationService reconciliationService = new ReconciliationService();

        MappingReconcilePreparation mappingReconcileDWC = new MappingReconcilePreparation(newMappingDWC, reconciliationService, 1);
        mappingReconcileDWC.setOriginalName(file.getName());
        mappingReconcileDWC.setOriginalExtension(FilenameUtils.getExtension(file.getName()));
        listMappingReconcileDWC.add(mappingReconcileDWC);


        inputParameters.setListMappingReconcileFiles(listMappingReconcileDWC);
        return inputParameters;
    }

}

package fr.bird.bloom.dto;

import fr.bird.bloom.model.DwcHeaders;

import java.util.Map;

/**
 * This class is a Data Transfer Object used to pass all the information needed to run the bloom workflow for a single
 * input file (including the DarwinCore header mapping)
 */
public class ServiceInput {

    private boolean synonym = false;
    private boolean tdwg4Code = false;
    private boolean raster = false;
    private boolean establishment = false;
    private int nbInput = 11;
    private boolean sendEmail = true;
    private String userEmail;

    private String inputFileUrl;

    private Map<String, DwcHeaders> csvHeaderToDarwinCoreHeaderMapping;

    public ServiceInput(boolean synonym,
                        boolean tdwg4Code,
                        boolean raster,
                        boolean establishment,
                        int nbInput,
                        boolean sendEmail,
                        String userEmail,
                        String inputFileUrl,
                        Map<String, DwcHeaders> csvHeaderToDarwinCoreHeaderMapping) {
        this.synonym = synonym;
        this.tdwg4Code = tdwg4Code;
        this.raster = raster;
        this.establishment = establishment;
        this.nbInput = nbInput;
        this.sendEmail = sendEmail;
        this.userEmail = userEmail;
        this.inputFileUrl = inputFileUrl;
        this.csvHeaderToDarwinCoreHeaderMapping = csvHeaderToDarwinCoreHeaderMapping;
    }

    public ServiceInput() {
        this(false, false, false, false, 0, false, null, null, null);
    }

    public boolean isSynonym() {
        return synonym;
    }

    public boolean isTdwg4Code() {
        return tdwg4Code;
    }

    public boolean isRaster() {
        return raster;
    }

    public boolean isEstablishment() {
        return establishment;
    }

    public int getNbInput() {
        return nbInput;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getInputFileUrl() {
        return inputFileUrl;
    }

    public Map<String, DwcHeaders> getCsvHeaderToDarwinCoreHeaderMapping() {
        return csvHeaderToDarwinCoreHeaderMapping;
    }


}
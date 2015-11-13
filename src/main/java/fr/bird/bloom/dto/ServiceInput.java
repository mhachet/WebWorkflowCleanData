package fr.bird.bloom.dto;

import java.util.Map;

public class ServiceInput {

    private boolean synonym;
    private boolean tdwg4Code;
    private boolean raster;
    private boolean establishment;
    private int nbInput;
    private boolean sendEmail;
    private String userEmail;

    private String inputFileUrl;

    private Map<String, String> csvHeaderToDarwinCoreHeaderMapping;

    public ServiceInput(boolean synonym, boolean tdwg4Code, boolean raster, boolean establishment,
                        int nbInput, boolean sendEmail, String userEmail, String inputFileUrl,
                        Map<String, String> csvHeaderToDarwinCoreHeaderMapping) {
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

    public Map<String, String> getCsvHeaderToDarwinCoreHeaderMapping() {
        return csvHeaderToDarwinCoreHeaderMapping;
    }


}
/**
 * fr.bird.bloom.model
 * Finalisation
 */
package fr.bird.bloom.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * src.model
 * 
 * Finalisation.java
 * Finalisation
 */
public class Finalisation {

    private List<File> finalOutputFiles = new ArrayList<>();
    private File wrongCoordinatesFile = new File("");
    private File wrongGeospatial = new File("");
    private File wrongPolygon = new File("");
    private File matrixFileValidCells = new File("");
    private File wrongEstablishmentMeans = new File("");
    
    private String pathMatrixFile = "";
    private String pathWrongCoordinatesFile = "";
    private String pathWrongGeospatial = "";
    private String pathWrongPolygon = "";
    private String pathWrongEstablishmentMeans = "";
    private List<String> listPathsOutputFiles = new ArrayList<>();
    
    private String step1_resultMappingDwC = "";
    private String step2_reconciliationService = "";
    private String step3_checkCoordinates = "";
    private String step4_checkGeospatialIssue = "";
    private String step5_checkTaxonomy = "";
    private String step6_checkSynonym = "";
    private String step7_checkTDWG = "";
    private String step8_checkPolygonIso2 = "";
    private String step9_checkRaster = "";
    
    public Finalisation(){
	
    }
    
    public String getStep1_resultMappingDwC() {
        return step1_resultMappingDwC;
    }

    public void setStep1_resultMappingDwC(String step1_resultMappingDwC) {
        this.step1_resultMappingDwC = step1_resultMappingDwC;
    }
    
    public String getStep2_reconciliationService() {
        return step2_reconciliationService;
    }

    public void setStep2_reconciliationService(String step2_reconciliationService) {
        this.step2_reconciliationService = step2_reconciliationService;
    }

    public String getStep3_checkCoordinates() {
        return step3_checkCoordinates;
    }

    public void setStep3_checkCoordinates(String step3_checkCoordinates) {
        this.step3_checkCoordinates = step3_checkCoordinates;
    }

    public String getStep4_checkGeospatialIssue() {
        return step4_checkGeospatialIssue;
    }

    public void setStep4_checkGeospatialIssue(String step4_checkGeospatialIssue) {
        this.step4_checkGeospatialIssue = step4_checkGeospatialIssue;
    }

    public String getStep5_checkTaxonomy() {
        return step5_checkTaxonomy;
    }

    public void setStep5_checkTaxonomy(String step5_checkTaxonomy) {
        this.step5_checkTaxonomy = step5_checkTaxonomy;
    }

    public String getStep6_checkSynonym() {
        return step6_checkSynonym;
    }

    public void setStep6_checkSynonym(String step6_checkSynonym) {
        this.step6_checkSynonym = step6_checkSynonym;
    }

    public String getStep7_checkTDWG() {
        return step7_checkTDWG;
    }

    public void setStep7_checkTDWG(String step7_checkTDWG) {
        this.step7_checkTDWG = step7_checkTDWG;
    }

    public String getStep8_checkPolygonIso2() {
        return step8_checkPolygonIso2;
    }

    public void setStep8_checkPolygonIso2(String step8_checkPolygonIso2) {
        this.step8_checkPolygonIso2 = step8_checkPolygonIso2;
    }

    public String getStep9_checkRaster() {
        return step9_checkRaster;
    }

    public void setStep9_checkRaster(String step9_checkRaster) {
        this.step9_checkRaster = step9_checkRaster;
    }

    public String getPathWrongCoordinatesFile() {
        return pathWrongCoordinatesFile;
    }

    public void setPathWrongCoordinatesFile(String pathWrongCoordinatesFile) {
        this.pathWrongCoordinatesFile = pathWrongCoordinatesFile;
    }

    public String getPathWrongGeospatial() {
        return pathWrongGeospatial;
    }

    public void setPathWrongGeospatial(String pathWrongGeospatial) {
        this.pathWrongGeospatial = pathWrongGeospatial;
    }

    public String getPathWrongPolygon() {
        return pathWrongPolygon;
    }

    public void setPathWrongPolygon(String pathWrongPolygon) {
        this.pathWrongPolygon = pathWrongPolygon;
    }

    public String getPathWrongEstablishmentMeans() {
        return pathWrongEstablishmentMeans;
    }

    public void setPathWrongEstablishmentMeans(String pathWrongEstablishmentMeans) {
        this.pathWrongEstablishmentMeans = pathWrongEstablishmentMeans;
    }

    public List<String> getListPathsOutputFiles() {
        return listPathsOutputFiles;
    }

    public void setListPathsOutputFiles(List<String> listPathsOutputFiles) {
        this.listPathsOutputFiles = listPathsOutputFiles;
    }

    public String getPathMatrixFile() {
        return pathMatrixFile;
    }

    public void setPathMatrixFile(String pathMatrixFile) {
        this.pathMatrixFile = pathMatrixFile;
    }

    public List<File> getFinalOutputFiles() {
        return finalOutputFiles;
    }

    public void setFinalOutputFiles(List<File> finalOutputFiles) {
        this.finalOutputFiles = finalOutputFiles;
    }

    public File getWrongCoordinatesFile() {
        return wrongCoordinatesFile;
    }

    public void setWrongCoordinatesFile(File wrongCoordinatesFile) {
        this.wrongCoordinatesFile = wrongCoordinatesFile;
    }

    public File getWrongGeospatial() {
        return wrongGeospatial;
    }

    public void setWrongGeospatial(File wrongGeospatial) {
        this.wrongGeospatial = wrongGeospatial;
    }

    public File getWrongPolygon() {
        return wrongPolygon;
    }

    public void setWrongPolygon(File wrongPolygon) {
        this.wrongPolygon = wrongPolygon;
    }

    public File getMatrixFileValidCells() {
        return matrixFileValidCells;
    }

    public void setMatrixFileValidCells(File matrixFileValidCells) {
        this.matrixFileValidCells = matrixFileValidCells;
    }

    public File getWrongEstablishmentMeans() {
        return wrongEstablishmentMeans;
    }

    public void setWrongEstablishmentMeans(File wrongEstablishmentMeans) {
        this.wrongEstablishmentMeans = wrongEstablishmentMeans;
    }
    
}

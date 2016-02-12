/**
 * src.model
 * ReconciliationService
 */
package fr.bird.bloom.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.*;


/**
 * src.model
 * 
 * ReconciliationService.java
 * ReconciliationService
 */
public class ReconciliationService{

    private File reconcileFile;
    private boolean reconcile;
    private String reconcileTagBased;
    private Map<Integer, String> linesConnectedNewName;
    private String filename;
    private String successReconcile;
    private String filepath;
    private ArrayList<String> lines;
    
    public ReconciliationService(){

    }

    /**
     *
     * @return File
     */
    public File getReconcileFile() {

        return reconcileFile;
    }

    /**
     *
     * @param reconcileFile
     */
    public void setReconcileFile(File reconcileFile) {

        this.reconcileFile = reconcileFile;
    }

    /**
     *
     * @return boolean
     */
    public boolean isReconcile() {

        return reconcile;
    }

    /**
     *
     * @param reconcile
     */
    public void setReconcile(boolean reconcile) {

        this.reconcile = reconcile;
    }

    /**
     *
     * @return String
     */
    public String getReconcileTagBased() {

        return reconcileTagBased;
    }

    /**
     *
     * @param reconcileTagBased
     */
    public void setReconcileTagBased(String reconcileTagBased) {

        this.reconcileTagBased = reconcileTagBased;
    }

    /**
     *
     * @return Map<Integer, String>
     */
    public Map<Integer, String> getLinesConnectedNewName() {

        return linesConnectedNewName;
    }

    /**
     *
     * @param linesConnectedNewName
     */
    public void setLinesConnectedNewName(Map<Integer, String> linesConnectedNewName) {
        this.linesConnectedNewName = linesConnectedNewName;
    }

    /**
     *
     * @return String
     */
    public String getFilename() {

        return filename;
    }

    /**
     *
     * @param filename
     */
    public void setFilename(String filename) {

        this.filename = filename;
    }

    /**
     *
     * @return String
     */
	public String getSuccessReconcile() {

		return successReconcile;
	}

    /**
     *
     * @param successReconcile
     */
	public void setSuccessReconcile(String successReconcile) {

		this.successReconcile = successReconcile;
	}

    /**
     *
     * @return String
     */
	public String getFilepath() {

		return filepath;
	}

    /**
     *
     * @param filepath
     */
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

    /**
     *
     * @return List<String>
     */
	public List<String> getLines() {
		return lines;
	}

    /**
     *
     * @param lines
     */
	public void setLines(ArrayList<String> lines) {
		this.lines = lines;
	}
    
}

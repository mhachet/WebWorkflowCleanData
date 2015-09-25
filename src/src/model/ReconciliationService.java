/**
 * src.model
 * ReconciliationService
 */
package src.model;

import java.io.File;
import java.util.HashMap;

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
    private HashMap<Integer, String> linesConnectedNewName;
    private String filename;
    private String successReconcile;
    private String filepath;
    
    public ReconciliationService(){

    }

    public File getReconcileFile() {
        return reconcileFile;
    }

    public void setReconcileFile(File reconcileFile) {
        this.reconcileFile = reconcileFile;
    }

    public boolean isReconcile() {
        return reconcile;
    }

    public void setReconcile(boolean reconcile) {
        this.reconcile = reconcile;
    }

    public String getReconcileTagBased() {
        return reconcileTagBased;
    }

    public void setReconcileTagBased(String reconcileTagBased) {
        this.reconcileTagBased = reconcileTagBased;
    }

    public HashMap<Integer, String> getLinesConnectedNewName() {
        return linesConnectedNewName;
    }

    public void setLinesConnectedNewName(
    	HashMap<Integer, String> linesConnectedNewName) {
        this.linesConnectedNewName = linesConnectedNewName;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
	
	public String getSuccessReconcile() {
		return successReconcile;
	}

	public void setSuccessReconcile(String successReconcile) {
		this.successReconcile = successReconcile;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
    
}

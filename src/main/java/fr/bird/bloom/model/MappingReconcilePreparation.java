/**
 * src.model
 * MappingReconcilePreparation
 */
package fr.bird.bloom.model;


/**
 * src.model
 * 
 * MappingReconcilePreparation.java
 * MappingReconcilePreparation
 */
public class MappingReconcilePreparation {

    private MappingDwC mappingDWC;
    private ReconciliationService reconcileDWC;
    private int idFile;
    private String originalName = "";
    private String originalExtension = "";
    private boolean isValid = true;
    
   /**
    * 
    * src.model
    * MappingReconcilePreparation
    */
    public MappingReconcilePreparation(MappingDwC mappingDWC, ReconciliationService reconcileDWC, int idFile){
	this.mappingDWC = mappingDWC;
	this.reconcileDWC = reconcileDWC;
	this.idFile = idFile;
    }
    
    /**
     * 
     * @return MappingDwC
     */
    public MappingDwC getMappingDWC() {
	return mappingDWC;
    }

    /**
     * 
     * @param mappingDWC
     * @return void
     */
    public void setMappingDWC(MappingDwC mappingDWC) {
        this.mappingDWC = mappingDWC;
    }

    /**
     * 
     * @return ReconciliationService
     */
    public ReconciliationService getReconcileDWC() {
        return reconcileDWC;
    }
    
    /**
     * 
     * @param reconcileDWC
     * @return void
     */
    public void setReconcileDWC(ReconciliationService reconcileDWC) {
        this.reconcileDWC = reconcileDWC;
    }

    /**
     * 
     * @return int
     */
    public int getIdFile() {
        return idFile;
    }

    /**
     * 
     * @param idFile
     * @return void
     */
    public void setIdFile(int idFile) {
        this.idFile = idFile;
    }

    /**
     * 
     * @return String
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * 
     * @param originalName
     */
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    /**
     * 
     * @return String
     */
    public String getOriginalExtension() {
        return originalExtension;
    }

    /**
     * 
     * @param originalExtension
     */
    public void setOriginalExtension(String originalExtension) {
        this.originalExtension = originalExtension;
    }

    /**
     * 
     * @return boolean
     */
	public boolean isValid() {
		return isValid;
	}

	/**
	 * 
	 * @param isValid
	 */
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
    
}

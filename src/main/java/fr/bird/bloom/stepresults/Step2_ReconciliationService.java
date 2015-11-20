/**
 * fr.bird.bloom.beans
 * Step2_ReconciliationService
 */
package fr.bird.bloom.stepresults;

import fr.bird.bloom.model.ReconciliationService;

import java.util.HashMap;
import java.util.Map;

/**
 * fr.bird.bloom.beans
 * 
 * Step2_ReconciliationService.java
 * Step2_ReconciliationService
 */
public class Step2_ReconciliationService {

    private boolean involved = false;
    private Map<Integer, ReconciliationService> infos_reconcile = new HashMap<Integer, ReconciliationService>();
    
    public Step2_ReconciliationService(){
	
    }
    
    public boolean isInvolved() {
        return involved;
    }

    public void setInvolved(boolean involved) {
        this.involved = involved;
    }

	public Map<Integer, ReconciliationService> getInfos_reconcile() {
		return infos_reconcile;
	}

	public void setInfos_reconcile(HashMap<Integer, ReconciliationService> infos_reconcile) {
		this.infos_reconcile = infos_reconcile;
	}
    
    
}

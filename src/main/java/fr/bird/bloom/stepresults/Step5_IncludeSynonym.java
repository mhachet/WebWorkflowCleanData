/**
 * fr.bird.bloom.beans
 * Step5_IncludeSynonym
 */
package fr.bird.bloom.stepresults;

/**
 * fr.bird.bloom.beans
 * 
 * Step5_IncludeSynonym.java
 * Step5_IncludeSynonym
 */
public class Step5_IncludeSynonym {

    private boolean step5_ok = true;
    private int nbFound = 0;
    private boolean involved = false;
    
    /**
     * 
     * fr.bird.bloom.beans
     * Step5_IncludeSynonym
     */
    public Step5_IncludeSynonym(){
	
    }

    public boolean isStep5_ok() {
        return step5_ok;
    }

    public void setStep5_ok(boolean step5_ok) {
        this.step5_ok = step5_ok;
    }

    public int getNbFound() {
        return nbFound;
    }

    public void setNbFound(int nbFound) {
        this.nbFound = nbFound;
    }

    public boolean isInvolved() {
        return involved;
    }

    public void setInvolved(boolean involved) {
        this.involved = involved;
    }

    
    
}

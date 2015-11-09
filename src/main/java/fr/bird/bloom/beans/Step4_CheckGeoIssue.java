/**
 * fr.bird.bloom.beans
 * Step4_CheckGeoIssue
 */
package fr.bird.bloom.beans;

/**
 * fr.bird.bloom.beans
 * 
 * Step4_CheckGeoIssue.java
 * Step4_CheckGeoIssue
 */
public class Step4_CheckGeoIssue {
    private boolean step4_ok = true;
    private int nbFound = 0;
    private boolean involved = true;
    private String pathWrongGeoIssue= "";
    
    /**
     * 
     * fr.bird.bloom.beans
     * Step4_CheckGeoIssue
     */
    public Step4_CheckGeoIssue(){
	
    }

    public boolean isInvolved() {
        return involved;
    }


    public void setInvolved(boolean involved) {
        this.involved = involved;
    }

    public boolean isStep4_ok() {
        return step4_ok;
    }

    public void setStep4_ok(boolean step4_ok) {
        this.step4_ok = step4_ok;
    }

    public int getNbFound() {
        return nbFound;
    }

    public void setNbFound(int nbFound) {
        this.nbFound = nbFound;
    }

    public String getPathWrongGeoIssue() {
        return pathWrongGeoIssue;
    }

    public void setPathWrongGeoIssue(String pathWrongGeoIssue) {
        this.pathWrongGeoIssue = pathWrongGeoIssue;
    }

    
}

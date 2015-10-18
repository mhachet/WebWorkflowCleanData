/**
 * fr.bird.bloom.beans
 * Step3_CheckCoordinates
 */
package fr.bird.bloom.beans;

/**
 * fr.bird.bloom.beans
 * 
 * Step3_CheckCoordinates.java
 * Step3_CheckCoordinates
 */
public class Step3_CheckCoordinates {
    private boolean step3_ok = true;
    private int nbFound = 0;
    private boolean involved = true;
    private String pathWrongCoordinates = "";   
    
    /**
     * fr.bird.bloom.beans
     * Step3_CheckCoordinates
     */
    public Step3_CheckCoordinates() {

    }

    public boolean isInvolved() {
        return involved;
    }

    public void setInvolved(boolean involved) {
        this.involved = involved;
    }

    public boolean isStep3_ok() {
        return step3_ok;
    }

    public void setStep3_ok(boolean step3_ok) {
        this.step3_ok = step3_ok;
    }

    public int getNbFound() {
        return nbFound;
    }

    public void setNbFound(int nbFound) {
        this.nbFound = nbFound;
    }

    public String getPathWrongCoordinates() {
        return pathWrongCoordinates;
    }

    public void setPathWrongCoordinates(String pathWrongCoordinates) {
        this.pathWrongCoordinates = pathWrongCoordinates;
    }    
}

/**
 * fr.bird.bloom.beans
 * Step7_CheckISo2Coordinates
 */
package fr.bird.bloom.stepresults;

/**
 * fr.bird.bloom.beans
 * 
 * Step7_CheckISo2Coordinates.java
 * Step7_CheckISo2Coordinates
 */
public class Step7_CheckISo2Coordinates {

    private boolean step7_ok = true;
    private String pathWrongIso2 = "";
    private int nbFound = 0;
    private boolean involved = true;
    
    public Step7_CheckISo2Coordinates(){
	
    }

    public boolean isStep7_ok() {
        return step7_ok;
    }

    public void setStep7_ok(boolean step7_ok) {
        this.step7_ok = step7_ok;
    }

    public String getPathWrongIso2() {
        return pathWrongIso2;
    }

    public void setPathWrongIso2(String pathWrongIso2) {
        this.pathWrongIso2 = pathWrongIso2;
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

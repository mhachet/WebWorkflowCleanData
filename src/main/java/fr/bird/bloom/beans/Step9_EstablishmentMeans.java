/**
 * fr.bird.bloom.beans
 * Step9_EstablishmentMeans
 */
package fr.bird.bloom.beans;

/**
 * fr.bird.bloom.beans
 * 
 * Step9_EstablishmentMeans.java
 * Step9_EstablishmentMeans
 */
public class Step9_EstablishmentMeans {

    private boolean step9_ok = true;
    private boolean involved = false;
    private int nbFound = 0;
    private String pathWrongEstablishmentMeans = "";
    
    public Step9_EstablishmentMeans(){
	
    }

    public boolean isStep9_ok() {
        return step9_ok;
    }

    public void setStep9_ok(boolean step9_ok) {
        this.step9_ok = step9_ok;
    }

    public boolean isInvolved() {
        return involved;
    }

    public void setInvolved(boolean involved) {
        this.involved = involved;
    }

    public int getNbFound() {
        return nbFound;
    }

    public void setNbFound(int nbFound) {
        this.nbFound = nbFound;
    }

    public String getPathWrongEstablishmentMeans() {
        return pathWrongEstablishmentMeans;
    }

    public void setPathWrongEstablishmentMeans(String pathWrongEstablishmentMeans) {
        this.pathWrongEstablishmentMeans = pathWrongEstablishmentMeans;
    }
    
}

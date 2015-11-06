/**
 * @author mhachet
 */
package fr.bird.bloom.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.filter.expression.ThisPropertyAccessorFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * src.model
 * 
 * JSONobject.java
 */

public class JSONobject {

    private String DIRECTORY_PATH = "/home/mhachet/workspace/WebWorkflowCleanData/WebContent/output/"; 
    
    private ArrayList<String> presentTags;
    private ArrayList<String> dwcTags;
    private ArrayList<String> noMappedTags;
    private String uploadFilename;
    private JSONObject globalJsonObject;
    
    
    public JSONobject(String fileUploadName){
	this.uploadFilename = fileUploadName;
    }
    
    public File createJSONfile() throws IOException{
      globalJsonObject = new JSONObject();
      JSONArray globalArrayJSON = new JSONArray();
      
      JSONObject objectPresentTags = new JSONObject();
      objectPresentTags.put("presentTags", this.getPresentTags());
      
      JSONObject objectDwCTags = new JSONObject();
      objectDwCTags.put("DwCTags", this.getDwcTags());
      
      JSONObject objectNoMappedTags = new JSONObject();
      objectNoMappedTags.put("noMappedTags", this.getNoMappedTags());
      
      globalArrayJSON.add(objectPresentTags);
      globalArrayJSON.add(objectDwCTags);
      globalArrayJSON.add(objectNoMappedTags);
      
      globalJsonObject.put(this.getUploadFilename(), globalArrayJSON);
      
      File jsonFile = new File(DIRECTORY_PATH + "temp/data/" + this.getUploadFilename() + ".json");
      if (!jsonFile.exists()){
	  jsonFile.createNewFile();
      }
      FileWriter writer = new FileWriter(jsonFile);
      writer.write(globalJsonObject.toJSONString());
      writer.close();
      
      return jsonFile;
    }
    
    public JSONObject getGlobalJsonObject() {
        return globalJsonObject;
    }

    public void setGlobalJsonObject(JSONObject globalJsonObject) {
        this.globalJsonObject = globalJsonObject;
    }

    public ArrayList<String> getPresentTags() {
        return presentTags;
    }

    public void setPresentTags(ArrayList<String> presentTags) {
        this.presentTags = presentTags;
    }

    public ArrayList<String> getDwcTags() {
        return dwcTags;
    }

    public void setDwcTags(ArrayList<String> dwcTags) {
        this.dwcTags = dwcTags;
    }

    public ArrayList<String> getNoMappedTags() {
        return noMappedTags;
    }

    public void setNoMappedTags(ArrayList<String> noMappedTags) {
        this.noMappedTags = noMappedTags;
    }

    public String getUploadFilename() {
        return uploadFilename;
    }

    public void setUploadFilename(String uploadFilename) {
        this.uploadFilename = uploadFilename;
    }
    
    
    
    
}

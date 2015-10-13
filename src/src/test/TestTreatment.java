/**
 * src.test
 * TestPrepareTreatmentData
 */
package src.test;

import org.junit.BeforeClass;
import org.junit.Test;
import src.model.MappingDwC;
import src.model.Treatment;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.fail;
/**
 * src.test
 * 
 * TestPrepareTreatmentData.java
 * TestPrepareTreatmentData
 */
public class TestTreatment {

    private String DIRECTORY_PATH = "/home/mhachet/workspace/WebWorkflowCleanData/src/resources/test/";
    private String RESSOURCES_PATH = "/home/mhachet/workspace/WebWorkflowCleanData/src/resources/";
    private static Treatment treatment;
    private static PrepareTests prepareTest;
    private static String nbSessionRandom;
    
    @BeforeClass
    public static void beforeTestClass(){
	treatment = new Treatment();
	prepareTest = new PrepareTests();
	prepareTest.prepare();
	nbSessionRandom = "test_junit";
    }
    
    /**
     * Test method for {@link src.model.Treatment#mappingDwC(src.model.MappingDwC)}.
     */
    @Test
    public void testMappingDwC() {
	ArrayList<MappingDwC> listMappingDWC = prepareTest.getListMappedDWC();
	File mappedFileTest1 = new File(DIRECTORY_PATH + "intermediate_files/mappedDWC_test1NoDWC.csv");
	for(int i = 0 ; i < listMappingDWC.size() ; i++){
	    MappingDwC mappingDwc = listMappingDWC.get(i);
	    boolean mapping = Boolean.parseBoolean(mappingDwc.getMappingInvolved());
	    if(mapping){		
		/*try {
		    treatment.mappingDwC(mappingDwc, );
		    File mappedFile = mappingDwc.getMappedFile();
		    HashMap<String, ArrayList<String>> testConnectionTagsValues = mappingDwc.getConnectionValuesTags();
		    HashMap<String, String> connectionTags = mappingDwc.getConnectionTags();
		    //System.out.println(connectionTags);
		    FileAssert.assertEquals("mapping ok", mappedFile, mappedFileTest1);
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}*/
	    }
	}
	    
	
	
		
    }

    /**
     * Test method for {@link src.model.Treatment#initialiseFile(java.io.File, int)}.
     */
    @Test
    public void testInitialiseFile() {
	fail("Not yet implemented");
    }

    /**
     * Test method for {@link src.model.Treatment#createTemporaryFile(java.util.List, int)}.
     */
    @Test
    public void testCreateTemporaryFile() {
	fail("Not yet implemented");
    }

    /**
     * Test method for {@link src.model.Treatment#createSQLInsert(java.io.File, java.util.List)}.
     */
    @Test
    public void testCreateSQLInsert() {
	fail("Not yet implemented");
    }

    /**
     * Test method for {@link src.model.Treatment#createTableDarwinCoreInput(java.lang.String)}.
     */
    @Test
    public void testCreateTableDarwinCoreInput() {
	fail("Not yet implemented");
    }

}

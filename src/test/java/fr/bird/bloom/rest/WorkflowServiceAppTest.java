package fr.bird.bloom.rest;

import fr.bird.bloom.dto.ServiceInput;
import fr.bird.bloom.dto.WorkflowResults;
import fr.bird.bloom.model.CSVFile;
import fr.bird.bloom.model.DwcHeaders;
import fr.bird.bloom.utils.BloomConfig;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static fr.bird.bloom.model.DwcHeaders.*;

@Ignore
public class WorkflowServiceAppTest {
    private WorkflowService service;


    // mock creation

    @Test
    public void testProcess() throws Exception {
        final WorkflowResults workflowResults = service.process(getInput());
        System.out.println(workflowResults);
    }

    private ServiceInput getInput() {
        Map<String, DwcHeaders> headerMap = new HashMap<>();
        headerMap.put("hasCoordinate", HAS_COORDINATE);
        headerMap.put("scientificName", SCIENTIFIC_NAME);
        headerMap.put("decimalLatitude", DECIMAL_LATITUDE);
        headerMap.put("decimalLongitude", DECIMAL_LONGITUDE);
        headerMap.put("hasGeospatialIssues", HAS_GEOSPATIAL_ISSUES);
        headerMap.put("gbifID", GBIF_ID);
        headerMap.put("locationID", LOCATION_ID);
        headerMap.put("countryCode", COUNTRY_CODE);
        headerMap.put("establishmentMeans", ESTABLISHMENT_MEANS);
        return new ServiceInput(
                false,
                false,
                false,
                false,
                1,
                false,
                "",
                CSVFile.Separator.COMMA,
                false,
                "https://www.dropbox.com/s/e0thy0dloxu9xs7/test_ipt_ok.csv?dl=1",
                false,
                headerMap);
    }

    @Before
    public void setUp() {
        service = new WorkflowService();
        BloomConfig.initializeDirectoryPath("./target/tempTest/");
    }
}
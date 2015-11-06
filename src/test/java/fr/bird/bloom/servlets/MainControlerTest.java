package fr.bird.bloom.servlets;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MainControlerTest {

    MainControler mainControler;

    @Test
    public void init_should_set_up_directory_and_resource_paths() throws Exception {
        mainControler.init();
        assertThat(mainControler.getDIRECTORY_PATH()).contains("/home/mhachet/workspace/WebWorkflowCleanData/WebContent/output/");
        assertThat(mainControler.getRESSOURCES_PATH()).contains("/home/mhachet/workspace/WebWorkflowCleanData/src/resources/");
    }

    @Before
    public void setUp() {
        mainControler = new MainControler();
    }

}
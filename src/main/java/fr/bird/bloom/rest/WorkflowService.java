package fr.bird.bloom.rest;


import fr.bird.bloom.dto.ServiceInput;
import fr.bird.bloom.services.LaunchWorkflow;
import fr.bird.bloom.utils.BloomUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/workflow")
public class WorkflowService {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String landing() {

        return "it works over GET";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String process(ServiceInput input) throws IOException {
        final String uuid = BloomUtils.generateUUID();

        LaunchWorkflow workflow = new LaunchWorkflow(input, uuid);

        workflow.executeWorkflow();

        return "it works over POST";
    }

}

package fr.bird.bloom.servlets;


import fr.bird.bloom.dto.ServiceInput;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/workflow")
public class WebServiceController {


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String landing(){

        return "it works over GET";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String process(ServiceInput input) {


        return "it works over POST";
    }

}

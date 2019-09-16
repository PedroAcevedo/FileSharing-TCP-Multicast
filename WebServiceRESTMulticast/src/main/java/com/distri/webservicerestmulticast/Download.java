/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.webservicerestmulticast;

import java.io.File;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author KonnerEL
 */
@Path("/download")
public class Download {
    
    
    @GET
    @Path("/service-record/{Filename}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile(@PathParam("Filename") String filename) {
      File file = new File("/home/pedross/NetBeansProjects/WebServiceRESTMulticast/src/main/java/com/distri/webservicerestmulticast/resources/" + filename);
      return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
          .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" ) //optional
          .build();
    }
    

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.webservicerestmulticast;

import java.io.File;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author KonnerEL
 */
@Path("/download")
public class Download {
    
    @Context ServletContext servletContext;
    public String path;
    
    @GET
    @Path("/service-record/{Filename}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile(@PathParam("Filename") String filename) {
      this.path = servletContext.getRealPath("/") + "../../src/main/java/com/distri/webservicerestmulticast/resources/";
      File file = new File( path + filename);
      long length = file.length();
      return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
          .header("Content-length", String.valueOf(length))
          .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
          .build();
    }
    

}

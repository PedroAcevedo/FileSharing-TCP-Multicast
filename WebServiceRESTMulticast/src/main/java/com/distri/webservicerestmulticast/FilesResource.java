/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.webservicerestmulticast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author pedross
 */
@Path("Files")
public class FilesResource {

    @Context ServletContext servletContext;
    private String path;
    File file;
    /**
     * Creates a new instance of FilesResource
     */
    public FilesResource() {
        
    }

    /**
     * Retrieves representation of an instance of com.distri.webservicerestmulticast.FilesResource
     * @return an instance of java.lang.String
     */
    
    public String getPath() {
        return path;
    }

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getFiles() {
        this.path = servletContext.getRealPath("/") + "../../src/main/java/com/distri/webservicerestmulticast/resources";
        File file = new File(path);
        return "{ \"Files\":" +  doListing(file) + "}";
    }

    
    public String doListing(File file) {
        File[] listOfFiles = file.listFiles();
        String Files = "";
        String Size = "";
        for (int i = 0; i < listOfFiles.length; i++) {
            Files = Files + "\"" +  listOfFiles[i].getName() + "\":" + listOfFiles[i].length() +","; 
        }
        Files = "{" + Files.substring(0, Files.length()-1) + "}";
        return Files;

    }
    
    /**
     * PUT method for updating or creating an instance of FilesResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.TEXT_HTML)
    public void putHtml(String content) {
    }
}

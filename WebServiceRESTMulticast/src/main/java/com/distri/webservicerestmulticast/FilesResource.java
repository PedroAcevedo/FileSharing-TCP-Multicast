/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.webservicerestmulticast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

    @Context
    private UriInfo context;
    private final String dirName = "/home/pedross/NetBeansProjects/WebServiceRESTMulticast/src/main/java/com/distri/webservicerestmulticast/resources/";
    File file= new File(dirName);;
    /**
     * Creates a new instance of FilesResource
     */
    public FilesResource() {
        
    }

    /**
     * Retrieves representation of an instance of com.distri.webservicerestmulticast.FilesResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getFiles() {
        
        return "{\"Files\":\"" + doListing(file) +"\"}";
    }

    
    public String doListing(File dirName) {
        File[] listOfFiles = file.listFiles();
        String Files = "";
        for (int i = 0; i < listOfFiles.length-1; i++) {
            Files = listOfFiles[i].getName() + "," + Files; 
        }
        Files = "[" + Files + listOfFiles[listOfFiles.length-1].getName() + "]";
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

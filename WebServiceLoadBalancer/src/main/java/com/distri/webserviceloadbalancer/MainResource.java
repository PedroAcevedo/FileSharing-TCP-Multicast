/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.webserviceloadbalancer;

import com.distri.webserviceloadbalancer.methods.RequestMethods;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author pedross
 */
@Path("Main")
public class MainResource {

    @Context
    private UriInfo context;
    private RequestMethods r = new RequestMethods();
    private Set<String> hosts;
    /**
     * Creates a new instance of MainResource
     */
    public MainResource() {
    }

    /**
     * Retrieves representation of an instance of com.distri.webserviceloadbalancer.MainResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        
        hosts = r.whoIsThere();//new ArrayList<>();//
        ArrayList<String> files = new ArrayList<>();
        for(String host : hosts){
            ArrayList<String> filesInHost = r.getAvailablesFiles(host);
            if (!filesInHost.isEmpty()) {
                if (files.isEmpty()) {
                    files = filesInHost;
                }else{
                    files = r.selectDistinct(files, r.getAvailablesFiles(host));
                }   
            } 
        }
        return "{\"Files\":\""+ files +"\"}";
    }
    
    @GET
    @Path("/download/{Filename}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFile(@PathParam("Filename") String filename) throws MalformedURLException, IOException {
        return r.failovering(filename);
    }
    
    /**
     * PUT method for updating or creating an instance of MainResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    
    
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.webserviceloadbalancer;

import com.distri.webserviceloadbalancer.methods.FilesList;
import com.distri.webserviceloadbalancer.methods.RequestMethods;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Set;
import javax.servlet.ServletContext;
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

    @Context ServletContext servletContext;
    private UriInfo context;
    private RequestMethods r = new RequestMethods();
    private Set<String> hosts;
    /**
     * Creates a new instance of MainResource
     */
    public MainResource() {
        System.out.println("HOLA");
    }

    /**
     * Retrieves representation of an instance of com.distri.webserviceloadbalancer.MainResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        hosts = r.whoIsThere(servletContext.getRealPath("/") + "/../../hosts.txt");//new ArrayList<>();//
        FilesList files = new FilesList();
        for(String host : hosts){
            Gson gson = new Gson();
            if (files.isEmpty()) {
                files = gson.fromJson(r.getAvailablesFiles(host), FilesList.class);
            }else{
                files.addNewFiles(r.getAvailablesFiles(host));
            }
        }
        return "{\"Files\":"+ files.toJson() +"}";
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

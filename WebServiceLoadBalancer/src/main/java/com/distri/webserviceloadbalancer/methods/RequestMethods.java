/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.webserviceloadbalancer.methods;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author pdacevedo
 */
public class RequestMethods {
    
    
    
    public static HashMap<String, Integer> serverWeightMap = new HashMap<String, Integer>();  
    HttpURLConnection urlConnection;
    String path;
    public void RequestMethods(){

    }


    public Set<String> whoIsThere(String path){
        this.path = path;
        try (BufferedReader br = new BufferedReader(new FileReader(this.path))) {

            String strCurrentLine;

            while ((strCurrentLine = br.readLine()) != null) {
               if (isReacheble(strCurrentLine)) {
                   serverWeightMap.put(strCurrentLine,10);
               }
            }
            updateHost();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverWeightMap.keySet();
    }
    
    public boolean isReacheble(String Host){
        try {
            String endpoint="http://"+Host+":8080/WebServiceRESTMulticast/";
            URL url=new URL(endpoint);
            urlConnection=(HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }
        } catch (IOException e) {
            System.err.println(e);
        }finally {
            urlConnection.disconnect();
        }    
        return false;
    }
    
    public void updateHost() throws IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (String host : serverWeightMap.keySet()){
                writer.write(host);
            }
            writer.close();
        }
    }
    
    public ArrayList<String> selectDistinct(ArrayList<String> A1, ArrayList<String> A2){
        
        for(String file : A2){
            if(!A1.contains(file)){
                A1.add(file);
            }
        }
        
        return A1;
    }
    
    public ArrayList<String> getAvailablesFiles(String host){
        ArrayList<String> files = new ArrayList<>();
        try {
            String endpoint="http://"+host+":8080/WebServiceRESTMulticast/webresources/Files";
            URL url=new URL(endpoint);
            urlConnection=(HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            int httpResponseCode=urlConnection.getResponseCode();
            if(httpResponseCode==HttpURLConnection.HTTP_OK){
                BufferedReader reader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String newLine="";
                Gson gson = new Gson();
                if((newLine=reader.readLine()) != null){
                    Properties filesIn = gson.fromJson(newLine, Properties.class);
                    String fileList = filesIn.getProperty("Files");
                    String[] archives = ((String)fileList.subSequence(1, fileList.length()-1)).split(",");
                    files = new ArrayList<>(Arrays.asList(archives));
                }
            }
            return files;
        } catch (Exception e) {
            System.out.println(e);
        }finally {
            urlConnection.disconnect();
        }       
        return files;
    }
     
    public Response getFileFromHost(String hostIP, String filename) throws MalformedURLException, IOException{
       String endpoint="http://"+hostIP+":8080/WebServiceRESTMulticast/webresources/download/service-record/" + filename;
       return Response.ok(new URL(endpoint).openStream(), MediaType.APPLICATION_OCTET_STREAM)
       .build(); 
    }
    
    public Response failovering(String filename) throws IOException{
        int requestedServers = 0;
        while(requestedServers < serverWeightMap.keySet().size()){
            Response r = getFileFromHost(RoundRobin.getServer(), filename);
            if(r.getStatus()==200){
                return r;
            }else{
                requestedServers++;
            }
        }
        return Response.status(Status.NOT_FOUND).build();
    }
    
}

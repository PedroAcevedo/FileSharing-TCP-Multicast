/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.webserviceclient;

import com.google.gson.Gson;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;
import javax.swing.DefaultListModel;

/**
 *
 * @author pedross
 */
public class Request {

    public static String URL;

    public Request() throws UnknownHostException {
        Request.URL = "http://"+InetAddress.getLocalHost().getHostAddress()
                +":8080/WebServiceLoadBalancer/webresources/Main";
    }
    
    public DefaultListModel filesAvailables() throws MalformedURLException, IOException{
        DefaultListModel listModel = new DefaultListModel();
        URL url=new URL(URL);
        HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            Gson gson = new Gson();
            while ((line = reader.readLine()) != null) {
                Properties filesIn = gson.fromJson(line, Properties.class);
                String fileList = filesIn.getProperty("Files");
          
                String[] archives = ((String)fileList.subSequence(1, fileList.length()-1)).split(",");
                for(String archive : archives){
                    listModel.addElement(archive.replace(" ", ""));
                }
            }
        }
        return listModel;
    }    
    
    public String downloadFile(String filename){     
        try (BufferedInputStream in = new BufferedInputStream(new URL(Request.URL + "/download/" + filename).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream("/home/pedross/NetBeansProjects/WebServiceClient/src/" + filename)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
               fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
              return "Se descargo el archivo: " + filename;
        } catch (IOException e) {
            System.out.println(e);
        }
        return "Error en descarga";
    }

    
}

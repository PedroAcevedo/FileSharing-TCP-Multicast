/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.webserviceclient;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 *
 * @author pedross
 */
public class Downloading extends Thread{
    
  JProgressBar progressBar;
  JTextArea jText;
  private long increment = 0;
  private String filename;
  
  public Downloading(JProgressBar bar,String filename) {
    this.progressBar = bar;
    this.filename = filename;
  }
  
  public Downloading(JTextArea jText, String filename){
      this.jText = jText;
      this.filename = filename;
  }

  @Override
  public void run() {
    this.jText.setText( this.jText.getText() + "\n" + downloadFiles(this.filename));
  }
  
    public void downloadFile(String filename) throws MalformedURLException, IOException{     
      
        try{
        URL url = new URL(Request.URL + "/download/" + filename);
        HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
        urlConnection.connect();
        if(urlConnection.getResponseCode() / 100 == 2){
            float contentLength = urlConnection.getContentLengthLong(); 
            increment = (long) ((100*(contentLength/1024))/contentLength);
            BufferedInputStream in = new BufferedInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream("/home/pedross/NetBeansProjects/WebServiceClient/src/" + filename);
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
               fileOutputStream.write(dataBuffer, 0, bytesRead);
               int value = this.progressBar.getValue();
               this.progressBar.setValue(value + (int)increment);
               
            }
        }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public String downloadFiles(String filename){     
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

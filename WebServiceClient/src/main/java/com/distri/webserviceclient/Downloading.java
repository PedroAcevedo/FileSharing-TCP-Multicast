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
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author pedross
 */
public class Downloading extends Thread{
    
    JProgressBar progressBar;
    JTextArea jText;
    private double increment;
    private String filename;
    private double size;
    double d = (double)(1024*100);
    HttpURLConnection urlConnection;


    public Downloading(JProgressBar bar,String filename, double size) {
      this.progressBar = bar;
      this.filename = filename;
      this.size = size;
      System.out.println(size);
      this.increment = (double)(d/size);
    }

    public Downloading(JTextArea jText, String filename){
        this.jText = jText;
        this.filename = filename;
    }

    @Override
    public void run() {
//      try {
//          downloadFile(filename);
//      } catch (IOException ex) {
//          Logger.getLogger(Downloading.class.getName()).log(Level.SEVERE, null, ex);
//      }    
        String file = downloadFiles(filename);
        jText.setText(jText.getText() + "\n" + file);

    }
    
    
    
    public synchronized void RefreshProgress(long value)   {
        
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                if (this == null) return;
                increment = (double)increment*(value);
                progressBar.setValue((int)increment);
                System.out.println(increment);
              }
            });
            return;
        }
    }
  
    public void downloadFile(String filename) throws MalformedURLException, IOException{     
      
        try{
        URL url = new URL(Request.URL + "/download/" + filename);
        urlConnection=(HttpURLConnection) url.openConnection();
        urlConnection.connect();
        if(urlConnection.getResponseCode() / 100 == 2){
            //
            BufferedInputStream in = new BufferedInputStream(url.openStream());
            //System.out.println(urlConnection.getHeaderField("Content-length"));
            FileOutputStream fileOutputStream = new FileOutputStream("/home/pedross/Documents/Repositories/FileSharing-TCP-Multicast/WebServiceClient/" + filename);
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            long i = 0;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
               fileOutputStream.write(dataBuffer, 0, bytesRead);
               this.RefreshProgress(i);
               i++;
            }
        }
        } catch (IOException e) {
            System.out.println(e);
        }
        finally {
            urlConnection.disconnect();
        }    
    }
    
    public String downloadFiles(String filename){     
        try (BufferedInputStream in = new BufferedInputStream(new URL(Request.URL + "/download/" + filename).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream( System.getProperty("user.dir") + "/resources/" + filename)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
               fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
              return "Se descargo el archivo: " + filename + " con tamaño de: " + fileOutputStream.getChannel().size() + " Bytes";
        } catch (IOException e) {
            System.out.println(e);
        } 
        return "Error en descarga del archivo: " + filename;
    }
}

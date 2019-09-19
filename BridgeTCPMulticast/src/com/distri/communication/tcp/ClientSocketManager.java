/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.communication.tcp;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 *
 * @author eduar
 */
public class ClientSocketManager {
    
    private File file;
    private String serverIpAddress;
    private int port;
    private int MTU;

    public ClientSocketManager(File file, String serverIpAddress, int port, int MTU) {
        this.file = file;
        this.serverIpAddress = serverIpAddress;
        this.port = port;
        this.MTU = MTU;
    }
    
    public void uploadFile() {
        try {
            System.out.println("Uploading file...");
            
            Socket clientSocket = new Socket(serverIpAddress, port);
            
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    clientSocket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    clientSocket.getOutputStream());
            
            String fileName = file.getName();
            objectOutputStream.writeObject(fileName);
            
            String header = padding(fileName);
            
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[MTU-100];
            
            Integer bytesRead = 0;
            while((bytesRead = fileInputStream.read(buffer)) > 0) {
                objectOutputStream.writeObject((bytesRead + 100));
                byte[] dataToBeSent = concatenate(header.getBytes(), buffer);
                objectOutputStream.writeObject(Arrays.copyOf(dataToBeSent, dataToBeSent.length));
            }
            
            objectOutputStream.close();
            objectInputStream.close();
            fileInputStream.close();
            
            System.out.println("File uploaded successfully!");
            System.exit(0);
        }catch (Exception ex) {
            System.err.println(ex);
        }
    }
    
    private byte[] concatenate(byte[] header, byte[] data) {
        byte[] out = new byte[header.length + data.length];
        System.arraycopy(header, 0, out, 0, header.length);
        System.arraycopy(data, 0, out, header.length, data.length);  
        return out;
    }
    
    private String padding(String name) {
        String out = name;
        out += "/";
        while(out.length() < 100) {
            out += ".";
        }
        return out;
    }
    
}

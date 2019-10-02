/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.communication.tcp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    public synchronized void uploadFile(File file){
        try{
            System.out.println("uploading file..." + file.getName());
            Socket clientSocket = new Socket(serverIpAddress, port);
            
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    clientSocket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    clientSocket.getOutputStream());
            
            String fileHeader = padding(file.getName().strip());
            objectOutputStream.writeObject(fileHeader);
            objectOutputStream.flush();
            FileInputStream fileInputStream = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            DigestInputStream dis = new DigestInputStream(fileInputStream,md);
            byte[] buffer = new byte[MTU-100];
            int bytesRead = 0;
            //int packetCounter = 0;
            int aux = 0;
            while((bytesRead = dis.read(buffer)) != -1) {
                if(bytesRead > 0){
                    aux = bytesRead;
                }
                byte[] dataToBeSent = concatenate(fileHeader.getBytes(), buffer);
                objectOutputStream.writeObject(Arrays.copyOf(dataToBeSent, dataToBeSent.length));
                objectOutputStream.flush();
                wait(8);
            }
            System.out.println("SHA-256 : " + bytesToHex(md.digest()));
            objectOutputStream.writeObject("EOF/"+ fileHeader.split("/")[0] + "/" + aux + "/");
            objectOutputStream.close();
            objectInputStream.close();
            dis.close();
            fileInputStream.close();
            
            System.out.println("File uploaded successfully!... or not?");
            System.exit(0);
            
        }catch(IOException e){
            System.err.println(e.getMessage());
        } catch (NoSuchAlgorithmException | InterruptedException ex) {
            Logger.getLogger(ClientSocketManager.class.getName()).log(Level.SEVERE, null, ex);
        } 
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

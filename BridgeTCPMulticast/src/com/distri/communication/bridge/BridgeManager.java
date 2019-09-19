/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.communication.bridge;

import com.distri.communication.multicast.MulticastManager;
import com.distri.communication.multicast.MulticastManagerCallerInterface;
import com.distri.communication.tcp.TCPServiceManager;
import com.distri.communication.tcp.TCPServiceManagerCallerInterface;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author eduar
 */
public class BridgeManager implements TCPServiceManagerCallerInterface, MulticastManagerCallerInterface {
    
    public static int MTU;
    
    TCPServiceManager tcpServiceManager;
    MulticastManager multicastManager;
    
    ArrayList<byte[]> dataToBeSent;
    
    public BridgeManager() {
        try {
            BridgeManager.MTU = Integer.parseInt(
                    (new BufferedReader(new FileReader(
                            Paths.get("src\\com\\distri\\resources\\config\\MTU.config").toAbsolutePath().toString())
                    )).readLine()
            );
            this.tcpServiceManager = new TCPServiceManager(this);
            this.multicastManager = new MulticastManager("224.0.0.1", 9091, this, BridgeManager.MTU);
            sendString("C0/"+ BridgeManager.MTU+"/basura");
        }catch (Exception ex) {
            System.err.println(ex);
        }
    }
    
    public static void main(String[] args) {
        new BridgeManager();
    }

    @Override
    public void reSendFileReceivedFromClient(Socket clientSocket) {
        try {
            dataToBeSent = new ArrayList<>();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            
            byte[] buffer = new byte[BridgeManager.MTU];

            Object object = objectInputStream.readObject();
            
            String nameFile = "";
            if(object instanceof String) {
                nameFile = object.toString();
            }else {
                errorOnTCPServiceManager(new Exception("Something is wrong"));
            }

            int dataCounter = 0;
            Integer bytesRead = 0;

            do {
                object = objectInputStream.readObject();
                if (!(object instanceof Integer)) {
                    errorOnTCPServiceManager(new Exception("Something is wrong"));
                }
                bytesRead = (Integer) object;
                object = objectInputStream.readObject();
                if (!(object instanceof byte[])) {
                    errorOnTCPServiceManager(new Exception("Something is wrong"));
                }
                buffer = (byte[]) object;
                dataToBeSent.add(Arrays.copyOf(buffer, bytesRead));
                dataCounter++;
            }while (bytesRead == BridgeManager.MTU);
            
            String controlString = "P0/" + nameFile + "/" + dataCounter + "/" + bytesRead + "/";
            multicastManager.sendData(controlString.getBytes());
            
            for (byte[] data : dataToBeSent) {
                multicastManager.sendData(data);
            }
            
            System.out.println("File: " + nameFile + " resent successfully!");
            
            objectInputStream.close();
            objectOutputStream.close();
        }catch (Exception ex) {
            errorOnTCPServiceManager(ex);
        }
    }

    @Override
    public void errorOnTCPServiceManager(Exception ex) {
        System.err.println(ex);
    }

    @Override
    public void dataReceived(String sourceIpAddressOrHost, int sourcePort, byte[] data) {
        
    }

    @Override
    public void errorOnMulticastManager(Exception ex) {
        System.err.println(ex);
    }

    @Override
    public void sendString(String data) {
        multicastManager.sendData(data.getBytes());
    }
    
}

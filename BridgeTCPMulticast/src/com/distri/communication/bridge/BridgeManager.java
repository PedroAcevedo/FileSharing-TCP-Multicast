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
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author eduar
 */
public class BridgeManager implements TCPServiceManagerCallerInterface, MulticastManagerCallerInterface {
    
    public static int MTU;
    private final String CONFIG_PATH="src/com/distri/resources/config/Bridge.config";
    private final String HOSTS_PATH="../WebServiceLoadBalancer/hosts.txt";
    TCPServiceManager tcpServiceManager;
    MulticastManager multicastManager;
    ArrayList<byte[]> dataToBeSent;
    
    public BridgeManager() {
        try {
            BridgeManager.MTU = Integer.parseInt(readConfigFile("MTU",CONFIG_PATH));
            this.tcpServiceManager = new TCPServiceManager(this);
            this.multicastManager = new MulticastManager(readConfigFile("MulticastNetworkIP",CONFIG_PATH), 
                    Integer.parseInt(readConfigFile("Port", CONFIG_PATH)), this, BridgeManager.MTU);
            //sendString("C0/"+ BridgeManager.MTU+"/basura");
        }catch (NumberFormatException ex) {
            System.err.println(ex);
        }
    }
    
    @Override
    public void reSend(Socket clientSocket){
        String nameFile = "";
        Object object = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try{
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            object = objectInputStream.readObject();
            if(object instanceof String) {
                nameFile = object.toString().split("/")[0];
                multicastManager.sendData(("NEW/"+nameFile + "/").getBytes());
                System.out.println("receiving file...");
            }
            while(true){
                object = objectInputStream.readObject();
                if(object instanceof byte[]){
                    String aux = new String((byte[])object);
                    multicastManager.sendData(Arrays.copyOf((byte[])object, ((byte[])object).length));
                }else if(object instanceof String){
                    multicastManager.sendData(((String) object).getBytes());
                    objectOutputStream.close();
                    objectInputStream.close();
                    System.out.println("File: " + nameFile + " resent successfully!");
                    return;
                }
            }
        }catch(IOException | ClassNotFoundException e){
            System.err.println(e.getMessage());
        }
        
    }
    
    public static void main(String[] args) {
        new BridgeManager();
    }
    
    private String readConfigFile(String parameter, String PATH){
        try (BufferedReader br = new BufferedReader(new FileReader(PATH))) {
            String strCurrentLine;
            while ((strCurrentLine = br.readLine()) != null) {
                if (strCurrentLine.startsWith(parameter)){
                    return strCurrentLine.split("=")[1];
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
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
        String controlString = new String(data);
        String[] controlData = controlString.split("/");
        
        if(controlData[0].equals("HI")) {
            System.out.println(controlData[1] + " joined multicast group");
            try (BufferedReader br = new BufferedReader(new FileReader(HOSTS_PATH))) {

                String strCurrentLine;
                while ((strCurrentLine = br.readLine()) != null) {
                    if (strCurrentLine.equals(controlData[1])){
                        return;
                    }
                }
            } catch (IOException e) {
                System.err.println(e);
            }
            try(FileWriter fw = new FileWriter("../WebServiceLoadBalancer/hosts.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                out.println(controlData[1]);
                out.close();
                fw.close();
                System.out.println(controlData[1] + " added to the hosts.txt file");
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    @Override
    public void errorOnMulticastManager(Exception ex) {
        System.err.println(ex.getMessage());
    }

    @Override
    public void sendString(String data) {
        multicastManager.sendData(data.getBytes());
    }

    
    
}

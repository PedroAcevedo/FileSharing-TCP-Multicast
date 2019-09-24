/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.communication.multicast;

import com.distri.communication.tcp.TCPServiceManager;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

/**
 *
 * @author eduar
 */
public class MulticastManager extends Thread {
    
    MulticastSocket multicastSocket;
    private final String ipAddress;
    private final int port;
    private final MulticastManagerCallerInterface caller;
    private final boolean isEnable = true;
    private final int MTU;
    
    public MulticastManager(String ipAddress, int port, MulticastManagerCallerInterface caller, int MTU) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.caller = caller;
        this.MTU = MTU;
        this.start();
    }
    
    private boolean initializeMulticastSocket() {
        try {
            multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(InetAddress.getByName(ipAddress));
            System.out.println("Join Multicast Group on " + ipAddress + ":" + port);
            return true;
        }catch (IOException ex) {
            caller.errorOnMulticastManager(ex);
        }
        return false;
    }
    
    @Override
    public void run() {
        DatagramPacket datagramPacket = new DatagramPacket(
                new byte[MTU], MTU);
        if(initializeMulticastSocket()){
            while(isEnable){
                try {
                    multicastSocket.receive(datagramPacket);
                    caller.dataReceived(datagramPacket.getAddress().toString(), 
                            datagramPacket.getPort(), datagramPacket.getData());
                }catch (IOException ex) {
                    caller.errorOnMulticastManager(ex);
                }
            }
        }
    }
    
    public void sendData(byte[] payload) {
        try {
            DatagramPacket outgoingPacket = new DatagramPacket(payload, payload.length);
            outgoingPacket.setAddress(InetAddress.getByName(ipAddress));
            outgoingPacket.setPort(port);
            outgoingPacket.setData(payload);
            multicastSocket.send(outgoingPacket);
            Thread.sleep(4);
        }catch (IOException | InterruptedException ex) {
            caller.errorOnMulticastManager(ex);
        }
    }
    
}

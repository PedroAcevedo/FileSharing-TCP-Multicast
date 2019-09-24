/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.communication.tcp;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author eduar
 */
public class TCPServiceManager extends Thread {
    
    public static final int PORT = 9090;
    
    ServerSocket serverSocket;
    private TCPServiceManagerCallerInterface caller;
    boolean isEnable = true;
    
    public TCPServiceManager(TCPServiceManagerCallerInterface caller) {
        this.caller = caller;
        this.start();
    }
    
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is up...");
            while(isEnable) {
                Socket clientSocket = serverSocket.accept();
                caller.reSend(clientSocket);
            }
        }catch (Exception ex) {
            caller.errorOnTCPServiceManager(ex);
        }
    }
    
}

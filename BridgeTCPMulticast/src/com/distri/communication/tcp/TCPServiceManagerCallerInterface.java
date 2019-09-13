/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.communication.tcp;

import java.net.Socket;

/**
 *
 * @author eduar
 */
public interface TCPServiceManagerCallerInterface {
    
    void reSendFileReceivedFromClient(Socket clientSocket);
    void errorOnTCPServiceManager(Exception ex);
    
}

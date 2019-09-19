/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.communication.multicast;

/**
 *
 * @author eduar
 */
public interface MulticastManagerCallerInterface {
    
    void dataReceived(String sourceIpAddressOrHost, int sourcePort, byte[] data);
    void errorOnMulticastManager(Exception ex);
    void sendString(String data);
    
}

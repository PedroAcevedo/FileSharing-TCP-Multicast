/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.webserviceloadbalancer.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author pedross
 */
public class RoundRobin   {   
    private static Integer pos = 0;   
       
    public static String getServer()   
    {   
        Map<String, Integer> serverMap =    
                new HashMap<>();   
        serverMap.putAll(RequestMethods.serverWeightMap);   

        // Get the Ip address List 
        Set<String> keySet = serverMap.keySet();   
        ArrayList<String> keyList = new ArrayList<>();   
        keyList.addAll(keySet);   

        String server = null;   
        synchronized (pos)   
        {   
            if (pos >= keySet.size())   
                pos = 0;   
            server = keyList.get(pos);   
            pos ++;   
        }   
        
        return server;   
    }   
}

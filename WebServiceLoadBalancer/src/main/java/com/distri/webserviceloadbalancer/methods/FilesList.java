/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distri.webserviceloadbalancer.methods;

import com.google.gson.Gson;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author pedross
 */
public class FilesList {
    
    private Map<String, Long> Files;
    Gson gson = new Gson();
    
    public Set<String> getFiles(){
        return Files.keySet();
    }
    
    public boolean isEmpty(){
        return Files == null;
    }
    
    public void addNewFiles(String fileList){
        FilesList map = gson.fromJson(fileList, FilesList.class);
        Iterator i = map.Files.keySet().iterator();
        while(i.hasNext()){
            String file = i.next().toString();
            if (!Files.keySet().contains(file)) {
                Files.put(file,(long)map.Files.get(file));
            }
        }
    }
    
    public String toJson(){
        String json = "{";
        Iterator i = Files.keySet().iterator();
        while(i.hasNext()){
            String file = i.next().toString();
            json = json + "\"" + file + "\":" + Files.get(file) + ",";
        }
        json = json.subSequence(0, json.length()-1) + "}";
        return json;
    }
    
}

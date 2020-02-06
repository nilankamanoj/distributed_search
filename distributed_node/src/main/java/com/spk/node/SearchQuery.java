package com.spk.node;

public class SearchQuery {
    String message;
    Node sender;
    String uuid;

    public SearchQuery(Node sender, String msg, String uuid) {
        this.sender = sender;
        this.message = msg;
        this.uuid = uuid;
    }
    public String getHash(){
        String response = sender.ip+"_"+sender.port+":";
        for(String s: message.split("\\s+")){
            response+= s+"_";
        }
        return response.substring(0, response.length() - 1)+":"+uuid;
    }
}


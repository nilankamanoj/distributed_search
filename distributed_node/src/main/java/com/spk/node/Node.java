package com.spk.node;

public class Node {
    String ip;
    int port;

    public Node(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public String getIp(){
        return this.ip;
    }

    public int getPort(){
        return this.port;
    }

    public String getHttpUrl(){
        return "http://"+ip+":"+port;
    }
}

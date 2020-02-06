package com.spk.node;

import java.io.IOException;
import java.net.*;

public class UdpClient extends AbstractClient {

    private DatagramSocket recSocket = null;

    private UdpClient(String[] args) {
        String bs_ip = args[0];
        int bsPort = Integer.valueOf(args[1]);
        this.bs = new Node(bs_ip, bsPort);
        this.ip = args[2];
        this.receivePort = Integer.valueOf(args[3]);
        this.sendPort = Integer.valueOf(args[4]);
        this.username = args[5];
    }


    @Override
    protected void startListening() throws SocketException {
        log("Send Port", ip + ":" + sendPort);
        recSocket = new DatagramSocket(receivePort);
        recSocket.setSoTimeout(1000);
        log("UdpClient listening at", ip + ":" + receivePort);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    byte[] buffer = new byte[65536];
                    DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
                    try {
                        recSocket.receive(incomingPacket);
                        byte[] data = incomingPacket.getData();
                        String s = new String(data, 0, incomingPacket.getLength());
                        String reply = parseMessage(s);
                        if (reply != null)
                            synchronized (knownNodes) {
                                send(reply, new Node(incomingPacket.getAddress().toString().substring(1), incomingPacket.getPort()));
                            }
                    } catch (SocketTimeoutException e) {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                recSocket.close();
            }
        });
        t.start();
    }

    @Override
    protected String sendAndReceive(String msg, Node node) throws IOException {
        return sendReceiveUdp( msg, node);
    }

    protected void send(String msg, Node node) throws IOException {
        UDPSend(msg,node);
    }


    public static AbstractClient fromArgs(String[] args) {
        return new UdpClient(args);
    }


}

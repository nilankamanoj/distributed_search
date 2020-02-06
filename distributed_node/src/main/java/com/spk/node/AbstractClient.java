package com.spk.node;


import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.InetAddress;
import java.util.*;
import java.util.function.Predicate;
import java.util.UUID;


public abstract class AbstractClient {


    protected Node bs;
    protected String ip;
    protected int receivePort;
    protected int sendPort;
    protected String[] files;
    protected String username;
    protected volatile boolean running;
    protected ArrayList<Node> knownNodes;

    HashMap<String, Long> passedQueries = new HashMap<>();
    HashMap<String, List<String>> queryResults = new HashMap<>();
    HashMap<String, Long> receivedHeartBeats = new HashMap<>();

    protected abstract void startListening() throws SocketException;

    protected abstract void send(String msg, Node node) throws Exception;

    protected abstract String sendAndReceive(String msg, Node node) throws Exception;

    public static void log(String msg) {
        System.out.println(msg);
    }

    public static void log(String info, String msg) {
        System.out.println(info + ": " + msg);
    }


    // print client info
    protected void printClientInfo() {
        log("Bootstap", bs.ip + ":" + bs.port);
        log("receive port", ip + ":" + receivePort);
        log("Send Port", ip + ":" + sendPort);
    }

    protected void start() {
        try {
            running = true;
            printClientInfo();
            startListening();
            joinToBootstrapServer();
            startHeartBeat();

            log("Joining to the network using known nodes");
            for (Node node : knownNodes) {
                String join_msg = CommandOpCodes.JOIN + " " + ip + " " + receivePort;
                String join_response = sendAndReceive(join_msg, node);
                log("join response:" + join_response);
            }

            BufferedReader bufferedReader = null;
            while (running) {
                bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                String l = bufferedReader.readLine();
                if (l.equals(">leave")) {
                    running = !running;
                }
                else if(l.equals(">results")){
                    if(queryResults != null){
                        for(Map.Entry m:queryResults.entrySet()){
                            log((String) m.getKey());
                            for(String result:(ArrayList<String>) m.getValue()){
                                log(">>>"+result);
                            }
                            log("");
                        }
                    }
                    continue;
                }
                else if (l.equals(">nodes")) {
                    // for debugging purposes
                    if (knownNodes != null) {
                        for (Node node : knownNodes) {
                            log(node.ip + ":" + node.port);
                        }
                    }
                    continue;
                } else if (l.length() > 0) {
                    startSearch(l);
                }
            }

            //Leaving the network
            String unRegMsg = CommandOpCodes.UNREG+" " + ip + " " + receivePort + " " + username;
            UDPSend(unRegMsg, bs);
            bufferedReader.close();

        } catch (Exception e) {
            System.err.println("Exception " + e);
        } finally {
            if (running == true) {
                running = false;
            }
        }
    }

    public void startSearch(String searchText) throws Exception {
        UUID uuid = UUID.randomUUID();
        for (Node node : knownNodes) {
            String searchMsg = CommandOpCodes.SEARCH+" " + uuid + " " + ip + " " + receivePort + " " + "\"" + searchText + "\"" + " " + "1";
            send(searchMsg, node);
        }
    }

   public void setFiles(String[] files) {
        this.files = files;
    }

    protected void joinToBootstrapServer() throws IOException {
        String regMsg = CommandOpCodes.REG+" " + ip + " " + receivePort + " " + username;
        String regReply = sendReceiveUdp(regMsg, bs);
        knownNodes = parseContactMessage(regReply);
        log("Known Nodes:");
        for (Node node : knownNodes) {
            log(node.ip + ":" + node.port);
        }
    }

    protected String parseMessage(String msg) throws Exception {
        StringTokenizer tokenizer = new StringTokenizer(msg, " ");
        tokenizer.nextToken();
        String command = tokenizer.nextToken();

        synchronized (knownNodes){
            if (command.equals(CommandOpCodes.JOIN)) {
                return join(tokenizer);
            } else if (command.equals(CommandOpCodes.SEARCH)) {
                return search(tokenizer);
            } else if (command.equals(CommandOpCodes.LEAVE)) {
                return leave(tokenizer);
            } else if (command.equals(CommandOpCodes.SEARCH_OK)) {
                return processSearchResult(tokenizer);
            } else if (command.equals(CommandOpCodes.ALIVE)) {
                return processHeartBeat(tokenizer);
            } else if (command.equals(CommandOpCodes.REQUEST_CONTACTS)) {
                return processContactsRequest(tokenizer);
            } else if (command.equals(CommandOpCodes.REQUEST_CONTACTS_OK)) {
                return processContactsReply(msg);
            }
        }
        return null;
    }

    protected List<String> search(String msg) {

        List<String> filesFound = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(msg, " ");

        while (tokenizer.hasMoreTokens()) {
            String value = tokenizer.nextToken();
            for (String s : files) {
                if (s.toLowerCase().contains(value.toLowerCase())) {
                    filesFound.add(s);
                }
            }
        }

        Set setOfFiles = new HashSet(filesFound);
        List<String> fileNames = new ArrayList<String>();

        for (Object i : setOfFiles) {
            String x = i.toString().replaceAll(" ", "_");
            fileNames.add(x);
        }

        return fileNames;
    }

    protected String sendReceiveUdp(String msg, Node node) throws IOException {
        msg = lengthAdd(msg);
        log("Send and relieve(to:" + node.ip + ":" + node.port + ")", msg);
        DatagramSocket socket = new DatagramSocket(sendPort);
        InetAddress address = InetAddress.getByName(node.ip);
        byte[] buffer = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, node.port);
        socket.send(packet);

        buffer = new byte[65536];
        String s;
        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
        socket.receive(incoming);
        byte[] data = incoming.getData();
        s = new String(data, 0, incoming.getLength());

        socket.close();
        return s;
    }


    protected void UDPSend(String msg, Node node) throws IOException {

        synchronized (this) {
            msg = lengthAdd(msg);
            DatagramSocket sock = new DatagramSocket(sendPort);
            InetAddress node_address = InetAddress.getByName(node.ip);
            byte[] buffer = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, node_address, node.port);
            sock.send(packet);
            sock.close();
        }

    }

    protected static String lengthAdd(String msg) {
        if (msg.length() > 9000)
            return "0004";
        int len = msg.length() + 5;
        msg = len + " " + msg;
        while (msg.length() < len) {
            msg = "0" + msg;
        }
        return msg;
    }

    protected static ArrayList<Node> parseContactMessage(String msg) {
        if (msg == null)
            return new ArrayList<>();
        String[] parts = msg.split(" ");
        ArrayList<Node> nodes = new ArrayList<>();
        if (parts.length > 3) {
            for (int i = 3; i < parts.length; i += 2) {
                nodes.add(new Node(parts[i], Integer.valueOf(parts[i + 1])));
            }
        }
        return nodes;
    }

    protected void startHeartBeat() {
        log("starting heart beat of Node");
        Thread t1 = new Thread()
        {
            public void run()
            {
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while(running) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                          e.printStackTrace();
                    }
                    synchronized(knownNodes) {
                        Iterator<Node> iterator = knownNodes.iterator();
                        while (iterator.hasNext()) {
                            Node node = iterator.next();
                            Long timeStamp = System.currentTimeMillis();
                            if (receivedHeartBeats.get(node.getHttpUrl()) != null) {
                                if (timeStamp - receivedHeartBeats.get(node.getHttpUrl()) > 15000) {
                                    log("Removing Node",node.getIp() + " " + node.getPort());
                                    iterator.remove();
                                }
                            }
                        }
                        for (Node node : knownNodes) {
                            String heartBeatMsg = CommandOpCodes.ALIVE + " " + ip + " " + receivePort;
                            try {
                                send(heartBeatMsg, node);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (knownNodes.size() < 3){
                            String contactRequestMsg = CommandOpCodes.REQUEST_CONTACTS + " " + ip + " " + receivePort;
                            for (Node node : knownNodes) {
                                try {
                                    send(contactRequestMsg, node);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        };
        t1.start();
    }

    protected String join(StringTokenizer st) throws IOException {
        boolean isOkay = true;
        String reply = CommandOpCodes.JOIN_OK + " ";
        Node joiner = null;

        String ip = st.nextToken();
        int port = Integer.parseInt(st.nextToken());

        for (int i = 0; i < knownNodes.size(); i++) {
            if (knownNodes.get(i).getIp().equals(ip) && knownNodes.get(i).getPort() == port) {
                reply += "9999";
                isOkay = false;
            }
        }

        if (isOkay) {
            joiner = new Node(ip, port);
            knownNodes.add(joiner);
            reply += "0";
            return reply;
        }
        return null;
    }

    protected String search(StringTokenizer st) throws Exception {
        boolean isOkay;
        Node sender;

        String uuid = st.nextToken();
        String ip = st.nextToken();
        int port = Integer.parseInt(st.nextToken());

        String query = "";

        while (st.hasMoreTokens()) {
            String value = st.nextToken();
            Character lastChar = value.charAt(value.length() - 1);
            if (lastChar.equals('\"')) {
                value = value.substring(0, value.length() - 1);
                query = query + value;
                break;
            } else {
                query = query + value + " ";
            }
        }

        int hops = Integer.parseInt(st.nextToken());
        String searchQuery = query.substring(1, query.length());

        SearchQuery q = new SearchQuery(new Node(ip, port), searchQuery, uuid);
        Long millis = System.currentTimeMillis();


        for(Iterator<Map.Entry<String, Long>> it = passedQueries.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Long> entry = it.next();
            if(entry.getValue() < millis - 50000) {
                it.remove();
            }
        }
        if (passedQueries.containsKey(q.getHash()))
            return null;
        passedQueries.put(q.getHash(), millis);

        List<String> results = search(searchQuery);

        String reply = CommandOpCodes.SEARCH_OK + " " + q.getHash() + " " + this.ip + " " + this.receivePort + " ";
        if (results.isEmpty()) {
            reply += "0";
            isOkay = true;
        } else {
            reply += results.size() + " ";
            for (Object fileName : results) {
                reply += fileName.toString() + " ";
            }
            isOkay = true;
        }

        if (isOkay) {
            log(ip + " " + port);
            sender = new Node(ip, port);
            send(reply, sender);
        }

        hops++;
        if (hops < 15) {
            for (Node node : knownNodes) {
                String search_msg = "SER " + uuid + " " + ip + " " + port + " " + "\"" + searchQuery + "\"" + " " + hops;
                send(search_msg, node);
            }
        }
        return null;
    }

    protected String processSearchResult(StringTokenizer st) {

        String query = st.nextToken();
        if (!queryResults.containsKey(query)) {
            queryResults.put(query, new ArrayList<String>());
        }
        String result = "";
        while (st.hasMoreTokens()) {
            result += " " + st.nextToken();
        }
        log("Search Result",query+":"+result);
        queryResults.get(query).add(result);
        return null;
    }

    protected String processHeartBeat(StringTokenizer st){
        String receivedIp = st.nextToken();
        int receivedPort = Integer.parseInt(st.nextToken());
        Node n = new Node(receivedIp, receivedPort);
        receivedHeartBeats.put(n.getHttpUrl(),System.currentTimeMillis());
        return null;
    }

    protected String processContactsRequest(StringTokenizer st){
        String receivedIp = st.nextToken();
        int receivedPort = Integer.parseInt(st.nextToken());
        Node requester = new Node(receivedIp, receivedPort);

        if (knownNodes.size() > 1) {
            String reply_msg = CommandOpCodes.REQUEST_CONTACTS_OK + " " + (knownNodes.size() - 1);
            for (Node node : knownNodes) {
                if ( !node.getHttpUrl().equals(requester.getHttpUrl()) )
                    reply_msg += " " + node.getIp() + " " + node.getPort();
            }
            try {
                send(reply_msg, requester);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected String processContactsReply(String st){
        ArrayList<Node> newNodes = parseContactMessage(st);
        log("New Nodes:");
        String msg = "";
        for (Node node : newNodes) {
            msg+=node.ip + ":" + node.port+" | ";
            Boolean alreadyKnown = false;
            for (Node knownNode: knownNodes) {
                if (node.getHttpUrl().equals(knownNode.getHttpUrl())) {
                    alreadyKnown = true;
                    break;
                }
            }
            if (!alreadyKnown) {
                knownNodes.add(node);
            }
        }

        log(msg);
        log("Known Nodes(Updated):");
        int i=0;
        for (Node node : knownNodes) {
            i++;
            if(i>=4)
                break;
            log(node.ip + ":" + node.port);
        }
        return null;
    }

    protected String leave(StringTokenizer st) throws Exception {
        String reply = CommandOpCodes.LEAVE_OK + " ";

        final String ip = st.nextToken();
        final int port = Integer.parseInt(st.nextToken());

        knownNodes.removeIf(new Predicate<Node>() {
            @Override
            public boolean test(Node p) {
                return p.port == port && p.ip == ip;
            }
        });
        return reply;
    }
}

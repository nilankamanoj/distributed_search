package com.spk.node;


import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;
import java.util.Random;
import java.util.ArrayList;



public class Main {

    public static void main(String[] args) {

        if (args.length != 7)
        {
            echo("Invalid Count of Arguments");
            return;
        }
        AbstractClient client;

        if(args[6].equals("REST")){
            echo("Initiating REST client node");
            client = RestClient.fromArgs(args);
        }
        else if(args[6].equals("UDP")){
            echo("Initiating UDP client node");
            client = UdpClient.fromArgs(args);
        }
        else{
            echo("Node type should be either REST or UDP");
            return;
        }

        String[] files = getFileNames("FileNames.txt");
        client.setFiles(files);

        client.start();

    }

    //simple function to log data to terminal
    public static void echo(String msg) {
        System.out.println(msg);
    }


    private static String[] getFileNames(String fileName){
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        ArrayList<String> fileNames = new ArrayList<>();
        try {
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);
            ArrayList<String> allFileNames = new ArrayList<String>();

            String l;
            while ((l = bufferedReader.readLine()) != null) {
                allFileNames.add(l);
            }

            Random r = new Random();
            int countMin = 3;
            int countMax = 5;
            int noOfFilesInNode = r.nextInt(countMax - countMin) + countMin;

            int fileCount = allFileNames.size();
            for (int i = 0; i < noOfFilesInNode; i++){
                int random = r.nextInt(fileCount);
                fileNames.add(allFileNames.get(random));
                allFileNames.remove(random);
                fileCount--;
            }
            echo("Files in this node :");
            for(String f:fileNames){
                echo(f);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null)
                    fileReader.close();
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  fileNames.toArray(new String[fileNames.size()]);
        }
    }
}

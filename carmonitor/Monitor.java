/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmonitor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Date;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author debian
 */
public class Monitor {

    public LinkedList<String> roadNames;
    public LinkedList<Boolean> raodStates;
    public final int jamThreshold = 9;

    private MonitorGui mg;

    public static boolean guiOnline = true;

    private int changeCounter = 0;
    private int sendCount = 5;

    static DataOutputStream toGui;
    //a socket
    private Socket socket;

    private String hostName = "loclhost";

    private int port = 9999;

    public Monitor() {
        try {
            mg = new MonitorGui(this);
            mg.setVisible(true);
            initDataModel();
            //initTCPServer();
            initSimpleUDPServer();
    //        initMultiThreadUDPServer();

        } catch (IOException ex) {
            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Monitor(int port) {
        try {
            this.port = port;
            mg = new MonitorGui(this);
            mg.setVisible(true);
            initDataModel();
            initTCPServer();

        } catch (IOException ex) {
            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean processLine(String line) {

        String[] parts = line.split(";");
        if (parts.length != 6) {
            //the formating is broken or something is missing
            return false;
        }

        //check if the strig is correct
        //the first value determines what the rest means
        if (parts[0].equals("data")) {
            String speedString = parts[2];
            String roadName = parts[3];
            int speed = 0;

            try {
                speed = Integer.parseInt(speedString);
            } catch (NumberFormatException e) {
                //the speed string is broken and can not be turned into a number
                return false;
            }

            boolean nameOK;

            if (speed < jamThreshold) {
                nameOK = changeRoadState(roadName, true);
            } else {
                nameOK = changeRoadState(roadName, false);
            }

            if (nameOK) {
                //everything was ok
                return true;
            } else {
                //the edge name we got was unknowm
                return false;
            }

        } else {

            //not the type of packet we are looking for
            return false;
        }

    }

    public boolean changeRoadState(String name, boolean isJammed) {
        int index = -1;

        for (int i = 0; i < roadNames.size(); i++) {
            if (roadNames.get(i).equals(name)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return false;
        }

        raodStates.set(index, isJammed);
        return true;
    }

    private void initDataModel() {

        HighwayNetwork hn = new HighwayNetwork(20);
        roadNames = new LinkedList<>();
        raodStates = new LinkedList<>();
        for (int i = 0; i < hn.edgeNames.size(); i++) {
            roadNames.add(hn.edgeNames.get(i));
            raodStates.add(false);
        }
    }

    public void initMultiThreadUDPServer() throws IOException {
        DatagramSocket ds = new DatagramSocket(port);
        DatagramPacket incomingPacket;

        byte[] incMsg = new byte[1024];

        while (true) {
            incomingPacket = new DatagramPacket(incMsg, incMsg.length);
            ds.receive(incomingPacket);
            InetAddress clientAddress = incomingPacket.getAddress();
            int clientPort = incomingPacket.getPort();
            new CarService(clientAddress, clientPort, incomingPacket).start();

        }
    }

    public void initSimpleUDPServer() throws IOException {
        DatagramSocket ds = new DatagramSocket(port);
        DatagramPacket incomingPacket;
        DatagramPacket outgoingPacket;
        byte[] incMsg = new byte[1024];
        byte[] outMsg;

        while (true) {
            incomingPacket = new DatagramPacket(incMsg, incMsg.length);
            ds.receive(incomingPacket);
            InetAddress clientAddress = incomingPacket.getAddress();
            int clientPort = incomingPacket.getPort();

            if (processLine(new String(incomingPacket.getData()))) {//check here if string correct

                int maxTry = 5;
                int redo = 0;

                while (redo < maxTry) {
                    String ok = "ok";
                    outMsg = ok.getBytes();
                    outgoingPacket = new DatagramPacket(outMsg, outMsg.length, clientAddress, clientPort);
                    ds.send(outgoingPacket);
                    
                    ds.receive(incomingPacket);
                    
                    String reply = new String(incomingPacket.getData());
                    if(reply.equals("fin")){
                        break;
                    }else{
                        redo++;
                    }
                    
                }

            } else {
                System.out.println("string broken");
            }

            System.out.println(new String(incomingPacket.getData()));
        }
    }

    public void initTCPServer() throws IOException {

        ServerSocket listenSocket = new ServerSocket(port);
        System.out.println("Multithreaded Server starts on Port " + port);
        while (true) {
            Socket client = listenSocket.accept();
            new CarService(client, this).start();

            if (true) {
                changeCounter++;

                if (changeCounter == sendCount) {
                    changeCounter = 0;
                    try {
                        mg.updateTextbox();
                    } catch (Exception ex) {

                        Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            Monitor m = new Monitor();
        } else {
            Monitor m = new Monitor(Integer.parseInt(args[0]));
        }

    }

}

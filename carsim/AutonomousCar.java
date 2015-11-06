/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package carsim;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

/**
 *
 * @author debian
 */
public class AutonomousCar extends Car {
    
    
    //a socket
    private Socket socket;
    //connection to send data to the monitor
    private DataOutputStream toServer;
    
    private String hostName;
    private int port;
    
    /**
     * standart constructor
     * @param maxSpeed ma speed of the car
     * @param location the starting location of the car
     * @param id the if of the car
     * @param hn reverence to the highwayNetwork
     */
    public AutonomousCar(int maxSpeed,String location,int id,HighwayNetwork hn,String hostName,int port){
        super(maxSpeed,location,id,hn);
        this.hostName = hostName;
        this.port = port;
       
    }
    
    /**
     * inits the tcp connection
     * @throws Exception 
     */
    private void initTCPClient() throws Exception{
         socket = new Socket(hostName, port);
         toServer = new DataOutputStream(socket.getOutputStream());
    }
    
    public void sendDataToServerUDP() throws IOException{
        
        String finalMsg = "fin";
        DatagramSocket ds = new DatagramSocket();
        InetAddress address = InetAddress.getByName(hostName);
        byte[] msgToServer;
        byte[] finalMsgToServer;
        byte[] msgFromServer = new byte[1024];
        String dataString = buildDataString();
        msgToServer = dataString.getBytes();
        finalMsgToServer = finalMsg.getBytes();
        DatagramPacket packetOutgoing = new DatagramPacket(msgToServer, msgToServer.length,address, port);
        DatagramPacket packetIncoming = new DatagramPacket(msgFromServer,msgFromServer.length);
        DatagramPacket finalPacket = new DatagramPacket(finalMsgToServer,finalMsgToServer.length,address, port);
        
        
        int maxTry = 5;
        int redo = 0;
        
        while(redo<maxTry){
            ds.send(packetOutgoing);//send data
            ds.receive(packetIncoming); //wait for reply
            
            String incString = new String(packetIncoming.getData());
            
            if(incString.equals("ok")){
                break;
            }else{
                //no reply was send
                redo++;
            }            
        }
        
        ds.send(finalPacket);
        
        
        System.out.println(new String(packetIncoming.getData()));
        ds.close();
    }
    
    /**
     * sends the required data to the monitor
     * @throws IOException 
     */
    public void sendDataToServerTCP() throws IOException, Exception {
        initTCPClient();    
        String data = buildDataString();    
        toServer.writeBytes(data);
        socket.close();
    }
    
    /**
     * overrides the basic performTurn function, its calls the superfunction
     * and afterwards sends the cars data to the monitor
     * @param tickLength how much time is sumulated between each tick
     */
    @Override
    public void performTurn(int tickLength){
        
        super.performTurn(tickLength);
        try {
            Date d1 = new Date();
            
            
           // sendDataToServerTCP();
            sendDataToServerUDP();
            
            Date d2 = new Date();
            
            long diff = d2.getTime() - d1.getTime();
            /*System.out.print("time for sending: ");
            System.out.print(diff);
            System.out.println(" ms");*/
        } catch (Exception ex) {
            Logger.getLogger(AutonomousCar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void testPrint(){
       
        
       System.out.print("dir is ");
       System.out.println(direction);
       
       System.out.print("location is ");
       System.out.println(location);
       
       System.out.print("speed is ");
       System.out.println(speed);
       
       System.out.print("pos on edge is is ");
       System.out.println(posOnEdge);
       
    }
    
    /*
    @Override
    public void leavingEdge(){
        try {
            super.leavingEdge();
            initTCPClient();
            toServer.writeBytes("-"+";"+location);
            socket.close();
        } catch (Exception ex) {
            Logger.getLogger(AutonomousCar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
    /*
    @Override
    public void movingToEdge(){
        try {
            super.movingToEdge();
            initTCPClient();
            toServer.writeBytes("+"+";"+location);
            socket.close();
        } catch (Exception ex) {
            Logger.getLogger(AutonomousCar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
    /**
     * builds the data string that will be send to the monitor
     * @return 
     */
    private String buildDataString(){
       
        StringBuffer sb = new StringBuffer();
               
        //string should look like this:
        //id;speed;location;direction;destination
        sb.append("data").append(";").append(Integer.toString(id)).append(";").append(speed).append(";").append(location).append(";").
                append(direction).append(";").append(dest);
        
        return sb.toString();
    }
}

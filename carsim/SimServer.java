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

/**
 *
 * @author debian
 */
public class SimServer extends Thread {
    
    ServerSocket listenSocket;
    CarSim cs;
    
    public SimServer(CarSim cs) throws IOException{
        int port = 9996;
        listenSocket = new ServerSocket(port);
      
        this.cs = cs;
    } 
    
     
    @Override
    public void run () {
         
        while (true){
            try {
                Socket client = listenSocket.accept();
                System.out.println("Connection with: " +     // Output connection
                        client.getRemoteSocketAddress());   // (Client) address
                new SimService(client,cs).start();
            } catch (IOException ex) {
                Logger.getLogger(SimServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
     
     
    
}

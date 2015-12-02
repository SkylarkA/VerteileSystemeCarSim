/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package carsim;
import java.net.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author debian
 */
public class SimService extends Thread {
    
    Socket client;
    CarSim cs;
    SimService(Socket client,CarSim cs){
        this.client = client;
        this.cs = cs;
    }
    
     @Override
    public void run () {
        String line;
        BufferedReader fromClient;
        try {
            fromClient = new BufferedReader              // Datastream FROM Client
                    (new InputStreamReader(client.getInputStream()));
            line = fromClient.readLine();
            
            //check what the gui wants
            if(line.equals("guiOnline")){
                CarSim.guiOnline = true;
            }else{
                String[] parts = line.split(";");
                if(parts[0].equals("normal")){
                    cs.addCars(Integer.parseInt(parts[1]),false);
                }else{
                    cs.addCars(Integer.parseInt(parts[1]),true);
                }
            }
            fromClient.close();
        } catch (IOException ex) {
            Logger.getLogger(SimService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

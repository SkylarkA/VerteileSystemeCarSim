package carsim;

// 22.10. 10

/**
 *
 * @author Peter Altenberd
 * (Translated into English by Ronald Moore)
 * Computer Science Dept.                   Fachbereich Informatik
 * Darmstadt Univ. of Applied Sciences      Hochschule Darmstadt
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
public class GuiService extends Thread{
    Socket client;
    SimGui gui;
    GuiService(Socket client,SimGui gui){
        this.client = client;
        this.gui = gui;
    }

  @Override
    public void run (){
        String line;
        BufferedReader fromClient;
        DataOutputStream toClient;
        boolean verbunden = true;
       
        try{
            fromClient = new BufferedReader              // Datastream FROM Client
            (new InputStreamReader(client.getInputStream()));
            toClient = new DataOutputStream (client.getOutputStream()); // TO Client
            while(verbunden){     // repeat as long as connection exists
                line = fromClient.readLine();              // Read Request
              
                if(line.equals("fin")){
                    verbunden = false;
                }else{
                    
                    //gui.processEdgeString(line);
                    verbunden = false;
                    
                }
            }
            fromClient.close(); toClient.close(); client.close(); // End
            gui.populateTable();
           
        }catch (Exception e){System.out.println(e);}
    }
}

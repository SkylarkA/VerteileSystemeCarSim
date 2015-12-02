package carmonitor;

// 22.10. 10
/**
 *
 * @author Peter Altenberd (Translated into English by Ronald Moore) Computer
 * Science Dept. Fachbereich Informatik Darmstadt Univ. of Applied Sciences
 * Hochschule Darmstadt
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

public class CarService extends Thread {

    Socket client;
    Monitor monitor;

    InetAddress ia;
    int port;
    
    private Date threadStarted;
    private Date threadIsDone;
   

    CarService(Socket client, Monitor monitor) {
        this.client = client;
        this.monitor = monitor;
        threadStarted = new Date();
      

    }

   

    private void processLine(String line) {

      

        String[] parts = line.split(";");

        //the first value determines what the rest means
        if (parts[0].equals("data")) {
            String speedString = parts[2];
            String roadName = parts[3];
            int speed = Integer.parseInt(speedString);

            if (speed < monitor.jamThreshold) {
                monitor.changeRoadState(roadName, true);
            } else {
                monitor.changeRoadState(roadName, false);
            }

        } else {

        }

    }
    
   

    private void runAsTCP() {
        String line;
        BufferedReader fromClient;

        boolean verbunden = true;

        try {
            fromClient = new BufferedReader // Datastream FROM Client
                    (new InputStreamReader(client.getInputStream()));

            while (verbunden) {     // repeat as long as connection exists
                line = fromClient.readLine();              // Read Request
                processLine(line);
                verbunden = false;   // Break Conneciton?

            }
            fromClient.close();
            client.close(); // End
            threadIsDone = new Date();
            
            long diff = threadIsDone.getTime() - threadStarted.getTime();
            System.out.print("Time spend for tcp in ms: ");
            System.out.println(diff);
            

        } catch (Exception e) {

            System.out.println(e);
        }

    }

    

    @Override
    public void run() {
      
            runAsTCP();
        

    }
}

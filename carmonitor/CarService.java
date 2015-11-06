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

public class CarService extends Thread {

    Socket client;
    Monitor monitor;

    InetAddress ia;
    int port;
    DatagramPacket incomingPacket;
    boolean udp;

    CarService(Socket client, Monitor monitor) {
        this.client = client;
        this.monitor = monitor;
        udp = false;

    }

    CarService(InetAddress ia, int port,DatagramPacket incomingpacket ) {
        this.ia = ia;
        this.port = port;
        this.incomingPacket = incomingpacket;
        udp = true;

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
    
    private void checkString(String str){
        System.out.println(str);
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

        } catch (Exception e) {

            System.out.println(e);
        }

    }

    private void runAsUDP() {
        try {
            checkString(new String(incomingPacket.getData()));
            
            DatagramPacket outgoingPacket;
            DatagramSocket ds = new DatagramSocket(9998);
            String ok = "ok";
            byte[] outMsg = new byte[254];
            outMsg = ok.getBytes();
            outgoingPacket = new DatagramPacket(outMsg, outMsg.length, ia, port);
            ds.send(outgoingPacket);
        } catch (SocketException ex) {
            Logger.getLogger(CarService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CarService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        if (udp) {
            runAsUDP();
        } else {
            runAsTCP();
        }

    }
}

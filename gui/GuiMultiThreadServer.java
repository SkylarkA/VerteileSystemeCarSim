/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author debian
 */
public class GuiMultiThreadServer extends Thread {
    
    ServerSocket listenSocket;
    
     //a socket
    private Socket socket;
    //connection to send data to the 
    private DataOutputStream toGui;
    
    private SimGui gui;
    
    public GuiMultiThreadServer(SimGui gui) throws IOException{
        int port = 9991;
        listenSocket = new ServerSocket(port);
        this.gui = gui;
    }
    
    public void addCarsToSim(String type,String count){
        try {
            initTCPClient(9996);
            toGui.writeBytes(type+";"+count);
            socket.close();
        } catch (Exception ex) {
            Logger.getLogger(GuiMultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void connectToSim(){
        try {
            initTCPClient(9996);
            toGui.writeBytes("guiOnline");
            socket.close();
        } catch (Exception ex) {
            Logger.getLogger(GuiMultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void connectToMonitor(){
        try {
            initTCPClient(9990);
            toGui.writeBytes("guiOnline");
            socket.close();
        } catch (Exception ex) {
            Logger.getLogger(GuiMultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initTCPClient(int portNumber) throws Exception{
         socket = new Socket("localhost", 9996);
         toGui = new DataOutputStream(socket.getOutputStream());
    }
    

    @Override
    public void run(){
         while (true){
             try {
                 Socket client = listenSocket.accept();
                 System.out.println("Connection with: " +     // Output connection
                         client.getRemoteSocketAddress());   // (Client) address
                 new GuiService(client,gui).start();
             } catch (IOException ex) {
                 Logger.getLogger(GuiMultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
             }
        }
    }
    
}

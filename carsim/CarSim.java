/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package carsim;

import gui.SimGui;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author debian
 */
public class CarSim {

    //all cars on the network
    private LinkedList<Car> cars;
   
    private SimGui gui;
    
    //the highway network
    public HighwayNetwork hn; 
    
    //the next global id that can be assigned to a car
    private int globalId =0;
    
    //random number generator
    private Random rng = new Random();
    
    //the timframe between each tick of the sumulation
    public int tickLength = 60; //in min
    
    //as long as this is true the simulation will keep on going
    public boolean keepGoing = true;
    
    public static boolean guiOnline = true;
    
    public SimServer simServer;
    
    public String hostName = "localhost";
   
    public int port = 9999;
   
    
    /**
     * constructor
     */
    public CarSim(){
        cars = new LinkedList<>();
       
        hn = new HighwayNetwork(20);
        gui = new SimGui(this);
        gui.setVisible(true);
        
        try {
            simServer = new SimServer(this);
            simServer.start();
        } catch (IOException ex) {
            Logger.getLogger(CarSim.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public CarSim(String ip,int port){
        cars = new LinkedList<>();
        hostName = ip;
        this.port = port;
        hn = new HighwayNetwork(20);
        gui = new SimGui(this);
        gui.setVisible(true);
        
        try {
            simServer = new SimServer(this);
            simServer.start();
        } catch (IOException ex) {
            Logger.getLogger(CarSim.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
   
    
    private void sendDataToGui() throws IOException, Exception{
       gui.populateTable();
    }
    
    /**
     * starts the simulation
     */
    public void startLoop(){
        //can only be turned of from the outside
        while(keepGoing){
            try {
                //add a delay here
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(CarSim.class.getName()).log(Level.SEVERE, null, ex);
            }
            moveAllCars();
            
            if(guiOnline){
                try {
                    sendDataToGui();
                } catch (Exception ex) {
                    Logger.getLogger(CarSim.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
    }
    
    /**
     * returns a random node from the highway network
     * @return a random node
     */
    private String getRandomLocation(){
        int index = rng.nextInt(hn.size);
        return hn.nodeNames[index];
    }
    
    /**
     * returns a rondom node from the highway network that is not the one given
     * as a parameter
     * @param notThisOne the name of the node that should not be returned
     * @return the name of a node
     */
    private String getRandomLocation(String notThisOne){
        String location;
        while(true){
            int index = rng.nextInt(hn.size);
            location = hn.nodeNames[index];
            //if the random locaton is the same as the one
            //we dont want we stay in the loop and get another one
            if(!location.equals(notThisOne)){
                break;
            }
        }
        
        return location;
    }
    
    
    /**
     * inits the simulation
     * starts of with 10 cars
     */
    public void initSim(){
        
        addCars(20,true);       
        
        
    }
    
    /**
     * provides the given car with a random location that is not the same as it´s
     * current location
     * @param car reverence to the car object
     */
    private void giveDestinationToCar(Car car){
        //get a destination that is not the current location
        String destination = getRandomLocation(car.location);
        Vector<String> path = hn.dijkstra(car.location, destination);
        car.sendOnJourney(destination, path);
    }
    
    
    /**
     * moves all cars in the simulation
     */
    private void moveAllCars(){
       
        for(int i=0;i<cars.size();i++){
            
            if(cars.get(i).reachedDest){
                giveDestinationToCar(cars.get(i));
            }
            cars.get(i).performTurn(tickLength);
        }
    } 
    
    
    
    private int getRandomSpeed(){
        int baseSpeed = 10;
        baseSpeed = baseSpeed + rng.nextInt(5);
        return baseSpeed;
    }
    
    /**
     * Adds cars to the simulation
     * @param count how many cars will be added
     * @param autonomous if the cars should be autonomous or not
     */
    public void addCars(int count,boolean autonomous){
        Car car;
        System.out.println("adding");
        
        for(int i=0;i<count;i++){
            if(autonomous){
                car = new AutonomousCar(getRandomSpeed(),getRandomLocation(),globalId,hn,hostName,port);
                giveDestinationToCar(car);
                cars.add(car);
                
            }else{
                car = new NormalCar(getRandomSpeed(),getRandomLocation(),globalId,hn);
                giveDestinationToCar(car);
                cars.add(car);
            }
            
            globalId++;
        }
    }
    
    
    /**
     * starts the simulation
     * @param args 
     */
     public static void main(String[] args){
         CarSim cs;
         
        if(args.length==0||args.length==1){
                 cs = new CarSim();
        }else{
           
            cs = new CarSim(args[0],Integer.parseInt(args[1]));
            
        }         
         
        cs.initSim();
        cs.startLoop();
     }
    
    
}

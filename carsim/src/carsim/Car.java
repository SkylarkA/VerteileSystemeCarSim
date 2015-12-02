/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package carsim;

import java.util.Vector;

/**
 *
 * @author debian
 */
public class Car {
    
    //the overall destination of the car for its current journey
    protected String dest;
    //the current location of the car
    protected String location;
    //the current speed of the car
    protected int speed;
    //the maximum speed of the car
    protected int maxSpeed;
    //the direction of the car on an edge, this means to which end the car is
    //moving
    protected String direction;
    //the id of the car
    protected int id;
    //the path of the current journey, format is as following:
    //the name of an edge is followed by the name of the node which is the end
    //of the egde we the car is moving towards
    protected Vector<String> path;
    //reverence to the highway network object
    public HighwayNetwork hn;
    //the pos of the car on the current edge, can be between 0 and 
    //the edges length
    public int posOnEdge;
    //the length of the edge the car is currently on
    private int currentEdgeLength;
    //determies if the car is currently on an edge or node
    private boolean isOnNode;
    //determines if the car has erached its destination
    public boolean reachedDest;
    //dertermines by which factor the speed is reduced if the car in a
    //traffic jam
    private final int jamFactor = 2;
    
    
    /**
     * constructor
     * @param maxSpeed ma speed of the car
     * @param location the starting location of the car
     * @param id the if of the car
     * @param hn reverence to the highwayNetwork
     */
    public Car(int maxSpeed,String location,int id,HighwayNetwork hn){
        this.maxSpeed = maxSpeed;
        this.location = location;
        this.id = id;
        this.hn = hn;
        this.speed = maxSpeed;
       
    }
    
    /**
     * moves the car though the simulation
     * @param tickLength how much time is simulated on each tick
     */
    public void performTurn(int tickLength){
        int moveDistance = calcMoveDistance(tickLength);
        
        if(isOnNode){
            moveToNewEdge(moveDistance);
        }else{
            moveOnEdge(moveDistance);
        }
    }
    
    /**
     * moves the car from a node to a new edge
     * @param moveDistance how far the car can move on the new edge
     */
    public void moveToNewEdge(int moveDistance){
        
       if(location.equals(dest)){
           reachedDest = true;
            isOnNode = true;
            return;
       }
        
       
       System.out.println("my iID is "+ id);
       System.out.println("my size is " + path.size()); 
       
       for(int i=0;i<path.size();i++){
    	   System.out.println(path.get(i));
       }
        
        //get the length of the next edge
        currentEdgeLength = hn.getEdgeLength(location, path.get(1));
        //move the car to the next edge
        isOnNode = false;
        location = path.get(0);
        direction = path.get(1);
        posOnEdge = 0;
        
        //remove the first two elements
        path.remove(0);
        path.remove(0);
        
        
            movingToEdge();
            moveOnEdge(moveDistance);
        
        
        
        
    }
    
    public void leavingEdge(){
        hn.leftEdge(location);
    }
    
    public void movingToEdge(){
         hn.movedToEdge(location);
    }
    
    /**
     * moves the car on an edge
     * @param moveDistance how far the car can still move
     */
    private void moveOnEdge(int moveDistance){
        
        int distanceToCover = currentEdgeLength - posOnEdge;
        
        //check if the car can move beyond the current edge
        if(moveDistance > distanceToCover){
            leavingEdge();
            //the direction  was the end of the edge we moved towards
            //since we reached it it is now our location
            location = direction;
            moveToNewEdge(moveDistance-distanceToCover);
        }else{
            //check if the edge we are on is jammed or not
            //if so we have to adjust our movement Distance
            
            if(hn.isJammed(location)){
                moveDistance = moveDistance/jamFactor;
                speed = speed/2;
            }else{
                speed = maxSpeed;
            }
            
            posOnEdge += moveDistance;
        }
    }
    
    /**
     * calculates how far the car can move in one turn
     * @param tickLength how long each simulated cycle is
     * @return the distance the car can move
     */
    private int calcMoveDistance(int tickLength){
       
        int distance = (speed/60)*tickLength;
        //just to make this work
        distance = maxSpeed;
        return distance;
    }
    
    
    
    /**
     * inits the car for a journey through the network
     * @param dest the overall destination of the car
     * @param path the path the car will take
     */
    public void sendOnJourney(String dest,Vector<String> path){
        this.dest = dest;
        this.path = path;
        posOnEdge = 0;
        
        isOnNode = true;
        reachedDest = false;
    }
    
   
    
}

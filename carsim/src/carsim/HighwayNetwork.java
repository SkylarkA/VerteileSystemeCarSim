/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsim;

import java.util.List;
import java.util.Vector;


/**
 *
 * @author debian
 */
public class HighwayNetwork {

    //how man nodes are currently in the network
    public int size;
    //the max size of the network
    private int maxSize;
    //the names of all nodes
    public String[] nodeNames;
    
    //the follwing 3 vectors are synced
    
    //the names of all edges
    public Vector<String> edgeNames;
    //the maximum edge capacity
    public Vector<Integer> maxEdgeCap;
    //the current usage of each edge
    public Vector<Integer> edgeUsage;

    //the first two dimensions serve as a normal adjacencyMatrix
    //the third dimension contains the index for each edge in the 3 synced vectors
    public int[][][] adjacencyMatrix;

    public Vector<String> artificialJams;
    
    /**
     * constructor
     * @param maxSize the max count of nodes 
     */
    public HighwayNetwork(int maxSize) {
        this.maxSize = maxSize;
        nodeNames = new String[maxSize];
        adjacencyMatrix = new int[maxSize][maxSize][2];
        edgeNames = new Vector<String>();
        maxEdgeCap = new Vector<Integer>();
        edgeUsage = new Vector<Integer>();
        artificialJams = new Vector<String>();

        size = 0;

        for (int i = 0; i < maxSize; i++) {
            for (int i2 = 0; i2 < maxSize; i2++) {
                adjacencyMatrix[i][i2][0] = -1;
                adjacencyMatrix[i][i2][1] = -1;
            }
        }
        
        populateNetwork();
        //populateTest();
    }
    
    public Vector<String> adjustedDijkstra(List<String> jammedRoads,String location,String destination){
    	
    	
    	System.out.println("-------------------------------");
    	System.out.println("location is "+ location);
    	System.out.println("destination is "+ destination);
    	int mulFactor = 10;
    	
    	//adjust the length of all adges that have a traffic jam
    	for(int i=0;i<jammedRoads.size();i++){
    		adjustEdgeLength(jammedRoads.get(i),mulFactor,false);
    	}
    	
    	Vector<String> newPath = dijkstra(location,destination);
    	
    	//reset all adges back to normal
    	for(int i=0;i<jammedRoads.size();i++){
    		adjustEdgeLength(jammedRoads.get(i),mulFactor,true);
    	}
    	
    	
    	
    	System.out.println("-------------------------------");
    	
    	return newPath;
    }
    
    public void toggleArtificalJam(String name){
    	//check if there is already artifical jam
    	for(int i=0;i<artificialJams.size();i++){
    		if(artificialJams.get(i).equals(name)){
    			artificialJams.remove(i);
    			edgeUsage.set(this.getEdgeIndex(name), edgeUsage.get(this.getEdgeIndex(name))-10);
    			return;
    		}
    	}
    	
    	edgeUsage.set(this.getEdgeIndex(name), edgeUsage.get(this.getEdgeIndex(name))+10);
    	artificialJams.add(name);
    	
    }
    
    /**
     * decrese the usage of the edge with the given name by one
     * @param name the name of the edge
     */
    public void leftEdge(String name){
        int edgeIndex = getEdgeIndex(name);
        edgeUsage.setElementAt(edgeUsage.get(edgeIndex)-1, edgeIndex);
    }
    
    /**
     * increse the usage of the edge with the given name by one
     * @param name the name of the edge
     */
    public void movedToEdge(String name){
        int edgeIndex = getEdgeIndex(name);
        edgeUsage.setElementAt(edgeUsage.get(edgeIndex)+1, edgeIndex);
    }
    
    /**
     * checks if the edge with the given name is jammed or not
     * @param name the name of the edge
     * @return 
     */    
    public boolean isJammed(String name){
        
        int edgeIndex = getEdgeIndex(name);
        
        if(edgeIndex == -1){
            return false;
        }
        
        int usage = edgeUsage.get(edgeIndex);
        int maxUsage = maxEdgeCap.get(edgeIndex);
        
        return usage>maxUsage;
    }
    
    /**
     * returns the index of the edge with the given name
     * @param name
     * @return 
     */
    private int getEdgeIndex(String name){
        for(int i=0;i<edgeNames.size();i++){
            if(edgeNames.get(i).equals(name)){
                return i;
            }
        }
        return -1;
    }
    
    /**
     * ignore
     */
    private void populateTest(){
        addNode("A");
        addNode("B");
        addNode("C");
        
        addConnection("A", "C",130,10,"A1");
        addConnection("A", "B",60,10,"A2");
        addConnection("B", "C",60,10,"A3");
        
       
    }

    /**
     * populates the network
     */
    private void populateNetwork() {
        addNode("Berlin");
        addNode("Hamburg");
        addNode("Leibzig");
        addNode("Bonn");
        addNode("Darmstadt");
        addNode("Frankfurt");
        addNode("Dortmund");
        addNode("Essen");
        addNode("München");
        addNode("Erfurt");

        addConnection("Berlin", "Hamburg",200,10,"A13");
        addConnection("Berlin", "Leibzig",300,10,"A16");
        addConnection("Leibzig", "Bonn",100,10,"A15");
        addConnection("Berlin", "Bonn",150,10,"A14");
        addConnection("Dortmund", "Hamburg",200,10,"A12");
        addConnection("Berlin", "Darmstadt",250,10,"A11");
        addConnection("Darmstadt", "Dortmund",100,10,"A10");
        addConnection("Darmstadt", "Frankfurt",60,10,"A9");
        addConnection("Darmstadt", "Bonn",80,10,"A8");
        addConnection("Bonn", "Frankfurt",50,10,"A7");
        addConnection("Frankfurt", "München",80,10,"A6");
        addConnection("Erfurt", "Dortmund",100,10,"A5");
        addConnection("Erfurt", "Bonn",300,10,"A4");
        addConnection("Essen", "Dortmund",70,10,"A3");
        addConnection("Essen", "München",150,10,"A2");
        addConnection("München", "Dortmund",50,10,"A1");
    }

    /**
     * returns the index of the node with the given name
     * @param name
     * @return 
     */
    private int getPosOfNode(String name) {
        for (int i = 0; i < size; i++) {
            if (name.equals(nodeNames[i])) {
                return i;
            }
        }
        return -1;

    }
    
    private void adjustEdgeLength(String edgeName,int adjustmentFactor,boolean reset){
    	int index = getEdgeIndex(edgeName);
    	
    	
    	for(int i=0;i<adjacencyMatrix.length;i++){
    		for(int j =0;j<adjacencyMatrix[0].length;j++){
    			if(adjacencyMatrix[i][j][1] == index){			
    				
    				if(reset){
    					adjacencyMatrix[i][j][0] = adjacencyMatrix[i][j][0]/adjustmentFactor;
    					adjacencyMatrix[j][i][0] = adjacencyMatrix[j][i][0]/adjustmentFactor;
    				}else{
    					adjacencyMatrix[i][j][0] = adjacencyMatrix[i][j][0]*adjustmentFactor;
    					adjacencyMatrix[j][i][0] = adjacencyMatrix[j][i][0]*adjustmentFactor;
    					
    				}    	    			
    				return;    				
    			}
    		}
    	}
    }
    
   

    /**
     * adds a connection between two nodes
     * @param snode
     * @param enode
     * @param weight
     * @param maxCap
     * @param name 
     */
    private void addConnection(String snode, String enode, int weight, int maxCap, String name) {
        int verticalPos = getPosOfNode(snode);
        int horizontalPos = getPosOfNode(enode);

        if (verticalPos == -1 || horizontalPos == -1) {
            System.out.println("error while building network");
            
            return;
        }

        adjacencyMatrix[verticalPos][horizontalPos][0] = weight;
        adjacencyMatrix[horizontalPos][verticalPos][0] = weight;
        
        edgeNames.add(name);
        maxEdgeCap.add(maxCap);
        edgeUsage.add(0);
        
        
        int edgeIndex = edgeNames.size()-1;
        adjacencyMatrix[verticalPos][horizontalPos][1] = edgeIndex;
        adjacencyMatrix[horizontalPos][verticalPos][1] = edgeIndex;

    }
    
    public int getEdgeLength(String start,String end){
        return adjacencyMatrix[getPosOfNode(start)][getPosOfNode(end)][0];
    }
    
    
    public int getEdgeCap(String name){
        for(int i =0;i<edgeNames.size();i++){
            if(edgeNames.get(i).equals(name)){
                return i;
            }
        }
        //edge does not exist
        return -1;
    }

    private void addNode(String name) {
        if (size >= maxSize) {
            return;
        } else {
            nodeNames[size] = name;
            size++;
        }
    }
    
    public Vector<String> dijkstra(String startNode,String endNode){
        
        //the predecessor for each node
        int[] predecessor = new int[size];
        
        //the shortest distance for each node to the start node
        int[] distance = new int[size];
        
        //if the node has been seen yet
        boolean[] hasBeenSeen = new boolean[size];
        
         //contains all visited nodes
        Vector<Integer> nodesVisited = new Vector<Integer>();
        
         //initialising the arrays
        for (int i = 0; i < size; i++) {
            distance[i] = Integer.MAX_VALUE;
            hasBeenSeen[i] = false;
            predecessor[i] = -1;

        }
        
         //pos of the start end end node in the array
        int endNodePos = getPosOfNode(endNode);
        int startNodePos = getPosOfNode(startNode);
        
        if(endNodePos==-1||startNodePos ==-1){
            System.out.println("node does not exist");
            return null;
        }
        
        hasBeenSeen[startNodePos] = true;
        distance[startNodePos] = 0;
        nodesVisited.add(startNodePos);
        
        //the shortest distance to a new node so far
        int shortestSoFar;
        //start and end of the obove node
        int from;
        int to;
        
        //mainloop
        while(nodesVisited.size()!=size){
           
            shortestSoFar = -1;
            //starting pos of the best connection so far
            from = 0;
            //end pos fo the best connection so far
            to = 0;
            
            for(int i=0;i<nodesVisited.size();i++){
                
                //the index of the node with the shortest connection
                //comming from the node i
                int nodeIndex = nextNode(nodesVisited.get(i),hasBeenSeen);
                
                
                //check if this new connection is shorter than any connection 
                //found so far
                if(nodeIndex != -1){
                    //the shortest distance of any node from i to any node
                    int edgeDist = adjacencyMatrix[nodesVisited.get(i)][nodeIndex][0];
                    
                    if(shortestSoFar == -1 ||shortestSoFar+distance[from]>edgeDist+distance[nodesVisited.get(i)]){
                        shortestSoFar = edgeDist;
                        from = nodesVisited.get(i);
                        to = nodeIndex;
                    }
                }
                
            }
            
            //the next node has been found
            predecessor[to] = from;
            distance[to] = shortestSoFar+distance[from];
            hasBeenSeen[to] = true;
            nodesVisited.add(to);
            
            
            //checking if the node which just has been added is the the end node
            if (to == endNodePos) {
                Vector<String> list = new Vector<String>();

                int nextNode = to;
               /* while (nextNode != -1) {

                    list.add(0, nodeNames[nextNode]);
                    nextNode = predecessor[nextNode];
                }*/
                
                while (predecessor[nextNode] != -1) {
                    list.add(0, nodeNames[nextNode]);
                    list.add(0,edgeNames.get(adjacencyMatrix[nextNode][predecessor[nextNode]][1]));
                    nextNode = predecessor[nextNode];
                }

                return list;
            }

            
            
        }
        
        return null;
    }
    
    private int nextNode(int baseNode,boolean[] alreadySeen){
        
        int shortestSoFar = Integer.MAX_VALUE;
        int shortestIndex=-1;
        
        for (int k = 0; k < size; k++) {

                    //node hasn´t been visited  yet && a connection exists &&is the edge smaller)
                    if ((!alreadySeen[k]) && adjacencyMatrix[baseNode][k][0] != -1 &&  adjacencyMatrix[baseNode][k][0] < shortestSoFar) {
                        //set start, end and edge size of the new shortest connection                       
                        shortestSoFar = adjacencyMatrix[baseNode][k][0];
                        shortestIndex = k;
                    }
                }
        
        
        
        return shortestIndex;
    }
    
    /*
    public Vector<String> dijkstra(String startNode, String endNode) {
        if (startNode == endNode) {
            return null;
        }

        //contains all visited nodes
        Vector<Integer> nodesVisited = new Vector<Integer>();

        int[] predecessor = new int[size];
        int[] distance = new int[size];
        boolean[] hasBeenSeen = new boolean[size];

        //initialising the arrays
        for (int i = 0; i < size; i++) {
            distance[i] = -1;
            hasBeenSeen[i] = false;
            predecessor[i] = -1;

        }
        //pos of the start end end node in the array
        int endNodePos = getPosOfNode(endNode);
        int startNodePos = getPosOfNode(startNode);
        //making the start node seen
        nodesVisited.add(startNodePos);
        hasBeenSeen[startNodePos] = true;
        distance[startNodePos] = 0;

        //shortest edge seen so far
        int shortestSoFar = -1;
        //start end end of the shortest edge
        int from = 0;
        int to = 0;
        //a node which has been seen and from which a new connection could come
        int knownNode;

        //mainloop
        while (nodesVisited.size() != size) {
            shortestSoFar = -1;
            //pos of the starting node
            from = 0;
            //pos of the end node
            to = 0;

            //check all nodes which has been visited so far 
            for (int i = 0; i < nodesVisited.size(); i++) {
                knownNode = nodesVisited.get(i);
                //check all edges that this node has
                for (int k = 0; k < size; k++) {

                    //node hasn´t been visited  yet && a connection exists &&(shortestSoFar has a real value || is the edge smaller)
                    if ((!hasBeenSeen[k]) && adjacencyMatrix[knownNode][k] != -1 && (shortestSoFar == -1 || adjacencyMatrix[knownNode][k] < shortestSoFar)) {
                        //set start, end and edge size of the new shortest connection
                        from = knownNode;
                        to = k;
                        shortestSoFar = adjacencyMatrix[knownNode][k];
                    }
                }
            }
            //the next node has been found
            predecessor[to] = from;
            distance[to] = shortestSoFar;
            hasBeenSeen[to] = true;
            nodesVisited.add(to);

            //checking if the node which just has been added is the the end node
            if (to == endNodePos) {
                Vector<String> list = new Vector<String>();

                int nextNode = to;
                while (nextNode != -1) {

                    list.add(0, nodeNames[nextNode]);
                    nextNode = predecessor[nextNode];
                }

                return list;
            }
        }

        return null;

    }*/
    
    /*
    public static void main(String[] args) {
        HighwayNetwork hn = new HighwayNetwork(20);
        System.out.println("done");
        
        Vector<String> path = hn.dijkstra("Erfurt","Darmstadt");
        
        if(path != null){
            for(int i=0;i<path.size();i++){
                System.out.println(path.get(i));
            }
        }else{
            System.out.println("path is null");
        }
        
       
       
    }*/

}

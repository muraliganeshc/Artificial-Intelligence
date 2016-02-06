package waterFlow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Arrays.fill;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Murali Ganesh
 */

class Vertex implements Comparable<Vertex> {
    int vertexid;
    int parentvertexid;
    String name;
    boolean isvisited;
    int gcost;
    public Vertex(int vertexid,String Name) {
        this.vertexid = vertexid;
        this.isvisited = false;
        this.name = Name;
        this.gcost=0;
    }
    public int getVertexid() {
        return vertexid;
    }
    public void setVertexid(int vertexid) {
        this.vertexid = vertexid;
    }
    public int getParentvertexid() {
        return parentvertexid;
    }
    public void setParentvertexid(int parentvertexid) {
        this.parentvertexid = parentvertexid;
    }
    public boolean isIsvisited() {
        return isvisited;
    }
    public void setIsvisited(boolean isvisited) {
        this.isvisited = isvisited;
    }
    public int getGcost() {
        return gcost;
    }
    public void setGcost(int gcost) {
        this.gcost = gcost;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public static Comparator<Vertex> goalsort = new Comparator<Vertex>() {

        @Override
	public int compare(Vertex n1, Vertex n2) {
            if(n1.getGcost() < n2.getGcost()) {
                return -1;
            }
            else if (n1.getGcost() == n2.getGcost()) {
                int nodeid1 = n1.getVertexid();
                int nodeid2 = n2.getVertexid();
                 //ascending order
                return nodeid1-nodeid2;
            }
            else
                return 1;

        }};
    @Override
    public int compareTo(Vertex o) {
        return Integer.valueOf(gcost).compareTo(o.gcost);
    }
}
class Edge {
        int vertexid;
        String name;
        int adjdistance;
        boolean[] timer = new boolean[24];
        public Edge(String nodename,int vertexid, int adjdistance) {
            this.name = nodename;
            this.adjdistance = adjdistance;
            this.vertexid = vertexid;
        }
        public Edge() {
            this.name = null;
            this.adjdistance = 0;
            fill(this.timer,false);
        }

        public String getNodename() {
            return name;
        }

        public void setNodename(String nodename) {
            this.name = nodename;
        }

        public int getAdjdistance() {
            return adjdistance;
        }

        public void setAdjdistance(int adjdistance) {
            this.adjdistance = adjdistance;
        }

        public boolean getInOperation(int i) {
            return this.timer[i];
        }

        public static Comparator<Edge> Namesortasec = new Comparator<Edge>() {

        @Override
	public int compare(Edge n1, Edge n2) {
	   String nodename1 = n1.getNodename().toUpperCase();
	   String nodename2 = n2.getNodename().toUpperCase();
	   //ascending order
	   return nodename1.compareTo(nodename2);
        }};
        public static Comparator<Edge> Namesortdsec = new Comparator<Edge>() {
        @Override
	public int compare(Edge n1, Edge n2) {
	   String nodename1 = n1.getNodename().toUpperCase();
	   String nodename2 = n2.getNodename().toUpperCase();
	   //Descending order
	   return nodename2.compareTo(nodename1);
        }};

        @Override
        public String toString() {
        return "[ Node=" + name + ", Distance =" + adjdistance + "]";
        }
}
public class waterFlow {

    //Static Variable Declaration
    //Re-using Variables
    static String algotobeused;
    static String source;
    static List<String> destination;
    static List<String> middlenodes;
    static List<String> nodelist;
    static List<Vertex> nodeobjects;
    static Map<String,List<Edge>> graph = new HashMap<String,List<Edge>>();
    static int starttime;
     //Buffered Reader and writer
    static BufferedReader br = null;
    static BufferedWriter wr = null;

    private static void readinput() {
        try {
            // The first line of the input is the source node which is read and stored in a string
            source = br.readLine().trim();
            // The destination are seperated by space so use split function
            destination = new ArrayList<String>(Arrays.asList(br.readLine().split(" ")));
            middlenodes = new ArrayList<String>(Arrays.asList(br.readLine().split(" ")));
            nodelist = new ArrayList<String>();
            nodelist.add(source);
            nodelist.addAll(destination);
            nodelist.addAll(middlenodes);
            Collections.sort(nodelist);

            //create vertices object
            nodeobjects = new ArrayList<Vertex>();
            Iterator<String> tempitr = nodelist.iterator();
            int nodecounter = 0;
            while(tempitr.hasNext()) {
                nodeobjects.add(new Vertex(nodecounter,(String) tempitr.next()));
                nodecounter++;
            }
            //get the no of edges 
            int noofpipes = Integer.parseInt(br.readLine());
            graph.clear();
            for(int i=0;i<noofpipes;i++) {
                String temp[];
                temp = br.readLine().split(" ");
                //create a new edge object (Name,ID,Adj distance)
                Edge tempobj = new Edge(temp[1],nodelist.indexOf(temp[1]),Integer.parseInt(temp[2]));
                //set the time of not operation as true if the pipe is not operating in the range given
                if(algotobeused.equals("UCS")) {
                	for(int j=0;j<Integer.parseInt(temp[3]);j++) {
                		String t[] =temp[(j+1)+3].split("-");
                		for(int x=Integer.parseInt(t[0]);x<=Integer.parseInt(t[1]);x++){
                			tempobj.timer[x]=true;
                		}
                	}
                }
                // if a edge is already inserted then get the object and add the temp object with comparator for sorting
                // else create a new array list
                if(graph.containsKey(temp[0])) {
                    graph.get(temp[0]).add(tempobj);
                    if(algotobeused.equals("DFS"))
                        Collections.sort(graph.get(temp[0]), Edge.Namesortdsec);
                    else
                        Collections.sort(graph.get(temp[0]), Edge.Namesortasec);
                }
                else {
                    List<Edge> templist = new ArrayList<Edge>();
                    templist.add(tempobj);
                    graph.put(temp[0],templist);
                }
            }
            //get the start time of the pipe
            starttime = Integer.parseInt(br.readLine().trim());
        }
        catch (Exception ex) {
            Logger.getLogger(waterFlow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
    private static void writeInFileNone() {
        try {
            wr.append("None");
            wr.newLine();   
        }
        catch(IOException ex){
             Logger.getLogger(waterFlow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private static void getPathandWrite(String goalnode,int distance) {
        List<String> path = new ArrayList<String>();
        path.add(goalnode);
        int parentid = nodeobjects.get(nodelist.indexOf(goalnode)).getParentvertexid();
        while(!(nodelist.indexOf(source)== parentid))
        {
            path.add(nodelist.get(parentid));
            parentid = nodeobjects.get(parentid).getParentvertexid();
        }
        path.add(source);
        //writeInFile(goalnode,((((path.size()-1))+starttime))%24);
        try {
			wr.append(goalnode.toUpperCase()+" "+((((path.size()-1))+starttime))%24);
			wr.newLine();
        }
		catch (Exception ex) {
			Logger.getLogger(waterFlow.class.getName()).log(Level.SEVERE, null, ex);
		}
    }
    private static void bfs() throws IOException {
        try {
            readinput();
            List<String> frontier = new ArrayList<String>();
            List<String> explored = new ArrayList<String>();
            String tempnode="None";
            int distance = starttime;
            frontier.add(source);
            nodeobjects.get(nodelist.indexOf(source)).setParentvertexid(nodelist.indexOf(source));
            boolean found = false;
            while(!frontier.isEmpty() && !found) {
                explored.add(frontier.get(0));
                nodeobjects.get(nodelist.indexOf(frontier.get(0))).setIsvisited(true);
                List<Edge> childnodeslist = graph.get(frontier.get(0));
                if(childnodeslist != null) {
                    for (Edge childnode : childnodeslist) {
                         if( (!frontier.contains(childnode.getNodename())) && (!explored.contains(childnode.getNodename())) ) {
                            nodeobjects.get(nodelist.indexOf(childnode.getNodename())).setParentvertexid(nodelist.indexOf(frontier.get(0)));
                            if(destination.contains(childnode.getNodename())) {
                                tempnode =  childnode.getNodename();
                                found = true;
                                break;
                            }
                            else {
                                frontier.add(childnode.getNodename());
                            }
                        }
                    }
                }
                frontier.remove(0);
                distance++;
            }
            if(tempnode.equals("None")) {
                writeInFileNone();
            }
            else {
                getPathandWrite(tempnode,distance);
            }
        }
        catch(Exception e) {
        	writeInFileNone();
        	Logger.getLogger(waterFlow.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    private static void dfs() throws IOException {
    	try {
    		readinput();
    		Deque<String> frontier = new ArrayDeque<String>();
    		List<String> explored = new ArrayList<String>();
    		String tempnode="None";
    		String popedfrontierele = null;
    		int distance = starttime;
    		frontier.push(source);
    		nodeobjects.get(nodelist.indexOf(source)).setParentvertexid(nodelist.indexOf(source));
    		boolean found = false;
    		while(!frontier.isEmpty() && !found) {
    			explored.add(frontier.peek());
    			popedfrontierele = frontier.peek();
    			frontier.pop();
    			if(destination.contains(popedfrontierele)) {
    				tempnode =  popedfrontierele;
    				found = true;
    				break;
    			}
    			nodeobjects.get(nodelist.indexOf(popedfrontierele)).setIsvisited(true);
    			List<Edge> childnodeslist = graph.get(popedfrontierele);
    			if(childnodeslist != null) {
    				for (Edge childnode : childnodeslist) {
    					if( (!frontier.contains(childnode.getNodename())) && (!explored.contains(childnode.getNodename())) ) {
    						nodeobjects.get(nodelist.indexOf(childnode.getNodename())).setParentvertexid(nodelist.indexOf(popedfrontierele));
    						frontier.push(childnode.getNodename());
    					}
    					else if((frontier.contains(childnode.getNodename())) && (!nodeobjects.get(nodelist.indexOf(childnode.getNodename())).isIsvisited())) {
    						frontier.remove(childnode.getNodename());
    						nodeobjects.get(nodelist.indexOf(childnode.getNodename())).setParentvertexid(nodelist.indexOf(popedfrontierele));
    						frontier.push(childnode.getNodename());
    					}
    				}
    			}
    			distance++;
    		}
    		if(tempnode.equals("None")) {
    			writeInFileNone();
    		}
    		else {
    			getPathandWrite(tempnode,distance);
    		}
    	}
    	catch(Exception e) {
    		writeInFileNone();
        	Logger.getLogger(waterFlow.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static void ucs() throws IOException {
    	try {
    		readinput();
    		Queue<Vertex> frontier = new PriorityQueue<Vertex>(nodelist.size(),Vertex.goalsort);
    		List<Vertex> explored = new ArrayList<Vertex>();
    		String tempnode="None";
    		int goalcost = starttime;
    		frontier.add(nodeobjects.get(nodelist.indexOf(source)));
    		nodeobjects.get(nodelist.indexOf(source)).setIsvisited(true);
    		nodeobjects.get(nodelist.indexOf(source)).setGcost(goalcost);
    		boolean found = false;
    		while(!frontier.isEmpty() && !found) {
    			List<Edge> childnodeslist = graph.get(frontier.peek().getName());
    			goalcost = frontier.peek().getGcost();
    			explored.add(frontier.peek());
    			if(destination.contains(frontier.peek().getName())) {
    				tempnode = frontier.peek().getName();
    				found = true;
    				break;
    			}
    			frontier.poll();
    			if(childnodeslist != null) {
    				for (Edge childnode : childnodeslist) {
    					if(!childnode.getInOperation(goalcost%24)) {
    						if((!frontier.contains(nodeobjects.get(nodelist.indexOf(childnode.getNodename())))) &&(!explored.contains(nodeobjects.get(nodelist.indexOf(childnode.getNodename()))))) {
    							frontier.remove(nodeobjects.get(nodelist.indexOf(childnode.getNodename())));
    							nodeobjects.get(nodelist.indexOf(childnode.getNodename())).setGcost(goalcost+childnode.getAdjdistance());
    							nodeobjects.get(nodelist.indexOf(childnode.getNodename())).setParentvertexid(explored.get(explored.size()-1).getVertexid());
    							frontier.add(nodeobjects.get(nodelist.indexOf(childnode.getNodename())));
    						}
    						else if(frontier.contains(nodeobjects.get(nodelist.indexOf(childnode.getNodename())))) {
    							if(goalcost+childnode.getAdjdistance() < nodeobjects.get(nodelist.indexOf(childnode.getNodename())).getGcost()){
    								frontier.remove(nodeobjects.get(nodelist.indexOf(childnode.getNodename())));
    								nodeobjects.get(nodelist.indexOf(childnode.getNodename())).setGcost(goalcost+childnode.getAdjdistance());
    								nodeobjects.get(nodelist.indexOf(childnode.getNodename())).setParentvertexid(explored.get(explored.size()-1).getVertexid());
    								frontier.add(nodeobjects.get(nodelist.indexOf(childnode.getNodename())));
    							}
    						}
    					}
    				}
    			}
    		}
    		if(tempnode.equals("None")) {
    			writeInFileNone();
    		}
    		else {
    			wr.append(tempnode.toUpperCase()+" "+nodeobjects.get(nodelist.indexOf(tempnode)).getGcost()%24);
    			wr.newLine();
    		}
    	}
    	catch(Exception e) {
    		writeInFileNone();
        	Logger.getLogger(waterFlow.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void main(String[] args) throws IOException {
        File filename = null;
        File outputfilename = null;
        int noofinputs =0;
        try {
            if(args.length > 0) {
                filename = new File(args[1]);
                outputfilename = new File("output.txt");

            }
            br = new BufferedReader(new FileReader(filename));
            wr = new BufferedWriter(new FileWriter(outputfilename));
            //Read the first line of the input which contains the no of input
            noofinputs = Integer.parseInt(br.readLine());
            while(noofinputs > 0){
                algotobeused = br.readLine();
                switch (algotobeused) {
                    case "BFS": {
                        bfs();
                        break;
                    }
                    case "DFS": {
                        dfs();
                    }
                    break;
                    case "UCS": {
                        ucs();
                    }
                    break;
                }
                destination.clear();
                middlenodes.clear();
                br.readLine();
                noofinputs--;
            }
        }
        catch(Exception e) {
            wr.close();
//            e.printStackTrace();
        }
        finally  {
            br.close();
            wr.close();
        }
    }
 
}

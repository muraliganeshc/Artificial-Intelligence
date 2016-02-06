import java.io.*;
class MancalaBoard {
	int pit[];
	MancalaBoard parentnode;
	String Name;
	int depth;
	int alpha;
	int beta;
	int utility;
	boolean extramove = false;
	public MancalaBoard() {
	}
	public MancalaBoard(int totalpitsize) {
		this.pit = new int[totalpitsize];
	}
	public MancalaBoard(int[] pit){
		this.pit = new int[pit.length];
		for(int i=0;i<pit.length;i++)
			this.pit[i] = pit[i];
	}
	public MancalaBoard(int[] pit,String Name,int utility,int depth){
		this.pit = new int[pit.length];
		for(int i=0;i<pit.length;i++)
			this.pit[i] = pit[i];
		this.Name = Name;
		this.utility = utility;
		this.depth = depth;
		this.extramove = false;
	}
	public void fillPit(int pitno,int noofstones) {
		pit[pitno] = noofstones;
	}
	public int getStonesInPit(int pitno) {
		return this.pit[pitno];
	}
	public int removeStonesInPit(int pitno) {
		int stones = this.getStonesInPit(pitno);
		this.pit[pitno] = 0;
		return stones;
	}
}
public class mancala {
	public static BufferedReader br;
	public static BufferedWriter fr_nextstate;
	public static BufferedWriter fr_traversallog;
	public static int algotobeused;
	public static int noofpitsperplayer;
	public static int myplayer;
	public static int cutooffdepth;
	public static int mancala_player[];
	public static MancalaBoard rootnode = null;
	public static MancalaBoard bestmove = null;
	
	public static void main(String[] args) throws IOException {
		try {
//			long startTime = System.currentTimeMillis();
//			System.out.println("Running................");
			File inputFile = new File(args[1]);
			br = new BufferedReader(new FileReader(inputFile));
			algotobeused = Integer.parseInt(br.readLine());
			myplayer = Integer.parseInt(br.readLine());
			cutooffdepth = Integer.parseInt(br.readLine());
			String temp[] = br.readLine().split(" ");
			noofpitsperplayer = temp.length;
			mancala_player = new int[3];
			mancala_player[2] = noofpitsperplayer;
			mancala_player[1]= noofpitsperplayer*2+1;
			rootnode = new MancalaBoard((noofpitsperplayer*2)+2);
			for(int i=(temp.length-1);i>=0;i--)
				rootnode.fillPit(i, Integer.parseInt(temp[(noofpitsperplayer-1)-i]));
			temp = br.readLine().split(" ");
			for(int i=0;i<temp.length;i++)
				rootnode.fillPit((noofpitsperplayer+1)+i, Integer.parseInt(temp[i]));
			rootnode.fillPit(mancala_player[2], Integer.parseInt(br.readLine()));
			rootnode.fillPit(mancala_player[1], Integer.parseInt(br.readLine()));
			switch(algotobeused) {
			case 1:
				fr_nextstate = new BufferedWriter(new FileWriter("next_state.txt")); 
				bestmove = new MancalaBoard();
				bestmove.utility = -999999999;
				doGreedy(rootnode,myplayer);
//				System.out.println("-----------------------------------");
//				printPath(bestmove);
//				displayPit(rootnode);
				writeInNextState(bestmove);
				break;
			case 2:
				fr_nextstate = new BufferedWriter(new FileWriter("next_state.txt")); 
				fr_traversallog = new BufferedWriter(new FileWriter("traverse_log.txt")); 
				fr_traversallog.append("Node,Depth,Value");
				fr_traversallog.newLine();
				rootnode.Name="root";
				rootnode.depth=0;
				rootnode.utility=Integer.MIN_VALUE;
				doMiniMax(rootnode,myplayer);
//				System.out.println("-------------Best Move----------------------");
//				displayPit(bestmove);
				writeInNextState(bestmove);
				fr_traversallog.close();
				break;
			case 3:
				fr_nextstate = new BufferedWriter(new FileWriter("next_state.txt")); 
				fr_traversallog = new BufferedWriter(new FileWriter("traverse_log.txt")); 
				fr_traversallog.append("Node,Depth,Value,Alpha,Beta");
				fr_traversallog.newLine();
				rootnode.Name="root";
				rootnode.depth=0;
				rootnode.utility=Integer.MIN_VALUE;
				doalphaBeta(rootnode,myplayer);
//				System.out.println("-------------Best Move----------------------");
//				displayPit(bestmove);
				writeInNextState(bestmove);
				fr_traversallog.close();
				break;
			case 4:
				break;
			}
			br.close();
//			long endTime = System.currentTimeMillis();
//			System.out.println("Program Completed................");
//			System.out.println("Took "+(endTime - startTime) + " ms"); 
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally{
			if(br != null)
				br.close();
			if(fr_nextstate != null)
				fr_nextstate.close();
			if(fr_traversallog != null)
				fr_traversallog.close();
		}
	}
	
	private static void doalphaBeta(MancalaBoard board, int player){
//		System.out.println(board.Name+","+board.depth+","+board.utility);
		writeInTraversalLogalphaBeta(board.Name,board.depth,board.utility,Integer.MIN_VALUE,Integer.MAX_VALUE);
		bestmove = alphaBetaMaxValue(board,player,0,Integer.MIN_VALUE,Integer.MAX_VALUE);
	}
	
	
	
	private static MancalaBoard alphaBetaMaxValue(MancalaBoard currentstate, int activeplayer, int currentdepth, int alpha, int beta) {
		currentdepth++;
		
		if(currentdepth>cutooffdepth) {
			if(activeplayer==1) {
				currentstate.utility = currentstate.getStonesInPit(mancala_player[1]) - currentstate.getStonesInPit(mancala_player[2]);
//				System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
				writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
			}
			else {
				currentstate.utility = currentstate.getStonesInPit(mancala_player[2]) - currentstate.getStonesInPit(mancala_player[1]);
//				System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
				writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
			}
			return currentstate;
		}
		
		if ((isTerminalState(currentstate,(noofpitsperplayer+1),(noofpitsperplayer*2))) && (activeplayer==1)) {
			currentstate.utility = currentstate.getStonesInPit(mancala_player[1]) - currentstate.getStonesInPit(mancala_player[2]);
//			System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
			writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
			return currentstate;
		}
		else if ((isTerminalState(currentstate,0,(noofpitsperplayer-1))) && (activeplayer==2)) {
			currentstate.utility = currentstate.getStonesInPit(mancala_player[2]) - currentstate.getStonesInPit(mancala_player[1]);
//			System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
			writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
			return currentstate;
		}
		
		
		MancalaBoard v = new MancalaBoard();
		v.utility = Integer.MIN_VALUE;		
		MancalaBoard tempnode = new MancalaBoard(currentstate.pit,currentstate.Name,currentstate.utility,currentstate.depth);
		tempnode.parentnode = currentstate;
		
		if(activeplayer==1){
			for(int i=(noofpitsperplayer+1);i<=(noofpitsperplayer*2);i++) {
				if(tempnode.pit[i]!=0) {
					tempnode.Name = "B"+((i-noofpitsperplayer)+1);
					tempnode.depth = currentdepth;
					if(doMove(tempnode,i,mancala_player[1],mancala_player[2],(noofpitsperplayer+1),(noofpitsperplayer*2),0,(noofpitsperplayer-1))) {
						tempnode.utility = Integer.MIN_VALUE;
						tempnode.extramove = true;
//						System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility+","+alpha+","+beta);
						writeInTraversalLogalphaBeta(tempnode.Name,tempnode.depth,tempnode.utility,alpha,beta);
						MancalaBoard v1 = alphaBetaMaxValue(tempnode,activeplayer,(currentdepth-1),alpha,beta);
						if(v1.utility > v.utility) {
							v = v1;
							currentstate.utility = v1.utility;
						}	
					}
					else {
						tempnode.utility = Integer.MAX_VALUE;
						if(tempnode.depth != cutooffdepth) {
//							System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility+","+alpha+","+beta);
							writeInTraversalLogalphaBeta(tempnode.Name,tempnode.depth,tempnode.utility,alpha,beta);
						}
						MancalaBoard v1 = alphaBetaMinValue(tempnode,activeplayer,currentdepth,alpha,beta);
						if(v1.utility > v.utility) {
							v = tempnode;
							currentstate.utility = v1.utility;
						}	
					}	
					if(v.utility >= beta){
//						System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
						writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
						return v;
					}	
					if(v.utility > alpha)
						alpha = v.utility;
//					System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
					writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
					tempnode = new MancalaBoard(currentstate.pit,currentstate.Name,currentstate.utility,currentstate.depth);
					tempnode.parentnode = currentstate;
				}
			}
		}
		else {
			for(int i=(noofpitsperplayer-1);i>=0;i--) {
				if(tempnode.pit[i]!=0) {
					tempnode.Name = "A"+((noofpitsperplayer-i)+1);
					tempnode.depth = currentdepth;
					if(doMove(tempnode,i,mancala_player[2],mancala_player[1],0,(noofpitsperplayer-1),(noofpitsperplayer+1),(noofpitsperplayer*2))) {
						tempnode.utility = Integer.MIN_VALUE;
						tempnode.extramove = true;
//						System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility+","+alpha+","+beta);
						writeInTraversalLogalphaBeta(tempnode.Name,tempnode.depth,tempnode.utility,alpha,beta);
						MancalaBoard v1 = alphaBetaMaxValue(tempnode,activeplayer,(currentdepth-1),alpha,beta);
						if(v1.utility > v.utility) {
							v = v1;
							currentstate.utility = v1.utility;
						}			
					}
					else {
						tempnode.utility = Integer.MAX_VALUE;
						if(tempnode.depth != cutooffdepth) {
//							System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility+","+alpha+","+beta);
							writeInTraversalLogalphaBeta(tempnode.Name,tempnode.depth,tempnode.utility,alpha,beta);
						}
						MancalaBoard v1 = alphaBetaMinValue(tempnode,activeplayer,currentdepth,alpha,beta);
						if(v1.utility > v.utility) {
							v = tempnode;
							currentstate.utility = v1.utility;
						}	
					}					
					if(v.utility >= beta){
//						System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
						writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
						return v;
					}
					if(v.utility > alpha)
						alpha = v.utility;
//					System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
					writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
					tempnode = new MancalaBoard(currentstate.pit,currentstate.Name,currentstate.utility,currentstate.depth);
					tempnode.parentnode = currentstate;
				}
			}			
		}
		return v;
	}

	
	
	private static MancalaBoard alphaBetaMinValue(MancalaBoard currentstate, int activeplayer, int currentdepth, int alpha, int beta) {
		currentdepth++;
		if(currentdepth>cutooffdepth) {
			if(activeplayer==1) {
				currentstate.utility = currentstate.getStonesInPit(mancala_player[1]) - currentstate.getStonesInPit(mancala_player[2]);
//				System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
				writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
			}
			else {
				currentstate.utility = currentstate.getStonesInPit(mancala_player[2]) - currentstate.getStonesInPit(mancala_player[1]);
//				System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
				writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
			}
			return currentstate;
		}
		
		if ((isTerminalState(currentstate,(noofpitsperplayer+1),(noofpitsperplayer*2))) && (activeplayer==1)) {
			currentstate.utility = currentstate.getStonesInPit(mancala_player[1]) - currentstate.getStonesInPit(mancala_player[2]);
//			System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
			writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
			return currentstate;
		}
		else if ((isTerminalState(currentstate,0,(noofpitsperplayer-1))) && (activeplayer==2)) {
			currentstate.utility = currentstate.getStonesInPit(mancala_player[2]) - currentstate.getStonesInPit(mancala_player[1]);
//			System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
			writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
			return currentstate;
		}
		
		MancalaBoard v = new MancalaBoard();
		v.utility = Integer.MAX_VALUE;
		
		MancalaBoard tempnode = new MancalaBoard(currentstate.pit,currentstate.Name,currentstate.utility,currentstate.depth);
		tempnode.parentnode = currentstate;	
		
		if(activeplayer==1){
			for(int i=(noofpitsperplayer-1);i>=0;i--) {
				if(tempnode.pit[i]!=0) {
					tempnode.Name = "A"+((noofpitsperplayer-i)+1);
					tempnode.depth = currentdepth;
					if(doMove(tempnode,i,mancala_player[2],mancala_player[1],0,(noofpitsperplayer-1),(noofpitsperplayer+1),(noofpitsperplayer*2))) {
						tempnode.utility = Integer.MAX_VALUE;
						tempnode.extramove = true;
//						System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility+","+alpha+","+beta);
						writeInTraversalLogalphaBeta(tempnode.Name,tempnode.depth,tempnode.utility,alpha,beta);
						MancalaBoard v1 = alphaBetaMinValue(tempnode,activeplayer,(currentdepth-1),alpha,beta);
						if(v1.utility < v.utility) {
							v = v1;
							currentstate.utility = v1.utility;
						}	
					}
					else {
						tempnode.utility = Integer.MIN_VALUE;
						if(tempnode.depth != cutooffdepth) {
//							System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility+","+alpha+","+beta);
							writeInTraversalLogalphaBeta(tempnode.Name,tempnode.depth,tempnode.utility,alpha,beta);
						}
						MancalaBoard v1 = alphaBetaMaxValue(tempnode,activeplayer,currentdepth,alpha,beta);
						if(v1.utility < v.utility) {
							v = tempnode;
							currentstate.utility = v1.utility;
						}
					}						
					
					if(v.utility <= alpha) {
//						System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
						writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
						return v;
					}
					if(v.utility < beta)
						beta = v.utility;
//					System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
					writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
					
					tempnode = new MancalaBoard(currentstate.pit,currentstate.Name,currentstate.utility,currentstate.depth);
					tempnode.parentnode = currentstate;
				}
			}
		}
		else {
			for(int i=(noofpitsperplayer+1);i<=(noofpitsperplayer*2);i++) {
				if(tempnode.pit[i]!=0) {
					tempnode.Name = "B"+((i-noofpitsperplayer)+1);
					tempnode.depth = currentdepth;
					if(doMove(tempnode,i,mancala_player[1],mancala_player[2],(noofpitsperplayer+1),(noofpitsperplayer*2),0,(noofpitsperplayer-1))) {
						tempnode.utility = Integer.MAX_VALUE;
						tempnode.extramove = true;
//						System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility+","+alpha+","+beta);
						writeInTraversalLogalphaBeta(tempnode.Name,tempnode.depth,tempnode.utility,alpha,beta);
						MancalaBoard v1 = alphaBetaMinValue(tempnode,activeplayer,(currentdepth-1),alpha,beta);
						if(v1.utility < v.utility) {
							v = v1;
							currentstate.utility = v1.utility;
						}	
					}
					else {
						tempnode.utility = Integer.MIN_VALUE;
						if(tempnode.depth != cutooffdepth) {
//							System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility+","+alpha+","+beta);
							writeInTraversalLogalphaBeta(tempnode.Name,tempnode.depth,tempnode.utility,alpha,beta);
						}
						MancalaBoard v1 = alphaBetaMaxValue(tempnode,activeplayer,currentdepth,alpha,beta);
						if(v1.utility < v.utility) {
							v = tempnode;
							currentstate.utility = v1.utility;
						}
					}	
					if(v.utility <= alpha) {
//						System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
						writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
						return v;
					}
					if(v.utility < beta)
						beta = v.utility;
//					System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility+","+alpha+","+beta);
					writeInTraversalLogalphaBeta(currentstate.Name,currentstate.depth,currentstate.utility,alpha,beta);
					
					tempnode = new MancalaBoard(currentstate.pit,currentstate.Name,currentstate.utility,currentstate.depth);
					tempnode.parentnode = currentstate;
				}
			}
		}
		return v;
		
	}

	private static void doMiniMax(MancalaBoard board, int player) {

//		System.out.println(board.Name+","+board.depth+","+board.utility);
		writeInTraversalLog(board.Name,board.depth,board.utility);
		bestmove = MaxValue(board,player,0);	
	}
	
	private static MancalaBoard MaxValue(MancalaBoard currentstate,int activeplayer,int currentdepth){
		currentdepth++;
		if(currentdepth>cutooffdepth) {
			if(activeplayer==1) {
				currentstate.utility = currentstate.getStonesInPit(mancala_player[1]) - currentstate.getStonesInPit(mancala_player[2]);
//				System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility);
				writeInTraversalLog(currentstate.Name,currentstate.depth,currentstate.utility);
			}
			else {
				currentstate.utility = currentstate.getStonesInPit(mancala_player[2]) - currentstate.getStonesInPit(mancala_player[1]);
//				System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility);
				writeInTraversalLog(currentstate.Name,currentstate.depth,currentstate.utility);
			}
			return currentstate;
		}
		if ((isTerminalState(currentstate,(noofpitsperplayer+1),(noofpitsperplayer*2))) && (activeplayer==1)) {
			currentstate.utility = currentstate.getStonesInPit(mancala_player[1]) - currentstate.getStonesInPit(mancala_player[2]);
//			System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility);
			writeInTraversalLog(currentstate.Name,currentstate.depth,currentstate.utility);
			return currentstate;
		}
		else if ((isTerminalState(currentstate,0,(noofpitsperplayer-1))) && (activeplayer==2)) {
			currentstate.utility = currentstate.getStonesInPit(mancala_player[2]) - currentstate.getStonesInPit(mancala_player[1]);
//			System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility);
			writeInTraversalLog(currentstate.Name,currentstate.depth,currentstate.utility);
			return currentstate;
		}
		
		MancalaBoard v = new MancalaBoard();
		v.utility = Integer.MIN_VALUE;		
		MancalaBoard tempnode = new MancalaBoard(currentstate.pit,currentstate.Name,currentstate.utility,currentstate.depth);
		tempnode.parentnode = currentstate;
		
		if(activeplayer==1){
			for(int i=(noofpitsperplayer+1);i<=(noofpitsperplayer*2);i++) {
				if(tempnode.pit[i]!=0) {
					tempnode.Name = "B"+((i-noofpitsperplayer)+1);
					tempnode.depth = currentdepth;
					if(doMove(tempnode,i,mancala_player[1],mancala_player[2],(noofpitsperplayer+1),(noofpitsperplayer*2),0,(noofpitsperplayer-1))) {
						tempnode.utility = Integer.MIN_VALUE;
						tempnode.extramove = true;
//						System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility);
						writeInTraversalLog(tempnode.Name,tempnode.depth,tempnode.utility);
						MancalaBoard v1 = MaxValue(tempnode,activeplayer,(currentdepth-1));
						if(v1.utility > v.utility) {
							v = v1;
							currentstate.utility = v1.utility;
						}						
					}
					else {
						tempnode.utility = Integer.MAX_VALUE;
						if(tempnode.depth != cutooffdepth) {
//							System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility);
							writeInTraversalLog(tempnode.Name,tempnode.depth,tempnode.utility);
						}
						MancalaBoard v1 = MinValue(tempnode,activeplayer,currentdepth);
						if(v1.utility > v.utility) {
							v = tempnode;
							currentstate.utility = v1.utility;
						}	
					}					
//					System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility);
					writeInTraversalLog(currentstate.Name,currentstate.depth,currentstate.utility);
					tempnode = new MancalaBoard(currentstate.pit,currentstate.Name,currentstate.utility,currentstate.depth);
					tempnode.parentnode = currentstate;
				}
			}
		}
		else {
			for(int i=(noofpitsperplayer-1);i>=0;i--) {
				if(tempnode.pit[i]!=0) {
					tempnode.Name = "A"+((noofpitsperplayer-i)+1);
					tempnode.depth = currentdepth;
					if(doMove(tempnode,i,mancala_player[2],mancala_player[1],0,(noofpitsperplayer-1),(noofpitsperplayer+1),(noofpitsperplayer*2))) {
						tempnode.utility = Integer.MIN_VALUE;
						tempnode.extramove = true;
//						System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility);
						writeInTraversalLog(tempnode.Name,tempnode.depth,tempnode.utility);
						MancalaBoard v1 = MaxValue(tempnode,activeplayer,(currentdepth-1));
						if(v1.utility > v.utility) {
							v = v1;
							currentstate.utility = v1.utility;
						}						
					}
					else {
						tempnode.utility = Integer.MAX_VALUE;
						if(tempnode.depth != cutooffdepth) {
//							System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility);
							writeInTraversalLog(tempnode.Name,tempnode.depth,tempnode.utility);
						}
						MancalaBoard v1 = MinValue(tempnode,activeplayer,currentdepth);
						if(v1.utility > v.utility) {
							v = tempnode;
							currentstate.utility = v1.utility;
						}	
					}					
//					System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility);
					writeInTraversalLog(currentstate.Name,currentstate.depth,currentstate.utility);
					tempnode = new MancalaBoard(currentstate.pit,currentstate.Name,currentstate.utility,currentstate.depth);
					tempnode.parentnode = currentstate;
				}
			}			
		}
		return v;
	}
	private static MancalaBoard MinValue(MancalaBoard currentstate,int activeplayer,int currentdepth){
		currentdepth++;
		if(currentdepth>cutooffdepth) {
			if(activeplayer==1) {
				currentstate.utility = currentstate.getStonesInPit(mancala_player[1]) - currentstate.getStonesInPit(mancala_player[2]);
//				System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility);
				writeInTraversalLog(currentstate.Name,currentstate.depth,currentstate.utility);
			}
			else {
				currentstate.utility = currentstate.getStonesInPit(mancala_player[2]) - currentstate.getStonesInPit(mancala_player[1]);
//				System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility);
				writeInTraversalLog(currentstate.Name,currentstate.depth,currentstate.utility);
			}
			return currentstate;
		}
		
		if ((isTerminalState(currentstate,(noofpitsperplayer+1),(noofpitsperplayer*2))) && (activeplayer==1)) {
			currentstate.utility = currentstate.getStonesInPit(mancala_player[1]) - currentstate.getStonesInPit(mancala_player[2]);
//			System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility);
			writeInTraversalLog(currentstate.Name,currentstate.depth,currentstate.utility);
			return currentstate;
		}
		else if ((isTerminalState(currentstate,0,(noofpitsperplayer-1))) && (activeplayer==2)) {
			currentstate.utility = currentstate.getStonesInPit(mancala_player[2]) - currentstate.getStonesInPit(mancala_player[1]);
//			System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility);
			writeInTraversalLog(currentstate.Name,currentstate.depth,currentstate.utility);
			return currentstate;
		}
		
		MancalaBoard v = new MancalaBoard();
		v.utility = Integer.MAX_VALUE;
		
		MancalaBoard tempnode = new MancalaBoard(currentstate.pit,currentstate.Name,currentstate.utility,currentstate.depth);
		tempnode.parentnode = currentstate;	
		
		if(activeplayer==1){
			for(int i=(noofpitsperplayer-1);i>=0;i--) {
				if(tempnode.pit[i]!=0) {
					tempnode.Name = "A"+((noofpitsperplayer-i)+1);
					tempnode.depth = currentdepth;
					if(doMove(tempnode,i,mancala_player[2],mancala_player[1],0,(noofpitsperplayer-1),(noofpitsperplayer+1),(noofpitsperplayer*2))) {
						tempnode.utility = Integer.MAX_VALUE;
						tempnode.extramove = true;
//						System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility);
						writeInTraversalLog(tempnode.Name,tempnode.depth,tempnode.utility);
						MancalaBoard v1 = MinValue(tempnode,activeplayer,(currentdepth-1));
						if(v1.utility < v.utility) {
							v = v1;
							currentstate.utility = v1.utility;
						}	
					}
					else {
						tempnode.utility = Integer.MIN_VALUE;
						if(tempnode.depth != cutooffdepth) {
//							System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility);
							writeInTraversalLog(tempnode.Name,tempnode.depth,tempnode.utility);
						}
						MancalaBoard v1 = MaxValue(tempnode,activeplayer,currentdepth);
						if(v1.utility < v.utility) {
							v = tempnode;
							currentstate.utility = v1.utility;
						}
					}						
//					System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility);
					writeInTraversalLog(currentstate.Name,currentstate.depth,currentstate.utility);
					tempnode = new MancalaBoard(currentstate.pit,currentstate.Name,currentstate.utility,currentstate.depth);
					tempnode.parentnode = currentstate;
				}
			}
		}
		else {
			for(int i=(noofpitsperplayer+1);i<=(noofpitsperplayer*2);i++) {
				if(tempnode.pit[i]!=0) {
					tempnode.Name = "B"+((i-noofpitsperplayer)+1);
					tempnode.depth = currentdepth;
					if(doMove(tempnode,i,mancala_player[1],mancala_player[2],(noofpitsperplayer+1),(noofpitsperplayer*2),0,(noofpitsperplayer-1))) {
						tempnode.utility = Integer.MAX_VALUE;
						tempnode.extramove = true;
//						System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility);
						writeInTraversalLog(tempnode.Name,tempnode.depth,tempnode.utility);
						MancalaBoard v1 = MinValue(tempnode,activeplayer,(currentdepth-1));
						if(v1.utility < v.utility) {
							v = v1;
							currentstate.utility = v1.utility;
						}	
					}
					else {
						tempnode.utility = Integer.MIN_VALUE;
						if(tempnode.depth != cutooffdepth) {
//							System.out.println(tempnode.Name+","+tempnode.depth+","+tempnode.utility);
							writeInTraversalLog(tempnode.Name,tempnode.depth,tempnode.utility);
						}
						MancalaBoard v1 = MaxValue(tempnode,activeplayer,currentdepth);
						if(v1.utility < v.utility) {
							v = tempnode;
							currentstate.utility = v1.utility;
						}
					}	
					
//					System.out.println(currentstate.Name+","+currentstate.depth+","+currentstate.utility);
					writeInTraversalLog(currentstate.Name,currentstate.depth,currentstate.utility);
					tempnode = new MancalaBoard(currentstate.pit,currentstate.Name,currentstate.utility,currentstate.depth);
					tempnode.parentnode = currentstate;
				}
			}
		}
		return v;
	}	
	private static void doGreedy(MancalaBoard currentstate, int activeplayer){
		MancalaBoard tempnode = new MancalaBoard(currentstate.pit);
		tempnode.parentnode = currentstate;
		boolean extramove = false;		
		if(activeplayer == 1) {
			for(int i=(noofpitsperplayer+1);i<=(noofpitsperplayer*2);i++) {
				if(tempnode.pit[i]!=0) {
					extramove = doMove(tempnode,i,mancala_player[1],mancala_player[2],(noofpitsperplayer+1),(noofpitsperplayer*2),0,(noofpitsperplayer-1));
					if(extramove)
						doGreedy(tempnode,activeplayer);
					else {
						tempnode.utility = tempnode.getStonesInPit(mancala_player[1]) - tempnode.getStonesInPit(mancala_player[2]);
						if((tempnode.utility > bestmove.utility))
							bestmove = tempnode;
					}
					tempnode = new MancalaBoard(currentstate.pit);
					tempnode.parentnode = currentstate;
				}
			}
		}
		else {
			for(int i=(noofpitsperplayer-1);i>=0;i--) {
				if(tempnode.pit[i]!=0) {
					extramove = doMove(tempnode,i,mancala_player[2],mancala_player[1],0,(noofpitsperplayer-1),(noofpitsperplayer+1),(noofpitsperplayer*2));
					if(extramove)
						doGreedy(tempnode,activeplayer);
					else {
						tempnode.utility = tempnode.getStonesInPit(mancala_player[2]) - tempnode.getStonesInPit(mancala_player[1]);
						if((tempnode.utility > bestmove.utility))
							bestmove = tempnode;
					}
					tempnode = new MancalaBoard(currentstate.pit);
					tempnode.parentnode = currentstate;
				}	
			}				
		}
	}		
	private static boolean doMove(MancalaBoard board, int pitnum,int player_mancala,int opp_player_mancala,int startpit,int endpit,int oppstartpit,int oppendpit) {
//		int tempitnum = pitnum;
		int noofstones = board.removeStonesInPit(pitnum);		
		boolean extramove = false;
		for (int i=0;i<noofstones;i++) {
			pitnum++;
			if(pitnum>(noofpitsperplayer*2)+1)
				pitnum=0;
			if(pitnum!=opp_player_mancala)
				board.pit[pitnum]+=1;
			else
				noofstones++;
		}	
		if(pitnum==player_mancala)
			extramove = true;
		else if((board.pit[pitnum]==1) && ((pitnum>=startpit) && (pitnum<=endpit))){
			//&& (board.pit[(noofpitsperplayer*2)-pitnum]!=0)
			board.pit[player_mancala] += board.removeStonesInPit((noofpitsperplayer*2)-pitnum);
			board.pit[player_mancala] += board.removeStonesInPit(pitnum);
		}
		
		if(isTerminalState(board,startpit,endpit)) {
			for(int i=oppstartpit;i<=oppendpit;i++)
				board.pit[opp_player_mancala] += board.removeStonesInPit(i);
		}
		
		if(isTerminalState(board,oppstartpit,oppendpit)) {
			for(int i=startpit;i<=endpit;i++)
				board.pit[player_mancala] += board.removeStonesInPit(i);
		}
		
		return extramove;
	}	
	private static boolean isTerminalState(MancalaBoard board,int startpit,int endpit) {
		boolean allpitempty = true;
		for (int i= startpit;i<=endpit;i++) {
			if(board.pit[i]!=0) {
				allpitempty  = false;
				break;
			}
		}
		return allpitempty;
	}
//	private static void printPath(MancalaBoard board) {
//		while(board.parentnode!=null) {
//			displayPit(board);
//			board = board.parentnode;
//		}
//	}
//	private static void displayPit(MancalaBoard board){
//		int i;
//		System.out.println("-----------------------------------------");
//		for(i=noofpitsperplayer-1;i>=0;i--)
////		for(i=0;i<=(noofpitsperplayer-1);i++)
//			System.out.print(board.pit[i]+" ");
//		System.out.println("");
//		for(i=noofpitsperplayer+1;i<=(noofpitsperplayer*2);i++)
//			System.out.print(board.pit[i]+" ");
//		System.out.println("");
//		System.out.print(board.pit[mancala_player[2]]);
//		System.out.println("");
//		System.out.print(board.pit[mancala_player[1]]);
//		System.out.println("");
//		System.out.println("Utility "+board.utility);
//		System.out.println("-----------------------------------------");
//	}
	
	private static void writeInNextState(MancalaBoard board){	
		int i;
		try {
			for(i=noofpitsperplayer-1;i>=0;i--)
				fr_nextstate.append(board.pit[i]+" ");
			fr_nextstate.newLine();
			for(i=noofpitsperplayer+1;i<=(noofpitsperplayer*2);i++)
				fr_nextstate.append(board.pit[i]+" ");
			fr_nextstate.newLine();
			fr_nextstate.append(Integer.toString(board.pit[mancala_player[2]]));
			fr_nextstate.newLine();
			fr_nextstate.append(Integer.toString(board.pit[mancala_player[1]]));
			fr_nextstate.close();
		}catch (IOException e) {
				e.printStackTrace();
		}
		
	}
	
	private static void writeInTraversalLog(String node,int depth,int value) {
		try {
			if(value == Integer.MAX_VALUE)
				fr_traversallog.append(node+","+depth+",Infinity");
			else if(value == Integer.MIN_VALUE)
				fr_traversallog.append(node+","+depth+",-Infinity");
			else
				fr_traversallog.append(node+","+depth+","+value);
			fr_traversallog.newLine();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private static void writeInTraversalLogalphaBeta(String node,int depth,int value,int alpha,int beta) {
		try {
			String valuef = Integer.toString(value);
			String alphaf = Integer.toString(alpha);
			String betaf = Integer.toString(beta);
			if(value == Integer.MAX_VALUE)
				valuef="Infinity";
			else if(value == Integer.MIN_VALUE)
				valuef="-Infinity";
			if(alpha == Integer.MIN_VALUE)
				alphaf="-Infinity";
			if(beta == Integer.MAX_VALUE)
				betaf="Infinity";
			fr_traversallog.append(node+","+depth+","+valuef+","+alphaf+","+betaf);
			fr_traversallog.newLine();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}

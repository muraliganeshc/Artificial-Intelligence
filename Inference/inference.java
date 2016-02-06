package inference;

import java.io.*;
import java.util.*;


class KB {
	String name;
	int noofarguments;
	ArrayList<String> variables;
	ArrayList<String> facts;
	ArrayList<String> lhs;
	ArrayList<String> RHS;
	boolean hasmore;
	KB() {
		name = "";
		noofarguments = 0;
		variables = new ArrayList<String>();
		facts = new ArrayList<String>();
		lhs = new ArrayList<String>();
		RHS = new ArrayList<String>();
	}
}

public class inference {
	//static variable declaration goes here
	public static BufferedReader br = null;
	public static BufferedWriter wr = null;
	public static ArrayList<String> queries;
	public static ArrayList<String> entailed;
	public static List<String> toprove;
	public static Map<String,KB> index;
	public static Map<String,String> substitution;
	
	public static void main(String[] args) throws Exception {
		try {
			File filename = null;
	        File outputfilename = null;
	        if(args.length > 0) {
                filename = new File(args[1]);
                outputfilename = new File("output.txt");
            }
	        br = new BufferedReader(new FileReader(filename));
            wr = new BufferedWriter(new FileWriter(outputfilename));
			doreadInput();
			//doPrintQueries();
			//parsed = new ArrayList<String>();
			for(int i=0;i<queries.size();i++) {
				//parsed.clear();
				if(queryinFact(queries.get(i))) {
					System.out.println("True");
					wr.append("TRUE");
					wr.newLine();
					continue;
				}
				ArrayList<HashMap<String,String>> theta = FOLBCOR(queries.get(i),1,new ArrayList<HashMap<String,String>>(),new ArrayList<String>());
				if(theta.size()>=1){
					System.out.println("True");
					wr.append("TRUE");
					wr.newLine();
				}
				else {
					System.out.println("False");
					wr.append("FALSE");
					wr.newLine();
				}
			}
			br.close();
			wr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		finally {
			br.close();
			wr.close();
		}
	}

	private static boolean queryinFact(String query) {
		// TODO Auto-generated method stub
		boolean found = false;
		KB kbobj = doGetKBObject(query);
		if(kbobj == null)
			return found;		
		ArrayList<String> all_facts = kbobj.facts;
		if(all_facts.contains(query))
			found = true;
		
		return found;
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<HashMap<String,String>> FOLBCOR(String query,int level,ArrayList<HashMap<String,String>> theta,ArrayList<String> ParsedList) throws Exception {
		// TODO Backward chaining algorithm
		
		ArrayList<HashMap<String,String>> theta_and = new ArrayList<HashMap<String,String>>();
		ArrayList<HashMap<String,String>> theta_or = new ArrayList<HashMap<String,String>>();
		ArrayList<HashMap<String,String>> theta_copy = new ArrayList<HashMap<String,String>>();
		theta_copy = (ArrayList<HashMap<String, String>>) theta.clone();
		ArrayList<String> all_goals = new ArrayList<String>();	
		ArrayList<String> ParsedList_or = new ArrayList<String>();
		ParsedList_or.addAll(ParsedList);
		
		if(queryinFact(query))
			return theta;
			
		if(ParsedList_or.contains(query))
			return theta_and;
		
		ParsedList_or.add(query);
		
		KB kbobj = doGetKBObject(query);
		if(kbobj == null)
			return theta_and;		
		ArrayList<String> all_clause = kbobj.lhs;
		ArrayList<String> all_facts = kbobj.facts;
		all_goals.addAll(all_clause);
		all_goals.addAll(all_facts);
			
		for(int i=0;i<all_goals.size();i++) {	
			ArrayList<HashMap<String,String>> work_theta = new ArrayList<HashMap<String,String>>();
			if(i<all_clause.size()){			
				HashMap<String,String> theta_unify = unifyClause(kbobj.RHS.get(i),query,theta);				
				if(theta_unify == null || theta_unify.size() == 0) {
					theta.clear();
					return theta;
				}
				work_theta.add(theta_unify);					
				theta_and = FOLBCAND(all_goals.get(i),work_theta,level,ParsedList_or);
				theta_and = substvar(work_theta,theta_and);
				if ((level == 1)&&(theta_and.size() >= 1)) {
					return theta_and;
				}
				/*if(theta_and.size() == 0 || theta_and.isEmpty()) {
					work_theta.remove(0);
				}*/
				theta_or.addAll(theta_and);
			}
			else {
				HashMap<String,String>theta_unify = unifyClause(all_goals.get(i),query,theta);
				if(theta_unify != null && theta_unify.size() != 0) {
					work_theta.add(theta_unify);
					work_theta = substvar(theta_copy,work_theta);
					theta_or.addAll(work_theta);;
				}
				
			}
		}	
		return theta_or;
	}
	

	@SuppressWarnings("unchecked")
	private static ArrayList<HashMap<String, String>> substvar(ArrayList<HashMap<String, String>> theta_copy,
			ArrayList<HashMap<String, String>> theta_and) {
		// TODO Auto-generated method stub
		HashMap<String, String> temp = new HashMap<String,String>();
		ArrayList<HashMap<String, String>> theta_and_clone =(ArrayList<HashMap<String, String>>) theta_and.clone();
		temp = theta_copy.get(0);
		Set<String> keyset = temp.keySet();
	    ArrayList<String> varkeys = new ArrayList<String>();
	    Iterator<String> keys = keyset.iterator();
	    while(keys.hasNext()){
	    	String t = keys.next();
	    	if(!isConstant(temp.get(t))){
	    		varkeys.add(t);
	    	}
	    }
	    for(int i=0;i<theta_and.size();i++){
	    	for(int x=0;x<varkeys.size();x++) {
	    		if(theta_and.get(i).containsKey(varkeys.get(x))){
	    			String value = theta_and_clone.get(i).get(varkeys.get(x));
	    			//theta_and_clone.get(i).remove(varkeys.get(x));
	    			theta_and_clone.get(i).put(temp.get(varkeys.get(x)), value);
	    		}
	    	}
	    	
	    }
	    return theta_and_clone;
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<HashMap<String, String>> FOLBCAND(String goals,
			ArrayList<HashMap<String, String>> theta,int level,
			ArrayList<String> parsedList) throws Exception {
		// TODO FOLBCAND Method
		
		String firstgoal = goals;
		String restgoal = null;
		ArrayList<HashMap<String,String>> theta_and = new ArrayList<HashMap<String,String>>();
		ArrayList<HashMap<String,String>> theta_copy = new ArrayList<HashMap<String,String>>();
		ArrayList<HashMap<String,String>> w2_theta_and = new ArrayList<HashMap<String,String>>();
		theta_copy = (ArrayList<HashMap<String, String>>) theta.clone();
		
		if(goals.contains("^")) {
			firstgoal = goals.substring(0,goals.indexOf("^")-1).trim();
			restgoal = goals.substring(goals.indexOf("^")+1).trim(); 
		}
		
		for(int i=0;i<theta_copy.size();i++) {
			ArrayList<HashMap<String,String>> theta_tosend = new ArrayList<HashMap<String,String>>();
			theta_tosend.add(theta.get(i));
			String firstsub = substitueclause(firstgoal,theta.get(i));
			ArrayList<HashMap<String,String>> w1_theta_and = new ArrayList<HashMap<String,String>>(); 
			w1_theta_and = FOLBCOR(firstsub,++level,theta_tosend,parsedList);	
			/*if(w1_theta_and.size() == 0 || w1_theta_and.isEmpty()) {
				theta_copy.remove(i);
			}*/
			
			if (restgoal != null) {		
				ArrayList<HashMap<String,String>> w1_theta_and_clone = (ArrayList<HashMap<String, String>>) w1_theta_and.clone();
				for (int j=0;j<w1_theta_and_clone.size();j++) {
					for(int x=0;x<theta_copy.size();x++) {
						w1_theta_and.get(j).putAll(theta_copy.get(x));
						HashMap<String, String> hashMap = new HashMap<String, String>();
						hashMap = loopvairable(w1_theta_and.get(j));
						w1_theta_and.remove(j);
						w1_theta_and.add(j,hashMap);
					}
						
				}
				
				for (int j=0;j<w1_theta_and.size();j++) {
					
					ArrayList<HashMap<String,String>> theta_tosend1 = new ArrayList<HashMap<String,String>>();
					theta_tosend1.add(w1_theta_and.get(j));
					w2_theta_and = FOLBCAND(restgoal,theta_tosend1,level,parsedList);
					theta_and.addAll(w2_theta_and);
					/*if(w2_theta_and.size() == 0 || w2_theta_and.isEmpty()) {
						w1_theta_and.remove(j);
						j--;
					}*/
				}
			}
			else
				theta_and.addAll(w1_theta_and);			
		}
		
		return theta_and;
	}
	
	@SuppressWarnings("unchecked")
	private static HashMap<String, String> loopvairable(HashMap<String, String> hashMap) {
		HashMap<String, String> hashMap1 = (HashMap<String, String>) hashMap.clone();
		Set<String> keyset = hashMap.keySet();
		Iterator<String> keys = keyset.iterator();
		while(keys.hasNext()){
	    	String t = keys.next();
	    	if(!isConstant(hashMap.get(t))){
	    		String Value = hashMap.get(t);
	    		if(hashMap.containsKey(Value)) {
	    			hashMap1.remove(t);
	    			hashMap1.put(t, hashMap.get(Value));
	    		}
	    	}
		}
		return hashMap1;
	}

	private static String substitueclause(String firstgoal,	HashMap<String, String> hashMap) throws Exception {
		ArrayList<String> goal_value = getVariables(firstgoal);
		
		for(int i =0;i<goal_value.size();i++) {
			if ( (!isConstant(goal_value.get(i))) && (hashMap.get(goal_value.get(i)) != null) 
					&& (isConstant(hashMap.get(goal_value.get(i)))) ) 
				firstgoal = firstgoal.replaceAll(goal_value.get(i), hashMap.get(goal_value.get(i)));
		}
		
		return firstgoal;
	}

	/*private static HashMap<String, String> unifyfact(String LHS, String query, ArrayList<HashMap<String, String>> theta) throws Exception {
		// TODO Auto-generated method stub
		HashMap<String,String> theta_unify = new HashMap<String,String>()
		
		//theta_unify.putAll(theta.get(0));
		
		ArrayList<String> RHS_value = getVariables(LHS);
		ArrayList<String> goal_value = getVariables(query);
		for(int i =0;i<goal_value.size();i++) {
			if(!isConstant(goal_value.get(i)))
				theta_unify.put(goal_value.get(i),RHS_value.get(i));	
		}

		return theta_unify;
	}*/

	private static HashMap<String,String> unifyClause(String rhs, String goal, ArrayList<HashMap<String, String>> theta) throws Exception {
		// TODO unifyClause Method
		HashMap<String,String> theta_unify = new HashMap<String,String>();
		ArrayList<String> value_added = new ArrayList<String>();
		
		
		ArrayList<String> goal_value = getVariables(goal);
		//ArrayList<String> goal_value = getVariables(goal);
		/*if(theta.size() > 0)
			theta_unify.putAll(theta.get(0));*/
		
		//for(int k=0;k<rhs.size();k++) {
			ArrayList<String> RHS_value = getVariables(rhs);
			/*X Y
			X John
			John X
			John Constant
			X X
			X John, Y John
			 */	
			for(int i =0;i<goal_value.size();i++) {
				
				if(isConstant(goal_value.get(i))) {
					if(value_added.contains(RHS_value.get(i))){
						theta_unify.clear();
						break;
					}
				}
				/*else if(isConstant(RHS_value.get(i))) {
					if(value_added.contains(goal_value.get(i))){
						theta_unify.clear();
						break;
					}
				}*/
				else {
					if(value_added.contains(goal_value.get(i))){
						theta_unify.clear();
						break;
					}
				}
					
			
				//x x
				if(!isConstant(RHS_value.get(i)) && (!isConstant(goal_value.get(i)))) {
					if (theta_unify.containsKey(RHS_value.get(i))) {
						//theta_unify.clear();
						break;
					}
					theta_unify.put(RHS_value.get(i),goal_value.get(i));
					value_added.add(goal_value.get(i));
				}
			
				//x John
				if(!isConstant(RHS_value.get(i)) && (isConstant(goal_value.get(i)))){
					if (theta_unify.containsKey(RHS_value.get(i))) {
						if(!theta_unify.get(RHS_value.get(i)).equals(goal_value.get(i))) {
							theta_unify.clear();
							break;
						}
					}
					theta_unify.put(RHS_value.get(i),goal_value.get(i));
					value_added.add(RHS_value.get(i));
				}
				//John X
				if(isConstant(RHS_value.get(i)) && (!isConstant(goal_value.get(i)))) {
					if (theta_unify.containsKey(goal_value.get(i))) {
						if(!theta_unify.get(goal_value.get(i)).equals(RHS_value.get(i))) {
							theta_unify.clear();
							break;
						}
					}
					theta_unify.put(goal_value.get(i),RHS_value.get(i));
					value_added.add(goal_value.get(i));
				}						
				//John John
				if(isConstant(RHS_value.get(i)) && (isConstant(goal_value.get(i)))) {
					if(!RHS_value.get(i).equals(goal_value.get(i))) { 
						theta_unify.clear();
						break;
					}
				}
			}	
		//}
		return theta_unify;
	}

	private static boolean isConstant(String string) {
		return Character.isUpperCase(string.charAt(0));
	}

	private static KB doGetKBObject(String query) {
		String name = getName(query);
		KB tempobj = index.get(name);
		return tempobj;
	}
	/*private static void doPrintQueries() {
		Iterator<String> iterator = queries.iterator();
		while(iterator.hasNext()) {
			System.out.println("queries -> "+iterator.next().toString());
		}
		
	}*/
	private static void doreadInput() throws IOException {		
        int noofinputs=0;
        ArrayList<String> KBlist = new ArrayList<String>();
        try {
        	noofinputs = Integer.parseInt(br.readLine());
        	queries = new ArrayList<String>();
        	while(noofinputs > 0){
        		queries.add(br.readLine().trim());
        		noofinputs--;
        	}
        	noofinputs = Integer.parseInt(br.readLine());
       	
        	while(noofinputs > 0){
        		KBlist.add(br.readLine());
        		noofinputs--;
        	}
        	
        	ArrayList<String> KBliststd = standKB(KBlist);
        	putInKB(KBliststd);
        }
        catch(Exception e){
        	br.close();
        	wr.close();
        	e.printStackTrace();
        }	
	}
	
	
	
	private static ArrayList<String> standKB(ArrayList<String> kBlist) throws Exception {
		
		ArrayList<String> KBliststd = new ArrayList<String>();
		HashMap<String,String> usedvariable = new HashMap<String,String>();
		
		int count=1;
		String stdvar = "x";
		for(int i=0;i<kBlist.size();i++) {
			
			StringBuffer newtemp = new StringBuffer();
			
			String tempstring = kBlist.get(i);
			if(!tempstring.contains("=>")) {
				System.out.println(tempstring);
				KBliststd.add(tempstring);
				continue;
			}
			String temp1[] = tempstring.split("=>");
			String RHS = temp1[1].trim();
			String LHS = temp1[0].trim();
			
			String LHSsplit[] = LHS.split("\\^");
			
			for(int j=0;j<LHSsplit.length;j++) {				
				newtemp.append(getName(LHSsplit[j])).append("(");
				ArrayList<String> variableLHS = getVariables(LHSsplit[j]);				
				for(int k=0;k<variableLHS.size();k++){
					if(isConstant(variableLHS.get(k))){
						if(k!=variableLHS.size()-1)
							newtemp.append(variableLHS.get(k)).append(",");
						else
							newtemp.append(variableLHS.get(k)).append(")");
						continue;
					}
					if(!usedvariable.containsKey(variableLHS.get(k))){
						usedvariable.put(variableLHS.get(k),stdvar+count);
						count++;
					}
					if(k!=variableLHS.size()-1)
						newtemp.append(usedvariable.get(variableLHS.get(k))).append(",");
					else
						newtemp.append(usedvariable.get(variableLHS.get(k))).append(")");	
						
				}			
				if(j != (LHSsplit.length-1))
					newtemp.append(" ^ ");
			}
			newtemp.append(" => ");
			newtemp.append(getName(RHS)).append("(");
			ArrayList<String> variableRHS = getVariables(RHS);
			for(int k=0;k<variableRHS.size();k++){
				if(isConstant(variableRHS.get(k))){
					if(k!=variableRHS.size()-1)
						newtemp.append(variableRHS.get(k)).append(",");
					else
						newtemp.append(variableRHS.get(k)).append(")");
					continue;
				}
				if(!usedvariable.containsKey(variableRHS.get(k))){
					usedvariable.put(variableRHS.get(k),stdvar+count);
					count++;
				}
				if(k != variableRHS.size()-1)
					newtemp.append(usedvariable.get(variableRHS.get(k))).append(",");
				else
					newtemp.append(usedvariable.get(variableRHS.get(k))).append(")");
			}
			usedvariable.clear();
			KBliststd.add(newtemp.toString());
			System.out.println(newtemp);
		}
		return KBliststd;
	}

	private static void putInKB(ArrayList<String> changedKB) throws Exception {
		boolean fact = false;
		index = new HashMap<String,KB>();
		for(int i=0;i<changedKB.size();i++) {
			String tempstring = changedKB.get(i);
			ArrayList<String> getvar = new ArrayList<String>();
			String RHS = tempstring;
			String LHS = "";
			KB tempobj;
			if(!tempstring.contains("=>")) {
				fact = true;
			}
			else {
				String temp1[] = tempstring.split("=>");
				RHS = temp1[1].trim();
				LHS = temp1[0].trim();
			}
			String key = getName(RHS);
			if(index.containsKey(key)){
				tempobj = index.get(key);
			}
			else {
				tempobj = new KB();
				tempobj.name=key;	
				getvar.addAll(tempobj.variables);
			}
			if(fact)
				tempobj.facts.add(tempstring);
			else {
				tempobj.lhs.add(LHS);
				getvar.addAll(getVariables(RHS));
				tempobj.variables.addAll(getvar); 
				tempobj.RHS.add(RHS);
			}
			index.put(key,tempobj);
			fact = false;
			getvar.clear();
		}	
	}

	private static String getName(String value) {
		String keyvalue[] = value.split("\\(");
		return keyvalue[0].trim();
	}
	
	private static ArrayList<String> getVariables(String Value) throws Exception{
		ArrayList<String> variables = new ArrayList<String>();
		String temp = Value.substring(Value.indexOf("(")+1, Value.indexOf(")"));
		variables = new ArrayList<String> (Arrays.asList(temp.split(",")));
		return variables;
		
	}

}
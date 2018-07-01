package com.qyl.nlp.cky;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
/*
 * 文法包含包含重写规则，非终结符集，终结符集
 */
public class CFG {
	private String type;
	private String startSymbol;
	private Set<String> nonTerminalSet=new HashSet<String>();//非终结符集
	private Set<String> terminalSet=new HashSet<String>();//终结符集
	private Set ruleSet=new HashSet();//规则集
	private HashMap<String,HashSet> ruleMapStartWithlhs=new HashMap<String,HashSet>();//以左部为key值的规则集map
    private HashMap<ArrayList<String>,HashSet> ruleMapStartWithrhs=
    		     new HashMap<ArrayList<String>,HashSet>();//以规则右部为key值的规则集map
	/*
     * 构造函数,一步创建
     */
    public CFG(Set<String> nonTerminalSet, Set<String> terminalSet,
			HashMap<String, HashSet> ruleMapStartWithlhs,
			HashMap<ArrayList<String>,HashSet> ruleMapStartWithrhs) {
		super();
		this.nonTerminalSet = nonTerminalSet;
		this.terminalSet = terminalSet;
		this.ruleMapStartWithlhs = ruleMapStartWithlhs;
		this.ruleMapStartWithrhs = ruleMapStartWithrhs;
		for(String lhs:ruleMapStartWithlhs.keySet()) {
			ruleSet.addAll(ruleMapStartWithlhs.get(lhs));
		}
	}
	/*
	 * 通过一步一步添加rule来实现规则集，终结符/非终结符的更新
	 */
	public CFG() {
	}
	
	/*
	 * 方法
	 */
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStartSymbol() {
		return startSymbol;
	}
	public void setStartSymbol(String startSymbol) {
		this.startSymbol = startSymbol;
	}
    public Set<String> getNonTerminalSet() {
		return nonTerminalSet;
	}
	public void setNonTerminalSet(Set<String> nonTerminalSet) {
		this.nonTerminalSet = nonTerminalSet;
	}
	public Set<String> getTerminalSet() {
		return terminalSet;
	}
	public void setTerminalSet(Set<String> terminalSet) {
		this.terminalSet = terminalSet;
	}
	public HashMap<String, HashSet> getRuleMapStartWithlhs() {
			return ruleMapStartWithlhs;
		}
	public void setRuleMapStartWithlhs(HashMap<String, HashSet> ruleMapStartWithlhs) {
			this.ruleMapStartWithlhs = ruleMapStartWithlhs;
		}
	public HashMap<ArrayList<String>,HashSet> getRuleMapStartWithrhs() {
			return ruleMapStartWithrhs;
		}
	public void setRuleMapStartWithrhs(HashMap<ArrayList<String>,HashSet> ruleMapStartWithrhs) {
			this.ruleMapStartWithrhs = ruleMapStartWithrhs;
		}
		/*
		 * 添加单个规则
		 */
	public void add(RewriteRule rule) {
	  ruleSet.add(rule);
	  if(ruleMapStartWithlhs.get(rule.getLhs())!=null) {
		  if(!ruleMapStartWithlhs.get(rule.getLhs()).contains(rule)) {
			  ruleMapStartWithlhs.get(rule.getLhs()).add(rule);
		  }  
  	  }else {
		 HashSet set=new HashSet();
		 set.add(rule);
		 ruleMapStartWithlhs.put(rule.getLhs(),set);
  	  }
	  if(ruleMapStartWithrhs.keySet().contains(rule.getRhs())) {
		  ruleMapStartWithrhs.get(rule.getRhs()).add(rule); 
	  }else {
		  HashSet set=new  HashSet();
		  set.add(rule);
		  ruleMapStartWithrhs.put(rule.getRhs(), set);
	  }
	}
	/*
	 * 得到规则集
	 */
	public Set getRuleSet() {
		return ruleSet;
	}
	/*
	 * 单独添加非中介符与非终结符
	 */
	public void addNonTerminal(String nonTer) {
		nonTerminalSet.add(nonTer);
	}
	public void addTerminal(String terminal) {
		terminalSet.add(terminal);
	}
		
	/*
	 * 根据规则左部得到所有对应规则
	 */
	public Set getRuleBylhs(String lhs){
		return ruleMapStartWithlhs.get(lhs);	
	}
	/*
	 * 根据规则右部得到所有对应规则
	 */
	public Set getRuleByrhs(String ...args){
		Vector<String> rhsVector=new Vector<String>();
		for(String string : args) {
			rhsVector.add(string);
		}
		return ruleMapStartWithrhs.get(rhsVector);
	}
	@Override
	public String toString()  {
		StringBuilder stb=new StringBuilder();
		Iterator itr1=nonTerminalSet.iterator();
		stb.append("非终结符集： "+'\n');
		int count=0;
		while(itr1.hasNext()) {
			stb.append(itr1.next()+" ");
			count++;
			if(count%25==0) {
				stb.append('\n');
			}
		}
		stb.append('\n');
		stb.append("-----------------------"+'\n');
		
		Iterator itr2=terminalSet.iterator();
		stb.append("终结符集： "+'\n');
		count=0;
		while(itr2.hasNext()) {
			stb.append(itr2.next()+" ");
			count++;
			if(count%25==0) {
				stb.append('\n');
			}
		}
		stb.append('\n');
		stb.append("-----------------------"+'\n');
		
		stb.append("规则集： "+'\n');
		Set<String> set=ruleMapStartWithlhs.keySet();
		for(String string : set) {
			HashSet ruleSet=ruleMapStartWithlhs.get(string);
			Iterator itr3=ruleSet.iterator();
			while(itr3.hasNext()) {
				stb.append(itr3.next()+" ");
			}
			stb.append('\n');
		}
		return stb.toString();
	}
}
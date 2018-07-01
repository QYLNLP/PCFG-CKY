package com.qyl.nlp.cky;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;


public class PCFG extends CFG{
   private Set<PRule> pRuleSet=new HashSet<PRule>();//规则集
   private HashMap<String,HashSet<PRule>> pRuleMapStartWithlhs=
		   new HashMap<String,HashSet<PRule>>();//以左部为key值的规则集map
   private HashMap<ArrayList<String>,HashSet<PRule>> pRuleMapStartWithrhs=
   		     new HashMap<ArrayList<String>,HashSet<PRule>>();//以规则右部为key值的规则集map
   public PCFG(){
	   super();
	   } 
	public void add(PRule prule) {
		
	}
	public HashMap<String, HashSet<PRule>> getPRuleMapStartWithlhs() {
		return pRuleMapStartWithlhs;
	}
	public void setPRuleMapStartWithlhs(HashMap<String, HashSet<PRule>> pRuleMapStartWithlhs) {
			this.pRuleMapStartWithlhs = pRuleMapStartWithlhs;
		}
	public HashMap<ArrayList<String>, HashSet<PRule>> getPRuleMapStartWithrhs() {
			return pRuleMapStartWithrhs;
		}
	public void setPRuleMapStartWithrhs(HashMap<ArrayList<String>, HashSet<PRule>> ruleMapStartWithrhs) {
			this.pRuleMapStartWithrhs = ruleMapStartWithrhs;
		}
	
	 /*
	  * 添加单个规则
	  */
	 
   public void addPRule(PRule rule) {
	   pRuleSet.add(rule);
	   HashSet<PRule> lRuleSet=pRuleMapStartWithlhs.get(rule.getLhs());
	   HashSet<PRule> RRuleSet=pRuleMapStartWithrhs.get(rule.getRhs());
	   if(lRuleSet!=null) {
		   if(!lRuleSet.contains(rule)) {
			   lRuleSet.add(rule);
		   }
	   }else {
		   HashSet<PRule> set=new HashSet<PRule>();
		   set.add(rule);
		   pRuleMapStartWithlhs.put(rule.getLhs(),set);
	   }
	   if(pRuleMapStartWithrhs.keySet().contains(rule.getRhs())) {
		   RRuleSet.add(rule);
	   }else {
		   HashSet<PRule> set=new  HashSet<PRule>();
		   set.add(rule);
		   pRuleMapStartWithrhs.put(rule.getRhs(), set);
	   } 
   }
	/*
	 *  得到规则集
	 */
	 
	public Set<PRule> getPRuleSet() {
		return pRuleSet;
	}
    /*
     *  根据规则左部得到所有对应规则
     */ 
	public Set<PRule> getPRuleBylhs(String lhs){
		return pRuleMapStartWithlhs.get(lhs);	
	}
	/*
	 * 根据规则右部得到所有对应规则
	 */

	 
	public Set<PRule> getPRuleByrhs(String ...args){
		Vector<String> rhsVector=new Vector<String>();
		for(String string : args) {
			rhsVector.add(string);
		}
		return pRuleMapStartWithrhs.get(rhsVector);
	}
	@Override
	public String toString()  {
		StringBuilder stb=new StringBuilder();
		Iterator itr1=super.getNonTerminalSet().iterator();
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
		
		Iterator itr2=super.getTerminalSet().iterator();
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
		Set<String> set=pRuleMapStartWithlhs.keySet();
		for(String string : set) {
			HashSet<PRule> ruleSet=pRuleMapStartWithlhs.get(string);
			Iterator itr3=ruleSet.iterator();
			while(itr3.hasNext()) {
				stb.append(itr3.next()+" ");
			}
			stb.append('\n');
		}
		return stb.toString();
	}
}

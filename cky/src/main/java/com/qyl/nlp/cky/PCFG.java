package com.qyl.nlp.cky;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class PCFG extends CFG {
   public boolean IsPCNF(){
	  return super.IsCNF();
   }
   public double getPro(String ...args) {
	   ArrayList<String> list=new ArrayList<String>();
	   for(String string:args) {
		   list.add(string);
	   }
	   Set<RewriteRule> set=this.getRuleBylhs(list.get(0));
	   if(set!=null) {
       Iterator itr=set.iterator();
       while(itr.hasNext()) {
    	   PRule prule=(PRule) itr.next();
    	   if(prule.getRhs().equals(list.subList(1, list.size()))) {
    		   return prule.getProOfRule();
    	   }
       }
	   }
       return -1.0;
   }
}

package com.qyl.nlp.cky;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;


public class CKY {
   private int back[][];//只需要记住k的位置
   private Extract ext;
   private CKYTreeNode[][] table;
   public void CreatParse(ArrayList<String> pos) throws UnsupportedOperationException, FileNotFoundException, IOException {
	   ext=new Extract("测试用例.txt","UTF-8");
	   PCFG pcnf=ext.getPCNF();
	   int n=pos.size();
	   table=new CKYTreeNode[n+1][n+1];
	   back=new int[n+1][n+1];
	   for(int j=1;j<n;j++) {
		   table[j-1][j]=new CKYTreeNode(pos.get(j-1),1);
		   for(int i=j-2;i>=0;i--) {
			   for(int k=i+1;k<=j-1;k++) {
				   Set<RewriteRule> ruleSet=pcnf.getRuleByrhs(table[i][k].getNodename(),table[k][j].getNodename());
				   double ik=table[i][k].getPro();
				   double kj=table[k][j].getPro();
				   PRule prule=null;
				   if(ik>0&&kj>0&&ruleSet!=null) {
					   prule=getHighestProRule(ruleSet);
				   }else {  
					   break; 
                   }
				   if(prule!=null&&table[i][j].getPro()<prule.getProOfRule()*ik*kj) {
						   table[i][j].setPro(prule.getProOfRule()*ik*kj);
						   table[i][j].setNodename(prule.getLhs());
						   back[i][j]=k;//只需要记录K的值就可回溯
				   }
			   }
		   }
	   }
	   CreateBracketList(n);
   }
   public void CreateBracketList(int n) {
	   
   }
   public PRule getHighestProRule(Set<RewriteRule> ruleSet) {
	   PRule bestPRule;
       Iterator itr=ruleSet.iterator();
       bestPRule=(PRule)itr.next();
       while(itr.hasNext()) {
    	   PRule prule=(PRule)itr.next();
    	   if(prule.getProOfRule()>bestPRule.getProOfRule()) {
    		   bestPRule=prule;
    	   }
       }
       return bestPRule;
   }
   /*
    * 内部类
    */
   class CKYTreeNode {
		// 节点名称
		private  String nodename;
		//概率
		private double pro;
	   public CKYTreeNode() {
			
		}
	   /*
	    * 构造方法，一次添加所有内容
	    */
	   public CKYTreeNode(String nodename, double pro) {
		this.nodename = nodename;
		this.pro = pro;
	   }
	   
		public String getNodename() {
			return nodename;
		}
		public void setNodename(String nodename) {
			this.nodename = nodename;
		}
		public double getPro() {
			return pro;
		}
		public void setPro(double pro) {
			this.pro = pro;
		}
	}
}

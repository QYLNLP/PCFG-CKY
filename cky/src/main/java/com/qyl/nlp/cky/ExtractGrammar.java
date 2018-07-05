package com.qyl.nlp.cky;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;



public class ExtractGrammar {
	  /*
	   * 定义文法的变量
	   */
	private CFG cfg;
	private PCFG pcfg;
	private Map<RewriteRule,Integer> ruleCounter;//规则计数器
	/*
	 * 得到文法集
	 */
	public PCFG getPGrammar() {
		
		return this.pcfg;
	}
	public CFG getGrammar() {
		
		return this.cfg;
	}
	/*
	 * 生成文法集
	 */
	public void CreateGrammar(String fileName,String enCoding,String type) throws UnsupportedOperationException, FileNotFoundException, IOException {
		  //括号表达式树拼接成括号表达式String数组
		  PlainTextByTreeStream ptbt=new PlainTextByTreeStream(new FileInputStreamFactory(new File(fileName)), enCoding);
	  	  String bracketStr=ptbt.read();
	      ArrayList<String> bracketStrList=new ArrayList<String>();
	  	  while(bracketStr.length()!=0) {
	  		bracketStrList.add(bracketStr);
	  		bracketStr=ptbt.read();
	  	  }
	  	  ptbt.close();
	  	  //括号表达式生成文法
	      bracketStrListConvertToGrammar(bracketStrList,type);
	}
	/*
     * 得到概率文法集,PCFG或者PCNF
     */
    public void CreatePGrammar() throws UnsupportedOperationException, FileNotFoundException, IOException {
    	pcfg=new PCFG();
    	Set<String> keySet=cfg.getRuleMapStartWithlhs().keySet();
    	for(String lhs:keySet) {
    		PRule pRule;
    		Set<RewriteRule> set=cfg.getRuleBylhs(lhs);
    		int sum=0;
    		double proOfRule=0;
    		//总Count
    		for(RewriteRule rule:set) {
    			sum+=ruleCounter.get(rule);
    		}
    		//概率
    		for(RewriteRule rule1:set) {
    			proOfRule=1.0*ruleCounter.get(rule1)/sum;
    			pRule=new PRule(rule1,proOfRule);
    			if(pRule!=null)
    			{
     			   pcfg.add(pRule);
    			}
    		}
    	}
    	pcfg.setStartSymbol(cfg.getStartSymbol());
    	pcfg.setNonTerminalSet(cfg.getNonTerminalSet());
    	pcfg.setTerminalSet(cfg.getTerminalSet());
    }
	//由括号表达式的list得到对应的文法集合CFG/或者CNF
	public void bracketStrListConvertToGrammar(ArrayList<String> bracketStrList,String type) throws IOException {
		  cfg=new CFG();  
		  ruleCounter=new HashMap<RewriteRule,Integer>();//重新定义规则计数器
		  for(String bracketStr:bracketStrList) {
	  		  TreeNode rootNode1=BracketExpUtil.generateTree(bracketStr);
	  		  traverseTree(rootNode1,type);
	  	  }
		  //如果需要转换为PCFG或者PCNF，则跟进一步执行CreateGrammar
		  if(type.contains("P")) {
			  CreatePGrammar();
		  }
	}
    /*
     * 遍历树得到CFG
     */
    public void traverseTree(TreeNode node,String type) {
    	  if(cfg.getStartSymbol()==null) {//起始符提取
    		  cfg.setStartSymbol(node.getNodeName()); 
    	  }
    	  if(node.getChildren().size()==0) {
    		  cfg.addTerminal(node.getNodeName());//终结符提取
    		  return;
    	  }
    	  cfg.addNonTerminal(node.getNodeName());//非终结符提取
    	   
    	  if(node.getChildren()!=null&&node.getChildren().size()>0) {
    	      if(type.contains("CFG")) {
    	    	  traverseTreeGetCFG(node);
    	      }else {
    	          traverseTreeGetCNF(node);
    	      }
    	  	  for(TreeNode node1:node.getChildren()) {//深度优先遍历
    	   			traverseTree(node1, type);
    	   		 } 
    	  }  
    } 
    /*
     * 遍历树得到CFG
     */
    public void traverseTreeGetCFG(TreeNode node) {
		  RewriteRule rule=new RewriteRule(node.getNodeName(),node.getChildren());
		  AddRuleAndCounter(rule);//添加规则
    }
    /*
     * 遍历树得到CNF
     */
    public void traverseTreeGetCNF(TreeNode node) {
			  RewriteRule rule=new RewriteRule(node.getNodeName(),node.getChildren());
			  //右侧的数目大于2
			  if(node.getChildren().size()>2) {
				  ArrayList<String> rhs=new ArrayList<String>();
				  //如果右侧中有终结符，则转换为伪非终结符
			    	for(int i=0;i<node.getChildrenNum();i++) {
			    		String str=node.getChildName(i);
			    		if(node.getChild(i).getChildrenNum()==0) {//若第i个节点为终结符
			    			
			    			terminalToDummyNonTerAndAdd(str);//将终结符转换为非终结符，并且新添加终结符，非终结符，新规则
			    			str="Du"+str;
			    		}
			    		rhs.add(str);
			    	};
			    	reduceNumOfrhs(new RewriteRule(node.getNodeName(),rhs));
			  }
			  //右侧长度为1
			  else if(node.getChildrenNum()==1) {
			      if(node.getFirstChild().getChildrenNum()!=0) {//如果是Unit Production
				      node.getChild(0).setNewName(node.getNodeName());//子节点名转换为父节点名,直接消除Unit Production
				  }else {
					  AddRuleAndCounter(rule);
				  }
				  
			  }//右侧长度为2
			  else{
			    	  if(node.getFirstChild().getChildrenNum()==0&&node.getChild(1).getChildrenNum()!=0) {
						  terminalToDummyNonTer(node,0,1);
					  }else if(node.getChild(1).getChildrenNum()==0&&node.getFirstChild().getChildrenNum()!=0) {
						  terminalToDummyNonTer(node,1,0);
					  }else {
						  //当规则本身就为CNF时
						  AddRuleAndCounter(rule);
					  }
			  }
    }
    /*
     * 若右侧的字符个数超过2个，则通过递归消减到2个
     */
    public void reduceNumOfrhs(RewriteRule rule) {
    	if(rule.getRhs().size()==2) {
    	   AddRuleAndCounter(rule);
		   return;
	    }  
    	List<String> list=rule.getRhs();
    	String str=list.get(0)+list.get(1);//新规则的左侧
  	
    	//最左侧的两个非终结符合成一个，并形成新的规则
    	RewriteRule rule1=new RewriteRule(str,list.get(0),list.get(1));
    	AddRuleAndCounter(rule1);
    	cfg.addNonTerminal(str);//添加新的非终结符
    	ArrayList<String> rhsList=new ArrayList<String>();
    	rhsList.add(str);
    	rhsList.addAll(rule.getRhs().subList(2,rule.getRhs().size()));
    	rule.setRhs(rhsList);
    	/*
    	 *递归，直到rhs的个数为2时
    	 */
    	reduceNumOfrhs(rule);
    }
    /*
     *规则右侧为一个终结符和一个非终结符时，将终结符转换为伪非终结符，并添加规则
     */
    public void terminalToDummyNonTer(TreeNode node,int numOfTerminal,int numOfNonTer) {
    	String terminal=node.getChildName(numOfTerminal);
    	terminalToDummyNonTerAndAdd(terminal);
    	RewriteRule rule1;
        //添加规则，并恢复顺序
    	if(numOfTerminal<numOfNonTer) {
    	rule1=new RewriteRule(node.getNodeName(),"Du"+terminal,node.getChildName(numOfNonTer));
        }else {
    	rule1=new RewriteRule(node.getNodeName(),node.getChildName(numOfNonTer),"Du"+terminal);
        }
    	AddRuleAndCounter(rule1);
     }
    /*
     * 添加规则并计数
     */
    public void AddRuleAndCounter(RewriteRule rule) {
    	  cfg.add(rule);
		  if(ruleCounter.containsKey(rule)) {
				  ruleCounter.put(rule,ruleCounter.get(rule)+1);
			  }else {
				ruleCounter.put(rule, 1);
			  } 
    }
    /*
     * 将终结符转换为伪终结符，并且添加新的终结符，伪终结符，规则
     */
    public void terminalToDummyNonTerAndAdd(String terminal) {
    	cfg.addTerminal(terminal);//添加新的终结符
        String st="Du"+terminal;
        RewriteRule rule=new RewriteRule(st,terminal);//添加新规则
        AddRuleAndCounter(rule);
        cfg.addNonTerminal(st);//添加新的非终结符 
    }
}

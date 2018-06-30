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
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.srl.PlainTextByTreeStream;

public class ExtractGrammar {
	  /*
	   * 定义文法的变量
	   */
	private CFG cfg;
	private PCFG pcfg;
	private Set<String> nonTerSet;//非终结符集
	private Set<String> terminalSet;//终结符集
	private Map<RewriteRule,Integer> ruleCounter;//规则计数器
	/*
	 * 得到文法集,i为零时得到CFG,i为1时得到CNF
	 */
	public CFG getGrammar(String fileName,String enCoding,int i) throws UnsupportedOperationException, FileNotFoundException, IOException {
		  cfg=new CFG();
		  ruleCounter=new HashMap<RewriteRule,Integer>();//重新定义规则计数器
		  nonTerSet=cfg.getNonTerminalSet();
	  	  terminalSet=cfg.getTerminalSet();
	  	  //括号表达式树拼接成括号表达式String数组
	  	  PlainTextByTreeStream ptbt=new PlainTextByTreeStream(new FileInputStreamFactory(new File(fileName)), enCoding);
	  	  String[] bracketStr=ptbt.read();
	  	  while(bracketStr[0]!=null) {
	  		  TreeNode rootNode1=BracketExpUtil.generateTree(bracketStr[0]);
	  		  traverseTree(rootNode1,i);//遍历树，并在遍历的过程中将得到的树添加至map中
	  		  bracketStr=ptbt.read();//读下一棵树
	  	  }
	  	  ptbt.close();
	  	  return cfg;
	}
    /*
     * 遍历树得到CFG
     */
    public void traverseTree(TreeNode node,int i) {
      if(i==0) {
    	  traverseTreeGetCFG(node);
      }else if(i==1) {
    	  traverseTreeGetCNF(node);
      }
  	  
    }
    public void traverseTreeGetCFG(TreeNode node) {
    	if(cfg.getStartSymbol()==null) {
    		  cfg.setStartSymbol(node.getNodeName()); 
    	  }
    	  if(node.getChildren().size()==0) {
    		  terminalSet.add(node.getNodeName());//终结符提取
    		  return;
    	  }
    	   nonTerSet.add(node.getNodeName());//非终结符提取
    	   
    	  if(node.getChildren()!=null&&node.getChildren().size()>0) {
  			  RewriteRule rule=new RewriteRule(node.getNodeName(),node.getChildren());
  			  if(ruleCounter.containsKey(rule)) {
  				  ruleCounter.put(rule,ruleCounter.get(rule)+1);
  			  }else {
  				ruleCounter.put(rule, 1);
  			  }  
  			  cfg.add(rule);//添加规则
    		 for(TreeNode node1:node.getChildren()) {//深度优先遍历
    			 traverseTreeGetCFG(node1);
    		 }
    	  }  
    } 
    /*
     * 遍历树得到CNF
     */
    public void traverseTreeGetCNF(TreeNode node) {
  	  if(node.getChildren().size()==0) {
  		  terminalSet.add(node.getNodeName());//终结符提取
  		  return;
  	  }
  	      nonTerSet.add(node.getNodeName());//非终结符提取
  	   
  	  if(node.getChildren()!=null&&node.getChildren().size()>0) {
			  RewriteRule rule=new RewriteRule(node.getNodeName(),node.getChildren());
			  //右侧的数目大于2
			  if(node.getChildren().size()>2) {
				  reduceNumOfrhs(rule);
			  }
			  //右侧只有一个非终结符
			  else if(node.getChildrenNum()==1&&node.getFirstChild().getChildrenNum()==1) {
					  eliminateUnitProduction(node);
					  return;//其子节点不用递归					  
			  }//右侧有一个终结符和一个非终结符
			  else if(node.getChildrenNum()==2) {
				  if(node.getFirstChild().getChildrenNum()==0&&node.getChild(1).getChildrenNum()!=0) {
					  terminalToDummyNonTer(node,0);
				  }else if(node.getChild(1).getChildrenNum()==0&&node.getFirstChild().getChildrenNum()!=0) {
					  terminalToDummyNonTer(node,1);
				  }
			  }
			  cfg.add(rule);//本身就为CNF时直接添加
			  cfgRuleCounterAdd(rule);
  		 for(TreeNode node1:node.getChildren()) {//深度优先遍历
  			traverseTreeGetCNF(node1);
  		 }
  	  }  
    }
    /*
     * 若右边的终结符大于2个，则将其最左侧的终结符
     * 一对非终结符转换为新的非终结符
     */
    public void reduceNumOfrhs(RewriteRule rule) {
    	if(rule.getRhs().size()==2) {
    		cfg.add(rule);
    		return;
    	}
    	List<String> list=rule.getRhs();
    	String str=list.get(0)+list.get(1);//新规则的左侧
    	
    	//最左侧的两个非终结符合成一个，并形成新的规则
    	RewriteRule rule1=new RewriteRule(str,list.get(0),list.get(1));
    	cfg.add(rule1);
    	cfgRuleCounterAdd(rule1);//计数
    	cfg.addNonTerminal(str);//添加新的非终结符
    	ArrayList<String> rhsList=new ArrayList<String>();
    	rhsList.add(str);
    	rhsList.addAll(rule.getRhs().subList(2,rule.getRhs().size()));
    	rule.setRhs(rhsList);
    	/*
    	 * 递归，直到rhs的个数为2时
    	 */
    	reduceNumOfrhs(rule);
    }
    /*
     * 消除Unit Production
     */
    public void eliminateUnitProduction(TreeNode node) {
    	RewriteRule rule=new RewriteRule(node.getNodeName(),
    			node.getFirstChild().getChildName(0));
    	cfg.add(rule);
    	cfgRuleCounterAdd(rule);
    }
    /*
     * 终结符转换为伪非终结符
     */
    public void terminalToDummyNonTer(TreeNode node,int num) {
        String st="Du"+node.getChildName(num).toUpperCase();
        RewriteRule rule=new RewriteRule(st,node.getChildName(num));
        cfg.add(rule);
        cfgRuleCounterAdd(rule);
        cfg.addNonTerminal(st);//添加新的非终结符 
    }
    public void cfgRuleCounterAdd(RewriteRule rule) {
		  if(ruleCounter.containsKey(rule)) {
				  ruleCounter.put(rule,ruleCounter.get(rule)+1);
			  }else {
				ruleCounter.put(rule, 1);
			  } 
    }
    /*
     * 得到概率上下文无关文法,i为0时为PCFG，i为1时为PCNF
     */
    public PCFG getPCFG(String fileName,String enCoding,int i) throws UnsupportedOperationException, FileNotFoundException, IOException {
    	pcfg=new PCFG();
    	CFG cfg=getGrammar(fileName,enCoding,i);//得到CFG
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
    			proOfRule=(double)ruleCounter.get(rule1)/sum;
    			pRule=new PRule(rule1,proOfRule);
/*    			System.out.println("概率规则  "+pRule);*/
    			if(pRule!=null)
    			{
     			   pcfg.addPRule(pRule);
    			}
    		}
    	}
    	pcfg.setStartSymbol(cfg.getStartSymbol());
    	pcfg.setNonTerminalSet(cfg.getNonTerminalSet());
    	pcfg.setTerminalSet(cfg.getTerminalSet());
    	return pcfg;
    }
}
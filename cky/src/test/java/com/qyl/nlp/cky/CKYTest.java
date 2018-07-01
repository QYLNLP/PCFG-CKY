package com.qyl.nlp.cky;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CKYTest {
    private ArrayList<String> sentences;
    private String fileName;
    private String enCoding;
    private Extract extract;
    private PrintWriter out;
    private Set<RewriteRule> ruleSet;
    private Set<PRule> pRuleSet;
    @Before
    public void BeforeTest() throws FileNotFoundException {
    	out=new PrintWriter("E:\\ExtractTest\\text.txt");
    	sentences=new ArrayList<String>();
    	sentences.add("(ROOT\r\n" + 
    			"  (IP\r\n" + 
    			"    (NP\r\n" + 
    			"      (NP (NR 中国))\r\n" + 
    			"      (NP (NN 篮球) (NN 协会)))\r\n" + 
    			"    (VP\r\n" + 
    			"      (PP (P 在)\r\n" + 
    			"        (NP\r\n" + 
    			"          (NP (NR 北京市) (NR 通州区) (NR 张家湾镇))\r\n" + 
    			"          (NP (NN 中心) (NN 小学))))\r\n" + 
    			"      (VP (VV 举行) (AS 了)\r\n" + 
    			"        (NP\r\n" + 
    			"          (NP (NN 小篮球) (NN 发展) (NN 计划))\r\n" + 
    			"          (CC 暨)\r\n" + 
    			"          (NP (NN 小篮球) (NN 联赛) (NN 启动) (NN 仪式)))))\r\n" + 
    			"    (PU 。)))  \r\n");
    	sentences.add("(ROOT\r\n" + 
    			"  (IP\r\n" + 
    			"    (NP (NN 小篮球) (NN 规则) (NN 适用) (NN 对象))\r\n" + 
    			"    (VP (VC 为))\r\n" + 
    			"    (NP\r\n" + 
    			"      (DNP\r\n" + 
    			"        (QP (CD 12)\r\n" + 
    			"            (CLP (M 岁))\r\n" + 
    			"            (CC 及)\r\n" + 
    			"            (NP (PN 以下)))\r\n" + 
    			"        (DEG 的))\r\n" + 
    			"      (NP (NN 少年) (NN 儿童)))\r\n" + 
    			"    (PU 。)))\r\n");
    	Iterator itr=sentences.iterator();
    	while(itr.hasNext()) {
        	out.print(itr.next());
    	}
    	out.close();
    	fileName="E:\\ExtractTest\\text.txt";
    	enCoding="UTF-8";
    	extract=new Extract(fileName,enCoding);
    }
    
    @Test
    public void getCFGTest() throws UnsupportedOperationException, FileNotFoundException, IOException {
    	Set<RewriteRule> rules=new HashSet<RewriteRule>();
    	//以由左侧得到所有规则右侧来进行测试
    	String startSymbol="IP";//起始符
    	//非终结符集
    	String[] list= {"IP","NP","NR","NN","VP","PP","P","VP","VV"
    			,"AS","CC","VC","DNP","QP","CD","CLP","M","PN","DEG","PU"};
    	Set<String> nonTerminal=new HashSet<String>();
    	for(String string:list) {
    		nonTerminal.add(string);
    	}
    	//终结符集
    	String[] list1= {"中国","篮球","协会","在","北京市","通州区","张家湾镇","中心","小学"
    			,"举行","了","小篮球","发展","计划","暨","联赛","启动","仪式","规则","适用"
    			,"对象","为","12","岁","及","以下","的","少年","儿童","。"};
    	Set<String> terminal=new HashSet<String>();
    	for(String string :list1) {
    		terminal.add(string);
    	}
    	rules.add(new RewriteRule("NP","NP","NP"));
    	rules.add(new RewriteRule("NP","NR"));
    	rules.add(new RewriteRule("NP","NN","NN"));
    	rules.add(new RewriteRule("NP","NR","NR","NR"));
    	rules.add(new RewriteRule("NP","NP","CC","NP"));
    	rules.add(new RewriteRule("NP","NN","NN","NN"));
    	rules.add(new RewriteRule("NP","NN","NN","NN","NN"));
    	rules.add(new RewriteRule("NP","DNP","NP"));
    	rules.add(new RewriteRule("NP","PN"));
    	CFG cfg=extract.getCFG();
    	ruleSet=cfg.getRuleBylhs("NP");//有规则左侧得到所有对应的规则
    	Assert.assertTrue(ruleSet.containsAll(rules)&&rules.containsAll(ruleSet));
    	Assert.assertEquals(startSymbol,cfg.getStartSymbol());
    	Assert.assertEquals(nonTerminal, cfg.getNonTerminalSet());
    	Assert.assertEquals(terminal, cfg.getTerminalSet());
    }
    @Test
    public void getCNFTest() throws UnsupportedOperationException, FileNotFoundException, IOException {
    	CFG cnf=extract.getCNF();
    	
    	Set<RewriteRule> rules=new HashSet<RewriteRule>();
    	//以由左侧得到所有规则右侧来进行测试
    	rules.add(new RewriteRule("NP","NP","NP"));
    	rules.add(new RewriteRule("NP","中国"));
    	rules.add(new RewriteRule("NP","NN","NN"));
    	rules.add(new RewriteRule("NP","NRNR","NR"));
    	rules.add(new RewriteRule("NP","NPCC","NP"));
    	rules.add(new RewriteRule("NP","NNNN","NN"));
    	rules.add(new RewriteRule("NP","NNNNNN","NN"));
    	rules.add(new RewriteRule("NP","DNP","NP"));
    	rules.add(new RewriteRule("NP","以下"));
    	ruleSet=cnf.getRuleBylhs("NP");
    	
    	Set<RewriteRule> rules1=new HashSet<RewriteRule>();
    	rules1.add(new RewriteRule("NRNR","NR","NR"));
    	rules1.add(new RewriteRule("NPCC","NP","CC"));
    	rules1.add(new RewriteRule("NNNNNN","NNNN","NN"));
    	rules1.add(new RewriteRule("NNNN","NN","NN"));
    	//转换为cnf后消除的规则
    	RewriteRule ruleReduce=new RewriteRule("NN","中国");
    	RewriteRule ruleReduce1=new RewriteRule("NP","NN","NN","NN");
    	
    	Assert.assertTrue(ruleSet.containsAll(rules)&&rules.containsAll(ruleSet));
    	Assert.assertTrue(cnf.getRuleSet().containsAll(rules1));
    	Assert.assertFalse(ruleSet.contains(ruleReduce1));
    	Assert.assertFalse(cnf.getRuleSet().contains(ruleReduce));
    }
    @Test
    public void getPCFGTest() throws UnsupportedOperationException, FileNotFoundException, IOException {
    	PCFG pcfg=extract.getPCFG();
    	Set<PRule> rules=new HashSet<PRule>();
    	rules.add(new PRule(0.25,"NR","中国"));
    	double pro=(double)3/17;
    	rules.add(new PRule(pro,"NN","小篮球"));
    	double pro1=(double)1/17;
    	rules.add(new PRule(pro,"NN","少年"));
    	double pro2=(double)1/3;
    	rules.add(new PRule(pro2,"VP","PP","VP"));
    	rules.add(new PRule(1,"DNP","QP","DEG"));
    	
    	Set<PRule> pRuleSet=pcfg.getPRuleSet();
    	Assert.assertTrue(pcfg.getPRuleBylhs("NR").contains(new PRule((double)1/(double)4,"NR","中国")));
    	Assert.assertTrue(pcfg.getPRuleSet().containsAll(rules));
    }
    @Test
    public void getPCNFTest() throws UnsupportedOperationException, FileNotFoundException, IOException {
    	PCFG pcnf=extract.getPCNF();
    	Set<PRule> rules=new HashSet<PRule>();
    	double pro0=(double)2/12;
    	rules.add(new PRule(pro0,"NP","NP","NP"));
    	double pro1=(double)3/12;
    	rules.add(new PRule(pro1,"NP","NN","NN"));
    	double pro2=(double)1/12;
    	rules.add(new PRule(pro2,"NP","以下"));
    	
    	Set<PRule> rules1=new HashSet<PRule>();
    	double pro3=(double)1/3;
   	    rules1.add(new PRule(pro3,"VP","PP","VP"));
     	rules1.add(new PRule(pro3,"VP","VVAS","NP"));
    	rules1.add(new PRule(1,"PP","P","NP"));
    	
    	Set<PRule> rulesLHSIsNP=pcnf.getPRuleBylhs("NP");
    	Assert.assertTrue(rulesLHSIsNP.containsAll(rules));
    	Assert.assertTrue(pcnf.getPRuleSet().containsAll(rules1));	
    }
    }

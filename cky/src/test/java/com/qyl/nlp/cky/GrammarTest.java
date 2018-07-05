package com.qyl.nlp.cky;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GrammarTest {
    private ArrayList<String> sentences;
    private ExtractGrammar extractGrammar;
    @Before
    public void BeforeTest() throws FileNotFoundException {
    	extractGrammar=new ExtractGrammar();
    	sentences=new ArrayList<String>();
    	sentences.add("(ROOT(IP(NP(Det (NN 市长)(的))(NP (NN 幕僚)))(VP(Aux-VP (会)(VP(VV 整理)(NP(Det (NN 产业整合)(的)(NN 详细报告))))))(PU 。)))");
    	sentences.add("(ROOT(IP(NP(NP (NR 中国))(NP (NN 篮球) (NN 协会)))(VP(PP (P 在)(NP(NP (NR 北京市) (NR 通州区) (NR 张家湾镇))"
    			+ "(NP (NN 中心) (NN 小学))))(VP (VV 举行) (AS 了)(NP(NN 篮球) (NN 联赛) (NN 启动) (NN 仪式))))(PU 。)))");
    }
    
    @Test
    public void getCFGTest() throws UnsupportedOperationException, FileNotFoundException, IOException {
    	extractGrammar.bracketStrListConvertToGrammar(sentences, "CFG");
    	CFG cfg=extractGrammar.getGrammar();
    	Set<RewriteRule> rules=new HashSet<RewriteRule>();//规则左侧为NP的集合
    	Set<RewriteRule> ruleMix=new HashSet<RewriteRule>();//测试一些特殊的规则
    	Set<RewriteRule> rules3=new HashSet<RewriteRule>();//测试规则右侧的集合
    	//以由左侧得到所有规则右侧来进行测试
    	String startSymbol="IP";//起始符
    	//非终结符集
    	String[] list= {"IP","NP","Det","NN","VP","PU","PP","P","NR","VV","AS","Aux-VP"};
    	Set<String> nonTerminal=new HashSet<String>();
    	for(String string:list) {
    		nonTerminal.add(string);
    	}
    	//终结符集
    	String[] list1= {"市长","的","幕僚","会","整理","产业整合","的","详细报告","。"
    			,"中国","篮球","协会","在","北京市","通州区","张家湾镇",
    			"中心","小学","举行","了","联赛","启动","仪式"};
    	Set<String> terminal=new HashSet<String>();
    	for(String string :list1) {
    		terminal.add(string);
    	}
    	ruleMix.add(new RewriteRule("Det","NN","的"));
    	ruleMix.add(new RewriteRule("Det","NN","的","NN"));
    	ruleMix.add(new RewriteRule("NR","北京市"));
    	ruleMix.add(new RewriteRule("NN","幕僚"));
    	ruleMix.add(new RewriteRule("VP","Aux-VP"));
    	ruleMix.add(new RewriteRule("Aux-VP","会","VP"));
    	ruleMix.add(new RewriteRule("NP","Det"));
    	
    	rules.add(new RewriteRule("NP","Det","NP"));
    	rules.add(new RewriteRule("NP","Det"));
    	rules.add(new RewriteRule("NP","NN"));
    	rules.add(new RewriteRule("NP","NP","NP"));
    	rules.add(new RewriteRule("NP","NR"));
    	rules.add(new RewriteRule("NP","NN","NN"));
    	rules.add(new RewriteRule("NP","NR","NR","NR"));
    	rules.add(new RewriteRule("NP","NN","NN","NN","NN"));
    	
    	rules3.add(new RewriteRule("Aux-VP","会","VP"));
    	
    	//判别是否为CNF的测试
    	Assert.assertFalse(cfg.IsCNF());
    	//测试由规则左侧得到的集合是否准确完整
    	Assert.assertTrue(cfg.getRuleBylhs("NP").containsAll(rules)&&rules.containsAll(cfg.getRuleBylhs("NP")));
    	//规则右侧集的测试
    	Assert.assertEquals(cfg.getRuleByrhs("会","VP"),rules3);
    	//起始符测试
    	Assert.assertEquals(startSymbol,cfg.getStartSymbol());
    	//非终结符集的测试
    	Assert.assertEquals(nonTerminal, cfg.getNonTerminalSet());
    	//终结符集的测试
    	Assert.assertEquals(terminal, cfg.getTerminalSet());
    	//整体规则集的测试
    	Assert.assertTrue(cfg.getRuleSet().containsAll(ruleMix));	
    }
    @Test
    public void getCNFTest() throws UnsupportedOperationException, FileNotFoundException, IOException {
    	extractGrammar.bracketStrListConvertToGrammar(sentences, "CNF");
    	CFG cnf=extractGrammar.getGrammar();
    	
    	Set<RewriteRule> rules=new HashSet<RewriteRule>();
    	//以由左侧得到所有规则右侧来进行测试
    	
    	//增加的
    	rules.add(new RewriteRule("NP","幕僚"));
    	rules.add(new RewriteRule("NP","中国"));
    	rules.add(new RewriteRule("NP","NRNR","NR"));
    	rules.add(new RewriteRule("NP","NNNNNN","NN"));
    	rules.add(new RewriteRule("NP","NNDu的","NN"));
        //原有的
    	rules.add(new RewriteRule("NP","Det","NP"));
    	rules.add(new RewriteRule("NP","NN","NN"));
    	rules.add(new RewriteRule("NP","NP","NP"));
    	
    	//增加的规则   	
    	Set<RewriteRule> rules1=new HashSet<RewriteRule>();
    	rules1.add(new RewriteRule("IP","NPVP","PU"));
    	rules1.add(new RewriteRule("NP","NNDu的","NN"));
    	rules1.add(new RewriteRule("NNDu的","NN","Du的"));
    	rules1.add(new RewriteRule("VP","Du会","VP"));
    	rules1.add(new RewriteRule("VP","VVAS","NP"));
    	rules1.add(new RewriteRule("Du的","的"));
    	rules1.add(new RewriteRule("Du会","会"));
    	rules1.add(new RewriteRule("NRNR","NR","NR"));
    	rules1.add(new RewriteRule("NNNNNN","NNNN","NN"));
    	rules1.add(new RewriteRule("NNNN","NN","NN"));
    	
    	//转换为cnf后消除的规则
    	Set<RewriteRule> rulesReduce=new HashSet<RewriteRule>();
    	rulesReduce.add(new RewriteRule("NR","中国"));
    	rulesReduce.add(new RewriteRule("NP","NN","NN","NN"));
    	rulesReduce.add(new RewriteRule("NP","NN"));
    	rulesReduce.add(new RewriteRule("NP","NR"));
    	rulesReduce.add(new RewriteRule("NP","NR","NR","NR"));
    	rulesReduce.add(new RewriteRule("NP","NN","NN","NN","NN"));
    	rulesReduce.add(new RewriteRule("Det","NN","的","NN"));
    	rulesReduce.add(new RewriteRule("VP","Aux-AP"));
    	
    	//终结符集
    	String[] list1= {"市长","的","幕僚","会","整理","产业整合","的","详细报告","。"
    			,"中国","篮球","协会","在","北京市","通州区","张家湾镇",
    			"中心","小学","举行","了","联赛","启动","仪式"};
    	Set<String> terminal=new HashSet<String>();
    	for(String string :list1) {
    		terminal.add(string);
    	}

    	//非终结符集
    	String[] list= {"IP","NP","Det","NN","VP","PU","PP","P","NR","VV","AS","NRNR","VVAS","Du的","Du会"
    			        ,"NPVP","NNNNNN","NNNN","NNDu的"};
    	Set<String> nonTerminal=new HashSet<String>();
    	for(String string:list) {
    		nonTerminal.add(string);
    	}

    	//判别是否为CNF的测试
    	Assert.assertTrue(cnf.IsCNF());
    	//测试由规则左侧得到的集合是否准确完整
    	Assert.assertTrue(cnf.getRuleBylhs("NP").equals(rules));
    	//规则右侧集的测试
    	Assert.assertTrue(cnf.getRuleByrhs("Du会","VP").contains(new RewriteRule("VP","Du会","VP")));
    	//非终结符集的测试
    	Assert.assertEquals(nonTerminal, cnf.getNonTerminalSet());
    	//终结符集的测试
    	Assert.assertEquals(terminal, cnf.getTerminalSet());
    	//整体规则集的测试: 本次测试增加的和减少的规则
    	Set<RewriteRule> rulesSet=cnf.getRuleSet();
    	Assert.assertTrue(rulesSet.containsAll(rules1));
    	for(RewriteRule rule:rulesReduce) {
    		Assert.assertFalse(rulesSet.contains(rule));
    	}
    }
    @Test
    public void getPCFGTest() throws UnsupportedOperationException, FileNotFoundException, IOException {
    	extractGrammar.bracketStrListConvertToGrammar(sentences, "PCFG");
    	PCFG pcfg=extractGrammar.getPGrammar();
    	Set<PRule> rules=new HashSet<PRule>();
    	double pro0=2.0/12;
    	rules.add(new PRule(pro0,"NN","篮球"));
    	double pro1=1.0/12;
    	rules.add(new PRule(pro1,"NN","幕僚"));
    	double pro2=1.0/2;
    	rules.add(new PRule(pro2,"VV","整理"));
    	rules.add(new PRule(pro2,"Det","NN","的","NN"));
    	double pro3=1.0/4;
    	rules.add(new PRule(pro3,"VP","Aux-VP"));
    	rules.add(new PRule(2.0/2,"IP","NP","VP","PU"));
    	double pro4=2.0/10;
    	rules.add(new PRule(pro4,"NP","NN","NN"));
    	
    	Assert.assertFalse(pcfg.IsPCNF());
    	Assert.assertTrue(pcfg instanceof PCFG);
    	Assert.assertTrue(pcfg.getRuleSet().containsAll(rules));	
    }
    @Test
    public void getPCNFTest() throws UnsupportedOperationException, FileNotFoundException, IOException {
    	extractGrammar.bracketStrListConvertToGrammar(sentences, "PCNF");
    	PCFG pcnf=extractGrammar.getPGrammar();
    	Set<PRule> rules=new HashSet<PRule>();
    	double pro0=1.0/10;
    	rules.add(new PRule(pro0,"NP","Det","NP"));
    	double pro1=1.0/10;
    	rules.add(new PRule(pro1,"NP","NNDu的","NN"));
    	double pro2=1.0;
    	rules.add(new PRule(pro2,"IP","NPVP","PU"));
    	double pro3=1.0/4;
    	rules.add(new PRule(pro3,"VP","VVAS","NP"));
    	double pro4=1.0/10;
    	rules.add(new PRule(pro4,"NP","中国"));
    	rules.add(new PRule(1.0,"Det","NN","Du的"));
    	rules.add(new PRule(1.0,"Du的","的"));
    	rules.add(new PRule(1.0,"Du的","的"));
    	rules.add(new PRule(1.0/10,"NP","NRNR","NR"));
    	rules.add(new PRule(1.0/4,"VP","Du会","VP"));
    	
    	Assert.assertTrue(pcnf instanceof PCFG);
    	Assert.assertTrue(pcnf.IsPCNF());
    	Assert.assertTrue(pcnf.getRuleSet().containsAll(rules));
    }
    }

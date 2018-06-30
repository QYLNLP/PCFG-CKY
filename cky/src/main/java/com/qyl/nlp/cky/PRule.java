package com.qyl.nlp.cky;

import java.util.ArrayList;


public class PRule extends RewriteRule {
    private double proOfRule;
    public PRule() {
    	super();
    }
    public PRule(double pro,String ...args) {
    	super(args);
    	this.proOfRule=pro;
    }
    public PRule(RewriteRule rule, double pro) {
    	super(rule.getLhs(),rule.getRhs());
    	this.proOfRule=pro;
    }
	public double getProOfRule() {
		return proOfRule;
	}
	
	public void setProOfRule(double proOfRule) {
		this.proOfRule = proOfRule;
	}
	@Override
	public String toString() {
		StringBuilder strb=new StringBuilder();
		strb.append(super.getLhs()+ "->");
		for(String st: super.getRhs()) {
			strb.append(st);
			strb.append(" ");
		}
		strb.append(" "+proOfRule);
		return strb.toString();
	}
}

package com.qyl.nlp.cky;

import java.io.FileNotFoundException;
import java.io.IOException;



public class Extract {
	 private String fileName;
	 private String enCoding;
	 private ExtractGrammar eg=new ExtractGrammar();
     public Extract(String fileName,String enCoding) {
    	 this.fileName=fileName;
    	 this.enCoding=enCoding;
     }
     public CFG getCFG() throws UnsupportedOperationException, FileNotFoundException, IOException {
         eg.CreateGrammar(fileName, enCoding,"CFG");
         return eg.getGrammar();
     }
     public PCFG getPCFG() throws UnsupportedOperationException, FileNotFoundException, IOException { 
    	 if(eg.getGrammar()!=null&&!eg.getGrammar().IsCNF()) {
    		 eg.CreatePGrammar();
    	 }else {
    	     eg.CreateGrammar(fileName,enCoding,"PCFG");
    	 }
		return eg.getPGrammar();
     }
     public CFG getCNF() throws UnsupportedOperationException, FileNotFoundException, IOException {
    	 eg.CreateGrammar(fileName, enCoding, "CNF");
    	 return eg.getGrammar();
     }
     public PCFG getPCNF() throws UnsupportedOperationException, FileNotFoundException, IOException {    	 
    	 if(eg.getGrammar()!=null&eg.getGrammar().IsCNF()) {
    		 eg.CreatePGrammar();
    	 }else {
    	     eg.CreateGrammar(fileName,enCoding,"PCNF");
    	 }
    	 return  eg.getPGrammar();
     }
}

package com.qyl.nlp.cky;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

public class Extract {
	 private String fileName;
	 private String enCoding;
	 private ExtractGrammar eg=new ExtractGrammar();
     public Extract(String fileName,String enCoding) {
    	 this.fileName=fileName;
    	 this.enCoding=enCoding;
     }
     public CFG getCFG() throws UnsupportedOperationException, FileNotFoundException, IOException {
        CFG cfg=eg.getGrammar(fileName, enCoding,0);
		return cfg;
     }
     public PCFG getPCFG() throws UnsupportedOperationException, FileNotFoundException, IOException { 
		return eg.getPCFG(fileName,enCoding,0); 
     }
     public CFG getCNF() throws UnsupportedOperationException, FileNotFoundException, IOException {
    	 CFG cnf=eg.getGrammar(fileName, enCoding, 1);
    	 return cnf; 
     }
     public PCFG getPCNF() throws UnsupportedOperationException, FileNotFoundException, IOException {    	 
    	 return eg.getPCFG(fileName, enCoding, 1);
     }
}

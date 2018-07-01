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
        return eg.getGrammar(fileName, enCoding,"CFG");
     }
     public CFG getPCFG() throws UnsupportedOperationException, FileNotFoundException, IOException { 
		return eg.getGrammar(fileName,enCoding,"PCFG"); 
     }
     public CFG getCNF() throws UnsupportedOperationException, FileNotFoundException, IOException {
    	 return eg.getGrammar(fileName, enCoding, "CNF");
     }
     public CFG getPCNF() throws UnsupportedOperationException, FileNotFoundException, IOException {    	 
    	 return eg.getGrammar(fileName, enCoding, "PCNF");
     }
}

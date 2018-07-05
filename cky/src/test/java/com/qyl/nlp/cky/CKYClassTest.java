package com.qyl.nlp.cky;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CKYClassTest {
	private CKY cky;
	ArrayList<String> posList;
   @Before
   public void BeforeCKY() {
	   //句子：徐国义已然记不清多少次目送弟子摘金。
	   String[] posOfWords= new String[]{"NR","AD","VV","AD","VA","CD","M","VV","NN","VV","PU"};
	   posList=new ArrayList<String>();
       for(String pos:posOfWords) {
    	   posList.add(pos);
       }
   }
   /*
    * 某节点的最佳扩展:没有则为空值，有输出最佳扩充
    * 其中内容包括孩子节点，以及本节点的非终结符符号
    */
/*   @Test
   public void GetBestPRuleTest() throws UnsupportedOperationException, FileNotFoundException, IOException {
	   cky=new CKY();
	   cky.CreatParse(posList);
   }*/
   //验证最佳分析结果的正确性，也就是剖析树的正确性
   @Test
   public void GetBestPath() throws UnsupportedOperationException, FileNotFoundException, IOException {
	   cky=new CKY();
	   cky.CreatParse(posList);
   }
}

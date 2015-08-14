package com.tyzhou.IKAnalyser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class IKTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        
        
        List<String> list = new ArrayList<String>();
        list.add("加湿");
        
        
        StringReader input = new StringReader("加湿器");
        IKSegmenter ikSeg = new IKSegmenter(input, false);
        //Dictionary.getSingleton().addWords(list);
        Lexeme lexeme;
        try {
            while ((lexeme = ikSeg.next()) != null) {
                System.out.println(lexeme.getLexemeText());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }

}

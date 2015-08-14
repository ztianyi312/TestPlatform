package com.tyzhou.analyzer.lucene;

import java.io.Reader;
import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;
import org.wltea.analyzer.lucene.IKTokenizer;

public class IKTokenizerFactory extends TokenizerFactory{
 private final boolean useSmart;
 public IKTokenizerFactory(Map<String, String> args) {
  super(args);
  this.useSmart = getBoolean(args, "useSmart", false);
 
 }

 @Override
 public Tokenizer create(AttributeFactory factory, Reader in) {
  return new IKTokenizer(in,this.useSmart);
 }

}
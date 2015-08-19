package com.tyzhou.protostuff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.tyzhou.pojo.Customer;
import com.tyzhou.pojo.Manager;

public class SerializeUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    //@Test
    public void testSer() {
        
        Manager manager = new Manager();
        manager.setId(1L);
        manager.setName("m");
        
        Customer customer = new Customer();
        customer.setId(2);
        customer.setName("c");
        customer.setManager(manager);
        customer.setBuff(new byte[1024]);
        
        SerializeUtil.ser(new Customer());
        byte[] buffer = SerializeUtil.ser(customer);
        
        
        Customer c = SerializeUtil.deser(Customer.class, buffer);
        
        assertTrue(customer != c);
        
        assertEquals(customer.getId(), c.getId());
        assertEquals(customer.getName(), c.getName());
        
        assertEquals(((Manager)customer.getManager()).getName(), ((Manager)c.getManager()).getName());
        assertEquals(((Manager)customer.getManager()).getId(), ((Manager)c.getManager()).getId());
    }

    //@Test
    public void testGC() {
        for(int i=0; i<100000000; i++) {
            testSer();
        }
    }
    
    @Test
    public void testSolution() {
        /*System.out.println(isMatch("aa","a"));
        System.out.println(isMatch("aa","aaa"));
        System.out.println(isMatch("aa", "a*"));
        System.out.println(isMatch("aaaaaaaaaaaaab", "a*a*a*a*a*a*a*a*a*a*c"));
        System.out.println(isMatch("aab", "c*a*b"));
        System.out.println(isMatch("aaa", "ab*a"));
        System.out.println(isMatch("ab", ".*c"));
        System.out.println(isMatch("aaa", "a*a"));
        System.out.println(isMatch("a", "ab*"));*/
        
        System.out.println(isMatch("aab", "b.*"));
    }
    
    public boolean isMatch(String s, String p) {
        Set<Integer> stateList = new HashSet<Integer>();
        stateList.add(-1);
        
        
        for(int i=0; i<s.length(); i++) {
            Set<Integer> nextList = new HashSet<Integer>();
            for(Integer state : stateList) {
                List<Integer> genrateList = findNextState(state, s.charAt(i), p);
                nextList.addAll(genrateList);
            
                //if(state==p.length() && i != s.length()-1){return false;}
            }
            stateList = nextList;
        }
        
        for(Integer state : stateList) {
            if(isEndState(state,p)) {
                return true;
            }
        }
        return false;
    }
    
    public List<Integer> findNextState(int current, char c, String p) {
        List<Integer> nextList = new ArrayList<Integer>();
        
        while(current<p.length()) {
            
            int index = current+1;
            
            if(index >= p.length()) {
                return nextList;
            }
            if(p.charAt(index) == '.' || p.charAt(index) == c) 
            {
                nextList.add(index);
                
                if(index+1 < p.length() && p.charAt(index+1) == '*') {
                    current++; 
                    continue;
                } else {
                    return nextList;
                }
            } else if(current == -1){
                if(index+1 < p.length() && p.charAt(index+1) == '*') {
                    current++; 
                    continue;
                } else {
                    return nextList;
                }
            }
            
            if((p.charAt(current) == '.' || p.charAt(current) == c) && p.charAt(index) == '*') {
                nextList.add(current);
                current += 1;
                continue;
            }
            
            if(p.charAt(current) != '*' && p.charAt(index) == '*') {
                current += 1;
            } else if(index+1 < p.length() && p.charAt(index+1) == '*'){
                current += 1;
            } else {
                return nextList;
            }
            
            
        }
        
        return nextList;
    }
    
    public boolean isEndState(int state, String p) {
        
        int i=state+1;
        for(; i<p.length() && (p.charAt(i) == '*' || (i+1 < p.length() && p.charAt(i+1) == '*')); i++); 
        
        if(i == p.length()) {
            return true;
        }
        
        
        return false;
    }
}

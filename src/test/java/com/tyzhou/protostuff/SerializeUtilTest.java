package com.tyzhou.protostuff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.tyzhou.pojo.Customer;
import com.tyzhou.pojo.Manager;

public class SerializeUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
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

    @Test
    public void testGC() {
        for(int i=0; i<100000000; i++) {
            testSer();
        }
    }
}

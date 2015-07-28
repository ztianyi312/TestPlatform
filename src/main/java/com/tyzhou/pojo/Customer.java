package com.tyzhou.pojo;

/**
 * 
 * @author zhoutianji
 *
 */
public class Customer {

    private long id;
    
    private String name;
    
    private Object manager;
    
    private byte[] buff;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getManager() {
        return manager;
    }

    public void setManager(Object manager) {
        this.manager = manager;
    }

    public byte[] getBuff() {
        return buff;
    }

    public void setBuff(byte[] buff) {
        this.buff = buff;
    }
    
    
}

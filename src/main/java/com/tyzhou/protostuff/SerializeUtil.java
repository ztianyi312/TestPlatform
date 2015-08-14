package com.tyzhou.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 
 * @author zhoutianji
 *
 * 不支持内部类序列化 使用的话会出现StackOverflow
 * pojo是按字段的顺序进行序列化或反序列化的 增加或删除字段的时要注意 尽量不删除字段 在最后加字段 或者annotation @Tag 固定顺序
 * @Tag annotations can be used on fields to have explicit control of the field numbers
 * 
 */
public class SerializeUtil {

    private static final int BUF_SIZE = 512;
    
    public static <T> byte[] ser(T o) {
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(o.getClass());
        LinkedBuffer buffer = getApplicationBuffer();
        try
        {
            return ProtostuffIOUtil.toByteArray(o, schema, buffer);
        }
        finally
        {
            buffer.clear();
        }
    }
    
    private static LinkedBuffer getApplicationBuffer() {
        return LinkedBuffer.allocate(BUF_SIZE);
    }

    public static <T> T deser(Class<T> clazz, byte[] protostuff) {
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(clazz);
        T t = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(protostuff, t, schema);
        
        return t;
    }
    
    
    public static <T> byte[] serList(List<T> c, Class<T> clazz) throws IOException {
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(clazz);
        LinkedBuffer buffer = getApplicationBuffer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            ProtostuffIOUtil.writeListTo(out, c, schema, buffer);
            return out.toByteArray();
        }
        finally
        {
            buffer.clear();
        }
    }
    
    public static <T> List<T> deserList(Class<T> clazz, byte[] protostuff) throws IOException {
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(clazz);
        ByteArrayInputStream in = new ByteArrayInputStream(protostuff);
        return ProtostuffIOUtil.parseListFrom(in, schema);

    }
}

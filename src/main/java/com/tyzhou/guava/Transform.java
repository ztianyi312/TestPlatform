package com.tyzhou.guava;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;

/**
 * 
 * @author zhoutianji
 *
 */
public class Transform {
	
	public static void main(String[] args) {
		
		List<Long> list = Longs.asList(1L, 2L, 2L, 3L);
		List<Object> result = Lists.transform(list, new Function<Long, Object>(){

			@Override
			public Object apply(Long input) {
				return new Object();
			}
			
		});

		for(Object o : result) {
			System.out.println(o.getClass());
		}
	}
}

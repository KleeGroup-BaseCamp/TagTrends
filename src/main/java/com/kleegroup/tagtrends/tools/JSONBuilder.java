package com.kleegroup.tagtrends.tools;

import java.util.Iterator;

public class JSONBuilder {
	private Iterator it;
	
	public JSONBuilder(Iterator iterator){
		this.it = iterator;
	}
	
	public String JSONArrayFromIterator(){
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		while(it.hasNext()){
			sb.append(it.next()+",");
		}
		// on enlève la virgule
		sb.deleteCharAt(sb.length()-1);
		sb.append(']');
		return sb.toString();
	}
	
	
}

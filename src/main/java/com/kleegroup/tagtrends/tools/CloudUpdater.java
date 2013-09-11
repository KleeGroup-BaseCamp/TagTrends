package com.kleegroup.tagtrends.tools;
import java.util.HashMap;
import java.util.Map.Entry;
import com.mongodb.BasicDBObject;


public class CloudUpdater {
	private HashMap<String, Integer> stock;
	
	public CloudUpdater() {
		this.stock = new HashMap<String, Integer>();
	}
	
	public void add(String word){
		if (stock.containsKey(word)){
			stock.put(word, stock.get(word)+1);
		} else {
			stock.put(word, 1);
		}
	}
		
	public BasicDBObject mongoQuery(){
		BasicDBObject[] tabTags = new BasicDBObject[stock.size()];
		int i = 0;
		for (Entry<String, Integer> b : stock.entrySet()){
			tabTags[i] = new BasicDBObject("tag", b.getKey());
			tabTags[i].append("size", b.getValue());
			i++;
		}
		BasicDBObject bdo = new BasicDBObject("cloud", tabTags);
		return bdo;
	}
	
	public String toString(){
		return stock.entrySet().toString();
	}

	public static void main(String[] args) {
		CloudUpdater cu = new CloudUpdater();
		cu.add("sapin");
		cu.add("rapt");
		cu.add("sapin");
		System.out.println(cu);
		System.out.println(cu.mongoQuery());
	}

}

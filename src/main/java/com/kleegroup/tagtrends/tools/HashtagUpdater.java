package com.kleegroup.tagtrends.tools;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import com.mongodb.BasicDBObject;


public class HashtagUpdater {
	private HashMap<Long, int[]> stock;
	private String hashtag;
	
	public HashtagUpdater(String hashtag) {
		this.stock = new HashMap<Long, int[]>();
		this.hashtag = hashtag;
	}
	
	public void add(long time, int[] semanticsAndNegation){
		if (stock.containsKey(time)){
			int[] data = new int[] {stock.get(time)[0]+1, stock.get(time)[1]+semanticsAndNegation[0], stock.get(time)[2]+semanticsAndNegation[1]};
			stock.put(time, data);
		} else {
			stock.put(time, new int[] {1,semanticsAndNegation[0], semanticsAndNegation[1]});
		}
	}
		
	public BasicDBObject mongoInsertion(){
		BasicDBObject[] tabTags = new BasicDBObject[stock.size()];
		long[] timesList = new long[stock.size()];
		int i = 0;
		int total = 0;
		for (Entry<Long, int[]> b : stock.entrySet()){
			tabTags[i] = new BasicDBObject("time", b.getKey());
			tabTags[i].append("frequency", b.getValue()[0]);
			tabTags[i].append("semantics", b.getValue()[1]);
			tabTags[i].append("negation", b.getValue()[2]);
			total += b.getValue()[0];
			timesList[i] = b.getKey();
			i++;
			
		}
		BasicDBObject bdo = new BasicDBObject("info", tabTags);
		bdo.append("total", total);
		bdo.append("timesList", timesList);
		bdo.append("hashtag", hashtag);
		bdo.append("created_at", new Date());
		/*
		 * this fields allows deletion of document after the time chosen when
		 * the collection was created (see FinalStocker parameters)
		 */
		return bdo;
	}
	
	public void print(){
		for (Entry<Long, int[]> b : stock.entrySet()){
			System.out.print(b.getKey()+" : ");
			for (int i = 0; i < b.getValue().length; i++){
				System.out.print(b.getValue()[i]+" ");
			}
			System.out.println("");
		}
	}

	public static void main(String[] args) {
		HashtagUpdater hu = new HashtagUpdater("blabla");
		hu.add(1256l, new int[] {4,-1});
		hu.add(4458654l, new int[] {2,-1});
		hu.add(1256l, new int[] {-1,0});
		hu.print();
		System.out.println(hu.mongoInsertion());
	}
}

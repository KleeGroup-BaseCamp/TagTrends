package com.kleegroup.tagtrends.tools;

import com.mongodb.BasicDBObject;


public class DebateUpdater {
	private int forRate;
	private int againstRate;
	private String topic;
	
	public DebateUpdater(String topic){
		this.topic = topic;
		this.forRate = 0;
		this.againstRate = 0;
	}
	
	
	public static void main(String[] args) {
		DebateUpdater du = new DebateUpdater("interventionSyrie");
		du.add("for");
		du.add("against");
		du.add("for");
		System.out.println(du.mongoQuery());

	}

	public Object mongoQuery() {
		BasicDBObject forObject = new BasicDBObject("opinion","for");
		forObject.append("rate",forRate);
		BasicDBObject againstObject = new BasicDBObject("opinion","against");
		againstObject.append("rate",againstRate);
		BasicDBObject debateObject = new BasicDBObject("topic", topic);
		debateObject.append("debate",new BasicDBObject[] {forObject, againstObject});
		return debateObject;
	}

	public void add(String category) {
				if (category.equals("for")){
					forRate++;
				}
				else if (category.equals("against")){
					againstRate++;
				}
				else {
					System.out.println("Unknown category...");
				}
	}

}

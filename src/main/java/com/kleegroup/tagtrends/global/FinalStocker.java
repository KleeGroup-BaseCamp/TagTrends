package com.kleegroup.tagtrends.global;
import java.net.UnknownHostException;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


public class FinalStocker {
	private final DB db;
	private final DBCollection coll;
	private final int expireAfterSeconds = 3600*24;
	/*
	 * documents will live for *expireAfterSeconds* seconds
	 * after the time registered in the "created_at" field
	 */
	
	public FinalStocker(DB db, String collectionName) throws UnknownHostException{
		this.db = db;
		coll = db.getCollection(collectionName);
		coll.ensureIndex(new BasicDBObject("created_at",1) , new BasicDBObject("expireAfterSeconds", expireAfterSeconds) );
	}
	
	public void startDB() throws UnknownHostException{
		Set<String> colls = db.getCollectionNames();
		System.out.println("----------------------------------------------------------");
		System.out.println("list of all collections:");
		for (String s : colls) {
		    System.out.println(s+" : "+db.getCollection(s).getCount()+" elements");
		}
		System.out.println(colls.size()+" collections in this database");
		System.out.println("----------------------------------------------------------");
	}
	
	public void printCollection(){
		System.out.println("----------------------------------------------------------");
		System.out.println("Collection "+coll.getName());
		DBCursor cursor = coll.find().limit(10);
		for (DBObject o : cursor){
			System.out.println(o);
		}
		System.out.println(coll.count()+" elements in this collection");
		System.out.println("hasData : "+hasData()+", hasResults : "+hasResults()+", hasCloud : "+hasCloud());
		System.out.println("----------------------------------------------------------");
	}
	
	public void clearCollection(){
		coll.remove(new BasicDBObject());
	}
	
	public void deleteCollection(){
		coll.drop();
	}
	
	public void remove(String field, String value){
		coll.update(new BasicDBObject(field, value),new BasicDBObject("$unset" , new BasicDBObject(field,"")),false, true);
	}
	// SUPPRIMER UNE ETIQUETTE
	// coll.update(new BasicDBObject("filter_level", "medium"), new BasicDBObject("$unset" , new BasicDBObject("filter_level","")),false, true );

	public void insert(DBObject dbObject) {
		coll.insert(dbObject);
	}

	public long count() { 
		return coll.count();
	}
	
	public boolean hasData(){
		return coll.find(new BasicDBObject("text", new BasicDBObject("$exists",true))).limit(1).size()>0;
	}
	
	public boolean hasResults(){
		return coll.find(new BasicDBObject("info", new BasicDBObject("$exists",true))).limit(1).size()>0;
	}
	
	public boolean hasCloud(){
		return coll.find(new BasicDBObject("cloud", new BasicDBObject("$exists",true))).limit(1).size()>0;
	}
	
	public void insertOne(DBObject dbObject) {
		coll.update(new BasicDBObject("_id", dbObject.get("_id")), dbObject,true,false);
	}
	
	public DBObject findOne() {
		return coll.findOne();
	}
	
}

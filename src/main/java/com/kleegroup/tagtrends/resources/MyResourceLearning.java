package com.kleegroup.tagtrends.resources;

import java.util.ArrayList;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.kleegroup.tagtrends.global.Database;
import com.kleegroup.tagtrends.tools.Hacker;
import com.kleegroup.tagtrends.tools.JSONBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Path("/myResourceLearning") // The Java class will be hosted at this URI path
public class MyResourceLearning {
	
	
	@POST // The Java method will process HTTP POST requests 
    @Produces("text/plain") // produce content identified by the MIME Media type "text/plain"
    public String getTweetTextsToLearn(@FormParam("length") int learningLength) throws Exception {
    	return tweetTextsToLearnReader(learningLength);
    }
    
	public String tweetTextsToLearnReader(int learningLength) throws Exception {
		Hacker hacker = new Hacker();
		DB twitterDb = Database.getDB();
		DBCollection dbCollection = twitterDb.getCollection("oneNightNoFilterData");
		BasicDBObject fieldsToTake = new BasicDBObject("text", 1);
		fieldsToTake.append("_id", 0); // field to leave ( otherwise it will come ... )
		DBCursor tweetTexts = dbCollection.find(
				new BasicDBObject("text", new BasicDBObject("$exists", true)),
				fieldsToTake).limit(learningLength);
		ArrayList<BasicDBObject> toLearn = new ArrayList<BasicDBObject>(
				learningLength);
		for (DBObject o : tweetTexts) {
			System.out.println("o : " + o);
			toLearn.add(new BasicDBObject("text", hacker.clearPonctuation(((String) o.get("text")))));
		}
		JSONBuilder jsonBuilder = new JSONBuilder(toLearn.iterator());
		return jsonBuilder.JSONArrayFromIterator();
	}
	
	 public static void main(String[] args) throws Exception {
		 MyResourceLearning mrl = new MyResourceLearning();
		System.out.println(mrl.tweetTextsToLearnReader(15));
				
	 }
}
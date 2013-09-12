package com.kleegroup.tagtrends.resources;


import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.kleegroup.tagtrends.global.Analyzer;
import com.kleegroup.tagtrends.global.AnalyzerMode;
import com.kleegroup.tagtrends.global.Database;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

@Path("/myResourceCloud") // The Java class will be hosted at this URI path
public class MyResourceCloud {
	
	
	@POST // The Java method will process HTTP POST requests 
    @Produces("text/plain") // produce content identified by the MIME Media type "text/plain"
    public String getCloud(@FormParam("hashtag") String hashtag, @FormParam("collection") String collectionName) throws Exception {
    	return cloudReader(hashtag, collectionName);
    }
    
	public String cloudReader(String hashtag, String collectionName) throws Exception {
		DB twitterDb = Database.getDB();
		DBCollection dbCollection = twitterDb.getCollection(collectionName);
		Analyzer analyzer = new Analyzer(AnalyzerMode.countHashtags,
				dbCollection);
		analyzer.analyzeCloud(hashtag);
		BasicDBObject fieldsToTake = new BasicDBObject("cloud", 1);
		BasicDBObject cloud = (BasicDBObject) dbCollection.findOne(
				new BasicDBObject("hashtag", hashtag), fieldsToTake);
		System.out.println("hashtag : "+hashtag);
		System.out.println("cloud : "+cloud);
		return cloud.get("cloud").toString();
	}
}

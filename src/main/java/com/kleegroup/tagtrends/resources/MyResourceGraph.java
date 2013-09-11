package com.kleegroup.tagtrends.resources;
import java.net.UnknownHostException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.kleegroup.tagtrends.global.Database;
import com.kleegroup.tagtrends.tools.JSONBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

// The Java class will be hosted at the URI path "/data"
@Path("/myResourceGraph")
public class MyResourceGraph {
	
    // The Java method will process HTTP GET requests
    @GET 
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
     @Produces("text/plain")
    public String getData() throws UnknownHostException {
    	return tweetReader();
    }
    
     public String tweetReader() throws UnknownHostException{
			DB twitterDb = Database.getDB();
			DBCollection collectionToRead = twitterDb.getCollection("oneNightNoFilterData");
			BasicDBObject fieldsToTake = new BasicDBObject("hashtag", 1);
			fieldsToTake.put("total", 1);
			fieldsToTake.put("info", 1);
			DBCursor cursor = collectionToRead.find(new BasicDBObject("hashtag", new BasicDBObject("$exists",true)), fieldsToTake).sort(new BasicDBObject("total", -1)).limit(50);
			for(Object o : cursor){
				System.out.println(o);
			}
			JSONBuilder jsonBuilder = new JSONBuilder(cursor.iterator());
			return jsonBuilder.JSONArrayFromIterator();
			
     }
    }
    
    
 



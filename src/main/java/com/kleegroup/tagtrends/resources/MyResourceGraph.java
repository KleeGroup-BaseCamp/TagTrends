package com.kleegroup.tagtrends.resources;

import java.net.UnknownHostException;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
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
	@POST
	// The Java method will produce content identified by the MIME Media
	// type "text/plain"
	@Produces("text/plain")
	public String getData(@FormParam("collection") final String collectionName) throws UnknownHostException {
		return tweetReader(collectionName);
	}

	public String tweetReader(final String collectionName) throws UnknownHostException {
		final DB twitterDb = Database.getDB();
		final DBCollection collectionToRead = twitterDb.getCollection(collectionName);
		final BasicDBObject fieldsToTake = new BasicDBObject("hashtag", 1);
		fieldsToTake.put("total", 1);
		fieldsToTake.put("info", 1);
		final DBCursor cursor = collectionToRead.find(new BasicDBObject("hashtag", new BasicDBObject("$exists", true)), fieldsToTake).sort(new BasicDBObject("total", -1)).limit(50);
		for (final Object o : cursor) {
			System.out.println(o);
		}
		final JSONBuilder jsonBuilder = new JSONBuilder(cursor.iterator());
		return jsonBuilder.JSONArrayFromIterator();

	}

	//     public static void main(String[] args) throws Exception {
	// 		MyResourceGraph mrg = new MyResourceGraph();
	// 		System.out.println(mrg.getData("exampleData"));
	// 	}

}

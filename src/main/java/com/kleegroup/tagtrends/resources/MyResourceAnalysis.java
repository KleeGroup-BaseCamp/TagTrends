package com.kleegroup.tagtrends.resources;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.kleegroup.tagtrends.global.Analyzer;
import com.kleegroup.tagtrends.global.AnalyzerMode;
import com.kleegroup.tagtrends.global.Database;
import com.kleegroup.tagtrends.global.ServerRunner;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

@Path("/myResourceAnalysis")
// The Java class will be hosted at this URI path
public class MyResourceAnalysis {

	@POST
	// The Java method will process HTTP POST requests 
	@Produces("text/plain")
	// produce content identified by the MIME Media type "text/plain"
	public String getAnalysis(@FormParam("collection") final String collectionName) throws Exception {
		return "" + runAnalysis(collectionName);
	}

	public int runAnalysis(final String collectionName) throws Exception {
		final DB twitterDb = Database.getDB();
		final DBCollection dbCollection = twitterDb.getCollection(collectionName);
		if (!collectionName.equals(ServerRunner.PROTECTED_COLLECTION)) {
			final Analyzer analyzer = new Analyzer(AnalyzerMode.countHashtags, dbCollection);
			analyzer.start();
			analyzer.join();
		}
		return dbCollection.find(new BasicDBObject("hashtag", new BasicDBObject("$exists", true))).count();
	}
}

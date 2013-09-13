package com.kleegroup.tagtrends.resources;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.kleegroup.tagtrends.global.CollectAndTreatment;
import com.kleegroup.tagtrends.global.Database;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

@Path("/myResourceCollect")
// The Java class will be hosted at this URI path
public class MyResourceCollect {

	@POST
	// The Java method will process HTTP POST requests 
	@Produces("text/plain")
	// produce content identified by the MIME Media type "text/plain"
	public String getCollectionStock(@FormParam("length") final int collectLength, @FormParam("collection") final String collectionName) throws Exception {
		return "" + limitedCollect(collectLength, collectionName);
	}

	public int limitedCollect(final int durationOfCollect, final String collectionName) throws Exception {
		removeEmptyCollections();
		//if (!collectionName.equals("exampleData")){
		removePreviousAnalysis(collectionName);
		CollectAndTreatment.launchCollectAndTreatment(collectionName, durationOfCollect);
		//}
		return Database.getDB().getCollection(collectionName).find(new BasicDBObject("text", new BasicDBObject("$exists", true))).count();
	}

	/* 
	 * remove trash in database (routine action)
	 */
	public void removeEmptyCollections() {
		for (final String collectionName : Database.getDB().getCollectionNames()) {
			if (!collectionName.contains("system")) {
				final DBCollection dbc = Database.getDB().getCollection(collectionName);
				if (dbc.count() == 0) {
					dbc.drop();
				}
			}
		}
	}

	/* 
	 * remove eventual previous analysisResults in case (analysis has to be redone from scratch) 
	 */
	public void removePreviousAnalysis(final String collectionName) {
		Database.getDB().getCollection(collectionName).remove(new BasicDBObject("hashtag", new BasicDBObject("$exists", true)));
	}

	public static void main(final String[] args) throws Exception {
		final MyResourceCollect mrc = new MyResourceCollect();
		System.out.println(mrc.getCollectionStock(5, "essai"));
	}
}

package com.kleegroup.tagtrends.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import twitter4j.internal.org.json.JSONArray;

import com.kleegroup.tagtrends.bayesclassifier.DebateClassifier;
import com.kleegroup.tagtrends.global.Analyzer;
import com.kleegroup.tagtrends.global.AnalyzerMode;
import com.kleegroup.tagtrends.global.Database;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

@Path("/myResourceStats")
// The Java class will be hosted at this URI path
public class MyResourceStats {

	@POST
	// The Java method will process HTTP POST requests 
	@Produces("text/plain")
	// produce content identified by the MIME Media type "text/plain"
	public String getStats(@FormParam("topic") final String topic, @FormParam("hashtag") final String hashtag, @DefaultValue("null") @FormParam("learntData") final String learntData, @FormParam("collection") final String collectionName) throws Exception {
		return statsReader(topic, hashtag, learntData, collectionName);
	}

	public String statsReader(final String topic, final String hashtag, final String learntData, final String collectionName) throws Exception {
		final DB twitterDb = Database.getDB();
		final DBCollection dbCollection = twitterDb.getCollection(collectionName);
		final Analyzer analyzer = new Analyzer(AnalyzerMode.countHashtags, dbCollection);
		if (learntData != null && !topic.equals("No topic yet")) {
			final JSONArray jsonArrayData = new JSONArray(learntData);
			final DebateClassifier debateClassifier = new DebateClassifier(topic, jsonArrayData);
			analyzer.analyzeDebate(hashtag, debateClassifier, topic);
			final BasicDBObject fieldsToTake = new BasicDBObject("debate", 1);
			fieldsToTake.append("topic", 1);
			fieldsToTake.append("_id", 0); // field to leave ( otherwise it will
											// come ... )
			final BasicDBObject debate = (BasicDBObject) dbCollection.findOne(new BasicDBObject("hashtag", hashtag), fieldsToTake);
			System.out.println("hashtag : " + hashtag);
			System.out.println("debate : " + debate);
			return debate.toString();
		} else {
			final BasicDBObject nothingToDraw = new BasicDBObject("topic", "No trend topic yet. Choose 'New learning' to review a trend.");
			return nothingToDraw.append("debate", null).toString();
		}
	}

	//	public static void main(String[] args) throws Exception {
	//		JSONArray data = new JSONArray();
	//		String[] texts = new String[] {" RT il y aura une grève", "les gens sont contents !!", "gros succès pour cet événement :)", "Des protestations vigoureuses ont suivi cette annonce"};
	//		String[] opinions = new String[] {"against", "for", "for", "against"};
	//		for (int i=0; i<4; i++){
	//			JSONObject o = new JSONObject();
	//			o.put("text", texts[i]);
	//			o.put("opinion", opinions[i]);
	//			data.put(o);
	//		}
	//		System.out.println((new MyResourceStats()).getStats("interventionSyrie", "Syrie", data.toString(), "exampleData"));
	//
	//	}

}

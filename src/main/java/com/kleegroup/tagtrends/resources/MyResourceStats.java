package com.kleegroup.tagtrends.resources;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONObject;

import com.kleegroup.tagtrends.bayesclassifier.DebateClassifier;
import com.kleegroup.tagtrends.global.Analyzer;
import com.kleegroup.tagtrends.global.AnalyzerMode;
import com.kleegroup.tagtrends.global.Database;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;


@Path("/myResourceStats") // The Java class will be hosted at this URI path
public class MyResourceStats {
	
	
	@POST // The Java method will process HTTP POST requests 
    @Produces("text/plain") // produce content identified by the MIME Media type "text/plain"
    public String getStats(@FormParam("topic") String topic, @FormParam("hashtag") String hashtag,@DefaultValue("null") @FormParam("learntData") String learntData) throws Exception {
    	return statsReader(topic, hashtag, learntData);
    }
    
	
	public String statsReader(String topic, String hashtag, String learntData) throws Exception {
		DB twitterDb = Database.getDB();
		DBCollection dbCollection = twitterDb.getCollection("oneNightNoFilterData");
		Analyzer analyzer = new Analyzer(AnalyzerMode.countHashtags,
				dbCollection);
		if (learntData != null && !topic.equals("No topic yet")){
			JSONArray jsonArrayData = new JSONArray(learntData);
			DebateClassifier debateClassifier = new DebateClassifier(topic,
					jsonArrayData);
			analyzer.analyzeDebate(hashtag, debateClassifier, topic);
			BasicDBObject fieldsToTake = new BasicDBObject("debate", 1);
			fieldsToTake.append("topic", 1);
			fieldsToTake.append("_id", 0); // field to leave ( otherwise it will
											// come ... )
			BasicDBObject debate = (BasicDBObject) dbCollection.findOne(
					new BasicDBObject("hashtag", hashtag), fieldsToTake);
			System.out.println("hashtag : " + hashtag);
			System.out.println("debate : " + debate);
			return debate.toString();
		} else {
			BasicDBObject nothingToDraw = new BasicDBObject("topic", "No topic yet");
			return nothingToDraw.append("debate", null).toString();
		}
	}

	public static void main(String[] args) throws Exception {
		JSONArray data = new JSONArray();
		String[] texts = new String[] {" RT il y aura une grève", "les gens sont contents !!", "gros succès pour cet événement :)", "Des protestations vigoureuses ont suivi cette annonce"};
		String[] opinions = new String[] {"against", "for", "for", "against"};
		for (int i=0; i<4; i++){
			JSONObject o = new JSONObject();
			o.put("text", texts[i]);
			o.put("opinion", opinions[i]);
			data.put(o);
		}
		System.out.println((new MyResourceStats()).getStats("interventionSyrie", "Syrie", data.toString()));

	}

}

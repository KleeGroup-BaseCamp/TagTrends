package com.kleegroup.tagtrends.global;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

import org.bson.BasicBSONObject;

import com.kleegroup.tagtrends.bayesclassifier.DebateClassifier;
import com.kleegroup.tagtrends.tools.CloudUpdater;
import com.kleegroup.tagtrends.tools.DebateUpdater;
import com.kleegroup.tagtrends.tools.Hacker;
import com.kleegroup.tagtrends.tools.HashtagUpdater;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


public class Analyzer extends Thread {
	private AnalyzerMode currentMode;
	private DBCollection dbCollection;
	private DBCollection dbCollectionBis;
	static LinkedList<String> goodDico;
	static LinkedList<String> badDico;
	static LinkedList<String> negationDico;
	static LinkedList<String> genericHashtags;
	
	
	public Analyzer(AnalyzerMode currentMode, DBCollection dbCollection) {
		this(currentMode, dbCollection, null);
	}
	
	public Analyzer(AnalyzerMode currentMode, DBCollection dbCollection, DBCollection dbCollectionBis) {
		super("Analyzer");
		this.currentMode = currentMode;
		this.dbCollection = dbCollection;
		this.dbCollectionBis = dbCollectionBis;
		try {
			Hacker hacker = new Hacker();
			goodDico = hacker.hackDico("good");
			badDico = hacker.hackDico("bad");
			negationDico = hacker.hackDico("negation");
			genericHashtags = hacker.hackHashtagsNoBlanks("genericHashtags");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		switch (currentMode) {
		case simpleCopy:
			simpleCopy(0);
			break;
		case countHashtags:
			countHashtags();
			break;
		default:
			break;
		}
	}
		
	public DBCollection simpleCopy(int n) {
		DBCursor cursor;
		if (n>0) { cursor = dbCollection.find().limit(n);}
		else if (n==0) { cursor = dbCollection.find().limit(10000);}
		else { cursor = dbCollection.find();}
		if (cursor==null){
			System.out.println("Pas de données à copier");
			return null;
		} else {
			Iterator<DBObject> itrc = cursor.iterator();
			while (itrc.hasNext()) {
				DBObject dbo = itrc.next();
				dbCollectionBis.insert(dbo);
			}
			System.out.println(cursor.length() + " objets copiés de "
					+ dbCollection.getName() + " dans "
					+ dbCollectionBis.getName());
			return dbCollectionBis;
		}
	}


	private DBCollection countHashtags() {
		long interval = 60*6*1000; // 6 minutes (unit: ms)
		/* aggregate all hashtags in a HashSet */
		HashSet<String> htagsSet = new HashSet<>();
		long timeChunk = 3600*1000;
		long referenceTime = (long) dbCollection.findOne(new BasicDBObject("text", new BasicDBObject("$exists",true))).get("date");
		System.out.println("referenceTime : "+referenceTime);
		int up = 0; int down = -1;
		while (true){
			/* Query on [referenceTime + timeChunk * up, referenceTime + timeChunk * (up+1)]
			* Break when there's no time values above current slice of time */
			System.out.println("New chunk of hashtag aggregation");
			if(!findHashtags(htagsSet, referenceTime+timeChunk*up, timeChunk)) break;
			up++;
		}
		while (true){
			/* Query on [referenceTime + timeChunk * down, referenceTime + timeChunk * (down+1)]
			* Break when there's no time values under current slice of time */
			System.out.println("New chunk of hashtag aggregation");
			if (!findHashtags(htagsSet, referenceTime+timeChunk*down, timeChunk)) break;
			down--;
		}
		/* treat each hashtag */
		System.out.println(htagsSet.size()+" hashtags to analyze");
		for (String htag : htagsSet){
			System.out.println("Analysis of hashtag #"+htag);
			analyzeHashtag(htag, interval);
		}
		/* print sample of result in the console */
		DBCursor cursor = dbCollection.find(new BasicDBObject("total", new BasicDBObject("$exists",true))).sort(new BasicDBObject("total", -1)).limit(10);
		while(cursor.hasNext()){
			System.out.println(cursor.next());
		} return dbCollection;
		
	}
	
	private boolean findHashtags(HashSet<String> htagsSet, long lowestTime,
			long timeChunk) {
		BasicDBObject[] hashtagQuery = new BasicDBObject[] {new BasicDBObject("text", new BasicDBObject("$exists",true)), new BasicDBObject("date", new BasicDBObject("$gte", lowestTime)), new BasicDBObject("date", new BasicDBObject("$lt", lowestTime + timeChunk))};
		DBCursor cursor = dbCollection.find(new BasicDBObject("$and",hashtagQuery),new BasicDBObject("entities",1));
		System.out.println(cursor.size()+" tweets found in this chunk of time");
		for (Object tweet : cursor){
			BasicDBList tweetHashtags =  (BasicDBList) ((BasicDBObject) ((BasicDBObject) tweet).get("entities")).get("hashtags");
			for (Object htag : tweetHashtags){
				if (!genericHashtags.contains(((String) ((BasicBSONObject) htag).get("text")).toLowerCase(Locale.ENGLISH))){
					htagsSet.add((String) ((BasicBSONObject) htag).get("text"));
				}
			}
		}
		return cursor.size()!=0;
	}
	
	private void analyzeHashtag(String hashtag, long interval){
		HashtagUpdater hashtagUpdater = new HashtagUpdater(hashtag);
		/* find tweets bearing current hashtag */
		DBCursor cursor = dbCollection.find(new BasicDBObject("entities.hashtags",
				new BasicDBObject("$elemMatch", new BasicDBObject("text",
						hashtag))));
		Iterator<DBObject> itrc = cursor.iterator();
		while(itrc.hasNext()){
			BasicDBObject tweet = (BasicDBObject) itrc.next();
			long date = (long) tweet.get("date");
			long time = date - date%interval;
			String text = (String) tweet.get("text");
			int[] semanticsAndNegation = new int[] {countSemantics(text), countNegation(text)};
			hashtagUpdater.add(time, semanticsAndNegation);
		} 
		dbCollection.insert(hashtagUpdater.mongoInsertion());
	}
	
	public void analyzeCloud(String hashtag) throws Exception {
		BasicDBObject[] conditions = {new BasicDBObject("cloud", new BasicDBObject("$exists",true)), new BasicDBObject("hashtag",hashtag)};
		DBObject hashtagQuery = new BasicDBObject("$and", conditions);
		
		/* do not calculate cloud if already done */
		if (dbCollection.findOne(hashtagQuery) == null) {
			BasicDBObject queryTweet = new BasicDBObject("entities.hashtags",
					new BasicDBObject("$elemMatch", new BasicDBObject("text",
							hashtag)));
			DBCursor cursor = dbCollection.find(queryTweet, new BasicDBObject(
					"text", 1));
			CloudUpdater cloudUpdater = new CloudUpdater();
			for (DBObject o : cursor) {
				/* increment fields for the hashtag in the cloudUpdater hashmap */
				incrementCloud(cloudUpdater, (String) o.get("text"));
			}
			dbCollection.update(new BasicDBObject("hashtag", hashtag), new BasicDBObject("$set", cloudUpdater.mongoQuery()), true, false);
		}
	}
	
	public void incrementCloud(CloudUpdater cloudUpdater, String text) throws Exception{
		String s = (new Hacker()).summarizeAndHomogenizeString(" "+text);
		String[] words = s.split(" ");
		for (String tag : words) {
			if (tag.length() > 0) {
				cloudUpdater.add(tag);
			}
		}
	}
	
	public void analyzeDebate(String hashtag, DebateClassifier debateClassifier, String topic) throws Exception {
		BasicDBObject[] conditions = {new BasicDBObject("for", new BasicDBObject("$exists",true)), new BasicDBObject("hashtag",hashtag)};
		DBObject debateQuery = new BasicDBObject("$and", conditions);
		
		/* do not calculate cloud if already done */
		if (dbCollection.findOne(debateQuery) == null) {
			BasicDBObject queryTweet = new BasicDBObject("entities.hashtags",
					new BasicDBObject("$elemMatch", new BasicDBObject("text",
							hashtag)));
			DBCursor cursor = dbCollection.find(queryTweet, new BasicDBObject(
					"text", 1));
			DebateUpdater debateUpdater = new DebateUpdater(topic);
			for (DBObject o : cursor) {
				/* 
				 * increment fields for the hashtag for the 2 counters of the debateUpdater
				 */
				debateUpdater.add(debateClassifier.classify((String) o.get("text")));
			}
			/* 
			 * update instead of "insert" so as to erase eventual previous debate-analysis 
			 */
			dbCollection.update(new BasicDBObject("hashtag", hashtag), new BasicDBObject("$set", debateUpdater.mongoQuery()));
		};
	}
	
	
	public int countSemantics(String text) {
		int cs = 0;
		for (String positive : goodDico) {
			if (text.indexOf(positive) != -1) {
				cs++;
			}
		}
		for (String negative : badDico) {
			if (text.indexOf(negative) != -1) {
				cs--;
			}
		}
		return cs;
	}

	public int countNegation(String text) {
		int cn = 0;
		for (String negation : negationDico) {
			if (text.indexOf(negation) != -1) {
				cn++;
			}
		}
		return cn;
	}
	
	
}

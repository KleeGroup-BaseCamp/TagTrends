package com.kleegroup.tagtrends.global;
import java.net.UnknownHostException;

import com.kleegroup.tagtrends.bayesclassifier.DebateClassifier;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class Main {
	static long time;
	static DB twitterDb;
	private PrimaryStocker primaryStocker;
	public FinalStocker finalStocker;
	private TwitterCollecter collecter;
	private Treatment treatment;
	public Analyzer analyzer;
	
	public static void main(String[] args) throws Exception {
		init();
		try {
			runCollecter(CollecterMode.filterFriends, 5.);
		} finally {
			tearsDown();
		}
	}
	
	public static void init(){
		time = System.currentTimeMillis();
		twitterDb = Database.getDB();
	}
	
	public static void tearsDown(){
		System.out.println(System.currentTimeMillis() - time
				+ " ms to go through !");
	}

	public static void runAnalyzer(AnalyzerMode mode, String collectionName) throws InterruptedException {
		Analyzer analyzer = new Analyzer(mode,
		twitterDb.getCollection(collectionName));
		analyzer.start();
		analyzer.join();
	}
	
	public static void runCollecter(CollecterMode mode, double secondsDuration) throws InterruptedException {
		TwitterCollecter twitterCollecter = new TwitterCollecter(mode, new PrimaryStocker(),secondsDuration);
		twitterCollecter.start();
		twitterCollecter.join();
	}
	
	public static void countDebate(String collectionName, String hashtag, String topic) throws Exception{
		
		DBCollection dbCollection = twitterDb.getCollection(collectionName);

		FinalStocker finalStocker = new FinalStocker(twitterDb,
				collectionName);
		finalStocker.startDB();
		finalStocker.printCollection();
		System.out.println(dbCollection.findOne(new BasicDBObject("hashtag",hashtag)));
		
		DebateClassifier debateClassifier = new DebateClassifier(topic);
		Analyzer analyzer = new Analyzer(AnalyzerMode.simpleCopy, dbCollection);
		analyzer.analyzeDebate(hashtag, debateClassifier, topic);
		System.out.println(dbCollection.findOne(new BasicDBObject("hashtag",hashtag)));
		System.out.println(dbCollection.findOne(new BasicDBObject("hashtag",hashtag)).get("debate"));
	}

	public void stockLast() throws UnknownHostException,
			InterruptedException {
		collecter = new TwitterCollecter(CollecterMode.stockLast,
				primaryStocker, 0.5);
		treatment = new Treatment(TreatmentMode.simpleTransfer, primaryStocker,
				finalStocker);
		collecter.start();
		collecter.join();
		System.out.println(collecter);
	}

	public void autoFilterLanguage() throws UnknownHostException,
			InterruptedException {
		collecter = new TwitterCollecter(CollecterMode.filterLong,
				primaryStocker, 0.5);
		treatment = new Treatment(TreatmentMode.simpleTransfer, primaryStocker,
				finalStocker);
		collecter.start();
		collecter.join();
		System.out.println(collecter);
	}

	public void entireChain() throws UnknownHostException,
			InterruptedException {
		collecter = new TwitterCollecter(CollecterMode.filterLong,
				primaryStocker, 3);
		treatment = new Treatment(TreatmentMode.simpleTransfer, primaryStocker,
				finalStocker);
		finalStocker.clearCollection();

		collecter.start();
		treatment.start();
		collecter.join();
		treatment.cancel();
		treatment.join();

		finalStocker.printCollection();
		System.out.println(finalStocker.findOne());
	}

	public void countWords() throws UnknownHostException,
			InterruptedException {
		collecter = new TwitterCollecter(CollecterMode.stockLast,
				primaryStocker, 0.5);
		treatment = new Treatment(TreatmentMode.simpleTransfer, primaryStocker,
				finalStocker);
		// obligatoire pour ne pas échouer à "tearsDown"

		finalStocker.printCollection();
		
		DBCollection collectionData = twitterDb.getCollection("provisory");
		DBCollection collectionResults = twitterDb.getCollection("words");
		collectionResults.remove(new BasicDBObject());
		analyzer = new Analyzer(AnalyzerMode.countWords, collectionData,
				collectionResults);
		analyzer.start();
		analyzer.join();
	}

	public void countHashtags() throws UnknownHostException,
			InterruptedException {
		collecter = new TwitterCollecter(CollecterMode.stockLast,
				primaryStocker, 0.5);
		treatment = new Treatment(TreatmentMode.simpleTransfer, primaryStocker,
				finalStocker);
		// obligatoire pour ne pas échouer à "tearsDown"

		finalStocker.printCollection();
		DBCollection collectionData = twitterDb.getCollection("provisory");
		DBCollection collectionResults = twitterDb.getCollection("hashtags");
		collectionResults.remove(new BasicDBObject());
		analyzer = new Analyzer(AnalyzerMode.countHashtags, collectionData,
				collectionResults);
		analyzer.start();
		analyzer.join();
	}

	
	public void testFilterLong() throws UnknownHostException,
			InterruptedException {
		collecter = new TwitterCollecter(CollecterMode.filterLong,
				primaryStocker, -1);
		treatment = new Treatment(TreatmentMode.simpleTransfer, primaryStocker,
				finalStocker);
		collecter.start();
		treatment.start();
		collecter.join();
		treatment.cancel();
		treatment.join();
		finalStocker.printCollection();
	}

	public void testSampleLong() throws UnknownHostException,
			InterruptedException {
		collecter = new TwitterCollecter(CollecterMode.sampleLong,
				primaryStocker, 0.5);
		treatment = new Treatment(TreatmentMode.simpleTransfer, primaryStocker,
				finalStocker);
		collecter.start();
		treatment.start();
		collecter.join();
		treatment.cancel();
		treatment.join();
		finalStocker.printCollection();
	}
	public static void print(Object o){
		System.out.println(o);
	}
}

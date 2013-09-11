package com.kleegroup.tagtrends.global;
import java.net.UnknownHostException;
import java.util.Date;

import com.kleegroup.tagtrends.bayesclassifier.DebateClassifier;
import com.kleegroup.tagtrends.tools.Hacker;
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
			FinalStocker fs = new FinalStocker(twitterDb, "TTLEssay");
			fs.startDB();
		


			
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

	public void manualFilter() throws UnknownHostException,
			InterruptedException {
		collecter = new TwitterCollecter(CollecterMode.filterLong,
				primaryStocker, 0.5);
		treatment = new Treatment(TreatmentMode.filterAndTransfer,
				primaryStocker, finalStocker);
		collecter.start();
		treatment.start();
		collecter.join();
		treatment.cancel();
		treatment.join();
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

	public void upsert() throws UnknownHostException {
		collecter = new TwitterCollecter(CollecterMode.stockLast,
				primaryStocker, 0.5);
		treatment = new Treatment(TreatmentMode.simpleTransfer, primaryStocker,
				finalStocker);
		// obligatoire pour ne pas échouer à "tearsDown"

		DBCollection collectionUpsert = twitterDb.getCollection("myMiniDB");
		collectionUpsert.remove(new BasicDBObject());
		collectionUpsert.update(new BasicDBObject("name", "leo"),
				new BasicDBObject("$inc", new BasicDBObject("age", 1)), true,
				false);

		DBCursor cursor = collectionUpsert.find().limit(1);
		while (cursor.hasNext()) {
			System.out.println(cursor.next());
		}
		collectionUpsert.update(new BasicDBObject("name", "leo"),
				new BasicDBObject("$inc", new BasicDBObject("age", 1)), true,
				false);
		collectionUpsert.update(new BasicDBObject("name", "leo"),
				new BasicDBObject("$inc", new BasicDBObject("age", 1)), true,
				false);
		collectionUpsert.update(new BasicDBObject("name", "leo"),
				new BasicDBObject("$inc", new BasicDBObject("age", 1)), true,
				false);
		cursor = collectionUpsert.find();
		while (cursor.hasNext()) {
			System.out.println(cursor.next());
		}
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

	/* whole cycles of collect and analysis */

	public void countHashtagsSimultaneously() throws UnknownHostException,
			InterruptedException {
		int nombre_sessions = 3;

		DBCollection collectionData = twitterDb.getCollection("provisory");
		collectionData.remove(new BasicDBObject());
		DBCollection collectionResults = twitterDb.getCollection("myFirstDB");
		collectionResults.remove(new BasicDBObject());
		analyzer = new Analyzer(AnalyzerMode.countHashtags, collectionData,
				collectionResults);

		for (int session = 0; session < nombre_sessions; session++) {
			System.out.println("Début de la session " + (session + 1) + " sur "
					+ nombre_sessions);
			collecter = new TwitterCollecter(CollecterMode.filterLong,
					primaryStocker, 4); // on récolte pendant 4 secondes
			treatment = new Treatment(TreatmentMode.simpleTransfer,
					primaryStocker, finalStocker);
			collecter.start();
			treatment.start();
			collecter.join();
			treatment.cancel();
			treatment.join();
			analyzer.join();
			// on attend la fin de l'analyse de la session précédente
			analyzer = new Analyzer(AnalyzerMode.countHashtags, collectionData,
					collectionResults);
			analyzer.start();
			while (!analyzer.isReady()) {
				Thread.sleep(100);
				// on attend que l'analyzer pose un curseur sur la session qui
				// vient de se dérouler
			}
			analyzer.setNotReady();
		}
		analyzer.join();
		DBCursor cursor = collectionResults.find()
				.sort(new BasicDBObject("total", -1)).limit(10);
		System.out.println("\nHashtags globalement les plus fréquents :");
		while (cursor.hasNext()) {
			System.out.println(cursor.next());
		}

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

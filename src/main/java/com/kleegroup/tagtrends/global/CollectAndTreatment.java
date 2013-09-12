package com.kleegroup.tagtrends.global;

import com.kleegroup.tagtrends.tools.Hacker;

public class CollectAndTreatment {
	private PrimaryStocker primaryStocker;
	public FinalStocker finalStocker;
	private TwitterCollecter collecter;
	private Treatment treatment;
	private String[] emptyWords;
		/*
		 * duration of collect (seconds).
		 * choose -1 for infinite collect.
		 */
	public CollectAndTreatment(String finalStockerName, int duration) throws Exception {
		primaryStocker = new PrimaryStocker();
		Hacker hacker = new Hacker();
		emptyWords = hacker.hackEmptyToArray("empty");
		collecter = new TwitterCollecter(CollecterMode.filterLong, primaryStocker, duration, emptyWords);
		 finalStocker = new FinalStocker(Database.getDB(),finalStockerName);
		treatment  = new Treatment(TreatmentMode.simpleTransfer, primaryStocker, finalStocker);
		 finalStocker.startDB();
	}
	
	public void start(){
		collecter.start();
		treatment.start();
	}
	
	public void join() throws InterruptedException{
		collecter.join();
		treatment.join();
	}
	
	public void stop() throws InterruptedException{
		collecter.interrupt();
		treatment.cancel();
		treatment.join();
		treatment.interrupt();
	}

	public static void testCollectAndTreatment() throws Exception {
		CollectAndTreatment infiniteCollectAndTreatment = new CollectAndTreatment("TTLEssay", -1);
		infiniteCollectAndTreatment.finalStocker.printCollection();
		System.out.println("Press a key to start");
		System.in.read();
		infiniteCollectAndTreatment.start();
    }
	
	/* used by the MyResourceCollect */
	public static void launchCollectAndTreatment(String collectionName, int duration) throws Exception {
		CollectAndTreatment collectAndTreatment = new CollectAndTreatment(collectionName, duration);
		collectAndTreatment.start();
		Thread.sleep(duration*1000);
		collectAndTreatment.stop();
    }
	
	public static void main(String[] args) throws Exception {
		launchCollectAndTreatment("essai", 5);
	 }
}

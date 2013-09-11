package com.kleegroup.tagtrends.global;

import com.kleegroup.tagtrends.tools.Hacker;

public class InfiniteCollectAndTreatment {
	private PrimaryStocker primaryStocker;
	public FinalStocker finalStocker;
	private TwitterCollecter collecter;
	private Treatment treatment;
	private String[] emptyWords;
		
	public InfiniteCollectAndTreatment(String finalStockerName) throws Exception {
		primaryStocker = new PrimaryStocker();
		Hacker hacker = new Hacker();
		emptyWords = hacker.hackEmptyToArray("empty");
	for (int i=0; i<emptyWords.length; i++){
		System.out.print(emptyWords[i]+" ");
	}
		collecter = new TwitterCollecter(CollecterMode.filterLong, primaryStocker, -1, emptyWords);
		 finalStocker = new FinalStocker(Database.getDB(),finalStockerName);
		treatment  = new Treatment(TreatmentMode.simpleTransfer, primaryStocker, finalStocker);
		 finalStocker.startDB();
	}
	
	public void start(){
		collecter.start();
		treatment.start();
	}
	
	public void stop() throws InterruptedException{
		collecter.interrupt();
		treatment.cancel();
		treatment.join();
		treatment.interrupt();
	}

	public static void main(String[] args) throws Exception {
		InfiniteCollectAndTreatment infiniteCollectAndTreatment = new InfiniteCollectAndTreatment("TTLEssay");
		infiniteCollectAndTreatment.finalStocker.printCollection();
		System.out.println("Press a key to start");
		System.in.read();
		infiniteCollectAndTreatment.start();
    }
}

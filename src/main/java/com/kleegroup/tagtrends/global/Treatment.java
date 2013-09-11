package com.kleegroup.tagtrends.global;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;


public class Treatment extends Thread {
	private static int treatmentIdCounter = 1;
	private static AtomicInteger count=new AtomicInteger(0);
	private final PrimaryStocker primaryStocker;
	private final FinalStocker finalStocker;
	private volatile boolean cancelled;
	private TreatmentMode currentMode;
//	private long  thisMorning = (System.currentTimeMillis()/(1000*60*60*24))*1000*60*60*24;
//	private final long time_session = (System.currentTimeMillis()-thisMorning)/1000; // nombre de s depuis le matin (heure GMT)
	
	public Treatment(TreatmentMode currentMode, PrimaryStocker primaryStocker, FinalStocker finalStocker){
		super("Treatment #"+treatmentIdCounter++);
		this.primaryStocker = primaryStocker;
		this.finalStocker = finalStocker;
		this.currentMode = currentMode;
//		System.out.println("time_session : "+time_session);
	}
	
	public void run() {
		switch (currentMode) {
		case simpleTransfer:
			while (!isInterrupted() && !cancelled) {
				try {
					simpleTransfer();
				} catch (InterruptedException e) {
					break;
				}
			}
			break;
		case filterAndTransfer:
			try {
				filterAndTransfer(" pour ");
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		default:
			break;
		}
	}
		
	public String toString(){
		return "la queue contient "+getStock()+" éléments.";
	}
	
	public void cancel() {
		cancelled = true;
	}
	
	public void simpleTransfer() throws InterruptedException  {
		while (getStock()!=0) {
			String json = load();
			DBObject dbObject = (DBObject) JSON.parse(json);
			if (dbObject!=null){
				BasicDBObject lightTweet = new BasicDBObject();
				lightTweet.append("created_at", getDate(dbObject)); // Date object
				lightTweet.append("date", getDate(dbObject).getTime()); // long representing the `created_at` in ms since 1970
				lightTweet.append("text", dbObject.get("text"));
				lightTweet.append("entities", dbObject.get("entities"));
				System.out.println(lightTweet);
				finalStocker.insert(lightTweet);
			}			
		} 
		Thread.sleep(3000);
	}
	
	
	public void filterAndTransfer(String w) throws InterruptedException {
		while(getStock()!=0){
			filterOne(w);
		}
	}
	
	private void filterOne(String w) throws InterruptedException{
			String json = load();
			DBObject dbObject = (DBObject) JSON.parse(json);
			if (dbObject!=null && dbObject.get("text")!=null && dbObject.get("text").toString().contains(w)){
				finalStocker.insert(dbObject);	
				  Thread t = Thread.currentThread();
				     // prints the thread name
				System.out.println(t+" : "+count.getAndIncrement()+" tweet(s) trouvé(s) contenant \""+w+"\"");
			}
			//System.out.println(s.getText());
	}	
	
	public static Object getPlace(String json){
		DBObject dbObject = (DBObject) JSON.parse(json);
		if (dbObject!=null){
			return dbObject.get("place");
		}
		return null;
	}
	
	public static Object getGeoLocation(String json){
		DBObject dbObject = (DBObject) JSON.parse(json);
		if (dbObject!=null){
			return dbObject.get("coordinates");
		}
		return null;
	}
	
	public static Object getUser(String json){
		DBObject dbObject = (DBObject) JSON.parse(json);
		if (dbObject!=null){
			return dbObject.get("user");
		}
		return null;
	}
	
	public static Object getLocation(String json){
		DBObject dbObject = (DBObject) getUser(json);
		if (dbObject!=null){
			return dbObject.get("location");
		}
		return null;
	}
	
	// time (ms) since 1970/01/01
		public static Date getDate(DBObject dbo){
			String date = (String) dbo.get("created_at"); // date format looks like  "Fri Aug 16 14:30:43 +0000 2013"
	SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.ENGLISH); 
	return sdf.parse(date, new ParsePosition(0));
		}
	
	public int getStock(){
		return primaryStocker.getStock();
	}
	
	public String load() throws InterruptedException { 
		return primaryStocker.load();
	}
}

package com.kleegroup.tagtrends.global;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import twitter4j.FilterQuery;
import twitter4j.IDs;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

public class TwitterCollecter extends Thread {
	private AtomicInteger counter = new AtomicInteger(0);
	private final PrimaryStocker primaryStocker;
	/* time of the collect (seconds) */
	private double timeLimit;
	private String[] toFilter;
	private CollecterMode currentMode;

	public TwitterCollecter(CollecterMode mode, PrimaryStocker primaryStocker,
			double timeLimit, String[] toFilter) {
		super("TwitterCollecter");
		this.currentMode = mode;
		this.primaryStocker = primaryStocker;
		this.timeLimit = timeLimit;
		this.toFilter = toFilter;
	}
	
	public TwitterCollecter(CollecterMode mode, PrimaryStocker primaryStocker,
			double timeLimit) {
		super("TwitterCollecter");
		this.currentMode = mode;
		this.primaryStocker = primaryStocker;
		this.timeLimit = timeLimit;
		this.toFilter = null;
	}

	public void run() {
		TwitterStream twitterStream;
		switch (currentMode) {
		case stockLast:
				try {
					try {
						counter.set(0);
						stockLast();
						System.out.println("+ "+counter.get() + " elements.");
					} catch (InterruptedException e) {
					}
				} catch (TwitterException e) {
					System.err.println(this
							+ " : Erreur de connexion à Twitter.");
					e.printStackTrace();
					break;
				}
			break;
		case filterLong:
			twitterStream = filterText();
			listenTwitter();
			twitterStream.shutdown();
			break;
		case sampleLong:
			counter.set(0);
			twitterStream = sampleLong();
			listenTwitter();
			twitterStream.shutdown();
			break;
		case filterFriends:
			try {
				counter.set(0);
				twitterStream = filterFriends();
				listenTwitter();
				twitterStream.shutdown();
				break;
			} catch (TwitterException e) {
				System.err.println(this
						+ " : Erreur de connexion à Twitter.");
				e.printStackTrace();
				break;
			}
		}
		System.out.println(this);
	}

	private void listenTwitter() {
		do {
			try {
				Thread.sleep(Math.round(Math.abs(timeLimit) * 1000));
			} catch (InterruptedException e) {
				// rien on stoppe l'attente
			}
		} while(timeLimit < 0 && !isInterrupted());
	}

	/* 
	 * get large but random stream of tweets (beware: strange languages...) 
	 */
	private TwitterStream sampleLong() {
		Listener listener = new Listener() {
			public void onStatus(Status status) {
				try {
					counter.getAndIncrement();
					String j = twitter4j.json.DataObjectFactory
							.getRawJSON(status);
					store(j);
					System.out.println(status.getUser().getName() + " : "
							+ status.getText());
				} catch (InterruptedException e1) {
					// rien
				}
			}
		};
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.addListener(listener);
		// sample() method internally creates a thread which manipulates
		// TwitterStream and calls these adequate listener methods continuously.
		twitterStream.sample();
		return twitterStream;
	}

	/* 
	 * filter according to the language and to an array of words
	 * (can not choose a language without giving an array of words)
	 */
	private TwitterStream filterText() {
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		// Listener
		Listener listener = new Listener() {
			public void onStatus(Status status) {
				counter.getAndIncrement();
				String j = twitter4j.json.DataObjectFactory.getRawJSON(status);
				try {
					store(j);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		twitterStream.addListener(listener);
		// Filter
		FilterQuery filtre = new FilterQuery();
		filtre.language(new String[] { "fr" });
		filtre.track(toFilter);
		twitterStream.filter(filtre);
		return twitterStream;
	}
	
	/* 
	 * filter according to an array of users' IDs
	 */
	private TwitterStream filterFriends() throws TwitterException {
		long[] followArray = getFriendsIds();
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		// Listener
		Listener listener = new Listener() {
			public void onStatus(Status status) {
				counter.getAndIncrement();
				String j = twitter4j.json.DataObjectFactory.getRawJSON(status);
				System.out.println(j);
			}
		};
		twitterStream.addListener(listener);
		// Filter
        // filter() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
        twitterStream.filter(new FilterQuery(0, followArray, new String[] {"et","le","la"}));
		return twitterStream;
	}

	/* 
	 * get last tweets edited on your homeTimeLine 
	 */
	private void stockLast() throws InterruptedException, TwitterException {
		Twitter twitter = TwitterFactory.getSingleton();
		List<Status> statuses = twitter.getHomeTimeline();
		System.out.println("Showing home timeline.");
		for (Status status : statuses) {
			String j = twitter4j.json.DataObjectFactory.getRawJSON(status);
			store(j);
		}
	}

	
	/* 
	 * get an array of the friends' IDs of the authenticated user 
	 */
	private static long[] getFriendsIds() throws TwitterException{
	       	Twitter twitter = new TwitterFactory().getInstance();
        	long cursor = -1;
            IDs ids;
            System.out.println("Listing following ids.");
            do {
                ids = twitter.getFriendsIDs(cursor);
                for (long id : ids.getIDs()) {
                    System.out.println(id);
                }
            } while ((cursor = ids.getNextCursor()) != 0);
           return ids.getIDs();
	}
	
	
	public String toString() {
		return "la queue contient " + getStock() + " éléments.";
	}

	public int getStock() {
		return primaryStocker.getStock();
	}

	public void store(String json) throws InterruptedException {
		primaryStocker.store(json);
	}
	
	
	    	
	 
}

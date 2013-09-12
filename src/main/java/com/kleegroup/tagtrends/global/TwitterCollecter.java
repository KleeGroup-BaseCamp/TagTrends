package com.kleegroup.tagtrends.global;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import twitter4j.FilterQuery;
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
//			while (!isInterrupted()
//					&& (System.currentTimeMillis() - start) < (timeLimit * 1000)) {
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
		//	}
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

	/* get big but random stream of tweets */
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

	private TwitterStream filterText() {
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		//System.out.println("!!! JE COMMENCE UNE NOUVELLE COLLECTE !!!");
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
		// filtre.locations(new double[][] {{-180,-90},{180,90}});
		filtre.track(toFilter);
		twitterStream.filter(filtre);
		return twitterStream;
	}

	/* get last tweets edited on your homeTimeLine */
	private void stockLast() throws InterruptedException, TwitterException {
		Twitter twitter = TwitterFactory.getSingleton();
		List<Status> statuses = twitter.getHomeTimeline();
		System.out.println("Showing home timeline.");
		for (Status status : statuses) {
			String j = twitter4j.json.DataObjectFactory.getRawJSON(status);
			store(j);
		}
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

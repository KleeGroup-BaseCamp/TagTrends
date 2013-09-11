package com.kleegroup.tagtrends.global;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;


public class Listener implements StatusListener{
	public Listener(){
		
	}
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
    	// System.out.println("LimitatedStatuses "+numberOfLimitedStatuses);
    }
    public void onException(Exception ex) {
        ex.printStackTrace();
    }
	@Override
	public void onScrubGeo(long arg0, long arg1) {
		System.out.println("ScrubGeo");
	}
	@Override
	public void onStallWarning(StallWarning arg0) {
		System.out.println("StallWarning");
	}
	@Override
	public void onStatus(Status arg0) {
		// TODO Auto-generated method stub
		
	}

}

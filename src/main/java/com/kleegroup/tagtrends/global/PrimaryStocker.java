package com.kleegroup.tagtrends.global;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PrimaryStocker{
    private BlockingQueue<String> queue;
    
    public PrimaryStocker(){
    	queue = new LinkedBlockingQueue<String>();
    }

    public int getStock() {
        return queue.size() ;
    }
    
    public String toString(){
		return queue.toString();
	}
    
  // si elle est pleine, il attend
  public  boolean store(String json) throws InterruptedException{
  	return queue.offer(json,  200, TimeUnit.MILLISECONDS) ;
 }

  // si elle est vide, il attend
  public String load() throws InterruptedException{
       return queue.take() ;     
 }
}
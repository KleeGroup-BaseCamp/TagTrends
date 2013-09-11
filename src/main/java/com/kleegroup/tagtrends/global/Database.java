package com.kleegroup.tagtrends.global;
import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
	 * Impl�mentation simple d'un singleton.
	 * L'instance est cr��e � l'initialisation. 
	 */
public class Database {
	private DB db;
	
		/** Constructeur priv� 
		 * @throws UnknownHostException */
		private Database() {
			try {
				ServerAddress serverAddress = new ServerAddress(
						"mongostd.dev.klee.lan.net");
				MongoClient mongoClient = new MongoClient(serverAddress);
				db = mongoClient.getDB("TwitterDB");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		
		/** Point d'acc�s pour l'instance unique du singleton */
	    public static DB getDB()
	    {
	      if (INSTANCE == null)
	          // it's ok, we can call this constructor
	    	  INSTANCE = new Database();
	      return INSTANCE.db;
	    }
	 
	    private static Database INSTANCE;
	
}

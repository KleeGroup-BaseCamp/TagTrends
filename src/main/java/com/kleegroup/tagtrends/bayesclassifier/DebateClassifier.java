package com.kleegroup.tagtrends.bayesclassifier;

import java.util.Arrays;
import java.util.LinkedList;

import com.kleegroup.tagtrends.tools.Hacker;

import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONObject;

public class DebateClassifier {
	final Classifier<String, String> bayes;
	private Hacker hacker;
	private int categorizedCounter;
	
	/* the data to learn is parsed from two local text files named after the relating topic */
	public DebateClassifier(String topic) throws Exception{
		categorizedCounter = 0;
		hacker = new Hacker();
		bayes = new BayesClassifier<String, String>();
				/* remember the last 500 learned classifications */
		bayes.setMemoryCapacity(500);
		LinkedList<String> forList = (LinkedList<String>) hacker
				.summarizeAndHomogenizeString(hacker.hackEmptyWords(topic+"Pour"));
		System.out.println(forList);
		for (String forArgument : forList) {
			bayes.learn("for", Arrays.asList(forArgument.split("\\s")));
		}
		LinkedList<String> againstList = (LinkedList<String>) hacker
				.summarizeAndHomogenizeString(hacker.hackEmptyWords(topic+"Contre"));
		System.out.println(againstList);
		for (String againstArgument : againstList) {
			bayes.learn("against", Arrays.asList(againstArgument.split("\\s")));
		}
		categorizedCounter += (againstList.size()+forList.size());
	}
	
	/* the data to learn comes in an array of jsonObjects structured as follows:
	 * [{"text":"blabla", "opinion":"for"/"against"/null},...]
	 */
	public DebateClassifier(String topic, JSONArray dataToLearn) throws Exception{
		System.out.println(dataToLearn);
		for (int i=0; i<dataToLearn.length(); i++) {
			System.out.println(dataToLearn.get(i));
		}
		categorizedCounter = 0;
		hacker = new Hacker();
		bayes = new BayesClassifier<String, String>();
				/* remember the last 500 learned classifications */
		bayes.setMemoryCapacity(500);
		for (int i=0; i<dataToLearn.length(); i++) {
			System.out.println(dataToLearn.getJSONObject(i).getClass());
			JSONObject categorizedText = dataToLearn.getJSONObject(i);
			String argument = hacker.summarizeAndHomogenizeString((String) categorizedText.get("text"));
			String opinion = (String) categorizedText.get("opinion");
			System.out.println(categorizedText.get("text"));
			System.out.println(argument);
			System.out.println(opinion);
			if (opinion.equals("for") || opinion.equals("against")){
				bayes.learn(opinion, Arrays.asList(argument.split("\\s")));
				categorizedCounter++;
			}
		}
		System.out.println(bayes.getFeatures());
	}
	
	public String classify(String unknownText){
		final String[] unknownTextTransformed = hacker.summarizeAndHomogenizeString(unknownText).split("\\s");
		
		/*
         * For each sentence, the classify method returns
         * a Classification Object, that contains the given featureset,
         * classification probability and resulting category.
         */
		System.out.println("\"" + unknownText + "\" classified as : "
				+ bayes.classify(Arrays.asList(unknownTextTransformed)).getCategory());
		
		 /*
         * The BayesClassifier extends the abstract Classifier and provides
         * detailed classification results that can be retrieved by calling
         * the classifyDetailed Method.
         *
         * The classification with the highest probability is the resulting
         * classification. The returned List will look like this.
         * [
         *   Classification [
         *     category=negative,
         *     probability=0.0078125,
         *     featureset=[today, is, a, sunny, day]
         *   ],
         *   Classification [
         *     category=positive,
         *     probability=0.0234375,
         *     featureset=[today, is, a, sunny, day]
         *   ]
         * ]
         */
		System.out.println(((BayesClassifier<String, String>) bayes)
				.classifyDetailed(Arrays.asList(unknownTextTransformed)));
		return bayes.classify(Arrays.asList(unknownTextTransformed)).getCategory();
	}

	public static void main(String[] args) throws Exception {
		// testLocalConstructor();
		//testJSONConstructor();
	
	}
	
	public static void testLocalConstructor() throws Exception{
		/* learn the local data */
		DebateClassifier debateClassifier = new DebateClassifier("interventionSyrie");

		/* test the classification process */
		String s1 = "Il faut intervenir en Syrie pour deux raisons.";
		System.out.println(debateClassifier.classify(s1));

		System.out.println("-----------------------------------------------------------");
		
		String s2 = " Il est scandaleux que l'on veuille intervenir sans preuve";
		System.out.println(debateClassifier.classify(s2));
	}
	
public static void testJSONConstructor() throws Exception{
	/* build the data to learn */
	JSONArray data = new JSONArray();
	String[] texts = new String[] {" RT il y aura une grève", "les gens sont contents !!", "gros succès pour cet événement :)", "Des protestations vigoureuses ont suivi cette annonce"};
	String[] opinions = new String[] {"against", "for", "for", "against"};
	for (int i=0; i<4; i++){
		JSONObject o = new JSONObject();
		o.put("text", texts[i]);
		o.put("opinion", opinions[i]);
		data.put(o);
	}
	
	/* learn the built data */
	DebateClassifier debateClassifier = new DebateClassifier("interventionSyrie", data);
	
	/* test the classification process */
	String s1 = " si c'est comme ça je fais la grève";
	System.out.println(debateClassifier.classify(s1));

	System.out.println("-----------------------------------------------------------");
	
	String s2 = "nous sommes si contents de cette annonce   ";
	System.out.println(debateClassifier.classify(s2));
	}

}

package com.kleegroup.tagtrends.tools;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;


public class Hacker {
	
	LinkedList<String> emptyDico;
	LinkedList<String> genericHashtags;
	
	public Hacker() throws Exception{
		emptyDico = this.hackEmptyWords("empty");
		genericHashtags = this.hackEmptyWords("genericHashtags");
	}
	
	
	/* 
	 * Takes documents with list of ","-separated words on each line and "#" separators before comments
	 * Returns LIST of words WITH one blank before each word
	 */
	public LinkedList<String> hackDico(String doc) throws Exception {
		BufferedReader br = docReader(doc);
		try {
			String words = readEmpty(br);
			words = words.replaceAll("\\s*,\\s*", ", ");
			words = words.replaceAll("(fam.)", "");
			words = words.replaceAll("(vx)", "");
			return splitter(words, ",");
		} finally {
			br.close();
		}
	}
	
	
	/* 
	 * Takes documents with one word per line and "|" separator before comments
	 * Returns an ARRAY of words with NO side blanks 
	 */
	public String[] hackEmptyToArray(String doc) throws Exception {
		BufferedReader br = docReader(doc);
		try {
			String words = readEmpty(br).substring(1);
			words = words.replaceAll("\\s*,\\s*", " , ");
			return words.split(" , ");
		} finally {
			br.close();
		}
	}
	
	
	/* 
	 * Takes documents with one word per line and "|" separator before comments
	 * Returns a LIST of words WITH one blank before and after each word 
	 */
	public LinkedList<String> hackEmptyWords(String doc) throws Exception {
		BufferedReader br = docReader(doc);
		try {
			String words = readEmpty(br)+" ";
			words = words.replaceAll("\\s*,\\s*", " , ");
			return splitter(words, ",");
		} finally {
			br.close();
		} 
	}
	
	
	/* 
	 * Takes documents with one word per line and "|" separator before comments
	 * Returns a LIST of words with NO side blanks 
	 */
	public LinkedList<String> hackHashtagsNoBlanks(String doc) throws Exception {
		BufferedReader br = docReader(doc);
		try {
			String words = readEmpty(br).substring(1);
			words = words.replaceAll("\\s*,\\s*", ",");
			return splitter(words, ",");
		} finally {
			br.close();
		}
	}
	
	/* 
	 * Removes all empty words from text 
	 */
	public String summarizeAndHomogenizeString(String s){
		s = clearPonctuation(s);
		s = s.toLowerCase(Locale.ENGLISH);
		for (String emptyWord : emptyDico){
			s = s.replaceAll(emptyWord, " ");
		}
		for (String genericHashtag : genericHashtags){
			s = s.replaceAll(genericHashtag, " ");
		}
		s = s.replaceAll("\\s{2,}", " ");
		s = clearSideBlanks(s);
		return s;
	}
	
	/* 
	 * idem for an iterable set of strings
	 */
	public Iterable<String> summarizeAndHomogenizeString(Iterable<String> i){
		LinkedList<String> treatedList = new LinkedList<String>();
		for (String s : i){
			treatedList.add(summarizeAndHomogenizeString(s));
		}
		return treatedList;
	}
	
	/* 
	 * Removes all punctuation from text 
	 */
	public String clearPonctuation(String s){
		s = s.replaceAll("\\p{Punct}", " ");
		s = s.replaceAll("\\s{2,}", " ");
		return s;
	}
		
	/* 
	 * Removes first and final blancks 
	 */
	public String clearSideBlanks(String s){
		while(s.charAt(0)==' '){
			s = s.substring(1);
		}
		while(s.charAt(s.length()-1)==' '){
			s = s.substring(0, s.length()-1);
		}
		return s;
	}
	
	/* 
	 * Provides BufferedReader to read in a file with the correct path
	 */
	public BufferedReader docReader(String doc) throws FileNotFoundException{
		return new BufferedReader(new FileReader(
				"C:/Users/cguinard/workspace/Tweeter/src/main/resources/" + doc + ".txt"));
	}
	
	/* 
	 * Parses documents with one word per line and "|" separator before comments
	 * Returns a STRING of words separated with ","
	 */
	public String readEmpty(BufferedReader br) throws IOException{
			String line = br.readLine();
			StringBuilder sb = new StringBuilder(); 
			/* until end of document */
			while (line != null) {
				sb.append(" ");
				if (line.length()>0 && line.charAt(0) != '#') {
					line = line.replaceAll("\\s*\\|.*", "");
					sb.append(line);
					sb.append(",");
				} line = br.readLine();
			}
			/* remove last coma */
			sb.deleteCharAt(sb.length()-1);
			String words = sb.toString();
			words = words.replaceAll(",\\s*,", ",");
			return words.replaceAll("\\s{2,}", " ");
	}
	
	/*
	 * Splits a string to an array according to the given separator
	 */
	public LinkedList<String> splitter(String toSplit, String separator){
		String[] arrayWords = toSplit.split(separator);
		LinkedList<String> listWords = new LinkedList<String>();
		for (String word : arrayWords) {
			listWords.add(word);
		}
		return listWords;
	}
}

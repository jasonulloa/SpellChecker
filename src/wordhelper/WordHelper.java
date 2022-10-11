package wordhelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;

import keyboard.Keyboard;
import trie.Trie;
import trie.Trie.TrieNode;

public class WordHelper {
	//Makes an array of all words with a given prefix
	public static String[] autoCompleteList(String prefix, Trie dictionary) {
		TrieNode tn = dictionary.prefix(prefix);
		
		if (tn == null) {
			return null;
		}
		
		ArrayList<String> words = new ArrayList<String>();
		Stack<TrieNode> stack = new Stack<TrieNode>();
		stack.add(tn);
		
		while (!stack.isEmpty()) {
			tn = stack.pop();
			
			if (tn.isLeaf()) {
				words.add(tn.getWord());
			}
			
			stack.addAll(tn.getChildren());
		}
		
		String[] toReturn = new String[words.size()];
		toReturn = words.toArray(toReturn);
		
		return toReturn;
	}
	
	//Makes a trie based off a word and keyboard for all possible words
	public static Trie makeAutoCorrectTrie(String word, Keyboard kb) {
		Trie trie = new Trie();
		ArrayList<TrieNode> nodes = new ArrayList<TrieNode>();
		nodes.add(trie.getRoot());
		
		for (Character ch : word.toCharArray()) {
			ArrayList<TrieNode> children = new ArrayList<TrieNode>();
			
			for (TrieNode tn : nodes) {
				Keyboard.Key chKey = kb.getKey(ch);
				children.add(tn.add(chKey.getSymbol()));
				
				for (Keyboard.Key nbr : chKey.getNeighbors()) {
					children.add(tn.add(nbr.getSymbol()));
				}
			}
			
			nodes = children;
		}
		
		for (TrieNode tn : nodes) {
			tn.makeLeaf();
		}
		
		return trie;
	}
	
	//Makes a list of all valid words that are similar to it in key position for each character
	public static String[] autoCorrectList(String string, Trie dictionary, Keyboard kb) {
		ArrayList<String> words = new ArrayList<String>();
		
		for (TrieNode tn : makeAutoCorrectTrie(string, kb).getLeafs()) {
			String word = tn.getWord();
			
			if (dictionary.contains(word)) {
				words.add(word);
			}
		}
		
		String[] toReturn = new String[words.size()];
		words.toArray(toReturn);
		
		return toReturn;
	}
	
	//Combines the auto correct trie and the auto complete
	public static String[] spellCheckAutoComplete(String string, Trie dictionary, Keyboard kb) {
		ArrayList<String> words = new ArrayList<String>();
		
		for (TrieNode tn : makeAutoCorrectTrie(string, kb).getLeafs()) {
			String word = tn.getWord();
			
			if (dictionary.isPrefix(word)) {
				for (String str : autoCompleteList(word, dictionary)) {
					words.add(str);
				}
			}
		}
		
		String[] toReturn = new String[words.size()];
		words.toArray(toReturn);
		
		return toReturn;
	}
	
	//Scores strings based on similarity of charcter of index in commonality and length
	static class StringSimilarityComparitor implements Comparator<String>{
		private String str;
		StringSimilarityComparitor(String s){
			str = s;
		}
		
		@Override
		public int compare(String o1, String o2) {
			int s1 = o1.length();
			int s2 = o2.length();
			
			for (int i = 0; i < str.length(); ++i) {
				if(!(str.charAt(i) == o1.charAt(i))) {
					s1++;
				}
				if(!(str.charAt(i) == o2.charAt(i))) {
					s2++;
				}
			}
			
			return s1 - s2;
		}
	}
	
	//Orders words based on similarity with a given string
	public static void order(String str, String[] words) {
		Arrays.sort(words, new StringSimilarityComparitor(str));
	}
	
	public static String[] getTopWithTies(String word, String[] results, int numResults) {
		ArrayList<String> topResultsWithTies = new ArrayList<String>();
		StringSimilarityComparitor ssc = new StringSimilarityComparitor(word);
		
		for (int i = 0; i < numResults; i++) {
			topResultsWithTies.add(results[i]);
		}
		
		int tie = ssc.compare(word, results[numResults - 1]);
		int index = numResults;
		
		while (tie == ssc.compare(word, results[index])) {
			topResultsWithTies.add(results[index]);
			index++;
			
			if (index == results.length) {
				break;
			}
		}
		
		String[] toReturn = new String[topResultsWithTies.size()];
		topResultsWithTies.toArray(toReturn);
		
		return toReturn;
	}
}
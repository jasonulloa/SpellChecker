package trie;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Trie {
	private final TrieNode root;
	private final Map<String, TrieNode> leafs;
	
	public static int created = 0;
	
	//instance constructor; runs when trie is created
	{
		root = new TrieNode('\0');
		leafs = new HashMap<String, TrieNode>();
	}
	
	public class TrieNode {
		private TrieNode parent;
		private Map<Character, TrieNode> children;
		private char character;
		private String word;
		
		//runs when a new TrieNode is created
		{
			created++;
			children = new HashMap<Character, TrieNode>();
			parent = null;
		}
		
		//regular constructor
		public TrieNode(char c) {
			character = c;
		}
		
		public TrieNode add(char c) {
			TrieNode newNode = children.get(c);
			
			//check if node already exists
			if (newNode != null) {
				return newNode;
			}
			
			//create if it doesn't exist
			newNode = new TrieNode(c);
			newNode.parent = this;
			
			return newNode;
		}
		
		public char getCharacter() {
			return character;
		}
		
		public String getWord() {
			return word;
		}
		
		public TrieNode getParent() {
			return parent;
		}
		
		public boolean isLeaf() {
			return word != null;
		}
		
		public boolean isRoot() {
			return parent == null;
		}
		
		//get list of all children nodes
		public Collection<TrieNode> getChildren() {
			return children.values();
		}
		
		public void makeLeaf() {
			TrieNode par = this;
			StringBuffer sb = new StringBuffer();
			
			//work backwards to create word
			while (!par.isRoot()) {
				sb.insert(0, par.getCharacter());
				par = par.getParent();
			}
			
			//record the word
			word = sb.toString();
			
			//retain word with path starting node 
			if (!leafs.containsKey(word)) {
				leafs.put(word, this);
			}
		}
	}
	
	public TrieNode getRoot() {
		return root;
	}
	
	public void add(String word) {
		//validate entry first
		if (word == null || word.length() == 0) {
			return;
		}
		
		TrieNode tnode = root;
		Map<Character, TrieNode> children = tnode.children;
		
		//go through word one letter at a time
		for (int i = 0; i < word.length(); ++i) {
			char c = word.charAt(i);
			
			//if node doesn't exist, make it
			if (!children.containsKey(c)) {
				TrieNode child = new TrieNode('c');
				child.parent = tnode;
				children.put(c, child);
			}
			
			//get the child nodes from this node
			tnode = children.get(c);
			children = tnode.children;
			
			//if at the end of word
			if (i == word.length() - 1) {
				//add word to leaf
				tnode.word = word;
				
				//put word in list if not there
				if (!leafs.containsKey(word)) {
					leafs.put(word, tnode);
				}
			}
		}
	}
	
	//returns node of given word; null if not found
	private TrieNode search(String word) {
		Map<Character, TrieNode> children = root.children;
		TrieNode tnode = null;
		
		for (int i = 0; i < word.length(); ++i) {
			char c = word.charAt(i);
			
			if (children.containsKey(c)) {
				tnode = children.get(c);
				children = tnode.children;
			}
			else {
				return null;
			}
		}
		
		return tnode;
	}
	
	//returns node that is the prefix of any existing word
	public TrieNode prefix(String word) {
		TrieNode tnode = search(word);
		
		return tnode;
	}
	
	//checks if prefix is a word contained in trie
	public boolean isPrefix(String word) {
		return prefix(word) != null;
	}
	
	//check if trie contains the given word
	public boolean contains(String word) {
		TrieNode tnode = search(word);
		
		if (tnode != null && tnode.word != null) {
			return true;
		}
		
		return false;
	}
	
	//returns all leafs of trie
	public TrieNode[] getLeafs() {
		TrieNode[] nodes = new TrieNode[leafs.size()];
		leafs.values().toArray(nodes);
		
		return nodes;
	}
}
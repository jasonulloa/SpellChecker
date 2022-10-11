package keyboard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Keyboard {
	//Map character to key
	private final Map<Character,Key> keys;
	
	{ //instance constructor
		keys = new HashMap<Character,Key>();
	}
	
	//Load in a keyboard
	public Keyboard(File config) throws IOException {
		Scanner scan = new Scanner(config);
		scan.useDelimiter("[,\n]");
		
		while (scan.hasNextLine()) {
			char symbol = scan.next().charAt(0);
			Key k = keys.get(symbol);
			
			if (k == null) {
				k = new Key(symbol);
				keys.put(symbol, k);
			}
			
			char[] neighbors = scan.next().trim().toCharArray();
			k.neighbors = new Key[neighbors.length];
			
			for (int i = 0; i < neighbors.length; ++i) {
				Key n = keys.get(neighbors[i]);
				
				if (n == null) {
					n = new Key(neighbors[i]);
					keys.put(neighbors[i], n);
				}
				
				k.neighbors[i] = n;
			}
		}
		
		scan.close();
	}
	
	//Keys have a symbol and a list of neighboring keys
	public class Key {
		private char symbol;
		private Key[] neighbors;
		
		Key(char symbol) {
			this.symbol = symbol;
		}
		
		public char getSymbol() {
			return symbol;
		}
		
		public Key[] getNeighbors() {
			return neighbors;
		}
	}
	
	//Get a given key
	public Key getKey(char c) {
		if (keys.containsKey(c)) {
			return keys.get(c);
		}
		else return null;
	}
}
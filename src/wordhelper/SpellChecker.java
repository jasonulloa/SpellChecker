package wordhelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import keyboard.Keyboard;
import keyboard.KeyboardLibrary;
import trie.Trie;

public class SpellChecker {
	private static final Scanner scan;
	
	static {
		scan = new Scanner(System.in);
	}
	
	public static final int WORDLIST = 0, KEYBOARD = 1, TEXT = 2;
	private static final String[] extensions = {".wl", ".kb", ".txt"};
	private static final String[] prompts = {"wordlist (.wl)", "keyboard (.kb)", "text (.txt)"};
	private Trie wordlist;
	private Keyboard keyboard;
	
	public static void main(String[] args) {
		File wlFile = null, kbFile = null, txtFile = null;
		
		for (String filename : args) {
			if (filename.endsWith(extensions[WORDLIST])) {
				wlFile = new File(filename);
			} else if (filename.endsWith(extensions[KEYBOARD])) {
				kbFile = new File(filename);
			} else if (filename.endsWith(extensions[TEXT])) {
				txtFile = new File(filename);
			}
		}
		
		SpellChecker sp = new SpellChecker();
		sp.loadWordList(wlFile);
		sp.loadKeyboard(kbFile);
		WordHelperResult[] results = sp.getResults(txtFile, 10);
		
		if (results != null) {
			WordHelperResult.printToConsole(results);
			WordHelperResult.writeToFile(results, new File(new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + ".txt"));
		}
	}
	
	public String[] getSpellingErrors(File wdlist) {
		if (wordlist == null) {
			return null;
		}
		
		ArrayList<String> errors = new ArrayList<String>();
		BufferedReader br = null;
		Scanner s = null;
		
		try {
			br = new BufferedReader(new FileReader(wdlist));
			String line;
			
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("[^A-Za-z ]", ""); //find all actual words in file
				s = new Scanner(line);
				s.useDelimiter("([^A-Za-z])");
				
				while (s.hasNext()) {
					String str = s.next();
					
					if (str.length() == 0) {
						continue;
					}
					
					str = str.toLowerCase();
					
					if (!wordlist.contains(str)) {  //found a spelling error
						errors.add(str);
					}
				}
			}
		} catch (IOException ioe) {
			System.out.println("Error reading file.");
		} finally {
			if (s != null) {
				s.close();
			}
		}
		
		String[] toReturn = new String[errors.size()];
		errors.toArray(toReturn);
		return toReturn;
	}
	
	public String[] getSpellingSuggestions(String str, int numResults) {
		String[] results = WordHelper.spellCheckAutoComplete(str, wordlist, keyboard);
		WordHelper.order(str, results);
		
		if (numResults >= 0) {
			String[] topResults;
			int size = numResults;
			
			if (size >= results.length) {
				size = results.length;
				topResults = new String[size];
				
				for (int i = 0; i < size; i++) {
					if (i == size) {
						break;
					}
					
					topResults[i] = results[i];
				}
			} else {
				topResults = WordHelper.getTopWithTies(str, results, numResults);
				results = topResults;
			}
			
		}
		
		return results;
	}
	
	//load wordlist from file
	public void loadWordList() {
		loadWordList(null);
	}
	
	public void loadWordList(File wlFile) {
		if (wlFile == null || !wlFile.exists()) {
			wlFile = getFileByType(WORDLIST);
		}
		
		try {
			Scanner s = new Scanner(wlFile);
			wordlist = new Trie();
			
			while (s.hasNextLine()) {
				wordlist.add(s.nextLine().toLowerCase());
			}
			
			s.close();
		} catch (FileNotFoundException fnfe) {
			wordlist = null;
			System.out.println("Wordlist file is invalid.");
		}
	}
	
	//load keyboard from file
	public void loadKeyboard() {
		loadKeyboard(null);
	}
	
	public void loadKeyboard(File kbFile) {
		if (kbFile == null || !kbFile.exists()) {
			kbFile = getFileByType(KEYBOARD);
		}
		
		KeyboardLibrary.loadKeyboard(kbFile.getAbsolutePath(), kbFile.getAbsolutePath());
		keyboard = KeyboardLibrary.getKeyboard(kbFile.getAbsolutePath());
	}
	
	//get results from given files
	public WordHelperResult[] getResults(File txtFile) {
		return getResults(txtFile, -1);
	}
	
	public WordHelperResult[] getResults(File txtFile, int numResults) {
		while (wordlist == null) {
			loadWordList();
		}
		
		while (keyboard == null) {
			loadKeyboard();
		}
		
		if (txtFile == null || !txtFile.exists()) {
			txtFile = getFileByType(TEXT);
		}
		
		ArrayList<WordHelperResult> whrList = new ArrayList<WordHelperResult>();
		String[] errors = getSpellingErrors(txtFile);
		
		for (String err : errors) {
			whrList.add(new WordHelperResult(err, getSpellingSuggestions(err, 10)));
		}
		
		WordHelperResult[] results = new WordHelperResult[whrList.size()];
		whrList.toArray(results);
		
		return results;
	}
	
	public File getFileByType(int type) {
		if (type < 0 || type > 2) {
			type = TEXT;
		}
		
		File toReturn = null;
		
		while (toReturn == null || !toReturn.exists()) {
			System.out.print("Please enter the name of a valid " + prompts[type] + "file:");
			String filename = scan.nextLine();
			
			if (!filename.endsWith(extensions[type])) {
				continue;
			}
			
			toReturn = new File(filename);
		}
		
		return toReturn;
	}
}
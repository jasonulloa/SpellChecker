package wordhelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class WordHelperResult {
	private final String mWord;  //The spelling error
	private final String[] mResults;  //The possible results
	
	WordHelperResult(String inWord, String[] inResults) {
		mWord = inWord;
		mResults = inResults;
	}
	
	//Writes an array of results to a given file
	public static void writeToFile(WordHelperResult[] results, File file) {
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(file);
			
			for (WordHelperResult result : results) {
				writer.write(result.toString() + "\n");
			}
			
			writer.flush();
		} catch (FileNotFoundException fnfe) {
			System.out.println("Error writing to output file.");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	//Prints an array of results to the console
	public static void printToConsole(WordHelperResult[] results) {
		for (WordHelperResult result : results) {
			System.out.println(result);
		}
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(mWord + ":");
		
		for (String result : mResults) {
			sb.append(" " + result);
		}
		
		return sb.toString();
	}
}
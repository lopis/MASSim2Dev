package massim2dev.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * This dictionary contains the mapping of classes between
 * JADE and SAJaS. This class contains a 1 to 1 mapping of
 * the classes.
 * @author joaolopes
 *
 */
public class Dictionary {


	/**
	 * Contains the mappings of SAJaS classes to JADE classes
	 */
	private static HashMap<String, String> forward = new HashMap<String, String>();

	/**
	 * Contains the mappings of JADE classes to SAJaS classes
	 */
	private static HashMap<String, String> backward = new HashMap<String, String>();

	/**
	 * Adds a new mapping to the dictionary.
	 * @param jadeClass
	 * @param sajasClass
	 */
	public static void add(String jadeClass, String sajasClass) {
		forward.put(jadeClass, sajasClass);
		backward.put(sajasClass, jadeClass);
	}

	public void remove(String className) {
		forward.remove(className);
		backward.remove(className);
	}

	public static String getJADEClass(String sajasClass) {
		return forward.get(sajasClass);
	}

	public static String getSAJaSClass(String jadeClass) {
		return backward.get(jadeClass);
	}

	public static void loadDictionaryFile(String fileName){
	    try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
	        String line = br.readLine();

	        while (line != null) {
	        	line.replaceAll("\\s+", "");
	        	
	        	
	        	if (line.length() > 0 &&	 // Ignore empty lines
	        		line.charAt(0) != '#') { // Ingore comments
	        		
	        		String[] entry = line.split("|");
	        		if (entry.length == 2) {
	        			add(entry[1], entry[0]);
					}
	        	}
	        	
	            line = br.readLine();	            
	        }
	    } catch (FileNotFoundException e) {
			System.err.println("Dictionary file not found. Quiting.");
		} catch (IOException e) {
			System.err.println("IOException. Failed to load dictionary. Quiting.");
		}
	    
	    System.out.println("Dictionary loaded");
	}
}

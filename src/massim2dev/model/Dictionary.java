package massim2dev.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	 * Contains the mappings of the classes
	 */
	private static HashMap<String, Entry> dictionary = new HashMap<String, Entry>();


	/**
	 * Adds a new mapping to the dictionary.
	 * @param jadeClass
	 * @param class
	 */
	public static void add(String key, Entry type) {
		dictionary.put(key, type);
	}

	public void remove(Entry className) {
		dictionary.remove(className);
	}

	/**
	 * Get the corresponding JADE class, given a SAJaS class
	 * @param sajasClass
	 * @return The JADE class, or null if there is no mapping.
	 */
	public static Entry get(String type) {
		return dictionary.get(type);
	}

	public static boolean loadDictionaryFile(String fileName){
		InputStream in = Dictionary.class.getClassLoader().getResourceAsStream(fileName);

		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = br.readLine();

			while (line != null) {
				line = line.replaceAll("\\s+", "");

				if (line.length() > 0 &&	 // Ignore empty lines
						line.charAt(0) != '#') { // Ingore comments

					boolean is = (line.charAt(0) == '§'); // Parse superclasses
					if (is) {
						line = line.substring(1);
						String[] entry = line.split("\\|");
						
						if (entry.length == 2 ) {
							String[] alternatives = entry[1].split("§");
							
							if (alternatives.length == 2) {
								add(entry[0], new Entry(alternatives[0], alternatives[1], is));
								System.out.println(entry[0] + " - " +  alternatives[0] + " - " +  alternatives[1]);
							} else {
								add(entry[0], new Entry(entry[1], is));
								System.out.println(entry[0] + " - " +  entry[1]);
							}
						} 
						
					} else {
						String[] entry = line.split("\\|");
						if (entry.length == 2) {
							add(entry[0], new Entry(entry[1], is));
						}
						
					}
				}

				line = br.readLine();	            
			}

		} catch (FileNotFoundException e) {
			System.err.println("Dictionary file not found. Quiting.");
			return false;
		} catch (IOException e) {
			System.err.println("IOException. Failed to load dictionary. Quiting.");
			return false;
		}

		System.out.println("Dictionary loaded");
		return true;
	}
}

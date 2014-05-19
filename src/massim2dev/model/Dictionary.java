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
	 * Contains the mappings of JADE classes to SAJaS classes
	 */
	private static HashMap<String, String> jade2sajas = new HashMap<String, String>();

	/**
	 * Contains the mappings of SAJaS classes to JADE classes
	 */
	private static HashMap<String, String> sajas2jade = new HashMap<String, String>();

	/**
	 * Adds a new mapping to the dictionary.
	 * @param jadeClass
	 * @param sajasClass
	 */
	public static void add(String jadeClass, String sajasClass) {
		jade2sajas.put(jadeClass, sajasClass);
		sajas2jade.put(sajasClass, jadeClass);
	}

	public void remove(String className) {
		jade2sajas.remove(className);
		sajas2jade.remove(className);
	}

	/**
	 * Get the corresponding JADE class, given a SAJaS class
	 * @param sajasClass
	 * @return The JADE class, or null if there is no mapping.
	 */
	public static String getJADEClass(String sajasClass) {
		HashMap<String, String> mapping = sajas2jade;
		String jadeClass = mapping.get(sajasClass);
		return jadeClass;
	}

	public static String getSAJaSClass(String jadeClass) {
		return jade2sajas.get(jadeClass);
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

					String[] entry = line.split("\\|");
					if (entry.length == 2) {
						add(entry[1], entry[0]);
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

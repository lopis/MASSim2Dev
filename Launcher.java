package launcher;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.StaleProxyException;

/**
 * This class is a wrapper to JADE's launcher/main class.
 * Override the setup() method to setup the agents, for instance.
 * Use the getters and setters to change the values of "gui", "port"
 * and "jade_domain_df_maxresult" in setupPlatform();
 * @author joaolopes
 *
 */
public class Launcher {
	
	private static Runtime runtime;
	private static jade.wrapper.AgentContainer container;
	private static Launcher instance;
	private static String property_gui = "true";
	private static String property_port = "8080";
	private static String property_dx_maxresult = "10000";
	
	
	
	public static void main(String[] args) {
		setupPlatform();
		runtime = jade.core.Runtime.instance();
		Profile profile = new ProfileImpl();
		profile.setParameter("gui", property_gui);
		profile.setParameter("port", property_port);
		profile.setParameter("jade_domain_df_maxresult", property_dx_maxresult );
		container = runtime.createMainContainer(profile);
		instance = new MyLauncher;
		instance.setup();
	}
	
	/**
	 * Called by the main before creating the platform.
	 * Use the gettters and setters to change the values of "gui", "port"
	 * and "jade_domain_df_maxresult".
	 */
	private static void setupPlatform() {}

	/**
	 * Called by the main method. Override this method.
	 */
	protected void setup() {}
	
	/**
	 * Agents are added to the AMS service and are
	 * given and AID. 
	 * @param name
	 * @param ra
	 * @return
	 */
	public void acceptNewAgent(String name, Agent a) {
		try {
			container.acceptNewAgent(name, a).start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
	
}

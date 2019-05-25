package taupegun.listeners;

import taupegun.exceptions.ListenersException;
import taupegun.start.TaupeGunPlugin;


/**
 * This class manage all listeners in the plugin
 * @author LetMeR00t
 *
 */
public class Listeners {

	/**
	 * Static field to check status of listeners
	 */
	private static EnumListenersStatus status = EnumListenersStatus.NOT_INSTANCIATED;
	
	/**
	 * This class has all listeners objects
	 */
	private Login loginListener = null;
	private Global globalListener = null;
	private Preparation preparationListener = null;
	private Ingame ingameListener = null;
	private ChatInventory chatInventory = null;
	
	/**
	 * Singleton object
	 */
	private static Listeners listeners = null;
	
	/**
	 * Private constructor for Listeners Class
	 */
	private Listeners(TaupeGunPlugin plugin){
		this.chatInventory = new ChatInventory(plugin);
		this.loginListener = new Login(plugin);
		this.globalListener = new Global(plugin);
		this.preparationListener = new Preparation(plugin,this.chatInventory);
		this.ingameListener = new Ingame(plugin);
	}
	
	/**
	 * Plugin object as reference
	 */
	private static TaupeGunPlugin plugin = null;
	
	/**
	 * Return the current status of listeners
	 * @return EnumListenersStatus enumeration
	 */
	public static EnumListenersStatus getStatus(){
		return status;
	}
	
	/**
	 * Instantiate the Listeners object, it has to be followed by startListeners() in order to start listening all events
	 * @throws ListenersException	An exception is raised if we want to instantiate listeners when they are already instantiated/listening
	 */
	public static void instanciateListeners(TaupeGunPlugin plugin) throws ListenersException{
		
		if (Listeners.getStatus() == EnumListenersStatus.NOT_INSTANCIATED){
			
			listeners = new Listeners(plugin);
			Listeners.plugin = plugin;
			setStatus(EnumListenersStatus.INSTANCIATED);
			
		}
		else{
			throw new ListenersException("Listeners have been already instanciated");
		}
		
	}
	
	/**
	 * Start the listeners
	 * @param plugin	TaupeGunPlugin object that will receive events
	 * @throws ListenersException	An exception is raised if we want to start listeners when they are not instantiated or already listening for events
	 */
	public static void startListeners() throws ListenersException{
		
		if (Listeners.getStatus() == EnumListenersStatus.INSTANCIATED){
			
			// Ask to the server to consider our listeners
			plugin.getServer().getPluginManager().registerEvents(listeners.loginListener, plugin);
			plugin.getServer().getPluginManager().registerEvents(listeners.globalListener, plugin);
			plugin.getServer().getPluginManager().registerEvents(listeners.preparationListener, plugin);
			plugin.getServer().getPluginManager().registerEvents(listeners.ingameListener, plugin);
			plugin.getServer().getPluginManager().registerEvents(listeners.chatInventory, plugin);
			
			setStatus(EnumListenersStatus.LISTENING);
			
		}
		else {
			
			if (Listeners.getStatus() == EnumListenersStatus.NOT_INSTANCIATED){
				
				throw new ListenersException("Listeners are not instanciated");
				
			}
			else if (Listeners.getStatus() == EnumListenersStatus.LISTENING){
				
				throw new ListenersException("Listeners are already listening");
				
			}
			else {
				
				throw new ListenersException("[SEVERE] Listeners have a problem with the status enumeration");
			}
			
		}
	}
	
	/**
	 * Set the status for Listeners
	 * @param newStatus	the new EnumListenersStatus status
	 */
	private static void setStatus(EnumListenersStatus newStatus){
		Listeners.status = newStatus;
	}
	
}
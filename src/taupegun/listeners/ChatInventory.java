package taupegun.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import taupegun.start.TaupeGunPlugin;
import taupegun.structures.Kit;
import taupegun.structures.Team;

/**
 * ChatInventory class contains custom items in order to configure the plugin graphically
 * @author LetMeR00t
 *
 */
public class ChatInventory implements Listener
{

	/**
	 * Structure that represents different elements that a player gives when it configure something
	 */
	private static final Map<Player, Map<StateChat, String>> states = new HashMap<Player, Map<StateChat, String>>();
	
	/**
	 * Reference about commands that a op player ask
	 */
	private static final Map<Player, StateChat> last_state = new HashMap<Player, StateChat>();
	
	/**
	 * Reference to the plugin Object
	 */
	private TaupeGunPlugin plugin = null;
	
	
	/**
	 * Default constructor
	 * @param plugin	TaupeGunPlugin Object
	 */
	public ChatInventory(TaupeGunPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void PlayerChat(AsyncPlayerChatEvent ev)
	{
		String message = ev.getMessage();
		Player player = ev.getPlayer();
		
		if (last_state.containsKey(player))
		{
			ev.setCancelled(true);
			
			StateChat etat = (StateChat) last_state.get(player);
			
			if (message.equalsIgnoreCase("cancel"))
			{
				player.sendMessage(ChatColor.RED + "Command cancel!");
				removePlayerStates(ev.getPlayer());
				return;
			}
			
			if (etat.equals(StateChat.CREATE_TEAM_NAME))
			{
				if (plugin.getContext().isTeamAlreadyExists(message))
				{
					ev.getPlayer().sendMessage(ChatColor.DARK_RED + "Team already exists");
					removePlayerStates(player);
					return;
				}
				if (message.length() > 16){
					ev.getPlayer().sendMessage(ChatColor.DARK_RED + "Team name can't be longer than 16 characters");
					return;
				}

				Team team = plugin.getContext().addTeam(message);
				// Add the score board team
				org.bukkit.scoreboard.Team scoreboardTeam = plugin.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam(message);
				team.setScoreboardTeam(scoreboardTeam);
				
				// Additional things
				scoreboardTeam.setPrefix(team.getColor()+"");
				scoreboardTeam.setSuffix(ChatColor.RESET+"");
				
				player.sendMessage(ChatColor.GRAY + "Team " + team.getColor() + team.getName() + ChatColor.GRAY + " has been created");
				removePlayerStates(player);
				
			}
		
			if (etat.equals(StateChat.RENAME_TEAM))
			{
				Team team = plugin.getContext().getTeam(getStateChatValue(player, StateChat.TEAM_NAME));
				
				if (team == null)
				{
					ev.getPlayer().sendMessage(ChatColor.DARK_RED + "Invalid team");
					removePlayerStates(player);
					return;
				}
				
				if (message.length() > 16){
					ev.getPlayer().sendMessage(ChatColor.DARK_RED + "Team name can't be longer than 16 characters");
					return;
				}
				
				player.sendMessage(ChatColor.GRAY + "Team " + team.getColor() + team.getName() + ChatColor.GRAY + " is now called " + team.getColor() + message + ChatColor.GRAY + ".");
				
				String oldName = team.getName();
				
				team.setName(message);
				
				org.bukkit.scoreboard.Team newScoreboardTeam = plugin.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam(message);
				
				for(Player play : team.getPlayers())
				{
					team.getScoreboardTeam().removeEntry(play.getName());
					play.setPlayerListName(team.getColor()+" "+play.getDisplayName());
					newScoreboardTeam.addEntry(play.getName());
				}
				
				team.getScoreboardTeam().unregister();
				team.setScoreboardTeam(newScoreboardTeam);
				
				plugin.getContext().getTeams().remove(oldName);
				plugin.getContext().getTeams().put(team.getName(),team);
				
				removePlayerStates(player);
				return;
			}
			
			if (etat.equals(StateChat.CREATE_KIT_NAME))
			{
				if (plugin.getContext().isKitAlreadyExists(message))
				{
					ev.getPlayer().sendMessage(ChatColor.DARK_RED + "Kit already exists");
					removePlayerStates(player);
					return;
				}
				
				Kit kit = plugin.getContext().addKit(message);

				player.sendMessage(ChatColor.GRAY + "Kit " + ChatColor.GOLD + kit.getName() + ChatColor.GRAY + " is created");
				
				removePlayerStates(player);
				return;
			}
			
			if (etat.equals(StateChat.RENAME_KIT))
			{
				Kit kit = null;
				Iterator<Kit> it = plugin.getContext().getKits().iterator();
				
				while (it.hasNext() && kit == null){
					Kit tmpKit = it.next();
					if (tmpKit.getName().equals(getStateChatValue(player, StateChat.KIT_NAME))){
						kit = tmpKit;
					}
				}
				
				if (kit == null)
				{
					ev.getPlayer().sendMessage(ChatColor.DARK_RED + "Invalid Kit");
					removePlayerStates(player);
					return;
				}
				
				player.sendMessage(ChatColor.GRAY + "Kit " + ChatColor.RED + kit.getName() + ChatColor.GRAY + " is now called " +  ChatColor.RED + message + ChatColor.GRAY + ".");
				
				kit.setName(message);
				
				removePlayerStates(player);
				return;
			}
		}
	}
	
	/**
	 * Add a value in a history structure
	 * @param p	Player that wants to add the value
	 * @param etat	State of the Chat
	 * @param value	value to store
	 */
	public void addValueState(Player p, StateChat etat, String value)
	{
		Map<StateChat, String> map;
		
		if (!states.containsKey(p))
		{
			map = new HashMap<StateChat, String>();
		}
		else
		{
			map = states.get(p);
		}
		
		map.put(etat, value);
		
		states.put(p, map);
	}
	
	/**
	 * Recover a stored value
	 * @param p	Player who wants to get the value
	 * @param etat	State of the chat associated
	 * @return
	 */
	public String getStateChatValue(Player p, StateChat etat)
	{
		return states.get(p).get(etat);
	}
	
	/**
	 * Change the last state
	 * @param p	Player who changes his state
	 * @param etat	new state
	 */
	public void changeLastState(Player p, StateChat etat)
	{
		last_state.put(p, etat);
	}
	
	/**
	 * Remove a configuration asked by the player
	 * @param p	Player according to the configuration
	 */
	public void removePlayerStates(Player p)
	{
		if (isInChatInventory(p))
		{
			states.remove(p);
			last_state.remove(p);
		}
	}
	
	/**
	 * Check if a player has already ask something
	 */
	public static boolean isInChatInventory(Player p)
	{
		return last_state.containsKey(p);
	}
	
	/**
	 * Get the states
	 * @return	Structures to the states
	 */
	public static Map<Player, Map<StateChat, String>> getEtats(){
		return states;
	}
	
	/**
	 * Get the last state
	 * @return	Structure according to the last state
	 */
	public static Map<Player, StateChat> getLastState(){
		return last_state;
	}
}

enum StateChat
{
	NULL,
	CREATE_TEAM_NAME,
	ADD_PLAYER,
	TEAM_NAME,
	RENAME_TEAM,
	CREATE_KIT_NAME,
	RENAME_KIT,
	KIT_NAME;
}

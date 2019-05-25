package taupegun.structures;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;


/**
 * This class defines the structure for a team
 * @author LetMeR00t
 *
 */
public class Team {

	/**
	 * Name of the team
	 */
	private String name = null;
	
	/**
	 * List of players
	 */
	private ArrayList<Player> players = null;
	
	/**
	 * Color for the team
	 */
	private ChatColor color = null;
	
	/**
	 * Spawing location of the team in the world
	 */
	private Location spawningLocation = null;
	
	/**
	 * Team score board object
	 */
	private org.bukkit.scoreboard.Team scoreboardTeam = null;
	
	/**
	 * Public constructor that instantiate a team
	 * @param newName	Name of the team
	 * @param newColor	Color of the team
	 * @param newLocation	Starting location of the team
	 * @param newScoreboardTeam	Score board team object associated
	 */
	public Team(String newName, ChatColor newColor, Location newLocation, org.bukkit.scoreboard.Team newScoreboardTeam){
		
		this.name = newName;
		this.players = new ArrayList<Player>();
		this.color = newColor;
		this.spawningLocation = newLocation;
		this.scoreboardTeam = newScoreboardTeam;
		
	}
	
	/**
	 * Get the name of the team
	 * @return	name of the team
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name for the team
	 * @param name	name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the list of players
	 * @return	the list of Player objects
	 */
	public ArrayList<Player> getPlayers() {
		return players;
	}

	/**
	 * Add a player to the team
	 * @param player	Player object to add
	 */
	public void addPlayer(Player player){
		this.scoreboardTeam.addEntry(player.getName());
		player.setPlayerListName(this.color+" "+player.getDisplayName());
		players.add(player);
	}

	/**
	 * Remove a player from the team
	 * @param player	Player object to remove
	 */
	public void removePlayer(Player player){
		this.scoreboardTeam.removeEntry(player.getName());
		player.setPlayerListName(player.getDisplayName());
		players.remove(player);
	}
	
	/**
	 * Count the number of players in the team
	 * @return	the number of players in the team
	 */
	public int countPlayer(){
		return players.size();
	}

	/***
	 * Get the color of the team
	 * @return	the color of the team
	 */
	public ChatColor getColor() {
		return color;
	}

	/**
	 * Set the color for the team
	 * @param color	new color for the team
	 */
	public void setColor(ChatColor color) {
		this.color = color;
	}
	
	/**
	 * Get the current spawning location for this team
	 * @return	Location object that contains location informations
	 */
	public Location getSpawningLocation(){
		return spawningLocation;
	}
	
	/**
	 * Set the current spawning location for this team
	 * @param loc	Location object that contains location informations
	 */
	public void setSpawningLocation(Location loc){
		this.spawningLocation = loc;
	}

	/**
	 * Get the score board object of the team
	 * @return	score board Team object associated to the team
	 */
	public org.bukkit.scoreboard.Team getScoreboardTeam() {
		return scoreboardTeam;
	}
	
	/**
	 * Set a scoreboard team object for this team
	 * @param scoreboardTeam	the scoreboard team object
	 */
	public void setScoreboardTeam(org.bukkit.scoreboard.Team scoreboardTeam){
		this.scoreboardTeam = scoreboardTeam;
	}




	
	
}
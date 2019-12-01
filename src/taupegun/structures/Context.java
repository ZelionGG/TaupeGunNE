package taupegun.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import taupegun.start.TaupeGunPlugin;

/**
 * Singleton Class - Only one instance
 * Context class contains all elements needed for a Taupe Gun session.
 */
public class Context {

	/**
	 * Object referencing the plugin
	 */
	private TaupeGunPlugin plugin = null;
	
	/**
	 * HashMap of teams with teamName -> Team
	 */
	private HashMap<String,Team> teams = null;
	
	/**
	 * List of moles teams
	 */
	private ArrayList<Team> molesTeams = null;
	
	/**
	 * List of moles teams
	 */
	private HashMap<Team, List<Player>> preMolesTeams = null;
	
	/**
	 * List of players (all), only modify when join or exit the server
	 */
	private ArrayList<Player> players = null;
	
	/**
	 * List of moles
	 */
	private ArrayList<Player> moles = null;
	
	/**
	 * HashMap to identify each Player to a team
	 */
	private HashMap<Player,Team> playersTeams = null;
	
	/**
	 * List of Kits
	 */
	private ArrayList<Kit> kits = null;
	
	/**
	 * List of moles that don't give their kit
	 */
	private ArrayList<Player> molesWaitingKit = null;
	
	/**
	 * List of available colors
	 */
	private ArrayList<ChatColor> availableColors = null;
	
	/**
	 * Random object
	 */
	private Random random = new Random();
	
	/**
	 * Time information
	 */
	private int minutesLeft = 0;
	private int secondsLeft = 0;
	private int minutesMolesLeft = 0;
	
	/**
	 * Episodes information
	 */
	private int episode = 0;
	
	/**
	 * Game status
	 */
	private boolean hasStarted = false;
	private boolean molesActivated = false;
	private boolean titleManagerPluginEnabled = false;
	private boolean invincible = true;
	private GAMEMODE_MOLE gameModeMoles = GAMEMODE_MOLE.CLASSIC;
	
	/**
	 * Quantities information
	 */
	private int molesPerTeam = 0;
	private int numberTeamMoles = 0;
	private int molesPerMolesTeam = 0;
	
	/**
	 * Singleton object representing the context
	 */
	private static Context context = null;
	
	private Context(TaupeGunPlugin newPlugin){
		
		this.plugin = newPlugin;
		this.teams = new HashMap<String,Team>();
		this.molesTeams = new ArrayList<Team>();
		this.players = new ArrayList<Player>();
		this.playersTeams = new HashMap<Player,Team>();
		this.kits = new ArrayList<Kit>();
		this.molesWaitingKit = new ArrayList<Player>();
		this.moles = new ArrayList<Player>();
		this.preMolesTeams = new HashMap<>();
		
		// Initialize available colors
		availableColors = new ArrayList<ChatColor>();
		
		// Add manually available colors
		availableColors.add(ChatColor.GRAY);
		availableColors.add(ChatColor.AQUA);
		availableColors.add(ChatColor.GREEN);
		availableColors.add(ChatColor.YELLOW);
		availableColors.add(ChatColor.RED);
		availableColors.add(ChatColor.GOLD);
		availableColors.add(ChatColor.DARK_PURPLE);
		availableColors.add(ChatColor.DARK_BLUE);
		
	}
	
	/**
	 * Get the current Context, this function has to be called at least once to initialize the context
	 * @param plugin	TaupeGunPlugin object associated
	 * @return	the Context object that contains most of information
	 */
	public static Context getContext(TaupeGunPlugin plugin){
		
		if (context == null){
			context = new Context(plugin);
		}
		
		return context;
	}
	
	/**
	 * Get the current Context without specifying the plugin structure
	 * @return	the Context object that contains most of information
	 */
	public Context getContext(){
		
		return context;
		
	}
	
	/* TEAMS AND PLAYERS FUNCTIONS */
	
	
	/**
	 * Add a new team
	 * @param teamName	Name of the team
	 * @return	the new instance of Team object
	 */
	public Team addTeam(String teamName){
		
		Team team = new Team(teamName,randomColor(),randomLocation(),null);
		
		teams.put(teamName, team);
		
		plugin.MatchInfo();
		
		return team;
		
	}
	
	/**
	 * Add a new team of moles
	 * @param teamName	Name of the team
	 */
	public void addMolesTeam(String molesTeamName){
		
		// Add a classic team
		Team team =  addTeam(molesTeamName);
		
		// Team will be added to scoreboard when the first mole will be revealed
		
		// But we notice that it's a team of moles
		molesTeams.add(team);
		
	}
	
	/**
	 * Add a new mole
	 * @param player	The player that will become a mole
	 */
	public void addMole(Player player){
		
		moles.add(player);
		molesWaitingKit.add(player);
		
	}
	
	/**
	 * Remove a mole (used when a player died before the moles time
	 * @param player	The player to remove
	 */
	public void removeMole(Player player){
		
		if (moles.contains(player)){
			moles.remove(player);
		}
		
		if (molesWaitingKit.contains(player)){
		molesWaitingKit.remove(player);
		}
		
	}
	
	/**
	 * Add a player to a team
	 * @param player	Player to add
	 * @param team	Team that will have the new player
	 */
	public void addPlayerToATeam(Player player, Team team){
	
		team.addPlayer(player);
		playersTeams.put(player, team);
		team.getScoreboardTeam().addEntry(player.getDisplayName());
		plugin.MatchInfo();
	}
	
	/**
	 * Check is a player is already in a team
	 * @param player	The player to check
	 * @return	Yes if so, else false
	 */
	public boolean isAlreadyInATeam(Player player){

		boolean check = false;
		
		Iterator<Player> it = playersTeams.keySet().iterator();
		
		while (it.hasNext() && check == false){
			
			Player play = it.next();
			
			if (play.getName().equalsIgnoreCase(player.getName())){
				check = true;
			}
			
		}
		
		return check;
	}
	
	/**
	 * Update the Player object when a player is reconnecting
	 * @param player	new Player structure
	 */
	public void updatePlayer(Player newPlayer){
		
		Player oldPlayer = getPlayerInTeamsByName(newPlayer.getName());
		
		if (oldPlayer != null){
			
			Inventory iv = oldPlayer.getInventory();
			newPlayer.getInventory().setContents(iv.getContents());
			
			// Update all structures
			Team team = getTeamOfPlayer(oldPlayer);
			
			if (team != null){
				if (team.getPlayers().contains(oldPlayer)){
					team.removePlayer(oldPlayer);
				}
				team.addPlayer(newPlayer);
			}
			
			if (playersTeams.containsKey(oldPlayer)){
				playersTeams.remove(oldPlayer);
			}
			playersTeams.put(newPlayer, team);
			
			players.remove(oldPlayer);
			
		}
		
		players.add(newPlayer);
		
	}
	
	/**
	 * Recover a Player structure using his name
	 * @param playerName	the name of the player
	 * @return	the current Player structure in "players"
	 */
	public Player getPlayerInTeamsByName(String playerName){
		
		Player player = null;
		
		Iterator<Player> it = players.iterator();
		
		while (it.hasNext() && player == null){
			
			Player tmpPlayer = it.next();
			
			if (tmpPlayer.getName().equalsIgnoreCase(playerName)){
				player = tmpPlayer;
			}
		}
		
		return player;
		
	}
	
	/**
	 * Add a new kit
	 * @param kitName	Name of the kit
	 * @return	the new instance of Kit object
	 */
	public Kit addKit(String kitName){
				
		Inventory inventory = plugin.getServer().createInventory(null, 27, ChatColor.DARK_BLUE + "- Kit "+kitName+" -");
		
		Kit kit = new Kit(kitName, inventory);
		
		kits.add(kit);
		
		return kit;
		
	}
	
	/**
	 * Change the team of a player
	 * @param player	Player concerned by the change
	 * @param team	Team that will have the new player
	 */
	public void changeTeamPlayer(Player player, Team team){
		
		removePlayerFromATeam(player);
		
		addPlayerToATeam(player,team);
		
	}
	
	/**
	 * Reveal a mole and change his team
	 * @param player	the player concerned
	 * @param moleTeam	the new team of moles that the player will have
	 */
	public void changeRevealedMole(Player player){
		
		removePlayerFromATeam(player);
		
		Team player_team_new = null ;
		for(Map.Entry<Team, List<Player>> entry : preMolesTeams.entrySet())
		{
			Team moles_team = entry.getKey();
			List<Player> value = entry.getValue();
			for(Player p : value)
			{
				if(p == player) {
					player_team_new = moles_team;
				}
			}
		}
			
		Team team = player_team_new;
		
		if (team.countPlayer() == 0){
			// Add the score board team
			org.bukkit.scoreboard.Team scoreboardTeam = plugin.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam(team.getName());
			team.setScoreboardTeam(scoreboardTeam);
			
			// Additional things
			scoreboardTeam.setPrefix(team.getColor()+"");
			scoreboardTeam.setSuffix(ChatColor.RESET+"");
			
		}
		
		addPlayerToATeam(player,team);

	}
	
	/**
	 * Get a team with his name
	 * @param teamName	name of the team
	 * @return	Team instance according to the given name
	 */
	public Team getTeam(String teamName){
		return teams.get(ChatColor.stripColor(teamName));
	}
	
	/**
	 * Get a team with his name
	 * @param teamName	name of the team
	 * @return	Team instance according to the given name
	 */
	public ArrayList<Team> getMolesTeam(){
		return molesTeams;
	}
	
	/**
	 * Get the structure that links a player to a team
	 * @return	the HashMap structure associated
	 */
	public HashMap<Player,Team> getPlayersTeams(){
		return playersTeams;
	}
	
	/**
	 * Get a git with his name
	 * @param kitName	name of the kit
	 * @return	Kit instance according to the given name
	 */
	public Kit getKit(String kitName){
		Kit kit = null;
		
		Iterator<Kit> it = kits.iterator();
		
		while (it.hasNext() && kit == null){
			Kit tmpKit = it.next();
			
			if (tmpKit.getName().equalsIgnoreCase(ChatColor.stripColor(kitName))){
				kit = tmpKit;
			}
		}
		
		return kit;
	}
	
	/**
	 * Change the color of the Team (random)
	 * @param teamName	Name of the team
	 */
	public void changeTeamColor(Team team){
		
		ChatColor color = randomColor();
		
		availableColors.add(team.getColor());
		
		team.setColor(color);
	
		availableColors.remove(team.getColor());
		
		team.getScoreboardTeam().setPrefix(team.getColor()+"");
		team.getScoreboardTeam().setSuffix(ChatColor.RESET+"");
		
		for (Player player: team.getPlayers()){
			player.setPlayerListName(team.getColor()+" "+player.getDisplayName());
		}
		
	}
	
	/**
	 * Recover the team of a player
	 * @param player	Player to search
	 * @return	the Team instance of the player
	 */
	public Team getTeamOfPlayer(Player player){
		return playersTeams.get(player);
	}
	
	/**
	 * Remove a player from a team
	 * @param player	Player to remove
	 */
	public void removePlayerFromATeam(Player player){

		Team team = playersTeams.get(player);
		team.removePlayer(player);
		
		playersTeams.remove(player);
		
		if (team.getPlayers().size() == 0){
			teams.remove(team.getName());
		}
		
		plugin.MatchInfo();
		
	}
	
	/**
	 * Remove a team
	 * @param teamName	the name of the team to remove
	 */
	public void removeTeam(Team team){
		
		ArrayList<Player> playersToRemoveFromTeam = null;
		
		teams.remove(team.getName(), team);
		
		playersToRemoveFromTeam = team.getPlayers();
		
		for (Player player: playersToRemoveFromTeam){
			team.removePlayer(player);
		}
		
		availableColors.add(team.getColor());
		
		team.getScoreboardTeam().unregister();
		plugin.MatchInfo();
	}
	
	/**
	 * Remove a kit
	 * @param kitName	The name of the kit to remove
	 */
	public void removeKit(Kit kit){
		
		kits.remove(kit);
		
	}
	
	/**
	 * Generate a random position according to the map size
	 * @return	a Location Object
	 */
	private Location randomLocation()
	{
		int size = plugin.getConfig().getInt("map.startSize");
		
		double rx = random.nextInt(size*2);
		double rz = random.nextInt(size*2);
		
		rx -= size;
		rz -= size;
		
		Location loc = new Location(plugin.getServer().getWorlds().get(0), rx, 200, rz);
		
		if ((-20 < rx && rx < 20) || (-20 < rz && rz < 20)) loc = randomLocation();
		
		return loc;
	}
	
	/**
	 * Generate a random color according to the available colors
	 * @return	A random color
	 */
	private ChatColor randomColor()
	{
		ChatColor color = null;
		
		int rand = random.nextInt(availableColors.size());
		
		color = availableColors.get(rand);
		
		// Remove color from available colors
		availableColors.remove(color);
		
		return color;
	}
	
	/**
	 * Get the HashMap of teams
	 * @return	HashMap of teams
	 */
	public HashMap<String,Team> getTeams(){
		return teams;
	}
	
	/**
	 * Get ArrayList of moles
	 * @return	ArrayList of moles
	 */
	public ArrayList<Player> getMoles(){
		return moles;
	}
	
	/**
	 * Get the list of kits
	 * @return	ArrayList of kits
	 */
	public ArrayList<Kit> getKits(){
		return kits;
	}
	
	/**
	 * Count the number of players (all)
	 * @return the number of players (total)
	 */
	public int countAllPlayers(){
		int count = 0;
		
		Iterator<Team> it = teams.values().iterator();
		
		while (it.hasNext()){
			
			count += it.next().countPlayer();
			
		}
		
		return count;
	}
	
	/**
	 * Count the number of teams (all)
	 * @return the number of teams (total)
	 */
	public int countAllTeams(){		
		return teams.size();
	}
	
	/**
	 * Check if a player is a mole
	 * @param player	Player to check
	 * @return	Yes if so, else false
	 */
	public boolean isMole(Player player){
		return context.getMoles().contains(player);
	}
	
	/**
	 * Get the list of moles who didn't claimed a kit
	 * @return	the list of moles
	 */
	public ArrayList<Player> getMolesWaitingKit(){
		return molesWaitingKit;
	}
	
	/**
	 * Give a kit to the player
	 * @param player	Player that will have the kit
	 */
	public void giveKit(Player player){

		int rand = random.nextInt(kits.size());
		
		Kit kit = kits.get(rand);
		
		ItemStack[] items = kit.getInventory().getContents();

		for(ItemStack item : items)
		{
			if(item != null && !item.getType().equals(Material.AIR)){
				
				player.getWorld().dropItemNaturally(player.getLocation(), item);
			
			}
		}
		
		molesWaitingKit.remove(player);
		
	}
	
	/**
	 * Get player who is online by name
	 * @param name	name of the player
	 * @return	the player structure associated
	 */
	public Player getPlayerOnlineByName(String name){
		
		Player player = null;
		
		Iterator<? extends Player> it = plugin.getServer().getOnlinePlayers().iterator();
		
		while (player == null && it.hasNext()){
			Player tmpPlayer = it.next();
			
			if (tmpPlayer.getName().equalsIgnoreCase(name)){
				player = tmpPlayer;
			}
			
		}
				
		return player;
	}
	
	/**
	 * Get the list of all players
	 * @return	the ArrayList with all players
	 */
	public ArrayList<Player> getAllPlayers(){
		return this.players;
	}
	
	/**
	 * Check if a given team name already exists
	 * @param teamName	Name of the team
	 * @return	True if so, else false
	 */
	public boolean isTeamAlreadyExists(String teamName){
		return teams.containsKey(teamName);
	}
	
	public boolean isKitAlreadyExists(String kitName){
		boolean check = false;
		
		Iterator<Kit> it = kits.iterator();
		
		while (it.hasNext() && check == false){
			if (it.next().getName().equals(kitName)){
				check = true;
			}
		}
		
		return check;
	}
	
	
	/* TIME FUNCTIONS */
	
	/**
	 * Get minutes left
	 * @return value of minutes left
	 */
	public int getMinutesLeft(){
		return this.minutesLeft;
	}
	
	/**
	 * Get seconds left
	 * @return value of seconds left
	 */
	public int getSecondsLeft(){
		return this.secondsLeft;
	}
	
	/**
	 * Get minutes moles left
	 * @return value of minutes moles left
	 */
	public int getMinutesMolesLeft(){
		return this.minutesMolesLeft;
	}
	
	/**
	 * Set minutes left
	 * @param minutes	minutes left to set
	 */
	public void setMinutesLeft(int minutes){
		this.minutesLeft = minutes;
	}
	
	/**
	 * Set seconds left
	 * @param seconds	seconds left to set
	 */
	public void setSecondsLeft(int seconds){
		this.secondsLeft = seconds;
	}
	
	/**
	 * Set minutes moles left
	 * @param minutes	minutes moles left to set
	 */
	public void setMinutesMolesLeft(int minutes){
		this.minutesMolesLeft = minutes;
	}
	
	/**
	 * Increments minutes left
	 * @return	the new value of minutes left
	 */
	public int incMinutesLeft(){
		return ++this.minutesLeft;
	}
	
	/**
	 * Decrements minutes left
	 * @return	the new value of minutes left
	 */
	public int decMinutesLeft(){
		return --this.minutesLeft;
	}
	
	/**
	 * Increments seconds left
	 * @return	the new value of seconds left
	 */
	public int incSecondsLeft(){
		return ++this.secondsLeft;
	}
	
	/**
	 * Decrements seconds left
	 * @return	the new value of seconds left
	 */
	public int decSecondsLeft(){
		return --this.secondsLeft;
	}
	
	/**
	 * Increments minutes moles left
	 * @return	the new value of minutes moles left
	 */
	public int incMinutesMolesLeft(){
		return ++this.minutesMolesLeft;
	}
	
	/**
	 * Decrements minutes moles left
	 * @return	the new value of minutes moles left
	 */
	public int decMinutesMolesLeft(){
		return --this.minutesMolesLeft;
	}
	
	
	/* EPISODES FUNCTIONS */
	
	/**
	 * Get the episode number
	 * @return value of episode
	 */
	public int getEpisode(){
		return this.episode;
	}
	
	/**
	 * Increments the episode number
	 * @return	the new episode number
	 */
	public int incEpisode(){
		return ++this.episode;
	}
	
	/* GAME STATUS */
	
	/**
	 * Check if moles are activated
	 * @return	a boolean
	 */
	public boolean isMolesActivated(){
		return this.molesActivated;
	}
	
	/**
	 * Check if session has started
	 * @return	a boolean
	 */
	public boolean hasStarted(){
		return this.hasStarted;
	}
	
	/**
	 * Activates moles
	 */
	public void activateMoles(){
		this.molesActivated = true;
	}
	
	/**
	 * Activates game session
	 */
	public void startGame(){
		this.hasStarted = true;
	}

	/**
	 * Check if Title Manager plugin is enabled
	 * @return	True if it is, else false
	 */
	public boolean isTitleManagerEnabled(){
		return this.titleManagerPluginEnabled;
	}
	
	/**
	 * Enable the plugin Title Manager for this plugin
	 */
	public void titleManagerPluginEnabled(){
		this.titleManagerPluginEnabled = true;
	}
	
	/**
	 * Activate damages
	 */
	public void activateDamages(){
		this.invincible = false;
	}
	
	/**
	 * Check if players are invincible
	 * @return	True if it is, else false
	 */
	public boolean isInvincible(){
		return this.invincible;
	}

	/**
	 * Set the game mode mole as extended
	 */
	public void setGameModeExtended(){
		gameModeMoles = GAMEMODE_MOLE.EXTENDED;
	}
	
	/**
	 * Check if game mode is extended
	 * @return	True if it so, else false
	 */
	public boolean isGameModeExtented(){
		if (gameModeMoles.equals(GAMEMODE_MOLE.EXTENDED)){
			return true;
		}
		else{
			return false;
		}
	}
	
	/* QUANTITIES FUNCTIONS */
	
	
	/**
	 * Get the number of moles per team
	 * @return	number of moles per team
	 */
	public int getMolesPerTeam() {
		return molesPerTeam;
	}

	/**
	 * Set the number of moles per team
	 * @param molesPerTeam	the number of moles per team
	 */
	public void setMolesPerTeam(int molesPerTeam) {
		this.molesPerTeam = molesPerTeam;
	}

	/**
	 * Get the number of moles teams
	 * @return	the number of moles teams
	 */
	public int getNumberTeamMoles() {
		return numberTeamMoles;
	}

	/**
	 * Set the number of moles teams
	 * @param numberTeamMoles	the number of moles teams
	 */
	public void setNumberTeamMoles(int numberTeamMoles) {
		this.numberTeamMoles = numberTeamMoles;
	}

	/**
	 * Get the number of moles per moles teams
	 * @return	the number of moles per moles teams
	 */
	public int getMolesPerMolesTeam() {
		return molesPerMolesTeam;
	}

	/**
	 * Set the number of moles per moles teams
	 * @param molesPerMolesTeam	the number of moles per moles teams
	 */
	public void setMolesPerMolesTeam(int molesPerMolesTeam) {
		this.molesPerMolesTeam = molesPerMolesTeam;
	}
	
	/**
	 * Get the random object for the plugin
	 * @return	the Random object
	 */
	public Random getRandom(){
		return this.random;
	}
	
	/**
	 * Set the players' moles team
	 */
	public void setPreMolesTeam() {		
		for(Team t : molesTeams)
		{
			preMolesTeams.put(t, new ArrayList<Player>());
		}
		
		for(Player p : moles) 
		{
			boolean mole_placed = false;
			
			while(mole_placed == false) 
			{
				Random       random    = new Random();
				List<Team> keys      = new ArrayList<Team>(preMolesTeams.keySet());
				Team       randomKey = keys.get( random.nextInt(keys.size()) );
				int value = preMolesTeams.get(randomKey).size();
				
				if(value < molesPerMolesTeam) 
				{
					this.preMolesTeams.get(randomKey).add(p);
					mole_placed = true;
				}
			}
			
			
		}	
	}
	
	public HashMap<Team, List<Player>> getPreMolesTeam() {
		return preMolesTeams;		
	}
	
	
}



/**
 * Classic mod is the classic game with constraints about number of players, extended is a mod where X moles are chosen whatever are their primary team.
 * @author LetMeR00t
 *
 */
enum GAMEMODE_MOLE{
	CLASSIC,
	EXTENDED
}

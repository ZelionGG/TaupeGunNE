package taupegun.start;

import io.puharesource.mc.titlemanager.api.TitleObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import taupegun.exceptions.ListenersException;
import taupegun.listeners.Listeners;
import taupegun.structures.Context;
import taupegun.structures.Kit;
import taupegun.structures.Team;


public class TaupeGunPlugin extends JavaPlugin{
	
	/**
	 * Reference to himself as static way
	 */
	private static TaupeGunPlugin plugin = null;
	
	/**
	 * Reference to a Context object that contains all useful information
	 */
	private Context context = null;
	
	/**
	 * Reference to a FileConfiguration object that contains useful parameters
	 */
	private FileConfiguration config = null;
	
	/**
	 * Name on the score board
	 */
	private String scoreboardTitle = "Taupe Gun : NE";
	
	/**
	 * Called when this plugin is enabled
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 */
	public void onEnable()
	{
		TaupeGunPlugin.plugin = this;
		
		// Initialize the configuration
		this.config = getConfig();
		
		// Initialize the context
		this.context = Context.getContext(this);
		
		// Get the server object
		Server server = this.getServer();
		// Get the Score board
		Scoreboard scoreboard = server.getScoreboardManager().getMainScoreboard();
		
		// Save configuration file to retrieve parameters later
		saveDefaultConfig();
		
		// Initialize listener events
		try {
			Listeners.instanciateListeners(this);
			Listeners.startListeners();
		} catch (ListenersException e1) {
			e1.printStackTrace();
		}
		
		try{
			if (scoreboard != null && scoreboard.getObjective("PlayerHealth") != null){
				scoreboard.getObjective("PlayerHealth").unregister();
			}
		}catch(IllegalStateException e){
			e.printStackTrace();
		}
		
		// Clean teams
		Iterator<org.bukkit.scoreboard.Team> it = scoreboard.getTeams().iterator();
		
		while (it.hasNext()){
			it.next().unregister();
		}
		
		// Initialize score board
		try{
			// Change the score board to show players life
			scoreboard.registerNewObjective("PlayerHealth", "health").setDisplaySlot(DisplaySlot.PLAYER_LIST);
		}catch (IllegalArgumentException e){
			e.printStackTrace();
		}
		
		// Generate the world
		generateWorldConfiguration();
		
		// Check game mode mole chosen
		if (config.getBoolean("moles.gamemode_no_contraint")){
			context.setGameModeExtended();
		}
		
	}
	
	/**
	 * Generate the world configuration
	 */
	private void generateWorldConfiguration(){
		
		World w = ((World)getServer().getWorlds().get(0));
		WorldBorder wb = w.getWorldBorder();
		
		w.setGameRuleValue("doDaylightCycle", config.getString("doDaylightCycle"));
		w.setGameRuleValue("naturalRegeneration", "false");
		((World)getServer().getWorlds().get(1)).setGameRuleValue("naturalRegeneration", "false");
		w.setTime(12000L);
		w.setSpawnLocation(0, 200, 0);
		w.setStorm(false);
		for (World world : plugin.getServer().getWorlds()){
			world.setDifficulty(Difficulty.PEACEFUL);
		}
		wb.setCenter(0,0);
		wb.setSize(getConfig().getDouble("map.startSize")*2);
		wb.setWarningDistance(10);
		wb.setWarningTime(10);
		wb.setDamageAmount(1D);
		wb.setDamageBuffer(10D);
		
		if(!(getConfig().getString("scoreboard").length() > 16))
		{
			scoreboardTitle = getConfig().getString("scoreboard");
		}
		
		MatchInfo();
		createSpawn();
		
	}
	
	public Context getContext(){
		return context;
	}
	
	@SuppressWarnings("deprecation")
	private void createSpawn()
	{
		getServer().broadcastMessage("Creating spawn...");
		try
		{
			World w = getServer().getWorlds().get(0);
			
			for(int ix = -15; ix<=15;ix++)
			{
				for(int iz = -15; iz<=15;iz++)
				{
					Block b = w.getBlockAt(ix, 199, iz);
					byte sid = (byte) new Random().nextInt(16);
					
					Block b3 = w.getBlockAt(ix, 204, iz);
					Block b4 = w.getBlockAt(ix, 205, iz);
					int rg = new Random().nextInt(3);
					
					if(ix == -15 || ix == 15 || iz == -15 || iz == 15)
					{
						b.setType(Material.QUARTZ_BLOCK);
						Block b2 = w.getBlockAt(ix, 200, iz);
						b2.setType(Material.QUARTZ_BLOCK);
						b3.setType(Material.QUARTZ_BLOCK);
						b4.setType(Material.QUARTZ_BLOCK);
					}
					else
					{
						b.setType(Material.STAINED_GLASS);
						b.setData(sid);
						
						if(rg == 0)
						{
							b3.setType(Material.REDSTONE_LAMP_OFF);
							b4.setType(Material.REDSTONE_BLOCK);
						}
						else
						{
							b3.setType(Material.GLASS);
							b4.setType(Material.GLASS);
						}
					}
				}
			}

			for(int iy = 201; iy<=203;iy++)
			{
				for(int ix = -15; ix<=15;ix++)
				{
					Block b1 = w.getBlockAt(ix, iy, -15);
					Block b2 = w.getBlockAt(ix, iy, 15);
					
					b1.setType(Material.THIN_GLASS);
					b2.setType(Material.THIN_GLASS);
				}
				for(int iz = -15; iz<=15;iz++)
				{
					Block b1 = w.getBlockAt(-15, iy, iz);
					Block b2 = w.getBlockAt(15, iy, iz);
					
					b1.setType(Material.THIN_GLASS);
					b2.setType(Material.THIN_GLASS);
				}
			}
			
			getServer().broadcastMessage("Spawn created");
		}
		catch(Exception e)
		{getServer().broadcastMessage("Spawn creation failed");}
	}
	
	/**
	 * Called when a command is detected
	 * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	public boolean onCommand(CommandSender s, Command c, String l, String[] a)
	{
		// Check possibilities for a command line
		
		if (c.getName().equalsIgnoreCase("moles"))
		{
			if(!(s instanceof Player))
			{
				s.sendMessage("Sorry, god can't modify the plugin :-) !");
				return true;
			}
			
			Player p = (Player) s;

			if(!p.isOp())
			{
				s.sendMessage(ChatColor.RED + "Really... ? ;)");
				return true;
			}
			
			if (a.length == 0)
			{
				p.sendMessage(ChatColor.RED + "Usage : /"+ c.getName() +" <team/start/kit>");
				return true;
			}
			
			if (a[0].equalsIgnoreCase("team"))
			{
				// Check if the session has started
				if (!context.hasStarted())
				{
					Inventory iv = getServer().createInventory(null, 54, ChatColor.DARK_BLUE + "- Teams -");
					
					int slot = 0;
					for(Entry<String,Team> entry : context.getTeams().entrySet())
					{
						Team team = entry.getValue();
						
						ItemStack i = new ItemStack(Material.BEACON);
						ItemMeta im = i.getItemMeta();
						im.setDisplayName(team.getColor() + team.getName());
						List<String> lore = new ArrayList<String>();
						
						for(Player player : team.getPlayers())
						{
							lore.add(ChatColor.AQUA + "- " + player.getDisplayName());
						}
						
						im.setLore(lore);
						i.setItemMeta(im);
						
						iv.setItem(slot, i);
						slot++;
					}
					
					ItemStack ct = new ItemStack(Material.DIAMOND);
					ItemMeta ctm = ct.getItemMeta();
					ctm.setDisplayName(ChatColor.AQUA + "Create a team");
					ct.setItemMeta(ctm);
					iv.setItem(53, ct);
					
					p.openInventory(iv);
				}
				
				return true;
			}
			
			if (a[0].equalsIgnoreCase("kit"))
			{
				if (!context.hasStarted())
				{
					Inventory iv = getServer().createInventory(null, 27, ChatColor.DARK_BLUE + "- Kits -");
					
					int slot = 0;
					for(Kit kit : context.getKits())
					{						
						ItemStack i = new ItemStack(Material.CHEST);
						ItemMeta im = i.getItemMeta();
						im.setDisplayName(ChatColor.GOLD + kit.getName());
						i.setItemMeta(im);
						
						iv.setItem(slot, i);
						slot++;
					}
					
					ItemStack ct = new ItemStack(Material.DIAMOND);
					ItemMeta ctm = ct.getItemMeta();
					ctm.setDisplayName(ChatColor.AQUA + "Create a kit");
					ct.setItemMeta(ctm);
					iv.setItem(26, ct);
					
					p.openInventory(iv);
				}
				
				return true;
			}
			
			if (a[0].equalsIgnoreCase("start"))
			{
				if (!context.hasStarted())
				{
					if (context.getTeams().size() == 0)
					{
						p.sendMessage(ChatColor.DARK_RED + "There is no team");
						return true;
					}
					
					int numberOfMolesPerMolesTeam = -1;
					
					if (!context.isGameModeExtented()){
					
						numberOfMolesPerMolesTeam = isMolesTeamsPerfect((Player) s);
						
						if(numberOfMolesPerMolesTeam == -1)
						{
							s.sendMessage(ChatColor.DARK_RED + "Moles teams are not perfect, check the above error you received, else :");
							s.sendMessage(ChatColor.RED + "Check this: ");
							s.sendMessage(ChatColor.RED + "x->Number of teams");
							s.sendMessage(ChatColor.RED + "y->Number of moles chosen by team");
							s.sendMessage(ChatColor.RED + "a->Number of moles teams");
							s.sendMessage(ChatColor.RED + "b->Number of moles per moles team (or) number of kits");
							s.sendMessage(ChatColor.RED + "xy=ab");
							
							return true;
						}
						
						if(context.getKits().size() != 0 && context.getKits().size() != numberOfMolesPerMolesTeam)
						{
							s.sendMessage(ChatColor.DARK_RED + "There is a problem with kits :");
							s.sendMessage(ChatColor.RED + "There is "+context.getKits().size()+" kits but "+numberOfMolesPerMolesTeam+" moles per moles team");
							
							return true;
						}
						
					}
					else{
						
						numberOfMolesPerMolesTeam = config.getInt("moles.option_extended_gamemode.number_moles");
						
						// Check number of players
						if (context.getAllPlayers().size() <= numberOfMolesPerMolesTeam){
							s.sendMessage(ChatColor.DARK_RED + "There is a problem with the number of players :");
							s.sendMessage(ChatColor.RED + "There is "+numberOfMolesPerMolesTeam+" moles but only "+context.getAllPlayers().size()+" players");
							
							return true;
						}
						
						
						// Check number of kits
						if(context.getKits().size() != 0 && context.getKits().size() != numberOfMolesPerMolesTeam)
						{
							s.sendMessage(ChatColor.DARK_RED + "There is a problem with kits :");
							s.sendMessage(ChatColor.RED + "There is "+context.getKits().size()+" kits but "+numberOfMolesPerMolesTeam+" moles (extended game mode), game needs one kit per mole");
							
							return true;
						}
					}
					
					// START THE GAME
					
					// Set moles context
					context.setMolesPerTeam(config.getInt("moles.option_classic_gamemode.moles_per_team"));
					context.setNumberTeamMoles(config.getInt("moles.number_team_moles"));
					context.setMolesPerMolesTeam(numberOfMolesPerMolesTeam);
					
					
					
					// Set configuration values
					context.setMinutesLeft(config.getInt("episodes.time"));
					context.incEpisode();
					context.startGame();
					context.setMinutesMolesLeft(config.getInt("moles.timer"));
					
					getServer().broadcastMessage(ChatColor.GREEN + "La partie démarre...");
					
					// Disable some previous events
					PlayerMoveEvent.getHandlerList().unregister(this);
					/* TODO */
					
					
					//GO GO GO
					notifyPlayersOnBroadcast("3",ChatColor.GREEN,true);
					
					
					Bukkit.getScheduler().runTaskLater(this, new Runnable()
					{
						public void run()
						{
							notifyPlayersOnBroadcast("2",ChatColor.GREEN,true);
						}
						
					}, 20L);
					
					Bukkit.getScheduler().runTaskLater(this, new Runnable()
					{
						public void run()
						{
							notifyPlayersOnBroadcast("1",ChatColor.GREEN,true);
						}
						
					}, 40L);
					
					Bukkit.getScheduler().runTaskLater(this, new Runnable()
					{
						public void run()
						{
							notifyPlayersOnBroadcast("Go",ChatColor.GREEN,true);
							
							new Timer(TaupeGunPlugin.getPlugin()).runTaskTimer(TaupeGunPlugin.getPlugin(), 20L, 20L);
							
							for (World w : plugin.getServer().getWorlds()){
								w.setDifficulty(Difficulty.HARD);
							}
							
							for (Entry<String,Team> entry : TaupeGunPlugin.getPlugin().getContext().getTeams().entrySet())
							{
								Team team = entry.getValue();
								
								if (team.countPlayer() != 0){
									team.getScoreboardTeam().setAllowFriendlyFire(true);
									for (Player player : team.getPlayers())
									{		
										// Teleport and configure players
										player.teleport(team.getSpawningLocation());
										player.setGameMode(GameMode.SURVIVAL);
										player.setExp(0F);
										player.setLevel(0);
										player.getInventory().clear();
										player.getInventory().setArmorContents(new ItemStack[] { new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR) });
										player.setHealth(20D);
										player.setExhaustion(20F);
										player.setFoodLevel(20);
										player.getActivePotionEffects().clear();
										player.setCompassTarget(team.getSpawningLocation());
										
										player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20, 255));
									}
								}
							}
						}
						
					}, 60L);
					

					Bukkit.getScheduler().runTaskLater(this, new Runnable()
					{
						public void run()
						{
							TaupeGunPlugin.getPlugin().getContext().activateDamages();
							TaupeGunPlugin.getPlugin().getServer().broadcastMessage(ChatColor.GOLD+"Vous n'êtes plus invincible");
						}
						
					}, 660L);
				}
				
				return true;
			}
		}
		
		if (c.getName().equalsIgnoreCase("resurrect"))
		{
			if (a.length == 2){
				Player player = plugin.getContext().getPlayerOnlineByName(a[0]);
				Team team = plugin.getContext().getTeam(a[1]);
				
				if (player != null){
					
					if (team != null){
					
						// We add the player
						plugin.getContext().addPlayerToATeam(player, team);
							
						plugin.getContext().getAllPlayers().add(player);
						
						// Change gamemode and teleport player
						player.setGameMode(GameMode.SURVIVAL);
						
						player.teleport(team.getPlayers().get(0).getLocation());
						
						plugin.getServer().broadcastMessage(player.getName()+" came back to life and has joined the team "+team.getColor()+team.getName());
					
					}
					else{
						s.sendMessage(ChatColor.DARK_RED+"Team "+a[1]+" doesn't exist");
					}
				}
				else{
					s.sendMessage(ChatColor.DARK_RED+"Player "+a[0]+" doesn't exist");
				}
			}
			else{
				s.sendMessage(ChatColor.DARK_RED+"Usage : /resurrect playerName teamName");
			}
			
			return true;
		}
		
		// Check if mole otherwise
		if (s instanceof Player && context.hasStarted() && context.isMolesActivated() && context.isMole((Player) s)){
		
			if (c.getName().equalsIgnoreCase("reveal"))
			{
				notifyPlayersOnBroadcast(s.getName()+" se révèle être une taupe !", ChatColor.RED, false);
				
				for(Player p : plugin.getContext().getAllPlayers())
				{
					p.playSound(p.getLocation(), Sound.GHAST_SCREAM, 1.0F, 1.0F);
			
				}
				
				context.changeRevealedMole((Player) s);
			
				((Player) s).getWorld().dropItemNaturally(((Player) s).getLocation(), new ItemStack(Material.GOLDEN_APPLE));

				return true;
			}
			
			if (c.getName().equalsIgnoreCase("t"))
			{				
				
					HashMap<Team, List<Player>> preMolesTeams = context.getPreMolesTeam();
					
					for(Map.Entry<Team, List<Player>> entry : preMolesTeams.entrySet())
					{
						//Team team = entry.getKey();
						List<Player> value = entry.getValue();
						if(value.contains(s)) {
						for(Player p : value)
							{
								p.sendMessage(ChatColor.DARK_RED+""+ChatColor.ITALIC + "[Taupe] "+ChatColor.RESET+ChatColor.RED+" <"+s.getName()+"> " + ChatColor.RESET + moleMessage(a));
							}
						}
					}
				
				return true;
			}
			
			if (c.getName().equalsIgnoreCase("claim"))
			{
				if (context.getKits().size() != 0 && context.getMolesWaitingKit().contains((Player) s))
				{
					context.giveKit((Player) s);
				}
				
				return true;
			}
			
		}
		
		return false;
	}
	
	/**
	 * Set the match information
	 */
	public void MatchInfo()
	{
		Scoreboard scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();
		
		
		try{
			if (scoreboard != null && scoreboard.getObjective(scoreboardTitle) != null){
				scoreboard.getObjective(scoreboardTitle).unregister();
			}
		}catch(IllegalStateException e){
			e.printStackTrace();
		}
		

		// ?
		Objective ob = scoreboard.registerNewObjective(scoreboardTitle, "TaupeGunObjective");
		ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		String ml = displayTime(context.getMinutesLeft());
		String sl = displayTime(context.getSecondsLeft());
		
		String txt = "Episode ";
		
		ob.getScore(ChatColor.GRAY + txt + ChatColor.WHITE + context.getEpisode()).setScore(-1);
		ob.getScore(""+(context.countAllPlayers()+" "+ChatColor.GRAY + "Joueurs")).setScore(-2);
		ob.getScore(""+(scoreboard.getTeams().size()+""+ChatColor.GRAY + " Equipes")).setScore(-3);
		ob.getScore("").setScore(-4);
		ob.getScore(""+(ml + ChatColor.GRAY + ":" + ChatColor.WHITE + sl)).setScore(-5);
		
		if (!context.isMolesActivated())
		{
			String tml = displayTime(context.getMinutesMolesLeft());
			
			ob.getScore(" ").setScore(-6);
			ob.getScore(ChatColor.RED + "Taupes" + ChatColor.GRAY + " dans :").setScore(-7);
			ob.getScore(""+(tml + ChatColor.GRAY + ":" + ChatColor.WHITE + sl) + " ").setScore(-8);
		}
		
	}
	
	/**
	 * Get the TaupeGunPlugin using a static way
	 * @return	the TaupeGunPlugin object
	 */
	public static TaupeGunPlugin getPlugin(){
		return TaupeGunPlugin.plugin;
	}
	
	/**
	 * Return the given number in two digits (so if the number is lower than 10, we concat a 0
	 * @param n	the number
	 * @return	the formated number
	 */
	private String displayTime(int n)
	{
		String txt = "" + n;
		
		if(n>=0 && n<=9)txt = "0"+n;
		
		return txt;
	}
	
	/**
	 * Check if the configuration for moles teams is perfect
	 * @param p	The player who ask for it, it's only to send a message to him if there is a problem
	 * @return	The number of moles per team if moles teams are perfect, else -1
	 */
	private int isMolesTeamsPerfect(Player p)
	{
		int molesPerMolesTeamResult = -1;
		
		int teamsSize = context.getTeams().size();
		int molesPerTeam = config.getInt("moles.option_classic_gamemode.moles_per_team");
		int numberTeamMoles = config.getInt("moles.number_team_moles");
		double molesPerMolesTeam = (teamsSize*molesPerTeam)/(double)numberTeamMoles;
		
		int sameNumber = sameNumberPlayersInEachTeam();
		
		if (sameNumber == -1){
			p.sendMessage(ChatColor.RED+" The number of players in each team is not the same");
		}
		else {
			
			if(sameNumber <= molesPerTeam){
				p.sendMessage(ChatColor.RED+" There is less (or equal) players per team ("+sameNumber+") than moles per team ("+molesPerTeam+")");
			}
			else if (teamsSize < numberTeamMoles){
				p.sendMessage(ChatColor.RED+" There is less teams ("+teamsSize+") than teams of moles ("+numberTeamMoles+")");
			}
			else{
				molesPerMolesTeamResult = new Double(molesPerMolesTeam).intValue();
				p.sendMessage(ChatColor.GREEN+" Number of moles per moles team validated : "+molesPerMolesTeamResult);
			}
			
		}
		return molesPerMolesTeamResult;
	}
	
	/**
	 * Check if the number of players is the same in each team
	 * @return	the number of players for each team if all teams have the same number, else -1
	 */
	private int sameNumberPlayersInEachTeam(){
		int count = -1;
		
		if (context.getTeams().size() != 0){
			Iterator<String> keys = context.getTeams().keySet().iterator();
			count = context.getTeams().get(keys.next()).countPlayer();
			while (keys.hasNext() && count != -1){
				if (count != context.getTeams().get(keys.next()).countPlayer()){
					count = -1;
				}
			}
			
		}
		
		return count;
	}
	
	/**
	 * Find all moles in different teams
	 */
	void startRandomMoles(){
	
		int rand = 0;
		
		if (!context.isGameModeExtented()){
		
			// Find the moles
			for (Entry<String,Team> entry : context.getTeams().entrySet()){
				
				ArrayList<Player> players = new ArrayList<Player>();
	
				for (int i = 0; i < context.getMolesPerTeam(); i++)
				{
					boolean check = false;
					
					while (check == false){
						rand = context.getRandom().nextInt(entry.getValue().countPlayer());
						
						if (!players.contains(entry.getValue().getPlayers().get(rand))){
							
							context.addMole(entry.getValue().getPlayers().get(rand));
							
							// Avoid two same moles
							players.add(entry.getValue().getPlayers().get(rand));
							
							check = true;
						}
					}
				}
				
			}
		}
		else{
				// Find the moles
				// Distribute moles in teams randomly but balanced between teams
				ArrayList<Team> teams = new ArrayList<Team>();
				ArrayList<Player> players = new ArrayList<Player>();
				ArrayList<String> teamsName = new ArrayList<String>();
				Iterator<String> it = context.getTeams().keySet().iterator();
				
				while (it.hasNext()){
					teamsName.add(it.next());
				}
				
				for (int i = 0; i < context.getMolesPerMolesTeam(); i++)
				{
					// Check is true when a good mole in a good team has been found
					boolean check = false;
					
					// while a new team (good team = not chosen) has not been found
					while (check == false){
						
						int randTeam = context.getRandom().nextInt(context.getTeams().size());
						
						Team team = context.getTeams().get(teamsName.get(randTeam));
						
						if (!teams.contains(team)){
							
							// while a new mole has not been found
							while (check == false){
							
								rand = context.getRandom().nextInt(team.countPlayer());
								
								if (!players.contains(team.getPlayers().get(rand)) & !team.getPlayers().get(rand).isDead()){
									
									context.addMole(team.getPlayers().get(rand));
									
									// Avoid two same moles
									players.add(team.getPlayers().get(rand));
									
									plugin.getLogger().log(Level.INFO, "[SPOIL]"+team.getPlayers().get(rand).getName()+" from team "+team.getName()+" has been selected to be a mole.");
									
									check = true;
								}
							}
							
							teams.add(team);
							
							// if we have check all teams, we clear chosen team and we start again to continue
							if (teams.size() == context.getTeams().size()){
								teams.clear();
							}
						}
					}
				}
		}
		
		// Now we have moles in each team
	}
	
	/**
	 * Send a message to all players in broadcast, the message will be shown in the console but also using Title Manager plugin if wanted
	 * @param message	Message to show
	 * @param useTitleManager	True if you want to show the message using this plugin, else false
	 */
	public void notifyPlayersOnBroadcast(String message, ChatColor color, boolean useTitleManager){
		
		getServer().broadcastMessage(color + message);
		
		if (useTitleManager)
		{
			// Show a message
			for(Player pl : getServer().getOnlinePlayers()){
				new TitleObject(color +message, "").send(pl);
			}
		}
		
	}
	
	/**
	 * Build the mole message
	 * @param args	: List of words
	 * @return	Concatenation of the words list
	 */
	private String moleMessage(String[] args)
	{
		String txt = "";
		
		for (String t : args)
		{
			txt += t + " ";
		}
		
		return txt;
	}
}

class Timer extends BukkitRunnable
{
	TaupeGunPlugin plugin;
	
	public Timer(TaupeGunPlugin plugin)
	{
		this.plugin = plugin;
	}

	public void run()
	{
		int seconds = plugin.getContext().decSecondsLeft(), minutes = 0;
	
		// GLOBAL TIME
		if(seconds == -1)
		{
			plugin.getContext().setSecondsLeft(59);
			minutes = plugin.getContext().decMinutesLeft();
			
			if (minutes == -1)
			{
				
				plugin.getContext().setMinutesLeft(plugin.getConfig().getInt("episodes.time")-1);
				plugin.getContext().setSecondsLeft(59);
				plugin.getContext().incEpisode();
				
				String txt = "";				
				
				plugin.notifyPlayersOnBroadcast("Episode "+plugin.getContext().getEpisode(),ChatColor.GOLD , true);
				
				if (plugin.getConfig().getInt("map.startResizeEpisodeNumber") == plugin.getContext().getEpisode())
				{
					plugin.getServer().getWorlds().get(0).getWorldBorder().setSize(plugin.getConfig().getDouble("map.endSize")*2, plugin.getConfig().getLong("map.timeFromStartSizeToEndSizeSeconds"));
					txt = ChatColor.DARK_RED + "La bordure rétrécit...";
					plugin.getServer().broadcastMessage(txt);
				}
				
				if (plugin.getConfig().getInt("map.startResizeEpisodeNumber") - 1  == plugin.getContext().getEpisode())
				{
					//plugin.getServer().getWorlds().get(0).getWorldBorder().setSize(plugin.getConfig().getDouble("map.endSize")*2, plugin.getConfig().getLong("map.timeFromStartSizeToEndSizeSeconds"));
					txt = ChatColor.DARK_RED + "La bordure rétrécira au prochain épisode";
					plugin.getServer().broadcastMessage(txt);
				}
				
			}
			
			// MOLES TIME
			if (!plugin.getContext().isMolesActivated())
			{
				minutes = plugin.getContext().decMinutesMolesLeft();
				
				if(minutes == -1)
				{
					
					// Choose moles now for the future
					plugin.startRandomMoles();
					
					// Initialize moles teams
					for(int i = 1; i <= this.plugin.getContext().getNumberTeamMoles(); i++)
					{
						this.plugin.getContext().addMolesTeam("Taupes-"+i);
					}
					
					plugin.getContext().activateMoles();
					plugin.getContext().setPreMolesTeam();
					
					for (Player player : plugin.getContext().getAllPlayers())
					{
						player.sendMessage(ChatColor.AQUA + "Les taupes ont été annoncées.");
					}
					
					for (Player player : plugin.getContext().getMoles())
					{
						player.sendMessage(ChatColor.RED + "-------------------------------------------");
						player.sendMessage(ChatColor.GOLD + "Vous êtes une taupe ! Votre nouvelle équipe est composée d'autres taupes ! Trahissez vos anciens cohéquipiers !");
						player.sendMessage(ChatColor.GOLD + "Utilisez '/reveal' pour vous révéler en tant que taupe dès que vous le souhaitez !");
						player.sendMessage(ChatColor.GOLD + "Utilisez '/t (message)' pour parler aux autres taupes.");
						if(!(plugin.getContext().getKits().size() == 0)){
							player.sendMessage(ChatColor.GOLD + "Use '/claim' to claim your kit.");
						}
						player.sendMessage(ChatColor.RED + "-------------------------------------------");
					}
				}
			}
		}

		plugin.MatchInfo();
	}
}

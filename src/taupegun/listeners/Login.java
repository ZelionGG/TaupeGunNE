package taupegun.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import taupegun.start.TaupeGunPlugin;
import taupegun.structures.Team;

public class Login implements Listener{

	private TaupeGunPlugin plugin = null;
	
	public Login(TaupeGunPlugin newPlugin){
		this.plugin = newPlugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent ev)
	{
		
		plugin.getContext().updatePlayer(ev.getPlayer());
		
		if (!plugin.getContext().hasStarted()){
			
			// Game not started
			ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
			ItemMeta ccm = is.getItemMeta();
			ccm.setDisplayName(ChatColor.AQUA + "Choisissez une équipe");
			is.setItemMeta(ccm);
			ev.getPlayer().getInventory().clear();
			ev.getPlayer().getInventory().addItem(is);
			
			ev.getPlayer().setGameMode(ev.getPlayer().isOp() ? GameMode.CREATIVE : GameMode.ADVENTURE );
			ev.getPlayer().teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation().add(0, 1, 0));
		}
		else{
			// Game started
			// Is the join player was in a team ?
			if (plugin.getContext().getTeamOfPlayer(ev.getPlayer()) != null)
			{
				ev.getPlayer().loadData();
				
			}
			else
			{
				ev.getPlayer().setGameMode(GameMode.SPECTATOR);
				ev.getPlayer().teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
			}
			
		}
		
		if (plugin.getContext().isAlreadyInATeam(ev.getPlayer())){
			
			Team team = plugin.getContext().getTeamOfPlayer(ev.getPlayer());
			ev.getPlayer().setPlayerListName(team.getColor()+" "+ev.getPlayer().getDisplayName());
			
		}
		
		plugin.MatchInfo();
		
	}
	
}

package taupegun.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import taupegun.start.TaupeGunPlugin;
import taupegun.structures.Team;

public class Ingame implements Listener{

	private TaupeGunPlugin plugin = null;
	
	public Ingame(TaupeGunPlugin newPlugin){
		this.plugin = newPlugin;
	}
	
	/**
	 * Trigger when a player died
	 * @param ev
	 */
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent ev)
	{		
		Player player = ev.getEntity();
		
		for(Player p : plugin.getServer().getOnlinePlayers())
		{
			p.playSound(p.getLocation(), Sound.WITHER_DEATH, 1.0F, 1.0F);
		}
		
		Team team = plugin.getContext().getTeamOfPlayer(player);
		
		ev.setDeathMessage(team.getColor()+player.getName()+ChatColor.GRAY+" a été tué");
		
		player.setGameMode(GameMode.SPECTATOR);
		plugin.getContext().removePlayerFromATeam(player);
		plugin.getContext().getAllPlayers().remove(player);
		if (plugin.getContext().isMole(player)){
			plugin.getContext().removeMole(player);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent ev)
	{
		
		if (plugin.getContext().isInvincible()){
			ev.setCancelled(true);
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent ev)
	{
		Player player = ev.getPlayer();
		String message = ev.getMessage();
		
		if (plugin.getContext().hasStarted()){
		
			if(!plugin.getContext().isAlreadyInATeam(player) && player.getGameMode() == GameMode.SPECTATOR)
			{
				ev.setCancelled(true);
				for (Player play : plugin.getServer().getOnlinePlayers())
				{
					play.sendMessage(ChatColor.GRAY + "[Spec] " + ChatColor.RESET + "<"+ev.getPlayer().getName()+"> "+ev.getMessage());
				}
				return;
			}
			
			if (plugin.getContext().isAlreadyInATeam(player)){
				
				ev.setCancelled(true);
				
				Team team = plugin.getContext().getTeamOfPlayer(player);
					
				plugin.getServer().broadcastMessage(team.getColor()+"<"+player.getName()+">"+ChatColor.WHITE+" "+message);
				
			}
			else {
				
				ev.setFormat("<"+ev.getPlayer().getName()+"> "+ev.getMessage());
				
			}
		}
		
	}
	
}

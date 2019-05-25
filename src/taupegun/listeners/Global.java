package taupegun.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import taupegun.start.TaupeGunPlugin;
import taupegun.structures.Team;

/**
 * Global listeners
 * @author LetMeR00t
 *
 */
public class Global implements Listener{

	/**
	 * Reference to the plugin Object
	 */
	private TaupeGunPlugin plugin = null;
	
	/**
	 * Default constructor
	 * @param newPlugin	plugin Object
	 */
	public Global(TaupeGunPlugin newPlugin){
		this.plugin = newPlugin;
	}
	
	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent ev){
		
		if (!plugin.getContext().hasStarted() && (ev.getAction().equals(Action.RIGHT_CLICK_BLOCK) || ev.getAction().equals(Action.RIGHT_CLICK_AIR))){
			
			Player player = ev.getPlayer();

			// Choose a team
			if (ev.getItem().getType() == Material.SKULL_ITEM && ChatColor.stripColor(ev.getItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Choisissez une équipe"))
			{
				ev.setCancelled(true);
				
				Inventory iv = plugin.getServer().createInventory(null, 54, ChatColor.AQUA + "Choisissez une équipe");
				
				int slot = 0;
				for (Entry <String,Team> entry : plugin.getContext().getTeams().entrySet())
				{
					Team team = entry.getValue();
					
					ItemStack i = new ItemStack(Material.BEACON);
					ItemMeta im = i.getItemMeta();
					im.setDisplayName(team.getColor() + team.getName());
					List<String> lore = new ArrayList<String>();
					
					for(Player play : team.getPlayers())
					{
						lore.add(team.getColor() + "- " + play.getDisplayName());
					}
					
					im.setLore(lore);
					i.setItemMeta(im);
					
					iv.setItem(slot, i);
					slot++;
				}
				
				player.openInventory(iv);
				return;

			}
			
		}
		
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent ev)
	{

	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent ev)
	{
		
	}
	
}

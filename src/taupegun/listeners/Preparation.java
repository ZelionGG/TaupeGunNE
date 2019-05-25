package taupegun.listeners;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import taupegun.start.TaupeGunPlugin;
import taupegun.structures.Kit;
import taupegun.structures.Team;

public class Preparation implements Listener{

	/**
	 * Reference to the plugin Object
	 */
	private TaupeGunPlugin plugin = null;
	
	/**
	 * Reference to the ChatInventory object
	 */
	private ChatInventory ci = null;
	
	/**
	 * Default constructor
	 * @param newPlugin	the TaupeGunPlugin instance
	 * @param ci	the ChatInventory instance
	 */
	public Preparation(TaupeGunPlugin newPlugin, ChatInventory ci){
		this.plugin = newPlugin;
		this.ci = ci;
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent ev)
	{
		if(!plugin.getContext().hasStarted())
		{
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent ev)
	{
		if(!plugin.getContext().hasStarted())
		{
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent ev)
	{
		if(!plugin.getContext().hasStarted())
		{
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onMoveItemInventory(InventoryMoveItemEvent ev){
		
		if(!plugin.getContext().hasStarted())
		{
			ev.setCancelled(true);
		}
		
	}
	
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent ev){
		
		if (!plugin.getContext().hasStarted()){
		
			if(plugin.getContext().isAlreadyInATeam(ev.getPlayer())){
				
				Block block = ev.getPlayer().getLocation().subtract(0, 1, 0).getBlock();
				
				if (block.getType().equals(Material.STAINED_GLASS) && !ev.getPlayer().isFlying()){
				
					ChatColor color = plugin.getContext().getTeamOfPlayer(ev.getPlayer()).getColor();
					
					byte sid = 0;
					
					// Get associated color
					
					if (color.equals(ChatColor.DARK_BLUE)) { sid = (byte) new Integer(11).intValue();}
					if (color.equals(ChatColor.DARK_PURPLE)) { sid = (byte) new Integer(10).intValue();}
					if (color.equals(ChatColor.GOLD)) { sid = (byte) new Integer(1).intValue();}
					if (color.equals(ChatColor.RED)) { sid = (byte) new Integer(14).intValue();}
					if (color.equals(ChatColor.YELLOW)) { sid = (byte) new Integer(4).intValue();}
					if (color.equals(ChatColor.GREEN)) { sid = (byte) new Integer(5).intValue();}
					if (color.equals(ChatColor.AQUA)) { sid = (byte) new Integer(3).intValue();}
					if (color.equals(ChatColor.WHITE)) { sid = (byte) new Integer(0).intValue();}
					if (color.equals(ChatColor.BLACK)) { sid = (byte) new Integer(15).intValue();}
					
					block.setData(sid);
				}
			}
			
		}
		
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent ev)
	{
		if(!plugin.getContext().hasStarted()){
		
			Player player = (Player) ev.getWhoClicked();
			
			// Choose a team
			if (ChatColor.stripColor(ev.getInventory().getName()).equalsIgnoreCase("Choisissez une équipe"))
			{
				ev.setCancelled(true);
								
				if(ev.getCurrentItem() != null && ev.getCurrentItem().getType() == Material.BEACON)
				{
					player.closeInventory();
					
					Team team = plugin.getContext().getTeam(ev.getCurrentItem().getItemMeta().getDisplayName());
					
					// Check if the player is already in a team
					if (plugin.getContext().isAlreadyInATeam(player)){
						
						// We change the team of the player
						plugin.getContext().changeTeamPlayer(player, team);
						
					}
					else{
						
						// We add the player
						plugin.getContext().addPlayerToATeam(player, team);
						
					}
					
					plugin.getServer().broadcastMessage(player.getName()+" a rejoint l'équipe "+team.getColor()+team.getName());
					
					return;
				}
			}
			
			// Manage teams
			if (ev.getInventory().getName().contains("- Teams -"))
			{
				ev.setCancelled(true);
				
				if(ev.getCurrentItem() != null && ev.getCurrentItem().getType() == Material.DIAMOND)
				{					
					ci.changeLastState(player, StateChat.CREATE_TEAM_NAME);
					player.closeInventory();
					player.sendMessage(ChatColor.GRAY + "Write the name of the new team");
					
					return;
				}
				
				if(ev.getCurrentItem() != null && ev.getCurrentItem().getType() == Material.BEACON)
				{
					player.closeInventory();
					
					Team team = plugin.getContext().getTeam(ev.getCurrentItem().getItemMeta().getDisplayName());
					
					Inventory iv = plugin.getServer().createInventory(null, 54, team.getColor() + team.getName());
					
					ItemStack cc = new ItemStack(Material.FEATHER);
					ItemMeta ccm = cc.getItemMeta();
					ccm.setDisplayName(ChatColor.AQUA + "Change the team color");
					cc.setItemMeta(ccm);
					iv.setItem(51, cc);
					
					ItemStack re = new ItemStack(Material.ANVIL);
					ItemMeta are = re.getItemMeta();
					are.setDisplayName(ChatColor.AQUA + "Rename the team");
					re.setItemMeta(are);
					iv.setItem(52, re);
					
					ItemStack de = new ItemStack(Material.TNT);
					ItemMeta ade = de.getItemMeta();
					ade.setDisplayName(ChatColor.AQUA + "Remove the team");
					de.setItemMeta(ade);
					iv.setItem(53, de);
					
					player.openInventory(iv);
					return;
				}
			}

			// Manage kits
			if (ev.getCurrentItem() != null && ev.getInventory().getName().contains("- Kits -"))
			{
				ev.setCancelled(true);

				if (ev.getCurrentItem() != null){
					if(ev.getCurrentItem().getType() == Material.DIAMOND)
					{					
						ci.changeLastState(player, StateChat.CREATE_KIT_NAME);
						player.closeInventory();
						player.sendMessage(ChatColor.GRAY + "Write the name of the new kit.");
						
						return;
					}
	
					if(ev.getCurrentItem().getType() == Material.CHEST)
					{
						player.closeInventory();
						
						Kit kit = plugin.getContext().getKit(ev.getCurrentItem().getItemMeta().getDisplayName());
						
						if (kit != null){
							Inventory iv = plugin.getServer().createInventory(null, InventoryType.HOPPER, ChatColor.GOLD + kit.getName() + ChatColor.GRAY + " Option");
							
							ItemStack cc = new ItemStack(Material.CHEST);
							ItemMeta ccm = cc.getItemMeta();
							ccm.setDisplayName(ChatColor.AQUA + "Set Kit");
							cc.setItemMeta(ccm);
							iv.setItem(0, cc);
							
							ItemStack re = new ItemStack(Material.ANVIL);
							ItemMeta are = re.getItemMeta();
							are.setDisplayName(ChatColor.AQUA + "Rename kit");
							re.setItemMeta(are);
							iv.setItem(2, re);
							
							ItemStack de = new ItemStack(Material.TNT);
							ItemMeta ade = de.getItemMeta();
							ade.setDisplayName(ChatColor.AQUA + "Remove kit");
							de.setItemMeta(ade);
							iv.setItem(4, de);
							
							player.openInventory(iv);
						}
						
						return;
					}
				}
			}
			
			// In the case we manage a team (name of the team as inventory name)
			for (Entry<String,Team> entry : plugin.getContext().getTeams().entrySet())
			{
				Team team = entry.getValue();
				
				if (ChatColor.stripColor(ev.getInventory().getName()).equalsIgnoreCase(team.getName()))
				{
					ev.setCancelled(true);
					
					if (ev.getCurrentItem() != null){
						
						if (ev.getCurrentItem().getType() == Material.FEATHER)
						{
						
							plugin.getContext().changeTeamColor(team);
							
							plugin.getServer().broadcastMessage(ChatColor.GRAY + "Team "+team.getColor() + team.getName() + ChatColor.GRAY + " has changed his color");
							
							player.closeInventory();
							
							return;
						}
						
						if (ev.getCurrentItem().getType() == Material.ANVIL)
						{
							ci.changeLastState(player,  StateChat.RENAME_TEAM);
							ci.addValueState(player, StateChat.TEAM_NAME, team.getName());;
							
							player.sendMessage(ChatColor.GRAY + "Write the name of the new team " + team.getColor() +team.getName() + ChatColor.GRAY + ".");
							player.closeInventory();
							return;
						}
						
						if (ev.getCurrentItem().getType() == Material.TNT)
						{
							player.sendMessage(ChatColor.GRAY + "Team " + team.getColor() + team.getName() + ChatColor.GRAY + " has been removed.");
							
							plugin.getContext().removeTeam(team);
							
							player.closeInventory();
							return;
						}
					}
				}
			}
			
			// Manage kits
			for (Kit kit : plugin.getContext().getKits())
			{
				if (ChatColor.stripColor(ev.getInventory().getName()).equalsIgnoreCase(kit.getName()+" Option"))
				{
					ev.setCancelled(true);
					
					if (ev.getCurrentItem() != null){
					
						if (ev.getCurrentItem().getType() == Material.CHEST)
						{
							player.closeInventory();
							player.openInventory(kit.getInventory());
							return;
						}
						
						if (ev.getCurrentItem().getType() == Material.ANVIL)
						{							
							ci.changeLastState(player, StateChat.RENAME_KIT);
							ci.addValueState(player, StateChat.KIT_NAME, kit.getName());
							
							player.sendMessage(ChatColor.GRAY + "Write the name of the new kit " + ChatColor.GOLD + kit.getName() + ChatColor.GRAY + ".");
							player.closeInventory();
							return;
						}
						
						if (ev.getCurrentItem().getType() == Material.TNT)
						{
							player.sendMessage(ChatColor.GRAY + "Kit " +ChatColor.GOLD + kit.getName() + ChatColor.GRAY + " has been removed.");
							
							plugin.getContext().removeKit(kit);
							
							player.closeInventory();
							return;
						}
					}
				}
			}
			
		}
		
	}
	
}

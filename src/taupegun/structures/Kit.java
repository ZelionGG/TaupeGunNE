package taupegun.structures;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Class Kit defines a Kit object that a mole can receive in the game
 * @author LetMeR00t
 *
 */
public class Kit
{
	/**
	 * Name of the kit
	 */
	private String name = null;
	
	/**
	 * Inventory that contains objects of the kit
	 */
	private Inventory inventory = null;
	
	
	/**
	 * Boolean to know if the kit has been claimed
	 */
	private boolean isClaimed = false;
	
	/**
	 * Default constructor for a Kit Object
	 * @param newName	Name of the Kit
	 * @param newInventory	Inventory object that contains objects of the kit
	 */
	public Kit(String newName, Inventory newInventory)
	{
		this.name = newName;
		this.inventory = newInventory;
	}
	
	/**
	 * Get the name of the kit
	 * @return name of the kit
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Set the name of the kit
	 * @param newName	New name of the kit
	 */
	public void setName(String newName){
		this.name = newName;
	}
	
	/**
	 * Get the Inventory object that contains objects
	 * @return	the Inventory object of the kit
	 */
	public Inventory getInventory(){
		return this.inventory;
	}
	
	/**
	 * Add an item stack to the kit
	 * @param item	new ItemStack
	 */
	public void addItemToTheKit(ItemStack items){
		this.inventory.addItem(items);
	}
	
	/**
	 * Remove an item stack from the kit
	 * @param items	ItemStack to remove
	 */
	public void removeItemFromKit(ItemStack items){
		this.inventory.removeItem(items);
	}
	
	/**
	 * Check if the kit has been claimed
	 * @return	True if it's claimed, else false
	 */
	public Boolean isClaimed(){
		return this.isClaimed;
	}
	
	/*public void giveKitPlayer()
	{
		Player sp = Bukkit.getPlayer(getPlayer().getUniqueId());
		
		if(!kitTake)
		{
			for(ItemStack i : iv.getContents())
			{
				addItem(i);
			}
			
			sp.sendMessage(ChatColor.BLUE + "You have received your kit little mole");
			
			kitTake = true;
			return;
		}
		if(kitTake)
		{
			sp.sendMessage(ChatColor.BLUE + "You already have received your kit, fucking little mole.");
		}
	}*/
	
	
	/*private void addItem(ItemStack i)
	{
		Player sp = Bukkit.getPlayer(getPlayer().getUniqueId());
		
		
		if(i == null)return;
		
		if(i.getType() == Material.AIR)return;
		
		sp.getWorld().dropItemNaturally(sp.getLocation(), i);
	}*/
}

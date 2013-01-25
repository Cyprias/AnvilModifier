package com.Cyprias.AnvilModifier.listeners;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.Cyprias.AnvilModifier.ChatUtils;
import com.Cyprias.AnvilModifier.Logger;

public class InventoryListener implements Listener {

	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event) {
		//No item under player's mouse, no need to do anything.
		if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
			return;
		
		if (!(event.getInventory().getType() == InventoryType.ANVIL))
			return;
		
		HumanEntity e = event.getWhoClicked();
		
		if (!(e instanceof Player))
			return;
			
		Player p = (Player) e;
		
		//Logger.debug("getLevel: " + p.getLevel() + " " + p.getTotalExperience());

		ItemStack cur = event.getCursor();
		//Logger.debug("InventoryClickEvent " + event.getCurrentItem().getType() + ", " + cur.getType() );
		//Logger.debug("InventoryClickEvent isAnvil " + (event.getInventory().getType() == InventoryType.ANVIL) );

		
		if (event.getRawSlot() == 2 && event.getSlot() == 9) {
			
			ItemStack firstItem = event.getInventory().getItem(0);
			ItemStack thirdItem = event.getCurrentItem();
	
			//ItemMeta meta = thirdItem.getItemMeta();
			
			
			if (thirdItem.getItemMeta().getDisplayName() != firstItem.getItemMeta().getDisplayName()){
				Logger.debug("Item has a new name, escaping...");
				return;
			}
			
			
			Logger.debug("Anvil craft! " + event.getRawSlot() + " " + event.getSlot()  + ", " + thirdItem.getItemMeta().getDisplayName());
			
			//Give player the item in their cursor.
			//Doing this seems to keep the player's level/exp the same server side by client sees their level change.
			event.setCursor(thirdItem);
			
			//Force update their exp so client sees their server level. 
			setExp(p, p.getTotalExperience());
			
			//Remove items from first and second slots.
			event.getInventory().setItem(0, null);
			event.getInventory().setItem(1, null);
			
			//Remove item from the 3rd slot.
			event.setCurrentItem(null);
			
			//Cancel event so it costs nothing?
			event.setCancelled(true);
			
			ChatUtils.send(p, "Repaired your " +  thirdItem.getType() + " at no level cost!");
			Logger.debug("Repaired " + p.getName() + "'s " + thirdItem.getType());
			
			
			
		}

	}
	
	
	public static void setExp(Player player, int amount){
		//Clear player's XP.
		player.setTotalExperience(0);
		player.setExp(0.0F);
		player.setLevel(0);

		//Give player XP amount. 
		player.giveExp(amount);
	}
	
}

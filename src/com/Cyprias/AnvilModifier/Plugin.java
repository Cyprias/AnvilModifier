package com.Cyprias.AnvilModifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import net.minecraft.server.v1_4_R1.Item;
import net.minecraft.server.v1_4_R1.ItemStack;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.Cyprias.AnvilModifier.configuration.Config;
import com.Cyprias.AnvilModifier.configuration.YML;
import com.Cyprias.AnvilModifier.listeners.InventoryListener;
import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

public class Plugin extends JavaPlugin {
	

	private static Plugin instance = null;

	public void onEnable() {
		instance = this;

		if (!(new File(getDataFolder(), "config.yml").exists())) {
			Logger.info("Copying config.yml to disk.");
			try {
				YML.toFile(getResource("config.yml"), getDataFolder(), "config.yml");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		try {
			Config.checkForMissingProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		registerListeners(new InventoryListener());

		createAdapter();
		ProtocolLibrary.getProtocolManager().addPacketListener(pAdapter);
		
		//getCommand("am").setExecutor(new Commands());
		
	}

	public void onDisable() {
		ProtocolLibrary.getProtocolManager().removePacketListener(pAdapter);
	}

	public static final Plugin getInstance() {
		return instance;
	}

	private void registerListeners(Listener... listeners) {
		PluginManager manager = getServer().getPluginManager();

		for (Listener listener : listeners) {
			manager.registerEvents(listener, this);
		}
	}

	public class Commands implements CommandExecutor {

		//Debugging cmd to set durability.
		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
			
			if (args[0].equalsIgnoreCase("dur")) {
				
				Player p = (Player) sender;
				
				org.bukkit.inventory.ItemStack item = p.getItemInHand();
				short dur = item.getDurability();
				item.setDurability((short) (dur * 2));
				
			}
			
			
			return false;
		}
		
	}
	
	private PacketAdapter pAdapter;

	private String getItemsName(ItemStack rawItem){
		return rawItem.r();
	}
	
	
	public static HashMap<String, String> rawItemName = new HashMap<String, String>();
	
	public static HashMap<String, Boolean> willCostLevels = new HashMap<String, Boolean>();
	
	public void createAdapter() {
		// ListenerPriority.NORMAL,
		//

		// Packets.Server.WORLD_EVENT, Packets.Server.WINDOW_ITEMS,

		// Packets.Server.ENTITY_METADATA, 40
		// Packets.Server.NAMED_ENTITY_SPAWN,, Packets.Server.ENTITY_STATUS

		
		//Packets.Server.OPEN_WINDOW
		//Packets.Server.WINDOW_ITEMS 69	105 
		//Packets.Server.ITEM_DATA, Packets.Server.SET_EXPERIENCE, Packets.Server.CRAFT_PROGRESS_BAR
		//, Packets.Server.CLOSE_WINDOW
		
		pAdapter = new PacketAdapter(this, ConnectionSide.SERVER_SIDE, Packets.Server.SET_SLOT, Packets.Server.OPEN_WINDOW) {
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();

				String playerName = event.getPlayer().getName();
				
				StructureModifier<Object> mods = packet.getModifier();
				switch (event.getPacketID()) {
				case Packets.Server.SET_SLOT:
					

					
					/**/
					if (mods.size() >= 3) {

						int a = (Integer) mods.read(0);
						int b = (Integer) mods.read(1);
						ItemStack c = (ItemStack) mods.read(2);
						if (c == null)
							break;

					//	Logger.info("SET_SLOT size " + mods.size());
						
					//	Logger.debug("SET_SLOT a " + a + ", b: " + b);
						
						if (b == 0){//Item in first slot of anvil.
							Logger.debug("SET_SLOT first slot: " + c);
							
							
							
							rawItemName.put(playerName, getItemsName(c));
							
							Logger.debug(playerName + " put " + getItemsName(c) + " in the anvil.");
							
						//}else if ( b == 1){//Item in second slot of anvil.
						//	Logger.debug("SET_SLOT second slot: " + c);
							
							
						}else if (b == 2){//Item in third slot of anvil.
							Logger.debug("SET_SLOT third slot: " + c + " a: " + c.a() + " r: " + c.r());
							
							//rawItemName.containsKey(playerName) && rawItemName.get(playerName).matches(regex)
							
							if (rawItemName.containsKey(playerName)){
								
								if (rawItemName.get(playerName).equals(getItemsName(c))){
									if (!willCostLevels.containsKey(playerName) || willCostLevels.get(playerName).equals(true))
										ChatUtils.send(event.getPlayer(), "That repair will cost no levels.");
									
									willCostLevels.put(playerName, false);
								}else{
									if (!willCostLevels.containsKey(playerName) || willCostLevels.get(playerName).equals(false))
										ChatUtils.send(event.getPlayer(), "That repair will cost levels.");
									
									willCostLevels.put(playerName, true);
								}
								
								
								
							}
							
			
							
						}else if (a == -1 && b == -1){//Item in third slot of anvil.
							Logger.debug("SET_SLOT mouse slot?: " + c + " a: " + c.a() + " r: " + c.r());
							
							rawItemName.remove(playerName);
							willCostLevels.remove(playerName);
							
						}
						
						

					}
					/*	
					for (int i = 0; i < mods.size(); i++) {// 0=entID
						Logger.info("SET_SLOT: " + i + " " + mods.getField(i));

					}*/
					break;
				case Packets.Server.OPEN_WINDOW:
				//	Logger.info("OPEN_WINDOW");
					
					rawItemName.remove(playerName);
					willCostLevels.remove(playerName);
					
					break;
				case Packets.Server.ENTITY_STATUS:
					break;
				case Packets.Server.WINDOW_ITEMS:
					
					
					
					int windowId = (Integer) mods.read(0);
					int count = (Integer) mods.read(1);
					//int slotData = (Integer) mods.read(1);
					
					Logger.debug("WINDOW_ITEMS windowId: " + windowId + ", count: " + count);
					break;
				case 0x69:	//105
					Logger.info("Update Window Property");
					break;
				default:
					Logger.debug("getPacketID: " + event.getPacketID());
				}

			}
		};
	}
}

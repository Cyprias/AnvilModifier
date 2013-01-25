package com.Cyprias.AnvilModifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.Cyprias.AnvilModifier.configuration.Config;
import com.Cyprias.AnvilModifier.configuration.YML;
import com.Cyprias.AnvilModifier.listeners.InventoryListener;

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
}

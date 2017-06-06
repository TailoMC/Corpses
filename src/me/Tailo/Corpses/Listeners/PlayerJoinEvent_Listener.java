package me.Tailo.Corpses.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.Tailo.Corpses.System.main;
import me.Tailo.Corpses.Utils.Corpse;

public class PlayerJoinEvent_Listener implements Listener {

	private main plugin;

	public PlayerJoinEvent_Listener(main main) {
		this.plugin = main;
		plugin.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				
				Corpse.spawnCorpses(e.getPlayer());
				
			}
		}, 20L);
		
	}
	
}

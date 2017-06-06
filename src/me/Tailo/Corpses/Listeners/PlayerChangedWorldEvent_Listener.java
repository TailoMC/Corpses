package me.Tailo.Corpses.Listeners;

import me.Tailo.Corpses.System.main;
import me.Tailo.Corpses.Utils.Corpse;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangedWorldEvent_Listener implements Listener {

	private main plugin;

	public PlayerChangedWorldEvent_Listener(main main) {
		this.plugin = main;
		plugin.getServer().getPluginManager().registerEvents(this, main);
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
		
		Corpse.spawnCorpses(e.getPlayer());
		
	}

}

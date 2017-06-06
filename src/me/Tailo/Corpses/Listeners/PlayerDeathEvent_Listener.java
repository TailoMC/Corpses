package me.Tailo.Corpses.Listeners;

import me.Tailo.Corpses.System.main;
import me.Tailo.Corpses.Utils.Corpse;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathEvent_Listener implements Listener {

	private main plugin;

	public PlayerDeathEvent_Listener(main main) {
		this.plugin = main;
		plugin.getServer().getPluginManager().registerEvents(this, main);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
		
		new Corpse(e.getEntity(), e.getDrops());
		
		e.getDrops().clear();
		
	}

}

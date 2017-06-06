package me.Tailo.Corpses.System;

import me.Tailo.Corpses.Listeners.Packet_Listener;
import me.Tailo.Corpses.Listeners.PlayerChangedWorldEvent_Listener;
import me.Tailo.Corpses.Listeners.PlayerDeathEvent_Listener;
import me.Tailo.Corpses.Listeners.PlayerJoinEvent_Listener;

import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class main extends JavaPlugin {

	public static ProtocolManager pm;
	
	public static main instance;
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		pm = ProtocolLibrary.getProtocolManager();
		
		new Packet_Listener(this);
		
		new PlayerDeathEvent_Listener(this);
		new PlayerJoinEvent_Listener(this);
		new PlayerChangedWorldEvent_Listener(this);
		
	}
	
}

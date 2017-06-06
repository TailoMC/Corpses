package me.Tailo.Corpses.Listeners;

import org.bukkit.inventory.Inventory;

import me.Tailo.Corpses.System.main;
import me.Tailo.Corpses.Utils.Corpse;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

public class Packet_Listener {

	private main plugin;

	@SuppressWarnings("static-access")
	public Packet_Listener(main main) {
		this.plugin = main;
		
		plugin.pm.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {
			
			@Override
			public void onPacketReceiving(PacketEvent e) {
				
				PacketContainer packet = e.getPacket();
				
				if(packet.getEntityUseActions().read(0) == EntityUseAction.INTERACT) {
					
					int id = packet.getIntegers().read(0);
					
					Inventory inv = Corpse.getInventory(id);
					
					if(inv != null) e.getPlayer().openInventory(inv);
					
				}
				
			}
			
		});
		
	}

}

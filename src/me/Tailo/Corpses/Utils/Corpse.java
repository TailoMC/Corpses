package me.Tailo.Corpses.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.Tailo.Corpses.System.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

public class Corpse {

	int id;
	Inventory inv;
	Player p;
	WrappedGameProfile prof;
	
	private static HashMap<Integer, Corpse> getcorpse = new HashMap<>();
	
	private static HashMap<World, ArrayList<PacketContainer>> packets = new HashMap<>();
	private static ArrayList<Location> blockchanges = new ArrayList<>();
	
	public Corpse(Player p, List<ItemStack> drops) {		

		id = (int) Math.ceil(Math.random() * 1000) + 2000;
		
		inv = Bukkit.createInventory(null, 45);
		ItemStack[] items = new ItemStack[drops.size()];		
		drops.toArray(items);
		inv.addItem(items);
		
		this.p = p;
		this.prof = WrappedGameProfile.fromPlayer(p);
		
		spawnCorpse();
		
		getcorpse.put(id, this);
		
	}
	
	public static Inventory getInventory(int id) {
		if(getcorpse.get(id) != null) {
			return getcorpse.get(id).inv;
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	private void spawnCorpse() {
		
		Location loc = p.getLocation().clone();
		loc.setY(getYDown(loc) + 1.15);
		
		PacketContainer corpse = main.pm.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);		
		corpse.getIntegers().write(0, id);
		corpse.getIntegers().write(1, toLocation(loc.getX()));
		corpse.getIntegers().write(2, toLocation(loc.getY()));
		corpse.getIntegers().write(3, toLocation(loc.getZ()));
		corpse.getIntegers().write(4, 0);		
		corpse.getBytes().write(0, toRotation(loc.getYaw()));
		corpse.getBytes().write(1, toRotation(loc.getPitch()));		
		corpse.getUUIDs().write(0, p.getUniqueId());
		WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(p).deepClone();
		corpse.getDataWatcherModifier().write(0, watcher);
		
		Location bedloc = loc.clone();
		bedloc.setY(0);
		
		PacketContainer bed = main.pm.createPacket(PacketType.Play.Server.BED);		
		bed.getIntegers().write(0, id);
		BlockPosition pos = new BlockPosition(bedloc.getBlockX(), bedloc.getBlockY(), bedloc.getBlockZ());
		bed.getBlockPositionModifier().write(0, pos);
		
		PacketContainer tp = main.pm.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
		tp.getIntegers().write(0, id);
		tp.getIntegers().write(1, toLocation(loc.getX()));
		tp.getIntegers().write(2, toLocation(loc.getY()));
		tp.getIntegers().write(3, toLocation(loc.getZ()));
		tp.getBytes().write(0, toRotation(loc.getYaw()));
		tp.getBytes().write(1, toRotation(loc.getPitch()));
		
		ArrayList<PacketContainer> containers = packets.get(p.getWorld());
		if(containers == null) {
			containers = new ArrayList<>();
		}		
		containers.add(corpse);
		containers.add(bed);		
		containers.add(tp);
		packets.put(p.getWorld(), containers);
		blockchanges.add(bedloc);
		
		for(Player players : p.getWorld().getPlayers()) {
			
			players.sendBlockChange(bedloc, Material.BED_BLOCK, (byte) 0);
			
			sendPlayerInfo(players, this);
			
			try {
				main.pm.sendServerPacket(players, corpse);
				main.pm.sendServerPacket(players, bed);
				main.pm.sendServerPacket(players, tp);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public static void spawnCorpses(Player p) { 
		
		for(Location loc : blockchanges) {
			p.sendBlockChange(loc, Material.BED_BLOCK, (byte) 0);
		}
		
		for(Corpse corpse : getcorpse.values()) {
			sendPlayerInfo(p, corpse);
		}
		
		if(packets.containsKey(p.getWorld())) {
			for(PacketContainer packet : packets.get(p.getWorld())) {
				
				try {
					main.pm.sendServerPacket(p, packet);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
	public static void despawnCorpses() {
		
		for(Player players : Bukkit.getOnlinePlayers()) {
			
			for(Corpse corpse : getcorpse.values()) {
				
				PacketContainer despawn = main.pm.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
				despawn.getIntegerArrays().write(0, new int[]{ corpse.id });
				
				try {
					main.pm.sendServerPacket(players, despawn);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
	
	private static void sendPlayerInfo(Player p, Corpse corpse) {
		
		if((!corpse.p.isOnline() || !p.canSee(corpse.p)) && !p.getName().equals(corpse.p.getName())) {
			
			PacketContainer info = main.pm.createPacket(PacketType.Play.Server.PLAYER_INFO);
			info.getPlayerInfoAction().write(0, PlayerInfoAction.ADD_PLAYER);
			List<PlayerInfoData> datas = new ArrayList<>();
			PlayerInfoData data = new PlayerInfoData(corpse.prof, 0, NativeGameMode.SURVIVAL, WrappedChatComponent.fromChatMessage("")[0]);
			datas.add(data);
			info.getPlayerInfoDataLists().write(0, datas);
			
			try {
				main.pm.sendServerPacket(p, info);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			PacketContainer remove = main.pm.createPacket(PacketType.Play.Server.PLAYER_INFO);
			remove.getPlayerInfoAction().write(0, PlayerInfoAction.REMOVE_PLAYER);
			remove.getPlayerInfoDataLists().write(0, datas);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(main.instance, new Runnable() {
				
				@Override
				public void run() {
					
					try {
						main.pm.sendServerPacket(p, remove);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					
				}
			}, 20L);
			
		}
		
	}
	
	private static double getYDown(Location loc) {
		
		double y = loc.getY();
		
		while(!loc.getBlock().getType().isSolid()) {
			y --;
			loc.setY(y);
		}
		
		return y;
		
	}
	
	public static int toLocation(double d) {
		return (int) Math.floor(d * 32.0D);
	}

	public static byte toRotation(float f) {
		return (byte) (int) (f * 256.0F / 360.0F);
	}
	
}

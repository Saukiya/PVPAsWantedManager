package vip.foxcraft.pvpaswantedmanager;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;

public class JailManager implements Listener {
	static File DataFile;
	static public void teleport(Player player,Location location){
		Double X = location.getX();
		Double Y = location.getY();
		Double Z = location.getZ();
		World World = location.getWorld();
		
        for(int i=0;i <= (256-Y);){
        	location = new Location(World,X,Y+i,Z);
        if(location.getBlock().isEmpty()){
        	location = new Location(World,X,Y+i+1,Z);
        	if(location.getBlock().isEmpty()){
        		if(i==0){
        			for(int l=1;l <= Y;){
        				location =  new Location(World,X,Y-l,Z);
        				if(location.getBlock().isEmpty()){
        					l++;
        				}else{
        					Y = Y -l+1;
        					break;
        				}
        			}
        		}
        		Y = Y+i+0.1;
        		location = new Location(World,X,Y,Z);
        		i=500;
        	}else{
        		i=i+2;
        	}
        }else{
        	i++;
        }
        }
        player.teleport(location);
	}
	static public void playerJoinJail(Player player){
		DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "PlayerData" + File.separator + player.getName() +".yml");
        YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player.getName());
        int playerX = player.getLocation().getBlockX();
        int playerY = player.getLocation().getBlockY();
        int playerZ = player.getLocation().getBlockZ();
        String playerWorld = player.getWorld().getName();
        PlayerData.set("attribute.originalX", playerX);
        PlayerData.set("attribute.originalY", playerY);
        PlayerData.set("attribute.originalZ", playerZ);
        PlayerData.set("attribute.originalWorld", playerWorld);
        PVPAsWantedManager.onSaveData(player.getName(), PlayerData);
        double jailX = Integer.valueOf(Config.getConfig("jail.location.X"))+0.5;
        double jailY = Integer.valueOf(Config.getConfig("jail.location.Y"));
        double jailZ = Integer.valueOf(Config.getConfig("jail.location.Z"))+0.5;
        World jailWorld = Bukkit.getWorld(Config.getConfig("jail.location.World"));
        Location jail = new Location(jailWorld,jailX,jailY,jailZ);
        player.setFlying(false);
        teleport(player,jail);
	}
	
	static public void playerTeleportJail(Player player){
        double jailX = Integer.valueOf(Config.getConfig("jail.location.X"))+0.5;
        double jailY = Integer.valueOf(Config.getConfig("jail.location.Y"));
        double jailZ = Integer.valueOf(Config.getConfig("jail.location.Z"))+0.5;
        World jailWorld = Bukkit.getWorld(Config.getConfig("jail.location.World"));
        Location jail = new Location(jailWorld,jailX,jailY,jailZ);
        teleport(player,jail);
        player.sendMessage(Message.getMsg("player.jailedeventMessage"));
	}
	
	static public void playerQuitJail(Player player){
		DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "PlayerData" + File.separator + player.getName() +".yml");
        YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player.getName());
        double playerX = PlayerData.getInt("attribute.originalX");
        double playerY = PlayerData.getInt("attribute.originalY");
        double playerZ = PlayerData.getInt("attribute.originalZ");
        World playerWorld = Bukkit.getWorld(PlayerData.getString("attribute.originalWorld"));
        Location playerLocatioin = new Location(playerWorld,playerX,playerY,playerZ);
        teleport(player,playerLocatioin);
        PlayerData.set("attribute.originalX", Integer.valueOf(0));
        PlayerData.set("attribute.originalY", Integer.valueOf(0));
        PlayerData.set("attribute.originalZ", Integer.valueOf(0));
        PlayerData.set("attribute.originalWorld", String.valueOf("world"));
		PVPAsWantedManager.onSaveData(player.getName(),PlayerData);
	}
	
	static public void playerSetJail(Player player){
        int playerX = player.getLocation().getBlockX();
        int playerY = player.getLocation().getBlockY();
        int playerZ = player.getLocation().getBlockZ();
        String playerWorld = player.getWorld().getName();
        Config.setConfig("jail.location.X", playerX);
        Config.setConfig("jail.location.Y", playerY);
        Config.setConfig("jail.location.Z", playerZ);
        Config.setConfig("jail.location.World", playerWorld);
        player.sendMessage(Message.getMsg("admin.setJailMessage", String.valueOf(playerX),String.valueOf(playerY),String.valueOf(playerZ),playerWorld));
	}
	
	public Boolean isJailPlayer(Player player){
		DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "PlayerData" + File.separator + player.getName() +".yml");
        YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player.getName());
        int playerJailPoints = PlayerData.getInt("jail.times");
        if(playerJailPoints > 0){
    		return true;
        }
        return false;
	}
	

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		if(Config.getConfig("jail.eventManager.joinServer.enabled").equals("false"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player)){
			playerTeleportJail(player);;
			player.sendMessage(Message.getMsg("player.timeDeductionJailedMessage",PVPAsWantedManager.onLoadData(player.getName()).getString("jail.times")));
		}
	}
	@EventHandler
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		if(Config.getConfig("jail.eventManager.dropItem.enabled").equals("false"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player))event.setCancelled(true);
	}
	@EventHandler
	public void PlayerPickupItemEvent(PlayerPickupItemEvent event){
		if(Config.getConfig("jail.eventManager.pickupItem.enabled").equals("false"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player))event.setCancelled(true);
	}
	@EventHandler
	public void PlayerPickupArrowEvent(PlayerPickupArrowEvent event){
		if(Config.getConfig("jail.eventManager.pickupItem.enabled").equals("false"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player))event.setCancelled(true);
	}
	@EventHandler
	public void PlayerPortalEvent(PlayerPortalEvent event){
		if(Config.getConfig("jail.eventManager.portal.enabled").equals("false"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player)){
    		playerTeleportJail(player);
    		event.setCancelled(true);
		}
	}
	@EventHandler
	public void AsyncPlayerChatEvent(AsyncPlayerChatEvent event){
		if(Config.getConfig("jail.eventManager.chat.enabled").equals("false"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player)){
			//TODO 开关
    		playerTeleportJail(player);
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		if(Config.getConfig("jail.eventManager.command.enabled").equals("false"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player)){
			String cmd = event.getMessage();
			ArrayList<String> list = Config.getList("jail.eventManager.command.whiteList");
			//TODO 白名单
			if(list !=null){
				for(int i=0;i < list.size();){
					String whiteCmd = list.get(i);
					if(cmd.toLowerCase().contains(whiteCmd.toLowerCase()))return;
					i++;
				}
			}
    		playerTeleportJail(player);
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event){
		if(Config.getConfig("jail.eventManager.InteractEntity.enabled").equals("false"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player))event.setCancelled(true);
	}
	@EventHandler
	public void PlayerDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getEntityType().equals(EntityType.PLAYER)){
			if(Config.getConfig("jail.eventManager.underAttack.enabled").equals("false"))return;
			Player player = (Player) event.getEntity();
			if(isJailPlayer(player))event.setCancelled(true);
		}else if(event.getDamager().getType().equals(EntityType.PLAYER)){
			if(Config.getConfig("jail.eventManager.attack.enabled").equals("false"))return;
			Player player = (Player) event.getDamager();
			if(isJailPlayer(player))event.setCancelled(true);
		}
	}
	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent event){
		if(Config.getConfig("jail.eventManager.blockPlace.enabled").equals("false"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player))event.setCancelled(true);
	}
	@EventHandler
	public void BlockBreakEvent(BlockBreakEvent event){
		if(Config.getConfig("jail.eventManager.blockBreak.enabled").equals("false"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player))event.setCancelled(true);
	}
	@EventHandler
	public void PlayerBucketFillEvent(PlayerBucketFillEvent event){
		if(Config.getConfig("jail.eventManager.bucket.enabled").equals("false"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player))event.setCancelled(true);
	}
	@EventHandler
	public void PlayerBucketEmptyEvent(PlayerBucketEmptyEvent event){
		if(Config.getConfig("jail.eventManager.bucket.enabled").equals("false"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player))event.setCancelled(true);
	}
}

package vip.foxcraft.pvpaswantedmanager;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;

import vip.foxcraft.pvpaswantedmanager.Util.Config;
import vip.foxcraft.pvpaswantedmanager.Util.Message;

public class JailManager implements Listener {
	static public void teleport(Player player,Location location){
		if(PVPAsWantedManager.versionValue >= 1112){
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
		}
        player.teleport(location);
	}
	static public void playerJoinJail(Player player,Location location){
        
        double jailX = Integer.valueOf(Config.getConfig("jail.location.X"))+0.5;
        double jailY = Integer.valueOf(Config.getConfig("jail.location.Y"));
        double jailZ = Integer.valueOf(Config.getConfig("jail.location.Z"))+0.5;
        World jailWorld = Bukkit.getWorld(Config.getConfig("jail.location.World"));
        Location jail = new Location(jailWorld,jailX,jailY,jailZ);
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
        YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player.getName());
        double playerX = PlayerData.getInt("attribute.X");
        double playerY = PlayerData.getInt("attribute.Y");
        double playerZ = PlayerData.getInt("attribute.Z");
        World playerWorld = Bukkit.getWorld(PlayerData.getString("attribute.World"));
        Location playerLocatioin = new Location(playerWorld,playerX,playerY,playerZ);
        teleport(player,playerLocatioin);
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
        YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player.getName());
        if(PlayerData == null) return false;
        int playerJaiTimes = PlayerData.getInt("jail.times");
        if(playerJaiTimes > 0){
    		return true;
        }
        return false;
	}
	
	public static void surrendPlayer(Player player){
        YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player.getName());
        int value = PlayerData.getInt("jail.times");
		int times = PlayerData.getInt("wanted.points")*Integer.valueOf(Config.getConfig("timeTick.jailPlayerTimes").replace("min", "").replace("m", ""));
		if(value > 0){
			player.sendMessage(Message.getMsg("player.isAlreadyInJailMessage"));
			return;
		}
		if(times <1){
			player.sendMessage(Message.getMsg("player.notSurrendMessage"));
		}
		Location location = player.getLocation();
        int playerX = location.getBlockX();
        int playerY = location.getBlockY();
        int playerZ = location.getBlockZ();
        String playerWorld = location.getWorld().getName();
        PlayerData.set("attribute.X", playerX);
        PlayerData.set("attribute.Y", playerY);
        PlayerData.set("attribute.Z", playerZ);
        PlayerData.set("attribute.World", playerWorld);
        PlayerData.set("wanted.points", Integer.valueOf(0));
        PlayerData.set("jail.times", times);
        PVPAsWantedManager.onDeleteList(player.getName(), "WantedList");
        PVPAsWantedManager.onCreateList(player.getName(), "JailedList");
        PVPAsWantedManager.onSaveData(player.getName(), PlayerData);
		JailManager.playerJoinJail(player,location);
		player.sendMessage(Message.getMsg("player.surrendMessage",String.valueOf(times)));
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(isJailPlayer(player)){
			playerTeleportJail(player);;
			player.sendMessage(Message.getMsg("player.timeDeductionJailedMessage",PVPAsWantedManager.onLoadData(player.getName()).getString("jail.times")));
		}
	}
	
	@EventHandler
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		if(!Config.getConfig("jail.eventManager.dropItem.enabled").equals("true"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player)){
	        player.sendMessage(Message.getMsg("player.jailedeventMessage"));
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void PlayerPickupItemEvent(PlayerPickupItemEvent event){
		if(!Config.getConfig("jail.eventManager.pickupItem.enabled").equals("true"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player))event.setCancelled(true);
	}
	
	@EventHandler
	public void PlayerPortalEvent(PlayerPortalEvent event){
		if(!Config.getConfig("jail.eventManager.portal.enabled").equals("true"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player)){
    		playerTeleportJail(player);
    		event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void AsyncPlayerChatEvent(AsyncPlayerChatEvent event){
		if(!Config.getConfig("jail.eventManager.chat.enabled").equals("true"))return;
		Player player = event.getPlayer();
		if(isJailPlayer(player)){
    		playerTeleportJail(player);
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		if(!Config.getConfig("jail.eventManager.command.enabled").equals("true"))return;
		if(event.getPlayer() == null && !(event.getPlayer() instanceof Player)) return;
		Player player = event.getPlayer();
		if(isJailPlayer(player)){
			String cmd = event.getMessage().split(" ")[0].replace("/", "");
			ArrayList<String> list = Config.getList("jail.eventManager.command.whiteList");
			if(list !=null){
				for(int i=0;i < list.size();){
					if(cmd.equalsIgnoreCase(list.get(i)))return;
					i++;
				}
			}
    		playerTeleportJail(player);
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void PlayerDeathEvent(PlayerDeathEvent event){
		Player player = event.getEntity();
		if(isJailPlayer(player)){
    		event.setDeathMessage(null);
			player.spigot().respawn();
    		playerTeleportJail(player);
		}
	}
	
	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event) {
		if(!Config.getConfig("jail.eventManager.Interact.enabled").equals("true"))return;
			Player player = event.getPlayer();
			if(isJailPlayer(player)){
		        player.sendMessage(Message.getMsg("player.jailedeventMessage"));
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
		if(!Config.getConfig("jail.eventManager.Interact.enabled").equals("true"))return;
			Player player = event.getPlayer();
			if(isJailPlayer(player)){
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void PlayerDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(!Config.getConfig("jail.eventManager.damage.enabled").equals("true"))return;
		if(event.getDamager() instanceof Projectile){
			Projectile pro = (Projectile) event.getDamager();
			if(pro.getShooter() instanceof Player){
				if(event.getEntity() instanceof Player){
					Player player = (Player) event.getEntity();
					if(isJailPlayer(player)){
						if(pro.getFireTicks() >0){
							player.setFireTicks(0);
						}
						event.setCancelled(true);
					}
				}
			}
		}
		if(event.getDamager() instanceof Player){
			Player Damager = (Player) event.getDamager();
			if(isJailPlayer(Damager)){
				Damager.sendMessage(Message.getMsg("player.jailedeventMessage"));
				event.setCancelled(true);
			}
			if(event.getEntity() instanceof Player){
				Player player = (Player) event.getEntity();
				if(isJailPlayer(player)){
					event.setCancelled(true);
				}
			}
		}
	}
}

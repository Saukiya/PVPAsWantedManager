package vip.foxcraft.pvpaswantedmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import vip.foxcraft.pvpaswantedmanager.Util.Config;
import vip.foxcraft.pvpaswantedmanager.Util.Message;
import vip.foxcraft.pvpaswantedmanager.Util.Money;
import vip.foxcraft.pvpaswantedmanager.Util.Placeholders;
import vip.foxcraft.pvpaswantedmanager.Util.PlayerCommand;

public class PVPAsWantedManager extends JavaPlugin implements Listener
{
	HashMap<String,BukkitRunnable> RunMap = new HashMap<String,BukkitRunnable>();
	public static int versionValue;
	static public YamlConfiguration onLoadData(String name){
		File DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "PlayerData" + File.separator + name + ".yml");
		if(DataFile.exists()){
			YamlConfiguration PlayerData = new YamlConfiguration();
			try {PlayerData.load(DataFile);} catch (FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();} catch (InvalidConfigurationException e) {e.printStackTrace();}
			return PlayerData;
		}
		return null;
	}
	@SuppressWarnings("deprecation")
	public void sendTitle(Player player,String title,String subTitle,int arg2,int arg3,int arg4){
		if(PVPAsWantedManager.versionValue >= 1112){
			player.sendTitle(title, subTitle, arg2, arg3, arg4);
		}else if(PVPAsWantedManager.versionValue >= 188){
			player.sendTitle(title, subTitle);
			new BukkitRunnable(){
				@Override
				public void run() {
					player.resetTitle();
				}
			}.runTaskLaterAsynchronously(this, arg3);
		}
	}
	
	static public void onSaveData(String name, YamlConfiguration PlayerData){
		File DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "PlayerData" + File.separator + name + ".yml");
		try {PlayerData.save(DataFile);} catch (IOException e) {e.printStackTrace();}
	}
	
	static public Boolean isPlayerNovice(String player){
		int protectionValue = Integer.valueOf(onLoadData(player).getString("cumulativeOnlineTime"));
		int noviceProtectionTimes = Integer.valueOf(Config.getConfig("playerNoviceProtection.times").replace("min", "").replace("m",""));
		if(noviceProtectionTimes > protectionValue){
			if(InventoryManager.PVPList.contains(player))return false;
			return true;
		}
		return false;
		
	}
	@SuppressWarnings("deprecation")
	static public Boolean isPlayerOnline(String player){
		return Bukkit.getOfflinePlayer(player).isOnline();
		
	}
	
	static public void onCreateList(String player,String type){
		File DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "data.dat");
		YamlConfiguration Data = new YamlConfiguration();
		try {Data.load(DataFile);} catch (IOException | InvalidConfigurationException e) {e.printStackTrace();}
		ArrayList<String> List = (ArrayList<String>) Data.getStringList(type);
		List.add(player);
		Data.set(type, List);
		try {Data.save(DataFile);} catch (IOException e) {e.printStackTrace();}
	}
	
	static public void onDeleteList(String player,String type){
		File DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "data.dat");
		YamlConfiguration playerListData = new YamlConfiguration();
		try {playerListData.load(DataFile);} catch (IOException | InvalidConfigurationException e) {e.printStackTrace();}
		ArrayList<String> List = (ArrayList<String>) playerListData.getStringList(type);
		for(int i = 0; i < List.size();i++){
			if(List.get(i).equalsIgnoreCase(player)){
				List.remove(i);
				playerListData.set(type, List);
				try {playerListData.save(DataFile);} catch (IOException e) {e.printStackTrace();}
				return;
			}
		}
	}
	public void setPlayerLevel(Player player , int playerWantedPoints){
		if(player.hasPermission("pvpaswantedmanager.levelwhite"))return;
		if(!Config.getConfig("DeathPenalize.enabled").equals("true"))return;
		if(player.getExp() ==0 &&player.getLevel() == 0)return;
		YamlConfiguration PlayerData = onLoadData(player.getName());
		Double value = Double.valueOf(Config.getConfig("DeathPenalize.Level"));
		if(value !=1){
			DecimalFormat    df   = new DecimalFormat("######0.0");
			value = Double.valueOf(df.format(value*playerWantedPoints));
			String str = String.valueOf(value).replace(".", "-");
			playerWantedPoints = Integer.valueOf(str.split("-")[0]);
			float exp = Float.valueOf("0."+str.split("-")[1]);
			if(exp != 0){
				exp = player.getExp()-exp;
				if(exp < 0){
					playerWantedPoints ++;
					exp = 1+exp;
				}
				player.setExp(exp);
			}
			int level = player.getLevel() - playerWantedPoints;
			if(level<0){
				level=0;
				player.setExp(0);
			}
			player.setLevel(level);
			player.sendMessage(Message.getMsg("player.deathMessage",String.valueOf(value)));
		}else{
			int level = player.getLevel() - playerWantedPoints;
			if(level<0)level=0;
			player.setLevel(level);
			player.sendMessage(Message.getMsg("player.deathMessage",String.valueOf(playerWantedPoints)));
		}
		PlayerData.set("attribute.level", player.getLevel());
		onSaveData(player.getName(),PlayerData);
	}
	
	@Override
	public void onEnable(){
		String version = Bukkit.getBukkitVersion().split("-")[0].replace(".", "_");
    	Bukkit.getConsoleSender().sendMessage("[PVPAsWantedManager] §aServerVersion:" + version);
		if(version.split("_").length < 3)version += "0";
		versionValue = Integer.valueOf(version.replace("_", ""));
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new JailManager(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryManager(), this);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
        	new Placeholders(this).hook();
        	Bukkit.getConsoleSender().sendMessage("[PVPAsWantedManager] §aFind PlacholderAPI!");
        }
        
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
        	Money.setupEconomy();
        	Bukkit.getConsoleSender().sendMessage("[PVPAsWantedManager] §aFind Vault!");
            Config.loadConfig();
    		Message.loadMessage();
            Bukkit.getConsoleSender().sendMessage("[PVPAsWantedManager] §a通缉追捕 加载成功! 插件作者: §eSaukiya");
        }else{
        	Bukkit.getConsoleSender().sendMessage("[PVPAsWantedManager] §cPlease install Vault!");
        	Bukkit.getPluginManager().disablePlugin(this);
        }
        
		//检测PlayerData文件夹是否存在
        File DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "playerdata");
		if(!DataFile.exists()){
			DataFile.mkdirs();
		}
		
		//检测data.dat是否存在
        DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "data.dat");
		if(!DataFile.exists()){
			YamlConfiguration Data = new YamlConfiguration();
			ArrayList<String>WantedList = new ArrayList<String>();
			ArrayList<String>JailedList = new ArrayList<String>();
			Data.set("WantedList", WantedList);
			Data.set("JailedList", JailedList);
			try {Data.save(DataFile);} catch (IOException e) {e.printStackTrace();}
		}
		
	}

	public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
        if(label.equalsIgnoreCase("pawm") || label.equalsIgnoreCase("pvpaswantedmanager")){
                //判断是否是玩家
                if((sender instanceof Player)){
                    if(!sender.hasPermission("pvpaswantedmanager.use")){
                        sender.sendMessage(Message.getMsg("player.noPermissionMessage"));
                        return true;
                    }
                }
                //无参数
                if(args.length==0){
                	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6==========[&b PVPAsWantedManager&6 ]=========="));
                        for(java.lang.reflect.Method method : this.getClass().getDeclaredMethods()){
                                if(!method.isAnnotationPresent(PlayerCommand.class)){
                                        continue;
                                }
                                PlayerCommand sub=method.getAnnotation(PlayerCommand.class);
                                if(sender.hasPermission("pvpaswantedmanager." + sub.cmd())){
                                	if(!(sender instanceof Player)){
                                		if(sub.cmd().equals("open")||sub.cmd().equals("set")||sub.cmd().equals("setjail")){
                                            continue;
                                		}
                                	}
                                	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/"+ label + " "+sub.cmd()+"&6"+sub.arg()+"&7-:&e "+Message.getMsg("command."+ sub.cmd())));
                                }
                        }
                        return true;
                }
                for(java.lang.reflect.Method method:this.getClass().getDeclaredMethods()){
                        if(!method.isAnnotationPresent(PlayerCommand.class)){
                                continue;
                        }
                        PlayerCommand sub=method.getAnnotation(PlayerCommand.class);
                        if(!sub.cmd().equalsIgnoreCase(args[0])){
                                continue;
                        }
                        
                        try {
                                method.invoke(this, sender,args);
                        } catch (IllegalAccessException e) {
                                e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                        } catch (InvocationTargetException e) {
                                e.printStackTrace();
                        }
                        return true;
                }
                sender.sendMessage(Message.getMsg("command.NoCommand", args[0]));
            return true;
        }
        return false;
}

	//sender instanceof Player
	@PlayerCommand(cmd="open")
	public void onOpenGUI(CommandSender sender,String args[]){
		if(sender instanceof Player){
			Player player = (Player)sender;
			if(!player.hasPermission("pvpaswantedmanager." + args[0])) {
				player.sendMessage(Message.getMsg("player.noPermissionMessage"));
				return;
			}
			InventoryManager.openAsWantedGUI(player, 1);
		}else{
			sender.sendMessage(Message.getMsg("admin.ConsoleNotMessage"));
		}
	}
	

	@PlayerCommand(cmd="joinjail",arg = " <player> <times>")
	public void onJoinJail(CommandSender sender,String args[]){
		if(sender instanceof Player){
			if(!sender.hasPermission("pvpaswantedmanager." + args[0])) {
				sender.sendMessage(Message.getMsg("player.noPermissionMessage"));
				return;
			}
		}
		if(args.length <=2 ){
			sender.sendMessage(Message.getMsg("admin.joinJailCorrectionsMessage"));
			return;
		}
		String playerName = args[1];
		if(!Pattern.compile("[0-9]*").matcher(args[2]).matches()){
			sender.sendMessage(Message.getMsg("admin.wrongFormatMessage"));
			return;
		}
		int times = Integer.valueOf(args[2]);
		if(isPlayerOnline(playerName)){
			Player player = Bukkit.getPlayer(playerName);
	        YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player.getName());
	        int value = PlayerData.getInt("jail.times");
			if(value > 0){
				sender.sendMessage(Message.getMsg("admin.playerIsAlreadyInJailMessage"));
				return;
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
	        PlayerData.set("jail.times", times);
	        PVPAsWantedManager.onSaveData(player.getName(), PlayerData);
			JailManager.playerJoinJail(player,location);
			sender.sendMessage(Message.getMsg("admin.joinJailPlayerMessage", args[1],args[2]));
			player.sendMessage(Message.getMsg("player.jailedJoinMessage",args[2]));
		}else{
			sender.sendMessage(Message.getMsg("admin.playerOfflineMessage"));
		}
	}
	

	@PlayerCommand(cmd="quitjail",arg = " <player>")
	public void onQuitJail(CommandSender sender,String args[]){
		if(sender instanceof Player){
			if(!sender.hasPermission("pvpaswantedmanager." + args[0])) {
				sender.sendMessage(Message.getMsg("player.noPermissionMessage"));
				return;
			}
		}
		if(args.length <=1){
			sender.sendMessage(Message.getMsg("admin.quitJailCorrectionsMessage"));
			return;
		}
		String playerName = args[1];
		if(isPlayerOnline(playerName)){
			Player player = Bukkit.getPlayer(playerName);
			YamlConfiguration PlayerData = onLoadData(player.getName());
			int value = PlayerData.getInt("jail.times");
			if(value == 0){
				sender.sendMessage(Message.getMsg("admin.playerIsNotInJailMessage"));
				return;
			}
			PlayerData.set("jail.times", Integer.valueOf(0));
	        PlayerData.set("attribute.X", Integer.valueOf(0));
	        PlayerData.set("attribute.Y", Integer.valueOf(0));
	        PlayerData.set("attribute.Z", Integer.valueOf(0));
	        PlayerData.set("attribute.World", String.valueOf("world"));
			JailManager.playerQuitJail(player);
			onDeleteList(player.getName(),"JailedList");
	        PVPAsWantedManager.onSaveData(player.getName(), PlayerData);
			sender.sendMessage(Message.getMsg("admin.quitJailPlayerMessage", args[1]));
			player.sendMessage(Message.getMsg("player.jailedCancelMessage"));
		}else{
			sender.sendMessage(Message.getMsg("admin.playerOfflineMessage"));
		}
	}

	@PlayerCommand(cmd="setjail")
	public void onSetJail(CommandSender sender,String args[]){
		if(sender instanceof Player){
			if(!sender.hasPermission("pvpaswantedmanager." + args[0])) {
				sender.sendMessage(Message.getMsg("player.noPermissionMessage"));
				return;
			}
			Player player = (Player)sender;
			JailManager.playerSetJail(player);
		}else{
			sender.sendMessage(Message.getMsg("admin.ConsoleNotMessage"));
		}
	}

	@PlayerCommand(cmd="reload")
	public void onReloadPlugin(CommandSender sender,String args[]){
		if(sender instanceof Player){
			if(!sender.hasPermission("pvpaswantedmanager." + args[0])) {
				sender.sendMessage(Message.getMsg("player.noPermissionMessage"));
				return;
			}
		}
        Config.loadConfig();
		Message.loadMessage();
		sender.sendMessage(Message.getMsg("admin.reloadMessage"));
	}

	@PlayerCommand(cmd="set",arg = " <player>")
	public void onSetPointGUI(CommandSender sender,String args[]){
		if(sender instanceof Player){
			if(!sender.hasPermission("pvpaswantedmanager." + args[0])) {
				sender.sendMessage(Message.getMsg("player.noPermissionMessage"));
				return;
			}
			if(args.length <= 1){
				sender.sendMessage(Message.getMsg("admin.setPointsCorrectionsMessage"));
				return;
			}
			Player player = (Player) sender;
			String playerName = args[1];
			InventoryManager.openSetPlayerGui(player, playerName);
		}else{
			sender.sendMessage(Message.getMsg("admin.ConsoleNotMessage"));
		}
	}

	@PlayerCommand(cmd="setpoint",arg = " <player> <value>")
	public void onSetPoint(CommandSender sender,String args[]){
		if(sender instanceof Player){
			if(!sender.hasPermission("pvpaswantedmanager." + args[0])) {
				sender.sendMessage(Message.getMsg("player.noPermissionMessage"));
				return;
			}
		}
		if(args.length <3){
			sender.sendMessage(Message.getMsg("admin.wrongFormatMessage"));
			return;
		}
		
		YamlConfiguration PlayerData = onLoadData(args[1]);
		if(PlayerData == null){
			sender.sendMessage(Message.getMsg("admin.playerNullMessage"));
			return;
		}
		if(!Pattern.compile("[0-9]*").matcher(args[2]).matches()){
			sender.sendMessage(Message.getMsg("admin.wrongFormatMessage"));
			return;
		}
		int value = Integer.valueOf(args[2]);
		if(PlayerData.getInt("wanted.points")==0&& value > 0){
			PVPAsWantedManager.onCreateList(args[1], "WantedList");
		}else if(PlayerData.getInt("wanted.points")>0 && value ==0){
			PVPAsWantedManager.onDeleteList(args[1], "WantedList");
		}
		PlayerData.set("wanted.points", value);
		PVPAsWantedManager.onSaveData(args[1], PlayerData);
		sender.sendMessage(Message.getMsg("admin.EditPlayerDataMessage"));
		
	}

	@EventHandler
	public void onJoinPlayer(PlayerJoinEvent event){
		Player player = event.getPlayer();
		File DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "PlayerData" + File.separator + player.getName() +".yml");
		   if(!DataFile.exists()){
			   YamlConfiguration PlayerData = new YamlConfiguration();
			   //玩家PK值(这是主要的数值，重要)
			   PlayerData.set("wanted.points", Integer.valueOf(0));
			   //玩家累计PK值(用于什么"累计到多少获得奖励或特权""狂热PK玩家称号?""或者别的!")
			   PlayerData.set("wanted.cumulativePoints", Integer.valueOf(0));
			   //玩家最高PK值(作用不大，可能跟上一行一样)
			   PlayerData.set("wanted.highestPoints", Integer.valueOf(0));
			   //玩家坐牢值(蹲监狱用来计算多久出狱的)
			   PlayerData.set("jail.times", Integer.valueOf(0));
			   //玩家累计坐牢次数(这个值我也不知道我为什么要统计,,反正统计就好了)
			   PlayerData.set("jail.cumulativeNumber", Integer.valueOf(0));
			   //玩家目前通缉的目标
			   PlayerData.set("asWanted.target", String.valueOf("N/A"));
			   //玩家累计抓人的次数(用于什么"累计到多少获得奖励或特权""随机传送到被通缉玩家附近?""或者别的!")
			   PlayerData.set("asWanted.cumulativenumber", Integer.valueOf(0));
			   //玩家连续抓人的次数(用于什么"累计到多少获得奖励或特权""随机传送到被通缉玩家附近?""或者别的!")
			   PlayerData.set("asWanted.continuitynumber", Integer.valueOf(0));
			   //记录玩家等级，不解释
			   PlayerData.set("attribute.level", Integer.valueOf(0));
			   PlayerData.set("attribute.X", Integer.valueOf(0));
			   PlayerData.set("attribute.Y", Integer.valueOf(100));
			   PlayerData.set("attribute.Z", Integer.valueOf(0));
			   PlayerData.set("attribute.World", String.valueOf("world"));
			   PlayerData.set("cumulativeOnlineTime", Integer.valueOf(0));
			   onSaveData(player.getName(), PlayerData);
		  	   try{
		  		   PlayerData.save(DataFile);
		  	       Bukkit.getConsoleSender().sendMessage("[PVPAsWantedManager] 创建空的配置: " + DataFile.getPath());
		  	   }catch (Exception e){
	  	    	   player.sendMessage("§8[§6PVPAsWantedManager§8] §c创建配置 " + player.getName() +" 数据失败!");
		  	   }
	  	   }
        
        
        BukkitRunnable runnable = new BukkitRunnable(){
	        int i = 0;
	        int playerWantedTime = 0;
	        int targetTime = 1;
	        int JailTime = 1;
	        int CumulativeTime = 1;
	        @Override
	        public void run(){
		        int wantedPlayerTimeDeduction = Integer.valueOf(Config.getConfig("timeTick.wantedPlayerTimeDeduction").replace("min", "").replace("m",""))*2;
		        int targetTimeMessage = Integer.valueOf(Config.getConfig("timeTick.targetTimeMessage").replace("min", "").replace("m",""))*2;
		        YamlConfiguration PlayerData = onLoadData(player.getName());
		        int playerWantedPoints = PlayerData.getInt("wanted.points");
		        int playerJailTimes = PlayerData.getInt("jail.times");
		        String TargetName = PlayerData.getString("asWanted.target");
				int CumulativeOnlineTime = Integer.valueOf(PlayerData.getString("cumulativeOnlineTime"));
				if(i > 0){
					if(playerWantedPoints > 0){
						if(playerWantedTime >= wantedPlayerTimeDeduction){
							playerWantedPoints = playerWantedPoints -1;
							if(playerWantedPoints == 0){
								onDeleteList(player.getName(),"WantedList");
								player.sendMessage(Message.getMsg("player.returnZeroWantedPointMessage"));
								playerWantedTime = 0;
							}else{
								player.sendMessage(Message.getMsg("player.timeDeductionWantedPointMessage" , String.valueOf(playerWantedPoints)));
								playerWantedTime = 1;
							}
						}else{
							playerWantedTime++;
						}
					}
					//消除监狱时间
					if(playerJailTimes > 0){
						if(JailTime >= 2){
							playerJailTimes = playerJailTimes -1;
							if(playerJailTimes == 0){
						        PlayerData.set("attribute.X", Integer.valueOf(0));
						        PlayerData.set("attribute.Y", Integer.valueOf(0));
						        PlayerData.set("attribute.Z", Integer.valueOf(0));
						        PlayerData.set("attribute.World", String.valueOf("world"));
								JailManager.playerQuitJail(player);
								player.sendMessage(Message.getMsg("player.jailedCancelMessage"));
								onDeleteList(player.getName(),"JailedList");
								JailTime = 0;
							}else{
								player.sendMessage(Message.getMsg("player.timeDeductionJailedMessage" , String.valueOf(playerJailTimes)));
								JailTime = 1;
							}
						}else{
							JailTime++;
						}
					}
					//通知玩家目标的位置
					if(!String.valueOf(String.valueOf(TargetName)).equals("N/A")){
						YamlConfiguration TargetData = onLoadData(TargetName);
						if(TargetData !=null){
							if(TargetData.getInt("wanted.points") == 0){
								PlayerData.set("asWanted.target", String.valueOf("N/A"));
								PlayerData.set("asWanted.continuitynumber", Integer.valueOf(0));
								onDeleteList(TargetName,"WantedList");
								player.sendMessage(Message.getMsg("player.nullTargetMessage", TargetName));
							}else{
								if(targetTime >= targetTimeMessage){
									if(isPlayerOnline(TargetName)){
										Player Target = Bukkit.getPlayer(TargetName);
										String world = Target.getWorld().getName();
										String x = String.valueOf(Target.getLocation().getBlockX());
										String z = String.valueOf(Target.getLocation().getBlockZ());
										
										player.sendMessage(Message.getMsg( "player.onlineTargetMessage", TargetName, String.valueOf(TargetData.getInt("wanted.points")), x, z,  world));
									}else{
										player.sendMessage(Message.getMsg( "player.offlineTargetMessage", TargetName));
									}
									targetTime = 1;
								}else{
									targetTime++;
								}
								
							}
						}else{
							PlayerData.set("asWanted.target", String.valueOf("N/A"));
							onDeleteList(TargetName,"WantedList");
							player.sendMessage(Message.getMsg("player.nullTargetMessage", TargetName));
						}
					}
					if(CumulativeTime >= 2){
						CumulativeOnlineTime++;
						CumulativeTime = 1;
					}else{
						CumulativeTime++;
					}
					PlayerData.set("cumulativeOnlineTime", CumulativeOnlineTime);
					PlayerData.set("wanted.points", playerWantedPoints);
					PlayerData.set("jail.times", playerJailTimes);
					onSaveData(player.getName() , PlayerData);
				}else{
					i++;
				}
			}
		};
		RunMap.put(player.getName(), runnable);
		runnable.runTaskTimerAsynchronously(this,0,30*20);
	}


	@EventHandler
	public void onQuitPlayer(PlayerQuitEvent event){
		RunMap.get(event.getPlayer().getName()).cancel();
		RunMap.remove(event.getPlayer().getName());
	}
	

	@EventHandler
	public void onKilledPlayer(EntityDeathEvent event){
		if(event.getEntityType().equals(EntityType.PLAYER)){
			Player player = (Player) event.getEntity();
			//检测是否在世界白名单
			String world = player.getWorld().getName();
			ArrayList<String> worldWhiteList = Config.getList("worldWhiteList");
			for(int i=0;i<worldWhiteList.size();i++){
				if(worldWhiteList.get(i).equalsIgnoreCase(world))return;
			}
			YamlConfiguration PlayerData = onLoadData(player.getName());
			int playerWantedPoints = PlayerData.getInt("wanted.points");
			int playerJailCumulativeNumber = PlayerData.getInt("jail.cumulativeNumber");
			if(event.getEntity().getKiller() != null){
				Player killer = event.getEntity().getKiller();
				YamlConfiguration KillerData = onLoadData(killer.getName());
				int killerWantedPoints = KillerData.getInt("wanted.points");
				int killerWantedCumulativePoints = KillerData.getInt("wanted.cumulativePoints");
				int killerHighestPoints = KillerData.getInt("wanted.highestPoints");
				int killerAsWantedCumulativeNumber = KillerData.getInt("asWanted.cumulativenumber");
				int killerAsWantedContinuityNumber = KillerData.getInt("asWanted.continuitynumber");
				//当为被捕目标时?
				if(KillerData.getString("asWanted.target").equalsIgnoreCase(player.getName())){
					if(playerWantedPoints == 0){
						KillerData.set("asWanted.target", String.valueOf("N/A"));
						KillerData.set("asWanted.cumulativenumber", Integer.valueOf(0));
						//通缉失败 killer
						killer.sendMessage(Message.getMsg("player.nullTargetMessage"));
						onDeleteList(player.getName(),"WantedList");
						onSaveData(killer.getName() , KillerData);
						return;
					}
			        int jailPlayerTime = Integer.valueOf(Config.getConfig("timeTick.jailPlayerTimes").replace("min", "").replace("m",""))*playerWantedPoints;
			        
					PlayerData.set("jail.times", Integer.valueOf(jailPlayerTime));
					PlayerData.set("jail.cumulativeNumber", Integer.valueOf(playerJailCumulativeNumber + 1));
					PlayerData.set("wanted.points", Integer.valueOf(0));
					onDeleteList(player.getName(),"WantedList");
					onCreateList(player.getName(),"JailedList");
					//被抓时记录位置(此处无法用void保存)
					Location location = player.getLocation();
			        int playerX = location.getBlockX();
			        int playerY = location.getBlockY();
			        int playerZ = location.getBlockZ();
			        String playerWorld = location.getWorld().getName();
			        PlayerData.set("attribute.X", playerX);
			        PlayerData.set("attribute.Y", playerY);
			        PlayerData.set("attribute.Z", playerZ);
			        PlayerData.set("attribute.World", playerWorld);
					player.spigot().respawn();
					JailManager.playerJoinJail(player,location);
					// 被抓消息 player 
					PlayerDeathEvent e = (PlayerDeathEvent) event;
					e.setDeathMessage(null);
					if(Config.getConfig("asWantedArrestBroadCastMessage").equals("true"))Bukkit.broadcastMessage(Message.getMsg("player.asWantedArrestbroadcastMessage", player.getDisplayName(),killer.getDisplayName()));
					player.sendMessage(Message.getMsg("player.jailedJoinMessage",String.valueOf(jailPlayerTime)));
					sendTitle(player,Message.getMsg("title.jailedJoin"), Message.getMsg("title.jailedJoinSub",String.valueOf(jailPlayerTime)), 5, 80, 5);
					if(killerWantedPoints > 0){
						if(killerWantedPoints >= playerWantedPoints){
							KillerData.set("wanted.points", Integer.valueOf(killerWantedPoints - playerWantedPoints));
							killer.sendMessage(Message.getMsg("player.timeDeductionWantedPointMessage", String.valueOf(killerWantedPoints - playerWantedPoints)));
						}else{
							KillerData.set("wanted.points", Integer.valueOf(0));
							onDeleteList(killer.getName(),"WantedList");
							killer.sendMessage(Message.getMsg("player.returnZeroWantedPointMessage"));
						}
					}
					KillerData.set("asWanted.target", String.valueOf("N/A"));
					KillerData.set("asWanted.cumulativenumber", Integer.valueOf(killerAsWantedCumulativeNumber + 1));
					KillerData.set("asWanted.continuitynumber", Integer.valueOf(killerAsWantedContinuityNumber + 1));
					double money = 0D;
					int value = 0;
					Double taskRewardMoney = Double.valueOf(Config.getConfig("TaskReward.money"));
					double TaskRewardBasicMoney = Double.valueOf(Config.getConfig("TaskReward.basicMoney"));
					if(killerWantedPoints <= playerWantedPoints){
						value= playerWantedPoints - killerWantedPoints;
					}
					money = (value+playerWantedPoints*0.25)*taskRewardMoney+TaskRewardBasicMoney;
					
					Money.give(killer.getName(), money);
					sendTitle(killer,Message.getMsg("title.asWantedArrest"), Message.getMsg("title.asWantedArrestSub",String.valueOf(money)), 5, 40, 5);
					killer.sendMessage(Message.getMsg("player.asWantedArrestMessage",player.getName(),String.valueOf(money)));
					
					
				}else{
					if(playerWantedPoints>0)setPlayerLevel(player, playerWantedPoints);
					if(!killer.hasPermission("pvpaswantedmanager." + "wantedwhite")){
						if(killerWantedPoints == 0){
							onCreateList(killer.getName(),"WantedList");
						}
						killer.sendMessage(Message.getMsg("player.newWantedMessage",player.getDisplayName(),String.valueOf(killerWantedPoints+1)));
						KillerData.set("wanted.points", Integer.valueOf(killerWantedPoints + 1));
						KillerData.set("wanted.cumulativePoints", Integer.valueOf(killerWantedCumulativePoints + 1));
						if(killerWantedPoints + 1 >= killerHighestPoints){
							KillerData.set("wanted.highestPoints", Integer.valueOf(killerHighestPoints + 1));
						}
					}
				}
				onSaveData(player.getName(), PlayerData);
				onSaveData(killer.getName(), KillerData);
			}else{
				if(playerWantedPoints>0)setPlayerLevel(player, playerWantedPoints);
			}
			}
		}
	
	//根据PK值修改经验加成
	@EventHandler
	public void onPlayerExpChange(PlayerExpChangeEvent event){
		Player player = event.getPlayer();
		YamlConfiguration PlayerData = onLoadData(player.getName());
		PlayerData.set("attribute.level", player.getLevel());
		onSaveData(player.getName(), PlayerData);
		if(Config.getConfig("extraExp.enabled").equals("true")){
			int playerWantedPoints = PlayerData.getInt("wanted.points");
			if(playerWantedPoints > 0){
				int exp = event.getAmount();
				int value = Integer.valueOf(Config.getConfig("extraExp.pointsValue").replaceAll("%", ""));
				PlayerData.set("attribute.level", player.getLevel());
				int addExp = Integer.valueOf(String.valueOf(exp*playerWantedPoints*value/100));
				event.setAmount(addExp + exp);
				if(getConfig().getBoolean("extraExp.message") == true)
				player.sendMessage(Message.getMsg("player.expMessage", String.valueOf(addExp), String.valueOf(exp), String.valueOf(exp+addExp)));
				onSaveData(player.getName(), PlayerData);
			}
		}
	}
	//新手保护
	@EventHandler
	public void PlayerDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(Config.getConfig("playerNoviceProtection.enabled").equals("false"))return;
		String times = Config.getConfig("playerNoviceProtection.times").replace("min", "").replace("m", "");
		if(event.getDamager() instanceof Arrow){
			Arrow arrow = (Arrow) event.getDamager();
			if(arrow.getShooter() instanceof Player && event.getEntity() instanceof Player){
				Player Damager = (Player) arrow.getShooter();
				Player player = (Player) event.getEntity();
				if(isPlayerNovice(player.getName())){
					event.setCancelled(true);
					sendTitle(Damager,"§c✘", "", 1, 35, 1);
					Damager.sendMessage(Message.getMsg("player.pvpProtectMessage2",player.getDisplayName()));
				}
				if(isPlayerNovice(Damager.getName())){
					event.setCancelled(true);
					sendTitle(Damager,"§c✘", "", 1, 35, 1);
					Damager.sendMessage(Message.getMsg("player.pvpProtectMessage1",times));
				}
			}
		}
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
			Player player = (Player) event.getEntity();
			Player Damager = (Player) event.getDamager();
			if(isPlayerNovice(player.getName())){
				event.setCancelled(true);
				sendTitle(Damager,"§c✘", "", 1, 35, 1);
				Damager.sendMessage(Message.getMsg("player.pvpProtectMessage2",player.getDisplayName()));
			}
			if(isPlayerNovice(Damager.getName())){
				event.setCancelled(true);
				sendTitle(Damager,"§c✘", "", 1, 35, 1);
				Damager.sendMessage(Message.getMsg("player.pvpProtectMessage1",times));
			}
		}
	}
	
	@Override
	public void onDisable(){
        Bukkit.getConsoleSender().sendMessage("[PVPAsWantedManager] §a通缉追捕 插件关闭! 插件作者: §eSaukiya");
	}
}

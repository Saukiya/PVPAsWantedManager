package vip.foxcraft.pvpaswantedmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

import vip.foxcraft.pvpaswantedmanager.PlayerCommand;
import vip.foxcraft.pvpaswantedmanager.Util.Placeholders;




public class PVPAsWantedManager extends JavaPlugin implements Listener{
	static File DataFile;

	HashMap<String,BukkitRunnable> RunMap = new HashMap<String,BukkitRunnable>();

	static public YamlConfiguration onLoadData(String name){
		DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "PlayerData" + File.separator + name + ".yml");
		if(DataFile.exists()){
			YamlConfiguration PlayerData = new YamlConfiguration();
			try {PlayerData.load(DataFile);} catch (FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();} catch (InvalidConfigurationException e) {e.printStackTrace();}
			return PlayerData;
		}
		return null;
		
	}
	
	
	static public void onSaveData(String name, YamlConfiguration PlayerData){
		DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "PlayerData" + File.separator + name + ".yml");
		try {PlayerData.save(DataFile);} catch (IOException e) {e.printStackTrace();}
	}
	
	@SuppressWarnings("deprecation")
	static public Boolean EditMoney(String name,int value){
		Double money = 0.0D;
		try {money = Economy.getMoney(name);} catch (UserDoesNotExistException e) {e.printStackTrace();}
		if(money >= value){
			money = money - value;
			try {Economy.setMoney(name, money);} catch (NoLoanPermittedException e) {e.printStackTrace();} catch (UserDoesNotExistException e) {e.printStackTrace();}
			return true;
		}else{
			return false;
		}
	}
	@SuppressWarnings("deprecation")
	static public int GetMoney(String name){
		int money = 0;
		try {money = (int) Economy.getMoney(name);} catch (UserDoesNotExistException e) {e.printStackTrace();}
		return money;
	}
	
	static public Boolean isPlayerOnline(String player){
		return Bukkit.getOfflinePlayer(player).isOnline();
	}
	
	static public void onCreateList(String player,String type){
		DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "data.dat");
		YamlConfiguration Data = new YamlConfiguration();
		try {Data.load(DataFile);} catch (IOException | InvalidConfigurationException e) {e.printStackTrace();}
		ArrayList<String> List = (ArrayList<String>) Data.getStringList(type);
		List.add(player);
		Data.set(type, List);
		try {Data.save(DataFile);} catch (IOException e) {e.printStackTrace();}
	}
	
	static public void onDeleteList(String player,String type){
		DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "data.dat");
		YamlConfiguration playerListData = new YamlConfiguration();
		try {playerListData.load(DataFile);} catch (IOException | InvalidConfigurationException e) {e.printStackTrace();}
		ArrayList<String> List = (ArrayList<String>) playerListData.getStringList(type);
		for(int i = 0; i < List.size();i++){
			if(List.get(i).equals(player)){
				List.remove(i);
				playerListData.set(type, List);
				try {playerListData.save(DataFile);} catch (IOException e) {e.printStackTrace();}
				return;
			}
		}
	}
	public void setPlayerLevel(Player player , int playerWantedPoints){
		int level = player.getLevel() - playerWantedPoints;
		player.setLevel(level);
		player.sendMessage(Message.getMsg("player.deathMessage",String.valueOf(level)));
	}



	
	@Override
	public void onEnable(){
		
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new JailManager(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryManager(), this);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
        	new Placeholders(this).hook();
        	Bukkit.getConsoleSender().sendMessage("[PVPAsWantedManager] Find PlacholderAPI!");
        }
		//检测PlayerData文件夹是否存在
		DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "playerdata");
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
			//TODO 记录正在坐牢的玩家
			Data.set("JailedList", JailedList);
			try {Data.save(DataFile);} catch (IOException e) {e.printStackTrace();}
		}
		
        Config.loadConfig();
		Message.loadMessage();
        Bukkit.getConsoleSender().sendMessage("§8[§6PVPAsWantedManager§8] §a通缉追捕 加载成功! 插件作者: §eSaukiya");
	}

	public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
        if(label.equalsIgnoreCase("pawm") || label.equalsIgnoreCase("pvpaswantedmanager")){
                //判断是否是玩家
                if(!(sender instanceof Player)){
                        sender.sendMessage("只能在游戏里执行此命令！");
                        return true;
                }
                //判断是否有权限
                if(!sender.hasPermission("pvpaswantedmanager.use")){
                        sender.sendMessage(Message.getMsg("player.noPermissionMessage"));
                        return true;
                }
                //强转对象
                Player p=(Player) sender;
                //
                //无参数
                if(args.length==0){
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6==========[&b PVPAsWantedManager&6 ]=========="));
                        for(java.lang.reflect.Method method : this.getClass().getDeclaredMethods()){
                                if(!method.isAnnotationPresent(PlayerCommand.class)){
                                        continue;
                                }
                                PlayerCommand sub=method.getAnnotation(PlayerCommand.class);
                                if(p.hasPermission("pvpaswantedmanager." + sub.cmd())){
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/pawm "+sub.cmd()+" &6"+sub.arg()+"&7-:&3 "+sub.des()));
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
                                method.invoke(this, p,args);        
                        } catch (IllegalAccessException e) {
                                e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                        } catch (InvocationTargetException e) {
                                e.printStackTrace();
                        }
                        return true;
                }
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a未找到此子命令:&c"+args[0]));
                return true;
        }
        return false;
}

	//sender instanceof Player
	@PlayerCommand(cmd="open",des ="打开通缉菜单")
	public void onOpenGUI(CommandSender sender,String args[]){
		if(!sender.hasPermission("pvpaswantedmanager." + args[0])) {
			sender.sendMessage(Message.getMsg("player.noPermissionMessage"));
			return;
		}
		if(sender instanceof Player){
			Player player = (Player)sender;
			InventoryManager.openAsWantedGUI(player, 1);
		}else{
			sender.sendMessage("控制台不允许执行此指令");
		}
	}
	

	@PlayerCommand(cmd="joinjail",arg = "<player>",des ="使玩家入狱")
	public void onJoinJail(Player sender,String args[]){
		if(!sender.hasPermission("pvpaswantedmanager." + args[0])) {
			sender.sendMessage(Message.getMsg("player.noPermissionMessage"));
			return;
		}
		if(String.valueOf(args.length).equals("1")){
			sender.sendMessage(Message.getMsg("admin.joinJailCorrectionsMessage"));
			return;
		}
		String playerName = args[1];
		if(isPlayerOnline(playerName)){
			Player player = Bukkit.getPlayer(playerName);
			Location location = player.getLocation();
	        YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player.getName());
	        int playerX = location.getBlockX();
	        int playerY = location.getBlockY();
	        int playerZ = location.getBlockZ();
	        String playerWorld = location.getWorld().getName();
	        PlayerData.set("attribute.X", playerX);
	        PlayerData.set("attribute.Y", playerY);
	        PlayerData.set("attribute.Z", playerZ);
	        PlayerData.set("attribute.World", playerWorld);
	        PVPAsWantedManager.onSaveData(player.getName(), PlayerData);
			JailManager.playerJoinJail(player,location);
		}else{
			sender.sendMessage("玩家必须是在线状态!");
		}
	}
	

	@PlayerCommand(cmd="quitjail",des ="使玩家出狱")
	public void onQuitJail(Player sender,String args[]){
		if(!sender.hasPermission("pvpaswantedmanager." + args[0])) {
			sender.sendMessage(Message.getMsg("player.noPermissionMessage"));
			return;
		}
		if(String.valueOf(args.length).equals("1")){
			sender.sendMessage(Message.getMsg("admin.quitjailCorrectionsMessage"));
			return;
		}
		String playerName = args[1];
		if(isPlayerOnline(playerName)){
			Player player = Bukkit.getPlayer(playerName);
			JailManager.playerQuitJail(player);
		}else{
			sender.sendMessage(Message.getMsg("admin.playerOfflineMessage"));
		}
	}

	@PlayerCommand(cmd="setjail",des ="设置监狱位置")
	public void onSetJail(Player sender,String args[]){
		if(!sender.hasPermission("pvpaswantedmanager." + args[0])) {
			sender.sendMessage(Message.getMsg("player.noPermissionMessage"));
			return;
		}
		JailManager.playerSetJail(sender);
	}

	@PlayerCommand(cmd="reload",des ="重载插件配置")
	public void onReloadPlugin(Player sender,String args[]){
		if(!sender.hasPermission("pvpaswantedmanager." + args[0])) {
			sender.sendMessage(Message.getMsg("player.noPermissionMessage"));
			return;
		}
        Config.loadConfig();
		Message.loadMessage();
		sender.sendMessage(Message.getMsg("admin.reloadMessage"));
	}

	@PlayerCommand(cmd="set",des ="<player> 更改玩家的点数")
	public void onSetPoint(Player sender,String args[]){
		if(!sender.hasPermission("pvpaswantedmanager." + args[0])) {
			sender.sendMessage(Message.getMsg("player.noPermissionMessage"));
			return;
		}

		if(String.valueOf(args.length).equals("1")){
			sender.sendMessage(Message.getMsg("admin.setPointsCorrectionsMessage"));
			return;
		}
		String playerName = args[1];
		InventoryManager.openSetPlayerGui(sender, playerName);
	}

	@PlayerCommand(cmd="bal",des ="测试-查看自己的金钱")
	public void bal(Player sender,String args[]) throws UserDoesNotExistException{
		if(!sender.hasPermission("pvpaswantedmanager." + args[0])) {
			sender.sendMessage(Message.getMsg("player.noPermissionMessage"));
			return;
		}
			sender.sendMessage(""+Economy.getMoney(sender.getName()));
	}

	@EventHandler
	public void onJoinPlayer(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(player.getName().equals("false")){
			player.kickPlayer("Ban");
			return;
		}
		DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "PlayerData" + File.separator + player.getName() +".yml");
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
			   PlayerData.set("attribute.Y", Integer.valueOf(0));
			   PlayerData.set("attribute.Z", Integer.valueOf(0));
			   PlayerData.set("attribute.World", String.valueOf("world"));
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
	        int targetTime = 0;
	        int JailTime = 1;
	        int wantedPlayerTimeDeduction = Integer.valueOf(Config.getConfig("timeTick.wantedPlayerTimeDeduction").replace("min", ""))*2;
	        int targetTimeMessage = Integer.valueOf(Config.getConfig("timeTick.targetTimeMessage").replace("min", ""))*2;
	        @Override
	        public void run(){
		        YamlConfiguration PlayerData = onLoadData(player.getName());
		        int playerWantedPoints = PlayerData.getInt("wanted.points");
		        int playerJailTimes = PlayerData.getInt("jail.times");
		        String TargetName = PlayerData.getString("asWanted.target");
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
					if(playerJailTimes > 0){
						if(JailTime >= 2){
							playerJailTimes = playerJailTimes -1;
							if(playerJailTimes == 0){
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
					if(!String.valueOf(String.valueOf(TargetName)).equals("N/A")){
						YamlConfiguration TargetData = onLoadData(TargetName);
						if(TargetData.getInt("wanted.points") == 0){
							PlayerData.set("asWanted.target", String.valueOf("N/A"));
							PlayerData.set("asWanted.continuitynumber", Integer.valueOf(0));
							player.sendMessage(Message.getMsg("player.nullTargetMessage", TargetName));
						}else{
							if(targetTime >= targetTimeMessage){
								if(isPlayerOnline(TargetName) == true){
									Player Target = Bukkit.getPlayer(TargetName);
									String world = Target.getWorld().getName();
									String x = String.valueOf(Target.getLocation().getX());
									String z = String.valueOf(Target.getLocation().getZ());
									
									player.sendMessage(Message.getMsg( "player.onlineTargetMessage", TargetName, String.valueOf(TargetData.getInt(TargetName + ".wanted.points")), x, z,  world));
								}else{
									player.sendMessage(Message.getMsg( "player.offlineTargetMessage", TargetName));
								}
								targetTimeMessage = 0;
							}else{
								targetTimeMessage++;
							}
						}
					}
					
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
				if(KillerData.getString("asWanted.target").toLowerCase().equals(player.getName().toLowerCase())){
					if(playerWantedPoints == 0){
						KillerData.set("asWanted.target", String.valueOf("N/A"));
						KillerData.set("asWanted.cumulativenumber", Integer.valueOf(0));
						//通缉失败 killer
						killer.sendMessage(Message.getMsg("player.nullTargetMessage"));
						onDeleteList(player.getName(),"WantedList");
						onSaveData(killer.getName() , KillerData);
						return;
					}
			        int jailPlayerTime = Integer.valueOf(Config.getConfig("timeTick.jailPlayerTimeDeduction").replace("min", ""))*playerWantedPoints;
			        
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
					player.sendMessage(Message.getMsg("player.jailedJoinMessage",String.valueOf(jailPlayerTime)));
					
					if(killerWantedPoints >= playerWantedPoints){
						KillerData.set("wanted.points", Integer.valueOf(killerWantedPoints - playerWantedPoints));
					}else{
						KillerData.set("wanted.points", Integer.valueOf(0));
						onDeleteList(killer.getName(),"WantedList");
					}
					KillerData.set("asWanted.target", String.valueOf("N/A"));
					KillerData.set("asWanted.cumulativenumber", Integer.valueOf(killerAsWantedCumulativeNumber + 1));
					KillerData.set("asWanted.continuitynumber", Integer.valueOf(killerAsWantedContinuityNumber + 1));
					//TODO 通缉奖励 killer
					double value = 0.0D;
					Double taskRewardMoney = Double.valueOf(Config.getConfig("TaskReward.money"));
					if(killerWantedPoints >= playerWantedPoints){
						value= playerWantedPoints/4*taskRewardMoney;
					}else{
						value= ((playerWantedPoints-killerWantedPoints)+playerWantedPoints/4)*taskRewardMoney;
						
					}
					int money = (int)value*-1;
					EditMoney(killer.getName(),money);

					Double taskRewardExp = Double.valueOf(Config.getConfig("TaskReward.exp"));
					value = (player.getLevel()/killer.getLevel()+1)*taskRewardExp;
					int exp = (int)value;
					killer.setLevel(exp+killer.getLevel());
					killer.sendMessage(Message.getMsg("player.asWantedArrestMessage",player.getName(),String.valueOf(money*-1),String.valueOf(exp)));
					
					
				}else{
					if(killerWantedPoints == 0){
						onCreateList(killer.getName(),"WantedList");
					}
					killer.sendMessage(Message.getMsg("player.newWantedMessage",player.getDisplayName(),String.valueOf(killerWantedPoints+1)));
					KillerData.set("wanted.points", Integer.valueOf(killerWantedPoints + 1));
					KillerData.set("wanted.cumulativePoints", Integer.valueOf(killerWantedCumulativePoints + 1));
					if(killerWantedPoints + 1 >= killerHighestPoints){
						KillerData.set("wanted.highestPoints", Integer.valueOf(killerHighestPoints + 1));
					}
					if(playerWantedPoints>0)setPlayerLevel(player, playerWantedPoints);
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
		if(getConfig().getBoolean("extraExp.enabled") == true){
			int playerWantedPoints = PlayerData.getInt("wanted.points");
			if(playerWantedPoints > 0){
				int exp = event.getAmount();
				int value = Integer.valueOf(Config.getConfig("extraExp.pointsValue").replaceAll("%", ""));
				PlayerData.set("attribute.level", player.getLevel());
				int addExp = Integer.valueOf(String.valueOf(exp*playerWantedPoints*value/100));
				event.setAmount(addExp + exp);
				if(getConfig().getBoolean("extraExp.message") == true)
				player.sendMessage(Message.getMsg("player.expMessage", String.valueOf(addExp), String.valueOf(Integer.valueOf(playerWantedPoints*value) + "%")));
				onSaveData(player.getName(), PlayerData);
			}
		}
	}

	
	@Override
	public void onDisable(){
        Bukkit.getConsoleSender().sendMessage("§8[§6PVPAsWantedManager§8] §a通缉追捕 插件关闭! 插件作者: §eSaukiya");
	}
	
}

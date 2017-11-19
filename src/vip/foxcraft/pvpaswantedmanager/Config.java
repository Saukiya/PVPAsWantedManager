package vip.foxcraft.pvpaswantedmanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	private static YamlConfiguration config;
	static File configFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "config.yml");
	
	static public void createConfig(){
        Bukkit.getConsoleSender().sendMessage("§8[§6PVPAsWantedManager§8] §cCreate Config.yml");
		config = new YamlConfiguration();//
		config.set("language", String.valueOf("CN"));
		//TODO false TaskRewardGoldCoin
		config.set("CancellAsWantedTarget.enabled", Boolean.valueOf(true));
		config.set("CancellAsWantedTarget.money", Integer.valueOf(100));
		config.set("TaskReward.money", Integer.valueOf(500));
		config.set("TaskReward.exp", Integer.valueOf(1));
		config.set("asWantedGui.ID.glass", String.valueOf("160:15"));
		config.set("asWantedGui.ID.pageDown", Integer.valueOf("262"));
		config.set("asWantedGui.ID.pageUp", Integer.valueOf("262"));
		config.set("asWantedGui.ID.jailInfo", Integer.valueOf("347"));
		config.set("asWantedGui.ID.quit", Integer.valueOf("166"));
		config.set("asWantedGui.ID.cancellTarget", Integer.valueOf("352"));
		config.set("timeTick.wantedPlayerTimeDeduction", String.valueOf("30min"));
		config.set("timeTick.jailPlayerTimeDeduction", String.valueOf("20min"));
		config.set("timeTick.targetTimeMessage", String.valueOf("15min"));
		config.set("extraExp.enabled", Boolean.valueOf(false));
		config.set("extraExp.message", Boolean.valueOf(true));
		config.set("extraExp.pointsValue", String.valueOf("10%"));
		config.set("jail.eventManager.attack.enabled",Boolean.valueOf(true));
		config.set("jail.eventManager.blockPlace.enabled",Boolean.valueOf(true));
		config.set("jail.eventManager.blockBreak.enabled",Boolean.valueOf(true));
		config.set("jail.eventManager.bucket.enabled",Boolean.valueOf(true));
		config.set("jail.eventManager.command.enabled",Boolean.valueOf(true));
		config.set("jail.eventManager.chat.enabled",Boolean.valueOf(true));
		config.set("jail.eventManager.dropItem.enabled",Boolean.valueOf(true));
		config.set("jail.eventManager.InteractEntity.enabled",Boolean.valueOf(true));
		config.set("jail.eventManager.joinServer.enabled",Boolean.valueOf(true));
		config.set("jail.eventManager.pickupItem.enabled",Boolean.valueOf(true));
		config.set("jail.eventManager.portal.enabled",Boolean.valueOf(true));
		config.set("jail.eventManager.underAttack.enabled",Boolean.valueOf(true));
		ArrayList<String> listCmd = new ArrayList<String>();
		listCmd.add("Testcmd");
		config.set("jail.eventManager.command.whiteList", listCmd);
		config.set("jail.location.X", Integer.valueOf(0));
		config.set("jail.location.Y", Integer.valueOf(80));
		config.set("jail.location.Z", Integer.valueOf(0));
		config.set("jail.location.World", String.valueOf("world"));
		try {config.save(configFile);} catch (IOException e) {e.printStackTrace();}
		config = new YamlConfiguration();
		try {config.load(configFile);} catch (IOException | InvalidConfigurationException e) {e.printStackTrace();Bukkit.getConsoleSender().sendMessage("§8[§6PVPAsWantedManager§8] §a读取config时发生错误");}
	}
	
	static public void loadConfig(){
		if(!configFile.exists()){
			createConfig();
			return;
		}else{
	        Bukkit.getConsoleSender().sendMessage("§8[§6PVPAsWantedManager§8] §aFind Config.yml");
		}
		config = new YamlConfiguration();
		try {config.load(configFile);} catch (IOException | InvalidConfigurationException e) {e.printStackTrace();Bukkit.getConsoleSender().sendMessage("§8[§6PVPAsWantedManager§8] §a读取config时发生错误");}
	}

	public static String getConfig(String loc){
		String raw = config.getString(loc);
		if(raw == null || raw.isEmpty()){
			createConfig();
			raw = config.getString(loc);
			return raw;
		}
		if(raw.contains("min")){
			raw = raw.replace("min", "");
		}else if(raw.contains("%")){
			raw = raw.replace("%", "");
		}
		
		return raw;
	}
	public static ArrayList<String> getList(String loc){
		ArrayList<String> list = (ArrayList<String>) config.getStringList(loc);
		if (list == null || list.isEmpty()) {
			createConfig();
			list = (ArrayList<String>) config.getStringList(loc);
			return list;
		}
		
		return list;
	}
	
	
	public static void setConfig(String loc , Object arg){
		config.set(loc, arg);
		try {config.save(configFile);} catch (IOException e) {e.printStackTrace();}
	}
}

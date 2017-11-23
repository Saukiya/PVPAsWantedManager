package vip.foxcraft.pvpaswantedmanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class Message {
	public static PVPAsWantedManager plugin;
	private static YamlConfiguration messages;
	
	static public void createCNMessage(){
		File messageFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "message.yml");
        Bukkit.getConsoleSender().sendMessage("§8[§6PVPAsWantedManager§8] §cCreate Message.yml");
		messages = new YamlConfiguration();
		messages.set("asWantedGui.guiName", String.valueOf("&4&l通缉任务列表"));
		messages.set("asWantedGui.wantedSkull.Name", String.valueOf("&c&o&l{0}"));
		messages.set("asWantedGui.wantedSkull.Online", String.valueOf("&2在线"));
		messages.set("asWantedGui.wantedSkull.Offline", String.valueOf("&c离线"));
        ArrayList<String> skullLore = new ArrayList<String>();
        skullLore.add("&3PK值: &7{0}");
        skullLore.add("&3等级: &7{1}");
        skullLore.add("&3状态: &7{2}");
        skullLore.add("                         ");
        skullLore.add("&e奖励: &6&l{3}&a金币 {4}PK值");
        skullLore.add("&c点击通缉 &c✘");
		messages.set("asWantedGui.wantedSkull.Lore", skullLore);
		messages.set("asWantedGui.wantedSkull.null", String.valueOf("&7&o暂时没有通缉任务噢~"));
		messages.set("asWantedGui.pageDownName", String.valueOf("&e上一页 第{0}页"));
		messages.set("asWantedGui.pageUpName", String.valueOf("&e下一页 第{0}页"));
		messages.set("asWantedGui.gettargetMessage", String.valueOf("&8[&c通缉&8] &7你正在通缉 &c{0} &7玩家! 将他击杀即可完成任务!"));
		messages.set("asWantedGui.hastargetMessage", String.valueOf("&8[&c通缉&8] &7你已经在通缉 &c{0} &7玩家了!"));
		messages.set("asWantedGui.info.Name", String.valueOf("&6&l你的信息"));
		ArrayList<String> infoLore = new ArrayList<String>();
		infoLore.add("&3PK值: &7{0}");
		infoLore.add("&3累计PK值: &7{1}");
		infoLore.add("&3最高PK值: &7{2}");
		infoLore.add("&3累计坐牢次数: &7{3}");
		infoLore.add("&3通缉目标: &7{4}");
		infoLore.add("&3累计通缉次数: &7{5}");
		infoLore.add("&3连续通缉次数: &7{6}");
		messages.set("asWantedGui.info.Lore", infoLore);
		messages.set("asWantedGui.info.exp", String.valueOf("&6经验加成: &a{0}%"));
		messages.set("asWantedGui.jailInfo.Name", String.valueOf("&l监狱时间"));
		ArrayList<String> jailInfoLore = new ArrayList<String>();
		jailInfoLore.add("&6剩余时间: &c{0}分钟");
		jailInfoLore.add("&6预计时间: &6{1}&7PK值*&6{2}&7分钟= &c{3}&7分钟");
		messages.set("asWantedGui.jailInfo.Lore", jailInfoLore);
		messages.set("asWantedGui.quit", String.valueOf("&7&l退出菜单"));
		messages.set("asWantedGui.cancellTarget.name", String.valueOf("&l放弃任务"));
		ArrayList<String> cancellTargetLore = new ArrayList<String>();
		cancellTargetLore.add("&6需要支付: &7对方&6{0}&7PK值*&6{1}&7金币= &c{2}&7金币");
		messages.set("asWantedGui.cancellTarget.lore", cancellTargetLore);
		
		messages.set("setPlayerGui.guiName", String.valueOf("&9&l玩家点数管理"));
		messages.set("setPlayerGui.wanted.Name", String.valueOf("&c&lPK点数"));
		ArrayList<String> wantedLore = new ArrayList<String>();
		wantedLore.add("&3PK值: &7{0}");
		wantedLore.add("&3左键&e-1 &3右键&e+1");
		wantedLore.add("&3Shift+左键 &c清零");
		messages.set("setPlayerGui.wanted.Lore", wantedLore);
		messages.set("setPlayerGui.jail.Name", String.valueOf("&c&l监狱点数"));
		ArrayList<String> jailLore = new ArrayList<String>();
		jailLore.add("&3监狱值: &7{0}");
		jailLore.add("&3左键&e-1 &3右键&e+1");
		jailLore.add("&3Shift+左键 &c清零");
		messages.set("setPlayerGui.jail.Lore", jailLore);
		messages.set("setPlayerGui.asWanted.Name", String.valueOf("&c&l通缉目标"));
		ArrayList<String> asWantedLore = new ArrayList<String>();
		asWantedLore.add("&3目标: &7{0}");
		asWantedLore.add("&3左键 &c清空");
		messages.set("setPlayerGui.asWanted.Lore", asWantedLore);
		messages.set("player.newWantedMessage", String.valueOf("&8[&4击杀&8] &7你击杀了 &6{0}&7! PK值达到 &e{1}&7!"));
		messages.set("player.deathMessage", String.valueOf("&8[&4死亡&8] &7你因为PK值导致额外扣取 &c{0}&7 经验等级"));
		messages.set("player.expMessage", String.valueOf("&8[&6经验&8] &7你因为PK值获得额外 &e{0}&7 经验 &7[{1}->{2}]"));
		messages.set("player.noTargetMeMessage", String.valueOf("&8[&c通缉&8] &7你不能通缉你自己!"));
		messages.set("player.nullTargetMessage", String.valueOf("&8[&c通缉&8] &7你的通缉任务失败!连续通缉次数归零! &7（&c原因&7:目标PK值已消除 或被他人抓获）"));
		messages.set("player.onlineTargetMessage", String.valueOf("&8[&c通缉&8] &7你的通缉目标 &c{0}&7 当前PK值为: {1} ，平面坐标（{2}，{3}），所在世界 {4}"));
		messages.set("player.offlineTargetMessage", String.valueOf("&8[&c通缉&8] &7你的通缉目标当前不在线!"));
		messages.set("player.cancellTargetMessage", String.valueOf("&8[&c通缉&8] &7你已经放弃了本次任务! 支付 &6{0}&7金币，剩余 &c{1}&7金币"));
		messages.set("player.returnZeroWantedPointMessage", String.valueOf("&8[&c通缉&8] &7你的PK值已消除！"));
		messages.set("player.timeDeductionWantedPointMessage", String.valueOf("&8[&c通缉&8] &7你的PK值自动消除到 &c{0}&7 了！"));
		messages.set("player.jailedCancelMessage", String.valueOf("&8[&c通缉&8] &7你已经出狱! 请好好干!"));	
		messages.set("player.timeDeductionJailedMessage", String.valueOf("&8[&c通缉&8] &7你还剩 &c{0}&7 分钟就能出狱!请不要擅自&c退出&7游戏!退出游戏不自动&c消除&7监狱时间!"));	
		messages.set("player.jailedJoinMessage", String.valueOf("&8[&c通缉&8] &7你被&c逮捕&7了! 需要坐牢 &6{0}&7分钟! 请好好的在监狱改过自新！!"));
		messages.set("player.jailedeventMessage", String.valueOf("&8[&c通缉&8] &7请老老实实的等待释放！"));
		messages.set("player.asWantedArrestMessage", String.valueOf("&8[&c通缉&8] &7你成功的抓住了 &6{0}&7 通缉犯！&6任务奖励: &e{1}&7金币"));	
		messages.set("player.noPermissionMessage", String.valueOf("&8[&c通缉&8] &7你没有权限"));
		messages.set("player.nobalanceMessage", String.valueOf("&8[&c通缉&8] &7你没有足够的金币"));	
		messages.set("admin.setJailMessage", String.valueOf("&8[&c通缉&8] &7设置监狱为:&7(&e{0}&7,&e{1}&7,&e{2}&7) &c世界:&7(&e{3}&7)"));
		messages.set("admin.joinJailCorrectionsMessage", String.valueOf("&8[&c通缉&8] &7请输入/pawm joinjail <玩家名>!"));
		messages.set("admin.quitjailCorrectionsMessage", String.valueOf("&8[&c通缉&8] &7请输入/pawm quitjail <玩家名>!"));
		messages.set("admin.setPointsCorrectionsMessage", String.valueOf("&8[&c通缉&8] &7请输入/pawm set <玩家名>!"));	
		messages.set("admin.playerOfflineMessage", String.valueOf("&8[&c通缉&8] &7玩家必须是在线状态!"));
		messages.set("admin.reloadMessage", String.valueOf("&8[&c通缉&8] &7插件重载成功!"));	
		try {messages.save(messageFile);} catch (IOException e) {e.printStackTrace();}
	}
	
	static public void loadMessage(){
		File messageFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "message.yml");
		if(!messageFile.exists()){
			if(Config.getConfig("language").equals("CN")){
				createCNMessage();
			}else if(Config.getConfig("language").equals("EN")){
				//TODO createENMessage();
			}else{
				createCNMessage();
			}
		}else{
	        Bukkit.getConsoleSender().sendMessage("§8[§6PVPAsWantedManager§8] §aFind Message.yml");
		}
		messages = new YamlConfiguration();
		try {messages.load(messageFile);} catch (IOException | InvalidConfigurationException e) {e.printStackTrace();Bukkit.getConsoleSender().sendMessage("§8[§6PVPAsWantedManager§8] §a读取message时发生错误");}
	}
	

	public static String getMsg(String loc,String... args){
		String raw = messages.getString(loc);
		if (raw == null || raw.isEmpty()) {
			return "缺少Message: " + loc;
		}
		raw = raw.replaceAll("&", "§");
		if (args == null) {
			return raw;
		}
		for (int i = 0; i < args.length; i++) {
			raw = raw.replace("{" + i + "}", args[i]==null ? "null" : args[i]);
		}
		
		return raw;
	}
	
	public static ArrayList<String> getList(String loc,String... args){
		ArrayList<String> list = (ArrayList<String>) messages.getStringList(loc);
		ArrayList<String> elist = new ArrayList<String>();
		if (list == null || list.isEmpty()) {
			list.add("缺少Message: " + loc);
			return list;
		}
		if (args == null) {
			for(int e= 0;e <list.size();){
				elist.add(list.get(e).replace("&", "§"));
				e++;
			}
			return elist;
		}else{
			
		}
		//循环lore
		for(int e= 0;e <list.size();){
			String lore = list.get(e);
			for (int i= 0; i < args.length;i++){
				lore = lore.replace("&", "§").replace("{" + i + "}", args[i]==null ? "null" : args[i]);
			}
			elist.add(lore);
			e++;
		}
		return elist;
	}
}

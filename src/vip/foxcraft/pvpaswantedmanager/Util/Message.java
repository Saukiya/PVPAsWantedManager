package vip.foxcraft.pvpaswantedmanager.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import vip.foxcraft.pvpaswantedmanager.PVPAsWantedManager;

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
		messages.set("asWantedGui.wantedSkull.Placeholder", "&cN/A &7Offline Player");
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
		messages.set("asWantedGui.pvpProtect.Name", String.valueOf("&b&l新手保护"));
		ArrayList<String> pvpProtectLore = new ArrayList<String>();
		pvpProtectLore.add("&3累计在线小于 &6{0}&3分钟 拥有&6PVP&3保护");
		pvpProtectLore.add("&3累计时长: &6{1}&3分钟");
		messages.set("asWantedGui.pvpProtect.Lore", pvpProtectLore);
		messages.set("asWantedGui.pvpProtect.Off", String.valueOf("&6点击关闭保护"));
		messages.set("asWantedGui.pvpProtect.On", String.valueOf("&b点击开启保护"));
		messages.set("asWantedGui.jailInfo.Name", String.valueOf("&6&l监狱时间"));
		ArrayList<String> jailInfoLore = new ArrayList<String>();
		jailInfoLore.add("&6剩余 &c{0}&6分钟");
		messages.set("asWantedGui.jailInfo.Lore", jailInfoLore);
		messages.set("asWantedGui.wantedInfo.Name", String.valueOf("&6&l消除时间"));
		ArrayList<String> wantedInfoLore = new ArrayList<String>();
		wantedInfoLore.add("&6{0}&7分钟/&6PK&7值");
		messages.set("asWantedGui.wantedInfo.Lore", wantedInfoLore);
		messages.set("asWantedGui.quit", String.valueOf("&7&l退出菜单"));
		messages.set("asWantedGui.cancellTarget.name", String.valueOf("&l放弃任务"));
		ArrayList<String> cancellTargetLore = new ArrayList<String>();
		cancellTargetLore.add("&6需要支付: &7对方&6{0}&7PK值*&6{1}&7金币= &c{2}&7金币");
		messages.set("asWantedGui.cancellTarget.lore", cancellTargetLore);
		
		messages.set("setPlayerGui.guiName", String.valueOf("&9&l玩家点数管理 &4&l{0}"));
		messages.set("setPlayerGui.wanted.Name", String.valueOf("&a&lPK点数"));
		ArrayList<String> wantedLore = new ArrayList<String>();
		wantedLore.add("&6PK值: &c{0}");
		wantedLore.add("&3点击编辑");
		messages.set("setPlayerGui.wanted.Lore", wantedLore);
		messages.set("setPlayerGui.jail.Name", String.valueOf("&a&l监狱时间"));
		ArrayList<String> jailLore = new ArrayList<String>();
		jailLore.add("&6剩余时间: &c{0}&7分钟");
		jailLore.add("&3点击编辑");
		messages.set("setPlayerGui.jail.Lore", jailLore);
		messages.set("setPlayerGui.asWanted.Name", String.valueOf("&a&l通缉目标"));
		ArrayList<String> asWantedLore = new ArrayList<String>();
		asWantedLore.add("&6目标: &c{0}");
		asWantedLore.add("&3点击&4清空");
		messages.set("setPlayerGui.asWanted.Lore", asWantedLore);
		messages.set("player.newWantedMessage", String.valueOf("&8[&4击杀&8] &7你击杀了 &6{0}&7! PK值达到 &e{1}&7!"));
		messages.set("player.deathMessage", String.valueOf("&8[&4死亡&8] &7你因为PK值导致额外扣取 &c{0}&7 经验等级"));
		messages.set("player.expMessage", String.valueOf("&8[&6经验&8] &7你因为PK值获得额外 &e{0}&7 经验 &7[{1}->{2}]"));
		messages.set("player.noTargetMeMessage", String.valueOf("&8[&c通缉&8] &7你不能通缉你自己!"));
		messages.set("player.nullTargetMessage", String.valueOf("&8[&c通缉&8] &7你的通缉任务失败!连续通缉次数归零! &7（&c原因&7:目标PK值已消除 或被他人抓获）"));
		messages.set("player.onlineTargetMessage", String.valueOf("&8[&c通缉&8] &7你的通缉目标 &c{0}&7 当前PK值为: {1} ，平面坐标（&6{2}&7，&6{3}&7），所在世界: &6{4}"));
		messages.set("player.offlineTargetMessage", String.valueOf("&8[&c通缉&8] &7你的通缉目标当前不在线!"));
		messages.set("player.cancellTargetMessage", String.valueOf("&8[&c通缉&8] &7你已经放弃了本次任务! 支付 &6{0}&7金币，剩余 &c{1}&7金币"));
		messages.set("player.returnZeroWantedPointMessage", String.valueOf("&8[&c通缉&8] &7你的PK值已消除！"));
		messages.set("player.timeDeductionWantedPointMessage", String.valueOf("&8[&c通缉&8] &7你的PK值消除到 &c{0}&7 了！"));
		messages.set("player.jailedCancelMessage", String.valueOf("&8[&c通缉&8] &7你已经出狱! 请好好干!"));	
		messages.set("player.timeDeductionJailedMessage", String.valueOf("&8[&c通缉&8] &7你还剩 &c{0}&7 分钟就能出狱!请不要擅自&c退出&7游戏!退出游戏不自动&c消除&7监狱时间!"));	
		messages.set("player.jailedJoinMessage", String.valueOf("&8[&c通缉&8] &7你被&c抓进监狱&7了! 需要坐牢 &6{0}&7分钟! 请好好的在监狱改过自新！!"));
		messages.set("player.jailedeventMessage", String.valueOf("&8[&c通缉&8] &7请老老实实的等待释放！"));
		messages.set("player.asWantedArrestMessage", String.valueOf("&8[&c通缉&8] &7你成功的抓住了 &6{0}&7 通缉犯! &6任务奖励: &e{1}&7金币"));	
		messages.set("player.noPermissionMessage", String.valueOf("&8[&c通缉&8] &7你没有权限"));
		messages.set("player.nobalanceMessage", String.valueOf("&8[&c通缉&8] &7你没有足够的金币"));
		messages.set("player.pvpProtectMessage1", String.valueOf("&8[&c通缉&8] &7你还在新手保护阶段，达到 &6{0}&7分钟即解除保护!"));
		messages.set("player.pvpProtectMessage2", String.valueOf("&8[&c通缉&8] &7玩家 &6{0}&7还在新手保护阶段!"));
		messages.set("player.pvpOffMessage", String.valueOf("&8[&c通缉&8] &7你临时解除了新手保护!"));
		messages.set("player.pvpOnMessage", String.valueOf("&8[&c通缉&8] &7你开启了新手保护!"));
		messages.set("admin.ConsoleNotMessage", String.valueOf("&8[&c通缉&8] &c控制台不可执行此指令!"));
		messages.set("admin.setJailMessage", String.valueOf("&8[&c通缉&8] &7设置监狱为:&7(&e{0}&7,&e{1}&7,&e{2}&7) &c世界:&7(&e{3}&7)"));
		messages.set("admin.joinJailCorrectionsMessage", String.valueOf("&8[&c通缉&8] &7请输入/pawm joinjail <玩家名> <分钟>!"));
		messages.set("admin.quitJailCorrectionsMessage", String.valueOf("&8[&c通缉&8] &7请输入/pawm quitjail <玩家名>!"));
		messages.set("admin.joinJailPlayerMessage", String.valueOf("&8[&c通缉&8] &7玩家 &6{0}&7进了监狱! 坐牢 &c{1}&7分钟!"));
		messages.set("admin.quitJailPlayerMessage", String.valueOf("&8[&c通缉&8] &7玩家 &6{0}&7离开了监狱!"));
		messages.set("admin.playerIsAlreadyInJailMessage", String.valueOf("&8[&c通缉&8] &7玩家已经在监狱了!"));
		messages.set("admin.playerIsNotInJailMessage", String.valueOf("&8[&c通缉&8] &7玩家不在监狱!"));
		messages.set("admin.setPointsCorrectionsMessage", String.valueOf("&8[&c通缉&8] &7请输入/pawm set <玩家名>!"));	
		messages.set("admin.playerNullMessage", String.valueOf("&8[&c通缉&8] &c玩家不存在!"));
		messages.set("admin.playerOfflineMessage", String.valueOf("&8[&c通缉&8] &7玩家必须是在线状态!"));
		messages.set("admin.waitForInputMessage", String.valueOf("&8[&c通缉&8] &a&o请输入你要修改的数值&7&o:"));
		messages.set("admin.wrongFormatMessage", String.valueOf("&8[&c通缉&8] &c格式错误!"));
		messages.set("admin.EditPlayerDataMessage", String.valueOf("&8[&c通缉&8] &a&o修改成功!"));
		messages.set("admin.reloadMessage", String.valueOf("&8[&c通缉&8] &7插件重载成功!"));	
		messages.set("title.jailedJoin", String.valueOf("&4&k|&c监狱坐牢&4&k|"));
		messages.set("title.jailedJoinSub", String.valueOf("&6时间: &c{0}&6分钟"));
		messages.set("title.asWantedArrest", String.valueOf("&a&k|&e任务完成&a&k|"));
		messages.set("title.asWantedArrestSub", String.valueOf("&6奖励: &a{0}&6金币"));
		messages.set("command.open", String.valueOf("打开通缉菜单"));
		messages.set("command.joinjail", String.valueOf("使玩家入狱"));
		messages.set("command.quitjail", String.valueOf("使玩家出狱"));
		messages.set("command.setjail", String.valueOf("设置监狱位置"));
		messages.set("command.reload", String.valueOf("重载插件配置"));
		messages.set("command.set", String.valueOf("打开玩家点数管理"));
		messages.set("command.setpoint", String.valueOf("修改玩家PK值"));
		messages.set("command.NoCommand", String.valueOf("&8[&c通缉&8] &c未找到此命令:&7/pawm &6{0}"));
		ArrayList<String> replaceList = new ArrayList<String>();
		replaceList.add("world:&2生存世界");
		replaceList.add("world_nether:&c地狱");
		replaceList.add("world_the_end:&3末地");
		messages.set("replace", replaceList);
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
			return "Null Message: " + loc;
		}
		raw = raw.replaceAll("&", "§");
		if (args == null) {
			return raw;
		}
		ArrayList<String> replaceList = Message.getList("replace");
		for (int i = 0; i < args.length; i++) {
			for(int l=0; l <replaceList.size();l++){
				String str = replaceList.get(l);
				String str1 = str.split(":")[0];
				String str2 = str.split(":")[1].replace("&", "§");
				if(args[i].equals(str1))args[i] = args[i].replace(str1, str2);
			}
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

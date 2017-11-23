package vip.foxcraft.pvpaswantedmanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;


public class InventoryManager implements Listener {
	static HashMap<Player,String> PKMap = new HashMap<Player,String>();
	static HashMap<Player,String> JailMap = new HashMap<Player,String>();
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onCheckAsWantedInventory(InventoryClickEvent event){
		Inventory inventory = event.getInventory();
		String inventoryName = inventory.getName();
		ItemStack item = event.getCurrentItem();
		if(inventoryName.equals(Message.getMsg("asWantedGui.guiName"))){
			Player player = (Player) event.getView().getPlayer();
			if(item != null){
				if(event.getRawSlot() <= 53){
					if(item.getType().equals(Material.SKULL_ITEM)){
						if(item.getDurability() == 3){
							YamlConfiguration Data = PVPAsWantedManager.onLoadData(player.getName());
							ItemMeta itemMeta = item.getItemMeta();
							String name = ((SkullMeta) itemMeta).getOwner();
							if(event.getRawSlot() == 4){
								event.setCancelled(true);
								return;
							}
							if(name.toLowerCase().equals(player.getName().toLowerCase())){
								player.sendMessage(Message.getMsg("player.noTargetMeMessage"));
								event.setCancelled(true);
								return;
							}
							if(Data.getString("asWanted.target").equals("N/A")){
								Data.set("asWanted.target", String.valueOf(name));
								PVPAsWantedManager.onSaveData(player.getName(),Data);
								player.sendMessage(Message.getMsg("asWantedGui.gettargetMessage", String.valueOf(name)));
								event.setCancelled(true);
								player.closeInventory();
							}else{
								name = Data.getString("asWanted.target");
								player.sendMessage(Message.getMsg("asWantedGui.hastargetMessage", String.valueOf(name)));
								event.setCancelled(true);
							}
						}
					}
					Material pageDownType = Material.getMaterial(Integer.valueOf(Config.getConfig("asWantedGui.ID.pageDown")));
					Material pageUpType = Material.getMaterial(Integer.valueOf(Config.getConfig("asWantedGui.ID.pageUp")));
					Material quitType = Material.getMaterial(Integer.valueOf(Config.getConfig("asWantedGui.ID.quit")));
					Material targetType = Material.getMaterial(Integer.valueOf(Config.getConfig("asWantedGui.ID.cancellTarget")));
					if(item.getType().equals(pageDownType) || item.getType().equals(pageUpType)){
						InventoryManager.openAsWantedGUI(player, item.getAmount());
						return;
					}
					if(item.getType().equals(quitType)){
						player.closeInventory();
					}
					if(item.getType().equals(targetType)){
						YamlConfiguration Data = PVPAsWantedManager.onLoadData(player.getName());
						String target = Data.getString("asWanted.target");
						if(!target.equals("N/A")){
							YamlConfiguration targetData = PVPAsWantedManager.onLoadData(target);
							int targetWantedPoints = targetData.getInt("wanted.points");
							int cancelltargetbalance = Integer.valueOf(Config.getConfig("CancellAsWantedTarget.money"));
							int value = targetWantedPoints*cancelltargetbalance;
							if(Money.has(player.getName(), value)){
								Money.take(player.getName(), value);
								Data.set("asWanted.target", String.valueOf("N/A"));
								PVPAsWantedManager.onSaveData(player.getName(), Data);
								player.sendMessage(Message.getMsg("player.cancellTargetMessage",String.valueOf(value),String.valueOf(Money.getBalance(player.getName()))));
							}else{
								player.sendMessage(Message.getMsg("player.nobalanceMessage"));
							}
						player.closeInventory();
						}
					}
				}
			}
		event.setCancelled(true);
		}
		
	}
	@EventHandler
	public void onCheckSetPlayerInventory(InventoryClickEvent event){
		Inventory inventory = event.getInventory();
		String inventoryName = inventory.getName();
		ItemStack item = event.getCurrentItem();
		if(inventoryName.contains(Message.getMsg("setPlayerGui.guiName").replace("{0}",""))){
			Player admin = (Player) event.getView().getPlayer();
			if(item != null&&!item.getType().equals(Material.AIR)){
				String player = inventory.getItem(4).getItemMeta().getDisplayName().replace("§4§l", "");
				if(event.getRawSlot() <=26){
					//当点到PK点数时
					if(item.getItemMeta().getDisplayName().equals(Message.getMsg("setPlayerGui.wanted.Name"))){
						if(JailMap.containsKey(admin)){
							JailMap.remove(admin);
						}
						PKMap.put(admin, player);
						admin.sendMessage(Message.getMsg("admin.waitForInputMessage"));
						admin.closeInventory();
					}
					//当点到监狱时间时
					if(item.getItemMeta().getDisplayName().equals(Message.getMsg("setPlayerGui.jail.Name"))){
						if(PKMap.containsKey(admin)){
							PKMap.remove(admin);
						}
						JailMap.put(admin, player);
						admin.sendMessage(Message.getMsg("admin.waitForInputMessage"));
						admin.closeInventory();
					}
					//当点到通缉目标时
					if(item.getItemMeta().getDisplayName().equals(Message.getMsg("setPlayerGui.asWanted.Name"))){
						YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player);
						if(!PlayerData.getString("asWanted,target").equals("N/A")){
							PlayerData.set("asWanted.target", String.valueOf("N/A"));
							PVPAsWantedManager.onSaveData(player, PlayerData);
							openSetPlayerGui(admin,player);
						}
					}
					//当点到退出菜单时
					if(item.getItemMeta().getDisplayName().equals(Message.getMsg("asWantedGui.quit"))){
						admin.closeInventory();
					}
				}
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent event){
		Player admin = event.getPlayer();
		if(PKMap.containsKey(admin)){
			String player = PKMap.get(admin);
			PKMap.remove(admin);
			String message = event.getMessage();
			if(Pattern.compile("[0-9]*").matcher(message).matches()){
				int value = Integer.valueOf(message);
				YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player);
				if(PlayerData.getInt("wanted.points")==0&& value > 0){
					PVPAsWantedManager.onCreateList(player, "WantedList");
				}else if(PlayerData.getInt("wanted.points")>0 && value ==0){
					PVPAsWantedManager.onDeleteList(player, "WantedList");
				}
				PlayerData.set("wanted.points", value);
				PVPAsWantedManager.onSaveData(player, PlayerData);
				admin.sendMessage(Message.getMsg("admin.EditPlayerDataMessage"));
			}else{
				admin.sendMessage(Message.getMsg("admin.wrongFormatMessage"));
			}
			event.setCancelled(true);
		}else if(JailMap.containsKey(admin)){
			String player = JailMap.get(admin);
			JailMap.remove(admin);
			String message = event.getMessage();
			if(Pattern.compile("[0-9]*").matcher(message).matches()){
				int value = Integer.valueOf(message);
				YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player);
				if(PlayerData.getInt("jail.times")==0&& value > 0){
					PVPAsWantedManager.onCreateList(player, "JailedList");
				}else if(PlayerData.getInt("jail.times")>0 && value ==0){
					PVPAsWantedManager.onDeleteList(player, "JailedList");
				}
				PlayerData.set("jail.times", value);
				PVPAsWantedManager.onSaveData(player, PlayerData);
				admin.sendMessage(Message.getMsg("admin.EditPlayerDataMessage"));
			}else{
				admin.sendMessage(Message.getMsg("admin.wrongFormatMessage"));
			}
			event.setCancelled(true);
		}
	}
	@SuppressWarnings("deprecation")
	static public void openAsWantedGUI(Player player,int page){
		YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player.getName());
		String wantedPoints = PlayerData.getString("wanted.points");
		Inventory inventory = Bukkit.createInventory(null, 54, Message.getMsg("asWantedGui.guiName"));
		ArrayList<String> WantedList = new ArrayList<String>();
		int number = (page-1)*36;
		YamlConfiguration Data = new YamlConfiguration();
		File file = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "data.dat");
		try {Data.load(file);} catch (IOException | InvalidConfigurationException e) {e.printStackTrace();}
		WantedList = (ArrayList<String>) Data.getStringList("WantedList");
		HashMap<String,Integer> OnlineMap = new HashMap<String,Integer>();
		HashMap<String,Integer> OfflineMap = new HashMap<String,Integer>();
		HashMap<String,Integer> LevelMap = new HashMap<String,Integer>();
		int playerWantedPoints = 0;
		int playerLevel = 0;
		//设置行数自动化
		int skullNumber = 9;
		if(WantedList.size() >0){
			for(int i = 0; i <= 36 && (i+number) < WantedList.size();){
				String playerName = WantedList.get(i+number);
				YamlConfiguration WantedData = PVPAsWantedManager.onLoadData(playerName);
				if(WantedData != null){
					playerWantedPoints = WantedData.getInt("wanted.points");
					playerLevel = WantedData.getInt("attribute.level");
				}else{
					playerWantedPoints = 0;
					playerLevel = 0;
				}
				LevelMap.put(playerName, playerLevel);
				if(PVPAsWantedManager.isPlayerOnline(playerName) == true){
					//如果存在，则导入到在线玩家Map
					OnlineMap.put(playerName, playerWantedPoints);
				}else{
					//如果不存在，则导入离线玩家Map
					OfflineMap.put(playerName, playerWantedPoints);
				}
				i++;
			}

			
			ArrayList<String> loreList = new ArrayList<String>();
	        List<Map.Entry<String, Integer>> Onlinelist = new ArrayList<Map.Entry<String, Integer>>(OnlineMap.entrySet());
	        Collections.sort(Onlinelist, new Comparator<Map.Entry<String, Integer>>() {
	            @Override  
	            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {  
	                return o2.getValue().compareTo(o1.getValue());  
	            }
	        });
	        List<Map.Entry<String, Integer>> Offlinelist = new ArrayList<Map.Entry<String, Integer>>(OfflineMap.entrySet());
	        Collections.sort(Offlinelist, new Comparator<Map.Entry<String, Integer>>() {
	            @Override  
	            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {  
	                return o2.getValue().compareTo(o1.getValue());  
	            }
	        });

			ItemStack itemStack = new ItemStack(Material.SKULL_ITEM);
			ItemMeta itemMeta = itemStack.getItemMeta();
			String name;
			int wantedpoints;
			int level;
			double Money = 0D;
            int value;
			double TaskRewardMoney = Double.valueOf(Config.getConfig("TaskReward.money"));
			double TaskRewardBasicMoney = Double.valueOf(Config.getConfig("TaskReward.basicMoney"));
			
	        String online = Message.getMsg("asWantedGui.wantedSkull.Online");
	        for (Map.Entry<String, Integer> OnlineList : Onlinelist) {
	            name = OnlineList.getKey();
	            wantedpoints = OnlineList.getValue();
	            level = LevelMap.get(name);
	            itemStack = new ItemStack(Material.SKULL_ITEM);
	            itemMeta = itemStack.getItemMeta();
	            value = 0;
	            if(Integer.valueOf(wantedPoints)<=wantedpoints){
	            	value = wantedpoints - Integer.valueOf(wantedPoints);
	            }
	            Money = (value+wantedpoints*0.25)*TaskRewardMoney+TaskRewardBasicMoney;
	            value = wantedpoints - value;
	            String PKPoints = "-"+value;
	            if(value==0){
	            	PKPoints = "§0";
	            }else{
	            	PKPoints = "§c§l-"+value+"§a";
	            }
				itemStack.setDurability((short) 3);
			    ((SkullMeta) itemMeta).setOwner(name);
			    loreList = Message.getList("asWantedGui.wantedSkull.Lore", String.valueOf(wantedpoints), String.valueOf(level), online,String.valueOf(Money),PKPoints);
				itemMeta.setDisplayName(Message.getMsg("asWantedGui.wantedSkull.Name", name));
				itemMeta.setLore(loreList);
				itemStack.setItemMeta(itemMeta);
				inventory.setItem(skullNumber, itemStack);
				skullNumber ++;
	        }

	        String offline = Message.getMsg("asWantedGui.wantedSkull.Offline");
	        for (Map.Entry<String, Integer> OfflineList : Offlinelist) {
	            name = OfflineList.getKey();
	            wantedpoints = OfflineList.getValue();
	            level = LevelMap.get(name);
	            itemStack = new ItemStack(Material.SKULL_ITEM);
	            itemMeta = itemStack.getItemMeta();
	            value = 0;
	            if(Integer.valueOf(wantedPoints) <wantedpoints){
	            	value = wantedpoints - Integer.valueOf(wantedPoints);
	            }
	            
	            Money = (value+wantedpoints*0.25)*TaskRewardMoney+TaskRewardBasicMoney;
	            value = wantedpoints - value;
	            String PKPoints = String.valueOf(value);
	            if(value==0){
	            	PKPoints = "§0"+value;
	            }else{
	            	PKPoints = "§c§l-"+value+"§a";
	            }
				itemStack.setDurability((short) 3);
			    ((SkullMeta) itemMeta).setOwner(name);
			    loreList = Message.getList("asWantedGui.wantedSkull.Lore", String.valueOf(wantedpoints), String.valueOf(level), offline,String.valueOf(Money),PKPoints);
				itemMeta.setDisplayName(Message.getMsg("asWantedGui.wantedSkull.Name", name));
				itemMeta.setLore(loreList);
				itemStack.setItemMeta(itemMeta);
				inventory.setItem(skullNumber, itemStack);
				skullNumber ++;
	        }
		}else{
			ItemStack Null = new ItemStack(Material.THIN_GLASS);
			ItemMeta NullMeta = Null.getItemMeta();
			NullMeta.setDisplayName(Message.getMsg("asWantedGui.wantedSkull.null"));
			Null.setItemMeta(NullMeta);
			inventory.setItem(9, Null);
		}

        String glassData = String.valueOf(Config.getConfig("asWantedGui.ID.glass"));
        int glassMaterial = 0;
        int glassDurability = 0;
        if(glassData.contains(":")){
        	glassMaterial = Integer.valueOf(glassData.split(":")[0]);
        	glassDurability = Integer.valueOf(glassData.split(":")[1]);
        }else{
        	glassMaterial = Integer.valueOf(glassData);
        }
        Material glassType = Material.getMaterial(glassMaterial);
		ItemStack glass = new ItemStack(glassType);
		glass.setDurability((short) glassDurability);
        if(!glassData.equals("0")){
    		ItemMeta glassMeta = glass.getItemMeta();
    		glassMeta.setDisplayName(" ");
    		glass.setItemMeta(glassMeta);
        }
		for(int i=0;i < 4;){
			inventory.setItem(i, glass);
			i++;
		}
		for(int i=5;i < 9;){
			inventory.setItem(i, glass);
			i++;
		}
		for(int i=46;i < 53;){
			inventory.setItem(i, glass);
			i++;
		}

		Material pageDownType = Material.getMaterial(Integer.valueOf(Config.getConfig("asWantedGui.ID.pageDown")));
		Material pageUpType = Material.getMaterial(Integer.valueOf(Config.getConfig("asWantedGui.ID.pageUp")));
		ItemStack pageDown = new ItemStack(pageDownType); 
		ItemStack pageUp = new ItemStack(pageUpType);
		ItemMeta pageDownMeta = pageDown.getItemMeta();
		ItemMeta pageUpMeta = pageUp.getItemMeta();
		pageDownMeta.setDisplayName(Message.getMsg("asWantedGui.pageDownName", String.valueOf(Integer.valueOf(page-1))));
		pageUpMeta.setDisplayName(Message.getMsg("asWantedGui.pageUpName", String.valueOf(Integer.valueOf(page+1))));
		pageDown.setAmount(page - 1);
		pageUp.setAmount(page + 1);
		pageDown.setItemMeta(pageDownMeta);
		pageUp.setItemMeta(pageUpMeta);
		if(page > 1){
			inventory.setItem(45, pageDown);
		}else{
			inventory.setItem(45, glass);
		}
		if((page*36) < WantedList.size()){
			inventory.setItem(53, pageUp);
		}else{
			inventory.setItem(53, glass);
		}

		String wantedCumulativePoints = PlayerData.getString("wanted.cumulativePoints");
		String wantedHighestPoints = PlayerData.getString("wanted.highestPoints");
		String jailCumulativeNumber = PlayerData.getString("jail.cumulativeNumber");
		int jailTimes = Integer.valueOf(PlayerData.getString("jail.times"));
		int jailCalculationTime = Integer.valueOf(Config.getConfig("timeTick.jailPlayerTimeDeduction").replace("min", ""));
		int cancelltargetbalance = Integer.valueOf(Config.getConfig("CancellAsWantedTarget.money"));
		String asWantedTarget = PlayerData.getString("asWanted.target");
		String asWantedCumulativeNumber = PlayerData.getString("asWanted.cumulativenumber");
		String asWantedContinuityNumber = PlayerData.getString("asWanted.continuitynumber");
		ItemStack info = new ItemStack(Material.SKULL_ITEM);
		ItemMeta infoMeta = info.getItemMeta();
		info.setDurability((short) 3);
	    ((SkullMeta) infoMeta).setOwner(player.getName());
	    int pointsValueExp = Integer.valueOf(Config.getConfig("extraExp.pointsValue").replace("%", ""));
		ArrayList<String> infoLore = Message.getList("asWantedGui.info.Lore", wantedPoints, wantedCumulativePoints, wantedHighestPoints, jailCumulativeNumber, asWantedTarget, asWantedCumulativeNumber, asWantedContinuityNumber);
		if(Config.getConfig("extraExp.enabled").equals("true"))infoLore.add(Message.getMsg("asWantedGui.info.exp", String.valueOf(Integer.valueOf(wantedPoints)*pointsValueExp)));
		infoMeta.setDisplayName(Message.getMsg("asWantedGui.info.Name", player.getName()));
		infoMeta.setLore(infoLore);
		info.setItemMeta(infoMeta);
		inventory.setItem(4, info);

		Material jailInfoType = Material.getMaterial(Integer.valueOf(Config.getConfig("asWantedGui.ID.jailInfo")));
		ItemStack jailInfo = new ItemStack(jailInfoType);
		ItemMeta jailInfoMeta = jailInfo.getItemMeta();
		ArrayList<String> jailInfoLore = Message.getList("asWantedGui.jailInfo.Lore",String.valueOf(jailTimes),wantedPoints,String.valueOf(jailCalculationTime),String.valueOf(jailCalculationTime*Integer.valueOf(wantedPoints)));
		if(jailTimes > 0){
			jailInfoMeta.setDisplayName("§c"+Message.getMsg("asWantedGui.jailInfo.Name"));
			jailInfoLore.remove(1);
		}else{
			jailInfoMeta.setDisplayName("§7"+Message.getMsg("asWantedGui.jailInfo.Name"));
			jailInfoLore.remove(0);
		}
		jailInfoMeta.setLore(jailInfoLore);
		jailInfo.setItemMeta(jailInfoMeta);
		inventory.setItem(47, jailInfo);
		
		Material quitType = Material.getMaterial(Integer.valueOf(Config.getConfig("asWantedGui.ID.quit")));
		ItemStack quit = new ItemStack(quitType);
		ItemMeta quitMeta = quit.getItemMeta();
		quitMeta.setDisplayName(Message.getMsg("asWantedGui.quit"));
		quit.setItemMeta(quitMeta);
		inventory.setItem(49, quit);
		
		if(Config.getConfig("CancellAsWantedTarget.enabled").equals("true")){
			Material targetType = Material.getMaterial(Integer.valueOf(Config.getConfig("asWantedGui.ID.cancellTarget")));
			ItemStack targetInfo = new ItemStack(targetType);
			ItemMeta targetMeta = targetInfo.getItemMeta();
			String targetWantedPoints = "0";
			if(!asWantedTarget.equals("N/A")){
				YamlConfiguration targetData = PVPAsWantedManager.onLoadData(asWantedTarget);
				if(targetData != null){
					targetWantedPoints = targetData.getString("wanted.points");
					targetMeta.setDisplayName("§c" + Message.getMsg("asWantedGui.cancellTarget.name"));
				}else{
					PlayerData.set("asWanted.target",String.valueOf("N/A"));
				}
			}else{
				targetMeta.setDisplayName("§7" + Message.getMsg("asWantedGui.cancellTarget.name"));
			}
			ArrayList<String> cancellTargetLore = Message.getList("asWantedGui.cancellTarget.lore",targetWantedPoints,String.valueOf(cancelltargetbalance),String.valueOf(cancelltargetbalance*Integer.valueOf(targetWantedPoints)));
			targetMeta.setLore(cancellTargetLore);
			targetInfo.setItemMeta(targetMeta);
			inventory.setItem(51, targetInfo);
		}
		player.openInventory(inventory);
	}
	
	@SuppressWarnings("deprecation")
	static public void openSetPlayerGui(Player sender,String player){
		//TODO 如果PlayerData为 Null 则不显示菜单
		YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player);
		if(PlayerData == null){
			sender.sendMessage(Message.getMsg("admin.playerNullMessage"));
			return;
		}
		String wantedPoints = PlayerData.getString("wanted.points");
		String wantedCumulativePoints = PlayerData.getString("wanted.cumulativePoints");
		String wantedHighestPoints = PlayerData.getString("wanted.highestPoints");
		String jailCumulativeNumber = PlayerData.getString("jail.cumulativeNumber");
		String asWantedTarget = PlayerData.getString("asWanted.target");
		String asWantedCumulativeNumber = PlayerData.getString("asWanted.cumulativenumber");
		String asWantedContinuityNumber = PlayerData.getString("asWanted.continuitynumber");
		Inventory inventory = Bukkit.createInventory(null, 27, Message.getMsg("setPlayerGui.guiName",player));
		ItemStack wantedItem = new ItemStack(Material.GOLD_AXE);
		ItemMeta wantedMeta = wantedItem.getItemMeta();
		int wantedPoint = PlayerData.getInt("wanted.points");
		wantedMeta.setDisplayName(Message.getMsg("setPlayerGui.wanted.Name"));
		ArrayList<String> wantedLore = Message.getList("setPlayerGui.wanted.Lore", String.valueOf(wantedPoint));
		wantedMeta.setLore(wantedLore);
		wantedMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		wantedItem.setItemMeta(wantedMeta);
		
		ItemStack jailItem = new ItemStack(Material.IRON_FENCE);
		ItemMeta jailMeta = jailItem.getItemMeta();
		jailMeta.setDisplayName(Message.getMsg("setPlayerGui.jail.Name"));
		int jailPoint = PlayerData.getInt("jail.points");
		ArrayList<String> jailLore = Message.getList("setPlayerGui.jail.Lore", String.valueOf(jailPoint));
		jailMeta.setLore(jailLore);
		jailItem.setItemMeta(jailMeta);
		
		ItemStack asWantedItem = new ItemStack(Material.ARROW);
		ItemMeta asWantedMeta = asWantedItem.getItemMeta();
		asWantedMeta.setDisplayName(Message.getMsg("setPlayerGui.asWanted.Name"));
		ArrayList<String> asWantedLore = Message.getList("setPlayerGui.asWanted.Lore", asWantedTarget);
		asWantedMeta.setLore(asWantedLore);
		asWantedItem.setItemMeta(asWantedMeta);

		ItemStack info = new ItemStack(Material.SKULL_ITEM);
		ItemMeta infoMeta = info.getItemMeta();
		info.setDurability((short) 3);
	    ((SkullMeta) infoMeta).setOwner(player);
	    int pointsValueExp = Integer.valueOf(Config.getConfig("extraExp.pointsValue").replace("%", ""));
		ArrayList<String> infoLore = Message.getList("asWantedGui.info.Lore", wantedPoints, wantedCumulativePoints, wantedHighestPoints, jailCumulativeNumber, asWantedTarget, asWantedCumulativeNumber, asWantedContinuityNumber);
		if(Config.getConfig("extraExp.enabled").equals("true"))infoLore.add(Message.getMsg("asWantedGui.info.exp", String.valueOf(Integer.valueOf(wantedPoints)*pointsValueExp)));
		infoMeta.setDisplayName("§4§l" + player);
		infoMeta.setLore(infoLore);
		info.setItemMeta(infoMeta);
		inventory.setItem(4, info);
		
        String glassData = String.valueOf(Config.getConfig("asWantedGui.ID.glass"));
        int glassMaterial = 0;
        int glassDurability = 0;
        if(glassData.contains(":")){
        	glassMaterial = Integer.valueOf(glassData.split(":")[0]);
        	glassDurability = Integer.valueOf(glassData.split(":")[1]);
        }else{
        	glassMaterial = Integer.valueOf(glassData);
        }
		Material glassType = Material.getMaterial(glassMaterial);
		ItemStack glass = new ItemStack(glassType);
		glass.setDurability((short) glassDurability);
        if(!glassData.equals("0")){
    		ItemMeta glassMeta = glass.getItemMeta();
    		glassMeta.setDisplayName(" ");
    		glass.setItemMeta(glassMeta);
        }
        
		Material quitType = Material.getMaterial(Integer.valueOf(Config.getConfig("asWantedGui.ID.quit")));
		ItemStack quit = new ItemStack(quitType);
		ItemMeta quitMeta = quit.getItemMeta();
		quitMeta.setDisplayName(Message.getMsg("asWantedGui.quit"));
		quit.setItemMeta(quitMeta);
		
		for(int i=0;i<9;){
			inventory.setItem(i, glass);
			i++;
		}
		for(int i=18;i<27;){
			inventory.setItem(i, glass);
			i++;
		}
		
		inventory.setItem(4, info);
		inventory.setItem(10, wantedItem);
		inventory.setItem(12, jailItem);
		inventory.setItem(14, asWantedItem);
		inventory.setItem(22, quit);
		sender.openInventory(inventory);
	}
}

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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

import net.ess3.api.Economy;

public class InventoryManager implements Listener {	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onCheckAsWantedInventory(InventoryClickEvent event) throws UserDoesNotExistException, NoLoanPermittedException{
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
							if(name.equals(player.getName())){
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
							int cancelltargetbalance = Integer.valueOf(Config.getConfig("CancellAsWantedTarget.balance"));
							int money = (int)Economy.getMoney(player.getName());
							if(money >=(cancelltargetbalance*targetWantedPoints)){
								money = money-(cancelltargetbalance*targetWantedPoints);
								Economy.setMoney(player.getName(), money);
								Data.set("asWanted.target", String.valueOf("N/A"));
								player.sendMessage(Message.getMsg("player.cancellTargetMessage",String.valueOf(cancelltargetbalance*targetWantedPoints),String.valueOf(money)));
							}else{
								player.sendMessage(Message.getMsg("player.nobalanceMessage"));
							}
							PVPAsWantedManager.onSaveData(player.getName(), Data);
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
		
	}

	@SuppressWarnings("deprecation")
	static public void openAsWantedGUI(Player player,int page){
		Inventory inventory = Bukkit.createInventory(null, 54, Message.getMsg("asWantedGui.guiName"));
		ArrayList<String> WantedList = new ArrayList<String>();
		int number = (page-1)*36;
		YamlConfiguration Data = new YamlConfiguration();
		PVPAsWantedManager.DataFile = new File("plugins" + File.separator + "PVPAsWantedManager" + File.separator + "data.dat");
		try {Data.load(PVPAsWantedManager.DataFile);} catch (IOException | InvalidConfigurationException e) {e.printStackTrace();}
		WantedList = (ArrayList<String>) Data.getStringList("WantedList");
		HashMap<String,Integer> OnlineMap = new HashMap<String,Integer>();
		HashMap<String,Integer> OfflineMap = new HashMap<String,Integer>();
		HashMap<String,Integer> LevelMap = new HashMap<String,Integer>();
		int playerWantedPoints = 0;
		int playerLevel = 0;
		//设置行数自动化
		int skullNumber = 9;
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

        
        String online = Message.getMsg("asWantedGui.wantedSkull.Online");
        for (Map.Entry<String, Integer> OnlineList : Onlinelist) {
            name = OnlineList.getKey();
            wantedpoints = OnlineList.getValue();
            level = LevelMap.get(name);

            itemStack = new ItemStack(Material.SKULL_ITEM);
            itemMeta = itemStack.getItemMeta();
            

			itemStack.setDurability((short) 3);
		    ((SkullMeta) itemMeta).setOwner(name);
		    loreList = Message.getList("asWantedGui.wantedSkull.Lore", String.valueOf(wantedpoints), String.valueOf(level), online);
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
            

			itemStack.setDurability((short) 3);
		    ((SkullMeta) itemMeta).setOwner(name);
		    loreList = Message.getList("asWantedGui.wantedSkull.Lore", String.valueOf(wantedpoints), String.valueOf(level), offline);
			itemMeta.setDisplayName(Message.getMsg("asWantedGui.wantedSkull.Name", name));
			itemMeta.setLore(loreList);
			itemStack.setItemMeta(itemMeta);
			inventory.setItem(skullNumber, itemStack);
			skullNumber ++;
        }

        String glassData = String.valueOf(Config.getConfig("asWantedGui.ID.glass"));
        int glassMaterial = 0;
        int glassDurability = 0;
        if(glassData.contains(":")){
        	glassData = glassData.replace(":", "-");
        	glassMaterial = Integer.valueOf(glassData.split("-")[0]);
        	glassDurability = Integer.valueOf(glassData.split("-")[1]);
        }else{
        	glassMaterial = Integer.valueOf(glassData);
        }
        Material glassType = Material.getMaterial(glassMaterial);
		ItemStack glass = new ItemStack(glassType);
		glass.setDurability((short) glassDurability);
		ItemMeta glassMeta = glass.getItemMeta();
		glassMeta.setDisplayName(" ");
		glass.setItemMeta(glassMeta);
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

		YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player.getName());
		String wantedPoints = PlayerData.getString("wanted.points");
		String wantedCumulativePoints = PlayerData.getString("wanted.cumulativePoints");
		String wantedHighestPoints = PlayerData.getString("wanted.highestPoints");
		String jailCumulativeNumber = PlayerData.getString("jail.cumulativeNumber");
		int jailTimes = Integer.valueOf(PlayerData.getString("jail.times"));
		int jailCalculationTime = Integer.valueOf(Config.getConfig("timeTick.jailPlayerTimeDeduction").replace("min", ""));
		int cancelltargetbalance = Integer.valueOf(Config.getConfig("CancellAsWantedTarget.balance"));
		String asWantedTarget = PlayerData.getString("asWanted.target");
		String asWantedCumulativeNumber = PlayerData.getString("asWanted.cumulativenumber");
		String asWantedContinuityNumber = PlayerData.getString("asWanted.continuitynumber");
		ItemStack info = new ItemStack(Material.SKULL_ITEM);
		ItemMeta infoMeta = info.getItemMeta();
		info.setDurability((short) 3);
	    ((SkullMeta) infoMeta).setOwner(player.getName());
		ArrayList<String> infoLore = Message.getList("asWantedGui.info.Lore", wantedPoints, wantedCumulativePoints, wantedHighestPoints, jailCumulativeNumber, asWantedTarget, asWantedCumulativeNumber, asWantedContinuityNumber,String.valueOf(Integer.valueOf(wantedPoints)*10));
		if(Config.getConfig("extraExp.enabled").equals("false"))infoLore.remove(7);
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
				targetWantedPoints = targetData.getString("wanted.points");
				targetMeta.setDisplayName("§c" + Message.getMsg("asWantedGui.cancellTarget.name"));
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
	
	
	static public void openSetPlayerGui(Player sender,String player){
		//TODO 创建3个图标， wantedItem jailItem asWantedItem
		YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player);
		Inventory inventory = Bukkit.createInventory(null, 9, Message.getMsg("setPlayerGui.guiName"));
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
		String asWantedTarget = PlayerData.getString("asWanted.target");
		ArrayList<String> asWantedLore = Message.getList("setPlayerGui.asWanted.Lore", asWantedTarget);
		asWantedMeta.setLore(asWantedLore);
		asWantedItem.setItemMeta(asWantedMeta);
		
		
		inventory.setItem(1, wantedItem);
		inventory.setItem(3, jailItem);
		inventory.setItem(5, asWantedItem);
		sender.openInventory(inventory);
	}
}

package vip.foxcraft.pvpaswantedmanager;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Money
{
	private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
    
	protected static boolean setupEconomy()
	{
        if (PVPAsWantedManager.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = PVPAsWantedManager.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    protected static boolean setupChat()
    {
        RegisteredServiceProvider<Chat> rsp = PVPAsWantedManager.getInstance().getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
    
    protected static boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> rsp = PVPAsWantedManager.getInstance().getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    public static Economy getEcononomy()
    {
        return econ;
    }
    
    public static Permission getPermissions()
    {
        return perms;
    }
    
    public static Chat getChat()
    {
        return chat;
    }
    
    public static MoneyOperationResult operate(Object player, Double money, MoneyOperation operation)
    {
    	OfflinePlayer offlinePlayer = null;
    	EconomyResponse response;
    	if (player instanceof String) 
    	{
    		offlinePlayer = Bukkit.getOfflinePlayer((String) player);
    	}
    	else if (player instanceof Player)
    	{
    		offlinePlayer = ((Player) player);
    	}
    	else if (player instanceof OfflinePlayer)
    	{
    		offlinePlayer = ((Player) player);
    	}
    	else if (player instanceof UUID)
    	{
    		offlinePlayer = Bukkit.getOfflinePlayer((UUID) player);
    	}
    	else
    	{
    		return new MoneyOperationResult(false, 0.000001);
    	}
    	
		if (operation == MoneyOperation.GIVE) response = econ.depositPlayer(offlinePlayer, money);
		else if (operation == MoneyOperation.TAKE) response = econ.withdrawPlayer(offlinePlayer, money);
		else if (operation == MoneyOperation.SEE) response = econ.getBalance(offlinePlayer, money);
    	return new MoneyOperationResult(response.transactionSuccess();
    }
}

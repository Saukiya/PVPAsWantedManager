package vip.foxcraft.pvpaswantedmanager.Util;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Money {
	  private static Economy economy;
	  private static boolean supportVault = false;

	  public static boolean setupEconomy()
	  {
	    RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
	    if (economyProvider != null) {
	      economy = (Economy)economyProvider.getProvider();
	    }
	    supportVault = economy != null;
	    return supportVault;
	  }

	  public static boolean isSupportVault()
	  {
	    if (economy != null) return true;
	    return setupEconomy();
	  }

	  
	@SuppressWarnings("deprecation")
	public static void give(String player, double money)
	  {
	    if (economy == null) throw new UnsupportedOperationException("Without this plugin, Vault");
	    economy.depositPlayer(player, money);
	  }

	  @SuppressWarnings("deprecation")
	public static void take(String player, double money)
	  {
	    if (economy == null) throw new UnsupportedOperationException("Without this plugin, Vault");
	    economy.withdrawPlayer(player, money);
	  }

	  @SuppressWarnings("deprecation")
	public static double getBalance(String player)
	  {
	    if (economy == null) throw new UnsupportedOperationException("Without this plugin, Vault");
	    return economy.getBalance(player);
	  }

	  public static boolean has(String player, double money)
	  {
	    if (economy == null) throw new UnsupportedOperationException("Without this plugin, Vault");
	    return getBalance(player) >= money;
	  }
}

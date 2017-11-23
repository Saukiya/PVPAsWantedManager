package vip.foxcraft.pvpaswantedmanager;

public class Setup
{
	public static boolean setup()
	{
		//以后其他API可以在这里加上
		return setupVault();
	}
	
	public static boolean setupVault()
	{
		if (!Money.setupEconomy() ) 
		{
            PVPAsWantedManager.getInstance().disablePlugin(String.format("[%s] - Disabled due to no Vault dependency found!", PVPAsWantedManager.getInstance().getDescription().getName()));
            return false;
        }
		Money.setupPermissions();
		Money.setupChat();
		return true;
	}
}

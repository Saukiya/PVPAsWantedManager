package vip.foxcraft.pvpaswantedmanager.Util;

import org.bukkit.entity.Player;

import org.bukkit.configuration.file.YamlConfiguration;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import vip.foxcraft.pvpaswantedmanager.PVPAsWantedManager;

public class Placeholders  extends EZPlaceholderHook{
	
	@SuppressWarnings("unused")
	private PVPAsWantedManager ourPlugin;

	public Placeholders(PVPAsWantedManager ourPlugin) {
		super(ourPlugin, "pawm");
		this.ourPlugin = ourPlugin;
	}

	@Override
	public String onPlaceholderRequest(Player player, String string) {
		
		YamlConfiguration PlayerData = PVPAsWantedManager.onLoadData(player.getName());
		if(string.equals("wanted_points")) return String.valueOf(PlayerData.getInt("wanted.points"));
		if(string.equals("wanted_cumulativepoints")) return String.valueOf(PlayerData.getInt("wanted.cumulativePoints"));
		if(string.equals("wanted_highestpoints")) return String.valueOf(PlayerData.getInt("wanted.highestPoints"));
		if(string.equals("jail_times")) return String.valueOf(PlayerData.getInt("jail.times"));
		if(string.equals("jail_cumulativenumber")) return String.valueOf(PlayerData.getInt("jail.cumulativeNumber"));
		if(string.equals("aswanted_target"))return String.valueOf(PlayerData.getString("asWanted.target"));
		if(string.equals("aswanted_cumulativenumber")) return String.valueOf(PlayerData.getInt("asWanted.cumulativenumber"));
		if(string.equals("aswanted_continuitynumber")) return String.valueOf(PlayerData.getInt("asWanted.continuitynumber"));
		return "§c请核对你的变量是否正确!";
	}

}

package me.blockcat.friendsop;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

	private Friends plugin = null;
	private static FileConfiguration config = null;
	
	public Config(Friends plugin) {
		this.plugin = plugin;
		
		config = plugin.getConfig();
		
		if (!config.contains("database.host")) config.set("database.host", "localhost");
		if (!config.contains("database.port")) config.set("database.port", "3306");
		if (!config.contains("database.database")) config.set("database.database", "FriendsOp");
		if (!config.contains("database.username")) config.set("database.username", "root");
		if (!config.contains("database.password")) config.set("database.password", "");
		if (!config.contains("player.friendprefix")) config.set("player.friendprefix", "&9");
		
		Friends.instance.saveConfig();
	}

	public static String getURL() {
		String host = config.getString("database.host", "localhost");
		String port = config.getString("database.port", "3306");
		String database = config.getString("database.database", "FriendsOp");
		return String.format("jdbc:mysql://%s:%s/%s", host, port, database);
		
	}

	public static String getUsername() {
		return config.getString("database.username", "root");
	}

	public static String getPass() {
		return config.getString("database.password", "");
	}
	
	public static String getChatColor() {
		return ChatColor.translateAlternateColorCodes('&', config.getString("player.friendprefix"));
	}

}

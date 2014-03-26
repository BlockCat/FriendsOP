package me.blockcat.friendsop;

import java.util.List;

import me.blockcat.friendsop.Pool.ConnectionFactory;
import me.blockcat.friendsop.Pool.DatabaseHandler;
import me.blockcat.friendsop.Pool.ObjectPool;
import me.blockcat.friendsop.Pool.Pool;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.tag.PlayerReceiveNameTagEvent;
import org.kitteh.tag.TagAPI;

import com.mysql.jdbc.Connection;

public class Friends extends JavaPlugin implements Listener{

	public static Pool<Connection> connectionPool = null;
	public static Friends instance = null;
	private FriendCommands commands = new FriendCommands();
	private ChatColor nameColor = ChatColor.BLUE;

	public void onEnable() {
		instance = this;
		connectionPool = new ObjectPool<Connection>();
		connectionPool.setFactory(new ConnectionFactory());

		Config config = new Config(this);

		if (DatabaseHandler.checkTables()) {
			System.out.println("[FriendsOP] Succesfully connected to database!");
		} else {
			System.out.println("[FriendsOP] Shutting down...");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		this.getCommand("friends").setExecutor(commands);
		this.getCommand("fr").setExecutor(commands);

		if (Bukkit.getPluginManager().getPlugin("TagAPI") != null) {
			System.out.println("[FriendsOP] TagAPI found,");
			getServer().getPluginManager().registerEvents(this, this);
			
			Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin("TagAPI"));
			for (Player p : Bukkit.getOnlinePlayers()) { 
				TagAPI.refreshPlayer(p);
			}
			/*Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

				@Override
				public void run() {
					for (Player p : Bukkit.getOnlinePlayers()) 
						TagAPI.refreshPlayer(p);
				}

			}, 100, 100);*/
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onNameTag(PlayerReceiveNameTagEvent event) {
		Player player = event.getPlayer();
		Player namedPlayer = event.getNamedPlayer();

		List<String> friends = DatabaseHandler.getFriendList(player.getName());
		for (String s : friends) {
			if (s.equalsIgnoreCase(namedPlayer.getName())) {
				if (event.isModified()) {
					String st = ChatColor.stripColor(event.getTag());
					event.setTag(Config.getChatColor() + st);
				} else {
					event.setTag(Config.getChatColor() + namedPlayer.getName());
				}
			}
		}
	}

	@EventHandler 
	public void onPlayerLogin (final PlayerLoginEvent event) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (!p.getName().equalsIgnoreCase(event.getPlayer().getName())) {
						TagAPI.refreshPlayer(p, event.getPlayer());
					}
				}
				TagAPI.refreshPlayer(event.getPlayer());
			}

		}, 20L);

	}
}

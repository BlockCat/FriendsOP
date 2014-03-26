package me.blockcat.friendsop;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FriendPlayer {

	String playerName = null;

	private FriendPlayer(OfflinePlayer offline) {
		this.playerName = offline.getName();
	}

	public String getName() {
		return playerName;
	}

	public Player getPlayer() {
		OfflinePlayer offline = Bukkit.getOfflinePlayer(playerName);
		return offline.getPlayer();
	}

	public static FriendPlayer getPlayer(String player) {
		OfflinePlayer offline = Bukkit.getOfflinePlayer(player);
		if (offline.hasPlayedBefore()) {
			return new FriendPlayer(offline);
		}
		return null;
	}
}

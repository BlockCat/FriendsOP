package me.blockcat.friendsop;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import me.blockcat.friendsop.Pool.DatabaseHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FriendCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(colorString("$6Friends:"));
			sender.sendMessage(colorString("$c~~~~~~~~~"));
			sender.sendMessage(colorString("$4Only players may have friends"));
			sender.sendMessage(colorString("$c~~~~~~~~~"));
			return true;
		}
		Player player = (Player) sender;

		if (args.length == 0) {
			showList(player);
			return true;
		}

		try {
			int page = Integer.parseInt(args[0]);
			showList(player, page);
			return true;
		} catch (Exception e) {

		}

		//Add a new friends, done
		if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("invite")) {

			if (args.length > 1) {
				if (args[1].equalsIgnoreCase(player.getName())) {
					this.sendMessage(player, "$6Friends", new String[] {"$4You can't befriend yourself."});
					return true;
				}
				invite(player, args[1]);				
			} else {
				this.sendMessage(player, "$6Friends",new String[] { "$4Which player do you want to add? Write down his/her complete name!"});
			}
			return true;

			//Show help, done
		} else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
			showHelp(player);
			return true;

			//Accept a request, done
		} else if(args[0].equalsIgnoreCase("acc") || args[0].equalsIgnoreCase("accept")) {
			if (args.length > 1) {
				acceptInvitation(player, args[1]);
			} else {
				this.sendMessage(player, "$6Friends",new String[] { "$4Which player do you want to accept Write down his/her complete name!"});
				return true;
			}

		} else if(args[0].equalsIgnoreCase("ab") || args[0].equalsIgnoreCase("abort")) {
			if (args.length > 1) {
				abortInvitation(player, args[1]);
			} else {
				this.sendMessage(player, "$6Friends", new String[] {"$4Which invitation do you want to abort?"});
				return true;
			}			

			//Decline a request
		} else if(args[0].equalsIgnoreCase("den") || args[0].equalsIgnoreCase("decline") || args[0].equalsIgnoreCase("deny")) {
			if (args.length > 1) {
				declineInvitation(player, args[1]);
			} else {
				this.sendMessage(player, "$6Friends",new String[] { "$4Which player do you want to decline? Write down his/her complete name!"});
				return true;
			}

			//Delete a friend, done
		} else if(args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("remove")) {
			if (args.length > 1) {
				deleteFriend(player, args[1]);
			} else {
				this.sendMessage(player, "$6Friends",new String[] { "$4Which player do you want to remove? Write down his/her complete name!"});
				return true;
			}
			//Show requests, done
		} else if(args[0].equalsIgnoreCase("req") || args[0].equalsIgnoreCase("requests")) {
			try {
				showRequests(player, Integer.parseInt(args[1]));
			} catch (Exception e) {
				showRequests(player);
			}
		} else if(args[0].equalsIgnoreCase("inv") || args[0].equalsIgnoreCase("invitations")) {
			try {
				showInvitations(player, Integer.parseInt(args[1]));
			} catch (Exception e) {
				showInvitations(player);
			}
		}
		return true;
	}


	private void showHelp(Player player) {
		player.sendMessage(colorString("$6Help:"));
		player.sendMessage(colorString("$c~~~~~~~~~"));
		player.sendMessage(colorString("$a/friends $3<page> ~ $bShows who is online!"));
		player.sendMessage(colorString("$a/friends $6help ~ $bShows the help."));
		player.sendMessage(colorString("$a/friends $6invite $3<name> ~ $bInvites the player to be your friend!"));
		player.sendMessage(colorString("$a/friends $6abort $3<name> ~ $bcancels the invitation to a player!"));
		player.sendMessage(colorString("$a/friends $6accept $3<name> ~ $bAccept an invitation from a player!"));
		player.sendMessage(colorString("$a/friends $6decline $3<name> ~ $bDecline the invitation from a player!"));
		player.sendMessage(colorString("$a/friends $6requests $3<page> ~ $bShows the open requests!"));
		player.sendMessage(colorString("$a/friends $6invitations $3<page> ~ $bShows the open invitations!"));
		player.sendMessage(colorString("$a/friends $6remove $3<name> ~ $bRemove a friend from your list!"));
		player.sendMessage(colorString("$c~~~~~~~~~"));
	}

	private void showInvitations(Player player) {
		showInvitations(player, 1);
	}

	private void showInvitations(Player player, int page) {
		List<String> list = DatabaseHandler.getInvitations(player.getName());

		if (list.size() == 0) {
			this.sendMessage(player, "$6Invitations",new String[] { "$bYou have no invitations!"});
			return;
		}

		List<String> messages = new ArrayList<String>();

		for (String requester : list) {
			messages.add("$3<$9" + requester + "$3>");
		}

		Collections.sort(messages);

		int pages = (int) Math.ceil(messages.size()/ 10.0D);

		if (page == 0) {
			page = 1;
		}

		if (page > pages) {
			page = pages;
		}

		String[] messageArray = new String[(messages.size() >= 10) ? 10 : messages.size()];

		for (int i = (page - 1) * 10; i < page * 10; i++) {
			if (messages.size() > i) {
				messageArray[i] = messages.get(i);
			}
		} 

		this.sendMessage(player, "$6Invitations", "$7Page: $2" + page + "/" + pages , messageArray);
	}

	private void showRequests(Player player) {
		showRequests(player, 1);
	}

	private void showRequests(Player player, int page) {
		List<String> list = DatabaseHandler.getRequestions(player.getName());

		if (list.size() == 0) {
			this.sendMessage(player, "$6Requests",new String[] { "$bYou have no requests!"});
			return;
		}

		List<String> messages = new ArrayList<String>();

		for (String requester : list) {
			messages.add("$3<$9" + requester + "$3>");
		}

		Collections.sort(messages);

		int pages = (int) Math.ceil(messages.size()/10.0D);

		if (page == 0) {
			page = 1;
		}

		if (page > pages) {
			page = pages;
		}

		String[] messageArray = new String[(messages.size() >= 10) ? 10 : messages.size()];

		for (int i = (page - 1) * 10; i < page * 10; i++) {
			if (messages.size() > i) {
				messageArray[i] = messages.get(i);
			}
		} 

		this.sendMessage(player, "$6Requests", "$7Page: $2" + page + "/" + pages , messageArray);
	}

	private void deleteFriend(Player player, String friend) {
		FriendPlayer fp = FriendPlayer.getPlayer(friend);

		if (fp == null) {
			this.sendMessage(player, "$6Friends", new String[] {"$4Player not found, incorrect name?"});
			return;
		}

		//So it is the correct name with Capitals.
		friend = fp.getName();

		if (!DatabaseHandler.hasFriends(player.getName(), friend)) {
			this.sendMessage(player, "$6Friends", new String[] {"$4You do not have $a" + friend + "$4 as friend."});
			return;
		}

		DatabaseHandler.removeFriends(player.getName(), friend);		
		this.sendMessage(player, "$6Friends", new String[] {"$6You have removed $a" + friend + " $6 from your friends."});

		Player rec = fp.getPlayer();
		if (rec != null && rec.isOnline()) {
			this.sendMessage(rec, "$6Friends", new String[] {"$6" + player.getName() + " $4has removed you from their friends."});
		}

	}

	private void abortInvitation(Player player, String invitated) {
		FriendPlayer fp = FriendPlayer.getPlayer(invitated);

		if (fp == null) {
			this.sendMessage(player, "$6Friends", new String[] {"$4Player not found, incorrect name?"});
			return;
		}

		invitated = fp.getName();

		if (!DatabaseHandler.isRequested(player.getName(), invitated)) {
			this.sendMessage(player, "$6Friends", new String[] {"$4You have not invited this player!"});
			return;
		}

		DatabaseHandler.removeRequest(player.getName(), invitated);
		this.sendMessage(player, "$6Friends", new String[] {"$6You have aborted your invitation to $b" + invitated});
	}

	private void declineInvitation(Player player, String sender) {
		FriendPlayer fp = FriendPlayer.getPlayer(sender);

		if (fp == null) {
			this.sendMessage(player, "$6Friends", new String[] {"$4Player not found, incorrect name?"});
			return;
		}

		sender = fp.getName();

		if (!DatabaseHandler.isRequested(sender, player.getName())) {
			this.sendMessage(player, "$6Friends", new String[] {"$4This player has not requested a friendship!"});
			return;
		}

		DatabaseHandler.removeRequest(sender, player.getName());
		this.sendMessage(player, "$6Friends", new String[] {"$6You have declined: $b" + sender + " $6 to be your friend!"});

		Player senderPlayer = fp.getPlayer();
		if (senderPlayer != null && senderPlayer.isOnline()) {
			this.sendMessage(senderPlayer, "$6Friends", new String[] {"$6" + player.getName() + " $a declined your invitation!"});
		}

	}

	private void acceptInvitation(Player player, String sender) {
		FriendPlayer fp = FriendPlayer.getPlayer(sender);
		if (fp == null) {
			this.sendMessage(player, "$6Friends", new String[] {"$4Player not found, incorrect name?"});
			return;
		}

		sender = fp.getName();

		if (!DatabaseHandler.isRequested(sender, player.getName())) {
			this.sendMessage(player, "$6Friends", new String[] {"$4This player has not requested a friendship!"});

			return;
		}

		DatabaseHandler.removeRequest(sender, player.getName());
		DatabaseHandler.addFriends(sender, player.getName());

		this.sendMessage(player, "$6Friends", new String[] {"$aYou have accepted: $b" + sender + " $a to be your friend!"});

		Player senderPlayer = fp.getPlayer();
		if (senderPlayer != null && senderPlayer.isOnline()) {
			this.sendMessage(senderPlayer, "$6Friends", new String[] {"$b" + player.getName() + " $a is now your friend!"});
		}

	}

	private void invite(Player player, String invited) {
		//test if already has this friends
		FriendPlayer fp = FriendPlayer.getPlayer(invited);

		if (fp == null) {
			this.sendMessage(player, "$6Friends", new String[] {"$4Player not found, incorrect name?"});
			return;
		}

		invited = fp.getName();

		if (DatabaseHandler.hasFriends(player.getName(), invited)) {
			player.sendMessage(colorString("$bYou have already befriended $a" + invited));
			this.sendMessage(player, "$6Friends", new String[] {"$bYou have already befriended $a" + invited});
			return;
		}

		if (DatabaseHandler.isRequested(player.getName(), invited)) {
			this.sendMessage(player, "$6Friends", new String[] {"$4You have already send an invitation to this player."});
			return;
		}

		DatabaseHandler.sendRequest(player.getName(), invited);
		this.sendMessage(player, "$6Friends", new String[] {"$bYou have send an invitation to: $a" + invited});

		Player rec = fp.getPlayer();
		if (rec != null && rec.isOnline()) {
			this.sendMessage(rec, "$6Friends", new String[] {
					"$b" + player.getName()  +" has send you an invitation to become friends!",
					"$a/friends accept " + player.getName() + " $6~To accept.",
					"$c/friends decline " + player.getName() + " $6~To decline."
			});
		}


	}

	private void showList(Player player) {
		showList(player, 1);
	}

	private void showList(Player player, int page) {
		List<String> friends = DatabaseHandler.getFriendList(player.getName());

		if (friends.size() == 0) {
			this.sendMessage(player, "$6Friends", new String[] {"$4You do not have friends yet, go make some!"});
			return;
		}

		Calendar calendar = new GregorianCalendar();
		List<String> messages = new ArrayList<String>();

		for (String friend : friends) {
			OfflinePlayer offline = Bukkit.getOfflinePlayer(friend);
			long millis = System.currentTimeMillis() - offline.getLastPlayed();
			calendar.setTimeInMillis(millis);
			int seconds = calendar.get(Calendar.SECOND);
			int minutes = calendar.get(Calendar.MINUTE);
			int hours = calendar.get(Calendar.HOUR_OF_DAY) - 1;
			int days = calendar.get(Calendar.DAY_OF_YEAR) - 1;
			int months = calendar.get(Calendar.MONTH);
			int years = calendar.get(Calendar.YEAR) - 1970;

			String sinceString = null;

			if (years > 0) {
				sinceString = years + (years == 1 ? " Year." : " Years.");
			} else if (months > 0) {
				sinceString = months + (months == 1 ? " Month." : " Months.");
			} else if (days > 0) {
				sinceString = days + (days == 1 ? " Day." : " Days.");
			} else if (hours > 0){
				sinceString = hours + (hours == 1 ? " Hour, " : " Hours, ") + minutes + (minutes == 1 ? " Minute." : " Minutes.")  ;
			} else if (minutes > 0) {
				sinceString = minutes + (minutes == 1 ? " Minute." : " Minutes.");
			} else {
				sinceString = seconds + (seconds == 1 ? " Second." : " Seconds");
			}
			Player friendPlayer = offline.getPlayer();
			if (friendPlayer == null) {
				//the strange $0 and $1 are for sorting.
				messages.add("$1$f<$4" + friend + "$f> $6 is: $4 Offline $6 since: $7" + sinceString);
			} else {
				messages.add("$0$f<$a" + friend + "$f> $6 is: $a Online.");
			}
		}
		Collections.sort(messages);


		int pages = (int) Math.ceil(messages.size()/10.0D);

		if (page == 0) {
			page = 1;
		}

		if (page > pages) {
			page = pages;
		}


		String[] messageArray = new String[(messages.size() >= 10) ? 10 : messages.size()];

		for (int i = (page - 1) * 10; i < page * 10; i++) {
			if (messages.size() > i) {
				messageArray[i] = messages.get(i);
			}
		} 

		this.sendMessage(player, "$6Friends", "$7Page: $2" + page + "/" + pages , messageArray);		
	}

	private String colorString(String str) {
		if (str != null) {
			return ChatColor.translateAlternateColorCodes('$', str);
		}
		return null;
	}

	private void sendMessage(Player player, String header, String page ,String[] messages) {
		player.sendMessage(colorString(header + ":"));
		player.sendMessage(colorString(page));
		player.sendMessage(colorString("$c~~~~~~~~~"));
		for (String msg : messages) {
			player.sendMessage(colorString(msg));
		}
		player.sendMessage(colorString("$c~~~~~~~~~"));
	}

	private void sendMessage(Player player, String header, String[] messages) {
		player.sendMessage(colorString(header + ":"));
		player.sendMessage(colorString("$c~~~~~~~~~"));
		for (String msg : messages) {
			player.sendMessage(colorString(msg));
		}
		player.sendMessage(colorString("$c~~~~~~~~~"));
	}

}

package me.blockcat.friendsop.Pool;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.blockcat.friendsop.Friends;

import com.mysql.jdbc.Connection;

public class DatabaseHandler {

	public static boolean checkTables() {
		try {
			execute("SELECT * FROM Friends");
		} catch (SQLException e) {
			try {
				execute("CREATE TABLE Friends(player VARCHAR(15), friend VARCHAR(15))");
			} catch (SQLException e1) {
				return false;
			}
		}
		try {
			execute("SELECT * FROM Requests");
		} catch (SQLException e) {
			try {
				//Here, the player is requests, and the sender sends.
				execute("CREATE TABLE Requests (requested VARCHAR(15), sender VARCHAR(15))");
			} catch (SQLException e1) {
				return false;
			}
		}
		return true;
	}

	public static boolean isRequested(String sender, String requested) {
		try {
			ResultSet result = executeWithResult("SELECT * FROM Requests WHERE requested='" + requested + "' AND sender='" + sender + "'" );
			while(result.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public static void sendRequest(String sender, String requested) {
		try {
			execute(String.format("INSERT INTO Requests (requested, sender) VALUES ('%s', '%s')", requested, sender ));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<String> getRequestions(String player) {
		List<String> list = new ArrayList<String>();

		try {
			System.out.println(player);
			ResultSet result = executeWithResult(String.format("SELECT * FROM Requests WHERE sender='%s'", player));
			while(result.next()) {
				list.add(result.getString("requested"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Collections.sort(list);
		return list;
	}

	public static List<String> getInvitations(String player) {
		List<String> list = new ArrayList<String>();

		try {
			ResultSet result = executeWithResult(String.format("SELECT * FROM Requests WHERE requested='%s'", player));
			while(result.next()) {
				list.add(result.getString("sender"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Collections.sort(list);
		return list;
	}

	public static void removeRequest(String sender, String requested) {
		try {
			execute(String.format("DELETE FROM Requests WHERE requested='%s' AND sender='%s'", requested, sender));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addFriends(String player1, String player2) {
		try {
			execute(String.format("INSERT INTO Friends (player, friend) VALUES ('%s','%s')", player1, player2));
			execute(String.format("INSERT INTO Friends (player, friend) VALUES ('%s','%s')", player2, player1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void removeFriends(String player1, String player2) {
		try {
			execute(String.format("DELETE FROM Friends WHERE player='%s' AND friend='%s'", player1, player2));
			execute(String.format("DELETE FROM Friends WHERE player='%s' AND friend='%s'", player2, player1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<String> getFriendList(String player1) {
		List<String> list = new ArrayList<String>();
		try {
			ResultSet result = executeWithResult("SELECT * FROM Friends WHERE player='" + player1 +"'");
			while(result.next()) {
				String pl = result.getString("friend");
				list.add(pl);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static boolean hasFriends(String player1, String player2) {
		try {
			ResultSet result = executeWithResult("SELECT * FROM Friends WHERE player='" + player1 +"' AND friend='" + player2 + "'");
			while(result.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void execute(String exe) throws SQLException {
		Connection conn = Friends.connectionPool.get();
		if (conn == null) {
			throw new SQLException();
		}
		conn.createStatement().execute(exe);
		Friends.connectionPool.recycle(conn);
		conn = null;
	}

	private static ResultSet executeWithResult(String exe) throws SQLException {
		Connection conn = Friends.connectionPool.get();
		java.sql.Statement st = conn.createStatement();
		st.execute(exe);
		ResultSet rs = st.getResultSet();
		Friends.connectionPool.recycle(conn);
		conn = null;
		return rs;
	}

}

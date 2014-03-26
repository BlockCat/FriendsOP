package me.blockcat.friendsop.Pool;

import java.sql.DriverManager;

import me.blockcat.friendsop.Config;

import com.mysql.jdbc.Connection;

public class ConnectionFactory implements PoolObjectFactory<Connection>{

	@Override
	public Connection newObject() {
		Connection conn = null;
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = (Connection) DriverManager.getConnection(Config.getURL(), Config.getUsername(), Config.getPass());
			return conn;
		}
		catch(Exception e){
			//e.printStackTrace();
			System.out.println("[FriendsOP] Not connected to database, wrong login?");
			return null;
		}
	}

}

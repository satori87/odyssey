package com.bg.bearplane.engine;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class MySQL {

	private static Connection con;

	static UUID uuid = UUID.randomUUID();

	public static String saddress = "";
	public static int sport = 0;
	public static String sdb = "";
	public static String spass = "";
	public static String suser = "";
	public static boolean connected = false;
	//public static long reStamp = 0;

	public static void connectSQL(String address, int port, String db, String user, String pass) {
		saddress = address;
		sport = port;
		sdb = db;
		suser = user;
		spass = pass;
		try {   
			//Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + db, user, pass);
			connected = true;
			
		} catch (Exception e) {
			Log.error(e);
			 connected = false;
			 //reStamp = System.currentTimeMillis() + 100;
		}
	}

	public static ResultSet get(String statement) {
		return get(statement, null);
	}

	// comment
	public static ResultSet get(String statement, LinkedList<Object> objects) {
		try {
			PreparedStatement p = MySQL.con.prepareStatement(statement);
			int c = 1;
			if (objects != null) {
				for (Object o : objects) {
					if (o instanceof Integer) {
						p.setInt(c, (int) o);
					} else if (o instanceof String) {
						p.setString(c, (String) o);
					} else if (o instanceof Long) {
						p.setLong(c, (long) o);
					} else if (o instanceof Timestamp) {
						p.setTimestamp(c, (Timestamp) o);
					} else if (o instanceof Boolean) {
						p.setBoolean(c, (boolean) o);
					} else if (o instanceof Double) {
						p.setDouble(c, (double) o);
					} else if (o instanceof Object) {
						p.setObject(c, o);
					} else {
						p.setNull(c, Types.CHAR);
					}
					c++;
				}
			}
			return p.executeQuery();
		} catch (Exception e) {
			try {
				con.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Log.error(e);
			connected = false;
		}
		return null;
	}

	public static void save(String statement) {
		save(statement, null);
	}

	public static void save(List<RawStatement> statements) {
		try {
			con.setAutoCommit(false);
			for (RawStatement r : statements) {
				save(r.statement, r.objects);
			}
			con.setAutoCommit(true);
		} catch (SQLException e) {
			try {
				con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			Log.error(e);
			connected = false;
		}
	}

	public static void save(String statement, LinkedList<Object> objects) {
		try {
			PreparedStatement p = MySQL.con.prepareStatement(statement);
			int c = 1;
			if (objects != null) {
				for (Object o : objects) {
					if (o instanceof Integer) {
						p.setInt(c, (int) o);
					} else if (o instanceof String) {
						p.setString(c, (String) o);
					} else if (o instanceof Long) {
						p.setLong(c, (long) o);
					} else if (o instanceof Timestamp) {
						p.setTimestamp(c, (Timestamp) o);
					} else if (o instanceof Boolean) {
						p.setBoolean(c, (boolean) o);
					} else if (o instanceof Double) {
						p.setDouble(c, (double) o);
					} else if (o instanceof Object) {
						p.setObject(c, o);
					} else {
						p.setNull(c, Types.CHAR);
					}
					c++;
				}
			}
			p.executeUpdate();
		} catch (SQLException e) {
			try {
				con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			Log.error(e);
			connected = false;
		}
	}

	public static String getUUID() {
		try {
			return java.util.UUID.randomUUID().toString().replace("-", "");
		} catch (Exception e) {
			Log.error(e);
		}
		return "";
	}
}
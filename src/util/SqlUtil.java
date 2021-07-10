package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SqlUtil {
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String DBURL = "jdbc:mysql://localhost:3306/";
	private static final String DBPASS = "root";
	private static final String DBUSER = "root";
	private static final String DBNAME = "DigiStore";
	private static Statement st;
	private static Connection conn;

	public static void connectDB() {
		try {
			Class.forName(DRIVER);
			conn = DriverManager.getConnection(DBURL + DBNAME, DBUSER, DBPASS);
			st = conn.createStatement();
		} catch (Exception ex) {
			System.out.println("Problem in connection" + ex);
		}

	}

	public static void create(String query) {
		try {
			if (st == null) {
				connectDB();
			}
			st.execute(query);
		} catch (Exception ex) {
			System.out.println("Problem in creation" + ex);
		}
	}

	public static void updateData(String query) {
		try {
			if (st == null) {
				connectDB();
			}
			st.executeUpdate(query);
		} catch (Exception ex) {
			System.out.println("Problem in updating " + ex);
		}
	}

	public static ResultSet read(String query) {
		ResultSet rs = null;
		try {
			if (st == null) {
				connectDB();
			}
			rs = st.executeQuery(query);
		} catch (Exception ex) {
			System.out.println("Problem reading data " + ex);
		}
		return rs;
	}

	public static void close() {
		try {
			if (st != null) {
				conn.close();
				st.close();
			}
		} catch (Exception ex) {
			System.out.println("Problem " + ex);
		}
	}

}
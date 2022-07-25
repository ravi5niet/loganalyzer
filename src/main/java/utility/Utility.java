package utility;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Utility {
	static final String tableCreateQueryPath = "create table if not exists logdetail (id VARCHAR(100) NOT NULL,duration numeric,type VARCHAR(100),host VARCHAR(100),alert BOOLEAN,PRIMARY KEY (id));";
	static final String connectionString = "jdbc:hsqldb:file:work/mydatabase";
	private static Logger logger = LogManager.getLogger();
    private static Connection conn=null;
	public Connection getDbConnection() {
		Connection conn = null;
		String createLogDetail = tableCreateQueryPath;

		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			// will create DB if does not exist
			// "SA" is default user with hypersql
			if(conn==null)
			conn = DriverManager.getConnection(connectionString, "SA", "");
			//conn.createStatement().executeUpdate("DROP TABLE logdetail");
			logger.info("create logdetail table in dont exist!!");
			
			conn.createStatement().executeUpdate(createLogDetail);

		} catch (Exception e) {
			logger.error("dbexception " + e.getMessage());
			e.printStackTrace();
		}
		return conn;

		/*
		 * try { // will create DB if does not exist // "SA" is default user with
		 * hypersql return DriverManager.getConnection(connectionString, "SA", "");
		 */
	}
	
	public void dropTable()
	{
		try {
		if(conn==null)
		 conn = DriverManager.getConnection(connectionString, "SA", "");
		
		DatabaseMetaData meta = conn.getMetaData();
		// check if "logdetail" table is there
		ResultSet tables1 = meta.getTables(null, null, "LOGDETAIL", null);
		if (tables1.next()) {
			conn.createStatement().executeUpdate("DROP TABLE LOGDETAIL");
		}
		
		}catch(Exception e)
		{
			logger.error("dbexception " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static String readToString(String fname) {
		String string = "";
		try {
			File file = new File(fname);
			string = FileUtils.readFileToString(file, "utf-8");
		} catch (Exception e) {

		}
		return string;
	}

}

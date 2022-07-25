package services;

import ServiceImpl.LogService;
import model.LogEntry;
import model.LogEntryPersist;
import utility.Utility;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LogServiceImpl implements LogService {

	static Connection con = null;
	static final String connectionString = "jdbc:hsqldb:file:work/mydatabase";
	private static Logger logger = LogManager.getLogger();
	/*
	 * private static final String sql =
	 * "INSERT INTO LOGDETAIL (id, duration, type, host, alert) " +
	 * " Values (?, ?, ?, ?, ?)";
	 */
	
	private static final String sql ="MERGE INTO LOGDETAIL d1 " +
    "USING (VALUES ?, ?, ?, ?, ?) d2 (id, duration, type, host, alert) " +
    "    ON (d1.id = d2.id) " +
    " WHEN MATCHED THEN UPDATE SET " +
    " d1.duration = d2.duration, d1.type = d2.type,  d1.host = d2.host, d1.alert = d2.alert " +
    " WHEN NOT MATCHED THEN INSERT (id, duration, type, host, alert) VALUES (d2.id, d2.duration, d2.type,d2.host,d2.alert)";
	private static final Utility util = new Utility();

	@Override
	public void readFileUpdate(String path) {
		
		//before reading new file drop table first other wise we will get old log file recore also 
		util.dropTable();
		File file = new File(path);
		List<LogEntryPersist> logEntryList = new ArrayList<>();//storing LogEntryPersist upto 10 record save it to DB
		Map<String, LogEntry> logEntryMap = new HashMap<>();// this map object is used for storing log entry if same id
															// repeat then
		LineIterator it = null;
		try {
			it = FileUtils.lineIterator(file, "UTF-8");
			ObjectMapper mapper = new ObjectMapper();
			while (it.hasNext()) {
				String line = it.nextLine();
				LogEntry entry = mapper.readValue(line, LogEntry.class);
				logger.debug("Entry OBJ:" + entry.getId() + " " + entry.getHost() + " " + entry.getState() + " "
						+ entry.getTimestamp());
				extracted(logEntryList, logEntryMap, entry);

			}
			// to update remaining records in case record is less then 10
			updateDb(logEntryList);
			// select everything
		    printRecord();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			LineIterator.closeQuietly(it);
		}

	}

	private void extracted(List<LogEntryPersist> logEntryList, Map<String, LogEntry> logEntryMap, LogEntry entry) {
		if (logEntryMap.get(entry.getId()) == null) {
			logEntryMap.put(entry.getId(), entry);
		} else {
			long duration = (logEntryMap.get(entry.getId()).getTimestamp() > entry.getTimestamp())
					? logEntryMap.get(entry.getId()).getTimestamp() - entry.getTimestamp()
					: entry.getTimestamp() - logEntryMap.get(entry.getId()).getTimestamp();
			LogEntryPersist lgpersist = null;
			lgpersist = new LogEntryPersist(entry.getId(), duration, entry.getType(), entry.getHost(),
					duration > 4 ? true : false);	
			logEntryList.add(lgpersist);
			logEntryMap.remove(entry.getId());
			if (logEntryList.size() % 10 == 0) {
			persistLogRecords(logEntryList);
			}

		}
	}

//considering that record may be bulk in size so updating table with chunck of 10 record 
	private void persistLogRecords(List<LogEntryPersist> logEntryList) {
		
			logger.info("update 10 record from persistLogRecords");
			updateDb(logEntryList);
			logEntryList.clear();
		}
	

	@Override
	public void updateDb(List<LogEntryPersist> logEntryList) {

		try {
			// will create DB if does not exist
			// "SA" is default user with hypersql
			con = util.getDbConnection();
			
			// add contacts
			PreparedStatement pstmt = con.prepareStatement(sql);
			for (LogEntryPersist lgEntry : logEntryList) {
				pstmt.setString(1, lgEntry.getId());
				pstmt.setLong(2, lgEntry.getDuration());
				//if (lgEntry.getType() != null)
					pstmt.setString(3, lgEntry.getType());
				//if (lgEntry.getHost() != null)
					pstmt.setString(4, lgEntry.getHost());
				pstmt.setBoolean(5, lgEntry.isAlert());
				
				pstmt.executeUpdate();
			}
			

		} catch (SQLException e) {
			logger.error("exception on DB " + e.getMessage());
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void printRecord() throws SQLException {
		con = util.getDbConnection();
		PreparedStatement pst = con.prepareStatement("select * from logdetail");
		pst.clearParameters();
		ResultSet rs = pst.executeQuery();
		List<LogEntryPersist> lgEntryList = new ArrayList<>();
		while (rs.next()) {
			lgEntryList.add(new LogEntryPersist(rs.getString(1), rs.getLong(2), rs.getString(3), rs.getString(4),
					rs.getBoolean(5)));
		}
		System.out.println("log Id" + "       " + "Duration" + "   " + "Alert");
		//will print all record multiple time 
		for (LogEntryPersist log : lgEntryList) {
			logger.debug("retrive data from logdetail table id: "+log.getId() + "  " + "duration : " +log.getDuration() + "      " + "Alert : "+ log.isAlert());
			System.out.println(log.getId() + "   " + log.getDuration() + "          " + log.isAlert());
		}
	}

	
	  public static void main(String[] args) throws Exception { 
		  LogServiceImpl lg =new LogServiceImpl();  
		  lg.readFileUpdate("C:/Personal/workSpace/credit-suisse-test/lotextfile/logfile.txt");
	  
	  }
	 

}

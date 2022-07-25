package JunitTest;

//import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ServiceImpl.LogService;
import model.LogEntryPersist;
import services.LogServiceImpl;
import utility.Utility;

public class LogAnalyzerJunit {

	private static final String textFilePath="./src/main/java/JunitTestTextFile/logfile.txt";
	private static final Logger logger = LogManager.getLogger();
	
	@Test
	public void test() {
		//fail("Not yet implemented");
		Utility util= new Utility();
		Connection con=util.getDbConnection();
		System.out.println("Junit test start test");
		LogService lgServ=new LogServiceImpl();
		lgServ.readFileUpdate(textFilePath);
		
		try {
		
		PreparedStatement pst = con.prepareStatement("select * from logdetail");
		pst.clearParameters();
		ResultSet rs = pst.executeQuery();
		List<LogEntryPersist> lgEntryList = new ArrayList<>();
		while (rs.next()) {
			lgEntryList.add(new LogEntryPersist(rs.getString(1), rs.getLong(2), rs.getString(3), rs.getString(4),
					rs.getBoolean(5)));
		}
		for (LogEntryPersist log : lgEntryList) {
			logger.debug("retrive data from logdetail table id: "+log.getId() + "  " + "duration : " +log.getDuration() + " " + "Alert : "+ log.isAlert());
			System.out.println(log.getId() + "  " + log.getDuration() + " " + log.isAlert());
			//if duration is more then 4 ms then Alert must be true 
			
			//if duration is more then 4 ms then Alert must be true
			if(log.getDuration()<=4)
			assertFalse((log.getDuration() <= 4) && log.isAlert());
			else
			assertTrue((log.getDuration() > 4) && log.isAlert());
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
	
	/*uncomment if want to test for sucess 
	 * public static void main(String[] args) {
	 * 
	 * Result result = JUnitCore.runClasses(LogAnalyzerJunit.class);
	 * 
	 * for (Failure failure : result.getFailures()) {
	 * System.out.println(failure.toString()); }
	 * 
	 * System.out.println(" Sucess Full Result :" +result.wasSuccessful()); }
	 */

	

}

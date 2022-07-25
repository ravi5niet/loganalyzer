package ravi;

import ServiceImpl.LogService;
import services.LogServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * Hello world!
 *
 */
public class App 
{
	 private static Logger logger = LogManager.getLogger();
    public static void main( String[] args )
    {
    	System.out.println("App Strat!");
    	 logger.info("App Strat!");
    	//String path ="./src/main/java/JunitTestTextFile/logfile.txt";
    	logger.info("path provided :" +args[0]);
    	LogService lgServ=new LogServiceImpl();
    	lgServ.readFileUpdate(args[0]);
    	System.out.println("App End!");
    	 
    }
}

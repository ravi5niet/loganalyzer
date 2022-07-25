package ServiceImpl;
import model.LogEntryPersist;
import java.util.List;

public interface LogService {
	
	void readFileUpdate(String path);
	void updateDb(List<LogEntryPersist> logEntryList );

}

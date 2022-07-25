package model;

public class LogEntry {
	
	private String id;
	private long timestamp;
	private String type;
	private String host;
	private boolean alert;
	private String state;
	
	
	public LogEntry()
	{
		
	}
	
	/*
	 * public LogEntry(String id, String type, String host, boolean alert) {
	 * super(); this.id = id; //this.duration = duration; this.type = type;
	 * this.host = host; this.alert = alert; }
	 */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	/*
	 * public long getDuration() { return duration; } public void setDuration(long
	 * duration) { this.duration = duration; }
	 */
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public boolean isAlert() {
		return alert;
	}
	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	
	
}

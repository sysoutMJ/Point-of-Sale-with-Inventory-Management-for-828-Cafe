package search.model;

import java.time.LocalDateTime;

public class SearchLogsModel {

	private int eventId;
    private String uniqueEventId;
    private String eventName;
    private String user;
    private LocalDateTime eventDatetime;
    
    public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public String getUniqueEventId() {
		return uniqueEventId;
	}
	public void setUniqueEventId(String uniqueEventId) {
		this.uniqueEventId = uniqueEventId;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public LocalDateTime getEventDatetime() {
		return eventDatetime;
	}
	public void setEventDatetime(LocalDateTime eventDatetime) {
		this.eventDatetime = eventDatetime;
	}
	

}

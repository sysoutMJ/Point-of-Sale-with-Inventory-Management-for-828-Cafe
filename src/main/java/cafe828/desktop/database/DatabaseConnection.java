package cafe828.desktop.database;

public class DatabaseConnection {
	
	// Can be edited based on the user's MySQL Database.
	
	private final String URL = "jdbc:mysql://localhost:3306/828cafe?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private final String USERNAME = "828Cafe";
    private final String PASSWORD = "Cafe828#2026";
    
	public String getUrl() {
		return URL;
	}
	public String getUsername() {
		return USERNAME;
	}
	public String getPassword() {
		return PASSWORD;
	}
}

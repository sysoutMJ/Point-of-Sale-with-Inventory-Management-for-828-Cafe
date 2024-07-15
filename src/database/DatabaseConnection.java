package database;

public class DatabaseConnection {
	
	// Can be edited based on the user's MySQL Database.
	
	private final String url = "jdbc:mysql://localhost:3306/828cafe";
    private final String username = "828Cafe";
    private final String password = "828Cafe!";
    
	public String getUrl() {
		return url;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
}

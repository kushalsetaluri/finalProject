import java.sql.*;

public class DatabaseSetup {

    private static final String DB_URL = "jdbc:sqlite:kids_learning.db"; // Or MySQL, PostgreSQL, etc.

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            createTables(conn);
            insertInitialData(conn);
            System.out.println("Database setup complete.");

        } catch (SQLException e) {
            System.err.println("Database setup error: " + e.getMessage());
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String createUsersTable = """
                    CREATE TABLE users (
                        user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT UNIQUE NOT NULL,
                        password TEXT NOT NULL -- In a real app, store password hashes here!
                    );
                    """;

            String createUserScoresTable = """
                    CREATE TABLE user_scores (
                        user_id INTEGER PRIMARY KEY,
                        score INTEGER DEFAULT 0,
                        FOREIGN KEY (user_id) REFERENCES users(user_id)
                    );
                    """;

            String createItemsTable = """
                    CREATE TABLE items (
                        item_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        item_name TEXT NOT NULL,
                        category TEXT NOT NULL -- e.g., "Objects", "Fruits", "Animals"
                    );
                    """;

            stmt.execute(createUsersTable);
            stmt.execute(createUserScoresTable);
            stmt.execute(createItemsTable);

            System.out.println("Tables created successfully.");
        }
    }

    private static void insertInitialData(Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            // In a real app, you would hash the password here!
            pstmt.setString(1, "testuser");
            pstmt.setString(2, "password");  //  Hash password before storing
            pstmt.executeUpdate();
        }

        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO user_scores (user_id) VALUES (?)")) {
            pstmt.setInt(1, 1);
            pstmt.executeUpdate();
        }

        // Insert items (using a loop for efficiency)
        String[] items = {"Apple", "Banana", "Cat", "Dog", "Table", "Chair"};
        String[] categories = {"Fruits", "Fruits", "Animals", "Animals", "Objects", "Objects"};

        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO items (item_name, category) VALUES (?, ?)")) {
             for (int i = 0; i < items.length; i++) {
                pstmt.setString(1, items[i]);
                pstmt.setString(2, categories[i]);
                pstmt.executeUpdate();
            }
        }

        System.out.println("Initial data inserted successfully.");
    }
}
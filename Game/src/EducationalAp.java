import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EducationalAp {

    private static final String DB_URL = "jdbc:sqlite:kids_learning.db"; // Or MySQL connection details

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int userId = authenticateUser(scanner);

        if (userId != -1) { // Authentication successful
            LearningModule learningModule = new LearningModule(userId);
            learningModule.startLearning(scanner);
        } else {
            System.out.println("Authentication failed. Exiting.");
        }
        scanner.close();
    }


    private static int authenticateUser(Scanner scanner) {
        // In a real application, you'd hash passwords for security.
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT user_id FROM users WHERE username = ? AND password = ?")) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return -1; // Authentication failed
    }



    static class LearningModule {
        private int userId;
        private int score;

        public LearningModule(int userId) {
            this.userId = userId;
            this.score = getScoreFromDatabase(userId); // Load initial score
        }

        private int getScoreFromDatabase(int userId) {
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement("SELECT score FROM user_scores WHERE user_id = ?")) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("score");
                }
            } catch (SQLException e) {
                System.err.println("Database error getting score: " + e.getMessage());
            }
            return 0; // Default score if not found
        }


        public void startLearning(Scanner scanner) {
            // Example learning content (you would expand this)
            String[] categories = {"Objects", "Fruits", "Animals"};

            for (String category : categories) {
                System.out.println("\n--- " + category + " ---");
                List<String> items = getItemsForCategory(category); // Fetch from DB

                for (String item : items) {
                    System.out.println("What is this? (Type the answer): " + item);  // Use visual aids here if possible.
                    String answer = scanner.nextLine();

                    if (answer.equalsIgnoreCase(item)) { // Case-insensitive comparison
                        System.out.println("Correct!");
                        score++;
                        updateScoreInDatabase();
                    } else {
                        System.out.println("Incorrect. The answer is: " + item);
                    }
                }
            }
            System.out.println("\nLearning complete. Final score: " + score);
        }

        private List<String> getItemsForCategory(String category) {
            List<String> items = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement("SELECT item_name FROM items WHERE category = ?")) {
                pstmt.setString(1, category);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    items.add(rs.getString("item_name"));
                }
            } catch (SQLException e) {
                System.err.println("Error fetching items: " + e.getMessage());
            }
            return items;
        }

        private void updateScoreInDatabase() {
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement("UPDATE user_scores SET score = ? WHERE user_id = ?")) {
                pstmt.setInt(1, score);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error updating score: " + e.getMessage());
            }
        }
    }
}
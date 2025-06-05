import java.io.*;
import java.util.*;

public class EnhancedRecommendationSystem {
    private static final String DATA_FILE = "data.csv";
    private static Map<Integer, Map<Integer, Double>> userRatings = new HashMap<>();

    public static void main(String[] args) {
        setupDataFile(DATA_FILE);
        loadData(DATA_FILE);

        // Allow user to input their ID for personalized recommendations
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your User ID for recommendations: ");
        int userId = scanner.nextInt();

        List<Integer> recommendations = recommendItems(userId);
        displayRecommendations(userId, recommendations);
    }

    private static void setupDataFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("UserID,ItemID,Rating");
                writer.println("1,101,4.5");
                writer.println("1,205,3.0");
                writer.println("2,101,5.0");
                writer.println("2,305,2.0");
                writer.println("3,205,4.0");
                System.out.println("Sample data file created: " + fileName);
            } catch (IOException e) {
                System.err.println("Error creating data file: " + e.getMessage());
            }
        }
    }

    private static void loadData(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int userId = Integer.parseInt(parts[0]);
                int itemId = Integer.parseInt(parts[1]);
                double rating = Double.parseDouble(parts[2]);

                userRatings.putIfAbsent(userId, new HashMap<>());
                userRatings.get(userId).put(itemId, rating);
            }
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    private static List<Integer> recommendItems(int userId) {
        Map<Integer, Double> userItems = userRatings.get(userId);
        if (userItems == null) {
            System.out.println("User ID not found. No recommendations available.");
            return Collections.emptyList();
        }

        Map<Integer, Double> recommendedItems = new HashMap<>();
        for (int otherUser : userRatings.keySet()) {
            if (otherUser != userId) {
                for (Map.Entry<Integer, Double> entry : userRatings.get(otherUser).entrySet()) {
                    int itemId = entry.getKey();
                    double rating = entry.getValue();

                    if (!userItems.containsKey(itemId)) {
                        recommendedItems.put(itemId, recommendedItems.getOrDefault(itemId, 0.0) + rating);
                    }
                }
            }
        }

        return recommendedItems.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .toList();
    }

    private static void displayRecommendations(int userId, List<Integer> recommendations) {
        System.out.println("\nRecommended Items for User ID " + userId + ":");
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations available.");
        } else {
            for (Integer itemId : recommendations) {
                System.out.println("Item ID: " + itemId);
            }
        }
        System.out.println();
    }
}

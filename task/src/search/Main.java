package search;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    private static List<String> people;  // List to store people
    private static Map<String, Set<Integer>> invertedIndex; // Inverted index for optimized searching
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Check for command line arguments for file input
        if (args.length < 2 || !args[0].equals("--data")) {
            System.out.println("Usage: --data filename");
            return;
        }
        String filename = args[1];
        initializeData(filename);  // Read data from the specified file
        buildInvertedIndex();       // Build the inverted index
        showMenu();
    }

    /** Method to read data from a file and initialize the people list */
    private static void initializeData(String filename) {
        people = new ArrayList<>(); // Using ArrayList for dynamic sizing
        try (FileReader fr = new FileReader(filename);
             Scanner fileScanner = new Scanner(fr)) {
            while (fileScanner.hasNextLine()) {
                people.add(fileScanner.nextLine().trim());  // Add line to list
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }

    /** Method to build the inverted index */
    private static void buildInvertedIndex() {
        invertedIndex = new HashMap<>();
        for (int i = 0; i < people.size(); i++) {
            String[] words = people.get(i).split("\\s+"); // Split by whitespace
            for (String word : words) {
                String lowerCaseWord = word.toLowerCase(); // Normalize to lowercase
                invertedIndex.putIfAbsent(lowerCaseWord, new HashSet<>());
                invertedIndex.get(lowerCaseWord).add(i); // Add line index
            }
        }
    }

    /** Method to show the menu */
    private static void showMenu() {
        int option;
        do {
            System.out.println("\n=== Menu ===");
            System.out.println("1. Find a person");
            System.out.println("2. Print all people");
            System.out.println("0. Exit");
            System.out.print("> ");
            option = Integer.parseInt(scanner.nextLine());
            switch (option) {
                case 1:
                    findPerson();
                    break;
                case 2:
                    printAllPeople();
                    break;
                case 0:
                    System.out.println("Bye!");
                    break;
                default:
                    System.out.println("Incorrect option! Try again.");
            }
        } while (option != 0);
    }

    /** Method to find a person by name or email using the inverted index */
    private static void findPerson() {
        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        String strategy = scanner.nextLine().trim().toUpperCase();

        System.out.println("Enter a name or email to search all suitable people.");
        String searchQuery = scanner.nextLine().trim().toLowerCase();
        String[] searchWords = searchQuery.split("\\s+");

        Set<Integer> resultSet = new HashSet<>();

        switch (strategy) {
            case "ALL":
                resultSet = findAll(searchWords);
                break;
            case "ANY":
                resultSet = findAny(searchWords);
                break;
            case "NONE":
                resultSet = findNone(searchWords);
                break;
            default:
                System.out.println("Invalid strategy! Please choose ALL, ANY, or NONE.");
                return;
        }

        if (!resultSet.isEmpty()) {
            System.out.println(resultSet.size() + " persons found:");
            for (int index : resultSet) {
                System.out.println(people.get(index)); // Print matching people
            }
        } else {
            System.out.println("No matching people found.");
        }
    }

    /** Method to find persons that match all search words */
    private static Set<Integer> findAll(String[] searchWords) {
        Set<Integer> resultSet = new HashSet<>();
        boolean first = true;

        for (String word : searchWords) {
            Set<Integer> foundIndexes = invertedIndex.get(word);
            if (foundIndexes == null) {
                return Collections.emptySet(); // If any word is not found, return empty
            }
            if (first) {
                resultSet.addAll(foundIndexes);
                first = false;
            } else {
                resultSet.retainAll(foundIndexes); // Keep only common indexes
            }
        }
        return resultSet;
    }

    /** Method to find persons that match any of the search words */
    private static Set<Integer> findAny(String[] searchWords) {
        Set<Integer> resultSet = new HashSet<>();
        for (String word : searchWords) {
            Set<Integer> foundIndexes = invertedIndex.get(word);
            if (foundIndexes != null) {
                resultSet.addAll(foundIndexes); // Add all found indexes
            }
        }
        return resultSet;
    }

    /** Method to find persons that match none of the search words */
    private static Set<Integer> findNone(String[] searchWords) {
        Set<Integer> excludedIndexes = new HashSet<>();
        for (String word : searchWords) {
            Set<Integer> foundIndexes = invertedIndex.get(word);
            if (foundIndexes != null) {
                excludedIndexes.addAll(foundIndexes); // Add all found indexes to exclude
            }
        }
        Set<Integer> resultSet = new HashSet<>();
        for (int i = 0; i < people.size(); i++) {
            if (!excludedIndexes.contains(i)) {
                resultSet.add(i); // Add indexes that are not in excluded set
            }
        }
        return resultSet;
    }

    /** Method to print all people stored */
    private static void printAllPeople() {
        System.out.println("\n=== List of people ===");
        for (String person : people) {
            System.out.println(person);
        }
    }
}

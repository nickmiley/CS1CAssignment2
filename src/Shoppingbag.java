import java.io.*;
import java.util.*;

public class Shoppingbag {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter input filename (e.g., inputFile01.txt):");
        String filename = scanner.nextLine();

        List<Double> groceries = readPricesFromFile("resources/" + filename);
        System.out.println("The list of groceries has " + groceries.size() + " items.");
        System.out.println("Groceries wanted:\n" + groceries);

        System.out.println("\nEnter your budget:");
        double budget = scanner.nextDouble();

        long startTime = System.nanoTime();
        List<Double> result = findSubset(groceries, budget);
        long endTime = System.nanoTime();

        System.out.println("\nAlgorithm Elapsed Time: " + formatElapsedTime(startTime, endTime));
        System.out.println("Purchased grocery prices are:\n" + result);
        System.out.println("Done with ShoppingBag.");
    }

    //  GroceriesFileReader functionality
    public static List<Double> readPricesFromFile(String filePath) {
        List<Double> prices = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    try {
                        double price = Double.parseDouble(parts[1].trim());
                        prices.add(price);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid price in line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
        }

        return prices;
    }

    //  SubsetSum functionality
    public static List<Double> findSubset(List<Double> prices, double target) {
        List<Double> bestMatch = new ArrayList<>();
        double[] bestSum = {0.0};

        // Edge case: target is larger than total sum
        double totalSum = prices.stream().mapToDouble(Double::doubleValue).sum();
        if (target >= totalSum) {
            System.out.println("Target exceeds total sum. Returning all items.");
            return new ArrayList<>(prices);
        }

        // Call recursive function to build valid subset
        findSubsetHelper(prices, target, 0, new ArrayList<>(), 0.0, bestMatch, bestSum);
        return bestMatch;
    }

    /**
     * Recursive helper for subset sum problem.
     * @param prices The list of item prices
     * @param target The budget
     * @param index  Current index in prices
     * @param currentList Current working subset
     * @param currentSum Sum of currentList
     * @param bestMatch Stores the best matching subset found so far
     * @param bestSum Stores the sum of bestMatch
     * @return true if an exact match is found and recursion should stop
     */
    private static boolean findSubsetHelper(List<Double> prices, double target, int index,
                                            List<Double> currentList, double currentSum,
                                            List<Double> bestMatch, double[] bestSum) {
        //  Base case: exact match found
        if (currentSum == target) {
            bestMatch.clear();
            bestMatch.addAll(currentList);
            return true;
        }

        //  Overshoot or end of list
        if (currentSum > target || index >= prices.size()) {
            return false;
        }

        //  Try including prices[index]
        currentList.add(prices.get(index));
        if (findSubsetHelper(prices, target, index + 1, currentList,
                currentSum + prices.get(index), bestMatch, bestSum)) return true;
        currentList.remove(currentList.size() - 1);  // backtrack

        //  Try excluding prices[index]
        if (findSubsetHelper(prices, target, index + 1, currentList,
                currentSum, bestMatch, bestSum)) return true;

        //  Track best match (if under target and better than previous best)
        if (currentSum > bestSum[0] && currentSum < target) {
            bestSum[0] = currentSum;
            bestMatch.clear();
            bestMatch.addAll(currentList);
        }

        return false;
    }

    // Utility to format elapsed time
    private static String formatElapsedTime(long start, long end) {
        long elapsed = end - start;
        long ms = elapsed / 1_000_000;
        long ns = elapsed % 1_000_000;
        long sec = ms / 1000;
        long min = sec / 60;
        long hrs = min / 60;
        return String.format("%d hrs : %d min : %d sec : %d ms : %d ns", hrs, min % 60, sec % 60, ms % 1000, ns);
    }
}

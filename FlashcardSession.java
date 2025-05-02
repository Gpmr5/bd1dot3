import java.io.*;
import java.util.*;

public class FlashcardSession {
    private final Map<String, String> flashcards = new LinkedHashMap<>();
    private Map<String, Integer> mistakeCount = new HashMap<>();
    private List<String> recentMistakes = new ArrayList<>();
    private final int repetitions;
    private final boolean invert;
    private final String order;

    public FlashcardSession(String order, int repetitions, boolean invert) {
        this.order = order;
        this.repetitions = repetitions;
        this.invert = invert;
        initializeFlashcards();
        loadState();
    }

    private void initializeFlashcards() {
        flashcards.put("France", "Paris");
        flashcards.put("Germany", "Berlin");
        flashcards.put("Japan", "Tokyo");
        flashcards.put("Italy", "Rome");
        flashcards.put("Spain", "Madrid");
    }

    public void run() {
        List<String> questions = organizeCards();
        Map<String, Integer> correctCount = new HashMap<>();
        List<String> thisRoundMistakes = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        long startTime = System.currentTimeMillis();

        for (String question : questions) {
            String q = invert ? flashcards.get(question) : question;
            String a = invert ? question : flashcards.get(question);

            int correct = 0;
            int attempts = 0;

            while (correct < repetitions) {
                System.out.print("Q: " + q + " -> ");
                String response = scanner.nextLine().trim();
                attempts++;

                if (response.equalsIgnoreCase(a)) {
                    correct++;
                    System.out.println("Correct!");
                } else {
                    System.out.println("Incorrect! Answer is: " + a);
                    mistakeCount.put(question, mistakeCount.getOrDefault(question, 0) + 1);
                    thisRoundMistakes.add(question);
                    correct = 0;
                }
            }
            correctCount.put(question, attempts);
        }

        long endTime = System.currentTimeMillis();
        saveState(thisRoundMistakes);
        System.out.println("Session completed.");

        checkAchievements(correctCount);

        double avgTime = (endTime - startTime) / 1000.0 / questions.size();
        System.out.printf("Average time per card: %.2f seconds\n", avgTime);
        if (avgTime < 5.0) System.out.println("🏅 SPEEDSTER: Average time under 5 seconds!");
    }

    private List<String> organizeCards() {
        if (order.equals("recent-mistakes-first")) {
            return new RecentMistakesFirstSorter().organize(flashcards, mistakeCount, recentMistakes);
        } else if (order.equals("worst-first")) {
            List<String> sorted = new ArrayList<>(flashcards.keySet());
            sorted.sort((a, b) -> Integer.compare(mistakeCount.getOrDefault(b, 0), mistakeCount.getOrDefault(a, 0)));
            return sorted;
        } else {
            List<String> shuffled = new ArrayList<>(flashcards.keySet());
            Collections.shuffle(shuffled);
            return shuffled;
        }
    }

    private void checkAchievements(Map<String, Integer> correctCount) {
        boolean allCorrect = true, hasRepeat = false, allConfident = true;

        for (String q : correctCount.keySet()) {
            int attempts = correctCount.get(q);
            if (mistakeCount.getOrDefault(q, 0) > 0) allCorrect = false;
            if (attempts > 5) hasRepeat = true;
            if (attempts >= 3) allConfident = false;
        }

        System.out.println("\nAchievements:");
        if (allCorrect) System.out.println(" CORRECT: All cards answered correctly!");
        if (hasRepeat) System.out.println(" REPEAT: A card was answered more than 5 times.");
        if (allConfident) System.out.println(" CONFIDENT: All cards answered in fewer than 3 tries.");
    }

    private void saveState(List<String> thisRoundMistakes) {
        try (PrintWriter writer = new PrintWriter("mistakes.txt")) {
            for (Map.Entry<String, Integer> entry : mistakeCount.entrySet()) {
                writer.println(entry.getKey() + "|" + entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error saving mistakes.txt");
        }

        try (PrintWriter writer = new PrintWriter("recent.txt")) {
            for (String q : thisRoundMistakes) {
                writer.println(q);
            }
        } catch (IOException e) {
            System.err.println("Error saving recent.txt");
        }
    }

    private void loadState() {
        mistakeCount = new HashMap<>();
        recentMistakes = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File("mistakes.txt"))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split("\\|", 2);
                if (parts.length == 2 && flashcards.containsKey(parts[0])) {
                    mistakeCount.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        } catch (IOException e) { }

        try (Scanner scanner = new Scanner(new File("recent.txt"))) {
            while (scanner.hasNextLine()) {
                String q = scanner.nextLine();
                if (flashcards.containsKey(q)) {
                    recentMistakes.add(q);
                }
            }
        } catch (IOException e) { }

        for (String q : flashcards.keySet()) {
            mistakeCount.putIfAbsent(q, 0);
        }
    }
}
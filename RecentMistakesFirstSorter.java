import java.util.*;

public class RecentMistakesFirstSorter implements CardOrganizer {
    @Override
    public List<String> organize(Map<String, String> flashcards, Map<String, Integer> mistakeCount, List<String> recentMistakes) {
        LinkedHashSet<String> ordered = new LinkedHashSet<>();

        List<String> reversed = new ArrayList<>(recentMistakes);
        Collections.reverse(reversed);
        for (String q : reversed) {
            if (flashcards.containsKey(q)) {
                ordered.add(q);
            }
        }

        // Add the rest of the cards
        for (String q : flashcards.keySet()) {
            ordered.add(q);
        }

        return new ArrayList<>(ordered);
    }
}

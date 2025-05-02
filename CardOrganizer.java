import java.util.List;
import java.util.Map;

public interface CardOrganizer {
    List<String> organize(Map<String, String> flashcards, Map<String, Integer> mistakeCount, List<String> recentMistakes);
}

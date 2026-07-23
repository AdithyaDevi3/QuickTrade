package quicktrade.com.entity;

import java.util.List;

/**
 * QuizQuestionDTO — a single multiple-choice quiz question returned by the
 * {@code GET /api/learn/quiz} endpoint.
 *
 * <p>Contains the question text, four answer options, the zero-based index of
 * the correct answer, an explanation shown after answering, and the glossary
 * term it is derived from.</p>
 */
public class QuizQuestionDTO {

    private String question;
    /** Exactly four answer choices. */
    private List<String> options;
    /** Zero-based index into {@code options} for the correct answer. */
    private int correctIndex;
    private String explanation;
    /** The glossary term this question is testing. */
    private String relatedTerm;
    private String difficulty;

    public QuizQuestionDTO() {}

    public QuizQuestionDTO(String question, List<String> options, int correctIndex,
                           String explanation, String relatedTerm, String difficulty) {
        this.question = question;
        this.options = options;
        this.correctIndex = correctIndex;
        this.explanation = explanation;
        this.relatedTerm = relatedTerm;
        this.difficulty = difficulty;
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public int getCorrectIndex() { return correctIndex; }
    public void setCorrectIndex(int correctIndex) { this.correctIndex = correctIndex; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getRelatedTerm() { return relatedTerm; }
    public void setRelatedTerm(String relatedTerm) { this.relatedTerm = relatedTerm; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}

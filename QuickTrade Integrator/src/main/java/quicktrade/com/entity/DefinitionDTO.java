package quicktrade.com.entity;

/**
 * DefinitionDTO — a single financial term definition returned by the
 * {@code GET /api/learn/definitions} endpoint.
 *
 * <p>Matches the {@link quicktrade.com.service.DefinitionsSeeder} data model
 * and maps directly to the iOS {@code Definition} Codable struct.</p>
 */
public class DefinitionDTO {

    private String term;
    private String definition;
    private String example;
    private String application;
    /** "beginner", "intermediate", or "advanced". */
    private String difficulty;

    public DefinitionDTO() {}

    public DefinitionDTO(String term, String definition, String example,
                         String application, String difficulty) {
        this.term = term;
        this.definition = definition;
        this.example = example;
        this.application = application;
        this.difficulty = difficulty;
    }

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }

    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }

    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }

    public String getApplication() { return application; }
    public void setApplication(String application) { this.application = application; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}

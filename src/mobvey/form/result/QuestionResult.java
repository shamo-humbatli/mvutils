package mobvey.form.result;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Shamo Humbatli
 */
public class QuestionResult {
    private String id;
    private List<InputResult> inputResults = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<InputResult> getInputResults() {
        return inputResults;
    }

    public void setInputResults(List<InputResult> inputResults) {
        this.inputResults = inputResults;
    }
    
    public void AddInputResult(InputResult inputResult)
    {
        this.inputResults.add(inputResult);
    }
}

package mobvey.form.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Shamo Humbatli
 */
public class QuestionResult implements Serializable {
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
    
    public QuestionResult CloneExact()
    {
        QuestionResult qr = new QuestionResult();
        qr.setId(id);
        
        if(inputResults != null)
        {
            for(InputResult ir : inputResults)
            {
                if(ir == null)
                    continue;
                
                InputResult irc = ir.CloneExact();
                qr.AddInputResult(irc);
            }
        }
        
        return qr;
    }
}

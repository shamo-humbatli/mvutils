package mobvey.form.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Shamo Humbatli
 */
public class FormResult implements Serializable {

    private String id;
    private String version;
    private String lang;
    private List<QuestionResult> questionResults = new ArrayList<>();

    public FormResult() {
    }

    public FormResult(String id) {
        this.id = id;
    }

    public FormResult(String id, String version) {
        this.id = id;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public List<QuestionResult> getQuestionResults() {
        return questionResults;
    }

    public void setQuestionResults(List<QuestionResult> questionResults) {
        this.questionResults = questionResults;
    }

    public void AddQuestionResults(QuestionResult questionResult) {
        this.questionResults.add(questionResult);
    }

    public FormResult CloneExact()
    {
        FormResult fr = new FormResult();
        fr.setId(id);
        fr.setLang(lang);
        fr.setVersion(version);
        
        if(questionResults != null)
        {
            for(QuestionResult qr : questionResults)
            {
                if(qr == null)
                    continue;
                
                QuestionResult qrc = qr.CloneExact();
                fr.AddQuestionResults(qrc);
            }
        }
        
        return fr;
    }
    
    @Override
    public String toString() {
        return "FormResult{" + "id=" + id + ", version=" + version + ", lang=" + lang + '}';
    }
}

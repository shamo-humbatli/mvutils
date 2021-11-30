package mobvey.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mobvey.form.question.Question;

/**
 *
 * @author Shamo Humbatli
 */
public class QuestionFormData implements Serializable {

    protected List<Question> questions;
    protected String formId;
    protected String formLanguage;
    protected String formVersion;
    protected String description;    
    protected String title;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormLanguage() {
        return formLanguage;
    }

    public void setFormLanguage(String formLanguage) {
        this.formLanguage = formLanguage;
    }

    public String getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(String formVersion) {
        this.formVersion = formVersion;
    }

    public void AddQuestion(Question question) {
        if (questions == null) {
            questions = new ArrayList<>();
        }

        questions.add(question);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public boolean hasQuestions() {
        if (questions == null || questions.isEmpty()) {
            return false;
        }

        return true;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "QuestionFormData{" + "formId=" + formId + ", formLanguage=" + formLanguage + ", formVersion=" + formVersion + ", description=" + description + ", title=" + title + '}';
    }
    
    
}

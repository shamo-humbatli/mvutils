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

    private List<Question> questions;
    private String formId;
    private String formLanguage;
    private String formVersion;
    private String description;

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

    @Override
    public String toString() {
        return "QuestionForm{" + "formId=" + formId + ", formLanguage=" + formLanguage + ", formVersion=" + formVersion + ", description=" + description + '}';
    }
}

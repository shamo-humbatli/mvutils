package mobvey.form.question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mobvey.form.base.AbstractAnswer;

/**
 *
 * @author Shamo Humbatli
 */
public class Question implements Serializable {

    private String questionId;
    private String questionText;
    private boolean enabled = true;

    private List<AbstractAnswer> answers;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<AbstractAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AbstractAnswer> answers) {
        this.answers = answers;
    }

    public void AddAnswer(AbstractAnswer answer) {
        if (answers == null) {
            answers = new ArrayList<>();
        }

        answers.add(answer);
    }

    @Override
    public String toString() {
        return "Question{" + "questionId=" + questionId + ", questionText=" + questionText + ", enabled=" + enabled + '}';
    }
}

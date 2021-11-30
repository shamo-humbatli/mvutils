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
    private String itemIndex;
    private boolean itemIndexing = true;

    public boolean isItemIndexing() {
        return itemIndexing;
    }

    public void setItemIndexing(boolean itemIndexing) {
        this.itemIndexing = itemIndexing;
    }
    private boolean forOperator = false;

    public boolean isForOperator() {
        return forOperator;
    }

    public void setIsForOperator(boolean isForOperator) {
        this.forOperator = isForOperator;
    }
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

    public String getItemIndex() {
        return itemIndex;
    }

    public void setItemIndex(String itemIndex) {
        this.itemIndex = itemIndex;
    }

    public Question CloneExact() {
        Question q = new Question();

        q.setEnabled(enabled);
        q.setIsForOperator(forOperator);
        q.setItemIndex(itemIndex);
        q.setQuestionId(questionId);
        q.setQuestionText(questionText);
        q.setItemIndexing(itemIndexing);

        if (answers != null) {
            for (AbstractAnswer answr : answers) {

                if (answr == null) {
                    continue;
                }

                AbstractAnswer answrCloned = answr.CloneExact();
                q.AddAnswer(answrCloned);
            }
        }

        return q;
    }

    @Override
    public String toString() {
        return "Question{" + "questionId=" + questionId + ", questionText=" + questionText + ", itemIndex=" + itemIndex + ", forOperator=" + forOperator + ", enabled=" + enabled + '}';
    }
}

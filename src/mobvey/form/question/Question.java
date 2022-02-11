package mobvey.form.question;

import java.util.ArrayList;
import java.util.List;
import mobvey.common.Strings;
import mobvey.form.base.AbstractAnswer;
import mobvey.form.base.AbstractFormElement;
import mobvey.form.enums.FormElementType;

/**
 *
 * @author Shamo Humbatli
 */
public class Question extends AbstractFormElement {
    
    public Question() {
        super(FormElementType.QUESTION);
    }
    
    private String questionText;
    private String itemIndex;
    private boolean itemIndexing = true;
    protected String explanation;
    
    public boolean isItemIndexing() {
        return itemIndexing;
    }
    
    public boolean hasExplanation() {
        return Strings.hasContent(explanation);
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
    
    private List<AbstractAnswer> answers;
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public List<AbstractAnswer> getAnswers() {
        if (answers == null) {
            answers = new ArrayList<AbstractAnswer>();
        }
        return answers;
    }
    
    public void setAnswers(List<AbstractAnswer> answers) {
        this.answers = answers;
        setParent(answers, this);
    }
    
    public void AddAnswer(AbstractAnswer answer) {
        
        if(answer == null)
            return;
        
        if (answers == null) {
            answers = new ArrayList<>();
        }
        
        answers.add(answer);
        answer.setParent(this);
    }
    
    public String getItemIndex() {
        return itemIndex;
    }
    
    public void setItemIndex(String itemIndex) {
        this.itemIndex = itemIndex;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    
    public Question CloneExact() {
        Question q = new Question();
        
        q.setEnabled(_enabled);
        q.setIsForOperator(forOperator);
        q.setItemIndex(itemIndex);
        q.setId(_id);
        q.setParentId(_parentId);
        q.setQuestionText(questionText);
        q.setItemIndexing(itemIndexing);
        q.setExplanation(explanation);
        
        if (_events != null) {
            q.setEvents(new ArrayList<>(_events));
        }
        
        if (answers != null) {
            for (AbstractAnswer answr : answers) {
                
                if (answr == null) {
                    continue;
                }
                
                AbstractAnswer answrCloned = (AbstractAnswer)answr.CloneExact();
                q.AddAnswer(answrCloned);
            }
        }
        
        return q;
    }
    
    @Override
    public String toString() {
        return "Question{"
                + "questionId=" + _id
                + ", questionText=" + questionText
                + ", itemIndex=" + itemIndex
                + ", forOperator=" + forOperator
                + ", enabled=" + _enabled
                + ", explanation=" + explanation
                + '}';
    }
}

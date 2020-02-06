package mobvey.form.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.enums.InputTypes;
import mobvey.form.enums.InputValueTypes;

/**
 *
 * @author Shamo Humbatli
 */
public class AbstractInput implements Serializable {

    private String id = null;
    private int contentColumnIndex;
    private String contentItemIndex;
    private InputValueTypes inputValueType;
    private String operation;
    private final InputTypes inputType;
    private Object displayContent;
    private String parentId = null;
    private boolean complex = false;

    private List<String> questionsToEnable;
    private List<String> questionsToDisable;
    
    private List<ContentContainer> contentContainers;

    private String returnContent = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public boolean isComplex() {
        return complex;
    }

    public void setComplex(boolean complex) {
        this.complex = complex;
    }
    
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Object getDisplayContent() {
        return displayContent;
    }

    public void setDisplayContent(Object displayContent) {
        this.displayContent = displayContent;
    }

    public AbstractInput(InputTypes inputType) {
        this.inputType = inputType;
    }

    public InputTypes getInputType() {
        return inputType;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public InputValueTypes getInputValueType() {
        return inputValueType;
    }

    public void setInputValueType(InputValueTypes inputValueType) {
        this.inputValueType = inputValueType;
    }

    public int getContentColumnIndex() {
        return contentColumnIndex;
    }

    public void setContentColumnIndex(int contentColumnIndex) {
        this.contentColumnIndex = contentColumnIndex;
    }

    public String getContentItemIndex() {
        return contentItemIndex;
    }

    public void setContentItemIndex(String contentItemIndex) {
        this.contentItemIndex = contentItemIndex;
    }

    public String getReturnContent() {
        return returnContent;
    }

    public void setReturnContent(String returnContent) {
        this.returnContent = returnContent;
    }

    public void AddQuestionToEnable(String questionId) {
        if (questionsToEnable == null) {
            questionsToEnable = new ArrayList<>();
        }

        questionsToEnable.add(questionId);
    }

    public List<String> getQuestionsToEnable() {
        return questionsToEnable;
    }

    public void setQuestionsToEnable(List<String> questionsToEnable) {
        this.questionsToEnable = questionsToEnable;
    }

    public List<String> getQuestionsToDisable() {
        return questionsToDisable;
    }

    public void setQuestionsToDisable(List<String> questionsToDisable) {
        this.questionsToDisable = questionsToDisable;
    }

    public void AddQuestionToDisable(String questionId) {
        if (questionsToDisable == null) {
            questionsToDisable = new ArrayList<>();
        }

        questionsToDisable.add(questionId);
    }

    public boolean HasOperation() {
        return operation != null;
    }
    
     public List<ContentContainer> getContentContainers() {
        return contentContainers;
    }

    public void setContentContainers(List<ContentContainer> contentContainers) {
        this.contentContainers = contentContainers;
    }
    
    public void AddContentContainer(ContentContainer contentContainer)
    {
        if(contentContainers == null)
            contentContainers = new ArrayList<>();
        
        contentContainers.add(contentContainer);
    }

    @Override
    public String toString() {
        return "AbstractInput{" + "id=" + id + ", contentColumnIndex=" + contentColumnIndex + ", contentItemIndex=" + contentItemIndex + ", inputValueType=" + inputValueType + ", operation=" + operation + ", inputType=" + inputType + ", displayContent=" + displayContent + ", parentId=" + parentId + ", returnContent=" + returnContent + '}';
    }
}

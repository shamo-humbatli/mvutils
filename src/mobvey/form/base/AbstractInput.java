package mobvey.form.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mobvey.common.KeyValuePair;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.answer.content.operation.AbstractOperation;
import mobvey.form.enums.ColumnDefinitionType;
import mobvey.form.enums.InputTypes;
import mobvey.form.enums.InputValueTypes;

/**
 *
 * @author Shamo Humbatli
 */
public abstract class AbstractInput implements Serializable {

    protected String id = null;
    protected String columnDefinition = "0";
    protected ColumnDefinitionType columnDefinitionType = ColumnDefinitionType.CI;
    protected boolean columnDefinitionDeclaredByDefault = true;
    protected String contentItemIndex;
    protected InputValueTypes inputValueType;
    protected AbstractOperation operation;
    protected final InputTypes inputType;
    protected Object displayContent;
    protected String parentId = null;
    protected boolean complex = false;

    protected List<String> questionsToEnable;
    protected List<String> questionsToDisable;

    protected List<KeyValuePair<String, Boolean>> containersRequired;

    protected List<ContentContainer> contentContainers;

    protected String returnContent = null;

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

    public AbstractOperation getOperation() {
        return operation;
    }

    public void setOperation(AbstractOperation operation) {
        this.operation = operation;
    }

    public InputValueTypes getInputValueType() {
        return inputValueType;
    }

    public void setInputValueType(InputValueTypes inputValueType) {
        this.inputValueType = inputValueType;
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

        if ("".equals(questionId)) {
            return;
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

        if ("".equals(questionId)) {
            return;
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

    public void AddContentContainer(ContentContainer contentContainer) {
        if (contentContainers == null) {
            contentContainers = new ArrayList<>();
        }

        contentContainers.add(contentContainer);
    }

    public List<KeyValuePair<String, Boolean>> getContainersRequired() {
        return containersRequired;
    }

    public void setContainersRequired(List<KeyValuePair<String, Boolean>> containersRequired) {
        this.containersRequired = containersRequired;
    }

    public void AddContainerIdRequired(String id, boolean required) {
        if (containersRequired == null) {
            containersRequired = new ArrayList<>();
        }

        containersRequired.add(new KeyValuePair<>(id, required));
    }
    
    public boolean HasContainersToReviewIfRequired()
    {
        return containersRequired != null && containersRequired.size() > 0;
    }

    public String getColumnDefinition() {
        return columnDefinition;
    }

    public void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    public ColumnDefinitionType getColumnDefinitionType() {
        return columnDefinitionType;
    }

    public void setColumnDefinitionType(ColumnDefinitionType columnDefinitionType) {
        this.columnDefinitionType = columnDefinitionType;
    }

    public boolean isColumnDefinitionDeclaredByDefault() {
        return columnDefinitionDeclaredByDefault;
    }

    public void setColumnDefinitionDeclaredByDefault(boolean columnDefinitionDeclaredByDefault) {
        this.columnDefinitionDeclaredByDefault = columnDefinitionDeclaredByDefault;
    }

    public abstract AbstractInput CloneExact();
    
    @Override
    public String toString() {
        return "AbstractInput{" + "id=" + id + ", columnDefinition=" + columnDefinition + ", columnDefinitionType=" + columnDefinitionType + ", columnDefinitionDeclaredByDefault=" + columnDefinitionDeclaredByDefault + ", contentItemIndex=" + contentItemIndex + ", inputValueType=" + inputValueType + ", operation=" + operation + ", inputType=" + inputType + ", displayContent=" + displayContent + ", parentId=" + parentId + ", complex=" + complex + ", questionsToEnable=" + questionsToEnable + ", questionsToDisable=" + questionsToDisable + ", containersRequired=" + containersRequired + ", returnContent=" + returnContent + '}';
    }
}

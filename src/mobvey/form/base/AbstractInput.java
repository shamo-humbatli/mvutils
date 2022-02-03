package mobvey.form.base;

import java.util.ArrayList;
import java.util.List;
import mobvey.common.KeyValuePair;
import mobvey.condition.AbstractCondition;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.enums.ColumnDefinitionType;
import mobvey.form.enums.FormElementType;
import mobvey.form.enums.InputType;
import mobvey.form.enums.InputValueType;
import mobvey.operation.AbstractOperation;

/**
 *
 * @author Shamo Humbatli
 */
public abstract class AbstractInput extends AbstractFormElement {

    protected String columnDefinition = "0";
    protected ColumnDefinitionType columnDefinitionType = ColumnDefinitionType.CI;
    protected boolean columnDefinitionDeclaredByDefault = true;
    protected String contentItemIndex;
    protected InputValueType inputValueType;
    protected final InputType inputType;
    protected Object displayContent;
    protected boolean complex = false;

    protected List<KeyValuePair<String, Boolean>> containersRequired;

    protected List<ContentContainer> contentContainers;

    protected Object returnContent = null;
    
    protected List<AbstractCondition> _validations;
    
    protected AbstractOperation _valueOperation;

    public AbstractInput(InputType inputType, FormElementType elementType) {
        super(elementType);
        this.inputType = inputType;
    }

    public boolean isComplex() {
        return complex;
    }

    public void setComplex(boolean complex) {
        this.complex = complex;
    }
    
    public Object getDisplayContent() {
        return displayContent;
    }

    public void setDisplayContent(Object displayContent) {
        this.displayContent = displayContent;
    }

    public InputType getInputType() {
        return inputType;
    }

    public InputValueType getInputValueType() {
        return inputValueType;
    }

    public void setInputValueType(InputValueType inputValueType) {
        this.inputValueType = inputValueType;
    }

    public String getContentItemIndex() {
        return contentItemIndex;
    }

    public void setContentItemIndex(String contentItemIndex) {
        this.contentItemIndex = contentItemIndex;
    }

    public Object getReturnContent() {
        return returnContent;
    }
    
     public String getStringReturnContent() {
        return returnContent == null ? null : String.valueOf(returnContent);
    }

    public void setReturnContent(Object returnContent) {
        this.returnContent = returnContent;
    }

    public List<ContentContainer> getContentContainers() {

        if (contentContainers == null) {
            contentContainers = new ArrayList<ContentContainer>();
        }

        return contentContainers;
    }

    public void setContentContainers(List<ContentContainer> contentContainers) {
        
        if(contentContainers == null)
            return;
        
        setParent(contentContainers, this);
        this.contentContainers = contentContainers;
    }

    public void AddContentContainer(ContentContainer contentContainer) {
        if (contentContainers == null) {
            contentContainers = new ArrayList<>();
        }
        
        if(contentContainer == null)
            return;
        
        contentContainer.setParent(this);

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

    public boolean HasContainersToReviewIfRequired() {
        return containersRequired != null && containersRequired.size() > 0;
    }
    
    public boolean hasValueOperation()
    {
        return _valueOperation != null;
    }
    
    public boolean hasValidations()
    {
        return _validations != null && !_validations.isEmpty();
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

    public List<AbstractCondition> getValidations() {
        return _validations;
    }

    public void setValidations(List<AbstractCondition> _validations) {
        this._validations = _validations;
    }

     public void AddValidation(AbstractCondition abstractCondition) {
        if (_validations == null) {
            _validations = new ArrayList<>();
        }

        _validations.add(abstractCondition);
    }

    public AbstractOperation getValueOperation() {
        return _valueOperation;
    }

    public void setValueOperation(AbstractOperation valueOperation) {
        this._valueOperation = valueOperation;
    }
    
    public abstract boolean isIndividuallyReturnable();
     
    @Override
    public String toString() {
        return "AbstractInput{" + "id=" + _id + ", columnDefinition=" + columnDefinition + ", columnDefinitionType=" + columnDefinitionType + ", columnDefinitionDeclaredByDefault=" + columnDefinitionDeclaredByDefault + ", contentItemIndex=" + contentItemIndex + ", inputValueType=" + inputValueType + ", inputType=" + inputType + ", displayContent=" + displayContent + ", parentId=" + getParentId() + ", complex=" + complex  + ", containersRequired=" + containersRequired + ", returnContent=" + returnContent + '}';
    }
}

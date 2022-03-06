package mobvey.form.operation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import mobvey.form.elements.QuestionForm;
import mobvey.form.elements.SimpleAnswer;
import mobvey.form.elements.InputOptionContent;
import mobvey.form.elements.InputTextContent;
import mobvey.form.elements.ContentContainer;
import mobvey.form.elements.AbstractAnswer;
import mobvey.form.elements.AbstractFormElement;
import mobvey.form.elements.AbstractInput;
import mobvey.form.elements.Question;
import mobvey.form.result.FormResult;
import mobvey.models.InputValidationResult;

/**
 *
 * @author Shamo Humbatli
 */
public interface IQuestionFormOperation {

    //set
    Collection<String> setReturnRequired(String contentContainerId, boolean required);
    Collection<String> setReturnValue(String inputId, Object value);
    Collection<String> setChecked(String inputOptionId, boolean checked);
    Collection<String> setEnabled(String elementId, boolean enabled);
    
    //get abstract level
    public AbstractFormElement getElementById(String id);
    public Collection<AbstractFormElement> getElementsByClass(String className);
    public <TElement extends AbstractFormElement> TElement getElement(String id, Class<TElement> elementClass);
    public <TElement extends AbstractFormElement> Collection<TElement> getElements(Class<TElement> elementClass);
    public <TElement extends AbstractFormElement> TElement getParentElementOfType(String id, Class<TElement> elementClass);
    

     //get specified level
    public FormResult getFormResult();
    public QuestionForm getQuestionForm();
    public SimpleAnswer getAnswerById(String id);
    public Question getQuestionById(String id);
    public ContentContainer getContentContainerById(String id);
    public InputTextContent getInputTextById(String id);
    public InputOptionContent getInputOptionById(String id);
    
    public Collection<AbstractInput> getInputContents();
    public Collection<InputTextContent> getInputTextContents();
    public Collection<InputOptionContent> getInputOptionContents();    
    public Collection<ContentContainer> getContentContainers();
    public Collection<Question> getQuestions();
    public Collection<AbstractAnswer> getAnswers();

    
    //other getters
    public Collection<AbstractInput> getInputsHavingValueOperation();   
    public Collection<AbstractInput> getInputsHavingValidations();
    public Collection<AbstractInput> getAvailableInputsHavingValidations();
    public String getActualShortDescription();
    
    public List<AbstractInput> getReturnableInputs(String elementId);
    public int getFormElementsCount();
    public Map<String, AbstractFormElement> getFormElements();
    public Collection<ContentContainer> getRequiredContainers();
    public Collection<ContentContainer> getUnsatisfiedContainers();
    public boolean getEnabledStateInTree(String elementId);
    
    public boolean isFormElementsLoaded();
    public boolean isAnyParentDisabled(String elementId);
    public boolean isEnabledInTree(String elementId);
    public boolean isDisabledInTree(String elementId);
    public boolean hasAnyReturnableInputs(String elementId);
  

    // validation
    public Collection<InputValidationResult> validateInputs();
    public boolean isValid(String inputTextId);
    public InputValidationResult validateInput(String inputTextId);
    
    //cloning
    public AbstractFormElement cloneExactById(String id);
}

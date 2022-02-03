package mobvey.form.operation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import mobvey.form.QuestionForm;
import mobvey.form.answer.SimpleAnswer;
import mobvey.form.answer.content.InputOptionContent;
import mobvey.form.answer.content.InputTextContent;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.base.AbstractAnswer;
import mobvey.form.base.AbstractFormElement;
import mobvey.form.base.AbstractInput;
import mobvey.form.question.Question;
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
    public <TElement extends AbstractFormElement> TElement getFormElement(String id, Class<TElement> elementClass);
    public <TElement extends AbstractFormElement> Collection<TElement> getFormElements(Class<TElement> elementClass);
    

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
    
    public List<AbstractInput> getReturnableInputs(String elementId);
    public int getFormElementsCount();
    public Map<String, AbstractFormElement> getFormElements();
    public Collection<ContentContainer> getRequiredContainers();
    public Collection<ContentContainer> getUnsatisfiedContainers();
    
    public boolean isFormElementsLoaded();
    public boolean isAnyParentDisabled(String elementId);
   
  

    // validation
    public Collection<InputValidationResult> validateInputs();
    public boolean isValid(String inputTextId);
    public InputValidationResult validateInput(String inputTextId);
    
    //cloning
    public AbstractFormElement cloneExactById(String id);
}

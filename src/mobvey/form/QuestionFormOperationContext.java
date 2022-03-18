package mobvey.form;

import mobvey.form.elements.QuestionForm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mobvey.common.NumberUtil;
import mobvey.common.Strings;
import mobvey.condition.AbstractCondition;
import mobvey.condition.CheckCheckedCondition;
import mobvey.condition.CheckElementsAreCheckedCondition;
import mobvey.condition.CheckElementsAreEnabledCondition;
import mobvey.condition.CheckEnabledCondition;
import mobvey.condition.CompareInputValueWithDirectValueCondition;
import mobvey.condition.CompareInputValueWithInputValueCondition;
import mobvey.condition.CompareWithDirectValueCondition;
import mobvey.condition.CompareWithInputValueCondition;
import mobvey.condition.ConditionCombination;
import mobvey.condition.ConditionGroup;
import mobvey.condition.HasContentOfInputsCondition;
import mobvey.condition.HasNoContentOfInputsCondition;
import mobvey.condition.IsInRangeOfDirectValuesCondition;
import mobvey.condition.IsInRangeOfInputValuesCondition;
import mobvey.form.elements.SimpleAnswer;
import mobvey.form.elements.InputOptionContent;
import mobvey.form.elements.InputTextContent;
import mobvey.form.elements.ContentContainer;
import mobvey.form.elements.AbstractAnswer;
import mobvey.form.elements.AbstractFormElement;
import mobvey.form.elements.AbstractInput;
import mobvey.form.elements.FormBody;
import mobvey.form.elements.Section;
import mobvey.form.enums.ComparisonType;
import mobvey.form.enums.FormElementType;
import mobvey.form.events.AbstractFormEvent;
import mobvey.form.elements.Question;
import mobvey.form.enums.FormEventType;
import mobvey.form.result.FormResult;
import mobvey.form.result.InputResult;
import mobvey.form.result.QuestionResult;
import mobvey.models.InputValidationResult;
import mobvey.operation.AbstractOperation;
import mobvey.operation.CheckConditionOperation;
import mobvey.operation.SumInputValuesByClassesOperation;
import mobvey.operation.SumInputValuesOperation;
import mobvey.procedure.AbstractProcedure;
import mobvey.procedure.DisableElementsProcedure;
import mobvey.procedure.EnableElementsByClassProcedure;
import mobvey.procedure.EnableElementsProcedure;
import mobvey.procedure.SetReturnRequiredProcedure;

/**
 *
 * @author Shamo Humbatli
 */
public class QuestionFormOperationContext implements IQuestionFormOperation {

    private final QuestionForm _questionForm;
    private Map<String, AbstractFormElement> _elements;
    private Collection<AbstractFormElement> _elementsHavingClasses;
    private int _elementCount = 0;
    private boolean _elementsLoaded = false;

    public QuestionFormOperationContext(QuestionForm questionForm) {
        this._questionForm = questionForm;
        this.loadElements();
    }

    // loading part
    private void loadElements() {
        if (_elementsLoaded) {
            return;
        }

        _elements = new HashMap<String, AbstractFormElement>();
        _elementsHavingClasses = new ArrayList<>();

        loadForm();

        for (AbstractFormElement afe : _elements.values()) {
            if (!afe.hasAnyClass()) {
                continue;
            }

            _elementsHavingClasses.add(afe);
        }

        _elementCount = _elements.size();
        _elementsLoaded = true;
    }

    private void putElement(String elementId, AbstractFormElement abstractFormElement) {
//        if (_elements.containsKey(elementId)) {
//
//        } else {
//            _elements.put(elementId, abstractFormElement);
//        }

        _elements.put(elementId, abstractFormElement);
    }

    private void loadForm() {
        putElement(_questionForm.getId(), _questionForm);

        FormBody fb = _questionForm.getBody();

        if (fb == null) {
            return;
        }

        putElement(fb.getId(), fb);

        for (AbstractFormElement afe : fb.getChildren()) {
            loadElement(afe);
        }
    }

    private void loadElement(AbstractFormElement abstractFormElement) {

        if (abstractFormElement == null) {
            return;
        }

        switch (abstractFormElement.getElementType()) {
            case FORM:
                break;
            case FORM_BODY:
                break;
            case SECTION:
                loadSection((Section) abstractFormElement);
                break;
            case QUESTION:
                loadQuestion((Question) abstractFormElement);
                break;
            case ANSWER:
                loadAnswer((AbstractAnswer) abstractFormElement);
                break;
            case CONTENT_CONTAINER:
                loadContentContainer((ContentContainer) abstractFormElement);
                break;
            case INPUT_TEXT:
            case INPUT_OPTION:
                loadInput((AbstractInput) abstractFormElement);
                break;
            default:
                break;
        }
    }

    private void loadSection(Section section) {

        if (section == null) {
            return;
        }

        putElement(section.getId(), section);

        for (AbstractFormElement afe : section.getChildren()) {
            loadElement(afe);
        }
    }

    private void loadQuestion(Question question) {
        if (question == null) {
            return;
        }

        putElement(question.getId(), question);

        for (AbstractAnswer ai : question.getAnswers()) {
            loadAnswer(ai);
        }
    }

    private void loadAnswer(AbstractAnswer abstractAnswer) {
        if (abstractAnswer == null) {
            return;
        }
        putElement(abstractAnswer.getId(), abstractAnswer);

        for (ContentContainer cc : abstractAnswer.getAnswerContentContainers()) {
            loadContentContainer(cc);
        }
    }

    private void loadContentContainer(ContentContainer contentContainer) {
        if (contentContainer == null) {
            return;
        }

        putElement(contentContainer.getId(), contentContainer);

        for (AbstractInput ai : contentContainer.getContentInputs()) {
            loadInput(ai);
        }
    }

    private void loadInput(AbstractInput abstractInput) {
        if (abstractInput == null) {
            return;
        }

        putElement(abstractInput.getId(), abstractInput);

        if (abstractInput.isComplex()) {
            List<ContentContainer> subCcs = abstractInput.getContentContainers();
            for (ContentContainer cc : subCcs) {
                loadContentContainer(cc);
            }
        }
    }

    // operations
    @Override
    public QuestionForm getQuestionForm() {
        return _questionForm;
    }

    @Override
    public boolean isEnabledInTree(String elementId) {
        return getEnabledStateInTree(elementId);
    }

    @Override
    public boolean isEnabled(String elementId) {
        AbstractFormElement afe = getElementById(elementId);

        if (afe == null) {
            return false;
        }

        return afe.isEnabled();
    }

    @Override
    public boolean isDisabledInTree(String elementId) {
        return !getEnabledStateInTree(elementId);
    }

    @Override
    public Map<String, Collection<AbstractFormEvent>> getEvents() {
        Map<String, Collection<AbstractFormEvent>> afeWithEvents = new HashMap<String, Collection<AbstractFormEvent>>();

        for (AbstractFormElement afe : _elements.values()) {
            if (afe.getEvents().isEmpty()) {
                continue;
            }

            afeWithEvents.put(afe.getId(), afe.getEvents());
        }

        return afeWithEvents;
    }

    @Override
    public boolean getEnabledStateInTree(String elementId) {
        AbstractFormElement afe = getElementById(elementId);

        if (afe == null) {
            return false;
        }

        if (!afe.isEnabled()) {
            return false;
        }

        if (Strings.isNullOrEmpty(afe.getParentId())) {
            return afe.isEnabled();
        }

        return getEnabledStateInTree(afe.getParentId());
    }

    @Override
    public boolean isAnyParentDisabled(String elementId) {

        if (Strings.isNothing(elementId)) {
            return false;
        }

        AbstractFormElement element = getElementById(elementId);

        if (element == null) {
            return false;
        }

        if (Strings.isNullOrEmpty(element.getParentId())) {
            return false;
        }

        AbstractFormElement parentElement = getElementById(element.getParentId());

        if (parentElement == null) {
            return false;
        }

        if (!parentElement.isEnabled()) {
            return true;
        }

        return isAnyParentDisabled(parentElement.getParentId());
    }

    @Override
    public Map<String, AbstractFormElement> getFormElements() {
        return _elements;
    }

    @Override
    public int getFormElementsCount() {
        return _elementCount;
    }

    @Override
    public boolean isFormElementsLoaded() {
        return _elementsLoaded;
    }

    @Override
    public String getActualShortDescription() {
        String asdStr = _questionForm.getActualShortDescription();

        if (Strings.isNullOrEmpty(asdStr)) {
            return null;
        }

        for (String inputId : Strings.getParams(asdStr, '{', '}')) {
            Collection<AbstractInput> ainputs = getReturnableInputs(inputId);

            Collection<String> valuesMapped = new ArrayList<>();

            for (AbstractInput ai : ainputs) {
                String hrText = ai.getHrDisplayText();

                if (Strings.isNothing(hrText)) {
                    continue;
                }

                valuesMapped.add(hrText);
            }

            if (valuesMapped.isEmpty()) {
                asdStr = asdStr.replace("{" + inputId + "}", "");
            } else {
                asdStr = asdStr.replace("{" + inputId + "}", Strings.join(", ", valuesMapped));
            }
        }

        return asdStr;
    }

    @Override
    public Collection<String> setReturnRequired(String contentContainerId, boolean required) {
        AbstractFormElement element = getElementById(contentContainerId);

        List<String> changes = new ArrayList<>();
        if (element == null) {
            return changes;
        }

        if (element instanceof ContentContainer) {
            ContentContainer cc = (ContentContainer) element;

            if (cc.isReturnRequeired() == required) {
                return changes;
            }

            cc.setReturnRequeired(required);

            changes.add(cc.getId());
        }

        return changes;
    }

    @Override
    public Collection<String> setReturnValue(String inputId, Object value) {
        List<String> changes = new ArrayList<>();
        AbstractInput ai = getElement(inputId, AbstractInput.class);

        if (ai == null) {
            return changes;
        }

        ai.setReturnContent(value);

        changes.add(ai.getId());
        changes.addAll(runElementEvents(ai));

        Collection<AbstractInput> mayRelatedOtherInputsByOperation = getInputsHavingValueOperation();

        for (AbstractInput otherAi : mayRelatedOtherInputsByOperation) {
            if (otherAi == null || otherAi.getId().equals(inputId)) {
                continue;
            }

            boolean operationShoultBeApplied = false;

            AbstractOperation otherAo = otherAi.getValueOperation();

            switch (otherAo.getOperationType()) {
                case SUM_IVS: {
                    SumInputValuesOperation otherOp = (SumInputValuesOperation) otherAo;

                    if (otherOp.getRangeIds().contains(inputId)) {
                        operationShoultBeApplied = true;
                    }
                }
                break;
                case SUM_IVS_BY_INDEX_RANGE:
                    break;
                case SUM_IVS_BY_CLASSES: {
                    SumInputValuesByClassesOperation otherOp = (SumInputValuesByClassesOperation) otherAo;

                    operationShoultBeApplied = otherOp.containsAtLeastOneClass(ai.getClassNames());
                }
                break;
            }

            if (operationShoultBeApplied) {
                applyValueOperation(otherAi);

                changes.add(otherAi.getId());
                changes.addAll(runElementEvents(otherAi));
            }
        }

        return changes;
    }

    @Override
    public Collection<String> setChecked(String inputOptionId, boolean checked) {

        Collection<String> changes = new ArrayList<>();

        InputOptionContent ioc = getElement(inputOptionId, InputOptionContent.class);

        if (ioc == null || (ioc.isChecked() == checked)) {
            return changes;
        }

        if (checked) {
            ContentContainer parentContainer = getElement(ioc.getParentId(), ContentContainer.class);

            if (parentContainer != null) {
                switch (parentContainer.getResultType()) {
                    case SINGLE: {
                        for (AbstractInput ai : parentContainer.getContentInputs()) {
                            switch (ai.getElementType()) {
                                case FORM:
                                    break;
                                case QUESTION:
                                    break;
                                case ANSWER:
                                    break;
                                case CONTENT_CONTAINER:
                                    break;
                                case INPUT_TEXT:
//                            {
//                                ai.setReturnContent(null);
//                                changes.add(ai.getId());
//                            }
                                    break;
                                case INPUT_OPTION: {
                                    if (ai.getId().equals(ioc.getId())) {
                                        continue;
                                    }

                                    InputOptionContent iocOther = (InputOptionContent) ai;

                                    if (iocOther.isChecked() == false) {
                                        continue;
                                    }

                                    iocOther.setChecked(false);
                                    changes.add(ai.getId());
                                    changes.addAll(runElementEvents(iocOther, FormEventType.ON_UNCHECKED));
                                }
                                break;
                                default:
                                    break;
                            }
                        }
                    }
                    break;
                    case MULTIPLE:
                        break;
                    default:
                        break;

                }
            }
        }

        ioc.setChecked(checked);

        changes.add(ioc.getId());
        changes.addAll(runElementEvents(ioc, checked ? FormEventType.ON_CHECKED : FormEventType.ON_UNCHECKED));

        return changes;
    }

    @Override
    public Collection<String> setEnabled(String elementId, boolean enabled) {
        Collection<String> changes = new ArrayList<>();

        AbstractFormElement afe = getElementById(elementId);

        if (afe == null || (afe.isEnabled() == enabled)) {
            return changes;
        }

//        if(!enabled && !getEnabledStateInTree(elementId))
//        {
//            return changes;
//        }
        afe.setEnabled(enabled);
        changes.addAll(applyEnabledStateToSubElementsTree(afe, enabled));

        changes.add(afe.getId());
        changes.addAll(runElementEvents(afe, enabled ? FormEventType.ON_ENABLED : FormEventType.ON_DISABLED));

        return changes;
    }

    private Collection<String> runSubEventsTree(AbstractFormElement abstractFormElement, FormEventType... eventTypes) {
        Collection<String> changes = new ArrayList<>();

        if (abstractFormElement == null) {
            return changes;
        }

        switch (abstractFormElement.getElementType()) {
            case FORM: {
                QuestionForm qf = (QuestionForm) abstractFormElement;

                FormBody fb = qf.getBody();
                changes.addAll(runEventsTree(fb, eventTypes));
            }
            break;
            case FORM_BODY: {
                FormBody fb = (FormBody) abstractFormElement;
                for (AbstractFormElement afe : fb.getChildren()) {
                    changes.addAll(runEventsTree(afe, eventTypes));
                }
            }
            break;
            case SECTION: {
                Section sc = (Section) abstractFormElement;
                for (AbstractFormElement afe : sc.getChildren()) {
                    changes.addAll(runEventsTree(afe, eventTypes));
                }
            }
            break;
            case QUESTION: {
                Question q = (Question) abstractFormElement;
                for (AbstractAnswer aa : q.getAnswers()) {
                    changes.addAll(runEventsTree(aa, eventTypes));
                }
            }
            break;
            case ANSWER: {
                AbstractAnswer aa = (AbstractAnswer) abstractFormElement;
                for (ContentContainer cc : aa.getAnswerContentContainers()) {
                    changes.addAll(runEventsTree(cc, eventTypes));
                }
            }
            break;
            case CONTENT_CONTAINER: {
                ContentContainer cc = (ContentContainer) abstractFormElement;
                for (AbstractInput ai : cc.getContentInputs()) {
                    changes.addAll(runEventsTree(ai, eventTypes));
                }
            }
            break;
            case INPUT_TEXT: {
                InputTextContent itc = (InputTextContent) abstractFormElement;
                for (ContentContainer ccSub : itc.getContentContainers()) {
                    changes.addAll(runEventsTree(ccSub, eventTypes));
                }
            }
            break;
            case INPUT_OPTION: {
                InputOptionContent ioc = (InputOptionContent) abstractFormElement;
                for (ContentContainer ccSub : ioc.getContentContainers()) {
                    changes.addAll(runEventsTree(ccSub, eventTypes));
                }
            }
            break;
            default:
                break;

        }

        return changes;
    }

    private Collection<String> applyEnabledStateToTree(AbstractFormElement abstractFormElement,
            boolean enabled) {
        Collection<String> changes = new ArrayList<>();

        if (abstractFormElement == null) {
            return changes;
        }

        if ((enabled && (abstractFormElement.isEnabled() != enabled))
                || (!enabled && abstractFormElement.isEnabled() == enabled)) {
            return changes;
        }

        FormEventType eventTypes = enabled ? FormEventType.ON_ENABLED : FormEventType.ON_DISABLED;
        changes.addAll(runEvents(abstractFormElement, eventTypes));

        switch (abstractFormElement.getElementType()) {
            case FORM: {
                QuestionForm qf = (QuestionForm) abstractFormElement;
                FormBody fb = qf.getBody();
                changes.addAll(applyEnabledStateToTree(fb, enabled));
            }
            break;
            case FORM_BODY: {
                FormBody fb = (FormBody) abstractFormElement;
                for (AbstractFormElement afe : fb.getChildren()) {
                    changes.addAll(applyEnabledStateToTree(afe, enabled));
                }
            }
            break;
            case SECTION: {
                Section sc = (Section) abstractFormElement;
                for (AbstractFormElement afe : sc.getChildren()) {
                    changes.addAll(applyEnabledStateToTree(afe, enabled));
                }
            }
            break;
            case QUESTION: {
                Question q = (Question) abstractFormElement;
                for (AbstractAnswer aa : q.getAnswers()) {
                    changes.addAll(applyEnabledStateToTree(aa, enabled));
                }
            }
            break;
            case ANSWER: {
                AbstractAnswer aa = (AbstractAnswer) abstractFormElement;
                for (ContentContainer cc : aa.getAnswerContentContainers()) {
                    changes.addAll(applyEnabledStateToTree(cc, enabled));
                }
            }
            break;
            case CONTENT_CONTAINER: {
                ContentContainer cc = (ContentContainer) abstractFormElement;
                for (AbstractInput ai : cc.getContentInputs()) {
                    changes.addAll(applyEnabledStateToTree(ai, enabled));
                }
            }
            break;
            case INPUT_TEXT: {
                InputTextContent itc = (InputTextContent) abstractFormElement;
                for (ContentContainer ccSub : itc.getContentContainers()) {
                    changes.addAll(applyEnabledStateToTree(ccSub, enabled));
                }
            }
            break;
            case INPUT_OPTION: {
                InputOptionContent ioc = (InputOptionContent) abstractFormElement;
                for (ContentContainer ccSub : ioc.getContentContainers()) {
                    changes.addAll(applyEnabledStateToTree(ccSub, enabled));
                }
            }
            break;
            default:
                break;
        }

        return changes;
    }

    private Collection<String> applyEnabledStateToSubElementsTree(AbstractFormElement abstractFormElement,
            boolean enabled) {
        Collection<String> changes = new ArrayList<>();

        if (abstractFormElement == null) {
            return changes;
        }

        switch (abstractFormElement.getElementType()) {
            case FORM: {
                QuestionForm qf = (QuestionForm) abstractFormElement;
                FormBody fb = qf.getBody();
                changes.addAll(applyEnabledStateToTree(fb, enabled));
            }
            break;
            case FORM_BODY: {
                FormBody fb = (FormBody) abstractFormElement;
                for (AbstractFormElement afe : fb.getChildren()) {
                    changes.addAll(applyEnabledStateToTree(afe, enabled));
                }
            }
            break;
            case SECTION: {
                Section sc = (Section) abstractFormElement;
                for (AbstractFormElement afe : sc.getChildren()) {
                    changes.addAll(applyEnabledStateToTree(afe, enabled));
                }
            }
            break;
            case QUESTION: {
                Question q = (Question) abstractFormElement;
                for (AbstractAnswer aa : q.getAnswers()) {
                    changes.addAll(applyEnabledStateToTree(aa, enabled));
                }
            }
            break;
            case ANSWER: {
                AbstractAnswer aa = (AbstractAnswer) abstractFormElement;
                for (ContentContainer cc : aa.getAnswerContentContainers()) {
                    changes.addAll(applyEnabledStateToTree(cc, enabled));
                }
            }
            break;
            case CONTENT_CONTAINER: {
                ContentContainer cc = (ContentContainer) abstractFormElement;
                for (AbstractInput ai : cc.getContentInputs()) {
                    changes.addAll(applyEnabledStateToTree(ai, enabled));
                }
            }
            break;
            case INPUT_TEXT: {
                InputTextContent itc = (InputTextContent) abstractFormElement;
                for (ContentContainer ccSub : itc.getContentContainers()) {
                    changes.addAll(applyEnabledStateToTree(ccSub, enabled));
                }
            }
            break;
            case INPUT_OPTION: {
                InputOptionContent ioc = (InputOptionContent) abstractFormElement;
                for (ContentContainer ccSub : ioc.getContentContainers()) {
                    changes.addAll(applyEnabledStateToTree(ccSub, enabled));
                }
            }
            break;
            default:
                break;
        }

        return changes;
    }

    private Collection<String> runEventsTree(AbstractFormElement abstractFormElement, FormEventType... eventTypes) {
        Collection<String> changes = new ArrayList<>();

        if (abstractFormElement == null) {
            return changes;
        }

        switch (abstractFormElement.getElementType()) {
            case FORM: {
                QuestionForm qf = (QuestionForm) abstractFormElement;
                changes.addAll(runEvents(qf, eventTypes));

                FormBody fb = qf.getBody();

                changes.addAll(runEvents(fb, eventTypes));
                if (fb != null) {
                    for (AbstractFormElement afe : fb.getChildren()) {
                        changes.addAll(runEventsTree(afe, eventTypes));
                    }
                }
            }
            break;
            case FORM_BODY: {
                FormBody fb = (FormBody) abstractFormElement;
                changes.addAll(runEvents(fb, eventTypes));
                for (AbstractFormElement afe : fb.getChildren()) {
                    changes.addAll(runEventsTree(afe, eventTypes));
                }
            }
            break;
            case SECTION: {
                Section sc = (Section) abstractFormElement;
                changes.addAll(runEvents(sc, eventTypes));

                for (AbstractFormElement afe : sc.getChildren()) {
                    changes.addAll(runEventsTree(afe, eventTypes));
                }
            }
            break;
            case QUESTION: {
                Question q = (Question) abstractFormElement;

                changes.addAll(runEvents(q, eventTypes));
                for (AbstractAnswer aa : q.getAnswers()) {
                    changes.addAll(runEventsTree(aa, eventTypes));
                }
            }
            break;
            case ANSWER: {
                AbstractAnswer aa = (AbstractAnswer) abstractFormElement;

                changes.addAll(runEvents(aa, eventTypes));
                for (ContentContainer cc : aa.getAnswerContentContainers()) {
                    changes.addAll(runEventsTree(cc, eventTypes));
                }
            }
            break;
            case CONTENT_CONTAINER: {
                ContentContainer cc = (ContentContainer) abstractFormElement;

                changes.addAll(runEvents(cc, eventTypes));
                for (AbstractInput ai : cc.getContentInputs()) {
                    changes.addAll(runEventsTree(ai, eventTypes));
                }
            }
            break;
            case INPUT_TEXT: {
                InputTextContent itc = (InputTextContent) abstractFormElement;

                changes.addAll(runEvents(itc, eventTypes));

                for (ContentContainer ccSub : itc.getContentContainers()) {
                    changes.addAll(runEventsTree(ccSub, eventTypes));
                }
            }
            break;
            case INPUT_OPTION: {
                InputOptionContent ioc = (InputOptionContent) abstractFormElement;

                changes.addAll(runEvents(ioc, eventTypes));

                for (ContentContainer ccSub : ioc.getContentContainers()) {
                    changes.addAll(runEventsTree(ccSub, eventTypes));
                }
            }
            break;
            default:
                break;

        }

        return changes;
    }

    @Override
    public Collection<InputValidationResult> validateInputs() {
        Collection<InputValidationResult> validationResults = new ArrayList<>();

        for (AbstractInput abstractInput : getInputsHavingValidations()) {
            InputValidationResult ivr = validateInput(abstractInput);
            validationResults.add(ivr);
        }

        return validationResults;
    }

    @Override
    public InputValidationResult validateInput(String inputTextId) {
        return validateInput(getElement(inputTextId, AbstractInput.class));
    }

    @Override
    public boolean isValid(String inputTextId) {
        return validateInput(inputTextId).isIsValid();
    }

    @Override
    public AbstractFormElement getElementById(String id) {
        loadElements();

        if (Strings.isNothing(id)) {
            return null;
        }

        id = id.trim();

        return _elements.get(id);
    }

    @Override
    public Collection<AbstractFormElement> getElementsByClass(String className) {
        Collection<AbstractFormElement> classElements = new ArrayList<>();

        if (Strings.isNothing(className)) {
            return classElements;
        }

        className = className.trim();

        for (AbstractFormElement afe : _elementsHavingClasses) {
            if (afe.hasClass(className)) {
                classElements.add(afe);
            }
        }

        return classElements;
    }

    @Override
    public FormResult getFormResult() {
        FormResult formResult = new FormResult();
        formResult.setId(_questionForm.getId());
        formResult.setLang(_questionForm.getLanguage());
        formResult.setVersion(_questionForm.getVersion());

        for (Question question : getQuestions()) {

            List<AbstractInput> returnableInputs = getReturnableInputs(question.getId());

            if (returnableInputs != null && !returnableInputs.isEmpty()) {
                QuestionResult qr = new QuestionResult();
                qr.setId(question.getId());

                for (AbstractInput ai : returnableInputs) {
                    InputResult ir = new InputResult();

                    ir.setColumnDefinition(ai.getColumnDefinition());
                    ir.setColumnDefinitionType(ai.getColumnDefinitionType());
                    ir.setId(ai.getId());
                    ir.setReturnValue(String.valueOf(ai.getReturnContent()));

                    qr.AddInputResult(ir);
                }

                formResult.AddQuestionResult(qr);
            }
        }

        return formResult;
    }

    @Override
    public Collection<ContentContainer> getRequiredContainers() {
        Collection<ContentContainer> containers = new ArrayList<>();

        for (AbstractFormElement afe : _elements.values()) {
            if (!afe.getElementType().equals(FormElementType.CONTENT_CONTAINER)) {
                continue;
            }

            ContentContainer container = (ContentContainer) afe;

            if (!container.isReturnRequeired()) {
                continue;
            }

            containers.add(container);
        }

        return containers;
    }

    @Override
    public Collection<ContentContainer> getUnsatisfiedContainers() {

        Collection<ContentContainer> requiredContainers = getRequiredContainers();

        Collection<ContentContainer> urc = new ArrayList<>();

        for (ContentContainer cc : requiredContainers) {
            if (!cc.isEnabled() || isDisabledInTree(cc.getId()) || hasReturnableInputs(cc)) {
                continue;
            }

            urc.add(cc);
        }

        return urc;
    }

    @Override
    public Object getInputReturnValue(String inputId) {
        AbstractInput abi = getElement(inputId, AbstractInput.class);

        if (abi == null) {
            return null;
        }

        return abi.getReturnContent();
    }

    @Override
    public List<AbstractInput> getReturnableInputs(String elementId) {
        AbstractFormElement afe = getElementById(elementId);

        return getReturnableInputs(afe);
    }

    @Override
    public boolean hasAnyReturnableInputs(String elementId) {
        AbstractFormElement afe = getElementById(elementId);

        return hasAnyReturnableInput(afe);
    }

    @Override
    public ContentContainer getContentContainerById(String id) {
        return getElement(id, ContentContainer.class);
    }

    @Override
    public InputTextContent getInputTextById(String id) {
        return getElement(id, InputTextContent.class);
    }

    @Override
    public InputOptionContent getInputOptionById(String id) {
        return getElement(id, InputOptionContent.class);
    }

    @Override
    public SimpleAnswer getAnswerById(String id) {
        return getElement(id, SimpleAnswer.class);
    }

    @Override
    public Question getQuestionById(String id) {
        return getElement(id, Question.class);
    }

    @Override
    public Collection<AbstractInput> getInputContents() {
        return getElements(AbstractInput.class);
    }

    @Override
    public Collection<InputTextContent> getInputTextContents() {
        return getElements(InputTextContent.class);
    }

    @Override
    public Collection<InputOptionContent> getInputOptionContents() {
        return getElements(InputOptionContent.class);
    }

    @Override
    public Collection<ContentContainer> getContentContainers() {
        return getElements(ContentContainer.class);
    }

    @Override
    public Collection<Question> getQuestions() {
        return getElements(Question.class);
    }

    @Override
    public Collection<AbstractAnswer> getAnswers() {
        return getElements(AbstractAnswer.class);
    }

    @Override
    public Collection<AbstractInput> getAvailableInputsHavingValidations() {
        Collection<AbstractInput> inputs = getInputsHavingValidations();

        inputs.removeIf(x -> !x.isEnabled() || isAnyParentDisabled(x.getId()));

        return inputs;
    }

    @Override
    public Collection<AbstractInput> getInputsHavingValidations() {
        Collection<AbstractInput> inputs = new ArrayList<>();

        Collection<AbstractInput> allInputs = getElements(AbstractInput.class);

        for (AbstractInput ai : allInputs) {
            if (!ai.hasValidations()) {
                continue;
            }

            inputs.add(ai);
        }

        return inputs;
    }

    @Override
    public Collection<AbstractInput> getInputsHavingValueOperation() {
        Collection<AbstractInput> inputs = new ArrayList<>();

        Collection<AbstractInput> allInputs = getElements(AbstractInput.class);

        for (AbstractInput ai : allInputs) {
            if (!ai.hasValueOperation()) {
                continue;
            }

            inputs.add(ai);
        }

        return inputs;
    }

    @Override
    public AbstractFormElement cloneExactById(String id) {
        AbstractFormElement element = getElementById(id);

        if (element == null) {
            return null;
        }

        return element.CloneExact();
    }

    @Override
    public <TElement extends AbstractFormElement> TElement getParentElementOfType(String id, Class<TElement> elementClass) {
        AbstractFormElement afeSupposed = getElementById(id);

        if (afeSupposed == null) {
            return null;
        }

        if (elementClass.isAssignableFrom(afeSupposed.getClass())) {
            return elementClass.cast(afeSupposed);
        }

        return getParentElementOfType(afeSupposed.getParentId(), elementClass);
    }

    @Override
    public <TElement extends AbstractFormElement> TElement getElement(String id,
            Class<TElement> elementClass) {
        try {
            AbstractFormElement afe = getElementById(id);

            return elementClass.cast(afe);
        } catch (Exception exp) {
            //ignored
            return null;
        }
    }

    @Override
    public <TElement extends AbstractFormElement> Collection<TElement> getElements(Class<TElement> elementClass) {
        Collection<TElement> elements = new ArrayList<>();

        for (AbstractFormElement afe : _elements.values()) {
            if (!elementClass.isAssignableFrom(afe.getClass())) {
                continue;
            }

            elements.add(elementClass.cast(afe));
        }

        return elements;
    }

    // helpful methods
    private InputValidationResult validateInput(AbstractInput abstractInput) {
        if (abstractInput == null) {
            return new InputValidationResult(true);
        }

        Collection<AbstractCondition> validations = abstractInput.getValidations();

        if (validations == null || validations.isEmpty()) {
            return new InputValidationResult(abstractInput, true);
        }

        for (AbstractCondition ac : validations) {
            if (!isConditionSatisfiedWithValue(ac, abstractInput)) {
                return new InputValidationResult(abstractInput, ac, false);
            }
        }

        return new InputValidationResult(abstractInput, true);
    }

    private boolean hasReturnableInputs(AbstractFormElement afe) {
        return !getReturnableInputs(afe).isEmpty();
    }

    private List<AbstractInput> getReturnableInputs(AbstractFormElement abstractFormElement) {
        List<AbstractInput> inputs = new ArrayList<>();

        if (abstractFormElement == null) {
            return inputs;
        }

        if (getEnabledStateInTree(abstractFormElement.getId())) {
            switch (abstractFormElement.getElementType()) {
                case FORM: {
                    QuestionForm qf = (QuestionForm) abstractFormElement;

                    FormBody fb = qf.getBody();

                    if (fb != null) {
                        for (AbstractFormElement afe : fb.getChildren()) {
                            inputs.addAll(getReturnableInputs(afe));
                        }
                    }
                }
                break;
                case FORM_BODY: {
                    FormBody sc = (FormBody) abstractFormElement;

                    for (AbstractFormElement afe : sc.getChildren()) {
                        inputs.addAll(getReturnableInputs(afe));
                    }
                }
                break;
                case SECTION: {
                    Section sc = (Section) abstractFormElement;

                    for (AbstractFormElement afe : sc.getChildren()) {
                        inputs.addAll(getReturnableInputs(afe));
                    }
                }
                break;

                case QUESTION: {
                    Question q = (Question) abstractFormElement;
                    for (AbstractAnswer aa : q.getAnswers()) {
                        if (aa == null || !aa.isEnabled()) {
                            continue;
                        }

                        inputs.addAll(getReturnableInputs(aa.getId()));
                    }
                }
                break;
                case ANSWER: {
                    AbstractAnswer aa = (AbstractAnswer) abstractFormElement;
                    for (ContentContainer cc : aa.getAnswerContentContainers()) {
                        if (cc == null || !cc.isEnabled()) {
                            continue;
                        }

                        inputs.addAll(getReturnableInputs(cc.getId()));
                    }
                }
                break;
                case CONTENT_CONTAINER: {
                    ContentContainer cc = (ContentContainer) abstractFormElement;

                    for (AbstractInput ai : cc.getContentInputs()) {
                        if (ai == null) {
                            continue;
                        }

                        if (!ai.isIndividuallyReturnable()) {
                            continue;
                        }

                        inputs.add(ai);

                        if (ai.isComplex()) {
                            for (ContentContainer subCc : ai.getContentContainers()) {
                                if (subCc == null || !subCc.isEnabled()) {
                                    continue;
                                }

                                inputs.addAll(getReturnableInputs(subCc.getId()));
                            }
                        }
                    }
                }
                break;
                case INPUT_TEXT:
                case INPUT_OPTION: {
                    AbstractInput ai = (AbstractInput) abstractFormElement;
                    if (ai.isIndividuallyReturnable()) {
                        inputs.add(ai);
                    }
                }
                break;
                default:
                    break;

            }
        }

        return inputs;
    }

    private boolean hasAnyReturnableInput(AbstractFormElement abstractFormElement) {

        if (abstractFormElement == null) {
            return false;
        }

        if (getEnabledStateInTree(abstractFormElement.getId())) {
            switch (abstractFormElement.getElementType()) {
                case FORM: {
                    QuestionForm qf = (QuestionForm) abstractFormElement;
                    FormBody fb = qf.getBody();

                    if (hasAnyReturnableInput(fb)) {
                        return true;
                    }
                }
                break;
                case FORM_BODY: {
                    FormBody fb = (FormBody) abstractFormElement;

                    for (AbstractFormElement afe : fb.getChildren()) {
                        if (afe == null || !afe.isEnabled()) {
                            continue;
                        }

                        if (hasAnyReturnableInput(afe)) {
                            return true;
                        }
                    }
                }
                break;
                case SECTION: {
                    Section sc = (Section) abstractFormElement;

                    for (AbstractFormElement afe : sc.getChildren()) {
                        if (afe == null || !afe.isEnabled()) {
                            continue;
                        }

                        if (hasAnyReturnableInput(afe)) {
                            return true;
                        }
                    }
                }
                break;
                case QUESTION: {
                    Question q = (Question) abstractFormElement;
                    for (AbstractAnswer aa : q.getAnswers()) {
                        if (aa == null || !aa.isEnabled()) {
                            continue;
                        }

                        if (hasAnyReturnableInput(aa)) {
                            return true;
                        }
                    }
                }
                break;
                case ANSWER: {
                    AbstractAnswer aa = (AbstractAnswer) abstractFormElement;
                    for (ContentContainer cc : aa.getAnswerContentContainers()) {
                        if (cc == null || !cc.isEnabled()) {
                            continue;
                        }

                        if (hasAnyReturnableInput(cc)) {
                            return true;
                        }
                    }
                }
                break;
                case CONTENT_CONTAINER: {
                    ContentContainer cc = (ContentContainer) abstractFormElement;

                    for (AbstractInput ai : cc.getContentInputs()) {
                        if (ai == null) {
                            continue;
                        }

                        if (ai.isIndividuallyReturnable()) {
                            return true;
                        }

                        if (ai.isComplex()) {
                            for (ContentContainer subCc : ai.getContentContainers()) {
                                if (subCc == null || !subCc.isEnabled()) {
                                    continue;
                                }

                                if (hasAnyReturnableInput(subCc)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                break;
                case INPUT_TEXT:
                case INPUT_OPTION: {
                    AbstractInput ai = (AbstractInput) abstractFormElement;
                    return ai.isIndividuallyReturnable();
                }
                default:
                    break;

            }
        }

        return false;
    }

    private Collection<String> runProcedures(Collection<AbstractProcedure> abstractProcedures) {
        Collection<String> changes = new ArrayList<>();

        if (abstractProcedures == null || abstractProcedures.isEmpty()) {
            return changes;
        }

        for (AbstractProcedure abstractProcedure : abstractProcedures) {
            changes.addAll(runProcedure(abstractProcedure));
        }

        return changes;
    }

    private Collection<String> runProcedure(AbstractProcedure abstractProcedure) {
        Collection<String> changes = new ArrayList<>();

        if (abstractProcedure == null) {
            return changes;
        }

        switch (abstractProcedure.getProcedureType()) {
            case ENABLE_ELEMENTS: {
                EnableElementsProcedure procedure = (EnableElementsProcedure) abstractProcedure;

                for (String elmId : procedure.getElementsIdsToEnable()) {
                    changes.addAll(setEnabled(elmId, true));
                }
            }
            break;
            case DISABLE_ELEMENTS: {
                DisableElementsProcedure procedure = (DisableElementsProcedure) abstractProcedure;

                for (String elmId : procedure.getElementsIdsToDisable()) {
                    changes.addAll(setEnabled(elmId, false));
                }
            }
            break;
            case SET_RETURN_REQUIRED: {
                SetReturnRequiredProcedure procedure = (SetReturnRequiredProcedure) abstractProcedure;

                for (String elmId : procedure.getElements()) {
                    changes.addAll(setReturnRequired(elmId, procedure.isRequired()));
                }
            }
            break;
            case ENABLE_ELEMENTS_BY_CLASS: {
                EnableElementsByClassProcedure procedure = (EnableElementsByClassProcedure) abstractProcedure;

                for (String className : procedure.getElementsClasses()) {
                    Collection<AbstractFormElement> classElements = getElementsByClass(className);

                    for (AbstractFormElement afeToEnable : classElements) {
                        changes.addAll(setEnabled(afeToEnable.getId(), procedure.isEnabled()));
                    }
                }
            }
            break;
            default:
                break;
        }

        return changes;
    }

    private Collection<String> runElementEvents(AbstractFormElement abstractFormElement, FormEventType... eventTypes) {
        return runEvents(abstractFormElement, eventTypes);
    }

    private Collection<String> runEvents(AbstractFormElement abstractFormElement, FormEventType... eventTypes) {
        Collection<String> changes = new ArrayList<>();

        if (abstractFormElement == null) {
            return changes;
        }

        if (eventTypes == null) {
            eventTypes = new FormEventType[0];
        }

        Collection<FormEventType> eventTypesColl = new ArrayList<>();
        eventTypesColl.addAll(Arrays.asList(eventTypes));

        eventTypesColl.add(FormEventType.ON_CHANGED);

        if (eventTypesColl.contains(FormEventType.ON_CHANGED)) {

        }

        Collection<AbstractFormEvent> events = abstractFormElement.getEvents();

        if (events != null && !events.isEmpty()) {
            for (AbstractFormEvent afe : events) {

                FormEventType fet = afe.getEventType();

                if (!eventTypesColl.contains(fet)) {
                    continue;
                }

                ConditionGroup cg = afe.getConditionGroup();

                boolean conditionSatisfied = isConditionGroupSatisfied(cg, abstractFormElement);

                if (!conditionSatisfied) {
                    continue;
                }

                Collection<AbstractProcedure> abstractProcedures = afe.getProcedures();
                Collection<String> otherChanges = new ArrayList<>();
                switch (fet) {
                    case ON_CHANGED: {
                        otherChanges = runProcedures(abstractProcedures);
                    }
                    break;
                    case ON_ENABLED: {
                        if (getEnabledStateInTree(abstractFormElement.getId())) {
                            otherChanges = runProcedures(abstractProcedures);
                        }
                    }
                    break;
                    case ON_DISABLED:
                        if (getEnabledStateInTree(abstractFormElement.getId()) == false) {
                            otherChanges = runProcedures(abstractProcedures);
                        }
                        break;
                    case ON_CHECKED: {
                        if (abstractFormElement.getElementType().equals(FormElementType.INPUT_OPTION)) {
                            InputOptionContent ioc = (InputOptionContent) abstractFormElement;
                            if (ioc.isChecked()) {
                                otherChanges = runProcedures(abstractProcedures);
                            }
                        }
                    }
                    break;
                    case ON_UNCHECKED: {
                        if (abstractFormElement.getElementType().equals(FormElementType.INPUT_OPTION)) {
                            InputOptionContent ioc = (InputOptionContent) abstractFormElement;
                            if (!ioc.isChecked()) {
                                otherChanges = runProcedures(abstractProcedures);
                            }
                        }
                    }
                    break;
                }
                changes.addAll(otherChanges);
            }
        }

        return changes;
    }

    private Object runOperation(AbstractOperation abstractOperation) {
        if (abstractOperation == null) {
            return null;
        }

        Object result = null;

        switch (abstractOperation.getOperationType()) {
            case SUM_IVS: {
                SumInputValuesOperation sivo = (SumInputValuesOperation) abstractOperation;

                List<String> otherInputIds = sivo.getRangeIds();

                if (otherInputIds != null && !otherInputIds.isEmpty()) {
                    List<Double> otherInputsDoubleValues = new ArrayList<Double>();
                    for (String otherInputId : otherInputIds) {
                        Object otherInputValue = getInputReturnValue(otherInputId);

                        if (otherInputValue == null) {
                            continue;
                        }

                        Double dblVal = NumberUtil.getAsDouble(otherInputValue);

                        if (dblVal == null) {
                            continue;
                        }

                        otherInputsDoubleValues.add(dblVal);
                    }

                    result = NumberUtil.sum(otherInputsDoubleValues);
                }

            }
            break;
            case SUM_IVS_BY_INDEX_RANGE:
                //{
//                    SumInputValuesByIndexRangeOperation operation = (SumInputValuesByIndexRangeOperation) valueOperation;
//                    ContentContainer parentCc = getElement(ai.getParentId(), ContentContainer.class);
//
//                    if (parentCc != null) {
//                        List<Double> otherInputsDoubleValues = new ArrayList<Double>();
//
//                        List<AbstractInput> parentCcInputs = parentCc.getContentInputs();
//                        for (int ri = operation.getIndexRangeStart(); ri <= operation.getIndexRangeEnd(); ri++) {
//
//                            if (ri > parentCcInputs.size() - 1) {
//                                continue;
//                            }
//
//                            AbstractInput ccInput = parentCcInputs.get(ri);
//
//                            Double dblVal = NumberUtil.getAsDouble(getInputReturnValue(ccInput.getId()));
//
//                            if (dblVal == null) {
//                                continue;
//                            }
//
//                            otherInputsDoubleValues.add(dblVal);
//                        }
//
//                        result = NumberUtil.sum(otherInputsDoubleValues);
//                    }
//                }
                break;
            case SUM_IVS_BY_CLASSES: {
                SumInputValuesByClassesOperation operation = (SumInputValuesByClassesOperation) abstractOperation;

                List<Double> doubleVals = new ArrayList<Double>();

                for (String className : operation.getInputClasses()) {

                    if (Strings.hasNoContent(className)) {
                        continue;
                    }

                    for (AbstractFormElement afeClass : getElementsByClass(className)) {
                        if (afeClass instanceof AbstractInput) {
                            AbstractInput abstractInput = (AbstractInput) afeClass;

                            Double dblVal = NumberUtil.getAsDouble(abstractInput.getReturnContent());

                            if (dblVal == null) {
                                continue;
                            }

                            doubleVals.add(dblVal);
                        }
                    }
                }

                result = NumberUtil.sum(doubleVals);
            }
            break;
            case CHECK_CONDITION: {
                CheckConditionOperation operation = (CheckConditionOperation) abstractOperation;
                return isConditionGroupSatisfied(operation.getConditionGroup(), null);
            }
            default:
                break;

        }
        return result;
    }

    private void applyValueOperation(AbstractInput abstractInput) {
        if (abstractInput == null) {
            return;
        }

        AbstractOperation ao = abstractInput.getValueOperation();

        if (ao == null) {
            return;
        }

        abstractInput.setReturnContent(runOperation(ao));
    }

    private boolean isComparisonSatisfied(Double number1, Double number2, ComparisonType comparisonType) {
        if (number1 == null && number2 != null) {
            return false;
        }

        if (number2 == null && number1 != null) {
            return false;
        }

        if (number1 == null && number2 == null) {
            return true;
        }

        switch (comparisonType) {
            case EQ:
                return Double.compare(number1, number2) == 0;
            case NT:
                return Double.compare(number1, number2) != 0;
            case GT:
                return Double.compare(number1, number2) > 0;
            case LT:
                return Double.compare(number1, number2) < 0;
            case GTE:
                return Double.compare(number1, number2) >= 0;
            case LTE:
                return Double.compare(number1, number2) <= 0;
            default:
                return false;
        }
    }

    private boolean isConditionGroupSatisfied(ConditionGroup conditionGroup,
            AbstractFormElement abstractFormElement) {
        if (conditionGroup == null) {
            return true;
        }

        switch (conditionGroup.getChoiceType()) {
            case COMBINATION: {
                ConditionCombination condComb = conditionGroup.getCombination();

                switch (condComb.getCombinationType()) {
                    case AND: {
                        for (ConditionGroup cgSub : condComb.getConditionGroups()) {
                            boolean cgSubSatisfied = isConditionGroupSatisfied(cgSub, abstractFormElement);

                            if (!cgSubSatisfied) {
                                return false;
                            }
                        }

                        return true;
                    }
                    case OR: {
                        for (ConditionGroup cgSub : condComb.getConditionGroups()) {
                            boolean cgSubSatisfied = isConditionGroupSatisfied(cgSub, abstractFormElement);

                            if (cgSubSatisfied) {
                                return true;
                            }
                        }

                        return false;
                    }
                    default:
                        throw new AssertionError(condComb.getCombinationType().name());
                }
            }
            case CONDITION:
                return isConditionSatisfied(conditionGroup.getCondition(), abstractFormElement);
            default:
                return false;
        }
    }

    private boolean isConditionSatisfied(AbstractCondition abstractCondition,
            AbstractFormElement abstractFormElement) {
        if (abstractCondition == null) {
            return true;
        }

        if (abstractFormElement == null) {
            return false;
        }

        if (abstractFormElement instanceof AbstractInput) {
            AbstractInput abstractInput = (AbstractInput) abstractFormElement;
            return isConditionSatisfiedWithValue(abstractCondition, abstractInput);
        }

        return false;
    }

    private boolean isConditionSatisfiedWithValue(AbstractCondition abstractCondition, AbstractFormElement conditionRefElm) {

        if (abstractCondition == null) {
            return true;
        }

        try {
            Object value = getInputReturnValue(conditionRefElm);
            switch (abstractCondition.getConditionType()) {
                case COMPARE_WITH_DV: {

                    if (value == null || Strings.isNullOrEmpty(value.toString())) {
                        return true;
                    }
                    Double doubleValue = Double.valueOf(value.toString());

                    CompareWithDirectValueCondition condition = (CompareWithDirectValueCondition) abstractCondition;
                    Double valueToCompare = condition.getCompareWith();
                    return isComparisonSatisfied(doubleValue, valueToCompare, condition.getComparisonType());
                }
                case COMPARE_WITH_IV: {

                    if (value == null || Strings.isNullOrEmpty(value.toString())) {
                        return true;
                    }
                    Double doubleValue = Double.valueOf(value.toString());

                    CompareWithInputValueCondition condition = (CompareWithInputValueCondition) abstractCondition;
                    String inputId = condition.getCompareWith();
                    Object valueToCompare = getInputReturnValue(inputId);
                    return isComparisonSatisfied(doubleValue, NumberUtil.getAsDouble(valueToCompare), condition.getComparisonType());
                }
                case IS_IN_RANGE_DVS: {
                    if (value == null || Strings.isNullOrEmpty(value.toString())) {
                        return true;
                    }
                    Double doubleValue = Double.valueOf(value.toString());

                    IsInRangeOfDirectValuesCondition condition = (IsInRangeOfDirectValuesCondition) abstractCondition;
                    return NumberUtil.isInRange(doubleValue, condition.getMinValue(), condition.getMaxValue());
                }
                case IS_IN_RANGE_IVS: {
                    if (value == null || Strings.isNullOrEmpty(value.toString())) {
                        return true;
                    }
                    Double doubleValue = Double.valueOf(value.toString());

                    IsInRangeOfInputValuesCondition condition = (IsInRangeOfInputValuesCondition) abstractCondition;
                    Object valueMinToCompare = getInputReturnValue(condition.getMinValueInputId());
                    Object valueMaxToCompare = getInputReturnValue(condition.getMaxValueInputId());

                    Double minValue = NumberUtil.getAsDouble(valueMinToCompare);
                    Double maxValue = NumberUtil.getAsDouble(valueMaxToCompare);

                    return NumberUtil.isInRange(doubleValue, minValue, maxValue);
                }
                case COMPARE_IV_DV: {
                    CompareInputValueWithDirectValueCondition condition = (CompareInputValueWithDirectValueCondition) abstractCondition;
                    Object comparingObj = getInputReturnValue(condition.getComparingInputId());

                    Double comparingValue = NumberUtil.getAsDouble(comparingObj);

                    return isComparisonSatisfied(comparingValue, condition.getCompareWith(), condition.getComparisonType());
                }
                case COMPARE_IV_IV: {
                    CompareInputValueWithInputValueCondition condition = (CompareInputValueWithInputValueCondition) abstractCondition;
                    Object comparingObj1 = getInputReturnValue(condition.getComparingInputId());
                    Object comparingObj2 = getInputReturnValue(condition.getCompareWithInputId());

                    Double comparingValue1 = NumberUtil.getAsDouble(comparingObj1);
                    Double comparingValue2 = NumberUtil.getAsDouble(comparingObj2);

                    return isComparisonSatisfied(comparingValue1, comparingValue2, condition.getComparisonType());
                }
                case HAS_CONTENT:
                    return value != null && !Strings.isNullOrEmpty(value.toString());
                case HAS_NO_CONTENT:
                    return value == null || Strings.isNullOrEmpty(value.toString());
                case HAS_CONTENT_IVS: {
                    HasContentOfInputsCondition condition = (HasContentOfInputsCondition) abstractCondition;

                    Collection<String> inputIds = condition.getInputIds();

                    if (inputIds == null || inputIds.isEmpty()) {
                        return true;
                    }

                    for (String inputId : inputIds) {
                        AbstractInput input = getElement(inputId, AbstractInput.class);

                        if (input == null) {
                            continue;
                        }

                        if (Strings.hasNoContent(input.getStringReturnContent())) {
                            return false;
                        }
                    }

                }
                break;
                case HAS_NO_CONTENT_IVS: {
                    HasNoContentOfInputsCondition condition = (HasNoContentOfInputsCondition) abstractCondition;

                    Collection<String> inputIds = condition.getInputIds();

                    if (inputIds == null || inputIds.isEmpty()) {
                        return true;
                    }

                    for (String inputId : inputIds) {
                        AbstractInput input = getElement(inputId, AbstractInput.class);

                        if (input == null) {
                            continue;
                        }

                        if (Strings.hasContent(input.getStringReturnContent())) {
                            return false;
                        }
                    }
                }
                break;
                case CHECK_ENABLED: {

                    if (conditionRefElm == null) {
                        return true;
                    }

                    CheckEnabledCondition condition = (CheckEnabledCondition) abstractCondition;
                    return getEnabledStateInTree(conditionRefElm.getId()) == condition.isEnabled();
                }
                case CHECK_ENABLED_ELMS: {
                    CheckElementsAreEnabledCondition condition = (CheckElementsAreEnabledCondition) abstractCondition;

                    for (String elmId : condition.getElementsIds()) {
                        if (getEnabledStateInTree(elmId) != condition.isEnabled()) {
                            return false;
                        }
                    }

                    return true;
                }
                case CHECK_CHECKED: {
                    CheckCheckedCondition condition = (CheckCheckedCondition) abstractCondition;

                    if (conditionRefElm instanceof InputOptionContent) {
                        return ((InputOptionContent) conditionRefElm).isChecked() == condition.isChecked();
                    }
                }
                break;
                case CHECK_CHECKED_ELMS: {
                    CheckElementsAreCheckedCondition condition = (CheckElementsAreCheckedCondition) abstractCondition;

                    for (String elmId : condition.getElementsIds()) {

                        InputOptionContent ioc = getElement(elmId, InputOptionContent.class);

                        if (ioc == null) {
                            continue;
                        }

                        if (ioc.isChecked() != condition.isChecked()) {
                            return false;
                        }
                    }

                    return true;
                }
                default:
                    return false;
            }

            return true;
        } catch (Exception exp) {
            //ignored
            return false;
        }

    }

    private Object getInputReturnValue(AbstractFormElement abstractFormElement) {
        if (abstractFormElement instanceof AbstractInput) {
            return ((AbstractInput) abstractFormElement).getReturnContent();
        }

        return null;
    }
}

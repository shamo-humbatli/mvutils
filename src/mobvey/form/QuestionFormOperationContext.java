package mobvey.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mobvey.common.NumberUtil;
import mobvey.common.Strings;
import mobvey.condition.AbstractCondition;
import mobvey.condition.CompareWithDirectValueCondition;
import mobvey.condition.CompareWithInputValueCondition;
import mobvey.condition.IsInRangeOfDirectValuesCondition;
import mobvey.condition.IsInRangeOfInputValuesCondition;
import mobvey.form.answer.SimpleAnswer;
import mobvey.form.answer.content.InputOptionContent;
import mobvey.form.answer.content.InputTextContent;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.base.AbstractAnswer;
import mobvey.form.base.AbstractFormElement;
import mobvey.form.base.AbstractInput;
import mobvey.form.enums.ComparisonType;
import mobvey.form.enums.FormElementType;
import mobvey.form.events.AbstractFormEvent;
import mobvey.form.operation.IQuestionFormOperation;
import mobvey.form.question.Question;
import mobvey.form.result.FormResult;
import mobvey.form.result.InputResult;
import mobvey.form.result.QuestionResult;
import mobvey.models.InputValidationResult;
import mobvey.operation.AbstractOperation;
import mobvey.operation.SumInputValuesOperation;
import mobvey.procedure.AbstractProcedure;
import mobvey.procedure.DisableElementsProcedure;
import mobvey.procedure.EnableElementsProcedure;
import mobvey.procedure.SetReturnRequiredProcedure;

/**
 *
 * @author Shamo Humbatli
 */
public class QuestionFormOperationContext implements IQuestionFormOperation {

    private final QuestionForm _questionForm;
    private Map<String, AbstractFormElement> _elements;
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

        loadQuestions();

        _elementCount = _elements.size();
        _elementsLoaded = true;
    }

    private void loadQuestions() {
        List<Question> questions = _questionForm.getQuestions();
        for (Question q : questions) {
            if (q == null) {
                continue;
            }
            _elements.put(q.getId(), q);
            loadAnswers(q);
        }
    }

    private void loadAnswers(Question question) {
        List<AbstractAnswer> answers = question.getAnswers();
        for (AbstractAnswer aa : answers) {
            if (aa == null) {
                continue;
            }
            _elements.put(aa.getId(), aa);
            loadContentContainers(aa);
        }
    }

    private void loadContentContainers(AbstractAnswer abstractAnswer) {
        List<ContentContainer> ccs = abstractAnswer.getAnswerContentContainers();
        for (ContentContainer cc : ccs) {
            if (cc == null) {
                continue;
            }
            _elements.put(cc.getId(), cc);
            loadInputs(cc);
        }
    }

    private void loadInputs(ContentContainer contentContainer) {
        List<AbstractInput> ais = contentContainer.getContentInputs();
        for (AbstractInput ai : ais) {
            _elements.put(ai.getId(), ai);

            if (ai.isComplex()) {
                List<ContentContainer> subCcs = ai.getContentContainers();
                for (ContentContainer cc : subCcs) {
                    if (cc == null) {
                        continue;
                    }
                    _elements.put(cc.getId(), cc);
                    loadInputs(cc);
                }
            }
        }
    }

    // operations
    @Override
    public QuestionForm getQuestionForm() {
        return _questionForm;
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
    public Collection<String> setReturnRequired(String contentContainerId, boolean required) {
        AbstractFormElement element = getElementById(contentContainerId);

        List<String> changes = new ArrayList<>();
        if (element == null) {
            return changes;
        }

        if (element instanceof ContentContainer) {
            ContentContainer cc = (ContentContainer) element;
            cc.setReturnRequeired(required);

            changes.add(cc.getId());
        }

        return changes;
    }

    @Override
    public Collection<String> setReturnValue(String inputId, Object value) {
        List<String> changes = new ArrayList<>();
        AbstractInput ai = getFormElement(inputId, AbstractInput.class);

        if (ai == null) {
            return changes;
        }

        ai.setReturnContent(value);
        changes.add(ai.getId());

        Collection<String> otherChanges = runEvents(ai);

        changes.addAll(otherChanges);

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
                case SUM_IVS_BY_CLASSES:
                    break;
            }

            if (operationShoultBeApplied) {
                applyValueOperation(otherAi);

                changes.add(otherAi.getId());
                changes.addAll(runEvents(otherAi));
            }
        }

        return changes;
    }

    @Override
    public Collection<String> setChecked(String inputOptionId, boolean checked) {

        if (inputOptionId.equals("inputOptionId")) {
            int x = 5;
        }

        Collection<String> changes = new ArrayList<>();

        InputOptionContent ioc = getFormElement(inputOptionId, InputOptionContent.class);

        if (ioc == null) {
            return changes;
        }

        ioc.setChecked(checked);

        changes.add(ioc.getId());
        changes.addAll(runEvents(ioc));

        if (!checked) {
            return changes;
        }

        ContentContainer parentContainer = getFormElement(ioc.getParentId(), ContentContainer.class);

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
                                iocOther.setChecked(false);
                                changes.add(ai.getId());
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

        return changes;
    }

    @Override
    public Collection<String> setEnabled(String elementId, boolean enabled) {
        Collection<String> changes = new ArrayList<>();

        AbstractFormElement afe = getElementById(elementId);

        if (afe == null) {
            return changes;
        }

        afe.setEnabled(enabled);

        changes.add(afe.getId());
        changes.addAll(runEvents(afe));

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
        return validateInput(getFormElement(inputTextId, AbstractInput.class));
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

        return _elements.get(id);
    }

    @Override
    public FormResult getFormResult() {
        FormResult formResult = new FormResult();
        formResult.setId(_questionForm.getId());
        formResult.setLang(_questionForm.getLanguage());
        formResult.setVersion(_questionForm.getVersion());

        for (Question question : _questionForm.getQuestions()) {

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
            if (!hasReturnableInputs(cc)) {
                continue;
            }

            urc.add(cc);
        }

        return urc;
    }

    @Override
    public List<AbstractInput> getReturnableInputs(String elementId) {
        AbstractFormElement afe = getElementById(elementId);

        return getReturnableInputs(afe);
    }

    @Override
    public ContentContainer getContentContainerById(String id) {
        return getFormElement(id, ContentContainer.class);
    }

    @Override
    public InputTextContent getInputTextById(String id) {
        return getFormElement(id, InputTextContent.class);
    }

    @Override
    public InputOptionContent getInputOptionById(String id) {
        return getFormElement(id, InputOptionContent.class);
    }

    @Override
    public SimpleAnswer getAnswerById(String id) {
        return getFormElement(id, SimpleAnswer.class);
    }

    @Override
    public Question getQuestionById(String id) {
        return getFormElement(id, Question.class);
    }

    @Override
    public Collection<AbstractInput> getInputContents() {
        return getFormElements(AbstractInput.class);
    }

    @Override
    public Collection<InputTextContent> getInputTextContents() {
        return getFormElements(InputTextContent.class);
    }

    @Override
    public Collection<InputOptionContent> getInputOptionContents() {
        return getFormElements(InputOptionContent.class);
    }

    @Override
    public Collection<ContentContainer> getContentContainers() {
        return getFormElements(ContentContainer.class);
    }

    @Override
    public Collection<Question> getQuestions() {
        return getFormElements(Question.class);
    }

    @Override
    public Collection<AbstractAnswer> getAnswers() {
        return getFormElements(AbstractAnswer.class);
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

        Collection<AbstractInput> allInputs = getFormElements(AbstractInput.class);

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

        Collection<AbstractInput> allInputs = getFormElements(AbstractInput.class);

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
    public <TElement extends AbstractFormElement> TElement getFormElement(String id,
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
    public <TElement extends AbstractFormElement> Collection<TElement> getFormElements(Class<TElement> elementClass) {
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

        List<AbstractCondition> validations = abstractInput.getValidations();

        if (validations == null || validations.isEmpty()) {
            return new InputValidationResult(abstractInput, true);
        }

        for (AbstractCondition ac : validations) {
            if (!isConditionSatisfiedWithValue(ac, abstractInput.getReturnContent())) {
                return new InputValidationResult(abstractInput, ac, false);
            }
        }

        return new InputValidationResult(abstractInput, true);
    }

    private boolean hasReturnableInputs(AbstractFormElement afe) {
        return !getReturnableInputs(afe).isEmpty();
    }

    private List<AbstractInput> getReturnableInputs(AbstractFormElement afe) {
        List<AbstractInput> inputs = new ArrayList<>();

        if (afe == null) {
            return inputs;
        }

        if (afe.isEnabled()) {
            switch (afe.getElementType()) {
                case FORM: {
                    QuestionForm qf = (QuestionForm) afe;
                    for (Question q : qf.getQuestions()) {
                        if (q == null || !q.isEnabled()) {
                            continue;
                        }

                        inputs.addAll(getReturnableInputs(q.getId()));
                    }
                }
                break;
                case QUESTION: {
                    Question q = (Question) afe;
                    for (AbstractAnswer aa : q.getAnswers()) {
                        if (aa == null || !aa.isEnabled()) {
                            continue;
                        }

                        inputs.addAll(getReturnableInputs(aa.getId()));
                    }
                }
                break;
                case ANSWER: {
                    AbstractAnswer aa = (AbstractAnswer) afe;
                    for (ContentContainer cc : aa.getAnswerContentContainers()) {
                        if (cc == null || !cc.isEnabled()) {
                            continue;
                        }

                        inputs.addAll(getReturnableInputs(cc.getId()));
                    }
                }
                break;
                case CONTENT_CONTAINER: {
                    ContentContainer cc = (ContentContainer) afe;

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
                    AbstractInput ai = (AbstractInput) afe;
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
            default:
                break;
        }

        return changes;
    }

    private Collection<String> runEvents(AbstractFormElement abstractFormElement) {

        Collection<String> changes = new ArrayList<>();

        if (abstractFormElement == null) {
            return changes;
        }

        List<AbstractFormEvent> events = abstractFormElement.getEvents();

        if (events != null && !events.isEmpty()) {
            for (AbstractFormEvent afe : events) {
                AbstractCondition ao = afe.getCondition();

                boolean conditionSatisfied = isConditionSatisfied(ao, abstractFormElement);

                if (!conditionSatisfied) {
                    continue;
                }

                Collection<AbstractProcedure> abstractProcedures = afe.getProcedures();
                Collection<String> otherChanges = new ArrayList<>();
                switch (afe.getEventType()) {
                    case ON_CHANGED: {
                        otherChanges = runProcedures(abstractProcedures);
                    }
                    break;
                    case ON_ENABLED: {
                        if (abstractFormElement.isEnabled()) {
                            otherChanges = runProcedures(abstractProcedures);
                        }
                    }
                    break;
                    case ON_DISABLED:
                        if (!abstractFormElement.isEnabled()) {
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
            case SUM_IVS_BY_CLASSES:
                break;
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

        abstractInput.setReturnContent(String.valueOf(runOperation(ao)));
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

    private Object getInputReturnValue(String inputId) {
        AbstractInput ai = getFormElement(inputId, AbstractInput.class);

        if (ai == null) {
            return null;
        }

        Object inputValue = ai.getReturnContent();

//        AbstractOperation valueOperation = ai.getValueOperation();
//
//        Object valOperationResult = runOperation(valueOperation);
//
//        if (valOperationResult != null) {
//            inputValue = valOperationResult;
//        }
        return inputValue;
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
            return isConditionSatisfiedWithValue(abstractCondition, abstractInput.getReturnContent());
        }

        return false;
    }

    private boolean isConditionSatisfiedWithValue(AbstractCondition abstractCondition, Object value) {

        if (abstractCondition == null) {
            return true;
        }

        if (value == null) {
            return true;
        }

        Double doubleValue = Double.valueOf(value.toString());

        switch (abstractCondition.getConditionType()) {
            case COMPARE_WITH_DV: {
                CompareWithDirectValueCondition cwdvc = (CompareWithDirectValueCondition) abstractCondition;
                Double valueToCompare = cwdvc.getCompareWith();
                return isComparisonSatisfied(doubleValue, valueToCompare, cwdvc.getComparisonType());
            }
            case COMPARE_WITH_IV: {
                CompareWithInputValueCondition cwivc = (CompareWithInputValueCondition) abstractCondition;
                String inputId = cwivc.getCompareWith();
                Object valueToCompare = getInputReturnValue(inputId);
                return isComparisonSatisfied(doubleValue, NumberUtil.getAsDouble(valueToCompare), cwivc.getComparisonType());
            }
            case IS_IN_RANGE_DVS: {
                IsInRangeOfDirectValuesCondition iirodvo = (IsInRangeOfDirectValuesCondition) abstractCondition;
                return doubleValue >= iirodvo.getMinValue() && doubleValue <= iirodvo.getMaxValue();
            }
            case IS_IN_RANGE_IVS: {
                IsInRangeOfInputValuesCondition iiroivo = (IsInRangeOfInputValuesCondition) abstractCondition;
                Object valueMinToCompare = getInputReturnValue(iiroivo.getMinValueInputId());
                Object valueMaxToCompare = getInputReturnValue(iiroivo.getMaxValueInputId());

                Double minValue = NumberUtil.getAsDouble(valueMinToCompare);
                Double maxValue = NumberUtil.getAsDouble(valueMaxToCompare);

                return NumberUtil.isInRange(doubleValue, minValue, maxValue);
            }
            default:
                return false;
        }
    }
}

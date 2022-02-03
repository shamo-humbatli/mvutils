package mobvey.parser;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import mobvey.common.CollectionUtil;
import mobvey.form.QuestionForm;
import mobvey.form.answer.SimpleAnswer;
import mobvey.form.answer.content.InputOptionContent;
import mobvey.form.answer.content.InputTextContent;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.base.AbstractAnswer;
import mobvey.form.base.AbstractInput;
import mobvey.form.enums.ColumnDefinitionType;
import mobvey.form.enums.ComparisonType;
import mobvey.form.enums.DisplayType;
import mobvey.form.enums.InputValueType;
import mobvey.form.enums.ResultType;
import mobvey.form.question.Question;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import mobvey.common.Strings;
import mobvey.condition.AbstractCondition;
import mobvey.condition.CompareWithDirectValueCondition;
import mobvey.condition.CompareWithInputValueCondition;
import mobvey.condition.ConditionType;
import mobvey.condition.IsInRangeOfDirectValuesCondition;
import mobvey.condition.IsInRangeOfInputValuesCondition;
import mobvey.form.enums.EventProcedureType;
import mobvey.form.enums.FormEventType;
import mobvey.form.events.AbstractFormEvent;
import mobvey.form.events.FormEvent;
import mobvey.operation.AbstractOperation;
import mobvey.operation.OperationType;
import mobvey.operation.SumInputValuesByIndexRangeOperation;
import mobvey.operation.SumInputValuesOperation;
import mobvey.procedure.AbstractProcedure;
import mobvey.procedure.DisableElementsProcedure;
import mobvey.procedure.EnableElementsProcedure;
import mobvey.procedure.SetReturnRequiredProcedure;
import org.w3c.dom.NamedNodeMap;

/**
 *
 * @author Shamo Humbatli
 */
public class FormParser {

    private final File workingDirectory;
    private final String encoding = "UTF-8";
    private final DocumentBuilder documentBuilder;
    private XPath xpath = null;
    private final XPathFactory xpathFactory;

    public FormParser(File workingDirectory) throws ParserConfigurationException {
        this.workingDirectory = workingDirectory;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        documentBuilder = factory.newDocumentBuilder();

        xpathFactory = XPathFactory.newInstance();
    }

    public QuestionForm ParseXml(String questionXmlFileName) {
        try {
            Document questionsXml = documentBuilder.parse(new FileInputStream(
                    new File(workingDirectory, questionXmlFileName)), encoding);

            questionsXml.getDocumentElement().normalize();

            QuestionForm qf = new QuestionForm();

            Element rootElement = questionsXml.getDocumentElement();
            NodeList nodeList = rootElement.getChildNodes();

            if (rootElement.hasAttributes()) {
                String formId = GetAttribute(rootElement, "id");
                String fversion = GetAttribute(rootElement, "version");
                String lang = GetAttribute(rootElement, "lang");

                if (Strings.isNothing(formId)) {
                    formId = Strings.GetRandomIdString();
                }
                qf.setId(formId.trim());

                if (Strings.hasContent(fversion)) {
                    qf.setVersion(fversion.trim());
                }

                if (Strings.hasContent(lang)) {
                    qf.setLanguage(lang.trim());
                }
            }

            if (nodeList == null || nodeList.getLength() == 0) {
                return qf;
            }

            for (int elmIndex = 0; elmIndex < nodeList.getLength(); elmIndex++) {

                Node formElm = nodeList.item(elmIndex);

                if (formElm.getNodeName().equals("description")) {
                    qf.setDescription(formElm.getTextContent());
                }

                if (formElm.getNodeName().equals("title")) {
                    qf.setTitle(formElm.getTextContent());
                }

                if (!formElm.getNodeName().equals("questions")) {
                    continue;
                }

                NodeList questions = formElm.getChildNodes();

                int questionItemIndexCounter = 1;
                for (int questionIndex = 0; questionIndex < questions.getLength(); questionIndex++) {
                    Node questionNode = questions.item(questionIndex);

                    if (questionNode == null || !questionNode.getNodeName().equals("question")) {
                        continue;
                    }

                    NodeList questionNodeList = questionNode.getChildNodes();

                    String questionId = GetAttribute(questionNode, "id");
                    String isEnabledString = GetAttribute(questionNode, "enabled");
                    String itemIndex = GetAttribute(questionNode, "itemIndex");
                    String isForOperatorString = GetAttribute(questionNode, "isForOperator");
                    String itemIndexingString = GetAttribute(questionNode, "itemIndexing");

                    if (itemIndex == null) {
                        itemIndex = String.valueOf(questionItemIndexCounter);
                        questionItemIndexCounter++;
                    } else if (itemIndex.toLowerCase().equals("empty")) {
                        itemIndex = "";
                    }

                    Question question = new Question();
                    question.setId(questionId);
                    question.setEnabled(true);
                    question.setItemIndex(itemIndex);
                    qf.AddQuestion(question);

                    if (Strings.hasContent(isForOperatorString)) {
                        question.setIsForOperator(Boolean.valueOf(isForOperatorString));
                    }

                    if (Strings.hasContent(itemIndexingString)) {
                        question.setItemIndexing(Boolean.valueOf(itemIndexingString));
                    }

                    if (Strings.hasContent(isEnabledString)) {
                        question.setEnabled(Boolean.valueOf(isEnabledString));
                    }

                    for (int qc = 0; qc < questionNodeList.getLength(); qc++) {
                        Node questionElement = questionNodeList.item(qc);

                        if (questionElement.getNodeName().equals("questionText")) {
                            question.setQuestionText(questionElement.getTextContent());
                        } else if (questionElement.getNodeName().equals("explanation")) {
                            question.setExplanation(questionElement.getTextContent());
                        } else if (questionElement.getNodeName().equals("answer")) {
                            AbstractAnswer answer = BuildAnswer(questionElement);
                            question.AddAnswer(answer);
                        }
                    }
                }
            }

            return qf;

        } catch (Exception exp) {
            LogException(exp);
        }
        return null;
    }

    private AbstractAnswer BuildAnswer(Node answerElement) {

        try {
            String answerId = GetAttribute(answerElement, "id");
            String answerEnabled = GetAttribute(answerElement, "enabled");

            SimpleAnswer simpleAnswer = new SimpleAnswer();

            if (answerId == null) {
                answerId = Strings.GetRandomIdString();
            }

            simpleAnswer.setId(answerId);

            simpleAnswer.setEnabled(true);

            if (answerEnabled != null) {
                simpleAnswer.setEnabled(Boolean.valueOf(answerEnabled));
            }

            NodeList answerContentContainers = answerElement.getChildNodes();

            for (int acc = 0; acc < answerContentContainers.getLength(); acc++) {
                Node accNode = answerContentContainers.item(acc);

                if (!accNode.getNodeName().equals("contentContainer")) {
                    continue;
                }

                ContentContainer cc = BuildContentContainer(accNode);

                if (cc == null) {
                    continue;
                }

                FinalizeOperationDefinition(cc);

                simpleAnswer.AddContentContainer(cc);
            }
            return simpleAnswer;

        } catch (Exception exp) {
            LogException(exp);
        }

        return null;
    }

    private ContentContainer BuildContentContainer(Node contentContinerNode) {
        if (contentContinerNode == null) {
            return null;
        }

        String id = GetAttribute(contentContinerNode, "id");
        String displayType = GetAttribute(contentContinerNode, "displayType");
        String resultType = GetAttribute(contentContinerNode, "resultType");
        String displayText = GetAttribute(contentContinerNode, "displayText");
        String source = GetAttribute(contentContinerNode, "source");
        String returnContentCommon = GetAttribute(contentContinerNode, "return");

        String columnDef = GetAttribute(contentContinerNode, "columnDef");
        String columnDefType = GetAttribute(contentContinerNode, "columnDefType");

        String returnRequiredString = GetAttribute(contentContinerNode, "returnRequired");

        String defaultQuestionsToEnable = GetAttribute(contentContinerNode, "enableQuestions");
        String defaultQuestionsToDisable = GetAttribute(contentContinerNode, "disableQuestions");

        String defaultElementsToEnable = GetAttribute(contentContinerNode, "enableElements");
        String defaultElementsToDisable = GetAttribute(contentContinerNode, "disableElements");

        String iig = GetAttribute(contentContinerNode, "itemIndexGeneration");
        String rg = GetAttribute(contentContinerNode, "returnGeneration");

        String isEnabledStr = GetAttribute(contentContinerNode, "enabled");

        if (id == null) {
            id = UUID.randomUUID().toString();
        }

        ContentContainer container = new ContentContainer();
        container.setDisplayType(DisplayType.BLOCK);
        container.setDisplayText(displayText);
        container.setResultType(ResultType.SINGLE);
        container.setId(id);

        if (displayType != null) {
            container.setDisplayType(DisplayType.valueOf(displayType.toUpperCase()));
        }

        if (resultType != null) {
            container.setResultType(ResultType.valueOf(resultType.toUpperCase()));
        }

        if (returnRequiredString != null) {
            container.setReturnRequeired(Boolean.valueOf(returnRequiredString));
        }

        if (Strings.hasContent(isEnabledStr)) {
            container.setEnabled(Boolean.valueOf(isEnabledStr));
        }

        if (source != null) {
            List<? extends AbstractInput> optionInputs = BuildInputOptions(contentContinerNode);
            container.AddContentInputsRange(optionInputs);

            RunGenAutoItemIndexing(container, iig);
            RunGenAutoReturn(container, rg);
            return container;
        }

        NodeList contentInputs = contentContinerNode.getChildNodes();

        if (contentInputs == null) {
            return container;
        }

        for (int ii = 0; ii < contentInputs.getLength(); ii++) {
            Node inputNode = contentInputs.item(ii);

            if (!inputNode.getNodeName().equals("input")) {
                continue;
            }

            AbstractInput input = BuildInput(inputNode);

            if (input == null) {
                continue;
            }

            if (input.getReturnContent() == null) {
                input.setReturnContent(returnContentCommon);
            }

            if (Strings.hasContent(columnDef) && input.isColumnDefinitionDeclaredByDefault()) {
                input.setColumnDefinition(columnDef);

                if (Strings.hasContent(columnDefType) && columnDefType.toLowerCase().equals("cn")) {
                    input.setColumnDefinitionType(ColumnDefinitionType.CN);
                }
            }

            if (defaultQuestionsToEnable != null
                    && !input.hasAtLeastOneEventByProcedureBy(EventProcedureType.ENABLE_ELEMENTS)) {

                String eventStr = String.format("on:changed; do:enable_elements(%s)", defaultQuestionsToEnable);
                List<AbstractFormEvent> events = BuildEvents(eventStr);
                input.addEvents(events);
            }

            if (defaultQuestionsToDisable != null
                    && !input.hasAtLeastOneEventByProcedureBy(EventProcedureType.DISABLE_ELEMENTS)) {

                String eventStr = String.format("on:changed; do:disable_elements(%s)", defaultQuestionsToDisable);
                List<AbstractFormEvent> events = BuildEvents(eventStr);
                input.addEvents(events);
            }

            if (defaultElementsToEnable != null
                    && !input.hasAtLeastOneEventByProcedureBy(EventProcedureType.ENABLE_ELEMENTS)) {

                String eventStr = String.format("on:changed; do:disable_elements(%s)", defaultElementsToEnable);
                List<AbstractFormEvent> events = BuildEvents(eventStr);
                input.addEvents(events);
            }

            if (defaultElementsToDisable != null
                    && !input.hasAtLeastOneEventByProcedureBy(EventProcedureType.DISABLE_ELEMENTS)) {

                String eventStr = String.format("on:changed; do:disable_elements(%s)", defaultElementsToDisable);
                List<AbstractFormEvent> events = BuildEvents(eventStr);
                input.addEvents(events);
            }

            container.AddContentInput(input);
        }

        RunGenAutoItemIndexing(container, iig);
        RunGenAutoReturn(container, rg);

        return container;
    }

    private void RunGenAutoItemIndexing(ContentContainer cc, String iigString) {
        try {

            if (cc == null || !cc.hasInputs()) {
                return;
            }

            if (!Strings.hasContent(iigString)) {
                return;
            }

            String[] components = iigString.split(";");

            String typeString = GetLinePropertyValue(iigString, "type");
            String startString = GetLinePropertyValue(iigString, "start");

            int start = 1;

            if (Strings.hasContent(startString)) {
                start = Integer.valueOf(startString);
            }

            for (AbstractInput ainput : cc.getContentInputs()) {
                if (Strings.hasContent(ainput.getContentItemIndex())) {
                    continue;
                }
                ainput.setContentItemIndex(String.valueOf(start));
                start++;
            }

        } catch (Exception exp) {
            LogException(exp);
        }
    }

    private void RunGenAutoReturn(ContentContainer cc, String rgString) {
        try {

            if (cc == null || !cc.hasInputs()) {
                return;
            }

            if (!Strings.hasContent(rgString)) {
                return;
            }

            String[] components = rgString.split(";");

            String typeString = GetLinePropertyValue(rgString, "type");
            String startString = GetLinePropertyValue(rgString, "start");

            int start = 0;

            if (Strings.hasContent(startString)) {
                start = Integer.valueOf(startString);
            }

            for (AbstractInput ainput : cc.getContentInputs()) {
                if (Strings.hasContent(ainput.getStringReturnContent())) {
                    continue;
                }
                ainput.setReturnContent(String.valueOf(start));
                start++;
            }

        } catch (Exception exp) {
            LogException(exp);
        }
    }

    private String GetLinePropertyValue(String lineString, String propertyName) {
        try {
            String[] components = lineString.split(";");

            for (String component : components) {
                if (!component.contains(propertyName)) {
                    continue;
                }

                String[] pair = component.split(":");

                if (!pair[0].contains(propertyName)) {
                    continue;
                }

                return pair[1].trim();
            }
        } catch (Exception exp) {

        }
        return null;
    }

    private AbstractInput BuildInput(Node inputElement) {

        try {
            String inputId = GetAttribute(inputElement, "id");
            String parentId = GetAttribute(inputElement, "parent");
            String contentType = GetAttribute(inputElement, "type");

            String columnDef = GetAttribute(inputElement, "columnDef");
            String columnDefType = GetAttribute(inputElement, "columnDefType");

            String itemIndex = GetAttribute(inputElement, "itemIndex");
            String returnContent = GetAttribute(inputElement, "return");
            String valueTypeString = GetAttribute(inputElement, "valueType");

            String enabledQuestions = GetAttribute(inputElement, "enableQuestions");
            String disabledQuestions = GetAttribute(inputElement, "disableQuestions");

            String enabledItems = GetAttribute(inputElement, "enableElements");
            String disabledItems = GetAttribute(inputElement, "disableElements");

            String requiredForReturn = GetAttribute(inputElement, "enableReturnRequired");
            String notRequiredForReturn = GetAttribute(inputElement, "disableReturnRequired");

            String isComplexString = GetAttribute(inputElement, "isComplex");
            String operationStr = GetAttribute(inputElement, "valueOperation");
            String eventsStr = GetAttribute(inputElement, "events");
            String validationsStr = GetAttribute(inputElement, "validations");

            String isEnabledStr = GetAttribute(inputElement, "enabled");

            InputValueType inputValueType = InputValueType.TEXT;

            if (Strings.isNothing(inputId)) {
                inputId = UUID.randomUUID().toString();
            }

            boolean isComplex = false;

            if (valueTypeString != null) {
                switch (valueTypeString) {
                    case "int": {
                        inputValueType = InputValueType.INT;
                        break;
                    }

                    case "double": {
                        inputValueType = InputValueType.DOUBLE;
                        break;
                    }
                }
            }

            AbstractInput abstractAnswerContent = null;
            boolean columnDefDeclaredByDefault = false;

            if (columnDef == null) {
                columnDef = "0";
                columnDefDeclaredByDefault = true;
            }

            if (isComplexString != null) {
                isComplex = Boolean.valueOf(isComplexString);
            }

            if (Strings.isNothing(contentType)) {
                contentType = "text";
            }

            switch (contentType) {
                case "option": {
                    InputOptionContent optionContent = new InputOptionContent();

                    String checkedStr = GetAttribute(inputElement, "checked");
                    if (Strings.hasContent(checkedStr)) {
                        optionContent.setChecked(Boolean.valueOf(checkedStr));
                    }

                    abstractAnswerContent = optionContent;
                    break;
                }

                case "text": {
                    InputTextContent textContent = new InputTextContent();
                    String placeHolder = GetAttribute(inputElement, "placeHolder");
                    String readonlyString = GetAttribute(inputElement, "readonly");
                    String minValStr = GetAttribute(inputElement, "minValue");
                    String maxValStr = GetAttribute(inputElement, "maxValue");

                    textContent.setPlaceHolder(placeHolder);
                    textContent.setReadonly(false);

                    if (Strings.hasContent(readonlyString)) {
                        textContent.setReadonly(Boolean.valueOf(readonlyString));
                    }

                    if (Strings.hasContent(minValStr) && Strings.hasContent(maxValStr)) {
                        String opStr = String.format("is_in_range_dvs(%s, %s)", minValStr, maxValStr);
                        AbstractCondition cond = BuildCondition(opStr);
                        textContent.AddValidation(cond);
                    } else if (Strings.hasContent(minValStr) && Strings.isNothing(maxValStr)) {
                        String opStr = String.format("compare_with_dv(%s, gte)", minValStr);
                        AbstractCondition cond = BuildCondition(opStr);
                        textContent.AddValidation(cond);
                    } else if (Strings.hasContent(maxValStr) && Strings.isNothing(minValStr)) {
                        String opStr = String.format("compare_with_dv(%s, lte)", maxValStr);
                        AbstractCondition cond = BuildCondition(opStr);
                        textContent.AddValidation(cond);
                    }

                    abstractAnswerContent = textContent;
                    break;
                }
            }

            abstractAnswerContent.setId(inputId);
            abstractAnswerContent.setParentId(parentId);
            abstractAnswerContent.setReturnContent(returnContent);
            abstractAnswerContent.setContentItemIndex(itemIndex);
            abstractAnswerContent.setInputValueType(inputValueType);

            abstractAnswerContent.setColumnDefinition(columnDef);
            abstractAnswerContent.setColumnDefinitionDeclaredByDefault(columnDefDeclaredByDefault);

            abstractAnswerContent.setEvents(BuildEvents(eventsStr));
            abstractAnswerContent.setValidations(BuildConditions(validationsStr));

            if (Strings.hasContent(columnDefType) && columnDefType.toLowerCase().equals("cn")) {
                abstractAnswerContent.setColumnDefinitionType(ColumnDefinitionType.CN);
            }

            if (Strings.hasContent(isEnabledStr)) {
                abstractAnswerContent.setEnabled(Boolean.valueOf(isEnabledStr));
            }

            if (Strings.hasContent(operationStr)) {

                AbstractOperation valueOperation = BuildOperation(operationStr);
                abstractAnswerContent.setValueOperation(valueOperation);
            }

            String displayContent = inputElement.getTextContent();

            if (isComplex) {
                abstractAnswerContent.setComplex(isComplex);
                NodeList children = inputElement.getChildNodes();

                if (children != null) {
                    for (int childIndex = 0; childIndex < children.getLength(); childIndex++) {
                        Node child = children.item(childIndex);

                        if (child.getNodeName().equals("inputText")) {
                            displayContent = child.getTextContent();
                        } else if (child.getNodeName().equals("contentContainer")) {
                            ContentContainer childCC = BuildContentContainer(child);

                            if (childCC == null) {
                                continue;
                            }
                            FinalizeOperationDefinition(childCC);
                            abstractAnswerContent.AddContentContainer(childCC);
                        }
                    }
                }
            }

            switch (inputValueType) {
                case TEXT: {
                    abstractAnswerContent.setDisplayContent(displayContent);
                    break;
                }
                case INT: {
                    if (Strings.hasContent(displayContent)) {
                        abstractAnswerContent.setDisplayContent(Integer.valueOf(displayContent));
                    }
                    break;
                }
                case DOUBLE: {
                    if (Strings.hasContent(displayContent)) {
                        abstractAnswerContent.setDisplayContent(Double.valueOf(displayContent));
                    }
                    break;
                }
            }

            if (enabledQuestions != null) {
                String eventStr = String.format("on:changed; do:enable_elements(%s)", enabledQuestions);
                List<AbstractFormEvent> events = BuildEvents(eventStr);
                abstractAnswerContent.addEvents(events);
            }

            if (disabledQuestions != null) {
                String eventStr = String.format("on:changed; do:disable_elements(%s)", disabledQuestions);
                List<AbstractFormEvent> events = BuildEvents(eventStr);
                abstractAnswerContent.addEvents(events);
            }

            if (Strings.hasContent(enabledItems)) {

                String eventStr = String.format("on:changed; do:enable_elements(%s)", enabledItems);
                List<AbstractFormEvent> events = BuildEvents(eventStr);
                abstractAnswerContent.addEvents(events);
            }

            if (Strings.hasContent(disabledItems)) {
                String eventStr = String.format("on:changed; do:disable_elements(%s)", disabledItems);
                List<AbstractFormEvent> events = BuildEvents(eventStr);
                abstractAnswerContent.addEvents(events);
            }

            if (Strings.hasContent(requiredForReturn)) {
                String eventStr = String.format("on:changed; do:set_return_required(true, [%s])", requiredForReturn);
                List<AbstractFormEvent> events = BuildEvents(eventStr);
                abstractAnswerContent.addEvents(events);
            }

            if (Strings.hasContent(notRequiredForReturn)) {
                String eventStr = String.format("on:changed; do:set_return_required(false, [%s])", notRequiredForReturn);
                List<AbstractFormEvent> events = BuildEvents(eventStr);
                abstractAnswerContent.addEvents(events);
            }

            return abstractAnswerContent;

        } catch (Exception exp) {
            LogException(exp);
        }
        return null;
    }

    private List<InputOptionContent> BuildInputOptions(Node containerNode) {
        try {
            List<InputOptionContent> options = new ArrayList<>();

            String sourceFileName = GetAttribute(containerNode, "source");
            String selectExp = GetAttribute(containerNode, "displayExpression");
            String returnExpression = GetAttribute(containerNode, "returnExpression");

            String columnDef = GetAttribute(containerNode, "columnDef");
            String columnDefType = GetAttribute(containerNode, "columnDefType");

            String returnContentCommon = GetAttribute(containerNode, "return");

            Document parsedDoc = documentBuilder.parse(new FileInputStream(
                    new File(workingDirectory, sourceFileName)), encoding);

            NodeList displayContents = (NodeList) EvaluateXPathExpression(parsedDoc, selectExp);
            NodeList returnContents = (NodeList) EvaluateXPathExpression(parsedDoc, returnExpression);

            for (int nodeIndx = 0; nodeIndx < displayContents.getLength(); nodeIndx++) {

                String returnContent = returnContents.item(nodeIndx).getTextContent();
                if (returnContent == null) {
                    returnContent = returnContentCommon;
                }

                String ph = displayContents.item(nodeIndx).getTextContent();

                InputOptionContent ioc = new InputOptionContent();

                if (Strings.hasContent(columnDef)) {
                    ioc.setColumnDefinition(columnDef);
                }

                if (Strings.hasContent(columnDefType) && columnDefType.toLowerCase().equals("cn")) {
                    ioc.setColumnDefinitionType(ColumnDefinitionType.CN);
                }

                ioc.setContentItemIndex(String.valueOf(nodeIndx + 1));
                ioc.setReturnContent(returnContent);
                ioc.setDisplayContent(ph);
                ioc.setId(UUID.randomUUID().toString());
                options.add(ioc);
            }

            return options;
        } catch (Exception exp) {
            LogException(exp);
        }
        return null;
    }

    //[{on:CHANGE; do:DISABLE_ELEMENTS(s23); if: COMPARE_WITH_DV(12, gt)},]
    private List<AbstractFormEvent> BuildEvents(String eventStr) {
        List<AbstractFormEvent> events = new ArrayList<>();
        try {
            if (Strings.isNullOrEmpty(eventStr)) {
                return null;
            }
            String[] eventLines = GetParamArrayItems(eventStr);

            for (String eventLine : eventLines) {
                String[] eventParams = GetParamObjectDeclarations(eventLine);

                String[] paramOn = GetParam("on", eventParams);
                String[] paramDo = GetParam("do", eventParams);
                String[] paramIf = GetParam("if", eventParams);

                if (paramOn == null || paramOn.length != 2) {
                    continue;
                }

                if (paramDo == null || paramDo.length != 2) {
                    continue;
                }

                String eventTypeStr = paramOn[0].trim().toLowerCase() + "_" + paramOn[1].trim().toLowerCase();
                FormEventType fet = FormEventType.valueOf(eventTypeStr.trim().toUpperCase());

                AbstractCondition ac = null;
                if (paramIf != null && paramIf.length == 2) {
                    ac = BuildCondition(paramIf[1]);
                }

                Collection<AbstractProcedure> aps = BuildProcedures(paramDo[1]);

                FormEvent formEvent = new FormEvent();
                formEvent.setEventType(fet);
                formEvent.setCondition(ac);
                formEvent.setProcedures(aps);

                events.add(formEvent);
            }
        } catch (Exception exp) {
            LogException(exp);
        }

        return events;
    }

    private List<AbstractProcedure> BuildProcedures(String proceduresStr) {
        List<AbstractProcedure> procedures = new ArrayList<>();
        if (Strings.isNullOrEmpty(proceduresStr)) {
            return procedures;
        }

        String[] procedureLines = GetParamArrayItems(proceduresStr);

        for (String procedureLine : procedureLines) {
            AbstractProcedure ap = BuildProcedure(procedureLine);

            if (ap == null) {
                continue;
            }

            procedures.add(ap);
        }

        return procedures;
    }

    private AbstractProcedure BuildProcedure(String procedureStr) {
        AbstractProcedure abstractProcedure = null;
        try {
            if (Strings.isNullOrEmpty(procedureStr)) {
                return null;
            }

            procedureStr = procedureStr.trim();

            int p11 = procedureStr.indexOf("(");
            int p12 = procedureStr.indexOf(")");
            String procParamsStr = procedureStr.substring(p11 + 1, p12);
            String procName = procedureStr.substring(0, p11);

            EventProcedureType procType = EventProcedureType.valueOf(procName.toUpperCase());

            switch (procType) {
                case ENABLE_ELEMENTS: {
                    EnableElementsProcedure procedure = new EnableElementsProcedure();
                    procedure.setElementsIdsToEnable(Arrays.asList(procParamsStr.split(",")));
                    abstractProcedure = procedure;
                }
                break;
                case DISABLE_ELEMENTS: {
                    DisableElementsProcedure procedure = new DisableElementsProcedure();
                    procedure.setElementsIdsToDisable(Arrays.asList(procParamsStr.split(",")));
                    abstractProcedure = procedure;
                }
                break;
                case SET_RETURN_REQUIRED: {
                    SetReturnRequiredProcedure procedure = new SetReturnRequiredProcedure();

                    String[] paramsArr = procParamsStr.split(",");
                    procedure.setRequired(Boolean.valueOf(paramsArr[0].trim()));
                    procedure.setElements(Arrays.asList(GetParamArrayItems(paramsArr[1])));
                    abstractProcedure = procedure;
                }
                break;
                default:
                    throw new AssertionError(procType.name());

            }

        } catch (Exception exp) {
            LogException(exp);
        }

        return abstractProcedure;
    }

    public AbstractOperation BuildOperation(String operationStr) {
        try {

            if (Strings.isNullOrEmpty(operationStr)) {
                return null;
            }

            int p11 = operationStr.indexOf("(");
            int p12 = operationStr.indexOf(")");
            String contentRaw = operationStr.substring(p11 + 1, p12);
            String cmd = operationStr.substring(0, p11);

            OperationType opType = OperationType.valueOf(cmd.toUpperCase());

            AbstractOperation abstractOperation = null;

            switch (opType) {
                case SUM_IVS: {
                    String[] params = contentRaw.split(",");
                    SumInputValuesOperation operation = new SumInputValuesOperation();
                    operation.setRangeIds(Arrays.asList(params));

                    abstractOperation = operation;
                }
                break;
                case SUM_IVS_BY_INDEX_RANGE: {
                    String[] params = contentRaw.split(",");
                    SumInputValuesByIndexRangeOperation operation = new SumInputValuesByIndexRangeOperation();
                    operation.setIndexRangeStart(Integer.valueOf(params[0].trim()));
                    operation.setIndexRangeEnd(Integer.valueOf(params[1].trim()));

                    abstractOperation = operation;
                }
                break;
                case SUM_IVS_BY_CLASSES:
                    break;
                default:
                    break;

            }

            return abstractOperation;

        } catch (Exception exp) {
            LogException(exp);
        }

        return null;
    }

    private List<AbstractCondition> BuildConditions(String conditionsStr) {
        List<AbstractCondition> conditions = new ArrayList<>();
        if (Strings.isNullOrEmpty(conditionsStr)) {
            return conditions;
        }

        String[] procedureLines = GetParamArrayItems(conditionsStr);

        for (String conditionLine : procedureLines) {
            AbstractCondition condition = BuildCondition(conditionLine);

            if (condition == null) {
                continue;
            }

            conditions.add(condition);
        }

        return conditions;
    }

    public AbstractCondition BuildCondition(String conditionStr) {
        try {

            if (Strings.isNullOrEmpty(conditionStr)) {
                return null;
            }

            int p11 = conditionStr.indexOf("(");
            int p12 = conditionStr.indexOf(")");
            String contentRaw = conditionStr.substring(p11 + 1, p12);
            String cmd = conditionStr.substring(0, p11);

            ConditionType opType = ConditionType.valueOf(cmd.toUpperCase());

            AbstractCondition abstractCondition = null;

            switch (opType) {
                case COMPARE_WITH_DV: {
                    String[] params = contentRaw.split(",");

                    CompareWithDirectValueCondition cvdv = new CompareWithDirectValueCondition();
                    cvdv.setCompareWith(Double.valueOf(params[0].trim()));
                    cvdv.setComparisonType(ComparisonType.valueOf(params[1].trim().toUpperCase()));
                    abstractCondition = cvdv;
                }
                break;
                case COMPARE_WITH_IV: {
                    String[] params = contentRaw.split(",");

                    CompareWithInputValueCondition condition = new CompareWithInputValueCondition();
                    condition.setCompareWith(params[0].trim());
                    condition.setComparisonType(ComparisonType.valueOf(params[1].trim().toUpperCase()));
                    abstractCondition = condition;
                }
                break;
                case IS_IN_RANGE_DVS: {
                    String[] params = contentRaw.split(",");

                    IsInRangeOfDirectValuesCondition condition = new IsInRangeOfDirectValuesCondition();
                    condition.setMinValue(Double.valueOf(params[0].trim()));
                    condition.setMaxValue(Double.valueOf(params[1].trim()));
                    abstractCondition = condition;
                }
                break;
                case IS_IN_RANGE_IVS: {
                    String[] params = contentRaw.split(",");

                    IsInRangeOfInputValuesCondition condition = new IsInRangeOfInputValuesCondition();
                    condition.setMinValueInputId(params[0].trim());
                    condition.setMaxValueInputId(params[1].trim());
                    abstractCondition = condition;
                }
                break;
                default:
                    return null;

            }

            return abstractCondition;

        } catch (Exception exp) {
            LogException(exp);
        }

        return null;
    }

    public void FinalizeOperationDefinition(ContentContainer cc) {
        if (cc == null) {
            return;
        }

        List<AbstractInput> inputs = cc.getContentInputs();

        if (inputs == null) {
            return;
        }

        for (AbstractInput ai : cc.getContentInputs()) {
            if (ai == null) {
                continue;
            }

            AbstractOperation ao = ai.getValueOperation();
            
            if (ao == null) {
                continue;
            }

            if (!ao.getOperationType().equals(OperationType.SUM_IVS_BY_INDEX_RANGE)) {
                continue;
            }

            SumInputValuesByIndexRangeOperation indexRangeOp = (SumInputValuesByIndexRangeOperation) ao;

            SumInputValuesOperation sumInputValsOp = new SumInputValuesOperation();

            for (int aii = 0; aii < inputs.size(); aii++) {
                if (aii < indexRangeOp.getIndexRangeStart() || aii > indexRangeOp.getIndexRangeEnd()) {
                    continue;
                }

                sumInputValsOp.addRangeId(inputs.get(aii).getId());
            }
            
            ai.setValueOperation(sumInputValsOp);
        }
    }

    private String[] GetParamObjectDeclarations(String objectStr) {

        if (Strings.isNullOrEmpty(objectStr)) {
            return new String[0];
        }

        objectStr = objectStr.trim();
        int vl = objectStr.length();

        if (objectStr.indexOf("{") == 0 && objectStr.lastIndexOf("}") == vl - 1) {
            objectStr = objectStr.substring(1, vl - 1);
        }

        return SplitByDepth(objectStr, ';', 0);
    }

    private String[] SplitByDepth(String strValue, char splitBy, int depth) {

        if (Strings.isNothing(strValue)) {
            return new String[0];
        }

        strValue = strValue.trim();

        int[] commaIndexes = GetSymbolIndexesInDepth(strValue, splitBy, depth);

        int vl = strValue.length();

        String[] arrItems;
        if (commaIndexes.length == 0) {
            arrItems = new String[]{
                strValue
            };
        } else {
            arrItems = new String[commaIndexes.length + 1];

            int ssStart = 0;
            int ssEnd = 0;

            int currentArrItemIndex = 0;

            while (currentArrItemIndex < arrItems.length) {
                if (currentArrItemIndex <= commaIndexes.length - 1) {
                    ssEnd = commaIndexes[currentArrItemIndex];
                } else {
                    ssEnd = vl;
                }

                String ss = strValue.substring(ssStart, ssEnd);
                ss = ss.trim();

                arrItems[currentArrItemIndex] = ss;
                currentArrItemIndex++;

                ssStart = ssEnd + 1;
            }
        }

        return arrItems;
    }

    private String[] GetParamArrayItems(String paramArr) {

        if (Strings.isNullOrEmpty(paramArr)) {
            return new String[0];
        }

        paramArr = paramArr.trim();
        int pl = paramArr.length();

        if (paramArr.indexOf("[") == 0 && paramArr.lastIndexOf("]") == pl - 1) {
            paramArr = paramArr.substring(1, pl - 1);
        }

        return SplitByDepth(paramArr, ',', 0);
    }

    private static String[] GetParam(String key, String[] paramsArr) {
        if (paramsArr == null || paramsArr.length == 0) {
            return null;
        }

        if (Strings.isNullOrEmpty(key)) {
            return null;
        }

        String keyLc = key.toLowerCase();

        for (String paramItem : paramsArr) {
            if (Strings.isNullOrEmpty(paramItem)) {
                continue;
            }

            String[] pkv = paramItem.split(":");

            if (pkv == null || pkv.length == 0) {
                continue;
            }

            String piKeyLc = pkv[0].trim().toLowerCase();

            if (!piKeyLc.equals(keyLc)) {
                continue;
            }

            return pkv;
        }

        return null;
    }

    private int[] GetSymbolIndexesInDepth(String value, char symbol, int depth) {
        List<Integer> indexes = new ArrayList<>();

        int currentDepth = 0;
        int vl = value.length();

        if (Strings.hasContent(value)) {

            Character cSbb = '[';
            Character cSbe = ']';

            Character cCbb = '{';
            Character cCbe = '}';

            Character cBb = '(';
            Character cBe = ')';

            for (int ci = 0; ci < vl; ci++) {
                Character cc = value.charAt(ci);

                if (cc.equals(cSbb) || cc.equals(cCbb) || cc.equals(cBb)) {
                    currentDepth++;
                } else if (cc.equals(cSbe) || cc.equals(cCbe) || cc.equals(cBe)) {
                    currentDepth--;
                }

                if (currentDepth == depth && cc.equals(symbol)) {
                    indexes.add(ci);
                }
            }
        }

        return CollectionUtil.toIntArray(indexes);
    }

    public String GetAttribute(Node node, String attrName) {

        if (!node.hasAttributes()) {
            return null;
        }

        NamedNodeMap attrs = node.getAttributes();

        for (int attrIndx = 0; attrIndx < attrs.getLength(); attrIndx++) {
            String nodeName = attrs.item(attrIndx).getNodeName();
            if (nodeName.equals(attrName)) {
                String attr = attrs.getNamedItem(attrName).getNodeValue().trim();

                if (Strings.isNothing(attr)) {
                    return null;
                }

                return attr.trim();
            }
        }

        return null;
    }

    public Object EvaluateXPathExpression(Document domDocument, String expression) throws XPathExpressionException {

        xpath = xpathFactory.newXPath();
        XPathExpression expr = xpath.compile(expression);
        Object result = expr.evaluate(domDocument, XPathConstants.NODESET);

        return result;
    }

    private static final Logger logger = Logger.getLogger(FormParser.class.getName());

    private void LogException(Exception exp) {
        logger.log(Level.SEVERE, exp.getMessage());
        logger.log(Level.SEVERE, exp.toString());
    }
}

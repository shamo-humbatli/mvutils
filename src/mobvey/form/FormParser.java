package mobvey.form;

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
import mobvey.form.elements.QuestionForm;
import mobvey.form.elements.SimpleAnswer;
import mobvey.form.elements.InputOptionContent;
import mobvey.form.elements.InputTextContent;
import mobvey.form.elements.ContentContainer;
import mobvey.form.elements.AbstractAnswer;
import mobvey.form.elements.AbstractInput;
import mobvey.form.enums.ColumnDefinitionType;
import mobvey.form.enums.ComparisonType;
import mobvey.form.enums.DisplayType;
import mobvey.form.enums.InputValueType;
import mobvey.form.enums.ResultType;
import mobvey.form.elements.Question;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import mobvey.common.Strings;
import mobvey.condition.AbstractCondition;
import mobvey.condition.CompareInputValueWithDirectValueCondition;
import mobvey.condition.CompareInputValueWithInputValueCondition;
import mobvey.condition.CompareWithDirectValueCondition;
import mobvey.condition.CompareWithInputValueCondition;
import mobvey.condition.ConditionType;
import mobvey.condition.HasContentCondition;
import mobvey.condition.HasContentOfInputsCondition;
import mobvey.condition.HasNoContentCondition;
import mobvey.condition.HasNoContentOfInputsCondition;
import mobvey.condition.IsInRangeOfDirectValuesCondition;
import mobvey.condition.IsInRangeOfInputValuesCondition;
import mobvey.form.elements.AbstractFormElement;
import mobvey.form.elements.FormBody;
import mobvey.form.elements.Section;
import mobvey.form.enums.EventProcedureType;
import mobvey.form.enums.FormEventType;
import mobvey.form.enums.InputType;
import mobvey.form.events.AbstractFormEvent;
import mobvey.form.events.FormEvent;
import mobvey.operation.AbstractOperation;
import mobvey.operation.OperationType;
import mobvey.operation.SumInputValuesByClassesOperation;
import mobvey.operation.SumInputValuesByIndexRangeOperation;
import mobvey.operation.SumInputValuesOperation;
import mobvey.procedure.AbstractProcedure;
import mobvey.procedure.DisableElementsProcedure;
import mobvey.procedure.EnableElementsByClassProcedure;
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

    public QuestionForm parseXml(String questionXmlFileName) {
        try {
            Document questionsXml = documentBuilder.parse(new FileInputStream(
                    new File(workingDirectory, questionXmlFileName)), encoding);

            questionsXml.getDocumentElement().normalize();

            QuestionForm qf = new QuestionForm();

            Element rootElement = questionsXml.getDocumentElement();
            NodeList nodeList = rootElement.getChildNodes();

            if (rootElement.hasAttributes()) {

                applyCommonAttributes(rootElement, qf);

                String fversion = getAttribute(rootElement, "version");
                String lang = getAttribute(rootElement, "lang");

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
                String nodeName = formElm.getNodeName();

                if (nodeName.equals("description")) {
                    qf.setDescription(formElm.getTextContent());
                    continue;
                }

                if (nodeName.equals("actualShortDescription")) {
                    qf.setActualShortDescription(formElm.getTextContent());
                    continue;
                }

                if (nodeName.equals("title")) {
                    qf.setTitle(formElm.getTextContent());
                    continue;
                }

                if (!nodeName.equals("body")) {
                    continue;
                }

                FormBody fb = buildFormBody(formElm);

                if (fb != null) {
                    qf.setBody(fb);
                }
            }

            return qf;

        } catch (Exception exp) {
            logException(exp);
        }
        return null;
    }

    private AbstractFormElement buildElement(Node elementNode) {
        switch (elementNode.getNodeName()) {
            case "section": {
                return buildSection(elementNode);
            }
            case "question": {
                return buildQuestion(elementNode);
            }
            case "contentContainer": {
                return buildContentContainer(elementNode);
            }
            case "body": {
                return buildFormBody(elementNode);
            }
            case "answer": {
                return buildAnswer(elementNode);
            }
            case "input": {
                return buildInput(elementNode);
            }
        }

        return null;
    }

    private FormBody buildFormBody(Node formBodyNode) {
        try {
            if (formBodyNode == null || !formBodyNode.getNodeName().equals("body")) {
                return null;
            }

            FormBody fb = new FormBody();
            applyCommonAttributes(formBodyNode, fb);

            NodeList childNodes = formBodyNode.getChildNodes();

            for (int ii = 0; ii < childNodes.getLength(); ii++) {
                Node childNode = childNodes.item(ii);

                AbstractFormElement afe = buildElement(childNode);

                fb.addChild(afe);
            }

            return fb;
        } catch (Exception exp) {
            logException(exp);
        }

        return null;
    }

    private Section buildSection(Node sectionNode) {
        try {
            if (sectionNode == null || !sectionNode.getNodeName().equals("section")) {
                return null;
            }

            Section section = new Section();
            applyCommonAttributes(sectionNode, section);

            String titleStr = getAttribute(sectionNode, "title");
            String descStr = getAttribute(sectionNode, "description");

            section.setTitle(titleStr);
            section.setDescription(descStr);

            NodeList childNodes = sectionNode.getChildNodes();

            for (int ii = 0; ii < childNodes.getLength(); ii++) {
                Node childNode = childNodes.item(ii);

                AbstractFormElement afe = buildElement(childNode);

                section.addChild(afe);
            }

            return section;

        } catch (Exception exp) {
            logException(exp);
        }
        return null;
    }

    private Question buildQuestion(Node questionNode) {
        try {

            if (questionNode == null || !questionNode.getNodeName().equals("question")) {
                return null;
            }

            NodeList questionNodeList = questionNode.getChildNodes();

            String itemIndex = getAttribute(questionNode, "itemIndex");
            String isForOperatorString = getAttribute(questionNode, "isForOperator");
            String itemIndexingString = getAttribute(questionNode, "itemIndexing");

            Question question = new Question();
            applyCommonAttributes(questionNode, question);

            question.setItemIndex(itemIndex);

            if (Strings.hasContent(isForOperatorString)) {
                question.setIsForOperator(Boolean.valueOf(isForOperatorString));
            }

            if (Strings.hasContent(itemIndexingString)) {
                question.setItemIndexing(Boolean.valueOf(itemIndexingString));
            }

            for (int qc = 0; qc < questionNodeList.getLength(); qc++) {
                Node questionElement = questionNodeList.item(qc);

                if (questionElement.getNodeName().equals("questionText")) {
                    question.setQuestionText(questionElement.getTextContent());
                } else if (questionElement.getNodeName().equals("explanation")) {
                    question.setExplanation(questionElement.getTextContent());
                } else if (questionElement.getNodeName().equals("answer")) {
                    AbstractAnswer answer = buildAnswer(questionElement);
                    question.AddAnswer(answer);
                }
            }

            return question;

        } catch (Exception exp) {
            logException(exp);
        }

        return null;
    }

    private AbstractAnswer buildAnswer(Node answerElement) {

        try {
            SimpleAnswer simpleAnswer = new SimpleAnswer();
            applyCommonAttributes(answerElement, simpleAnswer);

            NodeList answerContentContainers = answerElement.getChildNodes();

            for (int acc = 0; acc < answerContentContainers.getLength(); acc++) {
                Node accNode = answerContentContainers.item(acc);

                if (!accNode.getNodeName().equals("contentContainer")) {
                    continue;
                }

                ContentContainer cc = buildContentContainer(accNode);

                if (cc == null) {
                    continue;
                }

                finalizeOperationDefinition(cc);

                simpleAnswer.AddContentContainer(cc);
            }

            return simpleAnswer;

        } catch (Exception exp) {
            logException(exp);
        }

        return null;
    }

    private ContentContainer buildContentContainer(Node contentContinerNode) {
        if (contentContinerNode == null) {
            return null;
        }

        String displayType = getAttribute(contentContinerNode, "displayType");
        String resultType = getAttribute(contentContinerNode, "resultType");
        String displayText = getAttribute(contentContinerNode, "displayText");
        String source = getAttribute(contentContinerNode, "source");
        String returnContentCommon = getAttribute(contentContinerNode, "return");

        String columnDef = getAttribute(contentContinerNode, "columnDef");
        String columnDefType = getAttribute(contentContinerNode, "columnDefType");

        String returnRequiredString = getAttribute(contentContinerNode, "returnRequired");

        String defaultQuestionsToEnable = getAttribute(contentContinerNode, "enableQuestions");
        String defaultQuestionsToDisable = getAttribute(contentContinerNode, "disableQuestions");

        String defaultElementsToEnable = getAttribute(contentContinerNode, "enableElements");
        String defaultElementsToDisable = getAttribute(contentContinerNode, "disableElements");

        String inputValidations = getAttribute(contentContinerNode, "inputValidations");
        String inputEvents = getAttribute(contentContinerNode, "inputEvents");

        String iig = getAttribute(contentContinerNode, "itemIndexGeneration");
        String rg = getAttribute(contentContinerNode, "returnGeneration");

        ContentContainer container = new ContentContainer();
        applyCommonAttributes(contentContinerNode, container);

        container.setDisplayType(DisplayType.BLOCK);
        container.setDisplayText(displayText);
        container.setResultType(ResultType.SINGLE);

        if (displayType != null) {
            container.setDisplayType(DisplayType.valueOf(displayType.toUpperCase()));
        }

        if (resultType != null) {
            container.setResultType(ResultType.valueOf(resultType.toUpperCase()));
        }

        if (returnRequiredString != null) {
            container.setReturnRequeired(Boolean.valueOf(returnRequiredString));
        }

        if (source != null) {
            List<? extends AbstractInput> optionInputs = buildInputOptions(contentContinerNode);
            container.AddContentInputsRange(optionInputs);
            applyContaninerInputEvents(contentContinerNode, container);

            runGenAutoItemIndexing(container, iig);
            runGenAutoReturn(container, rg);

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

            boolean hasEnableElements = hasAttribute(inputNode, "enableElements");
            boolean hasDisableElements = hasAttribute(inputNode, "disableElements");

            AbstractInput input = buildInput(inputNode);

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
            String mappedEventType = input.getInputType().equals(InputType.TEXT) ? "changed" : "checked";
            if (!hasEnableElements && defaultQuestionsToEnable != null
                    && !input.hasAtLeastOneEventByProcedureBy(EventProcedureType.ENABLE_ELEMENTS)) {

                String eventStr = String.format("on:%s; do:enable_elements(%s); if: has_content", mappedEventType, defaultQuestionsToEnable);
                List<AbstractFormEvent> events = buildEvents(eventStr);
                input.addEvents(events);

                if (input.getInputType().equals(InputType.TEXT)) {
                    input.addEvents(buildEvents(String.format("on:%s; do:disable_elements(%s); if: has_no_content", mappedEventType, defaultQuestionsToEnable)));
                } else {
                    input.addEvents(buildEvents(String.format("on:unchecked; do:disable_elements(%s);", defaultQuestionsToEnable)));
                }
            }

            if (!hasDisableElements && defaultQuestionsToDisable != null
                    && !input.hasAtLeastOneEventByProcedureBy(EventProcedureType.DISABLE_ELEMENTS)) {

                String eventStr = String.format("on:%s; do:disable_elements(%s); if: has_content", mappedEventType, defaultQuestionsToDisable);
                List<AbstractFormEvent> events = buildEvents(eventStr);
                input.addEvents(events);

                if (input.getInputType().equals(InputType.TEXT)) {
                    input.addEvents(buildEvents(String.format("on:%s; do:enable_elements(%s); if: has_no_content", mappedEventType, defaultQuestionsToDisable)));
                } else {
                    input.addEvents(buildEvents(String.format("on:unchecked; do:enable_elements(%s);", defaultQuestionsToDisable)));
                }
            }

            if (!hasEnableElements && defaultElementsToEnable != null
                    && !input.hasAtLeastOneEventByProcedureBy(EventProcedureType.ENABLE_ELEMENTS)) {

                String eventStr = String.format("on:%s; do:enable_elements(%s); if: has_content", mappedEventType, defaultElementsToEnable);
                List<AbstractFormEvent> events = buildEvents(eventStr);
                input.addEvents(events);

                if (input.getInputType().equals(InputType.TEXT)) {
                    input.addEvents(buildEvents(String.format("on:%s; do:disable_elements(%s); if: has_no_content", mappedEventType, defaultElementsToEnable)));
                } else {
                    input.addEvents(buildEvents(String.format("on:unchecked; do:disable_elements(%s);", defaultElementsToEnable)));
                }
            }

            if (!hasDisableElements && defaultElementsToDisable != null
                    && !input.hasAtLeastOneEventByProcedureBy(EventProcedureType.DISABLE_ELEMENTS)) {

                String eventStr = String.format("on:%s; do:disable_elements(%s); if: has_content", mappedEventType, defaultElementsToDisable);
                List<AbstractFormEvent> events = buildEvents(eventStr);
                input.addEvents(events);

                if (input.getInputType().equals(InputType.TEXT)) {
                    input.addEvents(buildEvents(String.format("on:%s; do:enable_elements(%s); if: has_no_content", mappedEventType, defaultElementsToDisable)));
                } else {
                    input.addEvents(buildEvents(String.format("on:unchecked; do:enable_elements(%s);", defaultElementsToDisable)));
                }
            }

            if (Strings.hasContent(inputEvents) && input.getEvents().isEmpty()) {
                input.addEvents(buildEvents(inputEvents));
            }

            if (Strings.hasContent(inputValidations) && input.getValidations().isEmpty()) {
                input.addValidations(BuildConditions(inputValidations));
            }

            container.AddContentInput(input);
        }

        runGenAutoItemIndexing(container, iig);
        runGenAutoReturn(container, rg);

        return container;
    }

    private void applyContaninerInputEvents(Node contentContainerNode, ContentContainer contentContainer) {

        if (contentContainer == null) {
            return;
        }

        String defaultElementsToEnable = getAttribute(contentContainerNode, "enableElements");
        String defaultElementsToDisable = getAttribute(contentContainerNode, "disableElements");
        String inputEventsStr = getAttribute(contentContainerNode, "inputEvents");

        for (AbstractInput input : contentContainer.getContentInputs()) {

            if (input == null) {
                continue;
            }

            input.addEvents(buildEvents(inputEventsStr));

            String mappedEventType = input.getInputType().equals(InputType.TEXT) ? "changed" : "checked";

            if (defaultElementsToEnable != null
                    && !input.hasAtLeastOneEventByProcedureBy(EventProcedureType.ENABLE_ELEMENTS)) {

                String eventStr = String.format("on:%s; do:enable_elements(%s); if: has_content", mappedEventType, defaultElementsToEnable);
                List<AbstractFormEvent> events = buildEvents(eventStr);
                input.addEvents(events);

                if (input.getInputType().equals(InputType.TEXT)) {
                    input.addEvents(buildEvents(String.format("on:%s; do:disable_elements(%s); if: has_no_content", mappedEventType, defaultElementsToEnable)));
                } else {
                    input.addEvents(buildEvents(String.format("on:unchecked; do:disable_elements(%s);", defaultElementsToEnable)));
                }
            }

            if (defaultElementsToDisable != null
                    && !input.hasAtLeastOneEventByProcedureBy(EventProcedureType.DISABLE_ELEMENTS)) {

                String eventStr = String.format("on:%s; do:disable_elements(%s); if: has_content", mappedEventType, defaultElementsToDisable);
                List<AbstractFormEvent> events = buildEvents(eventStr);
                input.addEvents(events);

                if (input.getInputType().equals(InputType.TEXT)) {
                    input.addEvents(buildEvents(String.format("on:%s; do:enable_elements(%s); if: has_no_content", mappedEventType, defaultElementsToDisable)));
                } else {
                    input.addEvents(buildEvents(String.format("on:unchecked; do:enable_elements(%s);", defaultElementsToDisable)));
                }
            }
        }
    }

    private void runGenAutoItemIndexing(ContentContainer cc, String iigString) {
        try {

            if (cc == null || !cc.hasInputs()) {
                return;
            }

            if (!Strings.hasContent(iigString)) {
                return;
            }

            String[] components = iigString.split(";");

            String typeString = getLinePropertyValue(iigString, "type");
            String startString = getLinePropertyValue(iigString, "start");

            int start = 1;

            if (Strings.hasContent(startString)) {
                start = Integer.valueOf(startString);
            }

            for (AbstractInput ainput : cc.getContentInputs()) {
                if (ainput.getContentItemIndex() != null) {
                    continue;
                }

                ainput.setContentItemIndex(String.valueOf(start));
                start++;
            }

        } catch (Exception exp) {
            logException(exp);
        }
    }

    private void runGenAutoReturn(ContentContainer cc, String rgString) {
        try {

            if (cc == null || !cc.hasInputs()) {
                return;
            }

            if (!Strings.hasContent(rgString)) {
                return;
            }

            String[] components = rgString.split(";");

            String typeString = getLinePropertyValue(rgString, "type");
            String startString = getLinePropertyValue(rgString, "start");

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
            logException(exp);
        }
    }

    private String getLinePropertyValue(String lineString, String propertyName) {
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

    private AbstractInput buildInput(Node inputElement) {

        try {
            String contentType = getAttribute(inputElement, "type");
            String columnDef = getAttribute(inputElement, "columnDef");
            String columnDefType = getAttribute(inputElement, "columnDefType");
            String itemIndex = getAttribute(inputElement, "itemIndex");
            String returnContent = getAttribute(inputElement, "return");
            String valueTypeString = getAttribute(inputElement, "valueType");
            String enabledQuestions = getAttribute(inputElement, "enableQuestions");
            String disabledQuestions = getAttribute(inputElement, "disableQuestions");
            String enabledItems = getAttribute(inputElement, "enableElements");
            String disabledItems = getAttribute(inputElement, "disableElements");
            String requiredForReturn = getAttribute(inputElement, "enableReturnRequired");
            String notRequiredForReturn = getAttribute(inputElement, "disableReturnRequired");
            String isComplexString = getAttribute(inputElement, "isComplex");
            String operationStr = getAttribute(inputElement, "valueOperation");
            String validationsStr = getAttribute(inputElement, "validations");

            InputValueType inputValueType = InputValueType.TEXT;

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

                    String checkedStr = getAttribute(inputElement, "checked");
                    if (Strings.hasContent(checkedStr)) {
                        optionContent.setChecked(Boolean.valueOf(checkedStr));
                    }

                    abstractAnswerContent = optionContent;
                    break;
                }

                case "text": {
                    InputTextContent textContent = new InputTextContent();
                    String placeHolder = getAttribute(inputElement, "placeHolder");
                    String readonlyString = getAttribute(inputElement, "readonly");
                    String minValStr = getAttribute(inputElement, "minValue");
                    String maxValStr = getAttribute(inputElement, "maxValue");

                    textContent.setPlaceHolder(placeHolder);
                    textContent.setReadonly(false);

                    if (Strings.hasContent(readonlyString)) {
                        textContent.setReadonly(Boolean.valueOf(readonlyString));
                    }

                    if (Strings.hasContent(minValStr) && Strings.hasContent(maxValStr)) {
                        String opStr = String.format("is_in_range_dvs(%s, %s)", minValStr, maxValStr);
                        AbstractCondition cond = buildCondition(opStr);
                        textContent.addValidation(cond);
                    } else if (Strings.hasContent(minValStr) && Strings.isNothing(maxValStr)) {
                        String opStr = String.format("compare_with_dv(%s, gte)", minValStr);
                        AbstractCondition cond = buildCondition(opStr);
                        textContent.addValidation(cond);
                    } else if (Strings.hasContent(maxValStr) && Strings.isNothing(minValStr)) {
                        String opStr = String.format("compare_with_dv(%s, lte)", maxValStr);
                        AbstractCondition cond = buildCondition(opStr);
                        textContent.addValidation(cond);
                    }

                    abstractAnswerContent = textContent;
                    break;
                }
            }

            applyCommonAttributes(inputElement, abstractAnswerContent);

            abstractAnswerContent.setReturnContent(returnContent);
            abstractAnswerContent.setContentItemIndex(itemIndex);
            abstractAnswerContent.setInputValueType(inputValueType);

            abstractAnswerContent.setColumnDefinition(columnDef);
            abstractAnswerContent.setColumnDefinitionDeclaredByDefault(columnDefDeclaredByDefault);

            abstractAnswerContent.setValidations(BuildConditions(validationsStr));

            if (Strings.hasContent(columnDefType) && columnDefType.toLowerCase().equals("cn")) {
                abstractAnswerContent.setColumnDefinitionType(ColumnDefinitionType.CN);
            }

            if (Strings.hasContent(operationStr)) {

                AbstractOperation valueOperation = buildOperation(operationStr);
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
                            ContentContainer childCC = buildContentContainer(child);

                            if (childCC == null) {
                                continue;
                            }
                            finalizeOperationDefinition(childCC);
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

            String mappedEventType = abstractAnswerContent.getInputType().equals(InputType.TEXT) ? "changed" : "checked";
            if (Strings.hasContent(enabledQuestions)) {
                String eventStr = String.format("on:%s; do:enable_elements(%s); if: has_content", mappedEventType, enabledQuestions);
                List<AbstractFormEvent> events = buildEvents(eventStr);
                abstractAnswerContent.addEvents(events);

                if (abstractAnswerContent.getInputType().equals(InputType.TEXT)) {
                    abstractAnswerContent.addEvents(buildEvents(String.format("on:%s; do:disable_elements(%s); if: has_no_content", mappedEventType, enabledQuestions)));
                } else {
                    abstractAnswerContent.addEvents(buildEvents(String.format("on:unchecked; do:disable_elements(%s);", enabledQuestions)));
                }
            }

            if (Strings.hasContent(disabledQuestions)) {
                String eventStr = String.format("on:%s; do:disable_elements(%s); if: has_content", mappedEventType, disabledQuestions);
                List<AbstractFormEvent> events = buildEvents(eventStr);
                abstractAnswerContent.addEvents(events);

                if (abstractAnswerContent.getInputType().equals(InputType.TEXT)) {
                    abstractAnswerContent.addEvents(buildEvents(String.format("on:%s; do:enable_elements(%s); if: has_no_content", mappedEventType, disabledQuestions)));
                } else {
                    abstractAnswerContent.addEvents(buildEvents(String.format("on:unchecked; do:enable_elements(%s);", disabledQuestions)));
                }
            }

            if (Strings.hasContent(enabledItems)) {
                String eventStr = String.format("on:%s; do:enable_elements(%s); if: has_content", mappedEventType, enabledItems);
                List<AbstractFormEvent> events = buildEvents(eventStr);
                abstractAnswerContent.addEvents(events);

                if (abstractAnswerContent.getInputType().equals(InputType.TEXT)) {
                    abstractAnswerContent.addEvents(buildEvents(String.format("on:%s; do:disable_elements(%s); if: has_no_content", mappedEventType, enabledItems)));
                } else {
                    abstractAnswerContent.addEvents(buildEvents(String.format("on:unchecked; do:disable_elements(%s);", enabledItems)));
                }
            }

            if (Strings.hasContent(disabledItems)) {
                String eventStr = String.format("on:%s; do:disable_elements(%s); if: has_content", mappedEventType, disabledItems);
                List<AbstractFormEvent> events = buildEvents(eventStr);
                abstractAnswerContent.addEvents(events);

                if (abstractAnswerContent.getInputType().equals(InputType.TEXT)) {
                    abstractAnswerContent.addEvents(buildEvents(String.format("on:%s; do:enable_elements(%s); if: has_no_content", mappedEventType, disabledItems)));
                } else {
                    abstractAnswerContent.addEvents(buildEvents(String.format("on:unchecked; do:enable_elements(%s);", disabledItems)));
                }
            }

            if (Strings.hasContent(requiredForReturn)) {
                String eventStr = String.format("on:%s; do:set_return_required(true, [%s]); if: has_content", mappedEventType, requiredForReturn);
                List<AbstractFormEvent> events = buildEvents(eventStr);
                abstractAnswerContent.addEvents(events);

                if (abstractAnswerContent.getInputType().equals(InputType.TEXT)) {
                    abstractAnswerContent.addEvents(buildEvents(String.format("on:%s; do:set_return_required(false, [%s]); if: has_no_content", mappedEventType, requiredForReturn)));
                } else {
                    abstractAnswerContent.addEvents(buildEvents(String.format("on:unchecked; do:set_return_required(false, [%s]);", requiredForReturn)));
                }
            }

            if (Strings.hasContent(notRequiredForReturn)) {
                String eventStr = String.format("on:%s; do:set_return_required(false, [%s]); if: has_content", mappedEventType, notRequiredForReturn);
                List<AbstractFormEvent> events = buildEvents(eventStr);
                abstractAnswerContent.addEvents(events);

                if (abstractAnswerContent.getInputType().equals(InputType.TEXT)) {
                    abstractAnswerContent.addEvents(buildEvents(String.format("on:%s; do:set_return_required(true, [%s]); if: has_no_content", mappedEventType, notRequiredForReturn)));
                } else {
                    abstractAnswerContent.addEvents(buildEvents(String.format("on:unchecked; do:set_return_required(true, [%s]);", notRequiredForReturn)));
                }
            }

            return abstractAnswerContent;

        } catch (Exception exp) {
            logException(exp);
        }
        return null;
    }

    private List<InputOptionContent> buildInputOptions(Node containerNode) {
        try {
            List<InputOptionContent> options = new ArrayList<>();

            String sourceFileName = getAttribute(containerNode, "source");
            String selectExp = getAttribute(containerNode, "displayExpression");
            String returnExpression = getAttribute(containerNode, "returnExpression");

            String columnDef = getAttribute(containerNode, "columnDef");
            String columnDefType = getAttribute(containerNode, "columnDefType");

            String returnContentCommon = getAttribute(containerNode, "return");

            Document parsedDoc = documentBuilder.parse(new FileInputStream(
                    new File(workingDirectory, sourceFileName)), encoding);

            NodeList displayContents = (NodeList) evaluateXPathExpression(parsedDoc, selectExp);
            NodeList returnContents = (NodeList) evaluateXPathExpression(parsedDoc, returnExpression);

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
            logException(exp);
        }
        return null;
    }

    private void applyCommonAttributes(Node node, AbstractFormElement abstractFormElement) {
        try {
            if (abstractFormElement == null || node == null) {
                return;
            }

            String classStr = getAttribute(node, "class");
            String idStr = getAttribute(node, "id");
            String isEnabledStr = getAttribute(node, "enabled");
            String eventsStr = getAttribute(node, "events");

            if (Strings.isNullOrEmpty(idStr)) {
                idStr = Strings.GetRandomIdString();
            }

            abstractFormElement.setId(idStr);

            if (Strings.hasContent(isEnabledStr)) {
                abstractFormElement.setEnabled(Boolean.valueOf(isEnabledStr));
            }

            if (Strings.hasContent(classStr)) {
                abstractFormElement.addClassNames(Arrays.asList(classStr.split(",")));
            }

            abstractFormElement.setEvents(buildEvents(eventsStr));
        } catch (Exception exp) {
            //ignored
        }
    }

    //[{on:CHANGE; do:DISABLE_ELEMENTS(s23); if: COMPARE_WITH_DV(12, gt)},]
    private List<AbstractFormEvent> buildEvents(String eventStr) {
        List<AbstractFormEvent> events = new ArrayList<>();
        try {
            if (Strings.isNullOrEmpty(eventStr)) {
                return null;
            }
            String[] eventLines = getParamArrayItems(eventStr);

            for (String eventLine : eventLines) {
                String[] eventParams = getParamObjectDeclarations(eventLine);

                String[] paramOn = getParam("on", eventParams);
                String[] paramDo = getParam("do", eventParams);
                String[] paramIf = getParam("if", eventParams);

                if (paramDo == null || paramDo.length != 2) {
                    continue;
                }

                if (paramOn == null || paramOn.length != 2) {
                    paramOn = new String[2];
                    paramOn[0] = "on";
                    paramOn[1] = "changed";
                }

                String eventTypeStr = paramOn[0].trim().toLowerCase() + "_" + paramOn[1].trim().toLowerCase();
                FormEventType fet = FormEventType.valueOf(eventTypeStr.trim().toUpperCase());

                AbstractCondition ac = null;
                if (paramIf != null && paramIf.length == 2) {
                    ac = buildCondition(paramIf[1]);
                }

                Collection<AbstractProcedure> aps = buildProcedures(paramDo[1]);

                FormEvent formEvent = new FormEvent();
                formEvent.setEventType(fet);
                formEvent.setCondition(ac);
                formEvent.setProcedures(aps);

                events.add(formEvent);
            }
        } catch (Exception exp) {
            logException(exp);
        }

        return events;
    }

    private List<AbstractProcedure> buildProcedures(String proceduresStr) {
        List<AbstractProcedure> procedures = new ArrayList<>();
        if (Strings.isNullOrEmpty(proceduresStr)) {
            return procedures;
        }

        String[] procedureLines = getParamArrayItems(proceduresStr);

        for (String procedureLine : procedureLines) {
            AbstractProcedure ap = buildProcedure(procedureLine);

            if (ap == null) {
                continue;
            }

            procedures.add(ap);
        }

        return procedures;
    }

    private AbstractProcedure buildProcedure(String procedureStr) {
        AbstractProcedure abstractProcedure = null;
        try {
            if (Strings.isNullOrEmpty(procedureStr)) {
                return null;
            }

            procedureStr = procedureStr.trim();

            int p11 = procedureStr.indexOf("(");
            int p12 = procedureStr.indexOf(")");
            String procParamsStr = procedureStr.substring(p11 + 1, p12).trim();
            String procName = procedureStr.substring(0, p11).trim();

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
                    procedure.setElements(Arrays.asList(getParamArrayItems(paramsArr[1])));

                    abstractProcedure = procedure;
                }
                break;
                case ENABLE_ELEMENTS_BY_CLASS: {
                    EnableElementsByClassProcedure procedure = new EnableElementsByClassProcedure();

                    String[] paramsArr = procParamsStr.split(",");

                    procedure.setEnabled(Boolean.valueOf(paramsArr[0].trim()));
                    procedure.setElementsClasses(Arrays.asList(getParamArrayItems(paramsArr[1])));

                    abstractProcedure = procedure;
                }
                break;
                default:
                    throw new AssertionError(procType.name());

            }

        } catch (Exception exp) {
            logException(exp);
        }

        return abstractProcedure;
    }

    public AbstractOperation buildOperation(String operationStr) {
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
                case SUM_IVS_BY_CLASSES: {
                    String[] params = contentRaw.split(",");

                    SumInputValuesByClassesOperation operation = new SumInputValuesByClassesOperation();

                    operation.setInputClasses(Arrays.asList(params));

                    abstractOperation = operation;
                }
                break;
                default:
                    break;

            }

            return abstractOperation;

        } catch (Exception exp) {
            logException(exp);
        }

        return null;
    }

    private List<AbstractCondition> BuildConditions(String conditionsStr) {
        List<AbstractCondition> conditions = new ArrayList<>();
        if (Strings.isNullOrEmpty(conditionsStr)) {
            return conditions;
        }

        String[] procedureLines = getParamArrayItems(conditionsStr);

        for (String conditionLine : procedureLines) {
            AbstractCondition condition = buildCondition(conditionLine);

            if (condition == null) {
                continue;
            }

            conditions.add(condition);
        }

        return conditions;
    }

    public AbstractCondition buildCondition(String conditionStr) {
        try {

            if (Strings.isNullOrEmpty(conditionStr)) {
                return null;
            }

            int p11 = conditionStr.indexOf("(");
            int p12 = conditionStr.indexOf(")");
            String contentRaw = "";
            String cmd = "";

            if (p11 >= 0 && p12 >= 0) {
                contentRaw = conditionStr.substring(p11 + 1, p12).trim();
                cmd = conditionStr.substring(0, p11).trim();
            } else {
                cmd = conditionStr.trim();
            }

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
                case COMPARE_IV_DV: {
                    String[] params = contentRaw.split(",");

                    CompareInputValueWithDirectValueCondition condition = new CompareInputValueWithDirectValueCondition();
                    condition.setComparingInputId(params[0].trim());
                    condition.setCompareWith(Double.valueOf(params[1].trim()));
                    condition.setComparisonType(ComparisonType.valueOf(params[2].trim().toUpperCase()));
                    abstractCondition = condition;
                }
                break;
                case COMPARE_IV_IV: {
                    String[] params = contentRaw.split(",");

                    CompareInputValueWithInputValueCondition condition = new CompareInputValueWithInputValueCondition();
                    condition.setComparingInputId(params[0].trim());
                    condition.setCompareWithInputId(params[1].trim());
                    condition.setComparisonType(ComparisonType.valueOf(params[2].trim().toUpperCase()));
                    abstractCondition = condition;
                }
                break;
                case HAS_CONTENT: {
                    abstractCondition = new HasContentCondition();
                }
                break;
                case HAS_NO_CONTENT: {
                    abstractCondition = new HasNoContentCondition();
                }
                break;
                case HAS_CONTENT_IVS: {
                    String[] params = contentRaw.split(",");

                    HasContentOfInputsCondition condition = new HasContentOfInputsCondition();
                    condition.setInputIds(Arrays.asList(params));
                    abstractCondition = condition;
                }
                break;
                case HAS_NO_CONTENT_IVS: {
                    String[] params = contentRaw.split(",");

                    HasNoContentOfInputsCondition condition = new HasNoContentOfInputsCondition();
                    condition.setInputIds(Arrays.asList(params));
                    abstractCondition = condition;
                }
                break;
                default:
                    return null;

            }

            return abstractCondition;

        } catch (Exception exp) {
            logException(exp);
        }

        return null;
    }

    public void finalizeOperationDefinition(ContentContainer cc) {
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

    private String[] getParamObjectDeclarations(String objectStr) {

        if (Strings.isNullOrEmpty(objectStr)) {
            return new String[0];
        }

        objectStr = objectStr.trim();
        int vl = objectStr.length();

        if (objectStr.indexOf("{") == 0 && objectStr.lastIndexOf("}") == vl - 1) {
            objectStr = objectStr.substring(1, vl - 1);
        }

        return splitByDepth(objectStr, ';', 0);
    }

    private String[] splitByDepth(String strValue, char splitBy, int depth) {

        if (Strings.isNothing(strValue)) {
            return new String[0];
        }

        strValue = strValue.trim();

        int[] commaIndexes = getSymbolIndexesInDepth(strValue, splitBy, depth);

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

    private String[] getParamArrayItems(String paramArr) {

        if (Strings.isNullOrEmpty(paramArr)) {
            return new String[0];
        }

        paramArr = paramArr.trim();
        int pl = paramArr.length();

        if (paramArr.indexOf("[") == 0 && paramArr.lastIndexOf("]") == pl - 1) {
            paramArr = paramArr.substring(1, pl - 1);
        }

        return splitByDepth(paramArr, ',', 0);
    }

    private static String[] getParam(String key, String[] paramsArr) {
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

    private int[] getSymbolIndexesInDepth(String value, char symbol, int depth) {
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

    public String getAttribute(Node node, String attrName) {

        if (!node.hasAttributes()) {
            return null;
        }

        NamedNodeMap attrs = node.getAttributes();

        for (int attrIndx = 0; attrIndx < attrs.getLength(); attrIndx++) {
            String nodeName = attrs.item(attrIndx).getNodeName();
            if (nodeName.equals(attrName)) {
                String attr = attrs.getNamedItem(attrName).getNodeValue().trim();

                return attr;
            }
        }

        return null;
    }

    private boolean hasAttribute(Node node, String attrName) {
        return getAttribute(node, attrName) != null;
    }

    public Object evaluateXPathExpression(Document domDocument, String expression) throws XPathExpressionException {

        xpath = xpathFactory.newXPath();
        XPathExpression expr = xpath.compile(expression);
        Object result = expr.evaluate(domDocument, XPathConstants.NODESET);

        return result;
    }

    private static final Logger logger = Logger.getLogger(FormParser.class.getName());

    private void logException(Exception exp) {
        logger.log(Level.SEVERE, exp.getMessage());
        logger.log(Level.SEVERE, exp.toString());
    }
}

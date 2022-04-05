package mobvey.form;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
import mobvey.common.ConditionUtil;
import mobvey.common.ParamUtil;
import mobvey.form.elements.QuestionForm;
import mobvey.form.elements.SimpleAnswer;
import mobvey.form.elements.InputOptionContent;
import mobvey.form.elements.InputTextContent;
import mobvey.form.elements.ContentContainer;
import mobvey.form.elements.AbstractAnswer;
import mobvey.form.elements.AbstractInput;
import mobvey.form.enums.ColumnDefinitionType;
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
import mobvey.condition.ConditionGroup;
import mobvey.form.elements.AbstractFormElement;
import mobvey.form.elements.FormBody;
import mobvey.form.elements.Section;
import mobvey.form.enums.EventProcedureType;
import mobvey.form.enums.FormEventType;
import mobvey.form.enums.InputType;
import mobvey.form.events.AbstractFormEvent;
import mobvey.form.events.FormEvent;
import mobvey.operation.AbstractOperation;
import mobvey.operation.CheckConditionOperation;
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

    private int _currentElementIndex = 0;

    private String getNextElementId(String elementPrefix) {
        _currentElementIndex++;
        return String.format("%s_%s", elementPrefix, _currentElementIndex);
    }

    private String getNextElementId(AbstractFormElement abstractFormElement) {
        _currentElementIndex++;
        String elementPrefix = getElementPrefix(abstractFormElement);
        return String.format("%s_%s", elementPrefix, _currentElementIndex);
    }

    private String getNextElementId() {
        _currentElementIndex++;
        return String.format("felm_%s", _currentElementIndex);
    }

    private String getElementPrefix(AbstractFormElement abstractFormElement) {
        switch (abstractFormElement.getElementType()) {
            case FORM:
                return "fm";
            case FORM_BODY:
                return "fb";
            case SECTION:
                return "sn";
            case QUESTION:
                return "qn";
            case ANSWER:
                return "ar";
            case CONTENT_CONTAINER:
                return "cc";
            case INPUT_TEXT:
                return "it";
            case INPUT_OPTION:
                return "io";
            default:
                throw new AssertionError(abstractFormElement.getElementType().name());
        }
    }

    public QuestionForm parseXml(String questionXmlFileName) {
        try {

            _currentElementIndex = 0;

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

        if (elementNode == null) {
            return null;
        }

        switch (elementNode.getNodeName().trim()) {
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

                if (afe == null) {
                    continue;
                }

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

            section.setTitle(titleStr);

            NodeList childNodes = sectionNode.getChildNodes();

            for (int ii = 0; ii < childNodes.getLength(); ii++) {
                Node childNode = childNodes.item(ii);

                if (childNode.getNodeName().equals("description")) {
                    section.setDescription(childNode.getTextContent());
                    continue;
                }

                AbstractFormElement afe = buildElement(childNode);

                if (afe == null) {
                    continue;
                }

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

        try {
            String displayType = getAttribute(contentContinerNode, "displayType");
            String resultType = getAttribute(contentContinerNode, "resultType");
            String displayText = getAttribute(contentContinerNode, "displayText");
            String source = getAttribute(contentContinerNode, "source");
            String returnContentCommon = getAttribute(contentContinerNode, "return");

            String columnDef = getAttribute(contentContinerNode, "columnDef");
            String columnDefType = getAttribute(contentContinerNode, "columnDefType");

            String returnRequiredString = getAttribute(contentContinerNode, "returnRequired");

            String defaultElementsToEnable = getAttribute(contentContinerNode, "enableElements");
            String defaultElementsToDisable = getAttribute(contentContinerNode, "disableElements");

            String inputValidations = getAttribute(contentContinerNode, "inputValidations");
            String inputEvents = getAttribute(contentContinerNode, "inputEvents");

            String iig = getAttribute(contentContinerNode, "itemIndexGeneration");
            String rg = getAttribute(contentContinerNode, "returnGeneration");

            String addEnableReverseStr = getAttribute(contentContinerNode, "addEnableReverse");
            String addDisableReverseStr = getAttribute(contentContinerNode, "addDisableReverse");

            boolean addEnableReverse = true;
            boolean addDisableReverse = true;

            if (Strings.hasContent(addEnableReverseStr)) {
                addEnableReverse = Boolean.valueOf(addEnableReverseStr.toLowerCase());
            }

            if (Strings.hasContent(addDisableReverseStr)) {
                addDisableReverse = Boolean.valueOf(addDisableReverseStr.toLowerCase());
            }

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

                if (Strings.hasContent(columnDef) 
                        && Strings.isNullOrEmpty(input.getColumnDefinition())) {
                    input.setColumnDefinition(columnDef.trim());
                }

                if (Strings.hasContent(columnDefType) 
                        && input.getColumnDefinitionType() == ColumnDefinitionType.NS) {
                    input.setColumnDefinitionType(ColumnDefinitionType.valueOf(columnDefType.trim().toUpperCase()));
                }

                if (Strings.hasContent(inputEvents) && input.getEvents().isEmpty()) {
                    input.addEvents(buildEvents(inputEvents));
                }

                if (Strings.hasContent(inputValidations) && input.getValidations().isEmpty()) {
                    input.addValidations(buildConditions(inputValidations));
                }

                if (!hasEnableElements && defaultElementsToEnable != null && input.getEvents().isEmpty()) {

                    if (input.getInputType().equals(InputType.TEXT)) {
                        input.addEvents(buildEvents(String.format("on:changed; do:enable_elements(%s); if: has_content AND check_enabled(true)", defaultElementsToEnable)));
                        input.addEvents(buildEvents(String.format("on:enabled; do:enable_elements(%s); if: has_content", defaultElementsToEnable)));

                        if (addEnableReverse) {
                            input.addEvents(buildEvents(String.format("on:changed; do:disable_elements(%s); if: has_no_content AND check_enabled(true)", defaultElementsToEnable)));
                            input.addEvents(buildEvents(String.format("on:disabled; do:disable_elements(%s); if: has_content", defaultElementsToEnable)));
                        }
                    } else {
                        input.addEvents(buildEvents(String.format("on:checked; do:enable_elements(%s); if: check_enabled(true)", defaultElementsToEnable)));
                        input.addEvents(buildEvents(String.format("on:enabled; do:enable_elements(%s); if:  check_checked(true)", defaultElementsToEnable)));

                        if (addEnableReverse) {
                            input.addEvents(buildEvents(String.format("on:unchecked; do:disable_elements(%s); if: check_enabled(true)", defaultElementsToEnable)));
                            input.addEvents(buildEvents(String.format("on:disabled; do:disable_elements(%s); if:  check_checked(true)", defaultElementsToEnable)));
                        }
                    }
                }

                if (!hasDisableElements && defaultElementsToDisable != null && input.getEvents().isEmpty()) {

                    if (input.getInputType().equals(InputType.TEXT)) {
                        input.addEvents(buildEvents(String.format("on:changed; do:disable_elements(%s); if: has_content AND check_enabled(true)", defaultElementsToDisable)));
                        input.addEvents(buildEvents(String.format("on:enabled; do:disable_elements(%s); if: has_content", defaultElementsToDisable)));

                        if (addDisableReverse) {
                            input.addEvents(buildEvents(String.format("on:changed; do:enable_elements(%s); if: has_no_content AND check_enabled(true)", defaultElementsToDisable)));
                            input.addEvents(buildEvents(String.format("on:disabled; do:enable_elements(%s); if: has_content", defaultElementsToDisable)));
                        }
                    } else {
                        input.addEvents(buildEvents(String.format("on:checked; do:disable_elements(%s); if: check_enabled(true)", defaultElementsToDisable)));
                        input.addEvents(buildEvents(String.format("on:enabled; do:disable_elements(%s); if:  check_checked(true)", defaultElementsToDisable)));

                        if (addDisableReverse) {
                            input.addEvents(buildEvents(String.format("on:unchecked; do:enable_elements(%s); if: check_enabled(true)", defaultElementsToDisable)));
                            input.addEvents(buildEvents(String.format("on:disabled; do:enable_elements(%s); if:  check_checked(true)", defaultElementsToDisable)));
                        }
                    }
                }

                container.AddContentInput(input);
            }

            runGenAutoItemIndexing(container, iig);
            runGenAutoReturn(container, rg);

            return container;
        } catch (Exception exp) {
            logException(exp);
        }
        return null;
    }

    private void applyContaninerInputEvents(Node contentContainerNode, ContentContainer contentContainer) {

        if (contentContainer == null) {
            return;
        }

        String defaultElementsToEnable = getAttribute(contentContainerNode, "enableElements");
        String defaultElementsToDisable = getAttribute(contentContainerNode, "disableElements");
        String inputEventsStr = getAttribute(contentContainerNode, "inputEvents");

        String addEnableReverseStr = getAttribute(contentContainerNode, "addEnableReverse");
        String addDisableReverseStr = getAttribute(contentContainerNode, "addDisableReverse");

        boolean addEnableReverse = true;
        boolean addDisableReverse = true;

        if (Strings.hasContent(addEnableReverseStr)) {
            addEnableReverse = Boolean.valueOf(addEnableReverseStr.toLowerCase());
        }

        if (Strings.hasContent(addDisableReverseStr)) {
            addDisableReverse = Boolean.valueOf(addDisableReverseStr.toLowerCase());
        }

        for (AbstractInput input : contentContainer.getContentInputs()) {

            if (input == null) {
                continue;
            }

            input.addEvents(buildEvents(inputEventsStr));

            if (defaultElementsToEnable != null) {
                if (input.getInputType().equals(InputType.TEXT)) {
                    input.addEvents(buildEvents(String.format("on:changed; do:enable_elements(%s); if: has_content AND check_enabled(true)", defaultElementsToEnable)));
                    input.addEvents(buildEvents(String.format("on:enabled; do:enable_elements(%s); if: has_content", defaultElementsToEnable)));

                    if (addEnableReverse) {
                        input.addEvents(buildEvents(String.format("on:changed; do:disable_elements(%s); if: has_no_content AND check_enabled(true)", defaultElementsToEnable)));
                        input.addEvents(buildEvents(String.format("on:disabled; do:disable_elements(%s); if: has_content", defaultElementsToEnable)));
                    }
                } else {
                    input.addEvents(buildEvents(String.format("on:checked; do:enable_elements(%s); if: check_enabled(true)", defaultElementsToEnable)));
                    input.addEvents(buildEvents(String.format("on:enabled; do:enable_elements(%s); if: check_checked(true)", defaultElementsToEnable)));

                    if (addEnableReverse) {
                        input.addEvents(buildEvents(String.format("on:unchecked; do:disable_elements(%s); if: check_enabled(true)", defaultElementsToEnable)));
                        input.addEvents(buildEvents(String.format("on:disabled; do:disable_elements(%s); if: check_checked(true)", defaultElementsToEnable)));
                    }
                }
            }

            if (defaultElementsToDisable != null) {

                if (input.getInputType().equals(InputType.TEXT)) {
                    input.addEvents(buildEvents(String.format("on:changed; do:disable_elements(%s); if: has_content AND check_enabled(true)", defaultElementsToDisable)));
                    input.addEvents(buildEvents(String.format("on:enabled; do:disable_elements(%s); if: has_content", defaultElementsToDisable)));

                    if (addDisableReverse) {
                        input.addEvents(buildEvents(String.format("on:changed; do:enable_elements(%s); if: has_no_content AND check_enabled(true)", defaultElementsToDisable)));
                        input.addEvents(buildEvents(String.format("on:disabled; do:enable_elements(%s); if: has_content", defaultElementsToDisable)));
                    }
                } else {
                    input.addEvents(buildEvents(String.format("on:checked; do:disable_elements(%s); if: check_enabled(true)", defaultElementsToDisable)));
                    input.addEvents(buildEvents(String.format("on:enabled; do:disable_elements(%s); if:  check_checked(true)", defaultElementsToDisable)));

                    if (addDisableReverse) {
                        input.addEvents(buildEvents(String.format("on:unchecked; do:enable_elements(%s); if: check_enabled(true)", defaultElementsToDisable)));
                        input.addEvents(buildEvents(String.format("on:disabled; do:enable_elements(%s); if:  check_checked(true)", defaultElementsToDisable)));
                    }
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
            return ParamUtil.getLinePropertyValue(lineString, propertyName);
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
            String enabledItems = getAttribute(inputElement, "enableElements");
            String disabledItems = getAttribute(inputElement, "disableElements");
            String requiredForReturn = getAttribute(inputElement, "enableReturnRequired");
            String notRequiredForReturn = getAttribute(inputElement, "disableReturnRequired");
            String isComplexString = getAttribute(inputElement, "isComplex");
            String operationStr = getAttribute(inputElement, "valueOperation");
            String validationsStr = getAttribute(inputElement, "validations");

            String addEnableReverseStr = getAttribute(inputElement, "addEnableReverse");
            String addDisableReverseStr = getAttribute(inputElement, "addDisableReverse");
            
            String inputFormat = getAttribute(inputElement, "format");

            boolean addEnableReverse = true;
            boolean addDisableReverse = true;

            if (Strings.hasContent(addEnableReverseStr)) {
                addEnableReverse = Boolean.valueOf(addEnableReverseStr.toLowerCase());
            }

            if (Strings.hasContent(addDisableReverseStr)) {
                addDisableReverse = Boolean.valueOf(addDisableReverseStr.toLowerCase());
            }

            InputValueType inputValueType = InputValueType.TEXT;

            if (Strings.hasContent(valueTypeString)) {
                inputValueType = InputValueType.valueOf(valueTypeString.toUpperCase());
            }

            AbstractInput abstractInput = null;
            boolean isComplex = false;
            if (isComplexString != null) {
                isComplex = Boolean.valueOf(isComplexString);
            }

            if (Strings.isNothing(contentType)) {
                contentType = "text";
            }

            switch (contentType.toLowerCase()) {
                case "option": {
                    InputOptionContent optionContent = new InputOptionContent();

                    String checkedStr = getAttribute(inputElement, "checked");
                    if (Strings.hasContent(checkedStr)) {
                        optionContent.setChecked(Boolean.valueOf(checkedStr));
                    }

                    abstractInput = optionContent;
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
                    } else if (Strings.hasContent(minValStr)) {
                        String opStr = String.format("compare_with_dv(%s, gte)", minValStr);
                        AbstractCondition cond = buildCondition(opStr);
                        textContent.addValidation(cond);
                    } else if (Strings.hasContent(maxValStr)) {
                        String opStr = String.format("compare_with_dv(%s, lte)", maxValStr);
                        AbstractCondition cond = buildCondition(opStr);
                        textContent.addValidation(cond);
                    }

                    abstractInput = textContent;
                    break;
                }
            }

            applyCommonAttributes(inputElement, abstractInput);

            abstractInput.setReturnContent(returnContent);
            abstractInput.setContentItemIndex(itemIndex);
            abstractInput.setInputValueType(inputValueType);
            abstractInput.setFormat(inputFormat);
            abstractInput.setColumnDefinition(columnDef);
            abstractInput.addValidations(buildConditions(validationsStr));

            if (Strings.hasContent(columnDefType)) {
                abstractInput.setColumnDefinitionType(ColumnDefinitionType.valueOf(columnDefType.trim().toUpperCase()));
            }

            if (Strings.hasContent(operationStr)) {
                AbstractOperation valueOperation = buildOperation(operationStr);
                abstractInput.setValueOperation(valueOperation);
            }

            String displayContent = inputElement.getTextContent();

            if (isComplex) {
                abstractInput.setComplex(isComplex);
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
                            abstractInput.AddContentContainer(childCC);
                        }
                    }
                }
            }

            switch (inputValueType) {
                case TEXT: {
                    abstractInput.setDisplayContent(displayContent);
                    break;
                }
                case INT: {
                    if (Strings.hasContent(displayContent)) {
                        abstractInput.setDisplayContent(Integer.valueOf(displayContent));
                    }
                    break;
                }
                case DOUBLE: {
                    if (Strings.hasContent(displayContent)) {
                        abstractInput.setDisplayContent(Double.valueOf(displayContent));
                    }
                    break;
                }
            }

            if (Strings.hasContent(enabledItems)) {
                if (abstractInput.getInputType().equals(InputType.TEXT)) {
                    abstractInput.addEvents(buildEvents(String.format("on:changed; do:enable_elements(%s); if: has_content AND check_enabled(true)", enabledItems)));
                    abstractInput.addEvents(buildEvents(String.format("on:enabled; do:enable_elements(%s); if: has_content", enabledItems)));

                    if (addEnableReverse) {
                        abstractInput.addEvents(buildEvents(String.format("on:changed; do:disable_elements(%s); if: has_no_content AND check_enabled(true)", enabledItems)));
                        abstractInput.addEvents(buildEvents(String.format("on:disabled; do:disable_elements(%s); if: has_content", enabledItems)));
                    }
                } else {
                    abstractInput.addEvents(buildEvents(String.format("on:checked; do:enable_elements(%s); if: check_enabled(true)", enabledItems)));
                    abstractInput.addEvents(buildEvents(String.format("on:enabled; do:enable_elements(%s); if: check_checked(true)", enabledItems)));
                    if (addEnableReverse) {

                        abstractInput.addEvents(buildEvents(String.format("on:unchecked; do:disable_elements(%s); if: check_enabled(true)", enabledItems)));
                        abstractInput.addEvents(buildEvents(String.format("on:disabled; do:disable_elements(%s); if:  check_checked(true)", enabledItems)));
                    }
                }
            }

            if (Strings.hasContent(disabledItems)) {
                if (abstractInput.getInputType().equals(InputType.TEXT)) {
                    abstractInput.addEvents(buildEvents(String.format("on:changed; do:disable_elements(%s); if: has_content AND check_enabled(true)", disabledItems)));
                    abstractInput.addEvents(buildEvents(String.format("on:enabled; do:disable_elements(%s); if: has_content", disabledItems)));

                    if (addDisableReverse) {
                        abstractInput.addEvents(buildEvents(String.format("on:changed; do:enable_elements(%s); if: has_no_content AND check_enabled(true)", disabledItems)));
                        abstractInput.addEvents(buildEvents(String.format("on:disabled; do:enable_elements(%s); if: has_content", disabledItems)));
                    }
                } else {
                    abstractInput.addEvents(buildEvents(String.format("on:checked; do:disable_elements(%s); if: check_enabled(true)", disabledItems)));
                    abstractInput.addEvents(buildEvents(String.format("on:enabled; do:disable_elements(%s); if:  check_checked(true)", disabledItems)));

                    if (addDisableReverse) {
                        abstractInput.addEvents(buildEvents(String.format("on:unchecked; do:enable_elements(%s); if: check_enabled(true)", disabledItems)));
                        abstractInput.addEvents(buildEvents(String.format("on:disabled; do:enable_elements(%s); if:  check_checked(true)", disabledItems)));
                    }
                }
            }

            if (Strings.hasContent(requiredForReturn)) {
                if (abstractInput.getInputType().equals(InputType.TEXT)) {
                    abstractInput.addEvents(buildEvents(String.format("on:changed; do:set_return_required(true, [%s]); if: has_content AND check_enabled(true)", requiredForReturn)));
                    abstractInput.addEvents(buildEvents(String.format("on:changed; do:set_return_required(false, [%s]); if: has_no_content OR check_enabled(false)", requiredForReturn)));
                } else {
                    abstractInput.addEvents(buildEvents(String.format("on:checked; do:set_return_required(true, [%s]); if: check_enabled(true)", requiredForReturn)));
                    abstractInput.addEvents(buildEvents(String.format("on:changed; do:set_return_required(false, [%s]); if: check_checked(false) OR check_enabled(false)", requiredForReturn)));
                }
            }

            if (Strings.hasContent(notRequiredForReturn)) {
                if (abstractInput.getInputType().equals(InputType.TEXT)) {
                    abstractInput.addEvents(buildEvents(String.format("on:changed; do:set_return_required(false, [%s]); if: has_content AND check_enabled(true)", notRequiredForReturn)));
                    abstractInput.addEvents(buildEvents(String.format("on:changed; do:set_return_required(true, [%s]); if: has_no_content OR check_enabled(false)", notRequiredForReturn)));
                } else {
                    abstractInput.addEvents(buildEvents(String.format("on:checked; do:set_return_required(false, [%s]); if: check_enabled(true)", notRequiredForReturn)));
                    abstractInput.addEvents(buildEvents(String.format("on:changed; do:set_return_required(true, [%s]); if: check_checked(false) OR check_enabled(false)", notRequiredForReturn)));
                }
            }

            return abstractInput;

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

                if (Strings.hasContent(columnDefType)) {
                    ioc.setColumnDefinitionType(ColumnDefinitionType.valueOf(columnDefType.trim().toUpperCase()));
                }

                ioc.setContentItemIndex(String.valueOf(nodeIndx + 1));
                ioc.setReturnContent(returnContent);
                ioc.setDisplayContent(ph);
                ioc.setId(getNextElementId(ioc));
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
                idStr = getNextElementId(abstractFormElement);
            }

            abstractFormElement.setId(idStr);

            if (Strings.hasContent(isEnabledStr)) {
                abstractFormElement.setEnabled(Boolean.valueOf(isEnabledStr));
            }

            if (Strings.hasContent(classStr)) {
                abstractFormElement.addClassNames(Arrays.asList(ParamUtil.getParamArrayItems(classStr)));
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

                ConditionGroup cg = null;
                if (paramIf != null && paramIf.length == 2) {
                    cg = buildConditionGroup(paramIf[1]);
                }

                Collection<AbstractProcedure> aps = buildProcedures(paramDo[1]);

                FormEvent formEvent = new FormEvent();
                formEvent.setEventType(fet);
                formEvent.setConditionGroup(cg);
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
            int p12 = procedureStr.lastIndexOf(")");
            String procParamsStr = procedureStr.substring(p11 + 1, p12).trim();
            String procName = procedureStr.substring(0, p11).trim();

            EventProcedureType procType = EventProcedureType.valueOf(procName.toUpperCase());

            switch (procType) {
                case ENABLE_ELEMENTS: {
                    String[] params = ParamUtil.getParamArrayItems(procParamsStr);

                    EnableElementsProcedure procedure = new EnableElementsProcedure();
                    procedure.setElementsIdsToEnable(Arrays.asList(params));
                    abstractProcedure = procedure;
                }
                break;
                case DISABLE_ELEMENTS: {
                    String[] params = ParamUtil.getParamArrayItems(procParamsStr);

                    DisableElementsProcedure procedure = new DisableElementsProcedure();
                    procedure.setElementsIdsToDisable(Arrays.asList(params));
                    abstractProcedure = procedure;
                }
                break;
                case SET_RETURN_REQUIRED: {
                    String[] params = ParamUtil.getParamArrayItems(procParamsStr);

                    SetReturnRequiredProcedure procedure = new SetReturnRequiredProcedure();
                    procedure.setRequired(Boolean.valueOf(params[0].trim()));
                    procedure.setElements(Arrays.asList(getParamArrayItems(params[1])));

                    abstractProcedure = procedure;
                }
                break;
                case ENABLE_ELEMENTS_BY_CLASS: {
                    String[] params = ParamUtil.getParamArrayItems(procParamsStr);

                    EnableElementsByClassProcedure procedure = new EnableElementsByClassProcedure();
                    procedure.setEnabled(Boolean.valueOf(params[0].trim()));
                    procedure.setElementsClasses(Arrays.asList(getParamArrayItems(params[1])));

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
            int p12 = operationStr.lastIndexOf(")");
            String contentRaw = operationStr.substring(p11 + 1, p12);
            String cmd = operationStr.substring(0, p11);

            OperationType opType = OperationType.valueOf(cmd.toUpperCase());

            AbstractOperation abstractOperation = null;

            switch (opType) {
                case SUM_IVS: {
                    String[] params = ParamUtil.getParamArrayItems(contentRaw);

                    SumInputValuesOperation operation = new SumInputValuesOperation();
                    operation.setRangeIds(Arrays.asList(params));

                    abstractOperation = operation;
                }
                break;
                case SUM_IVS_BY_INDEX_RANGE: {
                    String[] params = ParamUtil.getParamArrayItems(contentRaw);

                    SumInputValuesByIndexRangeOperation operation = new SumInputValuesByIndexRangeOperation();
                    operation.setIndexRangeStart(Integer.valueOf(params[0].trim()));
                    operation.setIndexRangeEnd(Integer.valueOf(params[1].trim()));

                    abstractOperation = operation;
                }
                break;
                case SUM_IVS_BY_CLASSES: {
                    String[] params = ParamUtil.getParamArrayItems(contentRaw);

                    SumInputValuesByClassesOperation operation = new SumInputValuesByClassesOperation();
                    operation.setInputClasses(Arrays.asList(params));

                    abstractOperation = operation;
                }
                break;
                case CHECK_CONDITION:
                    CheckConditionOperation operation = new CheckConditionOperation();
                    ConditionGroup cg = buildConditionGroup(contentRaw);
                    operation.setConditionGroup(cg);

                    abstractOperation = operation;
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

    private Collection<AbstractCondition> buildConditions(String conditionsStr) {
        return ConditionUtil.buildConditions(conditionsStr);
    }

    public AbstractCondition buildCondition(String conditionStr) {
        try {
            return ConditionUtil.buildCondition(conditionStr);

        } catch (Exception exp) {
            logException(exp);
        }

        return null;
    }

    public ConditionGroup buildConditionGroup(String conditionChoiceStr) {
        try {

            return ConditionUtil.buildConditionGroup(conditionChoiceStr);

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
        return ParamUtil.getParamObjectDeclarations(objectStr);
    }

    private String[] splitByDepth(String strValue,
            char splitBy, int depth,
            String depthIncreaseSymbols,
            String depthDecreaseSymbols) {

        return ParamUtil.splitByDepth(strValue, splitBy, depth, depthIncreaseSymbols, depthDecreaseSymbols);
    }

    private String[] getParamArrayItems(String paramArr) {

        return ParamUtil.getParamArrayItems(paramArr);
    }

    private static String[] getParam(String key, String[] paramsArr) {
        return ParamUtil.getParam(key, paramsArr);
    }

    private int[] getSymbolIndexesInDepth(String value,
            char symbol,
            int depth,
            String depthIncreaseSymbols,
            String depthDecreaseSymbols) {
        return ParamUtil.getSymbolIndexesInDepth(value, symbol, depth, depthIncreaseSymbols, depthDecreaseSymbols);
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

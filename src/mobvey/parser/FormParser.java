package mobvey.parser;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
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
import mobvey.form.QuestionForm;
import mobvey.form.answer.SimpleAnswer;
import mobvey.form.answer.content.InputOptionContent;
import mobvey.form.answer.content.InputTextContent;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.base.AbstractAnswer;
import mobvey.form.base.AbstractInput;
import mobvey.form.enums.DisplayTypes;
import mobvey.form.enums.InputValueTypes;
import mobvey.form.enums.ResultTypes;
import mobvey.form.question.Question;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

    private static final Logger logger = Logger.getLogger(FormParser.class.getName());

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

                if (formId != null) {
                    qf.setFormId(formId);
                }

                if (fversion != null) {
                    qf.setFormVersion(fversion);
                }

                if (lang != null) {
                    qf.setFormLanguage(lang);
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

                if (!formElm.getNodeName().equals("questions")) {
                    continue;
                }

                NodeList questions = formElm.getChildNodes();

                for (int questionIndex = 0; questionIndex < questions.getLength(); questionIndex++) {
                    Node questionNode = questions.item(questionIndex);

                    if (questionNode == null || !questionNode.getNodeName().equals("question")) {
                        continue;
                    }

                    NodeList questionNodeList = questionNode.getChildNodes();

                    String questionId = GetAttribute(questionNode, "id");
                    String isEnabledString = GetAttribute(questionNode, "enabled");

                    Question question = new Question();
                    question.setQuestionId(questionId);
                    question.setEnabled(true);
                    qf.AddQuestion(question);

                    if (isEnabledString != null) {
                        question.setEnabled(Boolean.valueOf(isEnabledString));
                    }

                    for (int qc = 0; qc < questionNodeList.getLength(); qc++) {
                        Node questionElement = questionNodeList.item(qc);

                        if (questionElement.getNodeName().equals("questionText")) {
                            question.setQuestionText(questionElement.getTextContent());
                        } else if (questionElement.getNodeName().equals("answer")) {
                            AbstractAnswer answer = BuildAnswer(questionElement);
                            question.AddAnswer(answer);
                        }
                    }
                }
            }

            return qf;

        } catch (Exception exp) {
            logger.log(Level.SEVERE, exp.toString());
        }
        return null;
    }

    private AbstractAnswer BuildAnswer(Node answerElement) {

        try {
            String answerId = GetAttribute(answerElement, "id");
            String answerEnabled = GetAttribute(answerElement, "enabled");
            String answerRequired = GetAttribute(answerElement, "required");

            SimpleAnswer simpleAnswer = new SimpleAnswer();

            simpleAnswer.setAnswerId(answerId);
            simpleAnswer.setEnabled(true);
            simpleAnswer.setRequeired(true);

            if (answerEnabled != null) {
                simpleAnswer.setEnabled(Boolean.valueOf(answerEnabled));
            }

            if (answerRequired != null) {
                simpleAnswer.setRequeired(Boolean.valueOf(answerRequired));
            }

            NodeList answerContentContainers = answerElement.getChildNodes();

            for (int acc = 0; acc < answerContentContainers.getLength(); acc++) {
                Node accNode = answerContentContainers.item(acc);

                if (!accNode.getNodeName().equals("contentContainer")) {
                    continue;
                }

                ContentContainer cc = BuildContentContainer(accNode);
                simpleAnswer.AddContentContainer(cc);
            }
            return simpleAnswer;

        } catch (Exception exp) {
            logger.log(Level.SEVERE, exp.toString());
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

        String defaultQuestionsToEnable = GetAttribute(contentContinerNode, "enableQuestion");
        String defaultQuestionsToDisable = GetAttribute(contentContinerNode, "disableQuestion");

        if (id == null) {
            id = UUID.randomUUID().toString();
        }

        ContentContainer container = new ContentContainer(DisplayTypes.BLOCK);
        container.setDisplayText(displayText);
        container.setResultType(ResultTypes.SINGLE);
        container.setId(id);

        if (displayType != null) {
            container.setDisplayType(DisplayTypes.valueOf(displayType.toUpperCase()));
        }

        if (resultType != null) {
            container.setResultType(ResultTypes.valueOf(resultType.toUpperCase()));
        }

        if (source != null) {
            List<? extends AbstractInput> optionInputs = BuildInputOptions(contentContinerNode);
            container.AddContentInputsRange(optionInputs);

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

            if (defaultQuestionsToEnable != null && input.getQuestionsToEnable() == null) {

                for (String eq : defaultQuestionsToEnable.split(",")) {
                    eq = eq.trim();

                    if ("".equals(eq)) {
                        continue;
                    }

                    input.AddQuestionToEnable(eq);
                }
            }

            if (defaultQuestionsToDisable != null && input.getQuestionsToDisable() == null) {

                for (String dq : defaultQuestionsToDisable.split(",")) {
                    dq = dq.trim();

                    if ("".equals(dq)) {
                        continue;
                    }

                    input.AddQuestionToDisable(dq);
                }
            }

            container.AddContentInput(input);
        }

        return container;
    }

    private AbstractInput BuildInput(Node inputElement) {

        try {
            String inputId = GetAttribute(inputElement, "id");
            String parentId = GetAttribute(inputElement, "parent");
            String contentType = GetAttribute(inputElement, "type");
            String columnIndex = GetAttribute(inputElement, "columnIndex");
            String itemIndex = GetAttribute(inputElement, "itemIndex");
            String returnContent = GetAttribute(inputElement, "return");
            String valueTypeString = GetAttribute(inputElement, "valueType");
            String contentOperation = GetAttribute(inputElement, "contentOperation");

            String enabledQuestions = GetAttribute(inputElement, "enableQuestion");
            String disabledQuestions = GetAttribute(inputElement, "disableQuestion");
            String isComplexString = GetAttribute(inputElement, "isComplex");

            InputValueTypes inputValueType = InputValueTypes.TEXT;

            if (inputId == null) {
                inputId = UUID.randomUUID().toString();
            }

            boolean isComplex = false;

            if (valueTypeString != null) {
                switch (valueTypeString) {
                    case "int": {
                        inputValueType = InputValueTypes.INT;
                        break;
                    }

                    case "double": {
                        inputValueType = InputValueTypes.DOUBLE;
                        break;
                    }
                }
            }

            AbstractInput abstractAnswerContent = null;

            if (columnIndex == null) {
                columnIndex = "0";
            }

            if (itemIndex == null) {
                itemIndex = columnIndex;
            }

            if (isComplexString != null) {
                isComplex = Boolean.valueOf(isComplexString);
            }

            switch (contentType) {
                case "option": {
                    InputOptionContent optionContent = new InputOptionContent();

                    abstractAnswerContent = optionContent;
                    break;
                }

                case "text": {
                    InputTextContent textContent = new InputTextContent();
                    String placeHolder = GetAttribute(inputElement, "placeHolder");
                    String readonlyString = GetAttribute(inputElement, "readonly");

                    textContent.setPlaceHolder(placeHolder);
                    textContent.setReadonly(false);

                    if (readonlyString != null) {
                        textContent.setReadonly(Boolean.valueOf(readonlyString));
                    }

                    abstractAnswerContent = textContent;
                    break;
                }
            }

            abstractAnswerContent.setId(inputId);
            abstractAnswerContent.setParentId(parentId);
            abstractAnswerContent.setReturnContent(returnContent);
            abstractAnswerContent.setContentColumnIndex(Integer.valueOf(columnIndex));
            abstractAnswerContent.setContentItemIndex(itemIndex);
            abstractAnswerContent.setOperation(contentOperation);
            abstractAnswerContent.setInputValueType(inputValueType);

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
                    abstractAnswerContent.setDisplayContent(Integer.valueOf(displayContent));
                    break;
                }
                case DOUBLE: {
                    abstractAnswerContent.setDisplayContent(Double.valueOf(displayContent));
                    break;
                }
            }

            if (enabledQuestions != null) {

                if (enabledQuestions.equals("skip")) {
                    abstractAnswerContent.AddQuestionToEnable("");
                } else {
                    for (String eq : enabledQuestions.split(",")) {

                        eq = eq.trim();

                        if ("".equals(eq)) {
                            continue;
                        }

                        abstractAnswerContent.AddQuestionToEnable(eq);
                    }
                }
            }

            if (disabledQuestions != null) {

                if (disabledQuestions.equals("skip")) {
                    abstractAnswerContent.AddQuestionToDisable("");
                } else {
                    for (String dq : disabledQuestions.split(",")) {
                        dq = dq.trim();

                        if ("".equals(dq)) {
                            continue;
                        }

                        abstractAnswerContent.AddQuestionToDisable(dq);
                    }
                }
            }

            return abstractAnswerContent;

        } catch (Exception exp) {
            logger.log(Level.SEVERE, exp.toString());
        }
        return null;
    }

    private List<InputOptionContent> BuildInputOptions(Node containerNode) {
        try {
            List<InputOptionContent> options = new ArrayList<>();

            String sourceFileName = GetAttribute(containerNode, "source");
            String selectExp = GetAttribute(containerNode, "displayExpression");
            String returnExpression = GetAttribute(containerNode, "returnExpression");
            String columnIndexString = GetAttribute(containerNode, "columnIndex");
            String returnContentCommon = GetAttribute(containerNode, "return");

            int columnIndex = 0;

            if (columnIndexString != null) {
                columnIndex = Integer.valueOf(columnIndexString);
            }

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

                ioc.setContentColumnIndex(columnIndex);
                ioc.setContentItemIndex(String.valueOf(nodeIndx + 1));
                ioc.setReturnContent(returnContent);
                ioc.setDisplayContent(ph);
                ioc.setId(UUID.randomUUID().toString());
                options.add(ioc);
            }

            return options;
        } catch (Exception exp) {
            logger.log(Level.SEVERE, exp.toString());
        }
        return null;
    }

    public String GetAttribute(Node node, String attrName) {

        if (!node.hasAttributes()) {
            return null;
        }

        for (int attrIndx = 0; attrIndx < node.getAttributes().getLength(); attrIndx++) {
            if (node.getAttributes().item(attrIndx).getNodeName().equals(attrName)) {
                String attr = node.getAttributes().getNamedItem(attrName).getNodeValue().trim();

                if ("".equals(attr)) {
                    return null;
                }

                return attr;
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
}

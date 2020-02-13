package mobvey.form.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import mobvey.common.Converters;
import mobvey.common.Strings;
import mobvey.form.result.FormResult;
import mobvey.form.result.InputResult;
import mobvey.form.result.QuestionResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Shamo Humbatli
 */
public class FormResultBuilder {

    private static final Logger logger = Logger.getLogger(FormResultBuilder.class.getName());

    private final DocumentBuilderFactory documentFactory;

    private final DocumentBuilder documentBuilder;

    private String encoding = "UTF-8";

    private String resultValueDelimeter = ",";
    private String resultValueCombiner = ":";

    private String resultTN = "result";
    private String questionTN = "question";
    private String idAttrTN = "id";
    private String versionAttrTN = "version";
    private String langAttrTN = "lang";

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public FormResultBuilder() throws ParserConfigurationException {
        this.documentFactory = DocumentBuilderFactory.newInstance();
        this.documentBuilder = documentFactory.newDocumentBuilder();
    }

    public Document GetFormResultXmlDocument(FormResult formResult) {

        try {
            if (formResult == null) {
                return null;
            }

            if (formResult.getId() == null || formResult.getVersion() == null) {
                return null;
            }

            Document rootDocument = documentBuilder.newDocument();

            Element rootElement = rootDocument.createElement(resultTN);

            rootElement.setAttribute(idAttrTN, formResult.getId());
            rootElement.setAttribute(versionAttrTN, formResult.getVersion());

            if (formResult.getLang() != null) {
                rootElement.setAttribute(langAttrTN, formResult.getLang());
            }

            rootDocument.appendChild(rootElement);

            List<QuestionResult> questionResults = formResult.getQuestionResults();

            if (questionResults == null || questionResults.isEmpty()) {
                return rootDocument;
            }

            for (QuestionResult qr : questionResults) {
                if (qr.getId() == null) {
                    continue;
                }

                List<InputResult> inputResults = qr.getInputResults();

                if (inputResults == null || inputResults.isEmpty()) {
                    continue;
                }

                Element questionElement = rootDocument.createElement(questionTN);

                questionElement.setAttribute(idAttrTN, qr.getId());
                rootElement.appendChild(questionElement);

                List<String> inputValues = new ArrayList<>();
                for (InputResult ir : inputResults) {

                    String returnVal = ir.getReturnValue();
                    
                    if(returnVal == null)
                        continue;
                    
                    returnVal = returnVal.trim();
                    
                    if("".equals(returnVal))
                        continue;
                    
                    if (ir.getColumnIndex() < 0) {
                        continue;
                    }

                    inputValues.add(ir.getColumnIndex() + resultValueCombiner + ir.getReturnValue());
                }
                questionElement.setTextContent(Strings.Join(resultValueDelimeter, inputValues));
            }
            
            rootDocument.normalize();
            return rootDocument;
        } catch (Exception exp) {
            logger.log(Level.SEVERE, exp.toString());
        }

        return null;
    }

    public String GetFormResultXmlString(FormResult formResult) {
        try {
            Document xmlDocument = GetFormResultXmlDocument(formResult);

            if (xmlDocument == null) {
                return null;
            }

            return Converters.XmlDocToString(xmlDocument, encoding);
        } catch (Exception exp) {
            logger.log(Level.SEVERE, exp.toString());
        }

        return null;
    }
}

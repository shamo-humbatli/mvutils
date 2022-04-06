package test;

import java.io.File;
import javax.xml.parsers.ParserConfigurationException;
import mobvey.form.elements.QuestionForm;
import mobvey.form.QuestionFormOperationContext;
import mobvey.form.elements.AbstractInput;
import mobvey.form.FormResultBuilder;
import mobvey.form.enums.ColumnDefinitionType;
import mobvey.form.result.FormResult;
import mobvey.form.result.InputResult;
import mobvey.form.result.QuestionResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import mobvey.common.CollectionUtil;
import mobvey.common.NumberUtil;
import mobvey.common.Strings;
import mobvey.condition.AbstractCondition;
import mobvey.condition.CompareWithDirectValueCondition;
import mobvey.condition.CompareWithInputValueCondition;
import mobvey.condition.IsInRangeOfDirectValuesCondition;
import mobvey.condition.IsInRangeOfInputValuesCondition;
import mobvey.form.elements.SimpleAnswer;
import mobvey.form.elements.InputOptionContent;
import mobvey.form.elements.InputTextContent;
import mobvey.form.elements.ContentContainer;
import mobvey.form.elements.AbstractAnswer;
import mobvey.form.elements.AbstractFormElement;
import mobvey.form.elements.AbstractInput;
import mobvey.form.enums.ComparisonType;
import mobvey.form.enums.FormElementType;
import mobvey.form.events.AbstractFormEvent;
import mobvey.form.IQuestionFormOperation;
import mobvey.form.elements.Question;
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
import mobvey.form.FormParser;

/**
 *
 * @author Shamo Humbatli
 */
public class QuestionFormTest_Special {

    public static void main(String[] args) throws ParserConfigurationException {

        String filePath = "C:\\Test";

        if (filePath == null) {
            return;
        }

        FormParser fp = new FormParser(new File(filePath));
        QuestionForm qf = fp.parseXml("questions_workforce.xml");

        QuestionFormOperationContext qfoc = new QuestionFormOperationContext(qf);

        Random random = new Random();
        for (AbstractInput abstractInput : qfoc.getInputTextContents()) {

            Object returnContent;

            switch (abstractInput.getInputValueType()) {
//                case INT:
//                    returnContent = (int) ((random.nextDouble() + 1) * 10);
//                    break;
                case TEXT:
                    returnContent = String.valueOf((random.nextDouble() + 1) * 10);
                    break;
                case INT:
                case DOUBLE:
                    returnContent = (random.nextDouble() + 1) * 10;
                    break;
                case DATE_TIME:
                case DATE:
                case TIME:
                    returnContent = new Date();
                    break;
                default:
                    throw new AssertionError(abstractInput.getInputValueType().name());
            }

            abstractInput.setReturnContent(returnContent);
        }

        for (AbstractFormElement afe : qfoc.getFormElements().values()) {
            afe.setEnabled(true);
        }

        printStr("-----------------------------------");

        FormResult fr = qfoc.getFormResult();
        FormResultBuilder frb = new FormResultBuilder();
        String xmlResult = frb.GetFormResultXmlString(fr);
        printStr("form result xml:");
        printStr(xmlResult);

        printStr("-----------------------------------");

    }

    static void printStr(String val) {
        System.out.println("->> " + val);
    }

    static void printChanges(String itemId, Collection<String> changes) {
        int orgc = changes.size();
        changes = CollectionUtil.toDistinct(changes);
        printStr(itemId + " item changes(" + changes.size() + "/" + orgc + "): " + Strings.join(", ", changes));
    }
}

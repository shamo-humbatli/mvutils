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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
public class QuestionFormTest4 {

    public static void main(String[] args) throws ParserConfigurationException {

        String filePath = "C:\\Test";

        if (filePath == null) {
            return;
        }

        FormParser fp = new FormParser(new File(filePath));
        QuestionForm qf = fp.parseXml("questions_test4.xml");

        QuestionFormOperationContext qfoc = new QuestionFormOperationContext(qf);

      Collection<String> changes;
        
        //complex event test
        printStr("----------------------");

        Question d05 = qfoc.getElement("d05", Question.class);
        Question d09 = qfoc.getElement("d09", Question.class);
        Question d13 = qfoc.getElement("d13", Question.class);

        changes = qfoc.setChecked("c22_i1", true);
        printStr("c22_i1 changes: " + Strings.join(", ", changes));

        changes = qfoc.setReturnValue("d04_i1", 40);
        printStr("d04_i1 changes: " + Strings.join(", ", changes));

        printStr("d05 should be enabled: " + d05.isEnabled());
        printStr("d09 should be disabled: " + !d09.isEnabled());
        printStr("d13 should be disabled: " + !d13.isEnabled());

        printStr("----------------------");
        changes = qfoc.setChecked("c22_i2", true);
        printStr("c22_i2 changes: " + Strings.join(", ", changes));

        changes = qfoc.setReturnValue("d04_i1", 39);
        printStr("d04_i1 changes: " + Strings.join(", ", changes));

        printStr("d05 should be disabled: " + !d05.isEnabled());
        printStr("d09 should be enabled: " + d09.isEnabled());
        printStr("d13 should be disabled: " + !d13.isEnabled());
        printStr("----------------------");
        changes = qfoc.setReturnValue("d04_i1", 40);
        printStr("d04_i1 changes: " + Strings.join(", ", changes));

        printStr("d05 should be disabled: " + !d05.isEnabled());
        printStr("d09 should be disabled: " + !d09.isEnabled());
        printStr("d13 should be enabled: " + d13.isEnabled());
        printStr("----------------------");

    }

    static void printStr(String val) {
        System.out.println("->> " + val);
    }
}

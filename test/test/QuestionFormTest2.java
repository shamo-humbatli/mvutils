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
import mobvey.form.operation.IQuestionFormOperation;
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
public class QuestionFormTest2 {

    public static void main(String[] args) throws ParserConfigurationException {

        String filePath = "C:\\Test";

        if (filePath == null) {
            return;
        }

        FormParser fp = new FormParser(new File(filePath));
        QuestionForm qf = fp.parseXml("questions_test1.xml");

        QuestionFormOperationContext qfoc = new QuestionFormOperationContext(qf);
        Question q4 = qfoc.getElement("q4", Question.class);

        printStr("q4 should be disabled: " + !q4.isEnabled());

        Collection<String> changes = qfoc.setChecked("q3_i4", true);
        printStr("changes: " + Strings.join(", ", changes));

        printStr("q4 should be enabled: " + q4.isEnabled());

        changes = qfoc.setChecked("q3_i3", true);
        printStr("changes: " + Strings.join(", ", changes));
        printStr("q4 should be disabled: " + !q4.isEnabled());

        ContentContainer cc = qfoc.getElement("q3_c1", ContentContainer.class);
        printStr("q3_c1 should be not required: " + !cc.isReturnRequeired());

        changes = qfoc.setReturnRequired("q3_c1", true);
        printStr("changes: " + Strings.join(", ", changes));

        printStr("q3_c1 should be required: " + cc.isReturnRequeired());
        printStr("----------------------");
        AbstractFormElement afe1 = qfoc.getElementById("q4_i1");
        AbstractFormElement afe2 = qfoc.getElementById("q5");

        printStr("q4_i1 should be disabled: " + !afe1.isEnabled());
        printStr("q5 should be disabled: " + !afe2.isEnabled());
        changes = qfoc.setEnabled("q4_i1", true);
        printStr("changes: " + Strings.join(", ", changes));
        printStr("q4_i1 should be enabled: " + afe1.isEnabled());
        printStr("q5 should be enabked: " + afe2.isEnabled());

        printStr("----------------------");

        changes = qfoc.setEnabled("q4_i1", false);
        printStr("changes: " + Strings.join(", ", changes));
        printStr("q4_i1 should be disabled: " + !afe1.isEnabled());
        printStr("q5 should be disabled: " + !afe2.isEnabled());

        // InputOptionContent ioc = qfoc.getElement("q3_i4", InputOptionContent.class);
    }

    static void printStr(String val) {
        System.out.println("->> " + val);
    }
}

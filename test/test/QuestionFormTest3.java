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
public class QuestionFormTest3 {
    
    public static void main(String[] args) throws ParserConfigurationException {
        
        String filePath = "C:\\Test";
        
        if (filePath == null) {
            return;
        }
        
        FormParser fp = new FormParser(new File(filePath));
        QuestionForm qf = fp.parseXml("questions_test3.xml");
        
        QuestionFormOperationContext qfoc = new QuestionFormOperationContext(qf);
        
        Question q4 = qfoc.getElement("q4", Question.class);
        Question q5 = qfoc.getElement("q5", Question.class);
        
        InputTextContent q4_i2 = qfoc.getElement("q4_i2", InputTextContent.class);
        q4_i2.setReturnContent("q4_i2");
        
        printStr("----------------------");
        
        Collection<String> changes = qfoc.setChecked("q3_i4", true);
        printStr("q3_i4 changes: " + Strings.join(", ", changes));
        
        printStr("q4 should be enabled: " + q4.isEnabled());
        //printStr("q5 should be enabled: " + q5.isEnabled());

        printStr("----------------------");
        
        changes = qfoc.setChecked("q3_i3", true);
        printStr("q3_i3 changes: " + Strings.join(", ", changes));
        printStr("----------------------");
        
        printStr("q4 should be disabled: " + !q4.isEnabled());
        printStr("q5 should be disabled: " + !q5.isEnabled());
        printStr("q4_i2 should have not null content: " + (q4_i2.getReturnContent() != null));

        //complex event test
        printStr("----------------------");
        changes = qfoc.setChecked("c05_i2", true);
        printStr("c05_i2 changes: " + Strings.join(", ", changes));
        
        printStr("c05_a1 shoulb be disabled: " + qfoc.isEnabled("c05_a1"));
        
        printStr("----------------------");
        
    }
    
    static void printStr(String val) {
        System.out.println("->> " + val);
    }
}

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
import mobvey.common.ConditionUtil;
import mobvey.common.NumberUtil;
import mobvey.common.Strings;
import mobvey.condition.AbstractCondition;
import mobvey.condition.CompareWithDirectValueCondition;
import mobvey.condition.CompareWithInputValueCondition;
import mobvey.condition.ConditionGroup;
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
public class ConditionTests {

    public static void main(String[] args) throws ParserConfigurationException {

        String condition = "check_checked(true) and has_content_ivs(x1,x2) or (check_enabled_elms(a1) and has_content)";

        ConditionGroup cg = ConditionUtil.buildConditionGroup(condition);

        String conditionObt = cg.toString();

        // boolean isCorr = conditionObt.equals(condition);
        printStr("Initial Cond Str: " + condition);
        printStr("Gen Cond Str: " + conditionObt);
        printStr("----------------------");

        condition = "check_checked(true) and has_content_ivs(x1,x2) or (check_enabled_elms(a1) and has_content) and ((compare_with_dv(45, EQ) and check_checked(true)) and (has_content_ivs([a1, a2]) or CHECK_ENABLED_ELMS(true, [x1, x2]))) or has_content";

        cg = ConditionUtil.buildConditionGroup(condition);

        conditionObt = cg.toString();

        // boolean isCorr = conditionObt.equals(condition);
        printStr("Initial Cond Str: " + condition);
        printStr("Gen Cond Str: " + conditionObt);

    }

    static void printStr(String val) {
        System.out.println("->> " + val);
    }
}

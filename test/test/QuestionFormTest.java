package test;

import java.io.File;
import javax.xml.parsers.ParserConfigurationException;
import mobvey.form.QuestionForm;
import mobvey.form.QuestionFormOperationContext;
import mobvey.form.base.AbstractInput;
import mobvey.form.builder.FormResultBuilder;
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
import mobvey.form.answer.SimpleAnswer;
import mobvey.form.answer.content.InputOptionContent;
import mobvey.form.answer.content.InputTextContent;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.base.AbstractAnswer;
import mobvey.form.base.AbstractFormElement;
import mobvey.form.base.AbstractInput;
import mobvey.form.enums.ComparisonType;
import mobvey.form.enums.FormElementType;
import mobvey.form.events.AbstractFormEvent;
import mobvey.form.operation.IQuestionFormOperation;
import mobvey.form.question.Question;
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
import mobvey.parser.FormParser;

/**
 *
 * @author Shamo Humbatli
 */
public class QuestionFormTest {

    public static void main(String[] args) throws ParserConfigurationException {

        String filePath = "C:\\Test";

        if (filePath == null) {
            return;
        }

        FormParser fp = new FormParser(new File(filePath));
        QuestionForm qf = fp.ParseXml("questions_workforce.xml");

        QuestionFormOperationContext qfoc = new QuestionFormOperationContext(qf);

        AbstractInput ai = qf.getQuestions().get(0)
                .getAnswers().get(0)
                .getAnswerContentContainers().get(0)
                .getContentInputs().get(0);

        Collection<String> changes = qfoc.setReturnValue(ai.getId(), "test1");
        printStr("changes: " + changes.size());

        boolean boolCheck = ai.getReturnContent().equals("test1");
        printStr("change success: " + boolCheck);

        int requiredCcCount = qfoc.getRequiredContainers().size();
        int questionsCount = qfoc.getElements(Question.class).size();
        int onlyInputsCount = qfoc.getElements(AbstractInput.class).size();
        int onlyInputTextCount = qfoc.getElements(InputTextContent.class).size();
        int onlyInputOptionCount = qfoc.getElements(InputOptionContent.class).size();
        int containerCount = qfoc.getElements(ContentContainer.class).size();

        printStr("requiredCcCount: " + requiredCcCount);
        printStr("questionsCount: " + questionsCount);
        printStr("onlyInputsCount: " + onlyInputsCount);
        printStr("onlyInputTextCount: " + onlyInputTextCount);
        printStr("onlyInputOptionCount: " + onlyInputOptionCount);
        printStr("containerCount: " + containerCount);

        Collection<AbstractInput> valueOpInputs = qfoc.getInputsHavingValueOperation();

        if (valueOpInputs.size() > 0) {
            AbstractInput voAi = (AbstractInput) valueOpInputs.toArray()[0];

            AbstractOperation ao = voAi.getValueOperation();
            int checkVal = 0;

            switch (ao.getOperationType()) {
                case SUM_IVS: {
                    SumInputValuesOperation operation = (SumInputValuesOperation) ao;

                    int valAssing = 0;

                    for (String id : operation.getRangeIds()) {
                        checkVal += valAssing;
                        printStr("val set changes: " + qfoc.setReturnValue(id, valAssing).size());

                        valAssing++;
                    }
                }
                break;
                case SUM_IVS_BY_INDEX_RANGE:
                    break;
                case SUM_IVS_BY_CLASSES:
                    break;
            }

            Double dblVal1 = Double.valueOf(checkVal);
            Object retCont = voAi.getReturnContent();
            boolCheck = retCont.equals(dblVal1);

            printStr("val op success: " + boolCheck);
        }

        for (AbstractFormElement afe : qfoc.getFormElements().values()) {
            Collection<AbstractFormEvent> events = afe.getEvents();
            if (events.isEmpty()) {
                continue;
            }

            for (AbstractFormEvent afev : events) {
                switch (afev.getEventType()) {
                    case ON_CHANGED: {
                        for (AbstractProcedure ap : afev.getProcedures()) {

                        }
                    }
                    break;
                    case ON_ENABLED:
                        break;
                    case ON_DISABLED:
                        break;
                    case ON_CHECKED:
                        break;
                    case ON_UNCHECKED:
                        break;
                }
            }
        }

        FormResult ft = qfoc.getFormResult();

        AbstractFormElement afeTestOption = qfoc.getElementById("test1");

        AbstractFormElement randomElement = null;
        Random random = new Random();
        for (AbstractInput abstractInput : qfoc.getInputTextContents()) {
            Double randomDbl = (random.nextDouble() + 1) * 10;
            qfoc.setReturnValue(abstractInput.getId(), randomDbl);

            if (randomElement == null) {
                randomElement = abstractInput;
            }
        }

        for (InputOptionContent ioc : qfoc.getInputOptionContents()) {

            if (ioc.getId().equals("test1")) {
                int x = 6;
            }

            Collection<String> chckChanges = qfoc.setChecked(ioc.getId(), true);
            int x = 6;
        }

        printStr("-----------------------------------");
        AbstractFormElement afeParentOfRandom = qfoc.getParentElementOfType(randomElement.getId(), Question.class);
        printStr("Found parent element: " + afeParentOfRandom.toString());
        printStr("-----------------------------------");

        Collection<InputValidationResult> validationResults = qfoc.validateInputs();

        printStr("Validated inputs count: " + validationResults.size());

        validationResults.forEach(x -> printStr("Validation result: " + x.toString()));

        ft = qfoc.getFormResult();
        printStr("-----------------------------------");
        printStr("Form result question count: " + ft.getQuestionResults().size());
        printStr(ft.toString());

        for (QuestionResult qr : ft.getQuestionResults()) {
            printStr("-----------------------------------");
            printStr(qr.toString());

            for (InputResult ir : qr.getInputResults()) {
                printStr(ir.toString());
            }
        }

        printStr("-----------------------------------");

        qfoc.setReturnValue("anket_i1", 1);
        qfoc.setReturnValue("resp_i1", 2);
        
        printStr("Actual Desc: " + qfoc.getActualShortDescription());

        printStr("-----------------------------------");
    }

    static void printStr(String val) {
        System.out.println("->> " + val);
    }
}

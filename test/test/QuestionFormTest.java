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
public class QuestionFormTest {

    public static void main(String[] args) throws ParserConfigurationException {

        String filePath = "C:\\Test";

        if (filePath == null) {
            return;
        }

        FormParser fp = new FormParser(new File(filePath));
        QuestionForm qf = fp.parseXml("questions_workforce.xml");

        QuestionFormOperationContext qfoc = new QuestionFormOperationContext(qf);

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
        
//        for(AbstractFormElement afe : qfoc.getFormElements().values())
//        {
//            afe.setEnabled(true);
//        }

//        printStr("-----------------------------------");
//        printStr("FORM EVENTS");
//
//        for (Map.Entry<String, Collection<AbstractFormEvent>> entry : qfoc.getEvents().entrySet()) {
//            for (AbstractFormEvent afev : entry.getValue()) {
//                if (afev.getConditionGroup() != null) {
//                    printStr(entry.getKey() + ": " + afev.getConditionGroup().toString());
//                } else {
//                    printStr(entry.getKey() + ": NO CONDITION");
//                }
//            }
//        }
//        printStr("-----------------------------------");
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
            boolean boolCheck = retCont.equals(dblVal1);

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

            if (abstractInput.getId().equals("a01")) {
                int x = 6;
            }
            String questionId = qfoc.getParentElementOfType(abstractInput.getId(), Question.class).getId();
            Object returnContent;

            switch (abstractInput.getInputValueType()) {
                case INT:
                    returnContent = (int) ((random.nextDouble() + 1) * 10);
                    break;
                case TEXT:
                case DOUBLE:
                    returnContent = String.valueOf((random.nextDouble() + 1) * 10);
                    break;
                case DATE_TIME:
                case DATE:
                case TIME:
                    returnContent = new Date();
                    break;
                default:
                    throw new AssertionError(abstractInput.getInputValueType().name());
            }

            Collection<String> chckChanges = qfoc.setReturnValue(abstractInput.getId(), returnContent);

            int orgc = chckChanges.size();
            chckChanges = CollectionUtil.toDistinct(chckChanges);
            printStr(questionId + " item changes(" + chckChanges.size() + "/" + orgc + "): " + Strings.join(", ", chckChanges));
            if (randomElement == null) {
                randomElement = abstractInput;
            }
        }
        printStr("-----------------------------------");
        for (InputOptionContent ioc : qfoc.getInputOptionContents()) {

            if (ioc.getId().equals("test1")) {
                int x = 6;
            }

            if (ioc.getParentId() == null) {
                int x = 6;
            }

            if (ioc.getParentId().equals("b11_c1")) {
                int x = 6;
            }

            String questionId = qfoc.getParentElementOfType(ioc.getId(), Question.class).getId();
            if (questionId.equals("c26dfgdgd")) {
                int x = 6;
                printStr("-----------------------------------");
                printStr("--------------c26 events---------------------");

                for (AbstractFormEvent afev : ioc.getEvents()) {

                    printStr(afev.toString());
                }

                printStr("-----------------------------------");
            }

            printStr("-----------------------------------");
//            for (AbstractFormEvent afev : ioc.getEvents()) {
//
//                printStr(afev.toString());
//            }

            Collection<String> chckChanges = qfoc.setChecked(ioc.getId(), true);
            int orgc = chckChanges.size();
            chckChanges = CollectionUtil.toDistinct(chckChanges);
            printStr(questionId + " item changes(" + chckChanges.size() + "/" + orgc + "): " + Strings.join(", ", chckChanges));
            printStr("-----------------------------------");
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

        FormResultBuilder frb = new FormResultBuilder();
        String xmlResult = frb.GetFormResultXmlString(ft);
        printStr("form result xml:");
        printStr(xmlResult);

        printStr("-----------------------------------");

        qfoc.setReturnValue("anket_i1", 1);
        qfoc.setReturnValue("resp_i1", 2);

        printStr("Actual Desc: " + qfoc.getActualShortDescription());

        printStr("-----------------------------------");

        ContentContainer ccWithGenData = qfoc.getElement("b11_c1", ContentContainer.class);

        printStr("ccWithGenData input count: " + ccWithGenData.getContentInputs().size());

        printStr("-----------------------------------");
        Collection<String> changes = qfoc.setEnabled("main_questions", false);
        printChanges("main_questions", changes);
        printStr("-----------------------------------");
        changes = qfoc.setEnabled("main_questions", true);
        printChanges("main_questions", changes);
        printStr("-----------------------------------");
        changes = qfoc.setEnabled("main_questions", false);
        printChanges("main_questions", changes);
        printStr("-----------------------------------");
//        changes = qfoc.setEnabled("main_questions", true);
//        printChanges("main_questions", changes);
        printStr("-----------------------------------");

        changes = qfoc.setReturnValue("d01i1", 12);
        printChanges("d01i1", changes);
        changes = qfoc.setReturnValue("d05i1", 12);
        printChanges("d05i1", changes);

        Object d07_i1rv = qfoc.getInputReturnValue("d07_i1");
        printStr("d07_i1 value: " + d07_i1rv);
        printStr("-----------------------------------");

        changes = qfoc.setReturnValue("d01i1", 100);
        qfoc.setReturnValue("anket_i1", -1);

        printChanges("d01i1", changes);
        InputValidationResult ivr = qfoc.validateInput("d01i1");
        printStr("IVR: " + ivr.toString());
        printStr("-----------------------------------");
        Collection<InputValidationResult> ivrs = qfoc.validateInputs();

        Collection<AbstractInput> ihv = qfoc.getInputsHavingValidations();

        for (InputValidationResult vr : ivrs) {
            printStr("IVR: " + vr.toString());
        }

        printStr("-----------------------------------");

        Collection<ContentContainer> reqCcs = qfoc.getRequiredContainers();
        printStr("Req cont size: " + reqCcs.size());

        printStr("-----------------------------------");

        AbstractInput resp_emailInput = qfoc.getInputTextById("resp_email_i1");
        InputValidationResult reIvr = qfoc.validateInput("resp_email_i1");
        printStr("EMAILC_IVR 1: " + reIvr.toString());
        qfoc.setReturnValue("resp_email_i1", "s@g.com");
        reIvr = qfoc.validateInput("resp_email_i1");
        printStr("EMAILC_IVR 2: " + reIvr.toString());
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

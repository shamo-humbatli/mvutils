package test;

import java.io.File;
import javax.xml.parsers.ParserConfigurationException;
import mobvey.form.QuestionForm;
import mobvey.form.answer.SimpleAnswer;
import mobvey.form.answer.content.InputOptionContent;
import mobvey.form.answer.content.InputTextContent;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.base.AbstractAnswer;
import mobvey.form.base.AbstractInput;
import mobvey.form.question.Question;
import mobvey.parser.FormParser;

/**
 *
 * @author Shamo Humbatli
 */
public class FormParserTest {

    public static void main(String[] args) throws ParserConfigurationException {

//        if(args == null || args.length == 0)
//        {
//            System.out.println("Please give working directory path");
//            return;
//        }
//        
//        String filePath = args[0];
        String filePath = "C:\\Test";

        if (filePath == null) {
            return;
        }

        FormParser fp = new FormParser(new File(filePath));
        QuestionForm qf = fp.ParseXml("questions.xml");

        System.out.println("Form info: " + qf.toString());

        for (Question q : qf.getQuestions()) {
            System.out.println("Question info: " + q.toString());

            for (AbstractAnswer a : q.getAnswers()) {

                if (!(a instanceof SimpleAnswer)) {
                    continue;
                }

                System.out.println("Answer info: " + a.toString());

                for (ContentContainer cc : a.getAnswerContentContainers()) {
                    ContentContainerDetails(cc);
                }
            }

            System.out.println("\n============================================================\n");
        }

    }

    private static void ContentContainerDetails(ContentContainer cc) {
        System.out.println("Container info: " + cc.toString());

        for (AbstractInput ai : cc.getContentInputs()) {
            InputDetails(ai);
        }
    }

    private static void InputDetails(AbstractInput ai) {
        if (ai instanceof InputOptionContent) {
            InputOptionContent ioc = (InputOptionContent) ai;
            System.out.println("Input option info: " + ioc.toString());
        } else if (ai instanceof InputTextContent) {
            InputTextContent itc = (InputTextContent) ai;
            System.out.println("Input text info: " + itc.toString());
        }

        if (ai.isComplex()) {
            System.out.println("============children-begin=============");
            for (ContentContainer subCC : ai.getContentContainers()) {
                ContentContainerDetails(subCC);
            }
            System.out.println("============children-end=============");
        }
    }
}

package test;

import java.io.File;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import mobvey.form.elements.QuestionForm;
import mobvey.form.elements.SimpleAnswer;
import mobvey.form.elements.InputOptionContent;
import mobvey.form.elements.InputTextContent;
import mobvey.form.elements.ContentContainer;
import mobvey.form.elements.AbstractAnswer;
import mobvey.form.elements.AbstractInput;
import mobvey.form.elements.Question;
import mobvey.form.FormParser;

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
        QuestionForm qf = fp.parseXml("questions_workforce.xml");


        System.out.println("Form info: " + qf.toString());
        

       // PrintForm(qf);

//        QuestionForm qfc = qf.CloneExact();
//        System.out.println("Form info cloned: " + qfc.toString());
//        PrintForm(qfc);
    }

    private static void PrintForm(Collection<Question> questions) {
        for (Question q : questions) {
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
        System.out.println("\n\n================CONTAINER======================");
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

        if (ai.HasContainersToReviewIfRequired()) {
            System.out.println("============HAS REQUIRED CONTAINERS TO DEAL=============");
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

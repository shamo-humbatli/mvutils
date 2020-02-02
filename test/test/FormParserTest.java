package test;

import java.io.File;
import javax.xml.parsers.ParserConfigurationException;
import mobvey.form.QuestionForm;
import mobvey.form.answer.SimpleAnswer;
import mobvey.form.answer.content.InputOptionContent;
import mobvey.form.answer.content.InputTextContent;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.base.AbstractAnswer;
import mobvey.form.question.Question;
import mobvey.parser.FormParser;

/**
 *
 * @author Shamo Humbatli
 */
public class FormParserTest {

    public static void main(String[] args) throws ParserConfigurationException {

        FormParser fp = new FormParser(new File("C:\\Test"));
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
                    System.out.println("Container info: " + a.toString());

                    for (Object ai : cc.getContentInputs()) {

                        if (ai instanceof InputOptionContent) {
                            InputOptionContent ioc = (InputOptionContent) ai;
                            System.out.println("Input option info: " + ioc.toString());
                        } else if (ai instanceof InputTextContent) {
                            InputTextContent itc = (InputTextContent) ai;
                            System.out.println("Input text info: " + itc.toString());
                        }
                    }

                }
            }
        }

    }
}

package test;

import javax.xml.parsers.ParserConfigurationException;
import mobvey.form.builder.FormResultBuilder;
import mobvey.form.enums.ColumnDefinitionType;
import mobvey.form.result.FormResult;
import mobvey.form.result.InputResult;
import mobvey.form.result.QuestionResult;

/**
 *
 * @author Shamo Humbatli
 */
public class ResultTest {

    public static void main(String[] args) throws ParserConfigurationException {
        InputResult ir = new InputResult();
        ir.setColumnDefinition("0");
        ir.setReturnValue("004");

        InputResult ir1 = new InputResult();
        ir1.setColumnDefinition("test_col1");
        ir1.setColumnDefinitionType(ColumnDefinitionType.CN);
        ir1.setReturnValue("005");

        QuestionResult qr = new QuestionResult();
        qr.setId("s1");

        qr.AddInputResult(ir);
        qr.AddInputResult(ir1);

        QuestionResult qr1 = new QuestionResult();
        qr1.setId("s2");

        qr1.AddInputResult(ir);
        qr1.AddInputResult(ir1);

        FormResult fr = new FormResult("03120459", "1");
        fr.setLang("az"); //this is not important

        fr.AddQuestionResults(qr);
        fr.AddQuestionResults(qr1);

        FormResultBuilder frb = new FormResultBuilder();

        String resultXmlForRequest = frb.GetFormResultXmlString(fr);

        System.out.println(resultXmlForRequest);
        System.out.println("--------------Cloned Here----------------");

        FormResult frc = fr.CloneExact();

        String rc = frb.GetFormResultXmlString(frc);
        System.out.println(rc);
    }
}

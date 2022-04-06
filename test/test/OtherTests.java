package test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import mobvey.common.Strings;
import mobvey.form.elements.InputTextContent;
import mobvey.form.enums.InputValueType;

/**
 *
 * @author Shamo Humbatli
 */
public class OtherTests {
    
    public static void main(String[] args) {
        dtTests();
        srvTests();
    }
    
    static void dtTests() {
        printStr("\n------- Date/Time Tests--------");
        Date dt = new Date();
        SimpleDateFormat sfg = new SimpleDateFormat("HH:mm");
        printStr("TF: " + sfg.format(dt));
        
        sfg = new SimpleDateFormat("yyyy-MM-dd");
        printStr("DF: " + sfg.format(dt));
        
        sfg = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        printStr("DTF: " + sfg.format(dt));
        
        printStr("Number format: " + String.format("%02d", 99));
        printStr("Number format: " + String.format("%02d", 9));
    }
    
    static void srvTests() {
        printStr("\n------- Set Return Value Tests--------");
        
        InputTextContent itx = new InputTextContent();
        
        Object retv = null;
        
        if (itx.getReturnContent() == retv) {
            printStr("Values are same");
        }
        
        retv = 45.5;
        itx.setReturnContent(45.50);
        if (itx.getReturnContent().equals(retv)) {
            printStr("Values are same");
        }
        
        retv = "str_cnt";
        itx.setReturnContent("str_cnt");
        if (itx.getReturnContent().equals(retv)) {
            printStr("Values are same");
        }
        
        Calendar cl = Calendar.getInstance();
        Date dt = new Date();
        cl.setTime(dt);
        retv = dt;
        
        itx.setReturnContent(cl.getTime());
        if (itx.getReturnContent().equals(retv)) {
            printStr("Values are same");
        }
        
        printLine();
        
        String source = "shamofjkdjbmms37745545shamoenbjsdjkkgfshamo";
        String value = "shamo";
        int ni = Strings.getNextIndexOf(source, value, 4);
        printStr("next index at 2: " + ni);
        
        printLine();
        
        String tw1 = "Işçilərin iqtisadiyyatı";
        String tw2 = "işçi";
        
        String tw1lc = tw1.toLowerCase();
        String tw2lc = tw2.toLowerCase();
        
        printStr("contains: " + tw1lc.contains(tw2lc));
        printStr("matches: " + tw1.matches("(?i).*" + tw2 + ".*"));
        printStr("contains with util: " + Strings.containsIgnoringCase(tw1, tw2));
        
        printLine();
        itx = new InputTextContent();
        itx.setInputValueType(InputValueType.INT);
        itx.setFormat("%02d");
        itx.setReturnContent(45.99767678678);
        
        printStr("ITX RV: " + itx.getFormattedReturnContent());
        printLine();
    }
    
    static void printStr(String val) {
        System.out.println("->> " + val);
    }
    
    static void printLine() {
        printStr("----------------------");
    }
}

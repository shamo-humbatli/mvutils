package mobvey.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Shamo Humbatli
 */
public class DateUtil {
    
    private static final SimpleDateFormat _sdfDefault = new SimpleDateFormat();    
    private static final SimpleDateFormat _sdfCommon = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public static Date parseDefault(String value) throws ParseException {
        return _sdfDefault.parse(value);
    }
    
    public static Date parseCommon(String value) throws ParseException {
        return _sdfCommon.parse(value);
    }
    
    public static Date parse(String value, String format) throws ParseException {
        return new SimpleDateFormat(format).parse(value);
    }
}

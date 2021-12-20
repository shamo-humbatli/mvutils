package mobvey.common;

import java.util.List;

/**
 *
 * @author ShamoHumbatli
 */
public class Strings {

    public static String Join(String delimeter, List<? extends CharSequence> elements) {

        String result = "";

        if (delimeter == null) {
            delimeter = "";
        }

        if (elements == null || elements.isEmpty()) {
            return result;
        }
        
        Object[] arrayElements = elements.toArray();

        for (int ei = 0; ei < elements.size(); ei++) {
            if(ei < elements.size()-1)
            {
                result += arrayElements[ei] + delimeter;
            }
            else
            {
                result += arrayElements[ei];
            }   
        }
        
        return result;
    }
    
    public static boolean HasContent(String value)
    {
        return value != null && !value.isBlank();
    }
    
    public static boolean isNothing(String value)
    {
        return !HasContent(value);
    }
}

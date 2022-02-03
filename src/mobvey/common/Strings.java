package mobvey.common;

import java.util.List;
import java.util.UUID;

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
            if (ei < elements.size() - 1) {
                result += arrayElements[ei] + delimeter;
            } else {
                result += arrayElements[ei];
            }
        }

        return result;
    }

    public static boolean hasContent(String value) {
        return !isNullOrEmpty(value);
    }

    public static boolean isNothing(String value) {
        return !hasContent(value);
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().equals("");
    }
    
    public static String GetRandomIdString()
    {
       return UUID.randomUUID().toString();
    }
}

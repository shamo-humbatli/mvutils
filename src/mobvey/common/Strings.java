package mobvey.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 *
 * @author ShamoHumbatli
 */
public class Strings {

    public static String join(String delimeter, Collection<? extends CharSequence> elements) {

        String result = "";

        if (delimeter == null) {
            delimeter = "";
        }

        if (elements == null || elements.isEmpty()) {
            return result;
        }

        Object[] arrayElements = elements.toArray();

        return join(delimeter, arrayElements);
    }

    public static String join(String delimeter, Object[] elements) {

        String result = "";

        if (delimeter == null) {
            delimeter = "";
        }

        if (elements == null || elements.length == 0) {
            return result;
        }

        for (int ei = 0; ei < elements.length; ei++) {
            if (ei < elements.length - 1) {
                result += elements[ei] + delimeter;
            } else {
                result += elements[ei];
            }
        }

        return result;
    }

    public static boolean hasContent(String value) {
        return !isNullOrEmpty(value);
    }

    public static boolean hasNoContent(String value) {
        return isNullOrEmpty(value);
    }

    public static boolean isNothing(String value) {
        return !hasContent(value);
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().equals("");
    }

    public static String getRandomUuidString() {
        return UUID.randomUUID().toString();
    }

    public static Collection<String> getParams(String value, char paramOpener, char paramCloser) {
        Collection<String> result = new ArrayList<String>();

        if (isNullOrEmpty(value)) {
            return result;
        }

        while (true) {
            int fpo = value.indexOf(paramOpener);
            int fpc = value.indexOf(paramCloser);

            if (fpo < 0 || fpc < 0) {
                break;
            }

            result.add(value.substring(fpo + 1, fpc));
            value = value.substring(fpc + 1);
        }

        return result;
    }
}

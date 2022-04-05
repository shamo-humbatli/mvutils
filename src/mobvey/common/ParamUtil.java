package mobvey.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Shamo Humbatli
 */
public class ParamUtil {

    public static String getAsArray(Collection<String> values) {
        if (values == null || values.isEmpty()) {
            return "[]";
        }

        return String.format("[%s]", Strings.join(",", values));
    }

    public static String getAsStringLine(String value) {
        return String.format("'%s'", value);
    }

    public static String[] stringSplitByDepth(String strValue,
            String splitBy, int depth,
            String depthIncreaseSymbols,
            String depthDecreaseSymbols) {

        if (Strings.isNothing(strValue)) {
            return new String[0];
        }

        strValue = strValue.trim();

        int[] splitIndexes = getStringIndexesInDepth(strValue, splitBy, depth, depthIncreaseSymbols, depthDecreaseSymbols);

        int vl = strValue.length();
        int stl = splitBy.length();

        String[] arrItems;
        if (splitIndexes.length == 0) {
            arrItems = new String[]{
                strValue
            };
        } else {
            arrItems = new String[splitIndexes.length + 1];

            int ssStart = 0;
            int ssEnd = 0;

            int currentArrItemIndex = 0;

            while (currentArrItemIndex < arrItems.length) {
                if (currentArrItemIndex <= splitIndexes.length - 1) {
                    ssEnd = splitIndexes[currentArrItemIndex];
                } else {
                    ssEnd = vl;
                }

                String ss = strValue.substring(ssStart, ssEnd);
                ss = ss.trim();

                arrItems[currentArrItemIndex] = ss;
                currentArrItemIndex++;

                ssStart = ssEnd + stl;
            }
        }

        return arrItems;
    }

    public static String[] splitByDepth(String strValue,
            char splitBy, int depth,
            String depthIncreaseSymbols,
            String depthDecreaseSymbols) {

        if (Strings.isNothing(strValue)) {
            return new String[0];
        }

        strValue = strValue.trim();

        int[] commaIndexes = getSymbolIndexesInDepth(strValue, splitBy, depth, depthIncreaseSymbols, depthDecreaseSymbols);

        int vl = strValue.length();

        String[] arrItems;
        if (commaIndexes.length == 0) {
            arrItems = new String[]{
                strValue
            };
        } else {
            arrItems = new String[commaIndexes.length + 1];

            int ssStart = 0;
            int ssEnd = 0;

            int currentArrItemIndex = 0;

            while (currentArrItemIndex < arrItems.length) {
                if (currentArrItemIndex <= commaIndexes.length - 1) {
                    ssEnd = commaIndexes[currentArrItemIndex];
                } else {
                    ssEnd = vl;
                }

                String ss = strValue.substring(ssStart, ssEnd);
                ss = ss.trim();

                arrItems[currentArrItemIndex] = ss;
                currentArrItemIndex++;

                ssStart = ssEnd + 1;
            }
        }

        return arrItems;
    }

    public static int[] getSymbolIndexesInDepth(String value,
            char symbol,
            int depth,
            String depthIncreaseSymbols,
            String depthDecreaseSymbols) {
        if (Strings.isNullOrEmpty(value)) {
            return new int[0];
        }

        List<Integer> indexes = new ArrayList<>();

        int currentDepth = 0;
        int vl = value.length();

        for (int ci = 0; ci < vl; ci++) {
            Character cc = value.charAt(ci);

            if (depthIncreaseSymbols.indexOf(cc) >= 0) {
                currentDepth++;
            } else if (depthDecreaseSymbols.indexOf(cc) >= 0) {
                currentDepth--;
            }

            if (currentDepth == depth && cc.equals(symbol)) {
                indexes.add(ci);
            }
        }

        return CollectionUtil.toIntArray(indexes);
    }

    public static int[] getStringIndexesInDepth(String value,
            String text,
            int depth,
            String depthIncreaseSymbols,
            String depthDecreaseSymbols) {
        if (Strings.isNullOrEmpty(value)) {
            return new int[0];
        }
        List<Integer> indexes = new ArrayList<>();

        int currentDepth = 0;
        int vl = value.length();
        int tl = text.length();
        String tuc = text.toUpperCase();

        int cli = vl - tl;

        for (int ci = 0; ci <= cli; ci++) {

            Character cc = value.charAt(ci);

            if (depthIncreaseSymbols.indexOf(cc) >= 0) {
                currentDepth++;
            } else if (depthDecreaseSymbols.indexOf(cc) >= 0) {
                currentDepth--;
            }

            if (currentDepth == depth) {
                String ss = value.substring(ci, ci + tl);
                if (ss.toUpperCase().equals(tuc)) {
                    indexes.add(ci);
                }
            }
        }

        return CollectionUtil.toIntArray(indexes);
    }

    public static String[] getParamArrayItems(String paramArr) {

        if (Strings.isNullOrEmpty(paramArr)) {
            return new String[0];
        }

        paramArr = paramArr.trim();
        int pl = paramArr.length();

        if (paramArr.indexOf("[") == 0 && paramArr.lastIndexOf("]") == pl - 1) {
            paramArr = paramArr.substring(1, pl - 1);
        }

        return splitByDepth(paramArr, ',', 0, "[{('", "')}]");
    }

    public static String[] getParam(String key, String[] paramsArr) {
        if (paramsArr == null || paramsArr.length == 0) {
            return null;
        }

        if (Strings.isNullOrEmpty(key)) {
            return null;
        }

        String keyLc = key.toLowerCase();

        for (String paramItem : paramsArr) {
            if (Strings.isNullOrEmpty(paramItem)) {
                continue;
            }

            String[] pkv = paramItem.split(":");

            if (pkv == null || pkv.length == 0) {
                continue;
            }

            String piKeyLc = pkv[0].trim().toLowerCase();

            if (!piKeyLc.equals(keyLc)) {
                continue;
            }

            return pkv;
        }

        return null;
    }

    public static String[] getParamObjectDeclarations(String objectStr) {

        if (Strings.isNullOrEmpty(objectStr)) {
            return new String[0];
        }

        objectStr = objectStr.trim();
        int vl = objectStr.length();

        if (objectStr.indexOf("{") == 0 && objectStr.lastIndexOf("}") == vl - 1) {
            objectStr = objectStr.substring(1, vl - 1);
        }

        return splitByDepth(objectStr, ';', 0, "[{('", "')}]");
    }

    public static String getLinePropertyValue(String lineString, String propertyName) {
        try {
            String[] components = lineString.split(";");

            for (String component : components) {
                if (!component.contains(propertyName)) {
                    continue;
                }

                String[] pair = component.split(":");

                if (!pair[0].contains(propertyName)) {
                    continue;
                }

                return pair[1].trim();
            }
        } catch (Exception exp) {

        }
        return null;
    }

    public static String getStringValue(String stringLine) {
        if (Strings.isNullOrEmpty(stringLine)) {
            return stringLine;
        }

        if (stringLine.indexOf('\'') == 0
                && stringLine.lastIndexOf('\'') == stringLine.length() - 1) {
            stringLine = stringLine.substring(1, stringLine.length() - 1);
        }

        return stringLine;
    }
}

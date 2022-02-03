package mobvey.common;

import java.util.Collection;

/**
 *
 * @author ShamoHumbatli
 */
public class NumberUtil {

    public static Double getAsDouble(Object value) {
        try {
            return Double.valueOf(value.toString());
        } catch (Exception exp) {
            //ignored
            return null;
        }
    }

    public static boolean isInRange(Double value, Double min, Double max) {
        try {
            return value >= min && value <= max;
        } catch (Exception exp) {
            //ignored
            return false;
        }
    }

    public static Double sum(Collection<? extends Number> values) {
        if (values == null) {
            return null;
        }

        if (values.isEmpty()) {
            return 0.00;
        }

        Double sum = 0.00;
        for (Number number : values) {
            if (number == null) {
                continue;
            }

            sum += number.doubleValue();
        }

        return sum;
    }
}

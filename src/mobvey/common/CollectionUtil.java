package mobvey.common;

import java.util.Collection;

/**
 *
 * @author ShamoHumbatli
 */
public final class CollectionUtil {
    
    public static <T extends Number> int[] toIntArray(Collection<T> items) {
        int[] ret = new int[items.size()];
        int i = 0;
        for (T e : items) {
            ret[i++] = Integer.valueOf(e.toString());
        }
        return ret;
    }
}

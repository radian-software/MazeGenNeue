// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.util;

import java.util.List;

public final class ArrayUtil {

    private ArrayUtil() {}

    public static String join(String delimiter, int[] array) {
        Require.nonNull(delimiter, "delimiter");
        Require.nonNull(array, "array");
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<array.length-1; i++) {
            sb.append(array[i]);
            sb.append(delimiter);
        }
        sb.append(array[array.length-1]);
        return sb.toString();
    }

    public static <T> T pop(List<T> list) {
        return list.remove(list.size() - 1);
    }

    public static <T> T last(List<T> list) {
        return list.get(list.size() - 1);
    }

}

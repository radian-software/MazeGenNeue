// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.util;

public final class ArrayUtil {

    public static String join(String delimiter, int[] array) {
        if (delimiter == null) delimiter = "";
        if (array == null || array.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<array.length-1; i++) {
            sb.append(array[i]);
            sb.append(delimiter);
        }
        sb.append(array[array.length-1]);
        return sb.toString();
    }

}

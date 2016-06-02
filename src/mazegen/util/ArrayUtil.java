// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public static String horizontalJoin(String delimiter, List<String> strings) {
        return strings
                .stream()
                .map(str -> Arrays.asList(str.split("\\n")))
                .reduce((l1, l2) -> {
                    if (l1.size() != l2.size())
                        throw new IllegalArgumentException("each string must have the same number of lines");
                    for (int i=0; i<l1.size(); i++) {
                        l1.set(i, l1.get(i) + delimiter + l2.get(i));
                    }
                    return l1;
                })
                .get()
                .stream()
                .collect(Collectors.joining("\n"));
    }

}

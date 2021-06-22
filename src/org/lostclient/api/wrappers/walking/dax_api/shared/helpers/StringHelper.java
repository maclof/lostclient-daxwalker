package org.lostclient.api.wrappers.walking.dax_api.shared.helpers;

import java.util.Arrays;

public class StringHelper {
    public static String stripFormatting(String str) {
        return str == null ? null : str.replaceAll("\\<.*?\\>", "");
    }

    public static boolean nameEquals(String name, String... names) {
        return Arrays.stream(names).anyMatch((s) -> s.equals(name));
    }

    public static boolean nameContains(String name, String... names) {
        return Arrays.stream(names).anyMatch((s) -> s.contains(name));
    }
}

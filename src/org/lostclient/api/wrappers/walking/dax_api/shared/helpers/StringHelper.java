package org.lostclient.api.wrappers.walking.dax_api.shared.helpers;

public class StringHelper {
    public static String stripFormatting(String str) {
        return str == null ? null : str.replaceAll("\\<.*?\\>", "");
    }
}

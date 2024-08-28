package com.github.jtama.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public final class StringUtils {

    public static String strip(String str) {
        return str == null ? null : str.strip();
    }

    public static String stripToNull(String str) {
        if (str == null) {
            return null;
        }
        String ts = strip(str);
        return ts.isEmpty() ? null : ts;
    }

    public static URL toURL(String value) {
        try {
            return URI.create(value).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}

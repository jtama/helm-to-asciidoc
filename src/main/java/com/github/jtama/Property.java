package com.github.jtama;

import java.util.Objects;

import com.github.jtama.utils.StringUtils;

public record Property(String name, String description, String defaultValue, int lineNumber) {
    public Property {
        if (defaultValue != null) {
            defaultValue = StringUtils.stripToNull(defaultValue.replaceAll("\\|", "\\\\|"));
        }
        Objects.requireNonNull(lineNumber, "property :lineNumber is required");
    }
}

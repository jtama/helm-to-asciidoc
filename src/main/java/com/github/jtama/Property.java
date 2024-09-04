package com.github.jtama;

import com.github.jtama.utils.StringUtils;
import io.quarkus.qute.TemplateData;
import jakarta.annotation.Nullable;

import java.util.Objects;
import java.util.UUID;

@TemplateData
public record Property(String name, @Nullable String description, @Nullable String defaultValue, @Nullable UUID uuid, Boolean isObject,
                       int lineNumber) {


    public Property {
        Objects.requireNonNull(name, "property :name is required");
        Objects.requireNonNull(isObject, "property :isObject is required");
        if(defaultValue != null) {
            defaultValue = StringUtils.stripToNull(defaultValue.replaceAll("\\|", "\\\\|"));
        }
    }

    public Property(String name, @Nullable String description, @Nullable UUID uuid, int lineNumber) {
        this(name, description, null, uuid, true, lineNumber);
    }

    public Property(String name, @Nullable String description, @Nullable String defaultValue, int lineNumber) {
        this(name, description, defaultValue, null, false, lineNumber);
    }

    public String formattedID() {
        return StringUtils.formattedUUID(uuid);
    }

    public boolean hasDefaultValue() {
        return defaultValue != null;
    }
}

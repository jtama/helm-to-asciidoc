package org.acme;

public record Property(String name, String description, String defaultValue) {
    public Property {
        if (defaultValue == null || defaultValue.isBlank())
            defaultValue = null;
    }
}

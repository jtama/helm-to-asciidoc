package org.acme;

import java.util.Objects;

public record Dependency(String name, String repository, String version, String condition) {
    public Dependency {
        Objects.requireNonNull(name, "property :name is required");
        Objects.requireNonNull(repository, "property :repository is required");
        Objects.requireNonNull(version, "property :version is required");
        condition = Objects.requireNonNullElse(condition, "N/A");
    }

    public static final class Builder {
        private String name;

        private String repository;

        private String version;

        private String condition;

        public Builder() {
        }

        public Builder setName(String name) {
            this.name = Objects.requireNonNull(name, "Null name");
            return this;
        }

        public Builder setRepository(String repository) {
            this.repository = Objects.requireNonNull(repository, "Null repository");
            return this;
        }

        public Builder setVersion(String version) {
            this.version = Objects.requireNonNull(version, "Null version");
            return this;
        }

        public Builder setCondition(String condition) {
            this.condition = condition == null || condition.isBlank() ? null : condition;
            return this;
        }

        public Dependency build() {
            if (this.name == null || this.repository == null || this.version == null) {
                StringBuilder missing = new StringBuilder();
                if (this.name == null) {
                    missing.append(" name");
                }
                if (this.repository == null) {
                    missing.append(" repository");
                }
                if (this.version == null) {
                    missing.append(" version");
                }
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new Dependency(this.name, this.repository, this.version, this.condition);
        }
    }
}

package org.acme;

import java.util.List;
import java.util.Objects;

public record Chart(String name, String description, String type, String version, String appVersion,
                    List<Dependency> dependencies, List<Section> sections) {
    public Chart {
        Objects.requireNonNull(name, "property :name is required");
        Objects.requireNonNull(description, "property :description is required");
        Objects.requireNonNull(type, "property :type is required");
        Objects.requireNonNull(version, "property :version is required");
        Objects.requireNonNull(appVersion, "property :appVersion is required");
        Objects.requireNonNull(dependencies, "property :dependencies is required");
        Objects.requireNonNull(sections, "property :sections is required");
    }

    @Override
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(this));
    }

    public static final class Builder {
        private String name;

        private String description;

        private String type;

        private String version;

        private String appVersion;

        private List<Dependency> dependencies = List.of();

        private List<Section> sections = List.of();

        public Builder() {
        }

        public Builder setName(String name) {
            this.name = Objects.requireNonNull(name, "Null name");
            return this;
        }

        public Builder setDescription(String description) {
            this.description = Objects.requireNonNull(description, "Null description");
            return this;
        }

        public Builder setType(String type) {
            this.type = Objects.requireNonNull(type, "Null type");
            return this;
        }

        public Builder setVersion(String version) {
            this.version = Objects.requireNonNull(version, "Null version");
            return this;
        }

        public Builder setAppVersion(String appVersion) {
            this.appVersion = Objects.requireNonNull(appVersion, "Null appVersion");
            return this;
        }

        public Builder setDependencies(List<Dependency> dependencies) {
            this.dependencies = (dependencies == null) ? List.of() : dependencies;
            return this;
        }

        public Builder setSections(List<Section> sections) {
            this.sections = (sections == null) ? List.of() : sections;
            return this;
        }

        public Chart build() {
            if (this.name == null || this.description == null || this.type == null ||
                    this.version == null || this.appVersion == null || this.dependencies == null ||
                    this.sections == null) {
                StringBuilder missing = new StringBuilder();
                if (this.name == null) {
                    missing.append(" name");
                }
                if (this.description == null) {
                    missing.append(" description");
                }
                if (this.type == null) {
                    missing.append(" type");
                }
                if (this.version == null) {
                    missing.append(" version");
                }
                if (this.appVersion == null) {
                    missing.append(" appVersion");
                }
                if (this.dependencies == null) {
                    missing.append(" dependencies");
                }
                if (this.sections == null) {
                    missing.append(" sections");
                }
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new Chart(this.name, this.description, this.type, this.version, this.appVersion,
                    this.dependencies, this.sections);
        }
    }
}

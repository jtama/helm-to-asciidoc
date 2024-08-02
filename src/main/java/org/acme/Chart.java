package org.acme;

import java.util.List;
import java.util.Objects;

public record Chart(String name, String description, String type, String version, String appVersion,
                    List<Dependency> dependencies, List<Section> sections) {
    public Chart {
        Objects.requireNonNull(name, "property :name is required");
        Objects.requireNonNull(version, "property :version is required");
        Objects.requireNonNull(dependencies, "property :dependencies is required");
        Objects.requireNonNull(sections, "property :sections is required");
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
            this.description = description;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setVersion(String version) {
            this.version = Objects.requireNonNull(version, "Null version");
            return this;
        }

        public Builder setAppVersion(String appVersion) {
            this.appVersion = appVersion;
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
            if (this.name == null || this.version == null || this.dependencies == null || this.sections == null) {
                StringBuilder missing = new StringBuilder();
                if (this.name == null) {
                    missing.append(" name");
                }
                if (this.version == null) {
                    missing.append(" version");
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

package com.github.jtama;

import java.util.List;
import java.util.Objects;

import io.quarkus.qute.TemplateData;
import jakarta.annotation.Nullable;

import com.github.jtama.utils.StringUtils;

@TemplateData
public record Dependency(String name,
        String version,
        @Nullable String repository,
        @Nullable String condition,
        List<String> tags,
        List<String> importValues,
        @Nullable String alias) {

    public Dependency {
        Objects.requireNonNull(name, "property :name is required");
        Objects.requireNonNull(version, "property :version is required");
        Objects.requireNonNull(tags, "property :tags is required");
        Objects.requireNonNull(importValues, "property :importValues is required");
    }

    public static final class Builder {
        private String name;

        private String version;

        private String repository;

        private String condition;

        private List<String> tags = List.of();

        private List<String> importValues = List.of();

        private String alias;

        public Builder() {
        }

        public Builder setName(String name) {
            this.name = Objects.requireNonNull(StringUtils.stripToNull(name), "Null name");
            return this;
        }

        public Builder setVersion(String version) {
            this.version = Objects.requireNonNull(StringUtils.stripToNull(version), "Null version");
            return this;
        }

        public Builder setRepository(@Nullable String repository) {
            this.repository = StringUtils.stripToNull(repository);
            return this;
        }

        public Builder setCondition(@Nullable String condition) {
            this.condition = StringUtils.stripToNull(condition);
            return this;
        }

        public Builder setTags(List<String> tags) {
            this.tags = (tags == null) ? List.of() : tags;
            return this;
        }

        public Builder setImportValues(List<String> importValues) {
            this.importValues = (importValues == null) ? List.of() : importValues;
            return this;
        }

        public Builder setAlias(@Nullable String alias) {
            this.alias = StringUtils.stripToNull(alias);
            return this;
        }

        public Dependency build() {
            if (this.name == null || this.version == null || this.tags == null ||
                    this.importValues == null) {
                StringBuilder missing = new StringBuilder();
                if (this.name == null) {
                    missing.append(" name");
                }
                if (this.version == null) {
                    missing.append(" version");
                }
                if (this.tags == null) {
                    missing.append(" tags");
                }
                if (this.importValues == null) {
                    missing.append(" importValues");
                }
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new Dependency(this.name, this.version, this.repository, this.condition, this.tags,
                    this.importValues, this.alias);
        }
    }
}

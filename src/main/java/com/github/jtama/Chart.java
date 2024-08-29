package com.github.jtama;

import static com.github.jtama.utils.StringUtils.stripToNull;

import java.net.URL;
import java.util.List;
import java.util.Objects;

import io.quarkus.qute.TemplateData;
import jakarta.annotation.Nullable;

@TemplateData
public record Chart(String apiVersion,
        String name,
        String version,
        @Nullable String kubeVersion,
        @Nullable String description,
        @Nullable String type,
        List<String> keywords,
        @Nullable URL home,
        List<URL> sources,
        List<Dependency> dependencies,
        List<Maintainer> maintainers,
        @Nullable URL icon,
        @Nullable String appVersion,
        @Nullable Boolean deprecated,
        List<Annotation> annotations,
        List<Section> sections) {

    public Chart {
        Objects.requireNonNull(apiVersion, "property :apiVersion is required");
        Objects.requireNonNull(name, "property :name is required");
        Objects.requireNonNull(version, "property :version is required");
        Objects.requireNonNull(sources, "property :sources is required");
        Objects.requireNonNull(dependencies, "property :dependencies is required");
        Objects.requireNonNull(maintainers, "property :maintainers is required");
        Objects.requireNonNull(annotations, "property :annotations is required");
        Objects.requireNonNull(sections, "property :sections is required");
        deprecated = Objects.requireNonNullElse(deprecated, Boolean.FALSE);
    }

    public static final class Builder {
        private String apiVersion;

        private String name;

        private String version;

        private String kubeVersion;

        private String description;

        private String type;

        private List<String> keywords = List.of();

        private URL home;

        private List<URL> sources = List.of();

        private List<Dependency> dependencies = List.of();

        private List<Maintainer> maintainers = List.of();

        private URL icon;

        private String appVersion;

        private Boolean deprecated;

        private List<Annotation> annotations = List.of();

        private List<Section> sections = List.of();

        public Builder() {
        }

        public Builder setApiVersion(String apiVersion) {
            this.apiVersion = Objects.requireNonNull(stripToNull(apiVersion), "Null apiVersion");
            return this;
        }

        public Builder setName(String name) {
            this.name = Objects.requireNonNull(stripToNull(name), "Null name");
            return this;
        }

        public Builder setVersion(String version) {
            this.version = Objects.requireNonNull(stripToNull(version), "Null version");
            return this;
        }

        public Builder setKubeVersion(@Nullable String kubeVersion) {
            this.kubeVersion = stripToNull(kubeVersion);
            return this;
        }

        public Builder setDescription(@Nullable String description) {
            this.description = stripToNull(description);
            return this;
        }

        public Builder setType(@Nullable String type) {
            this.type = stripToNull(type);
            return this;
        }

        public Builder setKeywords(List<String> keywords) {
            this.keywords = (keywords == null) ? List.of() : keywords;
            return this;
        }

        public Builder setHome(@Nullable URL home) {
            this.home = home;
            return this;
        }

        public Builder setSources(List<URL> sources) {
            this.sources = (sources == null) ? List.of() : sources;
            return this;
        }

        public Builder setDependencies(List<Dependency> dependencies) {
            this.dependencies = (dependencies == null) ? List.of() : dependencies;
            return this;
        }

        public Builder setMaintainers(List<Maintainer> maintainers) {
            this.maintainers = (maintainers == null) ? List.of() : maintainers;
            return this;
        }

        public Builder setIcon(@Nullable URL icon) {
            this.icon = icon;
            return this;
        }

        public Builder setAppVersion(@Nullable String appVersion) {
            this.appVersion = stripToNull(appVersion);
            return this;
        }

        public Builder setDeprecated(@Nullable Boolean deprecated) {
            this.deprecated = deprecated;
            return this;
        }

        public Builder setAnnotations(List<Annotation> annotations) {
            this.annotations = (annotations == null) ? List.of() : annotations;
            return this;
        }

        public Builder setSections(List<Section> sections) {
            this.sections = (sections == null) ? List.of() : sections;
            return this;
        }

        public Chart build() {
            if (this.apiVersion == null || this.name == null || this.version == null ||
                    this.sources == null || this.dependencies == null || this.maintainers == null ||
                    this.annotations == null || this.sections == null) {
                StringBuilder missing = new StringBuilder();
                if (this.apiVersion == null) {
                    missing.append(" apiVersion");
                }
                if (this.name == null) {
                    missing.append(" name");
                }
                if (this.version == null) {
                    missing.append(" version");
                }
                if (this.sources == null) {
                    missing.append(" sources");
                }
                if (this.dependencies == null) {
                    missing.append(" dependencies");
                }
                if (this.maintainers == null) {
                    missing.append(" maintainers");
                }
                if (this.annotations == null) {
                    missing.append(" annotations");
                }
                if (this.sections == null) {
                    missing.append(" sections");
                }
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new Chart(this.apiVersion, this.name, this.version, this.kubeVersion, this.description,
                    this.type, this.keywords, this.home, this.sources, this.dependencies, this.maintainers, this.icon,
                    this.appVersion, this.deprecated, this.annotations, this.sections);
        }
    }

    record Annotation(String key, @Nullable String value) {

        public Annotation {
            Objects.requireNonNull(key, "property :key is required");
        }

        public static final class Builder {
            private String key;

            private String value;

            public Builder() {
            }

            public Builder setKey(String key) {
                this.key = Objects.requireNonNull(key, "Null key");
                return this;
            }

            public Builder setValue(String value) {
                this.value = value;
                return this;
            }

            public Annotation build() {
                if (this.key == null) {
                    throw new IllegalStateException("Missing required properties: key");
                }
                return new Annotation(this.key, this.value);
            }
        }
    }
}

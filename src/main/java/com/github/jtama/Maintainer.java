package com.github.jtama;

import io.quarkus.qute.TemplateData;

import static com.github.jtama.utils.StringUtils.stripToNull;

import java.net.URL;
import java.util.Objects;

@TemplateData
public record Maintainer(String name, String email, URL url) {

    public Maintainer {
        Objects.requireNonNull(name, "property :name is required");
    }

    public static final class Builder {
        private String name;

        private String email;

        private URL url;

        public Builder() {
        }

        public Builder setName(String name) {
            this.name = Objects.requireNonNull(name, "Null name");
            return this;
        }

        public Builder setEmail(String email) {
            this.email = stripToNull(email);
            return this;
        }

        public Builder setUrl(URL url) {
            this.url = url;
            return this;
        }

        public Maintainer build() {
            if (this.name == null) {
                throw new IllegalStateException("Missing required properties: name");
            }
            return new Maintainer(this.name, this.email, this.url);
        }
    }
}

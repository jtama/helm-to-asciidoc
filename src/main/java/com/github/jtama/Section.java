package com.github.jtama;

import com.github.jtama.utils.StringUtils;
import io.quarkus.qute.TemplateData;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@TemplateData
public record Section(String name, @Nullable String description, UUID uuid, List<Property> properties, List<Section> childs) {


    public Section {
        Objects.requireNonNull(name, "property :name is required");
        Objects.requireNonNull(uuid, "property :uuid is required");
        Objects.requireNonNull(properties, "property :properties is required");
        if (childs == null) {
            childs = new ArrayList<>();
        }
        if (properties == null) {
            properties = new ArrayList<>();
        }
        if (description != null) {
            description = StringUtils.stripToNull(description);
        }
    }

    public String formattedID() {
        return StringUtils.formattedUUID(uuid);
    }

    public static final class Builder {
        private String name;

        private String description;

        private UUID uuid = UUID.randomUUID();

        public Builder() {
        }

        public Builder setName(String name) {
            this.name = Objects.requireNonNull(name, "Null name");
            return this;
        }

        public Builder setUUID(UUID uuid) {
            this.uuid = Objects.requireNonNull(uuid, "Null uuid");
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Section build() {
            if (this.name == null) {
                throw new IllegalStateException("Missing required properties: name");
            }
            return new Section(this.name, this.description, uuid, new ArrayList<>(), new ArrayList<>());
        }
    }
}

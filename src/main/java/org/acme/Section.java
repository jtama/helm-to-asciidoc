package org.acme;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record Section(String name, String description, List<Property> properties, List<Section> childs) {


    public Section(String name, String description, List<Property> properties, List<Section> childs) {
        this.name = name;
        this.description = description;
        this.properties = properties;
        this.childs = childs;
    }

    public Section(String name, String description) {
        this(name, description, new ArrayList<>(), new ArrayList<>());
    }
}

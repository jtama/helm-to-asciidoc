package com.github.jtama;

import static com.github.jtama.utils.NodeUtils.getListValue;
import static com.github.jtama.utils.NodeUtils.getStringValue;

import java.util.List;
import java.util.function.Function;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class DependencyMapper {
    public static List<Dependency> readDependencies(SequenceNode valueNode) {
        return valueNode.getValue().stream()
                .map(DependencyMapper::mapToDependency)
                .toList();
    }

    public static Dependency mapToDependency(Node node) {
        if (node instanceof MappingNode mappingNode) {
            Dependency.Builder builder = new Dependency.Builder();
            mappingNode.getValue().forEach(tuple -> {
                switch (tuple.getKeyNode()) {
                    case ScalarNode key -> {
                        switch (key.getValue()) {
                            case "name" -> builder.setName(getStringValue(tuple.getValueNode()));
                            case "version" -> builder.setVersion(getStringValue(tuple.getValueNode()));
                            case "repository" -> builder.setRepository(getStringValue(tuple.getValueNode()));
                            case "condition" -> builder.setCondition(getStringValue(tuple.getValueNode()));
                            case "tags" ->
                                builder.setTags(getListValue((SequenceNode) tuple.getValueNode(), Function.identity()));
                            case "import-values" ->
                                builder.setImportValues(getListValue((SequenceNode) tuple.getValueNode(), Function.identity()));
                            case "alias" -> builder.setAlias(getStringValue(tuple.getValueNode()));
                        }
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + tuple.getKeyNode());
                }
            });
            return builder.build();
        }
        return null;
    }
}

package com.github.jtama;

import static com.github.jtama.utils.NodeUtils.getStringValue;

import java.util.List;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class AnnotationsMapper {
    public static List<Chart.Annotation> readMaintainers(SequenceNode valueNode) {
        return valueNode.getValue().stream()
                .map(AnnotationsMapper::mapToAnnotation)
                .toList();
    }

    public static Chart.Annotation mapToAnnotation(Node node) {
        if (node instanceof MappingNode mappingNode) {
            Chart.Annotation.Builder builder = new Chart.Annotation.Builder();
            mappingNode.getValue().forEach(tuple -> {
                switch (tuple.getKeyNode()) {
                    case ScalarNode key -> {
                        builder.setKey(key.getValue())
                                .setValue(getStringValue(tuple.getValueNode()));
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + tuple.getKeyNode());
                }
            });
            return builder.build();
        }
        return null;
    }
}

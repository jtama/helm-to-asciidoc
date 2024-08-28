package com.github.jtama;

import static com.github.jtama.utils.NodeUtils.getStringValue;
import static com.github.jtama.utils.StringUtils.toURL;

import java.util.List;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class MaintainersMapper {
    public static List<Maintainer> readMaintainers(SequenceNode valueNode) {
        return valueNode.getValue().stream()
                .map(MaintainersMapper::mapToMaintainer)
                .toList();
    }

    public static Maintainer mapToMaintainer(Node node) {
        if (node instanceof MappingNode mappingNode) {
            Maintainer.Builder builder = new Maintainer.Builder();
            mappingNode.getValue().forEach(tuple -> {
                switch (tuple.getKeyNode()) {
                    case ScalarNode key -> {
                        switch (key.getValue()) {
                            case "name" -> builder.setName(getStringValue(tuple.getValueNode()));
                            case "email" -> builder.setEmail(getStringValue(tuple.getValueNode()));
                            case "url" -> builder.setUrl(toURL(getStringValue(tuple.getValueNode())));
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

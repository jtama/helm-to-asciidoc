package com.github.jtama.utils;

import java.util.List;
import java.util.function.Function;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public final class NodeUtils {

    public static String getStringValue(Node tuple) {
        return ((ScalarNode) tuple).getValue();
    }

    public static <T> List<T> getListValue(SequenceNode node, Function<String, T> mapper) {
        return node.getValue()
                .stream()
                .map(item -> (ScalarNode) item)
                .map(ScalarNode::getValue)
                .map(mapper)
                .toList();
    }
}

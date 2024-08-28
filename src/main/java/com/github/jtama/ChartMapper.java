package com.github.jtama;

import static com.github.jtama.utils.NodeUtils.getListValue;
import static com.github.jtama.utils.NodeUtils.getStringValue;
import static com.github.jtama.utils.StringUtils.toURL;

import java.util.List;
import java.util.function.Function;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import com.github.jtama.utils.StringUtils;

public class ChartMapper {

    public static Chart mapToChart(MappingNode rootNode, Section rootSection) {
        Chart.Builder builder = new Chart.Builder();
        rootNode.getValue().forEach(tuple -> {
            switch (tuple.getKeyNode()) {
                case ScalarNode key -> {
                    switch (key.getValue()) {
                        case "apiVersion" -> builder.setApiVersion(getStringValue(tuple.getValueNode()));
                        case "name" -> builder.setName(getStringValue(tuple.getValueNode()));
                        case "version" -> builder.setVersion(getStringValue(tuple.getValueNode()));
                        case "kubeVersion" -> builder.setKubeVersion(getStringValue(tuple.getValueNode()));
                        case "description" -> builder.setDescription(getStringValue(tuple.getValueNode()));
                        case "type" -> builder.setType(getStringValue(tuple.getValueNode()));
                        case "keywords" ->
                            builder.setKeywords(getListValue((SequenceNode) tuple.getValueNode(), Function.identity()));
                        case "home" -> builder.setHome(toURL(getStringValue(tuple.getValueNode())));
                        case "sources" ->
                            builder.setSources(getListValue((SequenceNode) tuple.getValueNode(), StringUtils::toURL));
                        case "dependencies" ->
                            builder.setDependencies(DependencyMapper.readDependencies((SequenceNode) tuple.getValueNode()));
                        case "maintainers" ->
                            builder.setMaintainers(MaintainersMapper.readMaintainers((SequenceNode) tuple.getValueNode()));
                        case "icon" -> builder.setIcon(toURL(getStringValue(tuple.getValueNode())));
                        case "appVersion" -> builder.setAppVersion(getStringValue(tuple.getValueNode()));
                        case "deprecated" -> builder.setDeprecated(Boolean.valueOf(getStringValue(tuple.getValueNode())));
                        case "annotations" ->
                            builder.setAnnotations(AnnotationsMapper.readMaintainers((SequenceNode) tuple.getValueNode()));

                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + tuple.getKeyNode());
            }
        });
        builder.setSections(List.of(rootSection));
        return builder.build();
    }
}

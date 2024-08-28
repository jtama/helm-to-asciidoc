package com.github.jtama;

import java.util.List;
import java.util.function.Predicate;

import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class ValuesFileMapper {

    public static final String FIRST_SECTION_KEY = "Global";
    private String commentPrefix;

    public ValuesFileMapper(String commentPrefix) {
        this.commentPrefix = commentPrefix;
    }

    public Section readValues(Node node) {
        Section root = new Section(FIRST_SECTION_KEY, null);
        readNode(node, root);
        return root;
    }

    private void readNode(Node node, Section section) {
        switch (node) {
            case ScalarNode scalar ->
                section.properties().add(new Property(scalar.getNodeId().name(), getBlockComments(scalar.getBlockComments()),
                        scalar.getValue(), scalar.getStartMark().getLine()));
            case SequenceNode sequence ->
                section.properties()
                        .add(new Property(sequence.getNodeId().name(), getBlockComments(sequence.getBlockComments()),
                                getDefaultValues(sequence.getValue()), sequence.getStartMark().getLine()));
            case MappingNode sequence -> sequence.getValue().stream()
                    .forEach(tuple -> {
                        ScalarNode key = (ScalarNode) tuple.getKeyNode();
                        Node valueNode = tuple.getValueNode();
                        switch (valueNode) {
                            case ScalarNode value ->
                                section.properties().add(new Property(key.getValue(), getBlockComments(key.getBlockComments()),
                                        value.getValue(), value.getStartMark().getLine()));
                            case SequenceNode value ->
                                section.properties().add(new Property(key.getValue(), getBlockComments(key.getBlockComments()),
                                        getDefaultValues(value.getValue()), value.getStartMark().getLine()));
                            case MappingNode value -> {
                                if (value.getValue().isEmpty()) {
                                    section.properties().add(new Property(key.getValue(),
                                            getBlockComments(key.getBlockComments()), "{}", value.getStartMark().getLine()));
                                    return;
                                }
                                String childName = isFirstSection(section) ? key.getValue()
                                        : section.name() + '.' + key.getValue();
                                Section childSection = new Section(childName, getBlockComments(key.getBlockComments()));
                                readNode(value, childSection);
                                section.childs().add(childSection);
                            }
                            default -> throw new IllegalStateException("Unexpected value: " + tuple.getValueNode());
                        }
                    });
            default -> throw new IllegalStateException("Unexpected value: " + node);
        }
    }

    private static boolean isFirstSection(Section section) {
        return section.name().equals("Global");
    }

    private String getBlockComments(List<CommentLine> lines) {
        return lines == null ? ""
                : String.join(" + " + System.lineSeparator() + System.lineSeparator(), lines.stream().map(CommentLine::getValue)
                        .map(this::cleanComment).filter(Predicate.not(String::isBlank)).toList());
    }

    private String getDefaultValues(List<?> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }
        return switch (nodes.getFirst()) {
            case ScalarNode _ -> getScalarDefaultValues((List<ScalarNode>) nodes);
            case SequenceNode _ -> getSequenceDefaultValues((List<SequenceNode>) nodes);
            case MappingNode _ -> getMappingDefaultValues((List<MappingNode>) nodes);
            default -> throw new IllegalStateException("Unexpected value: " + nodes.getFirst());
        };
    }

    private String getScalarDefaultValues(List<ScalarNode> nodes) {
        return String.join(System.lineSeparator(), nodes.stream().map(ScalarNode::getValue).map(value -> "-" + value).toList());
    }

    private String getSequenceDefaultValues(List<SequenceNode> nodes) {
        return String.join(System.lineSeparator(),
                nodes.stream().map(SequenceNode::getValue).map(value -> "-" + value).toList());
    }

    private String getMappingDefaultValues(List<MappingNode> nodes) {
        return String.join(System.lineSeparator(), nodes.stream().map(MappingNode::getValue).flatMap(List::stream)
                .map(value -> "-" + ((ScalarNode) value.getKeyNode()).getValue()).toList());
    }

    private String cleanComment(String comment) {
        if (!comment.startsWith(commentPrefix))
            return comment;
        return comment.substring(commentPrefix.length()).replaceAll("\\|", "\\\\|").strip();
    }
}
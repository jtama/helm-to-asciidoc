package com.github.jtama;

import java.io.StringWriter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import com.github.jtama.utils.StringUtils;
import picocli.CommandLine;

public class ValuesFileMapper {

    public static final String FIRST_SECTION_KEY = "Global";
    private final Yaml yaml;
    private String commentPrefix;

    public ValuesFileMapper(String commentPrefix) {
        this.commentPrefix = commentPrefix;
        this.yaml = new Yaml();
    }

    public Section readValues(Node node) {
        Section root = new Section.Builder()
                .setName(FIRST_SECTION_KEY)
                .build();
        if (node == null) {
            throw new CommandLine.PicocliException("Values file is empty.");
        }
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
                                getDefaultValues(sequence), sequence.getStartMark().getLine()));
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
                                        getDefaultValues(value), value.getStartMark().getLine()));
                            case MappingNode value -> {
                                Property property = new Property(key.getValue(),
                                        getBlockComments(key.getBlockComments()),
                                        "{}",
                                        UUID.randomUUID(),
                                        !value.getValue().isEmpty(),
                                        value.getStartMark().getLine());
                                section.properties().add(property);
                                if (value.getValue().isEmpty()) {
                                    return;
                                }
                                String childName = isFirstSection(section) ? key.getValue()
                                        : section.name() + '.' + key.getValue();
                                Section childSection = new Section.Builder()
                                        .setName(childName)
                                        .setDescription(getBlockComments(key.getBlockComments()))
                                        .setUUID(property.uuid()).build();
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
                : String.join(" + " + System.lineSeparator(), lines.stream().map(CommentLine::getValue)
                        .map(comment -> cleanComment(comment))
                        .filter(Objects::nonNull).toList());
    }

    private String getDefaultValues(Node node) {
        StringWriter stringWriter = new StringWriter();
        yaml.serialize(node, stringWriter);
        return stringWriter.toString();
    }

    private String cleanComment(String comment) {
        if (comment == null || !comment.startsWith(commentPrefix))
            return null;

        return StringUtils.stripToNull(comment.substring(commentPrefix.length()).replaceAll("\\|", "\\\\|"));
    }
}

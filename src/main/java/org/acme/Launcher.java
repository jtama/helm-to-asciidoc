package org.acme;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import picocli.CommandLine;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class Launcher implements Runnable {

    @CommandLine.Option(names = {"-c", "--chart"}, description = "The chart file path")
    String chartFilePath;

    @CommandLine.Option(names = {"-v", "--values"}, description = "The values file path")
    String valuesFilePath;

    @CommandLine.Option(names = {"-o", "--output"}, description = "The output result file path")
    String outputFilePath;

    @CommandLine.Option(names = {"-cp", "--comment-prefix"}, description = "The comment prefix used in the values path", defaultValue = "#"/*, preprocessor = Launcher.CommentProcess.class*/)
    String commentPrefix;

    @Location("chart.adoc")
    Template chartTemplate;

    @Override
    public void run() {

        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setProcessComments(true);
        Yaml yaml = new Yaml(loaderOptions);
        File yamlFile = new File(valuesFilePath);
        try (FileReader reader = new FileReader(yamlFile)) {
            Node node = yaml.compose(reader);

            Section root = new Section("Global", null);
            readNode(node, root);
            Chart chart = readChart(new File(chartFilePath), root);
            try (FileWriter writer = new FileWriter(outputFilePath)) {
                writer.write(chartTemplate.data(chart).render());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void readNode(Node node, Section section) {
        switch (node) {
            case ScalarNode scalar ->
                    section.properties().add(new Property(scalar.getNodeId().name(), getBlockComments(scalar.getBlockComments()), scalar.getValue()));
            case SequenceNode sequence ->
                    section.properties().add(new Property(sequence.getNodeId().name(), getBlockComments(sequence.getBlockComments()), getDefaultValues(sequence.getValue())));
            case MappingNode sequence -> sequence.getValue().stream()
                    .forEach(tuple -> {
                        ScalarNode key = (ScalarNode) tuple.getKeyNode();
                        Node valueNode = tuple.getValueNode();
                        switch (valueNode) {
                            case ScalarNode value ->
                                    section.properties().add(new Property(key.getValue(), getBlockComments(key.getBlockComments()), value.getValue()));
                            case SequenceNode value ->
                                    section.properties().add(new Property(key.getValue(), getBlockComments(key.getBlockComments()), getDefaultValues(value.getValue())));
                            case MappingNode value -> {
                                if (value.getValue().isEmpty()) {
                                    section.properties().add(new Property(key.getValue(), getBlockComments(key.getBlockComments()), "{}"));
                                    return;
                                }
                                String childName = section.name().equals("Global") ? key.getValue() : section.name() + '.' + key.getValue();
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

    String getBlockComments(List<CommentLine> lines) {
        return lines == null ? "" : String.join(" + " + System.lineSeparator(), lines.stream().map(CommentLine::getValue).map(this::cleanComment).filter(Predicate.not(String::isBlank)).toList());
    }

    String getDefaultValues(List<?> nodes) {
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

    String getScalarDefaultValues(List<ScalarNode> nodes) {
        return String.join(".", nodes.stream().map(ScalarNode::getValue).toList());
    }

    String getSequenceDefaultValues(List<SequenceNode> nodes) {
        return String.join(System.lineSeparator(), nodes.stream().map(SequenceNode::getValue).map(value -> "-" + value).toList());
    }

    String getMappingDefaultValues(List<MappingNode> nodes) {
        return String.join(System.lineSeparator(), nodes.stream().map(MappingNode::getValue).flatMap(List::stream).map(value -> "-" + ((ScalarNode) value.getKeyNode()).getValue()).toList());
    }

    String cleanComment(String comment) {
        return comment.substring(comment.lastIndexOf(commentPrefix) + 1).replaceAll("\\|", "\\\\|").strip();
    }

    Chart readChart(File chartFile, Section rootSection) {
        try (FileReader reader = new FileReader(chartFile)) {
            MappingNode rootNode = (MappingNode) new Yaml().compose(reader);
            return mapToChart(rootNode, rootSection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Chart mapToChart(MappingNode rootNode, Section rootSection) {
        Chart.Builder builder = new Chart.Builder();
        rootNode.getValue().forEach(tuple -> {
            switch (tuple.getKeyNode()) {
                case ScalarNode key -> {
                    switch (key.getValue()) {
                        case "name" -> builder.setName(((ScalarNode) tuple.getValueNode()).getValue());
                        case "description" -> builder.setDescription(((ScalarNode) tuple.getValueNode()).getValue());
                        case "version" -> builder.setVersion(((ScalarNode) tuple.getValueNode()).getValue());
                        case "appVersion" -> builder.setAppVersion(((ScalarNode) tuple.getValueNode()).getValue());
                        case "type" -> builder.setType(((ScalarNode) tuple.getValueNode()).getValue());
                        case "dependencies" -> builder.setDependencies(readDependencies((SequenceNode)tuple.getValueNode()));
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + tuple.getKeyNode());
            }
        });
        builder.setSections(List.of(rootSection));
        return builder.build();
    }

    private List<Dependency> readDependencies(SequenceNode valueNode) {
        return valueNode.getValue().stream()
                .map(this::mapToDependency)
                .toList();
    }

    private Dependency mapToDependency(Node node) {
        if (node instanceof MappingNode mappingNode) {
            Dependency.Builder builder = new Dependency.Builder();
            mappingNode.getValue().forEach(tuple -> {
                switch (tuple.getKeyNode()) {
                    case ScalarNode key -> {
                        switch (key.getValue()) {
                            case "name" -> builder.setName(((ScalarNode) tuple.getValueNode()).getValue());
                            case "version" -> builder.setVersion(((ScalarNode) tuple.getValueNode()).getValue());
                            case "repository" -> builder.setRepository(((ScalarNode) tuple.getValueNode()).getValue());
                            case "condition" -> builder.setCondition(((ScalarNode) tuple.getValueNode()).getValue());
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

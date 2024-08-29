package com.github.jtama;

import static com.github.jtama.ChartMapper.mapToChart;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

import jakarta.inject.Inject;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;

import org.jboss.logging.Logger;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;

import picocli.CommandLine;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class Launcher implements Runnable {

    @CommandLine.Option(names = { "-c", "--chart" }, required = true, description = "The chart file path")
    String chartFilePath;

    @CommandLine.Option(names = { "-v", "--values" }, required = true, description = "The values file path")
    String valuesFilePath;

    @CommandLine.Option(names = { "-o", "--output" }, defaultValue = "output.adoc", description = "The output result file path")
    String outputFilePath;

    @CommandLine.Option(names = { "-cp",
            "--comment-prefix" }, description = "The comment prefix used in the values path", defaultValue = "#", parameterConsumer = Launcher.PrefixConsumer.class)
    String commentPrefix;

    @Location("chart.adoc")
    Template chartTemplate;

    @Inject
    Logger log;

    @Override
    public void run() {

        log.infof("Chart file : %s", chartFilePath);
        log.infof("Value file : %s", valuesFilePath);
        log.infof("Output file : %s", outputFilePath);
        log.infof("Comment prefix : %s", commentPrefix);

        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setProcessComments(true);

        Yaml yaml = new Yaml(loaderOptions);
        File yamlFile = new File(valuesFilePath);
        try (FileReader reader = new FileReader(yamlFile)) {
            Node node = yaml.compose(reader);

            Section root = new ValuesFileMapper(commentPrefix).readValues(node);
            Chart chart = readChart(new File(chartFilePath), root);
            try (FileWriter writer = new FileWriter(outputFilePath)) {
                writer.write(chartTemplate.data(chart).render());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Chart readChart(File chartFile, Section rootSection) {
        try (FileReader reader = new FileReader(chartFile)) {
            MappingNode rootNode = (MappingNode) new Yaml().compose(reader);
            return mapToChart(rootNode, rootSection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class PrefixConsumer implements CommandLine.IParameterConsumer {
        @Override
        public void consumeParameters(Stack<String> args, CommandLine.Model.ArgSpec argSpec,
                CommandLine.Model.CommandSpec cmdSpec) {
            String arg = args.pop();
            argSpec.setValue(arg);
        }
    }
}

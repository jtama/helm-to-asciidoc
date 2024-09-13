package com.github.jtama;

import static com.github.jtama.ChartMapper.mapToChart;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Stack;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import io.quarkus.picocli.runtime.PicocliCommandLineFactory;
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

    @CommandLine.Option(names = { "-i",
            "--include-raw" }, description = "Whether or not the raw value file should be included as reference", defaultValue = "false")
    Boolean includeValuesFile;

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

        process();

    }

    private void process() {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setProcessComments(true);

        Yaml yaml = new Yaml(loaderOptions);
        File yamlFile = new File(valuesFilePath);
        if (!yamlFile.exists()) {
            throw new CommandLine.PicocliException("Values file could not be found: %s".formatted(yamlFile.getAbsolutePath()));
        }
        try (FileReader reader = new FileReader(yamlFile)) {
            Node node = yaml.compose(reader);

            Section root = new ValuesFileMapper(commentPrefix).readValues(node);
            Chart chart = readChart(new File(chartFilePath), root);
            File ouptFile = new File(outputFilePath);
            if (!ouptFile.exists() && !ouptFile.createNewFile()) {
                throw new CommandLine.PicocliException("Couldn't create new file %s".formatted(ouptFile.getAbsolutePath()));
            }
            try (FileWriter writer = new FileWriter(outputFilePath)) {
                if (includeValuesFile) {
                    writer.write(chartTemplate.data("chart", chart,
                            "includeRaw", includeValuesFile,
                            "valuesFileName", yamlFile.getName(),
                            "valuesFileContent", Files.readString(Path.of(valuesFilePath))).render());
                } else {
                    writer.write(chartTemplate.data("chart", chart, "includeRaw", includeValuesFile).render());
                }
            }
        } catch (IOException e) {
            throw new CommandLine.PicocliException("An error occured while processing files: %s".formatted(e.getMessage()));
        }
    }

    private Chart readChart(File chartFile, Section rootSection) {
        if (!chartFile.exists()) {
            throw new CommandLine.PicocliException("Chart file could not be found: %s".formatted(chartFile.getAbsolutePath()));
        }
        try (FileReader reader = new FileReader(chartFile)) {
            MappingNode rootNode = (MappingNode) new Yaml().compose(reader);
            return mapToChart(rootNode, rootSection);
        } catch (IOException e) {
            throw new CommandLine.PicocliException("An error occured while reading chart file: %s".formatted(e.getMessage()));
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

@ApplicationScoped
class PicocliCustomConfiguration {

    @Produces
    CommandLine customCommandLine(PicocliCommandLineFactory factory) {
        return factory.create().setExecutionExceptionHandler((ex, cmd, fullParseResult) -> {
            cmd.getErr().println(cmd.getColorScheme().errorText(ex.getMessage()));
            return cmd.getExitCodeExceptionMapper() != null
                    ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                    : cmd.getCommandSpec().exitCodeOnExecutionException();
        });
    }
}

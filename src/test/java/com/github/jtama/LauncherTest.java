package com.github.jtama;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;

import org.assertj.core.data.Index;
import org.junit.jupiter.api.Test;

@QuarkusMainTest
class LauncherTest {

    @Test
    public void shouldValidateCmdLineArgs(QuarkusMainLauncher launcher) {
        LaunchResult launch = launcher.launch();
        assertThat(launch.exitCode()).isGreaterThan(0);
    }

    @Test
    public void shouldCheckEmptyValuesFile(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("-c", getClass().getClassLoader().getResource("Chart.yaml").getPath(), "-v",
                getClass().getClassLoader().getResource("empty_values.yaml").getPath());
        assertSoftly(softly -> {
            softly.assertThat(result.exitCode()).isGreaterThan(0);
            softly.assertThat(result.getErrorStream()).hasSizeGreaterThan(0);
            softly.assertThat(result.getErrorStream().get(0)).contains("picocli.CommandLine$PicocliException: Values file is empty.");
        });
    }

    @Test
    public void shouldCheckEmptyChartFile(QuarkusMainLauncher launcher) {

        LaunchResult result = launcher.launch("-c", getClass().getClassLoader().getResource("empty_Chart.yaml").getPath(), "-v", getClass().getClassLoader().getResource("values.yaml").getPath());
        assertSoftly(softly -> {
                    softly.assertThat(result.exitCode()).isGreaterThan(0);
                    softly.assertThat(result.getErrorStream()).hasSizeGreaterThan(0);
                    softly.assertThat(result.getErrorStream().get(0)).contains("picocli.CommandLine$PicocliException: Chart file is empty.");
                });
    }

    @Test
    public void shouldLaunch(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("-c", getClass().getClassLoader().getResource("Chart.yaml").getPath(), "-v",
                getClass().getClassLoader().getResource("values.yaml").getPath());
        assertThat(result.exitCode()).isEqualTo(0);
    }
}
import org.acme.Dependency;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DependencyUT {

    @Test
    public void dependency_should_be_initialize_with_default_condition_if_not_provided() {
        Dependency dependency = new Dependency("name", "repository", "version", null);
        assertEquals("N/A",dependency.condition());
    }
}

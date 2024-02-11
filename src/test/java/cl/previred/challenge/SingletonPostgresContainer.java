package cl.previred.challenge;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class SingletonPostgresContainer {
    private static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"));
        postgresContainer.start();
    }

    public static PostgreSQLContainer<?> getInstance() {
        return postgresContainer;
    }

    // Evita la instanciación
    private SingletonPostgresContainer() {}
}

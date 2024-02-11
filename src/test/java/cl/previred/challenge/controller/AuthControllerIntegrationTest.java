package cl.previred.challenge.controller;

import static org.assertj.core.api.Assertions.assertThat;

import cl.previred.challenge.SingletonPostgresContainer;
import cl.previred.challenge.controller.dto.ApiErrorResponse;
import cl.previred.challenge.controller.dto.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class AuthControllerIntegrationTest {

    static final String LOGIN_URL = "/api/auth/login";

    @Autowired
    private WebTestClient webTestClient;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer postgres = SingletonPostgresContainer.getInstance();

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    public void cleanup(){
        // logic
    }

    //  Test login endpoint
    @Test
    public void shouldReturnJWTToken_WhenUserIsRegistered() {


        String loginRequest = """
        {
          "username": "admin",
          "password": "123456"
        }
        """;
        LoginResponse loginResponse = webTestClient
                .post().uri(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(LoginResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.token()).isNotBlank();
    }

    @Test
    public void shouldReturnBadCredential() {

        String loginRequestWithWrongPassword = """
        {
          "username": "admin",
          "password": "12345678910"
        }
        """;
        ApiErrorResponse errorResponse = webTestClient
                .post().uri(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequestWithWrongPassword)
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody(ApiErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.errorCode()).isEqualTo(401);
        assertThat(errorResponse.description()).isEqualTo("Invalid username or password");
    }

    @Test
    public void shouldReturnUnauthorized_WhenUserNotRegistered() {
        String request = """
        {
          "username": "sebastian",
          "password": "123456"
        }
        """;
        ApiErrorResponse errorResponse = webTestClient
                .post().uri(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody(ApiErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.errorCode()).isEqualTo(401);
        assertThat(errorResponse.description()).isEqualTo("User does not exist, username: sebastian");
    }
}

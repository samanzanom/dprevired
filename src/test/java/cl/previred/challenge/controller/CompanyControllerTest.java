package cl.previred.challenge.controller;

import cl.previred.challenge.controller.dto.ApiErrorResponse;
import cl.previred.challenge.controller.dto.CompanyResponse;
import cl.previred.challenge.controller.dto.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class CompanyControllerTest {

    static final String COMPANY_URL = "/api/company/";
    static final String LOGIN_URL = "/api/auth/login";

    @Autowired
    private WebTestClient webTestClient;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer postgres = new PostgreSQLContainer<>("postgres:13");

    @BeforeEach
    public void cleanup(){
        // logic
    }

    @Test
    public void shouldCompanyCreatedNoAuthenticated() {


        String creationRequest = """
        {
          "rut": "1-9",
          "companyName": "previred"
        }
        """;
        webTestClient
                .post().uri(COMPANY_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(creationRequest)
                .exchange()
                .expectStatus()
                .isForbidden()
                .expectBody(CompanyResponse.class)
                .returnResult()
                .getResponseBody();

    }

    @Test
    public void shouldCompanyCreatedAuthenticated() {
        // First, get token
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
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(loginResponse).isNotNull();
        String token = loginResponse.token();
        assertThat(token).isNotBlank();

        // Ahora, usa el token para realizar una solicitud autenticada
        String creationRequest = """
        {
          "rut": "1-9",
          "companyName": "previred"
        }
        """;
        webTestClient
                .post().uri(COMPANY_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyResponse.class)
                .consumeWith(response -> {
                    assertThat(response.getResponseBody()).isNotNull();
                    assertThat(response.getResponseBody().rut()).isNotNull().isNotBlank();
                    assertThat(response.getResponseBody().uniqueIdentifier()).isNotNull().isNotBlank();
                    assertThat(response.getResponseBody().companyName()).isNotNull().isNotBlank();
                });
    }

    @Test
    public void shouldCompanyCreatedFailNoCompanyName() {
        // First, get token
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
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(loginResponse).isNotNull();
        String token = loginResponse.token();
        assertThat(token).isNotBlank();

        // Ahora, usa el token para realizar una solicitud autenticada
        String creationRequest = """
        {
          "rut": "1-9"
        }
        """;
        ApiErrorResponse errorResponse =webTestClient
                .post().uri(COMPANY_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApiErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.errorCode()).isEqualTo(400);
        assertThat(errorResponse.description()).isEqualTo("Validation of request failed: companyName: Company name cannot be blank");

    }

    @Test
    public void shouldCompanyCreatedFailCompanyNameLength() {
        // First, get token
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
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(loginResponse).isNotNull();
        String token = loginResponse.token();
        assertThat(token).isNotBlank();

        // Ahora, usa el token para realizar una solicitud autenticada
        String creationRequest = """
        {
          "rut": "1-9",
          "companyName": "prev"
        }
        """;
        ApiErrorResponse errorResponse =webTestClient
                .post().uri(COMPANY_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApiErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.errorCode()).isEqualTo(400);
        assertThat(errorResponse.description()).isEqualTo("Validation of request failed: companyName: Company name must be between 6 and 50 characters");

    }

    @Test
    public void shouldCompanyCreatedFailNoRut() {
        // First, get token
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
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(loginResponse).isNotNull();
        String token = loginResponse.token();
        assertThat(token).isNotBlank();

        // Ahora, usa el token para realizar una solicitud autenticada
        String creationRequest = """
        {
          "companyName": "previred"
        }
        """;
        ApiErrorResponse errorResponse =webTestClient
                .post().uri(COMPANY_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApiErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.errorCode()).isEqualTo(400);
        assertThat(errorResponse.description()).isEqualTo("Validation of request failed: rut: Invalid Rut, rut: Rut cannot be blank");

    }

    @Test
    public void shouldCompanyCreatedFailInvalidRut() {
        // First, get token
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
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(loginResponse).isNotNull();
        String token = loginResponse.token();
        assertThat(token).isNotBlank();

        // Ahora, usa el token para realizar una solicitud autenticada
        String creationRequest = """
        {
          "rut": "19",
          "companyName": "previred"
        }
        """;
        ApiErrorResponse errorResponse =webTestClient
                .post().uri(COMPANY_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApiErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.errorCode()).isEqualTo(400);
        assertThat(errorResponse.description()).isEqualTo("Validation of request failed: rut: Invalid Rut");

    }
}

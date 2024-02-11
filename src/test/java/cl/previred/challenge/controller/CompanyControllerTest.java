package cl.previred.challenge.controller;

import cl.previred.challenge.SingletonPostgresContainer;
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
    private static PostgreSQLContainer postgres = SingletonPostgresContainer.getInstance();

    @BeforeEach
    public void cleanup(){
        // logic
    }

    // Create
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
    public void shouldCompanyCreatedDuplicated() {
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

        String creationRequest = """
                {
                  "rut": "1-9",
                  "companyName": "previred"
                }
                """;
        ApiErrorResponse errorResponse = webTestClient
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
        assertThat(errorResponse.description()).contains("Duplicated");
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
        assertThat(errorResponse.description()).contains("rut: Invalid Rut");
        assertThat(errorResponse.description()).contains("rut: Rut cannot be blank");

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

    // Update
    @Test
    public void shouldCompanyUpdateNoAuthenticated() {


        String creationRequest = """
        {
          "rut": "1-9",
          "companyName": "previred"
        }
        """;
        webTestClient
                .put().uri(COMPANY_URL)
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
    public void shouldCompanyUpdateAuthenticated() {
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

        String creationRequest = """
        {
          "rut": "2-7",
          "companyName": "previred"
        }
        """;
        CompanyResponse companyReponse = webTestClient
                .post().uri(COMPANY_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyResponse.class)
                .returnResult()
                .getResponseBody();

        String updateRequest = """
        {
          "rut": "2-7",
          "companyName": "previred2"
        }
        """;
        CompanyResponse updateResponse = webTestClient
                .put().uri(COMPANY_URL + companyReponse.uniqueIdentifier())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(updateResponse.companyName()).isEqualTo("previred2");
    }

    @Test
    public void shouldCompanyUpdateWhitOutId() {
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

        String updateRequest = """
        {
          "rut": "2-7",
          "companyName": "previred2"
        }
        """;
        webTestClient
                .put().uri(COMPANY_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().is5xxServerError();

    }


    @Test
    public void shouldCompanyUpdateFailNoCompanyName() {
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

        String updateRequest = """
        {
          "rut": "2-7"
        }
        """;
        ApiErrorResponse errorResponse = webTestClient
                .put().uri(COMPANY_URL + "123")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(updateRequest)
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
    public void shouldCompanyUpdateFailCompanyNameLength() {
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

        String updateRequest = """
        {
          "rut": "2-7",
          "companyName": "123"
        }
        """;
        ApiErrorResponse errorResponse = webTestClient
                .put().uri(COMPANY_URL + "123")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(updateRequest)
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
    public void shouldCompanyUpdateFailNoRut() {
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

        String updateRequest = """
        {
          "companyName": "previred"
        }
        """;
        ApiErrorResponse errorResponse = webTestClient
                .put().uri(COMPANY_URL + "123")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApiErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.errorCode()).isEqualTo(400);
        assertThat(errorResponse.description()).contains("rut: Invalid Rut");
        assertThat(errorResponse.description()).contains("rut: Rut cannot be blank");


    }

    @Test
    public void shouldCompanyUpdatedFailInvalidRut() {
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

        String updateRequest = """
        {
          "rut": "19",
          "companyName": "previred"
        }
        """;
        ApiErrorResponse errorResponse = webTestClient
                .put().uri(COMPANY_URL + "123")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(updateRequest)
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

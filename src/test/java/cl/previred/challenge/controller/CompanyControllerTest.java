package cl.previred.challenge.controller;

import cl.previred.challenge.SingletonPostgresContainer;
import cl.previred.challenge.controller.dto.ApiErrorResponse;
import cl.previred.challenge.controller.dto.CompanyPageResponse;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class CompanyControllerTest {

    static final String LOGIN_URL = "/api/auth/login";

    @Autowired
    private WebTestClient webTestClient;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgres = SingletonPostgresContainer.getInstance();

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
                .post().uri("/api/company/")
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
        String token = obtainAccessToken();

        String creationRequest = """
        {
          "rut": "1-9",
          "companyName": "previred"
        }
        """;
        webTestClient
                .post().uri("/api/company")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyResponse.class)
                .consumeWith(response -> {
                    assertThat(response.getResponseBody()).isNotNull();
                    assertThat(response.getResponseBody().rut()).isNotNull().isNotBlank();
                    assertThat(response.getResponseBody().id()).isNotNull().isNotBlank();
                    assertThat(response.getResponseBody().companyName()).isNotNull().isNotBlank();
                });
    }

    @Test
    public void shouldCompanyCreatedDuplicated() {
        // First, get token
        String token = obtainAccessToken();

        String creationRequest = """
                {
                  "rut": "1-9",
                  "companyName": "previred"
                }
                """;
        ApiErrorResponse errorResponse = webTestClient
                .post().uri("/api/company")
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
        String token = obtainAccessToken();

        String creationRequest = """
        {
          "rut": "1-9"
        }
        """;
        ApiErrorResponse errorResponse =webTestClient
                .post().uri("/api/company")
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
        String token = obtainAccessToken();

        String creationRequest = """
        {
          "rut": "1-9",
          "companyName": "prev"
        }
        """;
        ApiErrorResponse errorResponse =webTestClient
                .post().uri("/api/company")
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
        String token = obtainAccessToken();

        String creationRequest = """
        {
          "companyName": "previred"
        }
        """;
        ApiErrorResponse errorResponse =webTestClient
                .post().uri("/api/company")
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
        String token = obtainAccessToken();

        String creationRequest = """
        {
          "rut": "19",
          "companyName": "previred"
        }
        """;
        ApiErrorResponse errorResponse =webTestClient
                .post().uri("/api/company")
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


        String updateRequest = """
        {
          "rut": "1-9",
          "companyName": "previred"
        }
        """;
        webTestClient
                .put().uri("/api/company")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus()
                .isForbidden()
                .expectBody(CompanyResponse.class)
                .returnResult()
                .getResponseBody();

    }

    @Test
    public void shouldCompanyUpdatedSuccessfully() {
        /// First, get token
        String token = obtainAccessToken();

        String creationRequest = """
        {
          "rut": "2-7",
          "companyName": "previred"
        }
        """;
        CompanyResponse companyResponse = webTestClient
                .post().uri("/api/company")
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
        assert companyResponse != null;
        CompanyResponse updateResponse = webTestClient
                .put().uri("/api/company/{id}",companyResponse.id())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyResponse.class)
                .returnResult()
                .getResponseBody();

        assert updateResponse != null;
        assertThat(updateResponse.companyName()).isEqualTo("previred2");
    }

    @Test
    public void shouldCompanyUpdateWhitOutId() {
        // First, get token
        String token = obtainAccessToken();

        String updateRequest = """
        {
          "rut": "2-7",
          "companyName": "previred2"
        }
        """;
        webTestClient
                .put().uri("/api/company")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().is5xxServerError();

    }


    @Test
    public void shouldCompanyUpdateFailNoCompanyName() {
        // First, get token
        String token = obtainAccessToken();

        String updateRequest = """
        {
          "rut": "2-7"
        }
        """;
        ApiErrorResponse errorResponse = webTestClient
                .put().uri("/api/company/{id}", "123")
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
        String token = obtainAccessToken();

        String updateRequest = """
        {
          "rut": "2-7",
          "companyName": "123"
        }
        """;
        ApiErrorResponse errorResponse = webTestClient
                .put().uri("/api/company/{id}" ,"123")
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
        String token = obtainAccessToken();

        String updateRequest = """
        {
          "companyName": "previred"
        }
        """;
        ApiErrorResponse errorResponse = webTestClient
                .put().uri("/api/company/{id}" ,"123")
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

        String token = obtainAccessToken();

        String updateRequest = """
        {
          "rut": "19",
          "companyName": "previred"
        }
        """;
        ApiErrorResponse errorResponse = webTestClient
                .put().uri("/api/company/{id}" , "123")
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

    @Test
    public void shouldReturn204WhenCompanyDeletedSuccessfully() {
        String token = obtainAccessToken();

        String creationRequest = """
        {
          "rut": "15722520-0",
          "companyName": "previred"
        }
        """;
        CompanyResponse companyResponse = webTestClient
                .post().uri("/api/company")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyResponse.class)
                .returnResult()
                .getResponseBody();

        assert companyResponse != null;
        webTestClient.delete().uri("/api/company/{id}", companyResponse.id())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isNoContent(); //204
    }

    @Test
    public void shouldReturn404WhenCompanyNotFound() {
        String token = obtainAccessToken();
        String nonExistentCompanyId = "non-existent-id";

        webTestClient.delete().uri("/api/company/{id}", nonExistentCompanyId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound(); // Verifica que el estado HTTP sea 404
    }

    @Test
    public void shouldDeleteReturn403WhenNotAuthenticated() {
        String nonAuthenticatedCompanyId = "123";

        webTestClient.delete().uri("/api/company/{id}", nonAuthenticatedCompanyId)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void getCompany_ShouldReturnCompanyDetails_WhenCompanyExists() {
        String token = obtainAccessToken();

        String creationRequest = """
        {
          "rut": "15722520-0",
          "companyName": "previred"
        }
        """;
        CompanyResponse creationResponse = webTestClient
                .post().uri("/api/company")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyResponse.class)
                .returnResult()
                .getResponseBody();

        assert creationResponse != null;
        CompanyResponse getResponse = webTestClient.get().uri("/api/company/{id}", creationResponse.id())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(getResponse).isNotNull();
        assertThat(getResponse.id()).isEqualTo(creationResponse.id());
    }

    @Test
    public void getCompany_ShouldReturnNotFound_WhenCompanyDoesNotExist() {
        String nonExistentCompanyId = "nonExistentId";
        String token = obtainAccessToken();

        webTestClient.get().uri("/api/company/{id}", nonExistentCompanyId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void shouldGetReturn403WhenNotAuthenticated() {
        String nonExistentCompanyId = "nonExistentId";

        webTestClient.get().uri("/api/company/{id}", nonExistentCompanyId)
                .exchange()
                .expectStatus().isForbidden();

    }

    @Test
    public void testListCompaniesReturnsPageInfoAndCompanies() {

        String token = obtainAccessToken();

        CompanyPageResponse pageResponse = webTestClient.get().uri("/api/company?page=1&size=5")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyPageResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(pageResponse).isNotNull();
        assertThat(pageResponse.getCurrentPage()).isEqualTo(1);
        assertThat(pageResponse.getTotalItems()).isGreaterThan(0);
        assertThat(pageResponse.getTotalPages()).isGreaterThan(0);
        assertThat(pageResponse.getCompanies()).isNotNull();
        assertThat(pageResponse.getCompanies()).isInstanceOf(List.class);
        assertThat(pageResponse.getCompanies()).hasSizeGreaterThan(0);

    }

    private String obtainAccessToken() {
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

        return token;
    }
}

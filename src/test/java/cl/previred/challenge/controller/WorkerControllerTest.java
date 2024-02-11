package cl.previred.challenge.controller;

import cl.previred.challenge.SingletonPostgresContainer;
import cl.previred.challenge.controller.dto.*;
import cl.previred.challenge.service.CompanyService;
import cl.previred.challenge.service.WorkerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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
class WorkerControllerTest {

    static final String LOGIN_URL = "/api/auth/login";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private CompanyService companyService;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgres = SingletonPostgresContainer.getInstance();

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

    // Create
    @Test
    void shouldWorkerCreatedNoAuthenticated() {
        String creationRequest = """
                {
                  "rut": "1-9",
                  "names": "Sebastian Amadiel",
                  "firstSurname": "Manzano",
                  "secondSurname": "Manzano",
                  "companyId": "pre20240211014921711"
                }
        """;
        webTestClient
                .post().uri("/api/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(creationRequest)
                .exchange()
                .expectStatus()
                .isForbidden()
                .expectBody(WorkerResponse.class)
                .returnResult()
                .getResponseBody();

    }



    @Test
    void shouldWorkerCreatedAuthenticated() {
        // First, get token
        String token = obtainAccessToken();

        CompanyResponse companyResponse = this.createCompany(token);
        String companyId = companyResponse.id();
        String creationWorkerRequest = String.format("""
            {
              "rut": "1-9",
              "names": "Sebastian Amadiel",
              "firstSurname": "Manzano",
              "secondSurname": "Manzano",
              "companyId": "%s"
            }
        """, companyId);
        webTestClient
                .post().uri("/api/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationWorkerRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WorkerResponse.class)
                .consumeWith(response -> {
                    assertThat(response.getResponseBody()).isNotNull();
                    assertThat(response.getResponseBody().rut()).isNotNull().isNotBlank();
                    assertThat(response.getResponseBody().id()).isNotNull();
                    assertThat(response.getResponseBody().names()).isNotNull().isNotBlank();
                    assertThat(response.getResponseBody().companyName()).isNotNull().isNotBlank();
                    workerService.delete(response.getResponseBody().id());
                    companyService.delete(companyId);
                });
    }


    @Test
    void shouldWorkerCreatedDuplicated() {
        // First, get token
        String token = obtainAccessToken();

        CompanyResponse companyResponse = this.createCompany(token);
        String companyId = companyResponse.id();

        String creationWorkerRequest = String.format("""
            {
              "rut": "1-9",
              "names": "Sebastian Amadiel",
              "firstSurname": "Manzano",
              "secondSurname": "Manzano",
              "companyId": "%s"
            }
        """, companyId);
        WorkerResponse workerResponse = webTestClient
                .post().uri("/api/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationWorkerRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WorkerResponse.class)
                .returnResult()
                .getResponseBody();

        ApiErrorResponse errorResponse =  webTestClient
                .post().uri("/api/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationWorkerRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApiErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.errorCode()).isEqualTo(400);
        assertThat(errorResponse.description()).contains("Duplicated");

        workerService.delete(workerResponse.id());
        companyService.delete(companyId);
    }


    @Test
    void shouldWorkerCreatedFailNoName() {
        // First, get token
        String token = obtainAccessToken();

        CompanyResponse companyResponse = this.createCompany(token);
        String companyId = companyResponse.id();

        String creationWorkerRequest = String.format("""
            {
              "rut": "1-9",
              "firstSurname": "Manzano",
              "secondSurname": "Manzano",
              "companyId": "%s"
            }
        """, companyId);


        ApiErrorResponse errorResponse =webTestClient
                .post().uri("/api/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationWorkerRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApiErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.errorCode()).isEqualTo(400);
        assertThat(errorResponse.description()).isEqualTo("Validation of request failed: names: Names cannot be blank");

        companyService.delete(companyId);
    }


    @Test
    void shouldWorkerCreatedFailNoRut() {
        // First, get token
        String token = obtainAccessToken();

        CompanyResponse companyResponse = this.createCompany(token);
        String companyId = companyResponse.id();

        String creationWorkerRequest = String.format("""
            {
              "names": "Sebastian Amadiel",
              "firstSurname": "Manzano",
              "secondSurname": "Manzano",
              "companyId": "%s"
            }
        """, companyId);

        ApiErrorResponse errorResponse =webTestClient
                .post().uri("/api/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationWorkerRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApiErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.errorCode()).isEqualTo(400);
        assertThat(errorResponse.description()).contains("rut: Invalid Rut");
        assertThat(errorResponse.description()).contains("rut: Rut cannot be blank");

        companyService.delete(companyId);
    }


    @Test
    void shouldCompanyCreatedFailInvalidRut() {
        // First, get token
        String token = obtainAccessToken();

        CompanyResponse companyResponse = this.createCompany(token);
        String companyId = companyResponse.id();

        String creationWorkerRequest = String.format("""
            {
              "rut": "19",
              "names": "Sebastian Amadiel",
              "firstSurname": "Manzano",
              "secondSurname": "Manzano",
              "companyId": "%s"
            }
        """, companyId);

        ApiErrorResponse errorResponse =webTestClient
                .post().uri("/api/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationWorkerRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApiErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.errorCode()).isEqualTo(400);
        assertThat(errorResponse.description()).isEqualTo("Validation of request failed: rut: Invalid Rut");

        companyService.delete(companyId);

    }


    // Update
    @Test
    void shouldCompanyUpdateNoAuthenticated() {

        // First, get token
        String token = obtainAccessToken();

        CompanyResponse companyResponse = this.createCompany(token);
        String companyId = companyResponse.id();
        String updateWorkerRequest = String.format("""
            {
              "rut": "1-9",
              "names": "Sebastian Amadiel",
              "firstSurname": "Manzano",
              "secondSurname": "Manzano",
              "companyId": "%s"
            }
        """, companyId);

        webTestClient
                .put().uri("/api/company")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateWorkerRequest)
                .exchange()
                .expectStatus()
                .isForbidden()
                .expectBody(CompanyResponse.class)
                .returnResult()
                .getResponseBody();

        companyService.delete(companyId);

    }



    @Test
    void shouldWorkerUpdatedSuccessfully() {
        /// First, get token
        String token = obtainAccessToken();

        CompanyResponse companyResponse = this.createCompany(token);
        String companyId = companyResponse.id();

        String creationWorkerRequest = String.format("""
            {
              "rut": "1-9",
              "names": "Sebastian Amadiel",
              "firstSurname": "Manzano",
              "secondSurname": "Manzano",
              "companyId": "%s"
            }
        """, companyId);
        WorkerResponse workerResponse = webTestClient
                .post().uri("/api/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationWorkerRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WorkerResponse.class)
                .returnResult()
                .getResponseBody();

        String updateRequest = String.format("""
        {
            "rut": "1-9",
            "names": "Sebastian",
            "firstSurname": "Manzano",
            "secondSurname": "Manzano",
            "companyId": "%s"
        }
        """, companyId);
        assert workerResponse != null;
        WorkerResponse updateResponse = webTestClient
                .put().uri("/api/worker/{id}",workerResponse.id())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WorkerResponse.class)
                .returnResult()
                .getResponseBody();

        assert updateResponse != null;
        assertThat(updateResponse.names()).isEqualTo("Sebastian");

        companyService.delete(companyId);
    }


    @Test
    void shouldCompanyUpdateWhitOutId() {
        // First, get token
        String token = obtainAccessToken();

        CompanyResponse companyResponse = this.createCompany(token);
        String companyId = companyResponse.id();

        String updateRequest = String.format("""
        {
            "rut": "1-9",
            "names": "Sebastian",
            "firstSurname": "Manzano",
            "secondSurname": "Manzano",
            "companyId": "%s"
        }
        """, companyId);
        webTestClient
                .put().uri("/api/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().is5xxServerError();

        companyService.delete(companyId);

    }

    @Test
    void shouldCompanyUpdateFailNoRut() {
        // First, get token
        String token = obtainAccessToken();

        CompanyResponse companyResponse = this.createCompany(token);
        String companyId = companyResponse.id();

        String updateRequest = String.format("""
        {
            "names": "Sebastian",
            "firstSurname": "Manzano",
            "secondSurname": "Manzano",
            "companyId": "%s"
        }
        """, companyId);
        ApiErrorResponse errorResponse = webTestClient
                .put().uri("/api/worker/{id}" ,"123")
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

        companyService.delete(companyId);
    }


    @Test
    void shouldCompanyUpdatedFailInvalidRut() {

        String token = obtainAccessToken();

        CompanyResponse companyResponse = this.createCompany(token);
        String companyId = companyResponse.id();

        String updateRequest = String.format("""
            {
              "rut": "19",
              "names": "Sebastian Amadiel",
              "firstSurname": "Manzano",
              "secondSurname": "Manzano",
              "companyId": "%s"
            }
        """, companyId);

        ApiErrorResponse errorResponse = webTestClient
                .put().uri("/api/worker/{id}" , "123")
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

        companyService.delete(companyId);
    }


    @Test
    void shouldReturn204WhenCompanyDeletedSuccessfully() {
        String token = obtainAccessToken();

        CompanyResponse companyResponse = this.createCompany(token);
        String companyId = companyResponse.id();

        String creationWorkerRequest = String.format("""
            {
              "rut": "1-9",
              "names": "Sebastian Amadiel",
              "firstSurname": "Manzano",
              "secondSurname": "Manzano",
              "companyId": "%s"
            }
        """, companyId);
        WorkerResponse workerResponse = webTestClient
                .post().uri("/api/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationWorkerRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WorkerResponse.class)
                .returnResult()
                .getResponseBody();

        assert workerResponse != null;
        webTestClient.delete().uri("/api/worker/{id}", workerResponse.id())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isNoContent(); //204

        companyService.delete(companyId);
    }

    @Test
    void shouldReturn404WhenCompanyNotFound() {
        String token = obtainAccessToken();
        Long nonExistentCompanyId = 10L;

        webTestClient.delete().uri("/api/worker/{id}", nonExistentCompanyId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound(); // Verifica que el estado HTTP sea 404
    }

    @Test
    void shouldDeleteReturn403WhenNotAuthenticated() {
        Long nonAuthenticatedCompanyId = 123L;

        webTestClient.delete().uri("/api/worker/{id}", nonAuthenticatedCompanyId)
                .exchange()
                .expectStatus().isForbidden();
    }


    @Test
    void getCompany_ShouldReturnWorkerDetails_WhenWorkerExists() {
        String token = obtainAccessToken();

        CompanyResponse companyResponse = this.createCompany(token);
        String companyId = companyResponse.id();

        String creationWorkerRequest = String.format("""
            {
              "rut": "1-9",
              "names": "Sebastian Amadiel",
              "firstSurname": "Manzano",
              "secondSurname": "Manzano",
              "companyId": "%s"
            }
        """, companyId);
        WorkerResponse workerResponse = webTestClient
                .post().uri("/api/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationWorkerRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WorkerResponse.class)
                .returnResult()
                .getResponseBody();

        assert workerResponse != null;
        WorkerResponse getResponse = webTestClient.get().uri("/api/worker/{id}", workerResponse.id())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WorkerResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(getResponse).isNotNull();
        assertThat(getResponse.id()).isEqualTo(workerResponse.id());

        companyService.delete(companyId);
    }

    @Test
    void getCompany_ShouldReturnNotFound_WhenWorkerDoesNotExist() {
        Long nonExistentCompanyId = 100L;
        String token = obtainAccessToken();

        webTestClient.get().uri("/api/worker/{id}", nonExistentCompanyId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldGetReturn403WhenNotAuthenticated() {
        Long nonExistentCompanyId = 200L;

        webTestClient.get().uri("/api/worker/{id}", nonExistentCompanyId)
                .exchange()
                .expectStatus().isForbidden();

    }


    @Test
    void testListWorkersReturnsPageInfoAndWorkers() {

        String token = obtainAccessToken();

        CompanyResponse companyResponse = this.createCompany(token);
        String companyId = companyResponse.id();

        String creationWorkerRequest = String.format("""
            {
              "rut": "1-9",
              "names": "Sebastian Amadiel",
              "firstSurname": "Manzano",
              "secondSurname": "Manzano",
              "companyId": "%s"
            }
        """, companyId);
        webTestClient
                .post().uri("/api/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationWorkerRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WorkerResponse.class)
                .returnResult()
                .getResponseBody();

        WorkerPageResponse pageResponse = webTestClient.get().uri("/api/worker?page=1&size=5")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WorkerPageResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(pageResponse).isNotNull();
        assertThat(pageResponse.getCurrentPage()).isEqualTo(1);
        assertThat(pageResponse.getTotalItems()).isGreaterThan(0);
        assertThat(pageResponse.getTotalPages()).isGreaterThan(0);
        assertThat(pageResponse.getWorkers()).isNotNull();
        assertThat(pageResponse.getWorkers()).isInstanceOf(List.class);
        assertThat(pageResponse.getWorkers()).hasSizeGreaterThan(0);

        companyService.delete(companyId);

    }

    @Test
    void testListWorkersCompanyReturnsPageInfoAndWorkers() {

        String token = obtainAccessToken();

        CompanyResponse companyResponse = this.createCompany(token);
        String companyId = companyResponse.id();

        String creationWorkerRequest = String.format("""
            {
              "rut": "1-9",
              "names": "Sebastian Amadiel",
              "firstSurname": "Manzano",
              "secondSurname": "Manzano",
              "companyId": "%s"
            }
        """, companyId);
        webTestClient
                .post().uri("/api/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationWorkerRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WorkerResponse.class)
                .returnResult()
                .getResponseBody();

        WorkerPageResponse pageResponse = webTestClient.get().uri("/api/worker?page=1&size=5&companyId={companyId}",companyId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WorkerPageResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(pageResponse).isNotNull();
        assertThat(pageResponse.getCurrentPage()).isEqualTo(1);
        assertThat(pageResponse.getTotalItems()).isGreaterThan(0);
        assertThat(pageResponse.getTotalPages()).isGreaterThan(0);
        assertThat(pageResponse.getWorkers()).isNotNull();
        assertThat(pageResponse.getWorkers()).isInstanceOf(List.class);
        assertThat(pageResponse.getWorkers()).hasSizeGreaterThan(0);

        companyService.delete(companyId);

    }

    private String obtainAccessToken() {
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

    private CompanyResponse  createCompany(String token) {
        String creationCompanyRequest = """
        {
          "rut": "1-9",
          "companyName": "previred"
        }
        """;
        return webTestClient
                .post().uri("/api/company")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(creationCompanyRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyResponse.class)
                .returnResult()
                .getResponseBody();
    }


}

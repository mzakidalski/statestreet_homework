package com.statestreet.carreservation.controller.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.jpa.hibernate.ddl-auto=create",
                "spring.jpa.defer-datasource-initialization=true"
        }
)
@Sql(scripts = "/test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String URL = "http://localhost:{port}/v1/users";
    private static final String URL_WITH_ID = URL + "/{id}";

    @Test
    void getAllUsersWorksCorrectly() {

        ResponseEntity<UserDto[]> response = restTemplate.getForEntity(URL, UserDto[].class, port);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto[] actual = response.getBody();

        assertThat(actual).isNotNull();
        List<UserDto> expected = List.of(
                new UserDto(1, "John",   "Doe"),
                new UserDto(2, "Jane",   "Smith"),
                new UserDto(3, "Carlos", "Gonzalez")
        );

        assertThat(List.of(actual))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(expected);
    }

    @Test
    void insertUserWorksCorrectly() {
        UserDto request = new UserDto(0, "Alice", "Smith");

        ResponseEntity<UserDto> postResponse = restTemplate.postForEntity(
                URL,
                request,
                UserDto.class,
                Map.of("port", port)
        );

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto created = postResponse.getBody();
        assertCorrectlyInserted(created);


        ResponseEntity<UserDto> retrieveResponse = restTemplate.getForEntity(
                URL_WITH_ID,
                UserDto.class,
                Map.of("port", port, "id", created.getId())
        );
        assertThat(retrieveResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto fetched = retrieveResponse.getBody();
        assertThat(fetched).usingRecursiveComparison().isEqualTo(created);

    }

    private void assertCorrectlyInserted(UserDto created) {
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
    }
}
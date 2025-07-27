package com.statestreet.carreservation.controller.user;

import com.statestreet.carreservation.TestUtils;
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
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
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
    void retrieveAllUsersWorksCorrectly() {

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

        UserDto created = postResponse.getBody();
        TestUtils.assertOperationSucceeded(created, postResponse.getStatusCode());


        ResponseEntity<UserDto> retrieveResponse = restTemplate.getForEntity(
                URL_WITH_ID,
                UserDto.class,
                Map.of("port", port, "id", created.getId())
        );
        UserDto fetched = retrieveResponse.getBody();
        TestUtils.assertOperationSucceeded(fetched, postResponse.getStatusCode());


        assertThat(fetched).usingRecursiveComparison().isEqualTo(created);
    }

    @Test
    void updateUserWorksCorrectly() {
        ResponseEntity<UserDto> retrieveResponse = restTemplate.getForEntity(
                URL_WITH_ID,
                UserDto.class,
                Map.of("port", port, "id", 1)
        );

        UserDto userForUpdate = retrieveResponse.getBody();
        TestUtils.assertOperationSucceeded(userForUpdate, retrieveResponse.getStatusCode());

        userForUpdate.setName("Jonathan");
        userForUpdate.setSurname("Doe‑Smith");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDto> putEntity = new HttpEntity<>(userForUpdate, headers);

        ResponseEntity<UserDto> updateResponse = restTemplate.exchange(
                URL,
                HttpMethod.PUT,
                putEntity,
                UserDto.class,
                Map.of("port", port)
        );

        UserDto updated = updateResponse.getBody();
        TestUtils.assertOperationSucceeded(updated, updateResponse.getStatusCode());

        assertThat(updated)
                .usingRecursiveComparison()
                .isEqualTo(userForUpdate);
    }

    @Test
    void deleteUserWorksCorrectly() {
        ResponseEntity<UserDto> userTwo = restTemplate.getForEntity(
                URL_WITH_ID,
                UserDto.class,
                Map.of("port", port, "id", 2)
        );
        assertThat(userTwo.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Void> userTwoDeleteResponse = restTemplate.exchange(
                URL_WITH_ID,
                HttpMethod.DELETE,
                null,
                Void.class,
                Map.of("port", port, "id", 2)
        );
        assertThat(userTwoDeleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);


        ResponseEntity<UserDto> userTwoDeletedResponse = restTemplate.getForEntity(
                URL_WITH_ID,
                UserDto.class,
                Map.of("port", port, "id", 2)
        );
        assertThat(userTwoDeletedResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


}
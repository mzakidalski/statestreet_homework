package com.statestreet.carreservation;

import com.statestreet.carreservation.controller.user.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtils {

    public static void assertOperationSucceeded(UserDto created, HttpStatusCode code) {
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(code).isEqualTo(HttpStatus.OK);
    }
}

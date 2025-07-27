package com.statestreet.carreservation.controller.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private int id;

    private String name;

    private String surname;
}

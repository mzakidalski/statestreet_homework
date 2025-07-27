package com.statestreet.carreservation.controller.user;

import com.statestreet.carreservation.repository.user.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getSurname()
        );
    }

    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        return user;
    }

    public List<UserDto> toDtoList(List<User> users) {
        return users == null ? null : users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<User> toEntityList(List<UserDto> dtos) {
        return dtos == null ? null : dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}


package com.statestreet.carreservation.controller.user;

import com.statestreet.carreservation.repository.user.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    /**
     * Convert entity to DTO
     */
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

    /**
     * Convert DTO to entity
     */
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

    /**
     * Convert a list of entities to a list of DTOs
     */
    public List<UserDto> toDtoList(List<User> users) {
        return users == null ? null : users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert a list of DTOs to a list of entities
     */
    public List<User> toEntityList(List<UserDto> dtos) {
        return dtos == null ? null : dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}


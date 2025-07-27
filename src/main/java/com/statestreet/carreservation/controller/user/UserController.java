package com.statestreet.carreservation.controller.user;

import com.statestreet.carreservation.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    @Autowired
    UserService service;

    @Autowired
    UserMapper mapper;

    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(defaultValue = "id") String sort,
                                  @RequestParam(defaultValue = "asc") String order,
                                  @RequestParam(defaultValue = "0") int pageNumber,
                                  @RequestParam(defaultValue = "10") int numOfRecords) {
        return mapper.toDtoList(service.findAll(sort, Sort.Direction.fromString(order),
                               pageNumber, numOfRecords));
    }

    @GetMapping("/{id}")
    public UserDto getSingleUser(@PathVariable int id) {
        return mapper.toDto(service.getById(id));
    }

    @PostMapping
    public UserDto insert(@RequestBody UserDto user) {
        return mapper.toDto(service.insert(mapper.toEntity(user)));
    }

    @PutMapping
    public UserDto update(@RequestBody UserDto user) {
        return mapper.toDto(service.update(mapper.toEntity(user)));
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        return service.delete(id);
    }




}

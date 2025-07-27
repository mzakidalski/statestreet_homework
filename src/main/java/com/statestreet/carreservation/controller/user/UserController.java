package com.statestreet.carreservation.controller.user;

import com.statestreet.carreservation.repository.user.User;
import com.statestreet.carreservation.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/v1/users")
public class UserController {

    @Autowired
    UserService service;

    @GetMapping
    public List<User> getAllUsers(@RequestParam(defaultValue = "id") String sort,
                                  @RequestParam(defaultValue = "asc") String order,
                                  @RequestParam(defaultValue = "0") int pageNumber,
                                  @RequestParam(defaultValue = "10") int numOfRecords) {
        return service.findAll(sort, Sort.Direction.fromString(order),
                               pageNumber, numOfRecords);
    }

    @GetMapping("/id")
    public User getSingleUser(@RequestParam int id) {
        return service.getById(id);
    }

    @PostMapping
    public User insert(@RequestBody User user) {
        return service.insert(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        return service.update(user);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        return service.delete(id);
    }




}

package com.statestreet.carreservation.service.user;

import com.statestreet.carreservation.repository.user.User;
import com.statestreet.carreservation.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    public List<User> findAll(String sort, Sort.Direction order,
                              int pageNumber, int numOfRecords) {
        Sort sortAndOrder = Sort.by(order, sort);
        Pageable pageRequest = PageRequest.of(pageNumber, numOfRecords, sortAndOrder);
        return repository.findAll(pageRequest).getContent();
    }

    public User getById(int id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        return repository.getReferenceById(id);
    }

    public User insert(User user) {
        user.setId(0);
        return repository.save(user);
    }

    public User update(User user) {
        return repository.save(user);
    }

    public String delete(int id) {
        Optional<User> foundUser = repository.findById(id);

        if(foundUser.isEmpty())
            throw new UserNotFoundException(id);

        repository.delete(foundUser.get());
        return "User with id "+ id +" deleted";
    }

    private String getErrorMessage(int id) {
        return "User with id "+ id +" has not been found";
    }
}

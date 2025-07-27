package com.statestreet.carreservation.service.user;

import com.statestreet.carreservation.repository.user.User;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface UserService {
   List<User> findAll(String sort, Sort.Direction order,
                              int pageNumber, int numOfRecords);

   User getById(int id);

   User insert(User user);

   User update(User user);

    String delete(int id);

}

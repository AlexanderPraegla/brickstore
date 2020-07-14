package edu.hm.praegla.oauth.persistence;

import edu.hm.praegla.oauth.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

    User findDistinctByUserName(String username);
}

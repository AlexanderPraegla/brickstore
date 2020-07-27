package edu.hm.brickstore.oauth.persistence;

import edu.hm.brickstore.oauth.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

    User findDistinctByUserName(String username);
}

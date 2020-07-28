package edu.hm.brickstore.account.repository;

import edu.hm.brickstore.account.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepository extends MongoRepository<Account, Long> {
}

package edu.hm.praegla.account.repository;

import edu.hm.praegla.account.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, Long> {

    Optional<Account> findByAccountId(long accountId);
}

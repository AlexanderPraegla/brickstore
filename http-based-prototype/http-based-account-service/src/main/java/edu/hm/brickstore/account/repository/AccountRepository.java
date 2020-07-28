package edu.hm.brickstore.account.repository;

import edu.hm.brickstore.account.entity.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
}

package edu.hm.praegla.account.repository;

import edu.hm.praegla.account.entity.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
}

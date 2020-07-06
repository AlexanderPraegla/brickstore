package hm.edu.praegla.account.repository;

import hm.edu.praegla.account.entity.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
}

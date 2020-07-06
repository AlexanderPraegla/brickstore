package hm.edu.praegla.demo.repository;

import hm.edu.praegla.demo.entity.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
}

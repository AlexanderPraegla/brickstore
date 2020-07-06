package hm.edu.praegla.account.repository;

import hm.edu.praegla.account.entity.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
}

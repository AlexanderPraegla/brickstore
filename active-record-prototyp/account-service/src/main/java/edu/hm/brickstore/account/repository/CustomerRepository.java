package edu.hm.brickstore.account.repository;

import edu.hm.brickstore.account.entity.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
}

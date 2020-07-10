package edu.hm.praegla.account.repository;

import edu.hm.praegla.account.entity.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
}

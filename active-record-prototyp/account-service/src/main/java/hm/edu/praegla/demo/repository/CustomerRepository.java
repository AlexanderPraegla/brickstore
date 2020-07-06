package hm.edu.praegla.demo.repository;

import hm.edu.praegla.demo.entity.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
}

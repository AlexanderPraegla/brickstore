package hm.edu.praegla.demo.repository;

import hm.edu.praegla.demo.entity.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
}

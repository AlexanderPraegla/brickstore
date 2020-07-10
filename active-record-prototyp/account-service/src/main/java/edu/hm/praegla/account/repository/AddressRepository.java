package edu.hm.praegla.account.repository;

import edu.hm.praegla.account.entity.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
}

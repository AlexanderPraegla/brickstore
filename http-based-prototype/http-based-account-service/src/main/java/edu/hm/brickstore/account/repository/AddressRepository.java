package edu.hm.brickstore.account.repository;

import edu.hm.brickstore.account.entity.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
}

package edu.hm.praegla.shoppingcart.repository;


import edu.hm.praegla.shoppingcart.entity.LineItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LineItemRepository extends CrudRepository<LineItem, Long> {

    @Query("SELECT l FROM LineItem l WHERE l.id = :lineItemId AND l.shoppingCart.accountId = :accountId")
    Optional<LineItem> findByIdAndAccountId(@Param("lineItemId") long lineItemId, @Param("accountId") long accountId);
}

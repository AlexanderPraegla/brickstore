package edu.hm.brickstore.client.account;

import edu.hm.brickstore.client.account.dto.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("account-service")
public interface AccountClient {

    @RequestMapping(method = RequestMethod.GET, value = "accounts/{accountId}")
    AccountDTO getAccount(@PathVariable long accountId);
}

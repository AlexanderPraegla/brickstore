package edu.hm.praegla.client.account;

import edu.hm.praegla.client.account.dto.AccountDTO;
import edu.hm.praegla.client.account.dto.ModifyAccountBalanceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("account-service")
public interface AccountClient {

    @RequestMapping(method = RequestMethod.GET, value = "accounts/{accountId}")
    AccountDTO getAccount(@PathVariable long accountId);

    @RequestMapping(method = RequestMethod.POST, value = "accounts/{accountId}/charge")
    void chargeAccount(@PathVariable long accountId, @RequestBody ModifyAccountBalanceDTO modifyAccountBalanceDTO);

    @RequestMapping(method = RequestMethod.POST, value = "accounts/{accountId}/debit")
    void debitAccount(@PathVariable long accountId, @RequestBody ModifyAccountBalanceDTO modifyAccountBalanceDTO);
}

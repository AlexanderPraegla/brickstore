package edu.hm.praegla.account;

import edu.hm.praegla.BrickstoreRestTest;
import edu.hm.praegla.account.dto.AccountDTO;
import edu.hm.praegla.account.dto.AddressDTO;
import edu.hm.praegla.account.dto.CustomerDTO;
import edu.hm.praegla.error.dto.ApiErrorDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountTest extends BrickstoreRestTest {

    private final AccountClient accountClient;

    public AccountTest() {
        accountClient = new AccountClient(spec);
    }

    @Test
    public void shouldCreateNewAccount() {
        CustomerDTO customerDTO = new CustomerDTO(0, "Bob", "Andrew", "bob.andrew@dreifragezeichen.com");
        AddressDTO addressDTO = new AddressDTO(0, "Am Schrottplatz 3", "Rocky Beach", "97468");

        AccountDTO createdAccount = accountClient.createAccount(customerDTO, addressDTO);

        assertThat(createdAccount.getCustomer()).isEqualToIgnoringGivenFields(customerDTO, "id");
        assertThat(createdAccount.getAddress()).isEqualToIgnoringGivenFields(addressDTO, "id");
        assertThat(createdAccount.getStatus()).isEqualTo("CREATED");
        assertThat(createdAccount.getBalance()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    public void shouldModifyAccountCustomer() {
        long accountId = 8;
        String newFirstname = "Henry";
        String newLastname = "Shaw";
        String newEmail = "Henry.Shaw@dreifragezeichen.com";

        accountClient.modifyCustomer(accountId, newFirstname, newLastname, newEmail)
                .then()
                .statusCode(200);
        AccountDTO account = accountClient.getAccountById(accountId);

        assertThat(account.getCustomer().getFirstname()).isEqualTo(newFirstname);
        assertThat(account.getCustomer().getLastname()).isEqualTo(newLastname);
        assertThat(account.getCustomer().getEmail()).isEqualTo(newEmail);
    }


    @Test
    public void shouldModifyAccountAddress() {
        long accountId = 3;
        String newStreet = "Am Pier 1";
        String newCity = "Santa Babara";
        String newPostalcode = "36784";

        accountClient.modifyAddress(accountId, newStreet, newCity, newPostalcode)
                .then()
                .statusCode(200);
        AccountDTO account = accountClient.getAccountById(accountId);

        assertThat(account.getAddress().getStreet()).isEqualTo(newStreet);
        assertThat(account.getAddress().getCity()).isEqualTo(newCity);
        assertThat(account.getAddress().getPostalCode()).isEqualTo(newPostalcode);
    }

    @Test
    public void shouldSetAccountStatusInactive() {
        long accountId = 4;
        Map<String, String> body = new HashMap<>();
        body.put("status", "INACTIVE");
        accountClient.updateAccountStatus(accountId, body)
                .then()
                .statusCode(200);
        AccountDTO account = accountClient.getAccountById(accountId);
        assertThat(account.getStatus()).isEqualTo("INACTIVE");
    }

    @Test
    public void shouldActivateNewAccountByChargingAccount() {
        long accountId = 5;

        accountClient.chargeAccount(accountId, new BigDecimal("17.42"));
        AccountDTO account = accountClient.getAccountById(accountId);

        assertThat(account.getBalance()).isEqualTo(new BigDecimal("17.42"));
        assertThat(account.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    public void shouldDebitAccountWithEnoughBalance() {
        long accountId = 6;

        accountClient.debitAccount(accountId, new BigDecimal("5.50"))
                .then()
                .statusCode(200);

        AccountDTO account = accountClient.getAccountById(accountId);
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("4.50"));
    }

    @Test
    public void shouldDenyDebitAccountWithNotEnoughBalance() {
        long accountId = 7;

        ApiErrorDTO apiErrorDTO = accountClient.debitAccount(accountId, new BigDecimal("11.00"))
                .then()
                .statusCode(400)
                .extract().as(ApiErrorDTO.class);
        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("BALANCE_INSUFFICIENT");

        AccountDTO account = accountClient.getAccountById(accountId);
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("10.00"));
    }

    @Test
    public void shouldDenyChargeToDeactivatedAccount() {
        long accountId = 9;
        ApiErrorDTO apiErrorDTO = accountClient.chargeAccount(accountId, new BigDecimal("10.00"))
                .then()
                .statusCode(400)
                .extract().as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ACCOUNT_INACTIVE");
    }



}

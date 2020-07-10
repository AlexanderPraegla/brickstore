package edu.hm.praegla.account;

import edu.hm.praegla.BrickstoreRestTest;
import edu.hm.praegla.account.dto.AccountDTO;
import edu.hm.praegla.account.dto.AddressDTO;
import edu.hm.praegla.account.dto.CustomerDTO;
import edu.hm.praegla.client.AccountClient;
import edu.hm.praegla.error.dto.ApiErrorDTO;
import edu.hm.praegla.parameterResolver.AddressParameterResolver;
import edu.hm.praegla.parameterResolver.CustomerParameterResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({AddressParameterResolver.class, CustomerParameterResolver.class})
public class AccountTestV2 extends BrickstoreRestTest {

    private final AccountClient accountClient;
    private AccountDTO testAccount;

    public AccountTestV2() {
        accountClient = new AccountClient(spec);
    }

    @BeforeEach
    public void beforeEach(CustomerDTO customerDTO, AddressDTO addressDTO) {
        testAccount = accountClient.createAccount(customerDTO, addressDTO);
    }

    @Test
    @Order(1)
    public void shouldCreateNewAccount() {
        CustomerDTO customerDTO = new CustomerDTO("Bob", "Andrew", "bob.andrew@dreifragezeichen.com");
        AddressDTO addressDTO = new AddressDTO("Rocky Beach", "97468", "Am Schrottplatz 3");
        AccountDTO createdAccount = accountClient.createAccount(customerDTO, addressDTO);

        assertThat(createdAccount.getCustomer()).isEqualToIgnoringGivenFields(customerDTO, "id");
        assertThat(createdAccount.getAddress()).isEqualToIgnoringGivenFields(addressDTO, "id");
        assertThat(createdAccount.getStatus()).isEqualTo("CREATED");
        assertThat(createdAccount.getBalance()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    @Order(2)
    public void shouldSetAccountStatusInactive() {
        accountClient.updateAccountStatus(testAccount.getId(), "INACTIVE")
                .then()
                .statusCode(200);
        AccountDTO account = accountClient.getAccountById(testAccount.getId());
        assertThat(account.getStatus()).isEqualTo("INACTIVE");
    }

    @Test
    @Order(3)
    public void shouldModifyAccountCustomer() {
        String newFirstname = "Henry";
        String newLastname = "Shaw";
        String newEmail = "Henry.Shaw@dreifragezeichen.com";

        accountClient.modifyCustomer(testAccount.getId(), newFirstname, newLastname, newEmail)
                .then()
                .statusCode(200);
        AccountDTO account = accountClient.getAccountById(testAccount.getId());

        assertThat(account.getCustomer().getFirstname()).isEqualTo(newFirstname);
        assertThat(account.getCustomer().getLastname()).isEqualTo(newLastname);
        assertThat(account.getCustomer().getEmail()).isEqualTo(newEmail);
    }

    @Test
    @Order(4)
    public void shouldModifyAccountAddress() {
        String newStreet = "Am Pier 1";
        String newCity = "Santa Babara";
        String newPostalcode = "36784";

        accountClient.modifyAddress(testAccount.getId(), newStreet, newCity, newPostalcode)
                .then()
                .statusCode(200);
        AccountDTO account = accountClient.getAccountById(testAccount.getId());

        assertThat(account.getAddress().getStreet()).isEqualTo(newStreet);
        assertThat(account.getAddress().getCity()).isEqualTo(newCity);
        assertThat(account.getAddress().getPostalCode()).isEqualTo(newPostalcode);
    }

    @Test
    @Order(5)
    public void shouldActivateNewAccountByChargingAccount() {
        accountClient.chargeAccount(testAccount.getId(), new BigDecimal("17.42"));
        AccountDTO account = accountClient.getAccountById(testAccount.getId());

        assertThat(account.getBalance()).isEqualTo(new BigDecimal("17.42"));
        assertThat(account.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    @Order(6)
    public void shouldDebitAccountWithEnoughBalance() {
        accountClient.chargeAccount(testAccount.getId(), new BigDecimal("10.00"));

        accountClient.debitAccount(testAccount.getId(), new BigDecimal("5.50"))
                .then()
                .statusCode(200);

        AccountDTO account = accountClient.getAccountById(testAccount.getId());
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("4.50"));
    }

    @Test
    @Order(7)
    public void shouldDenyDebitAccountWithNotEnoughBalance() {
        accountClient.chargeAccount(testAccount.getId(), new BigDecimal("10.00"));

        ApiErrorDTO apiErrorDTO = accountClient.debitAccount(testAccount.getId(), new BigDecimal("11.00"))
                .then()
                .statusCode(400)
                .extract().as(ApiErrorDTO.class);
        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("BALANCE_INSUFFICIENT");

        AccountDTO account = accountClient.getAccountById(testAccount.getId());
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("10.00"));
    }

    @Test
    @Order(8)
    public void shouldDenyChargeToDeactivatedAccount() {
        accountClient.updateAccountStatus(testAccount.getId(), "INACTIVE")
                .then()
                .statusCode(200);

        ApiErrorDTO apiErrorDTO = accountClient.chargeAccount(testAccount.getId(), new BigDecimal("10.00"))
                .then()
                .statusCode(400)
                .extract().as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ACCOUNT_INACTIVE");
    }



}

package edu.hm.brickstore.account;

import edu.hm.brickstore.BrickstoreRestTest;
import edu.hm.brickstore.account.dto.AccountDTO;
import edu.hm.brickstore.account.dto.AddressDTO;
import edu.hm.brickstore.account.dto.CustomerDTO;
import edu.hm.brickstore.client.AccountTestTestClient;
import edu.hm.brickstore.error.dto.ApiErrorDTO;
import edu.hm.brickstore.parameterResolver.AddressParameterResolver;
import edu.hm.brickstore.parameterResolver.CustomerParameterResolver;
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
public class AccountTest extends BrickstoreRestTest {

    private final AccountTestTestClient accountTestClient;
    private AccountDTO testAccount;

    public AccountTest() {
        accountTestClient = new AccountTestTestClient(spec);
    }

    @BeforeEach
    public void beforeEach(CustomerDTO customerDTO, AddressDTO addressDTO) {
        testAccount = accountTestClient.createAccount(customerDTO, addressDTO);
    }

    @Test
    @Order(1)
    public void shouldCreateNewAccount() {
        CustomerDTO customerDTO = new CustomerDTO("Bob", "Andrew", "bob.andrew@dreifragezeichen.com");
        AddressDTO addressDTO = new AddressDTO("Rocky Beach", "97468", "Am Schrottplatz 3");
        AccountDTO createdAccount = accountTestClient.createAccount(customerDTO, addressDTO);

        assertThat(createdAccount.getCustomer()).isEqualToIgnoringGivenFields(customerDTO, "id");
        assertThat(createdAccount.getAddress()).isEqualToIgnoringGivenFields(addressDTO, "id");
        assertThat(createdAccount.getStatus()).isEqualTo("CREATED");
        assertThat(createdAccount.getBalance()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    @Order(2)
    public void shouldSetAccountStatusDeactivated() {
        accountTestClient.updateAccountStatus(testAccount.getId(), "DEACTIVATED")
                .then()
                .statusCode(200);
        AccountDTO account = accountTestClient.getAccountById(testAccount.getId());
        assertThat(account.getStatus()).isEqualTo("DEACTIVATED");
    }

    @Test
    @Order(3)
    public void shouldModifyAccountCustomer() {
        String newFirstname = "Henry";
        String newLastname = "Shaw";
        String newEmail = "Henry.Shaw@dreifragezeichen.com";

        accountTestClient.modifyCustomer(testAccount.getId(), newFirstname, newLastname, newEmail)
                .then()
                .statusCode(200);
        AccountDTO account = accountTestClient.getAccountById(testAccount.getId());

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

        accountTestClient.modifyAddress(testAccount.getId(), newStreet, newCity, newPostalcode)
                .then()
                .statusCode(200);
        AccountDTO account = accountTestClient.getAccountById(testAccount.getId());

        assertThat(account.getAddress().getStreet()).isEqualTo(newStreet);
        assertThat(account.getAddress().getCity()).isEqualTo(newCity);
        assertThat(account.getAddress().getPostalCode()).isEqualTo(newPostalcode);
    }

    @Test
    @Order(5)
    public void shouldActivateNewAccountByCreditAccount() {
        accountTestClient.creditAccount(testAccount.getId(), new BigDecimal("17.42"));
        AccountDTO account = accountTestClient.getAccountById(testAccount.getId());

        assertThat(account.getBalance()).isEqualTo(new BigDecimal("17.42"));
        assertThat(account.getStatus()).isEqualTo("ACTIVATED");
    }

    @Test
    @Order(6)
    public void shouldDebitAccountWithEnoughBalance() {
        accountTestClient.creditAccount(testAccount.getId(), new BigDecimal("10.00"));

        accountTestClient.debitAccount(testAccount.getId(), new BigDecimal("5.50"))
                .then()
                .statusCode(200);

        AccountDTO account = accountTestClient.getAccountById(testAccount.getId());
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("4.50"));
    }

    @Test
    @Order(7)
    public void shouldDenyDebitAccountWithNotEnoughBalance() {
        accountTestClient.creditAccount(testAccount.getId(), new BigDecimal("10.00"));

        ApiErrorDTO apiErrorDTO = accountTestClient.debitAccount(testAccount.getId(), new BigDecimal("11.00"))
                .then()
                .statusCode(400)
                .extract().as(ApiErrorDTO.class);
        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("BALANCE_INSUFFICIENT");

        AccountDTO account = accountTestClient.getAccountById(testAccount.getId());
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("10.00"));
    }

    @Test
    @Order(8)
    public void shouldDenyCreditToDeactivatedAccount() {
        accountTestClient.updateAccountStatus(testAccount.getId(), "DEACTIVATED")
                .then()
                .statusCode(200);

        ApiErrorDTO apiErrorDTO = accountTestClient.creditAccount(testAccount.getId(), new BigDecimal("10.00"))
                .then()
                .statusCode(400)
                .extract().as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ACCOUNT_DEACTIVATED");
    }



}

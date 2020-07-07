package edu.hm.praegla.account;

import edu.hm.praegla.BrickstoreRestTest;
import edu.hm.praegla.account.dto.AccountDTO;
import edu.hm.praegla.account.dto.AddressDTO;
import edu.hm.praegla.account.dto.CreateAccountDTO;
import edu.hm.praegla.account.dto.CustomerDTO;
import edu.hm.praegla.error.dto.ApiErrorDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountTest extends BrickstoreRestTest {

    @Test
    @Order(1)
    public void shouldHaveTwoAccountsByDefault() {
        List<AccountDTO> accounts = given(spec)
                .when()
                .get("accounts")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList(".", AccountDTO.class);
        assertThat(accounts).hasSize(15);
    }

    @Test
    public void shouldCreateNewAccount() {
        CustomerDTO customerDTO = new CustomerDTO(0, "Bob", "Andrew", "bob.andrew@dreifragezeichen.com");
        AddressDTO addressDTO = new AddressDTO(0, "Am Schrottplatz 3", "Rocky Beach", "97468");

        AccountDTO createdAccount = createAccount(customerDTO, addressDTO);

        assertThat(createdAccount.getCustomer()).isEqualToIgnoringGivenFields(customerDTO, "id");
        assertThat(createdAccount.getAddress()).isEqualToIgnoringGivenFields(addressDTO, "id");
        assertThat(createdAccount.getStatus()).isEqualTo("CREATED");
        assertThat(createdAccount.getBalance()).isEqualTo(0);
    }

    @Test
    public void shouldModifyAccountCustomer() {
        long accountId = 8;
        String newFirstname = "Henry";
        String newLastname = "Shaw";
        String newEmail = "Henry.Shaw@dreifragezeichen.com";

        Map<String, String> body = new HashMap<>();
        body.put("firstname", newFirstname);
        body.put("lastname", newLastname);
        body.put("email", newEmail);

        given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/customer", accountId)
                .then()
                .statusCode(200);
        AccountDTO account = getAccountById(accountId);

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
        Map<String, String> body = new HashMap<>();
        body.put("street", newStreet);
        body.put("city", newCity);
        body.put("postalCode", newPostalcode);

        given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/address", accountId)
                .then()
                .statusCode(200);
        AccountDTO account = getAccountById(accountId);

        assertThat(account.getAddress().getStreet()).isEqualTo(newStreet);
        assertThat(account.getAddress().getCity()).isEqualTo(newCity);
        assertThat(account.getAddress().getPostalCode()).isEqualTo(newPostalcode);
    }

    @Test
    public void shouldSetAccountStatusInactive() {
        long accountId = 4;
        Map<String, String> body = new HashMap<>();
        body.put("status", "INACTIVE");
        given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/status", accountId)
                .then()
                .statusCode(200);
        AccountDTO account = getAccountById(accountId);
        assertThat(account.getStatus()).isEqualTo("INACTIVE");
    }

    @Test
    public void shouldActivateNewAccountByChargingAccount() {
        long accountId = 5;

        chargeAccount(accountId, 17.42);
        AccountDTO account = getAccountById(accountId);

        assertThat(account.getBalance()).isEqualTo(17.42);
        assertThat(account.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    public void shouldDebitAccountWithEnoughBalance() {
        long accountId = 6;

        Map<String, Double> body = new HashMap<>();
        body.put("amount", 5.5);
        given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/debit", accountId)
                .then()
                .statusCode(200);

        AccountDTO account = getAccountById(accountId);
        assertThat(account.getBalance()).isEqualTo(4.5);
    }

    @Test
    public void shouldDenyDebitAccountWithNotEnoughBalance() {
        long accountId = 7;

        Map<String, Double> body = new HashMap<>();
        body.put("amount", 11.0);
        ApiErrorDTO apiErrorDTO = given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/debit", accountId)
                .then()
                .statusCode(400)
                .extract().as(ApiErrorDTO.class);
        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("BALANCE_INSUFFICIENT");

        AccountDTO account = getAccountById(accountId);
        assertThat(account.getBalance()).isEqualTo(10);
    }

    @Test
    public void shouldDenyChargeToDeactivatedAccount() {
        long accountId = 9;
        Map<String, Double> body = new HashMap<>();
        body.put("amount", 10.0);
        ApiErrorDTO apiErrorDTO = given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/charge", accountId)
                .then()
                .statusCode(400)
                .extract().as(ApiErrorDTO.class);

        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("ACCOUNT_INACTIVE");
    }

    private AccountDTO getAccountById(long accountId) {
        return getResourceById("accounts/{accountId}", accountId, AccountDTO.class);
    }

    private AccountDTO createAccount(CustomerDTO customerDTO, AddressDTO addressDTO) {
        CreateAccountDTO accountDTO = new CreateAccountDTO(customerDTO, addressDTO);
        String accountLocation = createResource("accounts", accountDTO);
        return getResourceByLocationHeader(accountLocation, AccountDTO.class);
    }

    private void chargeAccount(long accountId, double amount) {
        Map<String, Double> body = new HashMap<>();
        body.put("amount", amount);
        given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/charge", accountId)
                .then()
                .statusCode(200);
    }

}

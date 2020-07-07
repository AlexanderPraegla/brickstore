package edu.hm.praegla.account;

import edu.hm.praegla.BrickstoreRestTest;
import edu.hm.praegla.account.dto.AccountDTO;
import edu.hm.praegla.account.dto.AddressDTO;
import edu.hm.praegla.error.dto.ApiErrorDTO;
import edu.hm.praegla.account.dto.CreateAccountDTO;
import edu.hm.praegla.account.dto.CustomerDTO;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class AccountTest extends BrickstoreRestTest {

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
        CustomerDTO customerDTO = new CustomerDTO(0, "Bob", "Andrew", "bob.andrew@dreifragezeichen.com");
        AddressDTO addressDTO = new AddressDTO(0, "Am Schrottplatz 3", "Rocky Beach", "97468");

        AccountDTO account = createAccount(customerDTO, addressDTO);
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
                .post("accounts/{accountId}/customer", account.getId())
                .then()
                .statusCode(200);
        account = getAccountById(account.getId());

        assertThat(account.getCustomer().getFirstname()).isEqualTo(newFirstname);
        assertThat(account.getCustomer().getLastname()).isEqualTo(newLastname);
        assertThat(account.getCustomer().getEmail()).isEqualTo(newEmail);
    }

    @Test
    public void shouldModifyAccountAddress() {
        CustomerDTO customerDTO = new CustomerDTO(0, "Kelly", "Madigan", "Kelly.Madigan@dreifragezeichen.com");
        AddressDTO addressDTO = new AddressDTO(0, "Am Stand 3", "Rocky Beach", "97468");

        AccountDTO account = createAccount(customerDTO, addressDTO);
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
                .post("accounts/{accountId}/address", account.getId())
                .then()
                .statusCode(200);
        account = getAccountById(account.getId());

        assertThat(account.getAddress().getStreet()).isEqualTo(newStreet);
        assertThat(account.getAddress().getCity()).isEqualTo(newCity);
        assertThat(account.getAddress().getPostalCode()).isEqualTo(newPostalcode);
    }

    @Test
    public void shouldSetAccountStatusInactive() {
        CustomerDTO customerDTO = new CustomerDTO(0, "Julius", "Jonas", "Julius.Jonas@dreifragezeichen.com");
        AddressDTO addressDTO = new AddressDTO(0, "Am Schrottplatz 3", "Rocky Beach", "97468");

        AccountDTO account = createAccount(customerDTO, addressDTO);
        Map<String, String> body = new HashMap<>();
        body.put("status", "INACTIVE");
        given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/status", account.getId())
                .then()
                .statusCode(200);
        account = getAccountById(account.getId());
        assertThat(account.getStatus()).isEqualTo("INACTIVE");
    }

    @Test
    public void shouldActivateNewAccountByChargingAccount() {
        CustomerDTO customerDTO = new CustomerDTO(0, "Samuel", "Reynolds", "Samuel.Reynolds@dreifragezeichen.com");
        AddressDTO addressDTO = new AddressDTO(0, "Am Polizeirevier 3", "Rocky Beach", "97468");

        AccountDTO account = createAccount(customerDTO, addressDTO);

        chargeAccount(account.getId(), 17.42);
        account = getAccountById(account.getId());

        assertThat(account.getBalance()).isEqualTo(17.42);
        assertThat(account.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    public void shouldDebitAccountWithEnoughBalance() {
        CustomerDTO customerDTO = new CustomerDTO(0, "Mathilda", "Jonas", "Mathilda.Jonas@dreifragezeichen.com");
        AddressDTO addressDTO = new AddressDTO(0, "Am Schrottplatz 4", "Rocky Beach", "97468");

        AccountDTO account = createAccount(customerDTO, addressDTO);
        chargeAccount(account.getId(), 10);

        Map<String, Double> body = new HashMap<>();
        body.put("amount", 5.5);
        given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/debit", account.getId())
                .then()
                .statusCode(200);

        account = getAccountById(account.getId());
        assertThat(account.getBalance()).isEqualTo(4.5);
    }

    @Test
    public void shouldDenyDebitAccountWithNotEnoughBalance() {
        CustomerDTO customerDTO = new CustomerDTO(0, "Titus", "Jonas", "Titus.Jonas@dreifragezeichen.com");
        AddressDTO addressDTO = new AddressDTO(0, "Am Schrottplatz 4", "Rocky Beach", "97468");

        AccountDTO account = createAccount(customerDTO, addressDTO);
        chargeAccount(account.getId(), 10);

        Map<String, Double> body = new HashMap<>();
        body.put("amount", 11.0);
        ApiErrorDTO apiErrorDTO = given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/debit", account.getId())
                .then()
                .statusCode(400)
                .extract().as(ApiErrorDTO.class);
        assertThat(apiErrorDTO.getResponseCode()).isEqualTo("BALANCE_INSUFFICIENT");

        account = getAccountById(account.getId());
        assertThat(account.getBalance()).isEqualTo(10);
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

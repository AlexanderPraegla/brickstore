package edu.hm.praegla.client;

import edu.hm.praegla.account.dto.AccountDTO;
import edu.hm.praegla.account.dto.AddressDTO;
import edu.hm.praegla.account.dto.CreateAccountDTO;
import edu.hm.praegla.account.dto.CustomerDTO;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class AccountTestTestClient extends ApiTestClient {

    public AccountTestTestClient(RequestSpecification spec) {
        super(spec);
    }

    public AccountDTO getAccountById(long accountId) {
        return getResourceById("accounts/{accountId}", accountId, AccountDTO.class);
    }

    public AccountDTO createAccount(CustomerDTO customerDTO, AddressDTO addressDTO) {
        CreateAccountDTO accountDTO = new CreateAccountDTO(customerDTO, addressDTO);
        String accountLocation = createResource("accounts", accountDTO);
        return getResourceByLocationHeader(accountLocation, AccountDTO.class);
    }

    public Response modifyCustomer(long accountId, String newFirstname, String newLastname, String newEmail) {
        Map<String, String> body = new HashMap<>();
        body.put("firstname", newFirstname);
        body.put("lastname", newLastname);
        body.put("email", newEmail);
        return given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/customer", accountId);
    }

    public Response modifyAddress(long accountId, String newStreet, String newCity, String newPostalcode) {
        Map<String, String> body = new HashMap<>();
        body.put("street", newStreet);
        body.put("city", newCity);
        body.put("postalCode", newPostalcode);
        return given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/address", accountId);
    }

    public Response updateAccountStatus(long accountId, String status) {
        Map<String, String> body = new HashMap<>();
        body.put("status", status);
        return given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/status", accountId);
    }

    public Response creditAccount(long accountId, BigDecimal amount) {
        Map<String, BigDecimal> body = new HashMap<>();
        body.put("creditAmount", amount);
        return given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/credit", accountId);
    }

    public Response debitAccount(long accountId, BigDecimal amount) {
        Map<String, BigDecimal> body = new HashMap<>();
        body.put("debitAmount", amount);
        return given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/debit", accountId);
    }

    public List<AccountDTO> getAccounts() {
        return given(spec)
                .when()
                .get("accounts")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList(".", AccountDTO.class);
    }
}

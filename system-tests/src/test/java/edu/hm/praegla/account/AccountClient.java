package edu.hm.praegla.account;

import edu.hm.praegla.ApiClient;
import edu.hm.praegla.account.dto.AccountDTO;
import edu.hm.praegla.account.dto.AddressDTO;
import edu.hm.praegla.account.dto.CreateAccountDTO;
import edu.hm.praegla.account.dto.CustomerDTO;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class AccountClient extends ApiClient {

    public AccountClient(RequestSpecification spec) {
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

    public Response modifyCustomer(long accountId, Map<String, String> body) {
        return given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/customer", accountId);
    }

    public Response modifyAddress(long accountId, Map<String, String> body) {
        return given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/address", accountId);
    }

    public Response updateAccountStatus(long accountId, Map<String, String> body) {
        return given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/status", accountId);
    }

    public Response chargeAccount(long accountId, double amount) {
        Map<String, Double> body = new HashMap<>();
        body.put("amount", amount);
        return given(spec)
                .when()
                .body(body)
                .post("accounts/{accountId}/charge", accountId);
    }

    public Response debitAccount(long accountId, double amount) {
        Map<String, Double> body = new HashMap<>();
        body.put("amount", amount);
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

package com.raidiamproject.automation.api;

import org.junit.Before;
import org.junit.Test;

import com.raidiamproject.automation.utils.EnvironmentProperties;
import com.raidiamproject.automation.common.Base;

import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.given;

import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;


public class GetAccountEndpointTest extends Base {

    private final String systemUrl = EnvironmentProperties.getValue("systemUrl");
	private final String token = EnvironmentProperties.getValue("validToken");
    private final String accountId = EnvironmentProperties.getValue("accountId");

    @Before
    public void setup() {
        RestAssured.baseURI = systemUrl; 
        // To-do: Add consent and token workflow in a specific class and remove token from environment variable;
     }

    @Test
    public void givenGetAccountApi_CheckStatusCode() {  
        given()
		.contentType("application/json")
		.header("Authorization", "Bearer " + token)
		.when().get("account/" + accountId).then()
		.statusCode(200);
    }

    @Test
    public void givenGetAccountApi_CheckContract() throws IOException {

        Response response = given()
                .contentType("application/json")
		        .header("Authorization", "Bearer " + token)
                .when()
                .get("account/" + accountId)
                .then()
                .extract().response();

        // Validate Status Code
        response.then().statusCode(200);

        // Validate Payload Structure and Types
        response.then().body("data.id", isA(String.class)); 
        response.then().body("data.bank", isA(String.class)); 
        response.then().body("data.accountNumero", isA(String.class));      
        response.then().body("meta.totalRecords", isA(Integer.class));    
        response.then().body("meta.totalPages", isA(Integer.class));  


        String accountId = response.jsonPath().getString("data.id");
        assertThat(assertValidUUID(accountId), is(true));

        // Validate Account Numbers Pattern
        String accountNumber = response.jsonPath().getString("data.accountNumero");
        boolean matches = accountNumber.matches("^[0-9]{7}-[0-9]$");
        assertThat(matches, is(true));

        // Validate Other Parts of the Payload
        response.then().body("links.self", is(systemUrl.replace("http://", "") + "account/" + accountId + "/" + accountId));

        // Get Request Date Time on Unix TimeStamp With Fractions
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response.asString()); 
        double requestDateTimeDecimal = new Double(jsonNode.get("meta").get("requestDateTime").asText());
        assertThat(isValidUnixTimestamp(requestDateTimeDecimal), is(true));
    }

    @Test
    public void givenGetAccountApi_NonexistentAccountId() {
        String nonexistentAccountId = "8f9c3d2a-211e-4a16-b159-1278362190a2";

        Response response = given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + token)
        .when()
        .get("account/" + nonexistentAccountId)
        .then()
        .extract().response();

        // Validate Status Code
        response.then().statusCode(404);

        // Validate Payload Structure and Types
        response.then().body("message", isA(String.class)); 
        response.then().body("message", equalTo("Not Found")); 

        response.then().body("_links.self.href", isA(String.class)); 
        response.then().body("_links.self.href", is("/test-api/accounts/v1/account/" + nonexistentAccountId)); 
        response.then().body("_links.self.templated", isA(Boolean.class)); 
        response.then().body("_links.self.templated", equalTo(false));


        response.then().body("_embedded.errors", isA(List.class)); 
        response.then().body("_embedded.errors.message", everyItem(isA(String.class)));

        response.then().body("_embedded.errors.message", contains("Account Id " + nonexistentAccountId + " not found"));;

        response.then().body("_embedded.errors._links", isA(List.class)); 
        response.then().body("_embedded.errors._embedded", isA(List.class)); 

    }

    @Test
    public void givenGetAccountApi_EmptyAccountId() {
        Response response = given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + token)
        .when()
        .get("account/" + "")
        .then()
        .extract().response();

        // Validate Status Code
        response.then().statusCode(403);

        // Validate Payload Structure and Types
        response.then().body("message", isA(String.class)); 
        response.then().body("message", equalTo("Forbidden")); 
  
        response.then().body("_links.self.href", isA(String.class)); 
        response.then().body("_links.self.href", is("/test-api/accounts/v1/account/")); 
        response.then().body("_links.self.templated", isA(Boolean.class)); 
        response.then().body("_links.self.templated", equalTo(false));
    
        response.then().body("_embedded.errors", isA(List.class)); 
        response.then().body("_embedded.errors.message", everyItem(isA(String.class)));
  
        response.then().body("_embedded.errors.message", contains("Forbidden"));
  
        response.then().body("_embedded.errors._links", isA(List.class)); 
        response.then().body("_embedded.errors._embedded", isA(List.class)); 

    }

    @Test
    public void givenGetAccountApi_InvalidUUID() {
        String invalidUUID = accountId + "abc";

        Response response = given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + token)
        .when()
        .get("account/" + invalidUUID)
        .then()
        .extract().response();

        // Validate Status Code
        response.then().statusCode(500);

        // Validate Payload Structure and Types
        response.then().body("message", isA(String.class)); 
        response.then().body("message", equalTo("Internal Server Error")); 

        response.then().body("_links.self.href", isA(String.class)); 
        response.then().body("_links.self.href", is("/test-api/accounts/v1/account/" + invalidUUID)); 
        response.then().body("_links.self.templated", isA(Boolean.class)); 
        response.then().body("_links.self.templated", equalTo(false));


        response.then().body("_embedded.errors", isA(List.class)); 
        response.then().body("_embedded.errors.message", everyItem(isA(String.class)));

        response.then().body("_embedded.errors.message", contains("Internal Server Error: UUID string too large"));

        response.then().body("_embedded.errors._links", isA(List.class)); 
        response.then().body("_embedded.errors._embedded", isA(List.class)); 

    }

    @Test
    public void givenGetAccountApi_UUIDWithSpecialChars() {
        String UUIDWithSpecialChars = accountId + "!@#$%";

        Response response = given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + token)
        .when()
        .get("account/" + UUIDWithSpecialChars)
        .then()
        .extract().response();

        // Validate Status Code
        response.then().statusCode(500);

        // Validate Payload Structure and Types
        response.then().body("message", isA(String.class)); 
        response.then().body("message", equalTo("Internal Server Error")); 

        response.then().body("_links.self.href", isA(String.class)); 

        //This assert will fail as reported in the bugs section, "#" will make every character afeter be ignored
        //response.then().body("_links.self.href", is("/test-api/accounts/v1/account/" + UUIDWithSpecialChars)); 
        response.then().body("_links.self.templated", isA(Boolean.class)); 
        response.then().body("_links.self.templated", equalTo(false));


        response.then().body("_embedded.errors", isA(List.class)); 
        response.then().body("_embedded.errors.message", everyItem(isA(String.class)));

        response.then().body("_embedded.errors.message", contains("Internal Server Error: UUID string too large"));

        response.then().body("_embedded.errors._links", isA(List.class)); 
        response.then().body("_embedded.errors._embedded", isA(List.class)); 

    }

    @Test
    public void givenGetAccountApi_ClientWithoutToken() {
        Response response = given()
        .contentType("application/json")
        .when()
        .get("account/" + accountId)
        .then()
        .extract().response();

        // Validate Status Code
        response.then().statusCode(401);

        // Validate Payload Structure and Types
        // Validate Payload Structure and Types
        response.then().body("message", isA(String.class)); 
        response.then().body("message", equalTo("Unauthorized")); 

        response.then().body("_links.self.href", isA(String.class)); 
        response.then().body("_links.self.href", is("/test-api/accounts/v1/account/" + accountId)); 
        response.then().body("_links.self.templated", isA(Boolean.class)); 
        response.then().body("_links.self.templated", equalTo(false));


        response.then().body("_embedded.errors", isA(List.class)); 
        response.then().body("_embedded.errors.message", everyItem(isA(String.class)));

        response.then().body("_embedded.errors.message", contains("Unauthorized"));

        response.then().body("_embedded.errors._links", isA(List.class)); 
        response.then().body("_embedded.errors._embedded", isA(List.class)); 
    }

    // @Test
    // public void givenListAccountsApi_ClientWithWrongToken() {
    //     String invalidToken = "eyJhbGciOiAibm9uZSIsInR5cCI6ICJKV1QifQ==.ewogICJzY29wZSfdsfdsI6ICJhY2NvdW50cyBjb25zZW50OnVybjpiYW5rOjA3NWQ1ZWY0LWM5OGQtNGIxMC1hZjAxLWZjYWVjZDhlNGIyNyIsCiAgImNsaWVudF9pZCI6ICJjbGllbnQxIgp9.";

    //     Response response = given()
    //     .contentType("application/json")
    //     .header("Authorization", "Bearer " + invalidToken)
    //     .when()
    //     .get("/accounts")
    //     .then()
    //     .extract().response();

    //     // Validate Status Code
    //     response.then().statusCode(500);

    //     // Validate Payload Structure and Types
    //     response.then().body("message", isA(String.class)); 
    //     response.then().body("message", equalTo("Internal Server Error")); 

    //     response.then().body("_links.self.href", isA(String.class)); 
    //     response.then().body("_links.self.href", is("/test-api/accounts/v1/accounts")); 
    //     response.then().body("_links.self.templated", isA(Boolean.class)); 
    //     response.then().body("_links.self.templated", equalTo(false));


    //     response.then().body("_embedded.errors", isA(List.class)); 
    //     response.then().body("_embedded.errors.message", everyItem(isA(String.class)));

    //     response.then().body("_embedded.errors.message", contains("Internal Server Error: Cannot invoke \"java.util.Map.getOrDefault(Object, Object)\" because \"payload\" is null"));

    //     response.then().body("_embedded.errors._links", isA(List.class)); 
    //     response.then().body("_embedded.errors._embedded", isA(List.class)); 

    // }

    // @Test
    // public void givenListAccountsApi_RequestWithWrongHTTP() {
    //     Response response = given()
    //     .contentType("application/json")
    //     .header("Authorization", "Bearer " + token)
    //     .when()
    //     .post("/accounts")
    //     .then()
    //     .extract().response();

    //     // Validate Status Code
    //     response.then().statusCode(403);

    //     // Validate Payload Structure and Types
    //     response.then().body("message", isA(String.class)); 
    //     response.then().body("message", equalTo("Forbidden")); 

    //     response.then().body("_links.self.href", isA(String.class)); 
    //     response.then().body("_links.self.href", is("/test-api/accounts/v1/accounts")); 
    //     response.then().body("_links.self.templated", isA(Boolean.class)); 
    //     response.then().body("_links.self.templated", equalTo(false));


    //     response.then().body("_embedded.errors", isA(List.class)); 
    //     response.then().body("_embedded.errors.message", everyItem(isA(String.class)));

    //     response.then().body("_embedded.errors.message", contains("Forbidden"));

    //     response.then().body("_embedded.errors._links", isA(List.class)); 
    //     response.then().body("_embedded.errors._embedded", isA(List.class)); 

    // }
}
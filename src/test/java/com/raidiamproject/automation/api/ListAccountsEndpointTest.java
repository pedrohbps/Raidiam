package com.raidiamproject.automation.api;

import org.junit.Before;
import org.junit.Test;

import com.raidiamproject.automation.utils.EnvironmentProperties;
import com.raidiamproject.automation.common.Base;

import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;


public class ListAccountsEndpointTest extends Base {

    private final String systemUrl = EnvironmentProperties.getValue("systemUrl");
	private final String token = EnvironmentProperties.getValue("validToken");

    @Before
    public void setup() {
        RestAssured.baseURI = systemUrl; 
        // To-do: Add consent and token workflow in a specific class and remove token from environment variable;
     }

    @Test
    public void givenListAccountsApi_CheckStatusCode() {  
        given()
		.contentType("application/json")
		.header("Authorization", "Bearer " + token)
		.when().get("accounts/").then()
		.statusCode(200);
    }

    @Test
    public void givenListAccountsApi_CheckContract() throws IOException {

        Response response = given()
                .contentType("application/json")
		        .header("Authorization", "Bearer " + token)
                .when()
                .get("/accounts")
                .then()
                .extract().response();

        // Validate Status Code
        response.then().statusCode(200);

        // Validate Payload Structure and Types
        response.then().body("data", isA(List.class)); 
        response.then().body("data.id", everyItem(isA(String.class))); 
        response.then().body("data.bank", everyItem(isA(String.class))); 
        response.then().body("data.accountNumero", everyItem(isA(String.class)));      
        response.then().body("meta.totalRecords", isA(Integer.class));    
        response.then().body("meta.totalPages", isA(Integer.class));  

        List<String> accountIds = response.jsonPath().getList("data.id");
        boolean allIdsValid = accountIds.stream().allMatch(id -> Pattern.matches("^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$", id));
        assertThat(allIdsValid, is(true));

        // Validate Account Numbers Pattern
        List<String> accountNumbers = response.jsonPath().getList("data.accountNumero");
        boolean allAccountNumbersValid = accountNumbers.stream().allMatch(numero -> Pattern.matches("^[0-9]{7}-[0-9]$", numero));
        assertThat(allAccountNumbersValid, is(true));

        // Validate Other Parts of the Payload
        response.then().body("links.self", is("localhost:8080/test-api/accounts/v1/accounts"));   

        // Get Request Date Time on Unix TimeStamp With Fractions
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response.asString()); 
        double requestDateTimeDecimal = new Double(jsonNode.get("meta").get("requestDateTime").asText());
        assertThat(isValidUnixTimestamp(requestDateTimeDecimal), is(true));
    }

    @Test
    public void givenListAccountsApi_ClientWithoutConsent() {
        String tokenWithNoConsentGiven = "eyJhbGciOiAibm9uZSIsInR5cCI6ICJKV1QifQ==.ewogICJzY29wZSI6ICJhY2NvdW50cyBjb25zZW50OnVybjpiYW5rOjA3NWQ1ZWY0LWM5OGQtNGIxMC1hZjAxLWZjYWVjZDhlNGIyNyIsCiAgImNsaWVudF9pZCI6ICJjbGllbnQxIgp9.";

        Response response = given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + tokenWithNoConsentGiven)
        .when()
        .get("/accounts")
        .then()
        .extract().response();

        // Validate Status Code
        response.then().statusCode(404);

        // Validate Payload Structure and Types
        response.then().body("message", isA(String.class)); 
        response.then().body("message", equalTo("Not Found")); 

        response.then().body("_links.self.href", isA(String.class)); 
        response.then().body("_links.self.href", is("/test-api/accounts/v1/accounts")); 
        response.then().body("_links.self.templated", isA(Boolean.class)); 
        response.then().body("_links.self.templated", equalTo(false));


        response.then().body("_embedded.errors", isA(List.class)); 
        response.then().body("_embedded.errors.message", everyItem(isA(String.class)));

        //To-do create function to generate token and consent
        response.then().body("_embedded.errors.message", contains("Consent Id urn:bank:075d5ef4-c98d-4b10-af01-fcaecd8e4b27 not found"));

        response.then().body("_embedded.errors._links", isA(List.class)); 
        response.then().body("_embedded.errors._embedded", isA(List.class)); 

    }

    @Test
    public void givenListAccountsApi_RequestWithoutToken() {
        Response response = given()
        .contentType("application/json")
        .when()
        .get("/accounts")
        .then()
        .extract().response();

        // Validate Status Code
        response.then().statusCode(401);

        // Validate Payload Structure and Types
        response.then().body("message", isA(String.class)); 
        response.then().body("message", equalTo("Unauthorized")); 

        response.then().body("_links.self.href", isA(String.class)); 
        response.then().body("_links.self.href", is("/test-api/accounts/v1/accounts")); 
        response.then().body("_links.self.templated", isA(Boolean.class)); 
        response.then().body("_links.self.templated", equalTo(false));


        response.then().body("_embedded.errors", isA(List.class)); 
        response.then().body("_embedded.errors.message", everyItem(isA(String.class)));

        response.then().body("_embedded.errors.message", contains("Unauthorized"));

        response.then().body("_embedded.errors._links", isA(List.class)); 
        response.then().body("_embedded.errors._embedded", isA(List.class)); 

    }

    @Test
    public void givenListAccountsApi_ClientWithWrongToken() {
        String invalidToken = "eyJhbGciOiAibm9uZSIsInR5cCI6ICJKV1QifQ==.ewogICJzY29wZSfdsfdsI6ICJhY2NvdW50cyBjb25zZW50OnVybjpiYW5rOjA3NWQ1ZWY0LWM5OGQtNGIxMC1hZjAxLWZjYWVjZDhlNGIyNyIsCiAgImNsaWVudF9pZCI6ICJjbGllbnQxIgp9.";

        Response response = given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + invalidToken)
        .when()
        .get("/accounts")
        .then()
        .extract().response();

        // Validate Status Code
        response.then().statusCode(500);

        // Validate Payload Structure and Types
        response.then().body("message", isA(String.class)); 
        response.then().body("message", equalTo("Internal Server Error")); 

        response.then().body("_links.self.href", isA(String.class)); 
        response.then().body("_links.self.href", is("/test-api/accounts/v1/accounts")); 
        response.then().body("_links.self.templated", isA(Boolean.class)); 
        response.then().body("_links.self.templated", equalTo(false));


        response.then().body("_embedded.errors", isA(List.class)); 
        response.then().body("_embedded.errors.message", everyItem(isA(String.class)));

        response.then().body("_embedded.errors.message", contains("Internal Server Error: Cannot invoke \"java.util.Map.getOrDefault(Object, Object)\" because \"payload\" is null"));

        response.then().body("_embedded.errors._links", isA(List.class)); 
        response.then().body("_embedded.errors._embedded", isA(List.class)); 

    }

    @Test
    public void givenListAccountsApi_RequestWithWrongHTTP() {
        Response response = given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + token)
        .when()
        .post("/accounts")
        .then()
        .extract().response();

        // Validate Status Code
        response.then().statusCode(403);

        // Validate Payload Structure and Types
        response.then().body("message", isA(String.class)); 
        response.then().body("message", equalTo("Forbidden")); 

        response.then().body("_links.self.href", isA(String.class)); 
        response.then().body("_links.self.href", is("/test-api/accounts/v1/accounts")); 
        response.then().body("_links.self.templated", isA(Boolean.class)); 
        response.then().body("_links.self.templated", equalTo(false));


        response.then().body("_embedded.errors", isA(List.class)); 
        response.then().body("_embedded.errors.message", everyItem(isA(String.class)));

        response.then().body("_embedded.errors.message", contains("Forbidden"));

        response.then().body("_embedded.errors._links", isA(List.class)); 
        response.then().body("_embedded.errors._embedded", isA(List.class)); 

    }
}
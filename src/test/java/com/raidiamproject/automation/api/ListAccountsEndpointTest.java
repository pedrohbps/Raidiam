package com.raidiamproject.automation.api;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;


public class ListAccountsEndpointTest {
    private final String token = "eyJhbGciOiAibm9uZSIsInR5cCI6ICJKV1QifQ==.ewogICJzY29wZSI6ICJhY2NvdW50cyBjb25zZW50OnVybjpiYW5rOjA3NWQ1ZWY0LWM5OGQtNGIxMC1hZjAxLWZjYWVjZDhlNGI2NyIsCiAgImNsaWVudF9pZCI6ICJjbGllbnQxIgp9.";

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080/test-api/accounts/v1/"; 
        // Ideally we should add the consent and token generation on this function;
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

        // Validate DateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
        OffsetDateTime dateTimeNow = OffsetDateTime.now();

        // Get Request Date Time on Unix TimeStamp With Fractions
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response.asString()); 
        double requestDateTimeDecimal = new Double(jsonNode.get("meta").get("requestDateTime").asText());
        //response.then().body("meta.requestDateTime")
        assertThat(isValidUnixTimestamp(requestDateTimeDecimal), is(true));


        // ZoneId zoneId = ZoneId.of("Brazil/East"); 
        // Instant timestamp = Instant.now();
        // double d = (double) timestamp.getEpochSecond() + (double) timestamp.getNano();// / 1000_000_000;

        // System.out.println("d: " + d);
        // System.out.println("requestDateTimeDecimal: " + requestDateTimeDecimal);

        //  assertThat(d, allOf(
        //      greaterThan(requestDateTimeDecimal),  // Assert value2 is greater than value1
        //      closeTo(requestDateTimeDecimal, 1000)  // Assert value2 is close to value1 within tolerance
        //  ));

        // long seconds = (long) requestDateTimeDecimal;  
        // int nanos = (int) ((requestDateTimeDecimal - seconds) * 1_000_000_000); 
        // Instant instant = Instant.ofEpochSecond(seconds, nanos);

        // // Adjust to GMT - 3
        
        // LocalDateTime dateTime = LocalDateTime.ofInstant(instant, zoneId);
        // System.out.println("dateTime: " + dateTime);
        
        // String formattedRequestTime = dateTime.format(formatter);
        // String formatteddateTimeNow = dateTimeNow.format(formatter);

        

       
        // Instant datetime = Instant.now();

        // // Extract needed information: date time as seconds + fraction of that second
        // long secondsFromEpoch = datetime.getEpochSecond();
        // int nanoFromBeginningOfSecond = datetime.getNano();
        // double nanoAsFraction = datetime.getNano()/1e9;

        

        //response.then().body("meta.requestDateTime", closeTo(d, 2000));

        // LocalDateTime requestDateTime = Instant.ofEpochSecond((long) requestDateTimeDouble)
        // .atZone(ZoneId.of("GMT-3"))
        // .toLocalDateTime();

        // System.out.println("requestDateTime " + requestDateTime + "\n");

        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // String formattedRequestDateTime = requestDateTime.format(formatter);


        //System.out.println("formattedRequestDateTime " + formattedRequestDateTime + "\n");

   






        // System.out.println("unixTimestampSeconds" + unixTimestampSeconds + "\n");
        // long unixTimestampMillis = (long) (unixTimestampSeconds * 1000); // Convert to milliseconds
        // System.out.println("unixTimestampMillis" + unixTimestampMillis + "\n");
        // LocalDateTime requestDateTime = Instant.ofEpochMilli(unixTimestampMillis)
        //     .atZone(ZoneId.of("UTC"))
        //     .toLocalDateTime();

        // System.out.println("requestDateTime" + requestDateTime + "\n");
        // // Format for Comparison
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // String formattedRequestDateTime = requestDateTime.format(formatter);

        // System.out.println(formattedRequestDateTime);

        // Ensure the request time is within the expected range and before "now"
        // response.then().body("meta.requestDateTime", 
        //     closeTo(unixTimestampSeconds, 10.0) // Allow for 1-second difference
        // );
        //response.then().body("meta.requestDateTime", lessThanOrEqualTo(Instant.now().getEpochSecond()));
    }

    public static boolean isValidUnixTimestamp(double timestamp) {
        long seconds = (long) timestamp; // Get integer part
        double fraction = timestamp - seconds; // Get fractional part

        return seconds >= 0 && fraction >= 0 && fraction < 1; 
    }

}
package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.GetBooks;
import resources.ApiResources;
import resources.Utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;

public class StepDefinitions extends Utils {

    RequestSpecification reqSpec;
    Response response;
    JsonPath jsonPath;

    @Given("Simple Books payload")
    public void simple_books_payload() throws IOException {
        reqSpec = given().spec(requestSpecification());
    }

    @When("User calls {string} with {string} http request")
    public void user_calls_with_http_request(String resource, String method) {
        ApiResources resourceAPI = ApiResources.valueOf(resource);
        if(method.equalsIgnoreCase("GET")){
            response = reqSpec.when().get(resourceAPI.getResource());
        }
        else if(method.equalsIgnoreCase("POST")){
            response = reqSpec.when().post(resourceAPI.getResource());
        }
        jsonPath = response.jsonPath();
    }

    @Then("the API returns success with status code {int}")
    public void the_api_returns_success_with_status_code(int statusCode) {
        assertEquals(response.getStatusCode(),statusCode);
    }

    @Then("the API response time is less than {int} ms")
    public void the_api_response_time_is_less_than_ms(int maxTime) {
        long time = response.getTime();
        assertTrue("Response time exceeded :"+time+"ms", time < maxTime);
    }

    @Then("the API size is less than {int} KB")
    public void the_api_size_is_less_than_kb(int maxSize) {
        byte[] responseBytes = response.asByteArray();
        double sizeInKB = responseBytes.length/1024.0;
        assertTrue("Response body is to large "+sizeInKB+"KB", sizeInKB < maxSize);
    }

    @Then("the API response is an array")
    public void the_api_response_is_an_array() {
        List<Object> jsonArray = jsonPath.getList("$");//root-level array
        assertNotNull("Response body is not a JSON array", jsonArray);
    }

    @Then("each element in the response array is a JSON object")
    public void each_element_in_the_response_array_is_a_json_object() {
        List<Object> jsonArray = jsonPath.getList("$");
        for(Object element : jsonArray){
            assertTrue("Element is not a json Object "+element, element instanceof Map);
        }
    }

    @Then("each book json should have {string}, {string}, {string}, {string} keys")
    public void each_book_json_should_have_keys(String key1, String key2, String key3, String key4) {
        List<Map<String,Object>> books = jsonPath.getList("$");
        assertFalse("Book list is empty", books.isEmpty());
        for(Map<String,Object> book: books){
            assertTrue("Missing key: "+key1 ,book.containsKey(key1));
            assertTrue("Missing key: "+key2 ,book.containsKey(key2));
            assertTrue("Missing key: "+key3 ,book.containsKey(key3));
            assertTrue("Missing key: "+key4 ,book.containsKey(key4));
        }
    }

    @Then("each book json should have valid data types")
    public void each_book_json_should_have_valid_data_types() {
        List<Map<String,Object>> books = jsonPath.getList("$");
        assertFalse("Book list is empty", books.isEmpty());
        for(Map<String,Object> book: books){
            assertTrue("ID is not Integer",book.get("id") instanceof Integer);
            assertTrue("Name is not String",book.get("name") instanceof String);
            assertTrue("Type is not String",book.get("type") instanceof String);
            assertTrue("Available is not Boolean",book.get("available") instanceof Boolean);
        }
    }

    @Then("each book Id is unique")
    public void each_book_id_is_Unique() {
        //Using the Deserialization technique to convert json response to java object
        List<GetBooks> getBooks = List.of(response.getBody().as(GetBooks[].class));
        assertFalse("Book list is empty", getBooks.isEmpty());

        Set<Integer> uniqueIds = new HashSet<Integer>();
        for(GetBooks getBook: getBooks){
            boolean added = uniqueIds.add(getBook.getId());
            assertTrue("Duplicate ids found "+getBook.getId(), added);
        }
    }

    @Then("book id {int} has expected name {string}, type {string}, available {string} values")
    public void book_id_has_expected_name_type_fiction_available_true_values(int givenBookId, String expectedBookName, String expectedBookType, String expectedBookAvailableStr) {
        //Using the Deserialization technique to convert json response to java object
        List<GetBooks> getBooks = List.of(response.getBody().as(GetBooks[].class));
        assertFalse("Book list is empty", getBooks.isEmpty());
        // Convert available string to boolean
        boolean expectedBookAvailable = Boolean.parseBoolean(expectedBookAvailableStr);

        for(GetBooks getBook: getBooks){
            if(getBook.getId() == givenBookId){
                assertEquals(expectedBookName, getBook.getName());
                assertEquals(expectedBookType, getBook.getType());
                assertEquals(expectedBookAvailable,getBook.isAvailable());
            }
        }
    }
}

package com.test.Demo;
import java.io.File;
import java.util.Map;

import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import java.text.SimpleDateFormat;  
import java.util.Date;  
public class ExchangRate_API_Test {
	String baseURI = "https://open.er-api.com/v6/latest/USD";
	RequestSpecification httpRequest = null;
	Response response = null;
	@BeforeMethod
	public void setUp()
	{
		RestAssured.baseURI = baseURI;
		httpRequest = RestAssured.given();
		response = httpRequest.request(Method.GET, "");
	}
	@Test
    public void testAPIStatusCode() {
		System.out.println("Check the status code and status retuned by the API response");
		//System.out.println("Response=>" + response.prettyPrint());
		int statusCode = response.getStatusCode();
		System.out.println("Status Line : " + response.getStatusLine());
        Assert.assertEquals(statusCode /*actual value*/, 200 /*expected value*/, 
         "Correct status code returned");
        System.out.println("Response Status : " + response.getStatusCode());
        System.out.println("*************************************************");
	}
	@Test
	public void testUSDPriceAgainstAED()
	{
		System.out.println("Fetch the USD price against the AED and make sure the prices are in range on 3.6 – 3.7");
		ResponseBody body = response.getBody();
        String bodyAsString = body.asString();
    	JsonPath jsonPathEvaluator = response.jsonPath();
    	Map<String, Float> rates = jsonPathEvaluator.get("rates");
    	System.out.println("USD Price " + rates.get("USD"));
    	System.out.println("AED Price " + rates.get("AED"));
    	float minRange = 3.6F;
    	float maxRange = 3.7F;
    	float price = rates.get("AED");
    	Assert.assertTrue(minRange<= price && maxRange >= price, "Price is in given range");
    	System.out.println("Price is in given range " + price);
    	System.out.println("*************************************************");
	}
	@Test
	public void testResponseTime() {
		System.out.println("Make sure API response is not less then 3 seconds then current time in second.");
		long c = response.getTime();
    	System.out.println("Response time in miliseconds : " + c);
    	ValidatableResponse v = response.then();
        v.time(Matchers.lessThan(3000L));
        System.out.println("verified response time lesser than 3 seconds");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
        Date date = new Date();  
        System.out.println("Current Date : " + formatter.format(date)); 
        System.out.println("*************************************************");
	}
	@Test
	public void testOneSixtyTwoCurrencyPairs()
	{
		System.out.println("Verify that 162 currency pairs are retuned by the API.");
		ResponseBody body = response.getBody();
        String bodyAsString = body.asString();
    	JsonPath jsonPathEvaluator = response.jsonPath();
    	Map<String, String> rates = jsonPathEvaluator.get("rates");
    	System.out.println("Base code is : " +  rates);
    	System.out.println("Total number of currecncy pairs : " + rates.size());
    	Assert.assertEquals(162, rates.size());
    	System.out.println("*************************************************");
	}
	@Test
	public void testJSONSchema()
	{
		System.out.println("Make sure API response matches the Json schema");
		response.then().assertThat()
        .body(JsonSchemaValidator.
        matchesJsonSchema(new File("C:\\Users\\haris\\eclipse-workspace\\Demo\\responseSchema.json")));
		System.out.println("*************************************************");
	}
}

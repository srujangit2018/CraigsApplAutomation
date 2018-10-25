package com.craigsappl.api.test;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.testng.annotations.Test;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.craigsappl.api.CraigsApplBase;
import com.craigsappl.util.CraigsApplUtil;

import io.restassured.RestAssured;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.filter.session.SessionFilter;
import io.restassured.response.Response;


/* Author: Srujan
 * This is the root class of API automation testing. Involves in verifying of Application login, and to save search results
 */
public class CraigsApplAPITest extends CraigsApplBase{
	CraigsApplUtil craigsApplUtil=null;
	CookieFilter cookieFilter=null;
	SessionFilter sessionFilter=null;
	Document doc=null;
	String resCookie1, resCookie2, resCookie3 = null;
	Set <String> toyotaCarsResultsSet=null;
	List <String> toyotaCarsResult=null;
  @BeforeTest
  public void beforeTest() {
	  sessionFilter = new SessionFilter();
	  cookieFilter= new CookieFilter();
	  craigsApplUtil=new CraigsApplUtil(); 
	  
	  // Holds the total cars list With duplicates, need to use set interface to avoid duplicates 
	  toyotaCarsResult = new ArrayList <String> ();
	 
	  // Holds the total cars list Without duplicates. For good practice storing the car names in the Set.
	   toyotaCarsResultsSet = new TreeSet <String> ();
  }
  
  @Test(priority=1)
  public void craigListApplApitest() {
   System.out.println("Before Connection");
   Response response = RestAssured.given().contentType("text/html")
    .headers(craigsApplUtil.commonHeaders())
    .header("Host", "sfbay.craigslist.org")
    .filter(sessionFilter)
    .filter(cookieFilter)
    .get(props.getProperty("craigsAppl.baseURI"));

   resCookie1 = response.getCookie("cl_def_hp");
   resCookie2 = response.getCookie("cl_b");
   resCookie3 = response.getCookie("cl_tocmode");
   response.then().assertThat().statusCode(200);
   searchToyotaVehicles();
   CraigListApplLogin();
   
   // storing cars names into a file as the console is not displaying all the cars list
   printToyotaSearchedResults();
 }

 /* Print and Store search results cars names  */
  public  void printToyotaSearchedResults() {
	 String carName=""; 
 	 BufferedWriter bufferedWriter=null;
 	     try {
 	    	 	bufferedWriter = new BufferedWriter(new FileWriter(new File(System.getProperty("user.dir")+"\\src\\test\\resources\\toyotacarslist.txt")));
 	    	 	Iterator<String> itr = toyotaCarsResultsSet.iterator(); 
 	    	 	while(itr.hasNext()){ 
 	    	 		carName=itr.next();
 	    	 		System.out.println(carName);
 	    	 		bufferedWriter.write(carName+"\n");
 				 }	
 		
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
 	     finally
 	 	{ 
 	 	   try{
 	 	      if(bufferedWriter!=null)
 	 	    	 bufferedWriter.close();
 	 	   }catch(Exception ex){
 	 	       System.out.println("Error in closing the BufferedWriter"+ex);
 	 	    }
 	 	}
 }

 /* Search requires vehicle name*/
 public void searchToyotaVehicles() {
   System.out.println("Before search Toyoto vehicle");
   int carsDisplayRange,carsCount=0;
   Response response = RestAssured.given()
    .queryParam("query", props.getProperty("craigsAppl.searchedVehicleName"))
    .queryParam("sort", "rel")
    .contentType("text/html")
    .headers(craigsApplUtil.commonHeaders())
    .header("Host", "sfbay.craigslist.org")
    .header("Referer", props.getProperty("craigsAppl.baseURI"))
    .cookie("cl_def_hp", resCookie1)
    .cookie("cl_b", resCookie2)
    .filter(sessionFilter)
    .filter(cookieFilter)
    .get(props.getProperty("craigsAppl.baseURI")+"search/sss");

   org.jsoup.nodes.Document doc = Jsoup.parse(response.then().extract().body().asString());
     
   carsCount=craigsApplUtil.getCarDetailsCount(doc, "span", "totalcount");
   carsDisplayRange=craigsApplUtil.getCarDetailsCount(doc, "span", "rangeTo");
   toyotaCarsResultsSet=craigsApplUtil.saveCarNames(doc, "a", "result-title hdrlnk", toyotaCarsResultsSet);
   System.out.println("Cars Count"+carsCount);
   response.then().assertThat().statusCode(200);
   for (int range = carsDisplayRange; range <= carsCount; range += carsDisplayRange) {
    iterateOverSearchedToyotoVehicles(range);
   }
  }
   
  /* This method is to iterate and save the search results vehicles list  */
  public void iterateOverSearchedToyotoVehicles(int paginationRange) {
    Response toyotoSearchResponse = RestAssured.given()
    .queryParam("s", paginationRange)
    .queryParam("query", props.getProperty("craigsAppl.searchedVehicleName"))
    .queryParam("sort", "rel")
    .contentType("text/html")
    .headers(craigsApplUtil.commonHeaders())
    .header("Host", "sfbay.craigslist.org")
    .header("Referer", props.getProperty("craigsAppl.baseURI"))
    .cookie("cl_def_hp", resCookie1)
    .cookie("cl_b", resCookie2)
    .filter(sessionFilter)
    .filter(cookieFilter)
    .get(props.getProperty("craigsAppl.baseURI")+"search/sss");

    doc = Jsoup.parse(toyotoSearchResponse.then().extract().body().asString());
    toyotaCarsResultsSet=craigsApplUtil.saveCarNames(doc, "a", "result-title hdrlnk", toyotaCarsResultsSet);
  }


  /* This method is to login into CarigList Application and to save the vehicle search  */
  public void CraigListApplLogin() {
   System.out.println("Login to Appl and Save your search");	 
   Map < String, String > cred = new HashMap < String, String > ();
   cred.put("step", "confirmation");
   cred.put("rt", "");
   cred.put("rp", "/savesearch/save?URL=https%3A%2F%2Fsfbay%2Ecraigslist%2Eorg%2Fsearch%2Fsss%3Fquery%3Dasdasd%26sort%3Drel");
   cred.put("p", "0");
   cred.put("t", "1531654843");
   cred.put("whichForm", "login");
   cred.put("inputEmailHandle", System.getProperty("username"));
   cred.put("inputPassword", System.getProperty("password"));
   cred.put("browserinfo", "");

   RestAssured.baseURI = props.getProperty("craigsAppl.accountURI")+"/login";
   RestAssured.useRelaxedHTTPSValidation();
   String path = "/login";
   Response response = RestAssured.given()
    .header("Cache-Control", "max-age=0")
    .header("Origin", props.getProperty("craigsAppl.accountURI"))
    .header("Upgrade-Insecure-Requests", "1")
    .contentType("application/x-www-form-urlencoded")
    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
    .header("Referer", props.getProperty("craigsAppl.accountURI")+"/login")
    .header("Accept-Encoding", "gzip, deflate, br")
    .header("Accept-Language", "en-US,en;q=0.9")
    .cookie("cl_def_hp", resCookie1)
    .cookie("cl_b", resCookie2)
    .formParams(cred)
    .filter(sessionFilter)
    .filter(cookieFilter)
    .post()
    .andReturn();
   
   // getting session id val
   Map < String, String > cookiesMap = response.getCookies();
   String sessionValue = null;
   for (Map.Entry < String, String > entry: cookiesMap.entrySet()) {
    if (entry.getKey().equals("cl_session")) {
     sessionValue = entry.getValue();
    }
   }

   Map < String, String > authenticationTokenDet = new HashMap < String, String > ();
   authenticationTokenDet.put("go", "confirm");
   authenticationTokenDet.put("_csrf", "adfsaf");

   RestAssured.baseURI = props.getProperty("craigsAppl.accountURI")+"/savesearch/save";
   RestAssured.useRelaxedHTTPSValidation();
   Response toyotoSearchResponse = RestAssured.given()
    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
    .header("Accept-Language", "en-US")
    .header("Cache-Control", "max-age=0")
    .header("Host", "accounts.craigslist.org")
    .header("Referer", props.getProperty("craigsAppl.accountURI")+"/savesearch/save?URL=https%3A%2F%2Fsfbay%2Ecraigslist%2Eorg%2Fsearch%2Fsss%3Fquery%3Dgun%26sort%3Drel")
    .cookie("cl_def_hp", resCookie1)
    .cookie("cl_b", resCookie2)
    .header("Accept-Encoding", "gzip, deflate, br")
    .filter(sessionFilter)
    .filter(cookieFilter)
    .formParams(authenticationTokenDet)
    .queryParam("URL", props.getProperty("craigsAppl.baseURI")+"search/sss?query=toyota&sort=rel")
    .post();

   doc = Jsoup.parse(toyotoSearchResponse.then().extract().body().asString());
   Elements po = doc.getElementsByAttributeValue("name", "_csrf");

   authenticationTokenDet.put("go", "confirm");
   authenticationTokenDet.put("_csrf", po.val());

   RestAssured.baseURI = props.getProperty("craigsAppl.accountURI")+"/savesearch/save";
   RestAssured.useRelaxedHTTPSValidation();
   Response toyotaCarSearchSave = RestAssured.given()
    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
    .header("Accept-Language", "en-US")
    .header("Cache-Control", "max-age=0")
    .header("Host", "accounts.craigslist.org")
    .header("Referer", props.getProperty("craigsAppl.accountURI")+"/savesearch/save?URL=https%3A%2F%2Fsfbay%2Ecraigslist%2Eorg%2Fsearch%2Fsss%3Fquery%3Dgun%26sort%3Drel")
    .cookie("cl_def_hp", resCookie1)
    .cookie("cl_b", resCookie2)
    .header("Accept-Encoding", "gzip, deflate, br")
    .filter(sessionFilter)
    .filter(cookieFilter)
    .formParams(authenticationTokenDet)
    .queryParam("URL", props.getProperty("craigsAppl.baseURI")+"search/sss?query=toyota&sort=rel")
    .post();
  }

  @AfterTest
  public void afterTest() {
	  //need to write close logics if any
  }

}

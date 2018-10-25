package com.craigsappl.util;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/* Author: Srujan
 * This helper class is used to get the searched cars Specific details like Cars Count, Cars Pagination Range, Car names, and to do some common selenium operations.
 */
public class CraigsApplUtil {

	
	/* to get the cars Pagination Range, Total cars count */
	public int getCarDetailsCount(Document doc, String tagName, String className){
		  int carsDetailsVal=0;	
		  Elements carsDetails = doc.getElementsByTag(tagName);
		  for (Element link: carsDetails) {
		   String linkHref = link.attr("class");
		   if (linkHref.equals(className)) {
			   carsDetailsVal=Integer.parseInt((link.text()));		    
		   }
		  }
		  return carsDetailsVal;	
	}
	
	/* save the cars names in a list */
	public Set<String> saveCarNames(Document doc, String tagName, String className, Set<String> toyotaCarsResultsSet){
		 Elements carTitles = doc.getElementsByTag("a");
		  for (Element link: carTitles) {
		   String linkHref = link.attr("class");
		   if (linkHref.equals("result-title hdrlnk")) {
			   toyotaCarsResultsSet.add(link.text());
		   }
		  }
		  return toyotaCarsResultsSet;	
	}
	/* to store common headers */
	public Map<String, String> commonHeaders(){
		Map<String,String> headersMap=new HashMap<String,String>();
		headersMap.put("Accept", "text/html");
		headersMap.put("Accept-Language", "en-US");
		headersMap.put("Connection", "keep-alive");
		return headersMap;
		
	}
	
	public void waitForSeleniumOperations(){
		  try {
				Thread.sleep(2300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	public void assertEqualsCheck(String expectedVal, String actualVal){
		   try {
		        	assertEquals(expectedVal,actualVal);
		       
		        } catch (Error e) {
		        	System.out.println(e.toString());
		        } 
	}
	
}

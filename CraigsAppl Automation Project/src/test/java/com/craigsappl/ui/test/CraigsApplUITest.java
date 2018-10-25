package com.craigsappl.ui.test;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.craigsappl.ui.pages.LoginPage;
import com.craigsappl.ui.pages.UpdateAndDelSearVehiclePage;
import com.craigsappl.util.CraigsApplUtil;


/* Author: Srujan
 * This is the root class of UI automation testing. Involves in verifying of Application login, update and delete searched values
 */
public class CraigsApplUITest
{
WebDriver driver;
Properties configProps, uiProps;
// this has to be commonly used for both UI and API W/O creating two seperate objects
CraigsApplUtil craigsApplUtil;

@BeforeTest
public void setup()
{
	try{
		configProps=new Properties();
		configProps.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		
		uiProps=new Properties();
		craigsApplUtil=new CraigsApplUtil();
		uiProps.load(getClass().getClassLoader().getResourceAsStream("uiproperties.properties"));
		
		// need to write if, else conditions based on the provided driver
		if(configProps.getProperty("driverName").equalsIgnoreCase("chrome")){
			ChromeOptions options = new ChromeOptions();
			options.setExperimentalOption("useAutomationExtension", false);

			driver=new ChromeDriver();
		}
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS); 
		driver.manage().window().maximize();
		Thread.sleep(5000);
	}
	catch (IOException ex) {
        ex.printStackTrace();
    }
	catch(Exception e){
		e.printStackTrace();
	}
  
}

//This method is to verify Login Credentials of the Application

@Test(priority=1)
public void loginToAppl()
{
	driver.get(configProps.getProperty("craigsListApplURL"));  
	Assert.assertTrue(driver.getTitle().contains(uiProps.getProperty("craigsAppl.subtitle"))); 	
	LoginPage login=new LoginPage(driver,configProps);
    login.click_myAccount();
	login.set_username(uiProps.getProperty("username"));
    login.set_password(uiProps.getProperty("password"));
    login.click_button();
    // verify the user name displayed on home page after login
    Assert.assertTrue(driver.getPageSource().contains("home of "+uiProps.getProperty("username")));
    craigsApplUtil.waitForSeleniumOperations();
}

	
@Test(priority=2)
public void verifyAndDeleteUpdatedVehicleVal()
{
    UpdateAndDelSearVehiclePage updateAndDelSearchedVehicle=new UpdateAndDelSearVehiclePage(driver);
    updateAndDelSearchedVehicle.clickOnSearchesLink();
    craigsApplUtil.assertEqualsCheck(uiProps.getProperty("craigsAppl.searchedVehicleName"), driver.findElement(By.xpath("//table//tbody//tr[1]/td[3]")).getText());

    updateAndDelSearchedVehicle.clickOnEditButton();
    craigsApplUtil.waitForSeleniumOperations();
    updateAndDelSearchedVehicle.updateAndSaveVehicleName(uiProps.getProperty("craigsAppl.updateSearchedVehicleName"));
   
    // need to keep these kind of assertions in one Properties file
    String updateCheck="Your edits to \"toyota prius\" have been saved.";
    
    Assert.assertTrue(driver.findElement(By.xpath("//div[@class='alert alert-md alert-success saved-searches-alert']/p")).getText().contains(updateCheck));
    craigsApplUtil.waitForSeleniumOperations();
    
    updateAndDelSearchedVehicle.deleteSearchVal();
    String deletionCheck="\"toyota prius\" was deleted";
    Assert.assertTrue(driver.findElement(By.xpath("//div[@class='alert alert-md alert-warning saved-searches-alert']/p")).getText().contains(deletionCheck));
    craigsApplUtil.waitForSeleniumOperations(); 
}

@AfterTest
public void close()
{
	craigsApplUtil.waitForSeleniumOperations(); 
    driver.close();
}
}
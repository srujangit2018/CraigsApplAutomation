package com.craigsappl.ui.pages;

import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/* Author: Srujan
 * This class is used to login to CraigsList Appl at UI level and it contains pom attributes needed for Login */

public class LoginPage 
{
    private WebDriver driver;
    private Properties objProps;
    
     
       @FindBy(linkText="my account")
       WebElement myAccountLink;
       @FindBy(id="inputEmailHandle")
       WebElement username;
       @FindBy(id="inputPassword")
       WebElement password;
       @FindBy(className="accountform-btn")
       WebElement button;
       public LoginPage(WebDriver driver, Properties objProps)
       {
        //initialize elements
          PageFactory.initElements(driver, this);
          this.objProps=objProps;

       }
       public void click_myAccount()
       {
    	  // driver.findElement(By.linkText(objProps.getProperty("myAccountLink"))).click();
    	   myAccountLink.click();;
       }
       public void set_username(String usern)
       {
    	   username.clear();
    	   username.sendKeys(usern);
       }
       public void set_password(String userp)
       {
    	   password.clear();
    	   password.sendKeys(userp);
       }
       public void click_button()
       {
    	   button.submit();
       }
}

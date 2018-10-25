package com.craigsappl.ui.pages;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/* Author: Srujan
 * This class is used to update searched vehicle value of  CraigsList Appl and it contains pom attributes needed for Update and delete of search operations
 * */

public class UpdateAndDelSearVehiclePage {
	private WebDriver driver;
	
	@FindBy(linkText="searches")
	WebElement searchesLink;
    @FindBy(xpath="//*[contains(text(),'edit')]")
    WebElement editButton;
    @FindBy(name="subName")
    WebElement newSearchVal;
    @FindBy(xpath="//button[contains(text(),'save')]")
    WebElement saveButton;
    @FindBy(xpath="//button[contains(text(),'delete')]")
    WebElement deleteButton;
    public UpdateAndDelSearVehiclePage(WebDriver driver)
    {
     //initialize elements
       PageFactory.initElements(driver, this);

    }
    public void clickOnSearchesLink()
    {
    	searchesLink.click();
    }
    public void clickOnEditButton()
    {
    	editButton.click();
    }
    public void updateAndSaveVehicleName(String updatedSearchVal)
    {
    	newSearchVal.clear();
    	newSearchVal.sendKeys(updatedSearchVal);
    	saveButton.click();
    }
    public void deleteSearchVal()
    {
    	deleteButton.click();
    }

}

package com.craigsappl.api;
import java.io.IOException;
import java.util.Properties;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

/* Author: Srujan
 * This class is used to provide some base configurations for CraigsList Appl */

public class CraigsApplBase {
    public RequestSpecification REQUEST;
    public static Properties props=null;

    public CraigsApplBase() {
        try {
        	props= new Properties();
            props.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
           } catch (IOException ex) {
            ex.printStackTrace();
        }
        //basic request setting
        //REQUEST = RestAssured.given().contentType("text/html");
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package car;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.hamcrest.CoreMatchers.is;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.Keys;

/**
 *
 * @author nikolai
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCar {

    private static final int maxWait = 4;
    static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\nikolai\\Documents\\NetBeansProjects\\DriversSelenium\\chromedriver.exe");

        //reset DB
        com.jayway.restassured.RestAssured.given().get("http://localhost:3000/reset");
        driver = new ChromeDriver();
        driver.get("http://localhost:3000");

    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
        //Reset DB
        com.jayway.restassured.RestAssured.given().get("http://localhost:3000/reset");
    }

    //opg 1
    @Test
    public void dataIsLoaded() {
        (new WebDriverWait(driver, maxWait)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                WebElement e = d.findElement(By.tagName("tbody"));
                List<WebElement> rows = e.findElements(By.tagName("tr"));
                assertThat(rows.size(), is(5));
                return true;
            }
        });
    }
    //opg 2
    @Test
    public void testFilterText(){
        driver.get("http://localhost:3000");
        WebElement element = driver.findElement(By.id("filter"));
        element.sendKeys("2002");
        element.click();
        
         (new WebDriverWait(driver, maxWait)).until(new ExpectedCondition<Boolean>()  {
            public Boolean apply(WebDriver d) {
                WebElement e = d.findElement(By.tagName("tbody"));
                List<WebElement> rows = e.findElements(By.tagName("tr"));
                assertThat(rows.size(), is(2));
                return true;
            }
        });
    }
    
    
    //opg 3
    @Test
    public void testClearText(){
        driver.get("http://localhost:3000/reset");
        driver.manage().timeouts().implicitlyWait(maxWait, TimeUnit.SECONDS);
       
        WebElement element = driver.findElement(By.id("filter"));
//        element.clear();
//        element.click();
        element.sendKeys(Keys.BACK_SPACE);
        
         (new WebDriverWait(driver, maxWait)).until(new ExpectedCondition<Boolean>()  {
            public Boolean apply(WebDriver d) {
                WebElement e = d.findElement(By.tagName("tbody"));
                List<WebElement> rows = e.findElements(By.tagName("tr"));
                assertThat(rows.size(), is(5));
                return true;
            }
        });
    }
    //opg4

    @Test
    public void testSortButtonYear() throws Exception {
        WebElement element = driver.findElement(By.tagName("thead")).findElement(By.tagName("tr"));
        List<WebElement> rows = element.findElements(By.tagName("th"));
        rows.get(1).findElement(By.tagName("a")).click();

        (new WebDriverWait(driver, maxWait)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                WebElement e = d.findElement(By.tagName("tbody"));
                List<WebElement> rows = e.findElements(By.tagName("tr"));
                String firstRow = rows.get(0).findElements(By.tagName("td")).get(0).getText();
                String lastRow = rows.get(4).findElements(By.tagName("td")).get(0).getText();

                assertThat(firstRow, is("938"));
                assertThat(lastRow, is("940"));
                return true;
            }
        });
    }

    //opg 5
    @Test
    public void testEditCar() {
        List<WebElement> rows = driver.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
        WebElement element = null;
        //find element with id 938 in the tbody
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).findElements(By.tagName("td")).get(0).getText().equalsIgnoreCase("938")) {
                element = rows.get(i);
                break;
            }
        }

        element = element.findElements(By.tagName("td")).get(7).findElements(By.tagName("a")).get(0);
        //click edit button
        element.click();
        //clear description input field
        driver.findElement(By.id("description")).clear();
        
        
        //WAIT for DOM to be executed
        driver.manage().timeouts().implicitlyWait(maxWait, TimeUnit.SECONDS);
        //edit description in input field
        driver.findElement(By.id("description")).sendKeys("cool cars");

        //click the save button
        driver.findElement(By.id("save")).click();

        //check that the tbody td, id 938 description is cool cars(check DOM works)
        (new WebDriverWait(driver, maxWait)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                WebElement e = d.findElement(By.tagName("tbody"));
                List<WebElement> rows = e.findElements(By.tagName("tr"));
                String result = null;
                for (int i = 0; i < rows.size(); i++) {
                    if (rows.get(i).findElements(By.tagName("td")).get(0).getText().equalsIgnoreCase("938")) {
                        result = rows.get(i).findElements(By.tagName("td")).get(5).getText();
                        break;
                    }
                }
                assertThat(result, is("cool cars"));
                return true;
            }
        });
    }
    
    //opg6
    @Test
    public void testSaveCareErrorMsg(){
        driver.findElement(By.id("new")).click();
        driver.findElement(By.id("save")).click();
        
        //check there is a error msg, in the DOM
        (new WebDriverWait(driver, maxWait)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                String result = d.findElement(By.id("submiterr")).getText();
                assertThat(result, is("All fields are required"));
                return true;
            }
        });
    }
    
    //opg 7
    @Test
    public void testAddNewCar(){
        driver.findElement(By.id("new")).click();
        driver.findElement(By.id("year")).sendKeys("2008");
        driver.findElement(By.id("registered")).sendKeys("2002-05-05");
        driver.findElement(By.id("make")).sendKeys("Kia");
        driver.findElement(By.id("model")).sendKeys("Rio");
        driver.findElement(By.id("description")).sendKeys("As new");
        driver.findElement(By.id("price")).sendKeys("31000");
        
        driver.findElement(By.id("save")).click();
        
         //WAIT for DOM to be executed
          (new WebDriverWait(driver, maxWait)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                WebElement e = d.findElement(By.tagName("tbody"));
                List<WebElement> rows = e.findElements(By.tagName("tr"));
                assertThat(rows.size(), is(6));
                
                //last row, should be the new row, year = 2008
                assertThat(rows.get(5).findElements(By.tagName("td")).get(1).getText(), is("2008"));
                //price = 31000
                assertThat(rows.get(5).findElements(By.tagName("td")).get(6).getText(), is("31.000,00 kr."));
                return true;
            }
        });

    }

}

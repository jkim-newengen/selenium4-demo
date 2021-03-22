package com.newengen.qa.ui.lorian.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BaseTests {

    protected static ChromeDriver driver;
    protected static DevTools devTools;
    protected static AccountId accountId;

    @BeforeAll
    public static void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        devTools = driver.getDevTools();
    }

    public void start(String userName, String password) {
        accountId = new AccountId("37", "dev Test Client");
        driver.navigate().to("https://platform.byjove.com");
        signIn(userName, password);
    }

    public void signIn(String userName, String password) {
        WebElement userNameBox = visible($x("//input[@type='email']"));
        WebElement passwordBox = visible($x("//input[@type='password']"));
        WebElement signInButton = visible($x("//button[contains(text(), 'Sign In')]"));
        userNameBox.clear();
        userNameBox.sendKeys(userName);
        passwordBox.clear();
        passwordBox.sendKeys(password);
        signInButton.click();
    }

    public void selectAccountByName() {
        By accountSelectBox = $x("//*[contains(@class, 'AccountSelectionCardHeader')]//input");
        By accountSelectListItems = $x("//a[contains(@class, 'AccountSelectionListItem')]");

        driver.findElement(accountSelectBox).clear();
        driver.findElement(accountSelectBox).sendKeys(accountId.getName());

        List<WebElement> visibles = visibles(accountSelectListItems, 1, Duration.ofSeconds(2));
        WebElement selectedAccount = visibles.get(0);
        selectedAccount.click();
    }

    @AfterAll
    public static void tearDown() {
        devTools.close();
        driver.quit();
    }

    protected FluentWait<WebDriver> waitFor(WebDriver driver, Duration duration) {
        return new WebDriverWait(driver, duration)
            .ignoring(StaleElementReferenceException.class);
    }

    protected FluentWait<WebDriver> waitFor() {
        return new WebDriverWait(driver, Duration.ofSeconds(5))
            .ignoring(StaleElementReferenceException.class);
    }

    protected WebElement visible(By selector) {
        try {
            return waitFor().until(ExpectedConditions.visibilityOfElementLocated(selector));
        }
        catch (NoSuchElementException | TimeoutException x) {
            final String currentUrl = driver.getCurrentUrl();
            System.out.println("currentUrl: " + currentUrl);
            System.out.println("Exception: " + x);
            throw x;
        }
    }

    protected List<WebElement> visibles(By selector, int minCount, Duration duration) {
        List<WebElement> elements = waitFor(driver, duration)
            .until(ExpectedConditions.numberOfElementsToBeMoreThan(selector, minCount - 1));

        if (elements.size() > 0) {
            return waitFor().until(ExpectedConditions.visibilityOfAllElements(elements));
        } else {
            return new ArrayList<>();
        }
    }

    protected By $x(String ... xpathExpressions) {
        return By.xpath(String.join("", xpathExpressions));
    }

}
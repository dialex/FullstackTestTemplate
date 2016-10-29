package com.diogonunes.fullstack.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.NoSuchElementException;

import static com.diogonunes.fullstack.helpers.Constants.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class PageObject {

    private WebDriver driver;

    PageObject(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public abstract String getEndpoint();

    public abstract String getExpectedCssElement();

    public abstract PageObject waitForPage();

    public int countErrors() {
        return countElements(ERROR_CSS_SELECTOR);
    }

    // === Package methods ===

    WebDriver getDriver() {
        return driver;
    }

    int countElements(String cssSelector) {
        return driver.findElements(By.cssSelector(cssSelector)).size();
    }

    boolean isElementVisible(String cssSelector) {
        try {
            return getElement(cssSelector).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    boolean isElementEnabled(String cssSelector) {
        try {
            return getElement(cssSelector).isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    boolean isElementClickable(String cssSelector) {
        try {
            waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    String composeElementSelector(String cssInnerSelector, String cssOuterSelector, int indexOuterElement) {
        if (indexOuterElement == LAST)
            indexOuterElement = driver.findElements(By.cssSelector(cssOuterSelector)).size();
        else
            indexOuterElement++; //nth-of-type index starts on 1
        cssOuterSelector += String.format(":nth-of-type(%1$d)", indexOuterElement);

        return cssOuterSelector + " " + cssInnerSelector;
    }

    WebElement getElement(String cssSelector) {
        return getElement(By.cssSelector(cssSelector));
    }

    WebElement getElement(By selector) {
        return getDriver().findElement(selector);
    }

    List<WebElement> getElements(String cssSelector) {
        return getDriver().findElements(By.cssSelector(cssSelector));
    }

    void setFieldValue(String cssSelector, String input) {
        WebElement field = getElement(cssSelector);
        assertThat("Field is visible at " + cssSelector, field.isDisplayed(), equalTo(true));
        assertThat("Field is editable at " + cssSelector, field.isEnabled(), equalTo(true));

        // DISCLAIMER: field.clear() doesn't work with React... the field gets filled again 1sec later
        field.sendKeys(Keys.CONTROL + "A");
        field.sendKeys(Keys.DELETE);

        field.sendKeys(input);
    }

    void setCheckbox(String cssSelector, boolean status) {
        if (getElement(cssSelector).isSelected() != status)
            getElement(cssSelector).click();
    }

    void waitAndClick(String cssSelector) {
        waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
        getElement(cssSelector).click();
    }

    void waitAndClick(By selector) {
        waitUntil(ExpectedConditions.elementToBeClickable(selector));
        getElement(selector).click();
    }

    void waitUntil(ExpectedCondition<?> expectedCondition) {
        waitUntil(expectedCondition, DEFAULT_WAIT_TIMEOUT);
    }

    void waitUntil(ExpectedCondition<?> expectedCondition, int timeoutSeconds) {
        WebDriverWait driverWait = new WebDriverWait(driver, timeoutSeconds);
        driverWait.until(expectedCondition);
    }

    void waitUntilLambda(ExpectedCondition<Object> expectedCondition, String timeoutMsg) {
        waitUntilLambda(expectedCondition, timeoutMsg, DEFAULT_WAIT_TIMEOUT);
    }

    void waitUntilLambda(ExpectedCondition<Object> expectedCondition, String timeoutMsg, int timeoutSeconds) {
        try {
            waitUntil(expectedCondition, timeoutSeconds);
        } catch (TimeoutException e) {
            throw new TimeoutException("Timeout " + timeoutMsg, e);
        }
    }

    void waitUntilPageLoads(String expectedCssElement) {
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.cssSelector(expectedCssElement)));
    }

    void waitUntilPageLoadsOrError(String expectedCssElement) {
        waitUntil(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(expectedCssElement)),    //happy path
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(ERROR_CSS_SELECTOR))));  //sad path
    }

    void waitUntilPageLoadsOrContinue(String expectedCssElement) {
        try {
            waitUntilPageLoads(expectedCssElement);
        } catch (TimeoutException e) {
            // ...or continue
        }
    }
}

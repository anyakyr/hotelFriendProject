package utility.services;

import lombok.extern.log4j.Log4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utility.Log;

import java.util.Collections;
import java.util.List;

import static utility.Constants.*;
import static utility.Log.info;
import static utility.Log.warn;
import static utility.services.ReportService.assertTrue;
import static utility.services.ReportService.catchException;
import static utility.services.WebElementService.*;


@Log4j
public class WaiterService {

    @Deprecated
    public static boolean waitForElementDisappear(WebElement element, String elementName) {
        info("Wait for element: \"" + elementName + "\" disappear.");
        int attempt_counter = 0;
        while (elementIsDisplayed(element, elementName)) {
            sleep(1);
            attempt_counter++;
            if (attempt_counter == 5) {
                assertTrue(false, "\"" + elementName + "\" not disappeared after timeout.");
                break;
            }
        }
        return true;
    }



    public static void waitForTextVisible(String text, WebElement element, WebDriver driver) {

        try {
            WebDriverWait wait = new WebDriverWait(driver,40);
            wait.until(ExpectedConditions.textToBePresentInElement(element, text));
            info("TEXT: \"" + text +"\" is present.");
        }
        catch (TimeoutException e){
            assertTrue(false, "TEXT: \"" + text + "\" is not presents.");
        }
        catch (NoSuchElementException e){
            assertTrue(false, "This element not found.");
        }

    }

    public  static void waitForTextIsEmpty(WebElement element){
        try {
            int attempt_counter = 0;
            while (!(element.getText().isEmpty())){
                sleep(1);
                attempt_counter++;
                if (attempt_counter == 10){
                    assertTrue(false,"Unnecessary string still present after "+attempt_counter+" seconds waiting "+element.getText());
                    break;
                }
            }
        }
        catch ( NoSuchElementException e){
            Log.error("Caught exception" + e);
        }
    }


    public static void waitForElementVisible(WebElement element, WebDriver driver) {

        try {
            WebDriverWait wait = new WebDriverWait(driver,20);
            wait.until(ExpectedConditions.visibilityOf(element));
        }
        catch (TimeoutException e){
            assertTrue(false, "ELEMENT: \"" + element + "\" is not presents.");
        }
        catch (StaleElementReferenceException e){
            warn("ELEMENT: \"" + element + "\" is not found in the cache.");
        }

    }

    public static void waitForElementVisibleWithReload(WebElement element, WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver,80, 5);
        wait.until((ExpectedCondition<Boolean>) d -> {
            ManageUrlService.refreshPage(driver);
            return element.isDisplayed();
        });
    }

    public static void waitForElementVisible(WebElement element, int delay, WebDriver driver) {

        try {
            WebDriverWait wait = new WebDriverWait(driver,delay);
            wait.until(ExpectedConditions.visibilityOf(element));
            //Log.info("ELEMENT: \"" + element +"\" is present.");
        }
        catch (TimeoutException e){
            warn("ELEMENT: \"" + element + "\" is not presents.");
        }
        catch (StaleElementReferenceException e){
            warn("ELEMENT: \"" + element + "\" is not found in the cache.");
        }

    }

    public static void sleep(int seconds){
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public  static void waitForCookieValue(String cookie, String value, WebDriver driver){
        int attempt = 0;
        boolean flag = true;
        info("Waiting for cookie \""+cookie+"\" became - "+value+".");
        while (flag && attempt<10){
            attempt++;
            sleep(1);
            if (driver.manage().getCookieNamed(cookie).getValue().equals(value)){
                flag = false;
                info("Cookie "+cookie+"\" became - "+value+".");
            }
        }
    }

    public static void waitForValue(WebElement element, String text){
        boolean flag = true;
        int attempt = 0;
        while (flag && attempt<10){
            attempt++;
            sleep(1);
            if (element.getAttribute("value").equals(text)){
                flag = false;
                info("Element has expected value.");
            }
        }
    }

    public static void waitPageLoader(String url, WebDriver driver) {
        waitPageLoader(url, PAGE_ATTEMPT, driver);
    }

    public static void waitPageLoader(String url, int seconds, WebDriver driver) {
        try {
            info("Waiting for \"" + url + "\" page.");
            int attempt = 0;
            while (!driver.getCurrentUrl().contains(url) && attempt < seconds) {
                attempt++;
                sleep(1);
            }
            info("Waiting for \"" + url + "\" page during " + attempt + " seconds.");
            if (!driver.getCurrentUrl().contains(url)) {
                assertTrue(false, "Expected page hasn't loaded  by timeout.\n" +
                        "                                   Current url:" + driver.getCurrentUrl());
            }
        } catch (TimeoutException e) {
            ManageUrlService.stopLoad(driver);
        }
    }

    public static void waitForElementDisappear(WebElement element,int seconds, String elementName){
        info("Wait for element: \"" + elementName + "\" disappear.");
        int attempt_counter = 0;
        while (elementIsDisplayed(element,elementName)) {
            sleep(1);
            attempt_counter++;
            if (attempt_counter == seconds){
                assertTrue(false, "\"" + elementName + "\" not disappeared after timeout.");
                break;
            }

        }
    }



    public static void waitPageLoader(WebDriver driver){
        try {
            info("Waiting for change url.");
            final String previousUrl = driver.getCurrentUrl();
            WebDriverWait wait = new WebDriverWait(driver,PAGE_TIMEOUT);
            wait.until((ExpectedCondition<Boolean>) driver1 -> (!driver1.getCurrentUrl().equals(previousUrl)));
        }
        catch (TimeoutException e){
            assertTrue(false, "Expected page hasn't changed  by timeout.\n" +
                    "                                   Current url:"+driver.getCurrentUrl());
        }
    }
    public static void waitForAttributeValueDisappear(final WebElement element, final String attribute, final String text, WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 10);

            wait.until((ExpectedCondition<Boolean>) driver1 -> {
                String enabled = element.getAttribute(attribute);
                return !enabled.contains(text);
            });
        } catch (TimeoutException e) {
            assertTrue(false, "Value: \"" + text + "\". Attribute: \"" + attribute + "\" is presents in element.");
        }
    }

    public static void waitForList(By by, WebDriver driver) {
        log.info("Wait for list of elements");
        try {
            WebDriverWait wait = new WebDriverWait(driver, ELEMENT_TIMEOUT);
            wait.withMessage("List of element is empty after timeout");
            wait.until((ExpectedCondition<Boolean>) d -> !driver.findElements(by).isEmpty());
        } catch (TimeoutException e) {
            catchException(e);
        }
    }

    public static void waitForElementIsSelected(WebElement el, WebDriver driver) {
        log.info("Wait for element to be selected");
        try {
            WebDriverWait wait = new WebDriverWait(driver, ELEMENT_TIMEOUT);
            wait.withMessage("Element is not selected after timeout");
            wait.until((ExpectedCondition<Boolean>) d -> el.isSelected());
        } catch (TimeoutException e) {
            catchException(e);
        }
    }

    public static void waitForJSandJQueryToLoad(final WebDriver driver) {
        ExpectedCondition<Boolean> jQueryLoad = driver1 -> {
            try {
                return ((Long) ((JavascriptExecutor) driver).executeScript("return !!window.jQuery && jQuery.active") == 0);
            } catch (Exception e) {
                return true;
            }
        };

        ExpectedCondition<Boolean> jsLoad = driver2 -> ((JavascriptExecutor) driver).executeScript("return document.readyState")
                .toString().equals("complete");

        try {
            WebDriverWait wait = new WebDriverWait(driver, PAGE_TIMEOUT);
            wait.until(jQueryLoad);
            wait.until(jsLoad);
        } catch (TimeoutException e) {
            info("No one jQuery or Js activity");
        }
    }

    public static void waitForList(List<WebElement> list, WebDriver driver) {
        waitForList(list, ELEMENT_TIMEOUT, driver);
    }

    public static void waitForList(List<WebElement> list, int delay, WebDriver driver) {
        log.info("Wait " + delay + " seconds for list of elements");
        try {
            WebDriverWait wait = new WebDriverWait(driver, delay);
            wait.withMessage("List of elements not loaded after timeout");
            wait.until((ExpectedCondition<Boolean>) d -> !list.isEmpty());
        } catch (TimeoutException e) {
            catchException(e);
        }
    }

    public static void waitForListSize(List<WebElement> list, int size, WebDriver driver) {
        log.info("Wait 20 seconds for list of elements with size - " + size);
        try {
            WebDriverWait wait = new WebDriverWait(driver, ELEMENT_TIMEOUT);
            wait.withMessage("List of elements with size: " + size + " was not loaded after timeout");
            wait.until((ExpectedCondition<Boolean>) d -> list.size() == size);
        } catch (TimeoutException e) {
            catchException(e);
        }
    }

    public static void waitForListTexts(List<WebElement> list, WebDriver driver){
        try {
            WebDriverWait wait = new WebDriverWait(driver, ELEMENT_TIMEOUT);
            wait.until((ExpectedCondition<Boolean>) d -> !list.get(list.size()-1).getText().isEmpty());
        }catch (TimeoutException e){
            catchException(e);
        }
    }

    public static void waitForChangeUrl(WebDriver driver, final String urlPart) {
        log.info("Wait for URL contains - " + urlPart);
        try {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.withMessage("URL not contains " + urlPart + " after timeout");
            wait.until((ExpectedCondition<Boolean>) dr -> (!driver.getCurrentUrl().contains(urlPart.toLowerCase())));
        } catch (TimeoutException e) {
            catchException(e);
        }
    }

    public static void waitForElementClickable(WebElement element, WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver,20);
            wait.until(ExpectedConditions.elementToBeClickable(element));
        }
        catch (TimeoutException e){
            assertTrue(false, "ELEMENT: \"" + element + "\" is not clickable.");
        }
        catch (StaleElementReferenceException e){
            warn("ELEMENT: \"" + element + "\" is not found in the cache.");
        }
    }



    /**
     * @param driver   webDriver for waiter
     * @param element  Element to check attribute
     * @param attrName attribute name
     */
    public static void waitForAttributeAppear(WebDriver driver, WebElement element, String attrName) {
        log.info("Wait for attribute '" + attrName + "' in element");
        try {
            WebDriverWait wait = new WebDriverWait(driver, ELEMENT_TIMEOUT);
            wait.withMessage("Attribute '" + attrName + "' not appear in element after timeout");
            wait.until((ExpectedCondition<Boolean>) d -> element.getAttribute(attrName) != null);
        } catch (TimeoutException e) {
            catchException(e);
        }
    }

    public static void waitForAttributeValueNotEmpty(WebDriver driver, WebElement element, String attrName) {
        log.info("Wait for attribute " + attrName + " is not empty.");

        WebDriverWait wait = new WebDriverWait(driver, ELEMENT_TIMEOUT);
        wait.withMessage("Failed to wait for attribute " + attrName + " is not empty.");
        wait.until((ExpectedCondition<Boolean>) d -> !element.getAttribute("href").isEmpty());
    }


     /** The method waits until some Jquery activity will be completed,
         *  in the most cases it matters to spinners
         * @param timeout time for waiting
         * @param driver WebDriver
         */
        public static void waitJqueryComplete(WebDriver driver,long timeout){
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            try {
                wait.until((WebDriver webDriver) -> ((JavascriptExecutor)driver).executeScript("return !!window.jQuery && window.jQuery.active == 0"));
            } catch (TimeoutException e) {
                info("No one jQuery activity or activity continues");
            }
        }



    /** The method waiting when locator is appear
     * @param locator should be present
     * @param driver WebDriver
     */
    public static void waitForElementIsPresent(By locator, WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, ELEMENT_TIMEOUT);
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        }catch (TimeoutException e){
            catchException(e);
        }
    }

    /**
     * The method expects change size of list (for example use for checking lazy-load)
     *
     * @param driver          webDriver
     * @param list            list of elements that method checks
     * @param sizeAfterChange size of list after changing
     */
    public static void waitForListChangeSize(WebDriver driver, List<WebElement> list, int sizeAfterChange) {
        log.info("Wait for size of list changed");
        try {
            WebDriverWait wait = new WebDriverWait(driver, ELEMENT_TIMEOUT);
            wait.withMessage("Size of list not changed after timeout");
            wait.until((ExpectedCondition<Boolean>) d -> list.size() == sizeAfterChange);
        } catch (TimeoutException e) {
            catchException(e);
        }
    }

    /**
     * The method waiting appear element's attribute.
     *
     * @param list     list of WebElement
     * @param attrName name of Attribute
     * @param driver   WebDriver
     */
    public static void waitForListAttribute(List<WebElement> list, String attrName, WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, ELEMENT_TIMEOUT);
            wait.until((ExpectedCondition<Boolean>) d -> list.get(list.size() - 1).getAttribute(attrName) != null);
        } catch (TimeoutException e) {
            catchException(e);
        }
    }

    public static void waitForCssValueChanged(WebElement element, String cssProperty, String oldCssValue,  WebDriver driver) {
        log.info("Waiting for changing CSS property - " + cssProperty + ". Old value is: " + oldCssValue);

        try {
            WebDriverWait wait = new WebDriverWait(driver, ELEMENT_TIMEOUT);
            wait.until((ExpectedCondition<Boolean>) d ->  !element.getCssValue(cssProperty).equals(oldCssValue));
        }
        catch (TimeoutException e){
            catchException(e);
        }

        log.info("Value changed. New value is: " + element.getCssValue(cssProperty));
    }

    public static void waitForElementNotVisible(WebElement element, int delay, WebDriver driver) {
        if (elementIsDisplayed(element, "")) {
            info("Wait for element will not be displayed in " + delay + " seconds.");
            try {
                WebDriverWait wait = new WebDriverWait(driver, delay);
                wait.until(ExpectedConditions.invisibilityOfAllElements(Collections.singletonList(element)));
                info("Element is not appear.");
            } catch (TimeoutException e) {
                warn("ELEMENT: \"" + element + "\" is present.");
                catchException(e);
            }
        }
    }

    public static void waitForTextVisible(String text, WebElement element, int delay, WebDriver driver) {

        try {
            WebDriverWait wait = new WebDriverWait(driver,delay);
            wait.until(ExpectedConditions.textToBePresentInElement(element, text));
            info("TEXT: \"" + text +"\" is present.");
        }
        catch (TimeoutException e){
            assertTrue(false, "TEXT: \"" + text + "\" is not presents.");
        }
        catch (NoSuchElementException e){
            assertTrue(false, "This element not found.");
        }

    }

    public static void waitForElementChangePosition(WebElement element, int x, int y, WebDriver driver) {
        log.info("Wait for element will change position in " + ELEMENT_TIMEOUT + " seconds.");
        try {
            WebDriverWait wait = new WebDriverWait(driver, ELEMENT_TIMEOUT);
            wait.until((ExpectedCondition<Boolean>) d ->
                    element.getLocation().getX() != x && element.getLocation().getY() != y);
            log.info("Element changed position");
        } catch (TimeoutException e) {
            assertTrue(false, "Element didn't change position");
        } catch (NoSuchElementException e) {
            assertTrue(false, "This element not found.");
        }
    }

    public static void waitForElementValueEquals(WebElement element, String expectedValue, WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, PAGE_TIMEOUT, 2000);
        wait.withMessage("Content of attribute 'value' of element " + element + " is not changed");
        wait.until((ExpectedCondition<Boolean>) d -> {
            String value = element.getAttribute("value");
//            log.info("Current value - '" + value + "'. Should be - " + expectedValue);
            return value.equals(expectedValue);
        });
    }

    /**
     * @param element which we waiting that he will disappear
     * @param driver
     */
    public static void waitForElementDisappear(WebElement element, int timeOut, WebDriver driver) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, timeOut);
                wait.until(ExpectedConditions.invisibilityOfElementLocated(webElementToBy(element)));
                info("Element disappeared.");
            }
            catch (TimeoutException e){
                catchException(e);
            }
        }

}

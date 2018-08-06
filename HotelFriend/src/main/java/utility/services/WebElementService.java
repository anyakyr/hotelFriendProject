package utility.services;

import lombok.NonNull;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utility.Log;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static utility.Log.*;
import static utility.services.ManageUrlService.scrollDown;
import static utility.services.ManageUrlService.stopLoad;
import static utility.services.PressKeysService.paste;
import static utility.services.PressKeysService.pressBackSpace;
import static utility.services.ReportService.assertTrue;
import static utility.services.WaiterService.*;


public class WebElementService {

    public static void selectTextInDDByText(WebElement dropDown, String text) {
        try{
            Select optionsFromDD = new Select(dropDown);
            optionsFromDD.selectByVisibleText(text);
            Log.info(text + " was selected in DropDown");
        } catch (Exception e){
            Log.error("Can not work with DropDown");
            Assert.fail("Can not work with DropDown");
        }
    }

    public static void selectValueInDDByValue(WebElement dropDown, String value) {
        try{
            Select optionsFromDD = new Select(dropDown);
            optionsFromDD.selectByValue(value);
            Log.info(value + " was selected in DropDown");
        } catch (Exception e){
            Log.error("Can not work with value in DropDown");
            Assert.fail("Can not work with value in DropDown");
        }
    }

    public static boolean getCheckboxState (WebElement checkbox) {
        if(checkbox.isSelected()){
            Log.info("Element is selected");
            return true;
        }
        else {
            Log.info("Element isn't selected");
            return false;
        }

    }

    public static void setCheckboxState(WebElement checkbox, String expectedState){
        // чекнутый
        boolean actualState = getCheckboxState(checkbox);

        // чекнутый чекнуть
        if (actualState == true && expectedState == "checked"){
            Log.info("Checkbox is checked");
        }
        // нечекнутый чекнуть
        else if (actualState == true && expectedState == "unchecked"){
            checkbox.click();
            Log.info("Checkbox is checked");
        }
        // нечекнутый
        else if (actualState == false && expectedState == "checked"){
            checkbox.click();
            Log.info("Checkbox is checked");
        }
        else if (actualState == false && expectedState == "unchecked"){
            Log.info("Checkbox is unchecked");
        }
    }

    public static boolean getRadioButtonState (WebElement radioButton) {

        if(radioButton.isSelected()){
            Log.info("Element is selected");
            return true;
        }
        else {
            Log.info("Element isn't selected");
            return false;
        }

    }

    public static boolean checkFocusOnElement(WebElement element, String elementName, WebDriver driver){
        int attempt_counter = 0;
        while (!element.equals(driver.switchTo().activeElement())){
            sleep(1);
            attempt_counter++;

            if (attempt_counter == 5){
                error("Break, element isn't focused by timeout.");
                break;
            }
        }
        //Check that field is focused.
        if(element.equals(driver.switchTo().activeElement())){
            info("\"" + elementName + "\" field is focused.");
            return true;
        }
        else {
            info("\"" + elementName + "\" field is NOT focused.");
            return false;
        }
    }
    public static boolean elementIsDisplayed(WebElement element, String elementName){

        try {

            if (element != null && element.isDisplayed()){
                //Log.info("\"" + elementName + "\" is displayed.");
                return true;
            }
            else {
                info("\"" + elementName + "\" is not displayed.");
                return false;
            }
        }
        catch (NoSuchElementException e){
            info("\"" + elementName + "\" is NOT displayed.");
            return false;
        }
        catch (ElementNotVisibleException e){
            assertTrue(false, "\"" + elementName + "\" was not visible.");
            return false;
        }
        catch (StaleElementReferenceException e){
            //ReportService.assertTrue(false, "\"" + elementName + "\" was not in the cache.");
            return false;
        }
    }

    public static boolean isElementVisible(WebElement element) {
        try {
            if (element.isDisplayed()) {
                Log.info("Element is Displayed: " + element);
                return true;
            }
        }
        catch(Exception e) {
            Log.info("Element is Not Displayed: " + element);
            return false;
        }

        return false;
    }

    public static boolean elementIsPresent(WebElement element) {

        try {
            element.getText();
            //Log.info(element + " is present.");
            return true;
        }
        catch (NoSuchElementException | NullPointerException e){
            //Log.info(element+ " is not present.");
            return false;
        }

    }

    public static boolean isElementPresent(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isElementNotPresent(WebElement element) {
        try {
            return !element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean elementIsEnable(WebElement element, String elementName){

        if (element.isEnabled()){
            info("\"" + elementName + "\" is enabled.");
            return true;
        }
        else {
            info("\"" + elementName + "\" is disabled.");
            return false;
        }


    }

    public static boolean elementIsDisable(WebElement element, String elementName){

        if (!element.isEnabled()){
            info("\"" + elementName + "\" is disabled.");
            return true;
        }
        else {
            info("\"" + elementName + "\" is enabled.");
            return false;
        }


    }

    public static void clickOnElement(WebElement element, String elementName, WebDriver driver){

        try {
            WebDriverWait wait = new WebDriverWait(driver,20);
            wait.until(ExpectedConditions.elementToBeClickable(element));}
        catch (TimeoutException ex){
            info("\"" + elementName + "\" is not clickable.");
            clickHack(element, elementName, driver);
            info("Click on \"" + elementName + "\".");
            return;
        }
        try {
            element.click();
            info("Click on \"" +elementName+"\".");
        }
        catch (NoSuchElementException  e){
            assertTrue(false, "\"" + elementName + "\" was not found on page after timeout.");
        }
        catch (ElementNotVisibleException e){
            error("ElementNotVisibleException");
            clickHack(element, elementName, driver);
        }
        catch (TimeoutException e){
            stopLoad(driver);
        }
        catch (StaleElementReferenceException e){
            warn("StaleElementReferenceException.");
            info("Click on \"" +elementName+"\".");
            element.click();
        }
        catch (WebDriverException e){
            error("WebDriverException" +e);
            clickHack(element, elementName, driver);
        }
    }

    private static void clickHack(WebElement element, String elementName, WebDriver driver){
        boolean flag = true;
        int attempt = 0;

        while (flag && attempt<5){
            attempt++;
            try {
                info("\"" + elementName + "\" is hide by another element, move down.");
                scrollDown(driver,500);
                moveToCoordinate(0, 0, driver);
                element.click();
                info("Click on \"" +elementName+"\".");
                flag = false;
            }
            catch (WebDriverException ignored){}
        }
    }

    public static boolean checkElementSelected(WebElement element, String elementName) {
        try {
            //Check WebElement is selected.
            info("Verify \"" + elementName + "\" is selected.");
            if (element.isSelected()) {
                info("\"" + elementName + "\" is selected.");
                return true;
            } else {
                info("\"" + elementName + "\" is NOT selected.");
                return false;
            }
        }
        catch (NoSuchElementException e) {
            assertTrue(false, "Element " + elementName + " was not found on page after timeout.");
            return false;
        }
    }

    public static String getElementValue(WebElement element, String elementName){
        return getElementAttribute(element, elementName, "value");
    }


    public static void sendKeys(WebElement element, String elementName, String inputText){

        try {
            element.sendKeys(inputText);
            info("\"" + elementName + "\" input text: \"" + inputText + "\".");
        }
        catch (NoSuchElementException e){
            assertTrue(false, "\"" + elementName + "\" was not found on page after timeout.");
        }
        catch (ElementNotVisibleException e){
            assertTrue(false, "\"" + elementName + "\" was not visible.");
        }

    }
    public static String getElementText(WebElement element, String elementName) {

        try {
            String text = element.getText();
            info("\"" + elementName +"\" content on page  - \"" + ObjectService.trimer(text) + "\".");
            return text;
        }
        catch (org.openqa.selenium.NoSuchElementException | ElementNotVisibleException e){
            assertTrue(false, "\"" + elementName + "\" was not found on page after timeout.");
            throw new CustomException(e.toString());
        }
    }

    public static void sendKeysClear(WebElement element, String elementName, String inputText, WebDriver driver){

        try {
            waitForElementVisible(element, driver);
            int attempt = 0;
            element.clear();
            element.sendKeys(inputText);
            while (element.getAttribute("value").length()!=inputText.length() && attempt<5){
                attempt++;
                element.clear();
                element.sendKeys(inputText);
            }
            info("\"" + elementName + "\" input text: \"" + inputText + "\".");
        }
        catch (NoSuchElementException e ){
            assertTrue(false, "\"" + elementName + "\" was not found on page after timeout.");
        }
        catch (ElementNotVisibleException e){
            assertTrue(false, "\"" + elementName + "\" was not visible.");
        }
        catch (InvalidElementStateException e){
            warn("Catch InvalidElementStateException.");
            WebDriverWait wait = new WebDriverWait(driver,10);
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.clear();
            element.sendKeys(inputText);
        }
    }

    public static int getWidth(WebElement element){
        return element.getSize().getWidth();
    }

    public static void moveInRange(WebElement element, WebDriver driver, int x, int y) {
        new Actions(driver)
                .moveToElement(element, x, y)
                .click()
                .perform();

        info("\"" + element + "\" was moved");
    }

    public static void moveToElement(WebElement element, String elementName, WebDriver driver) {
        try {
            waitForElementVisible(element,driver);
            Actions actions = new Actions(driver);
            actions.moveToElement(element).build().perform();
            sleep(1);
            info("\"" + elementName + "\" is active.");
        }
        catch (NoSuchElementException e){
            assertTrue(false, "\"" + elementName + "\" not found.");
        }
        catch (ElementNotVisibleException e){
            assertTrue(false, "\"" + elementName + "\" was not visible.");
        }
    }

    public static void clickAndHoldElement(WebElement element, String elementName, WebDriver driver) {
        try {
            waitForElementVisible(element,driver);
            new Actions(driver).clickAndHold(element).build().perform();
            sleep(1);
            info("\"" + elementName + "\" is hold.");
        }
        catch (NoSuchElementException e){
            assertTrue(false, "\"" + elementName + "\" not found.");
        }
        catch (ElementNotVisibleException e){
            assertTrue(false, "\"" + elementName + "\" was not visible.");
        }

    }

    public static void selectDropBoxByText(WebElement element, String text){
        try{
            Select select = new Select(element);
            select.selectByVisibleText(text);
            info("Select \""+text+"\".");
        }
        catch (NoSuchElementException e){
            assertTrue(false, "\""+text+"\" is missing!");
        }
    }

    public static void deSelectDropBoxByText(WebElement element, String text){
        try {
            Select select = new Select(element);
            select.deselectByVisibleText(text);
            info("Deselect \""+text+"\".");
        }
        catch (NoSuchElementException e){
            assertTrue(false, "\""+text+"\" is missing!");
        }
    }

    public static String getBackgroundColor(WebElement element, String elementName) {

        try {
            String color = element.getCssValue("background-color");
            info("Grab color - \""+color+"\".");

            return color;
        }
        catch (NoSuchElementException e){
            assertTrue(false, "\"" + elementName + "\" was not found on page after timeout.");
            return null;
        }
    }

    public static String getImage(WebElement element, String elementName) {

        try {
            String color = element.getCssValue("background-image");
            info("Grab color - \""+color+"\".");

            return color;
        }
        catch (NoSuchElementException e){
            assertTrue(false, "\"" + elementName + "\" was not found on page after timeout.");
            return null;
        }
    }

    public static String getElementFontColor(WebElement element, String elementName){


        try {
            switch (element.getCssValue("color")){
                case "rgba(216, 67, 21, 1)":
                    info("\"" + elementName + "\" font is red");
                    return "red";
                case "rgba(33, 150, 243, 1)":
                    info("\"" + elementName +"\" font is blue");
                    return "blue";
                case "rgba(144, 164, 174, 1)":
                    info("\"" + elementName +"\" font is grey");
                    return "grey";
                default:
                    info("\"" + elementName +"\" font is "+element.getCssValue("border-top-color"));
                    return "unknown";
            }
        }
        catch (NoSuchElementException e){
            assertTrue(false, "\"" + elementName + "\" was not found on page after timeout.");
            return null;
        }
        catch (ElementNotVisibleException e){
            assertTrue(false, "\"" + elementName + "\" was not visible.");
            return null;
        }

    }

    public static String getElementFontWeight(WebElement element, String elementName){
        try {
            return element.getCssValue("font-weight");
        }
        catch (NoSuchElementException e){
            assertTrue(false, "\"" + elementName + "\" was not found on page after timeout.");
            return "";
        }
        catch (ElementNotVisibleException e){
            assertTrue(false, "\"" + elementName + "\" was not visible.");
            return "";
        }

    }

    public static WebElement getElement(By locator, WebDriver driver){
        WebElement element = null;
        boolean flag  = true;
        int attempt = 0;
        while (flag && attempt<20){
            attempt++;
            sleep(1);
            try {
                element =  driver.findElement(locator);
                flag = false;
            }
            catch (StaleElementReferenceException e){
                warn("StaleElementReferenceException for "+locator);
                element = null;
            }
            catch (NoSuchElementException e){
                warn("NoSuchElementException");
            }
            catch (Exception e){
                assertTrue(false, "Unknown exception.");
            }
        }
        return element;
    }

    public static boolean isChildDisplayInParent(By locator, WebElement parent) {
        try{
            return parent.findElement(locator).isDisplayed();
        } catch (StaleElementReferenceException | NoSuchElementException e) {
            return false;
        }
    }

    public static WebElement getElement(By locator, WebElement parent) {
        WebElement element = null;
        boolean flag = true;
        int attempt = 0;
        while (flag && attempt < 5) {
            attempt++;
            sleep(1);
            try {
                element = parent.findElement(locator);
                flag = false;
            } catch (StaleElementReferenceException e) {
                warn("StaleElementReferenceException for " + locator);
            } catch (NoSuchElementException e) {
                warn("NoSuchElementException");
            } catch (Exception e) {
                assertTrue(false, "Unknown exception.");
            }
        }
        return element;
    }

    public static WebElement getElement(By locator, By parent, WebDriver driver){
        WebElement element = null;
        boolean flag  = true;
        int attempt = 0;
        while (flag && attempt<20){
            attempt++;
            sleep(1);
            try {
                WebElement webElement = driver.findElement(parent);
                element = webElement.findElement(locator);
                flag = false;
            }
            catch (StaleElementReferenceException e){
                warn("StaleElementReferenceException for "+locator);

            }
            catch (NoSuchElementException e){
                warn("NoSuchElementException");
            }
            catch (Exception e){
                assertTrue(false, "Unknown exception.");
            }
        }
        return element;
    }

    public static void clear(WebElement element, String elementName){
        element.clear();
        info("\""+elementName+"\" field clear.");
    }

    public static int countStringOnPage (String string, WebDriver driver){

        String pageSourse = driver.getPageSource();
        int count = 0;
        Pattern p = Pattern.compile(string, Pattern.UNICODE_CASE|Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(pageSourse);
        while(m.find()) count++;
        info("Find the line on the page - " + count);
        return count;

    }

    public static int countStringOnText (String string, String text){

        int count = 0;
        Pattern p = Pattern.compile(string, Pattern.UNICODE_CASE|Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        while(m.find()) count++;
        info("Find the line in the text - " + count);
        return count;

    }

    public static String getCanonicalLink(WebDriver driver){
        try {
            WebElement element = driver.findElement(By.xpath(".//head/link[@rel='canonical']"));
            return element.getAttribute("href");
        }
        catch (NoSuchElementException e){
            return null;
        }

    }

    public static By getXpathClassBlock(String block) {
        return By.xpath(".//*[contains(@class, '" + block + "')]");
    }

    public static boolean hybridCompare(String listWithValue[], String suffix, String string){

        boolean status = false;
        for (String prefix : listWithValue)
        {
            if (string.equals(prefix.concat(suffix)))
            {
                status = true;
                info("Matches found.");
                return status;
            }
        }
        info("Suffix -  " + "*"+suffix+"*; " + "String - *"+ string +"*");
        error("Matches not found!");
        return status;

    }

    public static void selectCheckBox(WebElement element, String elementName, WebDriver driver){
        if(!checkElementSelected(element, elementName)){
            WebElementService.clickOnElement(element,elementName, driver);
            info("Element " + elementName + " is selected.");
        }
        else {
            info("Element " + elementName + " is already selected.");
        }
    }

    public static void deSelectCheckBox(WebElement element, String elementName, WebDriver driver){
        if(checkElementSelected(element, elementName)){
            WebElementService.clickOnElement(element,elementName, driver);
            info("Element " + elementName + " is deselected.");
        }
        else {
            info("Element " + elementName + " is already deselected.");
        }
    }

    public static void deSelectCheckBox(WebElement isSelect, WebElement deSelect,String elementName, WebDriver driver){
        if(checkElementSelected(isSelect, elementName)){
            WebElementService.clickOnElement(deSelect,elementName, driver);
            info("Element " + elementName + " is deselected.");
        }
        else {
            info("Element " + elementName + " is already deselected.");
        }
    }

    public static void moveToCoordinate(int x, int y, WebDriver driver) {

        Actions actions = new Actions(driver);
        actions.moveByOffset(x, y).build().perform();
        info("Move to coordinate "+x+"x"+y);
    }

    public static By webElementToBy(WebElement element){

        String textElement = element.toString();

        //Get method.
        int index = textElement.indexOf("->")+3;
        String s = textElement.substring(index);
        String method = s.substring(0,s.indexOf(":"));

        //Get value method.
        String value = s.substring(s.indexOf(":")+2,s.length()-1);
        info("Method - \""+method+"\"; Value - \""+ value+"\"");

        //Default locator.
        By by;

        switch (method){
            case "id":
                by=By.id(value);
                break;
            case "class name":
                by=By.className(value);
                break;
            case "xpath":
                by=By.xpath(value);
                break;
            case "name":
                by=By.name(value);
                break;
            default:
                assertTrue(false,"Method undefined.");
                by=By.xpath(value);
        }
        return by;
    }

    public static Integer countDisplayedElements(WebElement element, WebDriver driver){
        int counter = 0;
        List<WebElement> list = driver.findElements(webElementToBy(element));
        for (WebElement webElement:list){
            if (webElement.isDisplayed()){
                counter++;
            }
        }
        info("Count displayed elements - " + counter);
        return counter;
    }

    public static List<WebElement> getElements(By locator, WebElement parent) {
        List<WebElement> elements = new ArrayList<>();
        boolean flag  = true;
        int attempt = 0;
        while (flag && attempt < 20){
            attempt++;
            sleep(1);
            try {
                elements =  parent.findElements(locator);
                flag = false;
            }
            catch (StaleElementReferenceException e){
                warn("StaleElementReferenceException for "+locator);
                elements = Collections.emptyList();
            }
            catch (NoSuchElementException e){
                warn("NoSuchElementException");
            }
            catch (Exception e){
                assertTrue(false, "Unknown exception.");
            }
        }
        return elements;
    }

    public static List<WebElement> getElements(By locator, WebDriver driver){
        List<WebElement> elements = new ArrayList<>();
        boolean flag  = true;
        int attempt = 0;
        while (flag && attempt < 20){
            attempt++;
            sleep(1);
            try {
                elements =  driver.findElements(locator);
                flag = false;
            }
            catch (StaleElementReferenceException e){
                warn("StaleElementReferenceException for "+locator);
                elements = Collections.emptyList();
            }
            catch (NoSuchElementException e){
                warn("NoSuchElementException");
            }
            catch (Exception e){
                assertTrue(false, "Unknown exception.");
            }
        }
        return elements;
    }

    public static String getElementPlaceholder(WebElement element, String elementName){
        return getElementAttribute(element, elementName,"placeholder");
    }

    public static boolean attributeIsPresent(WebElement element, String attribute){
        boolean flag;
        try {
            flag = element.getAttribute(attribute) != null && !element.getAttribute(attribute).isEmpty();
        }
        catch (NoSuchElementException e){
            flag = false;
            assertTrue(flag,"Element not found.");
        }
        return flag;
    }

    public static boolean placeholderIsPresent(WebElement element){
        return attributeIsPresent(element, "placeholder");
    }

    public static String getElementAttribute(WebElement element, String elementName, String attribute){
        String attributeValue = "";
        try {
            //Get value.
            if (element.getAttribute(attribute) != null){
                attributeValue = element.getAttribute(attribute);
            }
            else {
                assertTrue(false,"Attribute \""+attribute+"\" not present.");
            }
            info(attribute + " \"" + elementName +"\" = \"" + attributeValue + "\".");
        }
        catch (NoSuchElementException e){
            assertTrue(false, "\"" + elementName + "\" was not found on page after timeout.");
        }
        catch (ElementNotVisibleException e){
            assertTrue(false, "\"" + elementName + "\" was not visible.");
        }
        return attributeValue;
    }

    public static String getElementHref(WebElement element, String elementName){
        return getElementAttribute(element, elementName,"href");
    }

    public static int countElements(WebElement element, WebDriver driver){
        List<WebElement> list = driver.findElements(webElementToBy(element));
        info("Count elements - " + list.size());
        return  list.size();
    }

    public static int countElements(By loc, WebDriver driver){
        List<WebElement> list = driver.findElements(loc);
        info("Count elements - " + list.size());
        return  list.size();
    }

    public static void selectCheckBox(WebElement element, String elementName, WebElement checkElement, WebDriver driver){
        if(!checkElementSelected(checkElement, elementName)){
            WebElementService.clickOnElement(element,elementName, driver);
            info("Element " + elementName + " is selected.");
        }
        else {
            info("Element " + elementName + " is already selected.");
        }
    }

    public static void deSelectCheckBox(WebElement element, String elementName, WebElement checkElement, WebDriver driver){
        if(checkElementSelected(checkElement, elementName)){
            clickOnElement(element,elementName, driver);
            info("Element " + elementName + " is deselected.");
        }
        else {
            info("Element " + elementName + " is already deselected.");
        }
    }
    public static void selectDropBoxByValue(WebElement element, String value){
        try{
            Select select = new Select(element);
            select.selectByValue(value);
            info("Select \""+value+"\".");
        }
        catch (NoSuchElementException e){
            assertTrue(false, "\""+value+"\" is missing!");
        }
    }

    public static void sendKeysManualClear(WebElement element, String elementName, String inputText, WebDriver driver) {
            clearFieldManual(element, driver);
            sendKeysClear(element, elementName, inputText, driver);
    }

    public static void sendKeysNative(WebElement element, String elementName, String inputText){
        try {

            Stream.of(inputText.toCharArray())
                    .forEach(c -> element.sendKeys(String.valueOf(c)));

            info("\"" + elementName + "\" input text: \"" + inputText + "\".");
        }
        catch (NoSuchElementException e){
            assertTrue(false, "\"" + elementName + "\" was not found on page after timeout.");
        }
        catch (ElementNotVisibleException e){
            assertTrue(false, "\"" + elementName + "\" was not visible.");
        }
    }

    /**
     * Makes List of WebElements by By locator taken from given element and return concrete element by index in this List
     * @param element by which will be formed the List
     * @param indexInList which element would be uses
     * @param driver WebDriver
     * @return element by list index
     */
    public static WebElement getElementByIndex(WebElement element,int indexInList,WebDriver driver){
        By locator = webElementToBy(element);
        List<WebElement> list = getElements(locator,driver);
        if (list.isEmpty()){
            assertTrue(false, "List for " + "\""+ locator +"\"" + " is empty");
        }
        return list.get(indexInList);
    }

	/**
     * Method cleared the field (BackSpace)
     *
     * @param element input on page
     */
    public static void clearFieldManual(WebElement element, WebDriver driver) {

        int countChar = getElementValue(element, element.toString()).length();
        int i = 0;
        while (i<countChar){
            i++;
            clickOnElement(element,"element", driver);
            pressBackSpace(element);
        }
    }

    public static boolean elementIsDisableClass(WebElement element){
        return element.getAttribute("class").contains("disabled");
    }


    /**
     * @implNote Check if field is valid
     * @implSpec  Working with Reactjs component.
     */
    public static boolean isSuccessIconDisplay(@NonNull WebElement element) {
        WebElement icon = element.findElement(
                By.xpath("parent::*//*[contains(@class, 'icon-check_circle')]")
        );
        return elementIsDisplayed(icon, "Success icon for " + element);
    }


    /**
     * @implSpec  Working with Reactjs dropdown component.
     * @return Return all values from dropdown
     */
    public static List<WebElement> getValuesFromDropdown(@NonNull WebElement dropdown, final String value, @NonNull WebDriver driver) {
        clickOnElement(dropdown, dropdown.toString(), driver);
        WebElement dropdownInput = getElement(By.xpath(".//*[contains(@type, 'search')]"), dropdown);
        waitForElementVisible(dropdownInput, driver);
        clearFieldManual(dropdownInput, driver);
        sendKeysNative(dropdownInput, "dropdownInput", value);
        By dropdownOption = By.tagName("li");
        waitForList(dropdownOption, driver);
        return dropdown.findElements(dropdownOption);
    }



    public static WebElement getParentBlock(@NonNull WebElement element) {
        return Optional.ofNullable(getElement(By.xpath("parent::*"), element))
                .orElseThrow(() -> new CustomException("Parent block not found"));
    }



    /**
     * This method copied information to clipboard and paste in field through by keyboard command.
     * @param inputText - text which will be copied.
     * @param element - WebElement in which will be pasted copied text.
     */
    public static void submitFieldFromBuffer(String inputText, WebElement element){
        StringSelection selection = new StringSelection(inputText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
        paste(element);

    }

    /**
     * @implSpec  Working with Reactjs component.
     * @return Focus state
     */
    public static boolean isFocusOnComponent(@NonNull WebElement element) {
        return getElementAttribute(getParentBlock(element), "Component Name", "class").contains("text-field_focused");
    }

    /**
     * @implSpec  Working with Reactjs component.
     * @return Focused component in form
     */
    public static WebElement getFocusedField(@NonNull WebElement form) {
        return  getElement(By.xpath(".//*[contains(@class, 'text-field_focused')]"),  form);
    }

    /**
     * @implSpec  Working with Reactjs component.
     * @return Return notification block
     */
    public static WebElement getBlockNotification(@NonNull WebElement block) {
        return  getElement(By.xpath(".//*[contains(@class, 'tm-quark-notification__content')]"),  block);
    }


    /**
     * @implSpec  Working with Reactjs component.
     */
    public static void clickOnCheckBox(@NonNull WebElement element, @NonNull WebDriver driver) {
        clickOnElement(getParentBlock(element), "check box", driver);
    }

    /**
     * @implSpec  Working with Reactjs component.
     * @return RadioGroup selected value
     */
    public static String getRadioGroupValue(@NonNull WebElement element) {
        return getElements(By.xpath(".//*[@type = 'radio']"), element)
                .stream()
                .filter(item -> checkElementSelected(item, "RadioGroup item"))
                .findFirst()
                .map(radio -> getElementValue(radio, "radio"))
                .orElse("");
    }



    /**
     * @implSpec  Working with Reactjs component.
     */
    public static void deleteValueInDropDownListByIndex(@NonNull WebElement block, final int index, @NonNull WebDriver driver) {
        clickOnElement(
                getElements(By.xpath(".//button[contains(@class, 'tm-quark-tag')]"), block).get(index),
                "ValueInDropDownList",
                driver
        );
    }

    /**
     * @implSpec  Working with Reactjs component.
     */
    public static void deleteAllValuesInDropDownList(@NonNull WebElement block, @NonNull WebDriver driver) {
        getElements(By.xpath(".//button[contains(@class, 'tm-quark-tag')]"), block)
                .forEach(el -> clickOnElement(el, "DropDownListElement", driver));
    }

    /**
     * @implSpec  Working with Reactjs component.
     */
    public static boolean isElementSelect(WebElement element, String elementName) {
        return getElementValue(element, elementName).isEmpty();
    }


    /**
     * @implSpec  Working with Reactjs component.
     */
    @Deprecated
    public static void removeImageInUplouder(List<WebElement> imageBlocks, int index, WebDriver driver) {
        Optional.ofNullable(imageBlocks)
                .filter(el -> imageBlocks.size() >= index + 1)
                .map(el -> el.get(index))
                .ifPresent(el -> clickOnElement(getElement(By.xpath(".//button[contains(@id, '-media--" + index + "remove-btn')]"), el), "removeImageItem", driver));
    }

    /**
     * @implSpec  Working with Reactjs component.
     */
    @Deprecated
    public static void addYoutubeLinkInUplouder(List<WebElement> imageBlocks, String value, int index, WebDriver driver) {
        Optional.ofNullable(imageBlocks)
                .filter(el -> imageBlocks.size() >= index + 1)
                .map(el -> el.get(index))
                .ifPresent(el -> {
                    clickOnElement(getElement(By.xpath(".//*[contains(@class, 'uploader-product-media-file-area__add-video-btn')]"), el), "youtubeBtn", driver);
                    WebElement link = getElement(By.xpath(".//*[contains(@id, '-image-video-area-" + index + "-input')]"), el);
                    waitForElementVisible(link, driver);
                    sendKeysManualClear(link, "youtubeInput", value, driver);
                    clickOnElement(getElement(By.xpath(".//*[contains(@id, '-image-video-area-" + index + "-submit')]"), el), "youtubeSubmitBtn", driver);
                });
    }


}

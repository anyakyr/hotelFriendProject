package utility.services;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import utility.Constants;
import utility.Log;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static utility.Log.info;
import static utility.Log.warn;
import static utility.services.ReportService.assertTrue;
import static utility.services.WaiterService.*;
import static utility.services.WebElementService.moveToCoordinate;

public class ManageUrlService {

    /*
        Constants.
     */
    private static final String TAG_META_DESCRIPTION ="meta[name=description]";
    private static final String TAG_META_ROBOTS ="meta[name=robots]";
    private static int attempt;
    private static final String CLEAR_PURGE_CACHE ="";


    public static void getURL(String url, String locale, WebDriver driver) {

        driver.getCurrentUrl();

        if(locale.equals("US")){
            locale = "";
        }
        info("Navigate to \""+ Constants.URL+locale.toLowerCase()+url+"\".");
        try {
            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
            driver.get(Constants.URL+locale.toLowerCase()+ "/"+url);
            waitForJSandJQueryToLoad(driver);
            info("Navigate to \"" + Constants.URL+locale.toLowerCase()+url + "\" finished.");

        }
        catch (TimeoutException e){
            stopLoad(driver);
        }
        catch (UnhandledAlertException e){
            warn("Catch UnhandledAlertException.");
            skipModalWindow(driver);
        }


    }

    public static void getDirectlyURL(String url, WebDriver driver) {
        driver.getCurrentUrl();
        info("Navigate to \""+url+"\".");
        try {
            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
            driver.get(url);
            waitForJSandJQueryToLoad(driver);
            info("Navigate to \""+url+"\" finished.");
            moveToCoordinate(0,0,driver);
        }
        catch (TimeoutException e){
            stopLoad(driver);
        }
        catch (UnhandledAlertException e){
            warn("Catch UnhandledAlertException.");
            skipModalWindow(driver);
        }

    }

    public static boolean validateStringInUrl(String url, WebDriver driver){

        if ((driver.getCurrentUrl().contains(url))){
            info("This is an expected page.");
            return true;
        }
        else
        {
            return false;
        }
    }


    public static boolean verifyTextPresentOnPage(String someText, WebDriver driver){
        if(driver.getPageSource().contains(someText)){
            info("\"" + someText + "\" is on the Page.");
            return true;
        }
        else {
            info("\"" + someText+ "\" is NOT on the Page.");
            return false;
        }
    }

    public  static void refreshPage(WebDriver driver){
        try {
            driver.navigate().refresh();
            info("Page was refreshed.");
        }
        catch (WebDriverException e){
            stopLoad(driver);
        }
    }

    public  static String getCurrentURL(WebDriver driver){
        info("Current URL:"+driver.getCurrentUrl());
        return driver.getCurrentUrl();
    }

    public static void switchToFrame(String frameId, WebDriver driver){
        int attempt=0;
        boolean flag = true;
        while (flag && attempt<10){
            attempt++;
            try {
                driver.switchTo().frame(frameId);
                info("Switched to \""+frameId+"\" frame.");
                flag = false;
            }
            catch (NoSuchFrameException e){
                warn("NoSuchFrameException "+frameId);
            }
            catch (UnhandledAlertException e){
                skipModalWindow(driver);
            }
        }
    }

    public static void switchToFrame(int frameIndex, WebDriver driver){
        List<WebElement> frames = driver.findElements(By.tagName("frame"));
        int attemptCounter = 0;
        while (frames.size()<=frameIndex){
            frames = driver.findElements(By.tagName("frame"));
            sleep(1);
            attemptCounter++;
            if (attemptCounter == 10){
                assertTrue(false,"Invalid index of frame");
            }
        }

        driver.switchTo().frame(frames.get(frameIndex));
        info("Switch to "+frameIndex+"-index frame.");

    }

    public static void switchToIframe(int frameIndex, WebDriver driver){
        List<WebElement> frames = driver.findElements(By.tagName("iframe"));
        int attemptCounter = 0;
        while (frames.size()<=frameIndex){
            frames = driver.findElements(By.tagName("iframe"));
            sleep(1);
            attemptCounter++;
            if (attemptCounter == 10){
                assertTrue(false,"Invalid index of iframe");
            }
        }

        driver.switchTo().frame(frames.get(frameIndex));
        info("Switch to "+frameIndex+"-index iframe.");

    }

    public static boolean verifyTextPresent(String text, WebDriver driver){
        try {
            driver.findElement(By.xpath(".//*[contains(text(),'"+text+"')]"));
            info("\""+text+"\" present on page.");
            return true;
        }
        catch (NoSuchElementException e){
            info("\""+text+"\" does NOT present on page.");
            return false;
        }
    }

    public static String getWindow(WebDriver driver){
        return driver.getWindowHandle();
    }

    public static void switchToWindow(String windowName, WebDriver driver){
        info("Switch to \""+windowName+"\" window.");
        driver.switchTo().window(windowName);
    }

    public static void switchToWindow(WebDriver driver){
        for (String win:driver.getWindowHandles()){
            driver.switchTo().window(win);
        }
        info("Switch to another window.");
        driver.manage().window().maximize();
        info("Maximize window.");
    }

    public static void switchToLastWindow(WebDriver driver){
        try {
            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    return (driver.getWindowHandles().size() > 1) ;
                }
            });
            for (String win:driver.getWindowHandles()){
                driver.switchTo().window(win);
            }
            info("Switch to another window.");
        }
        catch (TimeoutException e){
            assertTrue(false, "You have only one window.");
        }
        driver.manage().window().maximize();
        info("Maximize window.");
    }

    public static void switchToLastWindowClose(WebDriver driver){
        try {
            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    return (driver.getWindowHandles().size() > 1) ;
                }
            });
            String last = "";
            Iterator<String> iterator = driver.getWindowHandles().iterator();
            while (iterator.hasNext()){
               last=iterator.next();
            }
            for (String win:driver.getWindowHandles()){
                if (win.equals(last)) {
                    driver.switchTo().window(win);
                }else driver.close();
            }
            info("Switch to another window.");
        }
        catch (TimeoutException e){
            assertTrue(false, "You have only one window.");
        }
        driver.manage().window().maximize();
        info("Maximize window.");
    }


    public static int countWindows(WebDriver driver){
        int counter = 0;
        for (String win :driver.getWindowHandles()){
            counter++;
        }
        info("Opened windows = "+counter);
        return counter;
    }

    public static void closeWindow(WebDriver driver){
        driver.close();
        info("Close current window.");
    }

    public  static void openNewWindow(WebDriver driver){
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("window.open('');");
        info("Open new window.");
    }


    public static void navigateBack (WebDriver driver){
        try {
            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
            driver.navigate().back();
            info("Returned to the previous page.");
        }
        catch (TimeoutException e){
            stopLoad(driver);
        }

    }

    public static void skipModalWindow(WebDriver driver){
        try {
            Alert alt=driver.switchTo().alert();
            alt.accept();
            info("Skip modal window.");
        }
        catch (NoAlertPresentException e){
            Log.warn("Catch NoAlertPresentException.");
        }
    }

    public static String getModalWindowText(WebDriver driver){
        Alert alert = driver.switchTo().alert();
        info("Modal window text is \""+alert.getText()+"\".");
        return alert.getText();
    }

    public static String getTitle(WebDriver driver){
        info("Page title: \""+driver.getTitle()+"\".");
        return driver.getTitle();
    }


    public static void scrollDown(WebDriver driver){
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("window.scrollBy(0,document.body.scrollHeight)", "");
    }

    public static void switchToContent(WebDriver driver){
        driver.switchTo().defaultContent();
        info("Switch to default content.");
    }

    public static void stopLoad(WebDriver driver){
        driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        info("Timeout on loading page \""+driver.getCurrentUrl()+"\".");
    }

    public  static String getSubHeading(WebDriver driver){
        String heading = driver.findElement(By.xpath(".//h1/following-sibling::p[1]")).getText();
        info("Sub-heading text: " + heading);
        return heading;
    }

    public static String getBrowserName(WebDriver driver){
        String browser  = (String)((JavascriptExecutor)driver).executeScript("return navigator.userAgent","");
        if (browser.contains("Firefox")){
            info("Running browser - ff");
            return "ff";
        }
        else if (browser.contains("Android")){
            info("Running browser - android");
            return "android";
        }
        else if (browser.contains("iPhone")){
            info("Running browser - iphone");
            return "iphone";
        }
        else if (browser.contains("Chrome")){
            info("Running browser - chrome");
            return "chrome";
        }
        else {
            info("Running browser - "+browser);
            return browser;
        }
    }


    public static String authUrl(String url, String name, String pass){
        String auth = "https://"+name+":"+pass+"@";
        url = url.substring(8);
        url = auth.concat(url);
        return url;
    }

    public static boolean verifyAllCustomUlr(List<String> list, String urlPart){
        List<String> propLinks = new ArrayList<>();
        if(list.size() == 0){
            info("Links not found.");
            return false;
        }
        info("Checking " + list.size() + " links.");
        for (String link:list){
            if (!link.contains(urlPart)){
                //info(link + " not contains \"" + urlPart + "\".");
                propLinks.add(link);
            }
        }

        return propLinks.size()==0;
    }

    public static boolean verifyAnyCustomUlr(List<String> list, String urlPart){
        List<String> propLinks = new ArrayList<>();
        if(list.size() == 0){
            info("Links not found.");
            return false;
        }
        info("Checking " + list.size() + " links.");
        for (String link:list){
            if (link.contains(urlPart)){
                info(link + " contains \"" + urlPart + "\".");
                propLinks.add(link);
            }
        }
        return propLinks.size()!=0;
    }

    public static String makeSecureUrl(){
        return "https://secure." + Constants.URL.substring(7).replaceAll("www.","");
    }

    public static boolean verifyAbsoluteLinks(List<String> list) {
        return verifyAllCustomUlr(list, "/");
    }


    public static String convertUrl(String url){
        String hash = "";
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");

            // JavaScript code in a String
            String script = "function check_tweet(str){    \n" +
                    "        var hash = 0;\n" +
                    "        if (str.length == 0) return hash;\n" +
                    "        for (var i = 0; i < str.length; i++) {\n" +
                    "            var charCode = str.charCodeAt(i);\n" +
                    "            hash = ((hash << 5) - hash) + charCode;\n" +
                    "            hash = hash & hash;\n" +
                    "        }\n" +
                    "        hash = hash.toString(16);\n" +
                    "        hash = hash.replace(\"-\", \"0\");\n" +
                    "        return hash;\n" +
                    "}";
            engine.eval(script);
            Invocable inv = (Invocable) engine;
            hash = inv.invokeFunction("check_tweet", url).toString();

        }
        catch (ScriptException e){
            assertTrue(false, "Catch ScriptException");
        }
        catch (NoSuchMethodException e){
            assertTrue(false, "Catch NoSuchMethodException");
        }

        return hash;
    }


    public static String concatUrlParams(HashMap<String, String> params) {
        StringBuffer sb = new StringBuffer();

        String key;
        for(Iterator i$ = params.keySet().iterator(); i$.hasNext(); sb.append(key).append("=").append(params.get(key))) {
            key = (String)i$.next();
            if(sb.length() > 0) {
                sb.append("&");
            }
        }

        return sb.toString();
    }

    public static void scrollUp(WebDriver driver, int px){
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("window.scrollBy(0,-"+px+")", "");
    }


    public static void switchToIframe(WebElement frame, WebDriver driver){
        waitForElementVisible(frame,10,driver);
        driver.switchTo().frame(frame);
        info("Switch to "+frame+" iframe.");

    }

    public static void scrollDown(WebDriver driver, int px){
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("window.scrollBy(0,"+px+")", "");
    }

}

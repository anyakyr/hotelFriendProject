package utility;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import pageobjects.GmailLoginPage;
import utility.services.GUIServices;
import utility.services.ManageUrlService;
import utility.services.WaiterService;
import utility.services.WebElementService;

import java.io.File;
import java.util.concurrent.TimeUnit;


public class BaseTest {

    public WebDriver driver;
    public ManageUrlService manageUrlService;
    public WaiterService waiterService;
    public WebElementService webElementService;

    protected String testCaseName = this.getClass().getSimpleName();
    public String name;


    private GUIServices guiServices = new GUIServices();
    private String pathToScreenShot;

    public GmailLoginPage gmailLoginPage;




    @BeforeTest
    public void runBrowser(){

        Log.info("TestCase: \"" + testCaseName + "\" started");

        File file = new File("");

        //        if ("fireFox".equals(browser)) {
//            logger.info("FireFox will be started ");
//            File fileFF = new File("drivers/geckodriver.exe");
//            System.setProperty("webdriver.gecko.driver", fileFF.getAbsolutePath());
//            FirefoxProfile profile = new FirefoxProfile();
//            profile.setPreference("browser.startup.page", 0); // Empty start page
//            profile.setPreference("browser.startup.homepage_override.mstone", "ignore"); // Suppress the "What's new" page
//            webDriver = new FirefoxDriver(profile);
//            logger.info(" FireFox is started");
//        } else if ("chrome".equals(browser)) {
//            logger.info("Chrome will be started ");
//            File fileFF = new File("drivers/chromedriver.exe");
//            System.setProperty("webdriver.chrome.driver", fileFF.getAbsolutePath());
//            webDriver = new ChromeDriver();
//            logger.info(" Chrome is started");
//        } else if ("iedriver".equals(browser)) {
//            logger.info("IE will be started ");
//            File file1 = new File("drivers/IEDriverServer.exe");
//            System.setProperty("webdriver.ie.driver", file1.getAbsolutePath());
//            DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
//            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
//            capabilities.setCapability("ignoreZoomSetting", true);
//            capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
//            webDriver = new InternetExplorerDriver();
//            logger.info(" IE is started");
//        } else if ("opera".equals(browser)) {
//            logger.info("Opera will be started");
//            File fileOpera = new File("drivers/operadriver.exe");
//            System.setProperty("webdriver.chrome.driver", fileOpera.getAbsolutePath());
//            webDriver = new ChromeDriver();
//            logger.info(" Opera is started");
//        } else if ("phantomJs".equals(browser)) {
//            logger.info("PHANTOMJS will be started");
//            DesiredCapabilities caps = new DesiredCapabilities();
//            caps.setJavascriptEnabled(true);
//            caps.setCapability("takesScreenshot", true);
//            caps.setCapability(
//                    PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
//                    "drivers/phantomjs-2.1.1-windows/bin/phantomjs.exe"
//            );
//            webDriver = new PhantomJSDriver(caps);
//            logger.info(" PHANTOMJS is started");
//        }


        pathToScreenShot = file.getAbsolutePath() + "\\target\\screenshots\\"
                + this.getClass().getPackage().getName() + "\\"
                + this.getClass().getSimpleName() + "\\"
                + this.testCaseName + ".jpg";


        File fileFF = new File("drivers/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", fileFF.getAbsolutePath());
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);

        getServiceConstructors();
    }




    @AfterTest
    public void closeBrowser() {

        guiServices.screenShot(pathToScreenShot,driver);
        driver.quit();
        Log.info("TestCase: \"" + testCaseName + "\" finished \n");
    }


    private void getServiceConstructors(){
        manageUrlService = new ManageUrlService();
        waiterService = new WaiterService();
        webElementService = new WebElementService();
        gmailLoginPage = new GmailLoginPage(driver);

    }
}

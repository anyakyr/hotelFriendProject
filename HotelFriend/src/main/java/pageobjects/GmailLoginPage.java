package pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static utility.services.WebElementService.clickOnElement;
import static utility.services.WebElementService.sendKeysClear;

/**
 * Created by Anya on 05.08.2018.
 */
public class GmailLoginPage {


    @FindBy(xpath = "//*[@data-g-label='Sign in']")
    public WebElement signInButton;

    @FindBy(xpath = "//input[@class='whsOnd zHQkBf']")
    public WebElement emailInput;

    @FindBy(id = "identifierNext")
    public WebElement nextButton;

    @FindBy(xpath = "//input[@name='password']")
    public WebElement passwordInput;

    @FindBy(id ="passwordNext")
    public WebElement passwordNextButton;

    @FindBy(xpath="//div[@class='LXRPh']//following-sibling::div")
    public WebElement emailError;

    @FindBy(xpath="//div[@class='LXRPh']//following-sibling::div[contains(text(), 'Не удалось найти аккаунт Google')]")
    public WebElement emailInvalidError;

    @FindBy(xpath="//div[@class='LXRPh']//following-sibling::div[contains(text(), 'Введите пароль')]")
    public WebElement passwordError;


    @FindBy(xpath="//div[@class='LXRPh']//following-sibling::div[contains(text(), 'Неверный пароль.')]")
    public WebElement passwordInvalidError;


    @FindBy(xpath="//div[@class='aj9 pp']")
    public WebElement leftMenu;

    @FindBy(xpath="//a[@href='https://mail.google.com/mail/']")
    public WebElement gmailButton;

    @FindBy(xpath="//div[@id='gbwa']/div/a")
    public WebElement googleAppsButton;

    protected WebDriver driver;

    public GmailLoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void enterEmail(String emailText){
        sendKeysClear(emailInput, "Email", emailText, driver);
    }


    public void enterPassword(String passwordText){
        sendKeysClear(passwordInput, "Password", passwordText, driver);
    }

    public void clickOnSignInButton() {
        clickOnElement(signInButton, "Sign In Button", driver);
    }

    public void clickOnNextButton() {
        clickOnElement(nextButton, "Next Button", driver);
    }

    public void clickOnPasswordNextButton() {
        clickOnElement(passwordNextButton, "Password Next Button", driver);
    }

    public void clickOnGoogleAppsButton() {
        clickOnElement(googleAppsButton, "Google Apps Button", driver);
    }

    public void clickOnGmailButton() {
        clickOnElement(gmailButton, "Gmail Button", driver);
    }

}

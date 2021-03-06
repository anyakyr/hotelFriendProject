package loginTests;

import businessobjects.User;
import org.testng.annotations.Test;
import utility.BaseTest;
import utility.Constants;
import utility.services.FileReaderService;

import java.util.Map;

import static utility.services.ManageUrlService.getDirectlyURL;
import static utility.services.ReportService.assertEquals;
import static utility.services.ReportService.assertTrue;
import static utility.services.WebElementService.elementIsDisplayed;
import static utility.services.WebElementService.getElementText;

/**
 * Created by Anya on 05.08.2018.
 */
public class Login_004_InvalidPasswordTest extends BaseTest{
    @Test
    public void test_004() {

        //Go to landing page.
        getDirectlyURL(Constants.URL,driver);

        //Verify that page has all necessary components.
        assertTrue(elementIsDisplayed(gmailLoginPage.emailInput,"Email TextField"),
                "Email TextField is NOT displayed");
        assertTrue(elementIsDisplayed(gmailLoginPage.nextButton,"Next Button"),
                "Next Button is NOT displayed");

        User user = new User("properties/user/login.properties");

        gmailLoginPage.enterEmail(user.getEmail());
        gmailLoginPage.clickOnNextButton();

        gmailLoginPage.enterPassword(user.getEmail());
        gmailLoginPage.clickOnPasswordNextButton();

        //Verify error messages.
        Map<String, String> error = FileReaderService.getMap("login/loginErrors.txt");
        assertEquals(getElementText(gmailLoginPage.passwordInvalidError, "Password Incorrect Error"),
                error.get("errorInvalidPassword"), "Password Incorrect Error message");
    }
}

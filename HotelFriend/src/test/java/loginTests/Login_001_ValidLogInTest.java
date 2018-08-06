package loginTests;

import businessobjects.User;
import org.testng.annotations.Test;
import utility.BaseTest;
import utility.Constants;

import static utility.services.ManageUrlService.getDirectlyURL;
import static utility.services.ReportService.assertTrue;
import static utility.services.WebElementService.elementIsDisplayed;

/**
 * Created by Anya on 05.08.2018.
 */

public class Login_001_ValidLogInTest extends BaseTest{

    @Test
    public void test_001() {

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
        gmailLoginPage.enterPassword(user.getPassword());
        gmailLoginPage.clickOnPasswordNextButton();

        gmailLoginPage.clickOnGoogleAppsButton();
        gmailLoginPage.clickOnGmailButton();

        assertTrue(elementIsDisplayed(gmailLoginPage.leftMenu,"Left Menu"),
                "Left Menu is NOT displayed");

    }
}


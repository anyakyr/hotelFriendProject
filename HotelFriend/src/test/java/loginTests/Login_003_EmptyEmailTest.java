package loginTests;

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
public class Login_003_EmptyEmailTest extends BaseTest {
    @Test
    public void test_003() {

        //Go to landing page.
        getDirectlyURL(Constants.URL,driver);

        //Verify that page has all necessary components.
        assertTrue(elementIsDisplayed(gmailLoginPage.emailInput,"Email TextField"),
                "Email TextField is NOT displayed");
        assertTrue(elementIsDisplayed(gmailLoginPage.nextButton,"Next Button"),
                "Next Button is NOT displayed");

        gmailLoginPage.clickOnNextButton();

        //Verify error messages.
        Map<String, String> error = FileReaderService.getMap("login/loginErrors.txt");
        assertEquals(getElementText(gmailLoginPage.emailError, "Empty Email Error"),
                error.get("errorEmptyEmail"), "Email Incorrect Error message");

    }
}

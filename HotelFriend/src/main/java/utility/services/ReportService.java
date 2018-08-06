package utility.services;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import utility.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ReportService {

    public static final String SCREENSHOTS_DIR = "target/screenshots/";

    static {
        Path path = Paths.get(SCREENSHOTS_DIR);
        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void takeScreenshot(String testCaseName, String message, WebDriver driver) {
		
		try {
            String screenshotName = testCaseName + "ScreenShot.png";
            final BufferedImage image;
            File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            image=ImageIO.read(scrFile);
            Graphics g = image.getGraphics();
            g.setFont(new Font("Arial Black", Font.PLAIN, 20));
            g.setColor(Color.DARK_GRAY);
            g.drawString("URL: " + message, 50, 100);
            g.dispose();

            ImageIO.write(image, "png", new File(SCREENSHOTS_DIR + screenshotName));

            Log.info("");
            Log.warn("Screenshot captured.");
            Log.warn("Screenshot name: \"" + screenshotName + "\".");
        }
        catch (WebDriverException | IOException e) {
            Log.error("Catch " + e);
        }

    }
	
	public static void assertTrue(Boolean condition, String errorMessage ) {
		
		if (!condition){
			Log.info("");
			Log.error("ACTUAL RESULT:");
			Log.error(errorMessage);
		}

		Assert.assertTrue(condition);
		
	}

    public static void assertFalse(Boolean condition, String errorMessage ) {

        if (condition){
            Log.info("");
            Log.error("ACTUAL RESULT:");
            Log.error(errorMessage);
        }

        Assert.assertFalse(condition);

    }

	public static <T> void assertEquals(T condition1, T condition2, String errorMessage) {

        String error = "ACTUAL RESULT:\n"+errorMessage+"\nExpected: \"" + condition2 + "\", but found: \"" + condition1 + "\".";

        if (condition1 instanceof String){
            if (!((String) condition1).equalsIgnoreCase((String)condition2)) {
                Log.error(error);
            }
        }
        else {
            if (!(condition1.equals(condition2))) {
                Log.error(error);
            }
        }
		
		Assert.assertEquals(condition1, condition2);
		
	}

    public static void assertEquals(double expected, double actual, double delta, String errorMessage) {

        String error = "ACTUAL RESULT:\n" + errorMessage + "\nExpected: \"" + expected + "\", but found: \"" + actual + "\".";

        if (Double.isInfinite(expected)) {
            if (expected != actual) {
                Log.error(error);
            }
        } else if (Math.abs(expected - actual) > delta) {
            Log.error(error);
        }

        Assert.assertEquals(expected, actual, delta);
    }


    public static void catchException(Exception e){
        Assert.assertTrue(false, String.valueOf(e));
    }

}

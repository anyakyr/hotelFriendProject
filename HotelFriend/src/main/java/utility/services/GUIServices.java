package utility.services;

import com.googlecode.fightinglayoutbugs.FightingLayoutBugs;
import com.googlecode.fightinglayoutbugs.LayoutBug;
import com.googlecode.fightinglayoutbugs.WebPage;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.im4java.core.CompareCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.StandardStream;
import org.openqa.selenium.*;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.screentaker.ViewportPastingStrategy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import static utility.Log.info;

@Log4j
public class GUIServices {

    final static By BANNER = By.xpath("//header[@id='header']//*[@class='js-close-banner']");

    public static boolean verifyImageLoaded(WebElement element, WebDriver driver){
        Boolean result = (Boolean) ((JavascriptExecutor)driver).
                executeScript("return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", element);
        return result;
    }

    public static void storeImageByLink(WebElement element, String name){
        try {
            String link = element.getAttribute("src");
            URL url = new URL(link);
            BufferedImage img = ImageIO.read(url);
            ImageIO.write(img, "PNG", new File("target/screenshots/" + name + ".png"));
        }
        catch (IOException e){
            ReportService.assertTrue(false,"Catch "+e);
        }

    }


    public static boolean compareImages(File expected, File current, String diff ){
        CompareCmd cmd = new CompareCmd();

        cmd.setErrorConsumer(StandardStream.STDERR);
        IMOperation operation = new IMOperation();
        operation.metric("pae");

        String expectedPath = expected.getAbsolutePath();
        String currentPath = current.getAbsolutePath();

        operation.addImage(expectedPath);
        operation.addImage(currentPath);
        operation.addImage(diff);
        try {
            cmd.run(operation);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }


    public static void takeScreenShot(String name, WebDriver driver){
        try {
            final Screenshot screenshot = new AShot()
                    .shootingStrategy(new ViewportPastingStrategy(0))
                    .takeScreenshot(driver);
            final BufferedImage image = screenshot.getImage();

            ImageIO.write(image, "PNG", new File("target/screenshots/" + name + ".png"));
        }
        catch (IOException e){
            ReportService.assertTrue(false,"Catch "+e);
        }
    }

    public static void takeScreenShot(WebElement element, String name, WebDriver driver){
        try {
            final Screenshot screenshot = new AShot()
                    .shootingStrategy(new ViewportPastingStrategy(0))
                    .takeScreenshot(driver, element);
            final BufferedImage image = screenshot.getImage();
            ImageIO.write(image, "PNG", new File("target/screenshots/" + name + ".png"));

        }
        catch (IOException e){
            ReportService.assertTrue(false,"Catch "+e);
        }
    }

    public static Collection<LayoutBug> getLayoutBugs(WebDriver driver){
        WebPage page = new WebPage(driver);
        FightingLayoutBugs fightingLayoutBugs = new FightingLayoutBugs();
        return fightingLayoutBugs.findLayoutBugsIn(page);
    }

    public static File getScreenShot(WebElement element,String name, WebDriver driver){

        for (int i = 0; i <5 ; i++) {
            try {
                File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
                final BufferedImage image = ImageIO.read(scrFile);
                Point point = element.getLocation();
                int width = element.getSize().getWidth();
                int hight = element.getSize().getHeight();
                BufferedImage elementScreen = image.getSubimage(point.getX(),point.getY(), width, hight);
                ImageIO.write(elementScreen, "png", new File("target/screenshots/" + name + ".png"));
                return scrFile;
            }
            catch (IOException e){
                ReportService.assertTrue(false, "Catch "+e);
                return null;
            }
            catch (RasterFormatException e){
                ReportService.assertTrue(false, "Catch "+e);
            }
        }
        return null;
    }

    /**
     * Taking screenshot into .//target// + pathToScreenShot
     *
     * @param pathToScreenShot
     * @param driver
     */
    public void screenShot(String pathToScreenShot, WebDriver driver) {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(pathToScreenShot));
            info("ScreenShot: " + pathToScreenShot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

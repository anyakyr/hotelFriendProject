package utility.services;

import org.openqa.selenium.WebElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utility.Constants;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Double.valueOf;
import static java.util.regex.Pattern.compile;
import static utility.Log.info;
import static utility.services.ReportService.catchException;
import static utility.services.WebElementService.getElementText;


public class
ObjectService {

    private static final DateFormat FORMAT = new SimpleDateFormat("MM.dd.yyyy");
    private static final String DISCOUNTS = "discounts";

    public static Double getPrice(String input) {
        return Optional.of(compile("\\d{1,3}[,\\.]?(\\d{1,2})?").matcher(input))
                .filter(Matcher::find)
                .map(el -> valueOf(el.group(0)))
                .orElseThrow(() -> new CustomException("Exception: price not found"));
    }


    public static String getName(WebElement element, String elementName){
        //Delete unnecessary chars.
        String name = getElementText(element, "element name")
                .replaceAll("\\d+","")
                .replace("$","")
                .replace("+","")
                .trim();
        info(elementName+" has name value - "+name);
        return name;

    }

    public static Integer getBonus(Integer price, Integer percent){
        Integer result = Math.round(price*((float)percent/100));
        info("Bonus Amount = "+result);
        return  result;
    }


    public static Integer parser(String text){
        try {
            return (text!=null && !text.isEmpty()) ? Integer.parseInt(text) : null;
        }
        catch (NumberFormatException e){
            throw new NumberFormatException("NumberFormatException");
        }
    }

    public static String trimer (String text){
        return text.replaceAll("\\s+", " ");
    }

    public static String saveReturn(String o, String errorMsg){
        if (o==null){
            ReportService.assertTrue(false, errorMsg);
        }
        return o;
    }


    /*
        Time diff (days).
    */
    public static int getTimeDiff(Date stop){
        Date currentDate = new Date();
        return (int) ((stop.getTime()-currentDate.getTime())/86400000);

    }

    public  static boolean verifyInterval(int value, int from, int to){
        return value>=from && value<=to;
    }

	/**
     * The method returns list urls with site map archive
     *
     * @return strings list with site map archive
     */
    public static List<String> getLocsSitemap(){

        List<String> list = new ArrayList();

        Document doc = getDocument(Constants.URL+"sitemap.xml");
        NodeList nList = doc.getElementsByTagName("sitemap");
        IntStream.range(0, nList.getLength())
            .mapToObj(nList::item)
            .map((node)->(Element) node)
            .forEach(element -> list.add(element.getElementsByTagName("loc").item(0).getTextContent()));
        return  list;
    }


    /**
     * The method returns list urls with site map archive of one sitemap
     *
     * @return strings list with site map archive
     */
    public static List<String> getLocsSitemap(String url){

        List<String> list = new ArrayList();

        Document doc = getDocument(url);
        NodeList nList = doc.getElementsByTagName("sitemap");
        IntStream.range(0, nList.getLength())
                .mapToObj(nList::item)
                .map((node)->(Element) node)
                .forEach(element -> list.add(element.getElementsByTagName("loc").item(0).getTextContent()));
        return  list;
    }


    /**
     * Method return percent of the number
     * @param percent percent
     * @param number number from which you need to get a percent
     * @return result
     */
    public static int getPercentOfTheNumber(int percent, int number){
        int result = number*percent/100;
        info("\"" + percent + "% from " + number + " = " + result + "\".");
        return result;
    }

    /**
     * @param xmlFilePart part xml or yml file (example	yml/ru/yandex_feed.yml)
     * @return document
     */
    public static Document getDocument(String xmlFilePart){

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setValidating(false);
        Document doc = null;

        try {
            DocumentBuilder builder = f.newDocumentBuilder();
            doc = builder.parse(String.valueOf(new URL(xmlFilePart)));
            doc.getDocumentElement().normalize();
        }
        catch (SAXException | IOException | ParserConfigurationException e) {
            catchException(e);
        }
        return doc;
    }

    /**
     * The method returns list with template id from xml document
     *
     * @return list with templates id
     */
    public static List<String> getListIdsFromFeed(Document document){

        List<String> list = new ArrayList();

        NodeList nList = document.getElementsByTagName("offer");
        IntStream.range(0, nList.getLength())
                .mapToObj(nList::item)
                .map((node)->(Element) node)
                .forEach(element -> list.add(element.getAttributes().getNamedItem("id").getTextContent()));

        return  list;
    }

    /**
     * The method returns list with lastmod date
     *
     * @return strings list with lastmod date
     */
    public static List<LocalDateTime> getLastModSitemap(){

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        Document doc = getDocument(Constants.URL+"sitemap.xml");
        NodeList nList = doc.getElementsByTagName("lastmod");
        return IntStream.range(0, nList.getLength())
                .mapToObj(nList::item)
                .map((node)->(Element) node)
                .map(element -> LocalDateTime.parse(element.getTextContent(), formatter))
                .collect(Collectors.toList());

    }

    /**
     * The method returns list urls with site map archive of one sitemap
     *
     * @return strings list with site map archive
     */
    public static List<String> getLocsSitemapWithoutGz(){

        List<String> list = getLocsSitemap();

        List<String> listWithoutGz = new LinkedList<>();

        list.forEach(element ->{
            listWithoutGz.add(element.replace(".gz",""));
        });
        return listWithoutGz;
    }

    /**
     * The method returns list urls with site map archive of one sitemap
     *
     * @return strings list with site map archive
     */
    public static List<String> getImageLocsSitemap(String url){

        List<String> list = new ArrayList();

        Document doc = getDocument(url);
        NodeList nList = doc.getElementsByTagName("sitemap");
        IntStream.range(0, nList.getLength())
                .mapToObj(nList::item)
                .map((node)->(Element) node)
                .forEach(element -> list.add(element.getElementsByTagName("image:loc").item(0).getTextContent()));
        return  list;
    }

}

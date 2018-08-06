package utility.services;


import au.com.bytecode.opencsv.CSVReader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jdk.nashorn.api.scripting.URLReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import utility.Log;
import utility.PropertyReader;

import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static utility.services.ReportService.assertTrue;
import static utility.services.ReportService.catchException;

public class FileReaderService {
    private static final String DATA_PROVIDER_FILE_DELIMITER = "-->";
    private static final String TEST_REPORTS_PATH = "target/reports/";
    private static final String FILE_PATH = TEST_REPORTS_PATH + "testng-failed.xml";
    private static String SUCCESS_FILE_PATH = TEST_REPORTS_PATH + "testng-results.xml";
    public static final String PROP_PATH = "src/test/resources/properties/";
    public static final String PROP_PATH_AUT = "properties/";

    public static List<String> listReader(String fileLocation){
        BufferedReader in;
        List<String> myList = new ArrayList<>();
        try {
            in = new BufferedReader(new FileReader(PROP_PATH+fileLocation));
            String str;
            while ((str = in.readLine()) != null) {
                myList.add(str);
            }
            in.close();
        }
        catch (IOException e){
            assertTrue(false, "Catch an exception " + e);
        }

        return myList;
    }


    public static Map<String, String> getMap(String fileLocation) {
        PropertyReader propertyReader = new PropertyReader(PROP_PATH_AUT + fileLocation);
        return new HashMap(propertyReader.getProperties());
    }

    public static String getValueWithReplace(String fileLocation,String key, String text){
        try {
            Map<String, String> map = new HashMap<>();
            BufferedReader in = new BufferedReader( new FileReader(PROP_PATH+fileLocation));
            String line;
            while ((line = in.readLine()) !=null){
                String parts[] = line.split("=");
                map.put(parts[0],parts[1]);
            }
            in.close();
            return map.get(key).replaceAll("PART", text);
        }
        catch (IOException e){
            assertTrue(false, "Catch an exception " + e);
            return null;
        }

    }

    public static String getLink(WebElement element){
        try {
            if (element.getTagName().equals("a")){
                Log.info("Link - "+ element.getAttribute("href"));
                return element.getAttribute("href");
            }
            else {
                ReportService.assertFalse(true, "link is not on a-tag.");
                return null;
            }
        }
        catch (NoSuchElementException e){
            ReportService.assertFalse(true, "Not such element "+element);
            return null;
        }
    }

    public static HttpResponse getResponse(String link){
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(),SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);


            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslsf)
                    .build();


            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(2000);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .setConnectionManager(cm)
                    .build();

            HttpResponse response = httpClient.execute(new HttpGet(link));
            return response;
        }
        catch (Exception e){
            ReportService.assertFalse(true, "Catch "+e);
            return null;
        }
    }

    public static int getStatusCode(HttpResponse response){
        int code = response.getStatusLine().getStatusCode();
        Log.info("Code = "+code);
        return code;
    }

    public static String getBody(HttpResponse response){
        try {
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        }
        catch (IOException e){
            throw new CustomException("Catch "+e);
        }
    }

    public static Map<String,String> getMap(String fileLocation, String delimiter){
        try {
            Map<String, String> map = new HashMap<>();
            BufferedReader in = new BufferedReader( new FileReader(PROP_PATH+fileLocation));
            String line;
            while ((line = in.readLine()) !=null){
                String parts[] = line.split(delimiter);
                map.put(parts[0],parts[1]);
            }
            in.close();
            return map;
        }
        catch (IOException e){
            assertTrue(false, "Catch an exception " + e);
            return null;
        }

    }

    private static void createDir(){
        File dir = new File(FILE_PATH.substring(0, FILE_PATH.lastIndexOf("/")));
        if (!dir.exists()){
            dir.mkdir();
            Log.info("Create directory");
        }
    }

    public static void finishFailedXml(){
        File file = new File(FILE_PATH);
        if (file.exists()) {
            String tag = "</suite>";
            try {
                FileWriter writer = new FileWriter(file, true);
                writer.append(tag);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                catchException(e);
            }
        }
    }

    public static void createFailedXml(String suiteName, String src){
        //Create dir.
        createDir();

        String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\">\n" +
                "        <suite name=\"Rerun of "+suiteName+"\">\n";
        String param = "";
        if (!src.isEmpty()){
            param = "<parameter name=\"src\" value=\""+src+"\"/>\n";
        }

        try {
            FileWriter writer = new FileWriter(new File(FILE_PATH), true);
            writer.write(head);
            writer.append(param);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            catchException(e);
        }
    }



    public static void addFailedTest(String testClass){
        //Save values
        String test = "<test name=\""+testClass+"\">\n" +
                "        <classes>\n" +
                "            <class name=\""+testClass+"\"/>\n" +
                "        </classes>\n" +
                "    </test>\n";

        //Write necessary data to file.
        try {
            FileWriter writer = new FileWriter(new File(FILE_PATH), true);
            writer.append(test);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            catchException(e);
        }
    }

    public static void createSuccessXml(String suiteName){
        //Create dir.
        createDir();

        String head = "";
        String param = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<testng-results >\n" +
                "  <reporter-output>\n" +
                "  </reporter-output>\n" +
                "  <suite name=\""+suiteName+"\" duration-ms=\"6055\" started-at=\"2016-12-02T10:56:29Z\" finished-at=\"2016-12-02T10:56:35Z\">\n" +
                "    <groups>\n" +
                "    </groups>";

        try {
            FileWriter writer = new FileWriter(new File(SUCCESS_FILE_PATH), true);
            writer.write(head);
            writer.append(param);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            catchException(e);
        }
    }

    public static void finishSuccesXml(){
        File file = new File(SUCCESS_FILE_PATH);
        if (file.exists()) {
            String tag = "</suite>\n" +
                    "</testng-results>";
            try {
                FileWriter writer = new FileWriter(file, true);
                writer.append(tag);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                catchException(e);
            }
        }
    }

    public static void addSuccessTest(String className, String methodName, String status){
        //Save values
        String test = "<test name=\""+className+"\" duration-ms=\"1717\" started-at=\"2016-12-02T10:56:29Z\" finished-at=\"2016-12-02T10:56:31Z\">\n" +
                "      <class name=\""+className+"\">\n" +
                "        <test-method status=\""+status+"\" signature=\"test()[pri:0, instance:"+className+"@7cd62f43]\" name=\"" + methodName + "\" duration-ms=\"1\" started-at=\"2016-12-02T12:56:31Z\" finished-at=\"2016-12-02T12:56:31Z\">\n" +
                "          <reporter-output>\n" +
                "          </reporter-output>\n" +
                "        </test-method>\n" +
                "      </class>\n" +
                "    </test>\n";

        //Write necessary data to file.
        try {
            FileWriter writer = new FileWriter(new File(SUCCESS_FILE_PATH), true);
            writer.append(test);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            catchException(e);
        }
    }



    public static Object[][] dataProviderFile(String filePath){
        List<String> emails = listReader(filePath);
        Object[][] objects = new Object[emails.size()][1];
        for (int i = 0; i < emails.size(); i++) {
            objects[i][0] = emails.get(i);
        }
        return objects;
    }

    /**
     * Converts strings from file to TestNG DataProvider applicable data.<br/>
     * Each parameter in file should be delimited with <code>'-->'</code>
     *
     * @param filePath Path to file with content for DataProvider
     * @return Array of objects for TestNG DataProvider
     */
    public static Object[][] readDataProviderFile(String filePath) {
        Object[][] fileContent = dataProviderFile(filePath);
        int dataProviderParametersSize = ((String) fileContent[0][0]).split(DATA_PROVIDER_FILE_DELIMITER).length;
        Object[][] dataProvider = new Object[fileContent.length][dataProviderParametersSize];

        for (int i = 0; i < fileContent.length; i++) {
            String[] currentLine = ((String) fileContent[i][0]).split(DATA_PROVIDER_FILE_DELIMITER);
            System.arraycopy(currentLine, 0, dataProvider[i], 0, dataProviderParametersSize);
        }

        return dataProvider;
    }

    /**
     * Converts strings from file to TestNG DataProvider applicable data.<br/>
     * Each parameter in file should be delimited with <code>'-->'</code>
     *
     * @param path Path to file with content for DataProvider
     * @return Array of objects for TestNG DataProvider
     */
    public static Iterator<Object[]> readFileAsDataProvider(String path) {
        try {
            URL systemResource = ClassLoader.getSystemResource(path);
            FileSystems.newFileSystem(systemResource.toURI(), Collections.emptyMap());
            return Files.lines(Paths.get(systemResource.toURI()))
                    .parallel()
                    .map(s -> s.split(DATA_PROVIDER_FILE_DELIMITER))
                    .map(Object[].class::cast)
                    .collect(Collectors.toList())
                    .iterator();
        } catch (Exception e) {
            throw new RuntimeException("Cant read file as data provider", e);
        }
    }


    public static String fileReader(File file){
        BufferedReader in;
        String result = "";
        try {
            in = new BufferedReader(new FileReader(file));
            String str;
            while ((str = in.readLine()) != null) {
                result+=str+"\n";
            }
            in.close();
        }
        catch (IOException e){
            assertTrue(false, "Catch an exception " + e);
        }

        return result;
    }



    public static String getBody(Response response){
        return response.readEntity(String.class);
    }

    public static HttpResponse getResponseHeader(String method, String link,String header){
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(),SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);


            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslsf)
                    .build();


            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(2000);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .setConnectionManager(cm)
                    .build();
           // String encoding = new String(Base64.encodeBase64((""+user.getUserName()+":"+user.getPassword()+"").getBytes()));
            HttpRequestBase httpGet;
            switch (method){
                case "DELETE":
                    httpGet = new HttpDelete(link);
                    break;
                default:
                    httpGet = new HttpGet(link);
            }
            httpGet.setHeader("Authorization",  header);

            HttpResponse response = httpClient.execute(httpGet);
            return response;
        }
        catch (Exception e){
            throw new CustomException(e.getMessage());
        }
    }
    public static HttpResponse getResponse(String method, String link, String body) {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(),SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);


            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslsf)
                    .build();


            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(2000);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .setConnectionManager(cm)
                    .build();

            HttpEntityEnclosingRequestBase requestBase;
            switch (method){
                case "PUT":
                    requestBase = new HttpPut(link);
                    break;
                case "POST":
                    requestBase = new HttpPost(link);
                    break;
                default:
                    throw new CustomException("Unknown http method.");
            }
            requestBase.addHeader("Content-type", "application/x-www-form-urlencoded");
            requestBase.setEntity(new StringEntity(body));
           // requestBase.setHeader("Authorization", "");

            HttpResponse response = httpClient.execute(requestBase);
            if (response!=null)return response;
            else throw new CustomException("Response is null");
        }
        catch (Exception e){
            throw new CustomException("Catch "+e);
        }
    }

    /**
     * Method returned links size in byte
     *
     * @param url - files or page link
     * @param typeSize - type size: mb, kb or byte (default)
     * @return size in byte
     */
    public static long getFileSize(String url, String typeSize){
        URLConnection con;
        int size;
        try {
            con = new URL(url).openConnection();
            size = con.getContentLength();
        }
        catch (IOException e) {
            throw new CustomException("Catch "+e);
        }
        switch (typeSize){
            case("kb"):
                return size/1024;
            case("mb"):
                return size/1048576;
            default:
                return size;
        }
    }

    public static URLConnection getURLConnection(String url){
        try {
           return new URL(url).openConnection();
        }
        catch (IOException e) {
            throw new CustomException("Catch "+e);
        }

    }

    /**
     * @param templateLocation freemaker template
     * @param mapData use in template
     * @return writer object
     */
    public static Writer getWriterFromTemplate(Map mapData, String templateLocation){
        Configuration configuration = new Configuration();
        StringWriter writer = new StringWriter();
        try {
            Template template = configuration.getTemplate(PROP_PATH+templateLocation);
            template.process(mapData, writer);
            writer.flush();
        }
        catch (IOException | TemplateException e) {
            catchException(e);
        }
        return writer;
    }



    /**
     * @param filePath - part url csv, example - TM/csv/en/fb-product-feed.csv
     * @return list with data csv
     */
    public static List<String[]> csvReader(String filePath){
        List<String[]> csv = null;
        try {
            CSVReader reader = new CSVReader(new URLReader(new URL(filePath)));
            csv=reader.readAll();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return csv;
    }

    public static String getValueWithReplace(String fileLocation,String key, String text1, String text2){
        try {
            Map<String, String> map = new HashMap<>();
            BufferedReader in = new BufferedReader( new FileReader(PROP_PATH+fileLocation));
            String line;
            while ((line = in.readLine()) !=null){
                String parts[] = line.split("=");
                map.put(parts[0],parts[1]);
            }
            in.close();
            return map.get(key).replaceAll("PART1", text1).replaceAll("PART2", text2);
        }
        catch (IOException e){
            assertTrue(false, "Catch an exception " + e);
            return null;
        }

    }
}
package utility.services;


import org.apache.commons.codec.binary.Base64;
import utility.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class EncodingService {

    public  static String convertUTF(String origin){
        String convert = null;
        try {
            convert = new String(origin.getBytes("ISO-8859-1"), "UTF-8");
        }
        catch (UnsupportedEncodingException e){
            Log.error(e.toString());
        }

        return convert;
    }

    public static String urlDecode(String url){
        try {
            return URLDecoder.decode(url, "UTF-8");
        }
        catch (UnsupportedEncodingException e){
            ReportService.assertTrue(false,e.toString());
            return null;
        }
    }

    public static String urlEncoder(String url){
        try {
            return URLEncoder.encode(url, "UTF-8").replace("+", "%20");
        }
        catch (UnsupportedEncodingException e){
            ReportService.catchException(e);
            return null;
        }
    }

    public static String decodeBase64(String origin){
        byte[] valueDecoded= Base64.decodeBase64(origin.getBytes());
        String res = new String(valueDecoded);
        Log.info("Decoded base64 - "+res);
        return res;

    }

    public static String encodeMD5(String text) {
        String result = "";
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");

            m.reset();
            m.update(text.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            result = bigInt.toString(16);
            while (result.length() < 32) {
                result = "0" + result;
            }
            System.out.println(result);
        }
        catch (NoSuchAlgorithmException e){
            ReportService.assertTrue(false, "Catch "+e);
        }
        return result;

    }

}

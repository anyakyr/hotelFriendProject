package utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import static utility.services.EncodingService.convertUTF;
import static utility.services.ReportService.catchException;


public class PropertyReader {
	public Properties properties;

	public PropertyReader(String file){
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(file);
		try {
			properties = new Properties();
			if (file.contains(".properties")){
				properties.load(inputStream);
			}
			else {
				properties.load(new InputStreamReader(inputStream, "UTF-8"));
			}
		}
		catch (IOException e){
			catchException(e);
		}
	}

	public String getValue(String key){
		return properties.getProperty(key)==null ? null :convertUTF(properties.getProperty(key));
	}

	public String getAnyVal(String key){
		return System.getProperty(key)!=null ? System.getProperty(key)
				: getValue(key);
	}

	public Properties getProperties() {
		return this.properties;
	}

}

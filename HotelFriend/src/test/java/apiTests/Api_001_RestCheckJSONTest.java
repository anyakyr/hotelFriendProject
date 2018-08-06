package apiTests;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.annotations.Test;
import utility.Constants;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.AssertJUnit.assertEquals;


/**
 * Created by Anya on 05.08.2018.
 */
public class Api_001_RestCheckJSONTest {

    @Test
    public void ResponseContentTypeIsJson() throws IOException {

        String jsonMimeType = "application/json";
        HttpUriRequest request = new HttpGet(Constants.baseUrl);

        HttpResponse response = HttpClientBuilder.create().build().execute( request );

        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();

        assertEquals(jsonMimeType, mimeType);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }
}

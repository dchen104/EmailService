import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.simple.JSONObject;

import java.io.IOException;

// The different subclasses that implement this abstract class represent the different servers we can send to.
// You can implement this class if you want to add a new server to possible send emails to. Right now, only Snailgun
// and Spendgrid should be implemented.
public abstract class EmailSender {
    protected String url;
    protected String contentType;
    protected String apiKey;
    protected HttpClientResponseHandler<String> responseHandler;

    abstract JSONObject convertEmail(Email email);

    public String send(Email email) {
       try {
           JSONObject js = convertEmail(email);
           HttpClient httpClient = HttpClientBuilder.create().build();

           HttpPost request = new HttpPost(this.url);
           StringEntity params = new StringEntity(js.toJSONString());
           request.addHeader("Content-Type", contentType);
           request.addHeader("X-Api-Key", this.apiKey);
           request.setEntity(params);

           final String responseBody = httpClient.execute(request, responseHandler);
           System.out.println("----------------------------------------");
           System.out.println(responseBody);
           return responseBody;
       } catch (IOException e) {
           throw new RuntimeException("Email Sending Error:" + e.getMessage());
       }
    }
}

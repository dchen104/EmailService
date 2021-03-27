import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

// Spendgrid implementation of an EmailSender
public class SpendgridSender extends EmailSender {

    public SpendgridSender(Properties properties) {
        super.apiKey = properties.getProperty("spend.x.api.key");
        super.url = properties.getProperty("spend.url");
        super.contentType = "application/json";

        final HttpClientResponseHandler<String> responseHandler = new HttpClientResponseHandler<String>() {
            @Override
            public String handleResponse(
                    final ClassicHttpResponse response) throws IOException {
                final int status = response.getCode();

                // For Spendgrid, we only really care that we received back an OK
                if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION) {
                        return "Successful email sent to Spendgrid";
                } else {
                    try {
                        // Return the error body when not a successful OK. For some reason, the API key is not working
                        // when sending to Sendgrid. Just making sure to return the error message returned by the server.
                        HttpEntity entity = response.getEntity();
                        InputStream streamBody = entity.getContent();
                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(streamBody, "UTF-8"));
                        return jsonObject.toJSONString();
                    } catch (org.json.simple.parser.ParseException ex) {
                        throw new RuntimeException("Spendgrid Response is not valid JSON");
                    }
                }
            }

        };

        super.responseHandler = responseHandler;
    }

    JSONObject convertEmail(Email email) {
        JSONObject js = new JSONObject();
        js.put("senders", email.getFromName() + " <" + email.getFrom() + ">");
        js.put("recipient", email.getToName() + " <" + email.getTo() + ">");
        js.put("subject", email.getSubject());
        js.put("body", email.getBody());

        return js;
    }
}

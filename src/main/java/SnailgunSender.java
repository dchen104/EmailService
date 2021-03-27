import org.apache.hc.client5.http.ClientProtocolException;
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

// Snailgun implementation of an EmailSender
public class SnailgunSender extends EmailSender {

    public SnailgunSender(Properties properties) {
        super.apiKey = properties.getProperty("snail.x.api.key");
        super.url = properties.getProperty("snail.url");
        super.contentType = "application/json";

        final HttpClientResponseHandler<String> responseHandler = new HttpClientResponseHandler<String>() {
            @Override
            public String handleResponse(
                    final ClassicHttpResponse response) throws IOException {
                final int status = response.getCode();
                if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION) {
                    final HttpEntity entity = response.getEntity();
                    try {
                        InputStream streamBody = entity.getContent();
                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(streamBody, "UTF-8"));

                        // Right now, just throw random RuntimeException if email fails to queue. If I had more time, I'd add more advanced logic here to
                        // check for a valid response.
                        if (jsonObject.get("status") == "failed") {
                            throw new RuntimeException("Snailgun failed to enqueue email");
                        }
                        return jsonObject.toJSONString();
                    } catch (org.json.simple.parser.ParseException ex) {
                        throw new RuntimeException("Snailgun Response is not valid JSON");
                    }
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }

        };

        super.responseHandler = responseHandler;
    }

    JSONObject convertEmail(Email email) {
        JSONObject js = new JSONObject();
        js.put("from_email", email.getFrom());
        js.put("from_name", email.getFromName());
        js.put("to_email", email.getTo());
        js.put("to_name", email.getToName());
        js.put("subject", email.getSubject());
        js.put("body", email.getBody());

        return js;
    }
}

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Properties;

/**
 * Created by Derek on 3/27/2021.
 */
public class EmailHandler implements HttpHandler {

    private String defaultSender;
    private EmailSender sender;

    // Instantiates a new email handler by using the reading and using config.properties files. Contains logic for
    // deciding which email server is to be set as the default.
    public EmailHandler() {
            Properties prop = new Properties();

            String propFileName = "config.properties";

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            if (inputStream != null) {
                try {
                    prop.load(inputStream);
                } catch (IOException e) {
                    throw new RuntimeException("Cannot load properties file");
                }
            } else {
                throw new RuntimeException("property file '" + propFileName + "' not found in the classpath");
            }

            // Logic for choosing which sender to use
            this.defaultSender = prop.getProperty("default.sender");

            if (this.defaultSender == null) {
                throw new RuntimeException("No default sender set");
            }
            if (this.defaultSender.equals("snail")) {
                this.sender = new SnailgunSender(prop);
            } else if (this.defaultSender.equals("spend")){
                this.sender = new SpendgridSender(prop);
            } else {
                throw new RuntimeException("Invalid Default Sender chosen");
            }

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        try {
            InputStream streamBody = httpExchange.getRequestBody();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(streamBody, "UTF-8"));
            Email email = Email.fromJsonObject(jsonObject);

            // Validate the email input sent in
            boolean valid = email.validateFields();
            if (!valid) {
                throw new RuntimeException("Invalid email input");
            }

            // Send email to other email server
            String response = this.sender.send(email);

            OutputStream outputStream = httpExchange.getResponseBody();
            httpExchange.sendResponseHeaders(200, response.length());

            outputStream.write(response.getBytes());
            outputStream.flush();
            outputStream.close();
        }
        catch(ParseException e) {
            throw new RuntimeException("Cannot parse");
        }
    }
}

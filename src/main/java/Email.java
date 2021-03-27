import org.json.simple.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Email model class for representing an email sent to our service.
public class Email {

    private String to;
    private String toName;
    private String from;
    private String fromName;
    private String subject;
    private String body;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public static Email fromJsonObject(JSONObject emailJSON) {
        Email email = new Email();
        email.to = (String) emailJSON.get("to");
        email.toName = (String) emailJSON.get("to_name");
        email.from = (String) emailJSON.get("from");
        email.fromName = (String) emailJSON.get("from_name");
        email.subject = (String) emailJSON.get("subject");
        email.body = (String) emailJSON.get("body");
        return email;
    }

    public boolean validateFields() {

        // Basic email regex matching for the from and to fields
        String emailRegex = "^(.+)@(.+)$";
        Pattern emailPattern = Pattern.compile(emailRegex);

        Matcher matcher = emailPattern.matcher(this.to);
        if (!matcher.matches()) {
            return false;
        };

        matcher = emailPattern.matcher(this.from);
        if (!matcher.matches()) {
            return false;
        };

        // Names can be anything as long as they are characters and spaces.
        Pattern namePattern = Pattern.compile("^[ A-Za-z]+$");
        matcher = namePattern.matcher(this.toName);
        if (!matcher.matches()) {
            return false;
        };

        matcher = namePattern.matcher(this.fromName);
        if (!matcher.matches()) {
            return false;
        };

        return true;
    }
}

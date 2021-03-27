import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * Created by Derek on 3/27/2021.
 */
public class Server {

    // Entry point of the program.
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        HttpContext context = server.createContext("/email", new EmailHandler());
        server.start();
        System.out.println("Server started");
    }
}

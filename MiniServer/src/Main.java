import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by asd on 24.05.2017.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException, URISyntaxException, IOException {
        WebSServer server = new WebSServer();
        server.start();
        File htmlFile = new File("js/simulation.html");
        Desktop.getDesktop().browse(htmlFile.toURI());
    }
}

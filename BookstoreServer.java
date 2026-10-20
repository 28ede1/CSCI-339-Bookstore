import org.apache.xmlrpc.webserver.WebServer; 
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.XmlRpcException;
import java.sql.*;
import java.util.*; 

/**
 * BookstoreServer interacts with database to preform client requests.

*/

public class BookstoreServer { 
    
    private BookstoreDatabase database = new BookstoreDatabase();

    public ArrayList<Map<String, Object>> Search(String topic) {
        return database.Search(topic);
    }

    /*
        Add remaining 5 methods.
    */

    public static void main (String [] args) {
        try {

            // load database driver
            Class.forName("org.sqlite.JDBC");

            // Will store connections between request names and Java classes
            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            XmlRpcServer xmlRpcServer;

            // Creates a web server configured to port 8888
            // Tells lab machine OS to accept all incoming connections on port 8888
	        WebServer server = new WebServer(8888);
            xmlRpcServer = server.getXmlRpcServer();

            // map requests starting with "sample." to the requested method in Server class (so sample.SumAndDifference refers to SumAndDifference"
            phm.addHandler("bookstore", BookstoreServer.class);
            xmlRpcServer.setHandlerMapping(phm);
	        System.out.println("Server starting on port 8888");

            //Tells lab machine OS to accept all incoming connections on port 8888
            server.start();
	    } catch (Exception exception) {
            System.err.println("Server: " + exception);
	    }
    }
}

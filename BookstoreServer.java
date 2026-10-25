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
    
    private static BookstoreDatabase database = new BookstoreDatabase();

    public ArrayList<Map<String, Object>> Search(String topic) {
        return database.FindMany(topic);
    }

    public Map<String, Object> Lookup(int item_number) {
        return database.FindOne(item_number);
    }

    public boolean Buy(int item_number) {
        return database.DecrementStock(item_number);
    }

    /*
        Add remaining 3 methods.
    */
    public static void Log() {
	    database.Log(); 
    }
    public static void Restock() {
   	    database.Restock(); 
    }
    public static boolean Update(int item_number, double price){
	    return database.Update(item_number, price); 
    }
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

            System.out.println("TESTING, Log()");
            Log();

            System.out.println("TESTING, Restock()");
            Restock();

            System.out.println("TESTING, Update()");
            Update(53477, 67.99);

	    } catch (Exception exception) {
            System.err.println("Server: " + exception);
	    }
    }
}

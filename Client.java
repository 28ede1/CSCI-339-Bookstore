import java.util.*; 
import java.net.URL;     
import org.apache.xmlrpc.*;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import java.util.*; 
import java.net.URI;

public class Client {
    public static void main (String [] args) {
	if(args.length == 0) {
	    System.out.println("Usage: java Client <server>");
	    System.exit(1);
	}

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        XmlRpcClient client=null;
        try {
            config.setServerURL( URI.create("http://" + args[0] + ":8888").toURL() );
            client = new XmlRpcClient();
            client.setConfig(config);
        } catch (Exception e) {
	    System.err.println("Client error: "+ e);
	}

    /*
        IN PROGRESS:
        
        As of right now, clients can request searching for books by topic.

        Eventually, allow users to enter any command line input like java Client localhost search
        to call a specific method from the server.
    */

    Vector<Object> params = new Vector<Object>();
    params.addElement("college life");

    Vector<Object> params2 = new Vector<Object>();
    params2.addElement(3);

    Vector<Object> params3 = new Vector<Object>();
    params3.addElement(486);


    try {
        Object[] result = (Object[])client.execute("bookstore.Search", params.toArray());

        Map<?, ?> result2 = (Map<?, ?>) client.execute(
            "bookstore.Lookup", params2.toArray()
        );

        boolean result3 = (boolean) client.execute(
            "bookstore.Buy", params3.toArray()
        );

        if (result.length == 0) {
            System.out.println("No books found for that topic.");
        }
        else {
            for (Object book : result) {
                System.out.println(book);
            }
        }

        if (result2.isEmpty()) {
            System.out.println("No book found for that id.");
        }
        else {
            System.out.println(result2);
        }

        if (!result3) {
            System.out.println("Could not complete purchase. May be out of stock.");
        }
        else {
            System.out.println("Successfully purchased.");
        }

    } catch (Exception e) {
        System.err.println("Client error: " + e);
    }
    }
}
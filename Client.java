import java.util.*; 
import java.net.URL;     
import org.apache.xmlrpc.*;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import java.util.*; 

public class Client {
    public static void main (String [] args) {
	if(args.length == 0) {
	    System.out.println("Usage: java Client <server>");
	    System.exit(1);
	}

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        XmlRpcClient client=null;
        try {
            config.setServerURL(new URL("http://" + args[0] + ":" + 8888));
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

    try {
        Object[] result = (Object[])client.execute("bookstore.Search", params.toArray());

        if (result.length == 0) {
            System.out.println("No books found for that topic.");
        }
        else {
            for (Object book : result) {
                System.out.println(book);
            }
        }
    } catch (Exception e) {
        System.err.println("Client error: " + e);
    }
    }
}
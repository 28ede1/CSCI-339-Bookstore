import java.util.*; 
import java.net.URL;     
import org.apache.xmlrpc.*;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import java.util.*; 
import java.net.URI;

public class Client {

    public static void Search(XmlRpcClient client, String topic) throws XmlRpcException {
        ArrayList<Map<String, Object>> books = new ArrayList<>();

        Vector<Object> params = new Vector<Object>();
        params.addElement(topic);
        Object[] result = (Object[])client.execute("bookstore.Search", params.toArray());

        if (result.length == 0) {
            System.out.println("No books found for that topic.");
        }
        else {
            for (Object book : result) {
                books.add((Map<String, Object>) book);
            }
        }
        System.out.println(books);
    }

    public static void Lookup(XmlRpcClient client, int item_number) throws XmlRpcException {
        Map<String, Object> book = new HashMap<>();

        Vector<Object> params = new Vector<Object>();
        params.addElement(item_number);
        Map<String, Object> result = (Map<String, Object>) client.execute(
            "bookstore.Lookup", params.toArray()
        );

        if (result.isEmpty()) {
            System.out.println("No book found for that id.");
        }
        else {
           book = result;
        }

        System.out.println(book);
    }

    public static void Buy(XmlRpcClient client, int item_number) throws XmlRpcException {
        Map<String, Object> book = new HashMap<>();

        Vector<Object> params = new Vector<Object>();
        params.addElement(item_number);
        boolean result = (boolean) client.execute(
            "bookstore.Buy", params.toArray()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("purchased", result);
        response.put("message", result
            ? "Bought book " + item_number
            : "Could not complete purchase. May be out of stock.");

        System.out.println(response);    
    }

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

    try {
        Search(client, "college life");
        Lookup(client, 53477);
        Buy(client, 53477);

        /* 
            call the remaining 3 methods here.
        */

    } catch (Exception e) {
        System.err.println("Client error: " + e);
    }
    }
}
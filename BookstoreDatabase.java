import java.sql.*;
import java.util.*; 

/**
 * BookstoreDatabase handles all required database operations for this assignment.
 * The methods for this class directly talk to the sqlite database.
 * Does not handle XML-RPC communication
*/

public class BookstoreDatabase {

   private String databaseUrl = "jdbc:sqlite:bookstore.db";

   private static final int RESTOCK_AMOUNT = 10;
   /**
    * Lists all books belonging to a topic
    * 
    * Given a topic, return a list of maps containing integer "id" and string "title" of a book.
    */
   public ArrayList<Map<String, Object>> FindMany(String topic) {
        Connection c = null;
        Statement stmt = null;
        String query = "SELECT * FROM BOOKS WHERE TOPIC = '" + topic + "'";
        ArrayList<Map<String, Object>> books = new ArrayList<>();

        try {
            c = DriverManager.getConnection(databaseUrl);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( query );

            while ( rs.next() ) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                Map<String, Object> book = new HashMap<>();
                book.put("id", id);
                book.put("title", title);
                books.add(book);
            }

            rs.close();
            stmt.close();
            c.close();

        } catch  ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            throw new RuntimeException("Could not search for books", e);
        }
        return books;
   }

    /**
        * Finds a single book.
        * 
        * Given an int "item_number" returns a map string "title" of a book,
        * float cost of the book, string "topic", and a boolean of whether the item is still in stock
        * (stock count is greater than 0)
    */
   public Map<String, Object> FindOne(int item_number) {
        Connection c = null;
        Statement stmt = null;
        String query = "SELECT * FROM BOOKS WHERE ID = '" + item_number + "'";
        Map<String, Object> book = new HashMap<>();

        try {
            c = DriverManager.getConnection(databaseUrl);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( query );

            if (rs.next()) {
                String title = rs.getString("title");
                double price = rs.getDouble("price");
                String topic = rs.getString("topic");
                boolean in_stock = rs.getInt("stock") > 0;

                book.put("title", title);
                book.put("price", price);
                book.put("topic", topic);
                book.put("in_stock", in_stock);
            }

            rs.close();
            stmt.close();
            c.close();

        } catch  ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            throw new RuntimeException("Could not find for book.", e);
        }
        return book;
   }

    /**
        * Purchases a book.
        * 
        * Returns true if the book can be purchased.
        * Accessed the book by int "item_number" and reduces stock by 1.
        * Does not allow negative stock amounts. Assume desired quantity is always 1.
        * Should also update the log of purchases.
    */
   public boolean DecrementStock(int item_number) {
        Connection c = null;
        Statement stmt = null;
        

        // written with one query statement rather than having to subtract stock THEN check if out of stock, or check if out of stock then subtract
        String update_query = "UPDATE BOOKS SET STOCK = STOCK - 1 WHERE ID = " + item_number + " AND STOCK > 0";
        String lookup_query = "SELECT TITLE FROM BOOKS WHERE ID = " + item_number;
        boolean is_updated = false;

        try {
            c = DriverManager.getConnection(databaseUrl);
            c.setAutoCommit(false);
            stmt = c.createStatement();
            int row_updated = stmt.executeUpdate( update_query );

            if (row_updated == 1) {

                ResultSet rs = stmt.executeQuery(lookup_query);

                if (!rs.next()) { 
                    throw new SQLException("Book not found during stock update.");
                }

                String title = rs.getString("TITLE");
                rs.close();

                String purchase_query = "INSERT INTO PURCHASES (BOOK_ID, TITLE) VALUES (" + item_number + ", '" + title + "')";
                stmt.executeUpdate(purchase_query);

                is_updated = true;

            }

            c.commit();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());

            // undo changes if the purchase failed.
            if (c != null) {
                try {
                    c.rollback();
                } catch (SQLException rollbackError) {
                    System.err.println(rollbackError.getMessage());
                }
            }

            throw new RuntimeException("Could not update book.", e);
        }
        return is_updated;
   }

    /**
        * Prints all book purchases.
        * 
        * Accesses purchases from the "Purchase" table rather than the "Book" table.
    */
   public void Log() {
        Connection c = null;
        Statement stmt = null;

        String retrieve_query = "SELECT * FROM PURCHASES";

        try 
            {
            c = DriverManager.getConnection(databaseUrl);
            stmt = c.createStatement();
            
            ResultSet rs = stmt.executeQuery(retrieve_query);

            // allows you to index md to get column name
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount(); 

            while ( rs.next() ) {
                StringBuilder line = new StringBuilder();
                for ( int i = 1; i <= cols; i++ ) {
                    if (i > 1) {
                        line.append(" | ");
                    } 

                    line.append(md.getColumnName(i)).append("=").append(rs.getString(i));
                }

                System.out.println(line);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            throw new RuntimeException("Could not read purchase log.", e);
        }			
        return;
   }

    /**
        * Updates stocks for all books. 
        * 
        * Updates stocks for each book by some set amount.
    */
   public void Restock() {
        Connection c = null;
        PreparedStatement ps = null;
        int restock_amt = 50;

        try {
            c = DriverManager.getConnection(databaseUrl);
            ps = c.prepareStatement("UPDATE BOOKS SET STOCK = STOCK + ?"); 
            ps.setInt(1, restock_amt );
            ps.executeUpdate();
            System.out.println("Successfully Restocked " + restock_amt + " books.");
        } catch (Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage()); 
            throw new RuntimeException("Could not restock books." , e); 
        }
        return;
   }

    /**
        * Updates price information for a book.
        * 
        * Given a int "item_number" and float "price", updates the price
    */
   public boolean Update(int item_number, double price) {
        Connection c = null;
        PreparedStatement ps = null;

        String update_query = "UPDATE BOOKS SET PRICE = ? WHERE ID = ?";
        boolean updated = false; 
        
        
        try {
            c = DriverManager.getConnection(databaseUrl);
            ps = c.prepareStatement(update_query); 
            ps.setDouble(1, price);
            ps.setInt(2, item_number); 
            updated = ps.executeUpdate() == 1;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage()); 
            throw new RuntimeException("Could not update books." , e); 
        }
            return updated;
    }
}
 

import java.sql.*;
import java.util.*; 

/**
 * BookstoreDatabase handles all required database operations for this assignment.
 * The methods for this class directly talk to the sqlite database.
 * Does not handle XML-RPC communication
*/

public class BookstoreDatabase {

   private String databaseUrl = "jdbc:sqlite:bookstore.db";

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
    return;
   }

    /**
        * Updates stocks for all books. 
        * 
        * Updates stocks for each book by some set amount.
    */
   public void Restock() {
    return;
   }

    /**
        * Updates price information for a book.
        * 
        * Given a int "item_number" and float "price", updates the price
    */
   public boolean Update(int item_number, float price) {
    return false;
   }
}
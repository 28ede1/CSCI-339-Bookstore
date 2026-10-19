import java.sql.*;

public class SQLiteJDBC {

   public static void main( String args[] ) {
        Connection c = null;
        Statement stmt = null;
        
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:bookstore.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            // can only do insertion once, PAY ATTENTION TO id
            // String sql = "INSERT INTO BOOK (ID,TITLE,TOPIC,STOCK,PRICE) " +
            //             "VALUES (1, 'Achieve Less Bugs and More Hugs in CSCI 339', 'distributed systems', 50, 200.00 );"; 
            // stmt.executeUpdate(sql);

            // sql = "INSERT INTO BOOK (ID,TITLE,TOPIC,STOCK,PRICE) " +
            //         "VALUES (2, 'Distributed Systems for Dummies', 'distributed systems', 100, 140.00 );"; 
            // stmt.executeUpdate(sql);

            // sql = "INSERT INTO BOOK (ID,TITLE,TOPIC,STOCK,PRICE) " +
            //         "VALUES (3, 'Surviving College', 'college life', 20, 120.00 );"; 
            // stmt.executeUpdate(sql);

            // sql = "INSERT INTO BOOK (ID,TITLE,TOPIC,STOCK,PRICE) " +
            //         "VALUES (4, 'Cooking for the Impatient Undergraduate', 'college life', 60, 100.00 );"; 
            // stmt.executeUpdate(sql);


            ResultSet rs = stmt.executeQuery( "SELECT * FROM BOOK;" );

            // Use a separate Statement for updates so we don't close the ResultSet we're reading.
            Statement updateStmt = c.createStatement();

            while ( rs.next() ) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String topic  = rs.getString("topic");
                int stock = rs.getInt("stock");
                float price = rs.getFloat("price");

                int new_stock = stock * -1;
                String sql = "UPDATE BOOK SET STOCK = " + new_stock + " WHERE ID = " + id;;
                updateStmt.executeUpdate(sql);

                
                System.out.println( "ID = " + id );
                System.out.println( "TITLE = " + title );
                System.out.println( "TOPIC = " + topic );
                System.out.println( "STOCK = " + new_stock );
                System.out.println( "PRICE = " + price );
                System.out.println();
            }

            rs.close();
            updateStmt.close();
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Records created successfully");
        System.out.println("Select operation Done successfully");
   }
}
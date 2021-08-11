import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Receipt {

    public static String writeReceipt(Client c, Product p, String date){
        String txt = "Satin Alma Basarili";
        txt += ("\n---------------Receipt---------------");
        txt += ("\nClient : " + c.getClientID() + " - " + c.getClientNO());
        txt += ("\nProduct : " + p.getProductID() + " - " + p.getProductName());
        txt += ("\nCost : " + p.getPrice());
        txt += ("\nRemaining Balance : " + c.getBalance());
        txt += ("\nDate : " + date);
        return txt;
    }

    public static void insertReceipt(Client c, Product p) throws SQLException {
        Connection connect = DriverManager.getConnection(Database.DATABASE_URL, Database.USERNAME, Database.PASSWORD);

        PreparedStatement ps = connect.prepareStatement("INSERT INTO RECEIPT(clientID, productID) VALUES (?,?)");

        ps.setInt(1,c.getClientID());
        ps.setInt(2,p.getProductID());

        int i=ps.executeUpdate();
    }

    public static String getTodayDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        LocalDate localDate = LocalDate.now();
        return dtf.format(localDate);
    }
}

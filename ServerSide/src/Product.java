import java.sql.*;

public class Product {
    private int productID;
    private String productName;
    private int sellerID;
    private double price;

    public Product(int productID, String productName, int sellerID, double price){
        this.productID = productID;
        this.productName = productName;
        this.sellerID = sellerID;
        this.price = price;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getSellerID() {
        return sellerID;
    }

    public void setSellerID(int sellerID) {
        this.sellerID = sellerID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public static Product productControl(String no){
        int temp = Database.isExist("PRODUCT","productID", no);
        if(temp == 0){
            return null;
        }
        try {
            return getProductFromDB(no);
        } catch (SQLException throwables) {
            System.out.println("---Client hata verdi---");
            throwables.printStackTrace();
            return null;
        }
    }

    public static Product getProductFromDB(String no) throws SQLException {
        Connection connect = null;
        Statement statement = null;
        connect = DriverManager.getConnection(Database.DATABASE_URL, Database.USERNAME, Database.PASSWORD);

        statement = connect.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM PRODUCT WHERE productID = '"+ no +"';");

        if(result.next())
            return new Product(result.getInt(1),result.getString(2),result.getInt(3),result.getDouble(4));
        else
            return null;
    }

    public static String getAllProductsFromDB() throws SQLException{

        Connection connect = null;
        Statement statement = null;
        connect = DriverManager.getConnection(Database.DATABASE_URL, Database.USERNAME, Database.PASSWORD);

        statement = connect.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM PRODUCT;");

        String allProducts = "";

        while (result.next()){
            allProducts += result.getInt(1) + "-" + result.getString(2) + "-" + result.getInt(3) + "-" + result.getDouble(4) + "!";
        }

        return allProducts;
    }
}

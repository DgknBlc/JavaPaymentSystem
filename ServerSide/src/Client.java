import java.sql.*;

public class Client {

    private int clientID;
    private String clientNO;
    private double balance;
    private String entryDate;

    public Client(int clientID,String clientNO, double balance, String entryDate) {
        this.clientID = clientID;
        this.clientNO = clientNO;
        this.entryDate = entryDate;
        this.balance = balance;
    }

    public int getClientID() {
        return clientID;
    }

    public String getClientNO() {
        return clientNO;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public static Client clientControl(String no){
        int temp = Database.isExist("CLIENT","clientNO", no);
        if(temp == 0){
            Database.executeQuery("INSERT INTO CLIENT(clientNO, balance) VALUES('"+ no +"', 50);");
            System.out.println("Yeni müsteri olusturuldu.");
        }
        try {
            return getClientFromDB(no);
        } catch (SQLException throwables) {
            System.out.println("---Client hata verdi---");
            throwables.printStackTrace();
            return null;
        }
    }

    public static Client getClientFromDB(String no) throws SQLException {
        Connection connect = null;
        Statement statement = null;
        connect = DriverManager.getConnection(Database.DATABASE_URL, Database.USERNAME, Database.PASSWORD);

        statement = connect.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM CLIENT WHERE clientNO = '"+ no +"';");

        result.next();
        return new Client(result.getInt(1),result.getString(2),result.getDouble(3),result.getString(4));
    }

    public void updateClientToDB() throws SQLException {

        Connection connect = DriverManager.getConnection(Database.DATABASE_URL, Database.USERNAME, Database.PASSWORD);

        PreparedStatement ps = connect.prepareStatement("UPDATE CLIENT SET balance = ? WHERE clientNO = ?;");

        ps.setDouble(1,this.getBalance());
        ps.setString(2,this.clientNO);

        int i=ps.executeUpdate();
        System.out.println(i +" records affected");


    }

}

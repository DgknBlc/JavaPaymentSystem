import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.*;

public class Database {

    static String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    static String DATABASE_URL = "jdbc:sqlserver://localhost:1433;database=Market;integratedSecurity=true;";
    static String USERNAME = "sa";
    static String PASSWORD = "159";


    public static void executeQuery(String query){

        Connection connect = null;
        Statement statement = null;
        try{

            Class.forName(JDBC_DRIVER); //Driveri ekliyoruz.
            connect = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);

            statement = connect.createStatement();
            statement.executeQuery(query);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                connect.close();
                statement.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static int isExist(String table, String column, String no){
        Connection connect = null;
        Statement statement = null;
        try{

            Class.forName(JDBC_DRIVER);
            connect = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);

            statement = connect.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(1) FROM "+ table + " WHERE "+ column + " = '"+ no + "';");
            rs.next();
            return rs.getInt(1);
        }
        catch(SQLException | ClassNotFoundException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        finally
        {
            try
            {
                connect.close();
                statement.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return 0;
    }



}

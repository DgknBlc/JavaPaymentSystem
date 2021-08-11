import java.net.*;
import java.io.*;

public class Main {

    static final int PORT = 3333;           //Client - Server Portu
    static final int OP_PORT = 3334;        //Server - Operator Portu
    static final int OPTEL_PORT = 3335;     //Operator - Tel Portu

    public static void main(String[] args)
    {
        new Operator(true).start();  //Operator'un Telefon iletişimini çalıştırır.
        new Operator(false).start();    //Operator'un Server iletişimini çalıştırır.

        ServerSocket ss = null;         //Geri kalan Serveri Çalıştırır.
        Socket s = null;

        System.out.println("Dinlemeye başladı.");
        try {
            ss = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
                s = ss.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            new ServerThread(s).start();
        }
    }
}

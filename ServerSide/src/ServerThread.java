import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class ServerThread extends Thread{

    protected Socket socket;

    public ServerThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run(){
        DataInputStream dis;
        DataOutputStream dout;
        try {
            dis = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }
        String line;
        while (true) {
            try {
                line = dis.readUTF();
                if (line.equalsIgnoreCase("STOP")) {
                    dis.close();
                    socket.close();
                    return;
                } else {
                    System.out.println("Gelen Mesaj : " + line);
                    String[] code = line.split(" ");

                    if (code[0].equalsIgnoreCase("gap")){
                        dout.writeUTF(Product.getAllProductsFromDB());
                        dout.flush();

                    }else if(code[0].length() == 11){

                        Product p = Product.getProductFromDB(code[1]);
                        Client c = Client.clientControl(code[0]);

                        if(c != null && p != null){

                            Socket opSocket = new Socket("localhost", Main.OP_PORT);  //Gelen isteği Operatore Yollamak için Socket oluşturuyor.
                            DataInputStream opDin = new DataInputStream(opSocket.getInputStream());
                            DataOutputStream opDout = new DataOutputStream(opSocket.getOutputStream());
                            opDout.writeUTF(code[0]+"-"+p.getProductName()+"-"+p.getPrice());
                            opDout.flush();

                            line = opDin.readUTF();
                            if(line.equalsIgnoreCase("success")){
                                if (c.getBalance()>p.getPrice()){
                                    c.setBalance(c.getBalance()-p.getPrice());
                                    c.updateClientToDB();
                                    String r = Receipt.writeReceipt(c,p,Receipt.getTodayDate());
                                    Receipt.insertReceipt(c,p);
                                    opDout.writeUTF(r);
                                    dout.writeUTF(r);
                                }else{
                                    opDout.writeUTF("Error2-Bakiye Yetersiz.");
                                    dout.writeUTF("Error2-Bakiye Yetersiz.");
                                }
                            }
                            else{
                                opDout.writeUTF("Error1-İslem Başarisiz.");
                                dout.writeUTF("Error1-İslem Başarisiz.");
                            }

                            dout.flush();
                            opDout.flush();
                            opDin.close();
                            opSocket.close();
                        }
                    }
                    dout.flush();
                }
            } catch (IOException | SQLException e ) {
                System.out.println("Error0-Cihaz ile Bağlantı kesildi!");
                return;
            }
        }
    }
}


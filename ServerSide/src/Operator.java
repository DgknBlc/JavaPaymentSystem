import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

public class Operator extends Thread{

    static Hashtable<String, Worker> workerHT = new Hashtable<>();
    ServerSocket ss = null;
    Socket s = null;
    boolean isWorker;

    public Operator(boolean isWorker){
        this.isWorker = isWorker;
    }

    public void run(){

        System.out.println("Operator Dinlemeye başladı.");
        try {
            if(isWorker){
                ss = new ServerSocket(Main.OPTEL_PORT);
            }else{
                ss = new ServerSocket(Main.OP_PORT);
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
                s = ss.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            if(isWorker){
                Worker w = new Worker(s);
                w.start();
            }else{
                PcWorker w = new PcWorker(s);
                w.start();
            }
        }

    }

    class Worker extends Thread{

        String number;
        Socket socket;
        DataInputStream din;
        DataOutputStream dout;
        boolean flag = true;

        public Worker(Socket socket){ this.socket = socket;}

        public String getNumber() {
            return number;
        }

        public void run(){
            System.out.println("Tel bağlandı.");
            try {
                din = new DataInputStream(socket.getInputStream());
                dout = new DataOutputStream(socket.getOutputStream());

            } catch (IOException e) {
                return;
            }
            String line ="";
            while (flag) {
                try {
                    do {
                        byte[] br = new byte[]{(byte) din.read()};
                        line += new String(br, StandardCharsets.UTF_8);
                    }while(din.available() != 0);
                    System.out.println("WORKER:"+line);
                    if (line.equalsIgnoreCase("STOP")) {    //İşlem kapatmak için var.
                        close();
                        return;
                    }
                    else {                                             //Telefonun bağlantısı ve kaydı için var.
                        if (line.length() == 11){
                            number = line;
                            workerHT.put(line,this);
                            flag = false;
                        }
                        else {
                            dout.writeUTF("Error1:Number is Not Valid.");
                            dout.flush();
                        }
                    }
                } catch (IOException e ) {
                    e.printStackTrace();
                    close();
                    return;
                }
            }
        }

        void close(){
            if(this.number != null)
                workerHT.remove(this.number);
            try {
                din.close();
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }


    }  //Telefonu kaydetme için kodlar burada ek başka bir durum yok.

    class PcWorker extends Worker{
        public PcWorker(Socket socket){
            super(socket);
        }

        @Override
        public void run() {
            try {
                din = new DataInputStream(socket.getInputStream());
                dout = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                return;
            }
            String line;
            while (flag) {
                try {
                    line = din.readUTF();
                    System.out.println("PCWORKER:"+line);
                    if (line.equalsIgnoreCase("STOP")) {    //İşlem kapatmak için var.
                        close();
                        return;
                    }
                    String[] code = line.split("-");

                    if (workerHT.containsKey(code[0])){
                        Worker w = workerHT.get(code[0]);
                        w.flag = false;
                        w.dout.writeUTF(code[1] + " - "+ code[2] + " TL. İşlemi onaylıyor musunuz?");
                        w.dout.flush();


                        int a = w.din.read();  //Telefondan Alınan Geri dönüş.
                        if (a == 49){
                            dout.writeUTF("success");
                        }else{
                            dout.writeUTF("fail");
                        }

                        line = din.readUTF();  //Telefona Son Bildirim Hata yada Fiş.
                        w.dout.writeUTF(line);
                        w.dout.flush();


                        w.flag = true;
                        dout.flush();
                        close();
                    }else{
                        dout.writeUTF("Error3:Worker/Number not Found");
                        dout.flush();
                    }
                } catch (Exception e ) {
                    e.printStackTrace();
                    close();
                    return;
                }
            }


        }
    }  //Sunucu ile iletişim kuran kısım.
}
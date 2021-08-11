import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';


void main() {
  runApp(MaterialApp(
    initialRoute: "/",
    routes: {
      "/" : (context) => MyApp(),
    }));
}

class Client{

  late Socket socket;
  List<String> messageList = [];
  List<String> notificationList = [];

  Future<bool> connect(String no) async{

    bool flag = false;

    try{
      await Socket.connect("12.11.11.2",3335,timeout: Duration(seconds: 5)).then((socket) {
        this.socket = socket;
        socket.write(no);
        socket.listen(
          dataHandler,
          onError: errorHandler,
          onDone: doneHandler,
          cancelOnError: false,
        );
      });
      flag = true;
    }catch(e){
      flag = false;
    }
    return flag;
  }

  void dataHandler(data){
    Utf8Decoder utf8decoder = Utf8Decoder();
    var a = utf8decoder.convert(data,2);
    if(a.startsWith("Error") || a.contains("Receipt")){
      notificationList.add(a);
    }else{
      messageList.add(a);
    }
  }

  void errorHandler(error){
    print(error);
  }

  void doneHandler(){
    socket.destroy();
  }


}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  final myController = TextEditingController();
  final Client client = Client();
  bool connectionFlag = false;
  bool nFlag = true;
  int i = 0;

  Future<dynamic> update({required BuildContext context, int ms = 1000}) async {
    i++;
    if(client.notificationList.length > 0 && nFlag){
      nFlag = false;
      nFlag = await showDialog(context: context, builder: (_) => NotificationAlert(txt: client.notificationList.first, client: client), barrierDismissible: false);
    }
    setState(() {
    });

    Future.delayed(Duration(milliseconds: ms), (){
      update(context: context);
    });


  }

  @override
  void dispose() {
    myController.dispose();
    client.socket.close();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      floatingActionButton: FloatingActionButton(
        onPressed: (){
          Navigator.pushReplacementNamed(context, "/");
        },
        backgroundColor: Colors.grey[900],
        child: Icon(Icons.restore),
      ),
      appBar: AppBar(
        title: Text("Message App"),
        centerTitle: true,
        backgroundColor: Colors.grey[900],
      ),
      body: Column(
        children: [
          if(!connectionFlag)Row(
            children: <Widget>[
              Expanded(child: Center(child: Text("Tel numarası : "))),
              Expanded(
                child: TextField(
                  controller: myController,
                  decoration: InputDecoration(
                    suffixIcon: IconButton(
                      icon: Icon(Icons.send),
                      onPressed: () async {
                        if(myController.text.length == 11){
                          connectionFlag = await client.connect(myController.text);
                          if(connectionFlag){
                            //TODO Something
                            update(context: context,ms: 2000);
                          }
                          else{
                            showDialog(context: context, builder: (_) => (AlertDialog(title: Text("Bağlantı Sağlanamadı."),)));
                          }
                        }
                        else{
                          showDialog(context: context, builder: (_) => (AlertDialog(title: Text("Yanlış Numara girişi"),)));
                        }
                      },
                    ),
                  ),
                ),
                flex: 3
              ),
            ],
          ),
          if(connectionFlag)Text("Seconds Passed: $i"),
          Container(
            child: Column(
              children: client.messageList.map((e) => BuyCard(txt: e, client: client,)).toList(),
            ),
          )
        ],
      )
    );
  }

}


class BuyCard extends StatelessWidget {
  final String txt;
  Client client;

  BuyCard({required this.txt, required this.client});

  @override
  Widget build(BuildContext context) {
    return Card(
      shadowColor: Colors.black,
      color: Colors.grey[300],
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Center(
            child: Text(
              "Bildirim",
              style: TextStyle(
                fontSize: 22,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
          Center(child: Text(txt)),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              IconButton(
                color: Colors.redAccent,
                icon: Icon(Icons.remove),
                onPressed: (){
                  try{
                    client.socket.write("0");
                  }
                  catch(e){
                    print("Bağlantı Başarısız.");
                  }
                  client.messageList.remove(txt);
                },
              ),
              IconButton(
                color: Colors.greenAccent,
                icon: Icon(Icons.add),
                onPressed: (){
                  try{
                    client.socket.write("1");
                  }
                  catch(e){
                    print("Bağlantı Başarısız.");
                  }
                  client.messageList.remove(txt);
                },
              )
            ],
          )
        ],
      ),
    );
  }
}


class NotificationAlert extends StatelessWidget {
  final String txt;
  Client client;

  NotificationAlert({required this.txt, required this.client});

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text("Gelen Bildirim."),
      content: Text(txt),
      actions: [
        ElevatedButton.icon(
          onPressed: (){
            if(client.notificationList.isNotEmpty)
              client.notificationList.removeAt(0);
            Navigator.pop(context, true);
          },
          icon: Icon(Icons.close),
          label: Text("Kapat"))
      ],
    );
  }
}


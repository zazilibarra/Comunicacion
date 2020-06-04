import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:material_design_icons_flutter/material_design_icons_flutter.dart';
import 'package:http/http.dart' as http;

Map<String, String> headers = {"Content-type": "application/json",  "Accept": "application/json",};

void main() {
  runApp(MyApp());
}

error() {}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'ACIOT Smart Room',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primarySwatch: Colors.grey,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MyHomePage(title: 'Centro de control'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);
  final String title;

  @override
  MainMenu createState() => new MainMenu();
}

class MainMenu extends State<MyHomePage> {
  Icon icono;
  String medida, status;
  Timer timer;
  var sensors = new List<Sensor>();

  getFrame() async{

    await API.getSensors().then((response){
      Iterable list = json.decode(response.body);
      sensors = list.map((model) => Sensor.fromJson(model)).toList();
    });

    setState((){});
  }

  @override
  void initState() {
    super.initState();
    timer = Timer.periodic(Duration(milliseconds: 500), (Timer t) => getFrame());
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: ListView.builder(
        itemCount: sensors.length,
          itemBuilder: (context, index) {
            switch(sensors[index].nombre.toLowerCase()) {
              case "ky-001":
                icono = Icon(MdiIcons.thermometer);
                medida = "Temperatura";
                status = sensors[index].value;
                break;
              case "ky-002":
                icono = Icon(MdiIcons.door);
                medida = "Puerta principal";
                status = (sensors[index].value == "1")?"ABIERTA":"CERRADA";
                break;
            }
            return ListTile(
              leading: icono,
              title: Text(status, style: TextStyle(fontWeight: FontWeight.bold),),
              subtitle: Text(medida),
              //trailing: Icon(Icons.arrow_forward_ios),
              //onTap: () {},
            );
          },
      ),
     /*ListView(
        children: <Widget>[
          //Image.network("http://192.168.1.68:8080/?r=" + (new DateTime.now()).toString(), errorBuilder: error(), width: 400, height: 300, gaplessPlayback: true),
          ListTile(
            leading: Icon(MdiIcons.thermometer),
            title: Text('23.4 °C', style: TextStyle(fontWeight: FontWeight.bold),),
            subtitle: Text('Sensor de temperatura'),
            trailing: Icon(Icons.arrow_forward_ios),
            onTap: () {
              //Navigator.push(context, MaterialPageRoute(builder: (context) => PerfilSC()));
            },
          ),
          ListTile(
            leading: Icon(Icons.volume_up),
            title: Text('27.6 db', style: TextStyle(fontWeight: FontWeight.bold),),
            subtitle: Text('Sensor de ruido'),
            trailing: Icon(Icons.arrow_forward_ios),
            onTap: () {
              //Navigator.push(context, MaterialPageRoute(builder: (context) => PerfilSC()));
            },
          ),
          ListTile(
            leading: Icon(MdiIcons.door),
            title: Text('ABIERTA', style: TextStyle(fontWeight: FontWeight.bold),),
            subtitle: Text('Puerta principal'),
            trailing: Icon(Icons.arrow_forward_ios),
            onTap: () {
              //Navigator.push(context, MaterialPageRoute(builder: (context) => PerfilSC()));
            },
          ),
          ListTile(
            leading: Icon(MdiIcons.windowOpen),
            title: Text('CERRADA', style: TextStyle(fontWeight: FontWeight.bold),),
            subtitle: Text('Ventana sala'),
            trailing: Icon(Icons.arrow_forward_ios),
            onTap: () {
              //Navigator.push(context, MaterialPageRoute(builder: (context) => PerfilSC()));
            },
          ),
          ListTile(
            leading: Icon(Icons.whatshot),
            title: Text('SEGURO', style: TextStyle(fontWeight: FontWeight.bold),),
            subtitle: Text('Detector de calor'),
            trailing: Icon(Icons.arrow_forward_ios),
            onTap: () {
              //Navigator.push(context, MaterialPageRoute(builder: (context) => PerfilSC()));
            },
          ),
        ],
      ),*/
    );
  }

  @override
  void dispose() {
    timer?.cancel();
    super.dispose();
  }
}

class API {
  static Future getSensors() {
    var url = "http://192.168.1.66:8080/getinfo";
    return http.get(url, headers: headers);
  }
}

class Sensor {
  String id;
  String nombre;
  String value;

  Sensor(String id, String nombre, String value) {
    this.id = id;
    this.nombre = nombre;
    this.value = value;
  }

  Sensor.fromJson(Map json):
    id = json['id'],
    nombre = json['nombre'],
    value = json['value'];

  Map toJson() {
    return {'id': id, 'nombre': nombre, 'value': value};
  }

}

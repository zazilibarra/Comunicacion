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

//Se crea una instancia de una pantalla con estado la cual servira como padre para nuestra pantalla principal.
class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);
  final String title;

  @override
  MainMenu createState() => new MainMenu();
}

//Esta pantalla es donde se muestra toda la informacion obtenida desde el servidor, es capaz de cambiar su estado lo que significa
//que la informacion va a ser actualizada constantemente
class MainMenu extends State<MyHomePage> {
  Icon icono;
  String medida, status;
  Timer timer;
  var sensors = new List<Sensor>();

  //Obtiene la informacion del servidor a traves de APIs.
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

  //Este constructor toma toda la información que se obtuvo del servidor y le da estructura en la pantalla.
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
              case "ky-033":
                icono = Icon(MdiIcons.door);
                medida = "Puerta principal";
                status = (sensors[index].value == "1")?"ABIERTA":"CERRADA";
                break;
              case "ky-026":
                icono = Icon(MdiIcons.door);
                medida = "Estufa";
                status = (sensors[index].value == "1")?"ENCENDIDA":"APAGADA";
                break;
              case "ky-036":
                icono = Icon(MdiIcons.door);
                medida = "Llaves";
                status = (sensors[index].value == "1")?"EN SU LUGAR":"FUERA DE LUGAR";
                break;
              case "ky-037":
                icono = Icon(MdiIcons.door);
                medida = "SONIDO";
                status = (sensors[index].value == "1")?"FUERTE":"EN RANGO";
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
    );
  }

  @override
  void dispose() {
    timer?.cancel();
    super.dispose();
  }
}

class API {
  //Esta funcion se comunica con el URL designado para obtener la información de los sensores que contiene el servidor
  static Future getSensors() {
    var url = "http://192.168.1.66:8080/getinfo";
    return http.get(url, headers: headers);
  }
}

//Clase sensor que se utilizara para obtener una instancia con la cual se podra obtener y manipular la informacion para mostrarla
//en la pantalla de la aplicación
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

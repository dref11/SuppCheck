import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:supp_check_app/takePicture.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        title: 'Flutter login UI',
        theme: ThemeData(
          primaryColor: Color(0xFFfb7756),
        ),
        home: MyHomePage(title: 'Flutter Login'));
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);
  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  Widget build(BuildContext context) {
    Color primary = Theme.of(context).primaryColor;
    void initState() {
      super.initState();
    }

    return Scaffold(
        body: Column(children: <Widget>[
          Padding(
            padding: const EdgeInsets.all(0.0),
              child: Container(
                height: MediaQuery.of(context).size.height * 0.4,
                decoration: BoxDecoration(color: Theme.of(context).primaryColor)
              )
          ),
          CupertinoButton(
              padding: EdgeInsets.fromLTRB(20.0, 40, 20, 0),
              child: Container(
              height: 150,
              width: 150,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(30.0),
                  color: Color(0xFF1ac0c6)
              )
            ),
          onPressed: () => Navigator.push(
            context,
            MaterialPageRoute(
              builder: (BuildContext context) => TakePictureScreen()
            )
          )
          )
        ])
    );
  }
}

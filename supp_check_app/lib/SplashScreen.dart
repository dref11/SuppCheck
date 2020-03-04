import 'package:flutter/material.dart';
import 'dart:io';
import 'dart:async';

import 'Home.dart';

class SplashScreenPage extends StatefulWidget{
  @override
  _SplashScreenPageState createState() => _SplashScreenPageState();
}

class _SplashScreenPageState extends State<SplashScreenPage>{
  @override
  void initState(){
    super.initState();
    startSplashScreen();
  }

  startSplashScreen() async {
    var duration = const Duration(seconds: 5);
    return Timer(duration, (){
      Navigator.of(context).pushReplacement(
          MaterialPageRoute(builder: (_){
            return HomePage();
          })
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Center(
        child: Column(
          children: <Widget>[
            Padding(
              padding: EdgeInsets.fromLTRB(0, 200, 0, 20),
              child: Image.asset(
                'assets/images/logo.JPG',
                width: 250.0,
                height: 150.0,
              ),
            ),
            Padding(
                padding: EdgeInsets.fromLTRB(0, 30, 0, 10),
                child: Text(
                  'SuppCheck',
                  style: TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 45.0
                  )
                )
            ),
            Padding(
              padding: EdgeInsets.fromLTRB(0, 20, 0, 0),
              child: Text(
                'Snap | Eat | Healthy',
                style: TextStyle(
                    fontSize: 20.0
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
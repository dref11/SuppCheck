import 'package:flutter/material.dart';
import 'dart:io';
import 'dart:async';


import 'package:camera/camera.dart';
import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';


class openCamera extends StatefulWidget {

  final CameraDescription camera;
  openCamera({@required this.camera});
  
  @override
  _openCameraState createState() => _openCameraState();
}

class _openCameraState extends State<openCamera> {
  CameraController _cameraController;
  Future <void> _initializeCameraControllerFuture;
  
  @override
  void initState(){
    super.initState();
    
    _cameraController =
        CameraController(widget.camera, ResolutionPreset.medium);
    
    _initializeCameraControllerFuture = _cameraController.initialize();
  }
  
  void _takePicture(BuildContext context) async {
    try{
      await _initializeCameraControllerFuture;
      
      final path =
          join((await getTemporaryDirectory()).path, '{$DateTime.now()}.png');
      
      await _cameraController.takePicture(path);

      Navigator.pop(context,path);
    } catch (e){
      print(e);
    }
  }

  @override
  Widget build(BuildContext context){
    return Stack(children: <Widget>[
      FutureBuilder(
        future: _initializeCameraControllerFuture,
        builder: (context, snapshot){
          if(snapshot.connectionState == ConnectionState.done){
            return CameraPreview(_cameraController);
          } else{
            return Center(child: CircularProgressIndicator());
          }
        },
      ),
      SafeArea(
        child: Align(
          alignment: Alignment.bottomRight,
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: FloatingActionButton(
              backgroundColor: Colors.black,
              child: Icon(Icons.camera),
              onPressed: (){
                _takePicture(context);
              },
            ),
          ),
        ),
      )
    ]);
  }

  @override
  void dispose(){
    _cameraController.dispose();
    super.dispose();
  }


}
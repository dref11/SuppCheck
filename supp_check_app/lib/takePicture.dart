import 'dart:async';
import 'dart:io';


import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';

Future<void> main() async{
  WidgetsFlutterBinding.ensureInitialized();

  final cameras = await availableCameras();
  final firstCamera = cameras.first;

  runApp(
    MaterialApp(
      theme: ThemeData.light(),
      home: TakePictureScreen(
        camera: firstCamera,
      ),
    ),
  );
}


class TakePictureScreen extends StatefulWidget{
  final CameraDescription camera;

  const TakePictureScreen({
    //Key key,
    @required this.camera});
  //}) : super(key: key);

  @override
  TakePictureScreenState createState() => TakePictureScreenState();

}

class TakePictureScreenState extends State<TakePictureScreen>{
  CameraController _controller;
  Future<void> _initialzeControllerFuture;

  @override
  void initState(){
    super.initState();

    _controller = CameraController(
      widget.camera,
      ResolutionPreset.medium,
    );
    _initialzeControllerFuture = _controller.initialize();
  }

  @override
  void dispose(){
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context){
    return Scaffold(
      appBar: AppBar(
        title: Text('Take A Picture'),
        backgroundColor: Theme.of(context).primaryColor
      ),
      body: FutureBuilder<void>(
        future: _initialzeControllerFuture,
        builder: (context, snapshot){
          //if(snapshot.connectionState == ConnectionState.done){
            return CameraPreview(_controller);
          //} else {
          //  return Center(child: CircularProgressIndicator());

        },
      ),
      floatingActionButton: FloatingActionButton(
        child: Icon(Icons.camera_alt),
        onPressed: () async{
          try{
            await _initialzeControllerFuture;
            final path = join(
            (await getTemporaryDirectory()).path,
              '${DateTime.now()}.png',
            );
            await _controller.takePicture(path);
            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => DisplayPictureScreen(imagePath: path),
              ),
            );
          } catch(e){
            print(e);
          }
        },
      ),
    );
  }
}

class DisplayPictureScreen extends StatelessWidget{
  final String imagePath;
  const DisplayPictureScreen({Key key, this.imagePath}): super(key:key);

  @override
  Widget build(BuildContext context){
    return Scaffold(
      appBar: AppBar(title: Text('Display the picture')),
      body: Image.file(File(imagePath)),
    );
  }
}
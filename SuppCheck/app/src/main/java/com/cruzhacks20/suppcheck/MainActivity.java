package com.cruzhacks20.suppcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    ImageButton CamButton, UploadButton;

    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1889;

    static String selectedImagePath;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int MY_EXTERNAL_PERMISSION_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CamButton = findViewById(R.id.CamButton);
        checkForPermissions();
    }

    public void openCamera(View v){
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }
        else
        {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    //onClick method for selecting the path of image to uplaod
    public void uploadPhoto(View v){
        //Create a new intent to select an image from storage to send to flask server
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        //Handle result of choosing photo from gallery
        if(requestCode== GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null){

            try{
                //Get the Uri results
                Uri uri = data.getData();
                selectedImagePath = getPath(getApplicationContext(), uri);
                //startActivity(new Intent(MainActivity.this,confirmUploadActivity.class));
            }catch(Exception e){
            }
        }
    }

    //onClick method for connect to server button
    public void connectServer(View v){

        Toast.makeText(this,"Connectiong",Toast.LENGTH_SHORT).show();

        String postUrl;
        postUrl = "http://cruz-hacks-2020-265505.appspot.com/upload-file";
        //postUrl = "http://127.0.0.1:8080/upload-file";


        //Construct the post request
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        //Read the Bitmap by the file path
        Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte [] byteArray = stream.toByteArray();

        //Get the title of the image
        String tempTitle = "photo";

        //Build the request body with the selected image
        RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image",tempTitle+".png",RequestBody.create(MediaType.parse("image/*png"), byteArray))
                .build();

        //Post the request to url
        postRequest(postUrl, postBodyImage);

        //Toast that post request is being made to the Flask server to correct path
        Toast.makeText(this,"Uploading Photo",Toast.LENGTH_SHORT).show();
    }

    //Send post request to the flask URL server
    void postRequest(String postUrl, RequestBody postBody){

        //Clinet object to deal with http requests
        OkHttpClient client = new OkHttpClient();

        //Post Request
        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        //Post request using a callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Cancel upon failure
                call.cancel();

                //Access the TextView inside UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Failed to connect to server.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                //If callback successful update the textview in the UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try{

                            //Start the rendering activity
                            startActivity(new Intent(MainActivity.this,ViewInfoActivity.class));

                            //Close activity
                            finish();

                        }catch(Exception e){
                        }
                    }
                });
            }
        });
    }









    //Helper function to get the path of selected image
    public static String getPath(final Context context, final Uri uri){

        //Check that the api level of the device is >= API 20
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        //Document provider
        if(isKitKat && DocumentsContract.isDocumentUri(context, uri)){

            //If the external storage provider
            if(isExternalStorageDocument(uri)){

                //Parse to get the document id of the chosen photo
                final String docId = DocumentsContract.getDocumentId(uri);
                final String [] split = docId.split(":");
                final String type = split[0];

                if("primary".equalsIgnoreCase(type)){
                    return Environment.getExternalStorageDirectory() +"/" + split[1];
                }

                //If the downloads provider
            } else if (isDownloadsDocuments(uri)){

                //Parse to get the document id of the chosen document
                final String id =DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);

                //if the media provider
            } else if(isMediaDocument(uri)){

                final String docId = DocumentsContract.getDocumentId(uri);
                final String [] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;

                if("image".equals(type)){
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if("video".equals(type)){
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if("audio".equals(type)){
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }

            // If MediaStore and general
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            return uri.getPath();

            //File
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            return uri.getPath();
        }
        return null;
    }

    //Helper functions to determine the uri type

    public static boolean isExternalStorageDocument(Uri uri){
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocuments(Uri uri){
        return "com.android.provicers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri){
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    //Returns the data column of selection argument
    public static String getDataColumn(Context context, Uri uri, String selection, String [] selectionArgs){

        Cursor cursor = null;
        final String column = "_data";
        final String [] projection = {
                column
        };

        try{
            //Query the selection with the cursor object
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

            //Check if values exist with the given query
            if(cursor != null && cursor.moveToFirst()){
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally{
            //Close the cursor after making the query to get the data
            if(cursor != null){
                cursor.close();
            }
        }
        return null;
    }

    void checkForPermissions(){

        //Check for the current activity if permission is granted for external read
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            //Permission not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                //Show rationale if permission is not granted
            } else{

                //Request the permission during runtime
                ActivityCompat.requestPermissions(this,
                        new String []{Manifest.permission.READ_EXTERNAL_STORAGE},
                        GALLERY_REQUEST);
            }

        }else{
            //Permission is already granted
        }

        //Check for the current activity if permisison is granted for external write
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    GALLERY_REQUEST);

        }
    }

}

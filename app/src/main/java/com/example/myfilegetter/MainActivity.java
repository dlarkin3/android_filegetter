package com.example.myfilegetter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
public class MainActivity extends AppCompatActivity {
    Context context;
    Activity activity;
    CoordinatorLayout coordinatorLayout;
    Button button;
    ProgressDialog mProgressDialog;
    ImageView mImageView;
    URL url;
    AsyncTask mMyTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        activity = MainActivity.this;
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        button = findViewById(R.id.btnDownload);
        mImageView = findViewById(R.id.imageView);
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle("AsyncTask");
        mProgressDialog.setMessage("Please wait, we are downloading your image file...");
        button.setOnClickListener(view -> mMyTask = new DownloadTask().execute(stringToURL()));
    }
    private class DownloadTask extends AsyncTask<URL,Void,Bitmap>{
        protected void onPreExecute(){
            mProgressDialog.show();
        }
        protected Bitmap doInBackground(URL...urls){
            URL url = urls[0];
            HttpURLConnection connection;
            try{
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();

                File myFile = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), "reds4.jpeg");
                if(myFile.exists()){
                    boolean deleted=myFile.delete();
                }
                myFile.createNewFile();
                copyInputStreamToFile(inputStream,myFile);

                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                return BitmapFactory.decodeStream(bufferedInputStream);
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        // Copy an InputStream to a File.
//
        private void copyInputStreamToFile(InputStream in, File file) {
            OutputStream out = null;

            try {
                out = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len;
                while((len=in.read(buf))>0){
                    out.write(buf,0,len);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                // Ensure that the InputStreams are closed even if there's an exception.
                try {
                    if ( out != null ) {
                        out.close();
                    }

                    // If you want to close the "in" InputStream yourself then remove this
                    // from here but ensure that you close it yourself eventually.
                    in.close();
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }


        // When all async task done
        protected void onPostExecute(Bitmap result){
            // Hide the progress dialog
            mProgressDialog.dismiss();
            if(result!=null){
                mImageView.setImageBitmap(result);
            } else {
                // Notify user that an error occurred while downloading image
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }

        }
    }
    protected URL stringToURL() {
        try {
            url = new URL("http://10.10.2.37:8000/reds2.jpeg");
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
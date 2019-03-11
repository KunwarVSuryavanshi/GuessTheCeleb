package com.example.guesstheceleb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebName = new ArrayList<String>();
    ArrayList<String> imageLink = new ArrayList<String>();
    ImageView celebImageView;
    int chosenCeleb = 0;

    public class ImageTask extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream is = urlConnection.getInputStream();
                Bitmap mybitmap = BitmapFactory.decodeStream(is);
                return mybitmap;

            } catch (IOException e) {

                e.printStackTrace();

            } catch (Exception e) {

                e.printStackTrace();

            }

            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... args) {

            URL url;
            String result = "";
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(args[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }

            return result;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        celebImageView = (ImageView)findViewById(R.id.celebImage);

        DownloadTask task = new DownloadTask();
        String result = null;

        try {

            result =task.execute("http://www.posh24.se/kandisar").get();
            String[] resultSplit = result.split(" <div class=\"col-xs-12 col-sm-6 col-md-4\">");

            Pattern p  = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(resultSplit[0]);

            while(m.find())
            {
                    imageLink.add(m.group(1));
            }

            p  = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(resultSplit[0]);

            while(m.find())
            {
                celebName.add(m.group(1));
            }

            Random random = new Random();
            chosenCeleb = random.nextInt(imageLink.size());
            ImageTask downld = new ImageTask();
            Bitmap celeImage;
            celeImage = downld.execute(imageLink.get(chosenCeleb)).get();
            celebImageView.setImageBitmap(celeImage);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
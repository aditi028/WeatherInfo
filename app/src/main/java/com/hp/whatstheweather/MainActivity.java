package com.hp.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultTextView;

    public void findWeather(View view){

        Log.i("cityName",cityName.getText().toString());

        // to hide keyboard.
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);

        try {
            // suppose city name contains space, this will convert it into url format...
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(),"UTF-8");

            DownloadTask task = new DownloadTask();
            task.execute("https://samples.openweathermap.org/data/2.5/weather?q=" + encodedCityName);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



    }

    public class DownloadTask extends AsyncTask<String,Void,String> {



        @Override
        protected String doInBackground(String... params){

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                url = new URL(params[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();


                }
                return result;
            } catch (Exception e) {

                e.printStackTrace();

            }

            return "failed";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.i("Contents of url",result);

            JSONObject jsonObject = null;
            try {
                String message = "";
                jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather Content",weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);
                for(int i=0;i<arr.length();i++){
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if(main != "" && description != ""){

                        message += main + ": " + description +"\r\n";
                    }


                    Log.i("main",jsonPart.getString("main"));
                    Log.i("Description",jsonPart.getString("description"));
                }

                if(message != ""){
                    resultTextView.setText(message);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Could not find weather", Toast.LENGTH_LONG).show();
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       cityName = (EditText) findViewById(R.id.cityName);
       resultTextView = (TextView) findViewById(R.id.resultTextView);
    }
}

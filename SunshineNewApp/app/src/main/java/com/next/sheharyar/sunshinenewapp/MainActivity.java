package com.next.sheharyar.sunshinenewapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.next.sheharyar.sunshinenewapp.data.SunshinePreferences;
import com.next.sheharyar.sunshinenewapp.utilities.NetworkUtils;
import com.next.sheharyar.sunshinenewapp.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView mWeatherTextView, mDisplayErrorMessage;
    ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWeatherTextView = (TextView)findViewById(R.id.tv_weather_data);
        mDisplayErrorMessage = (TextView)findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar)findViewById(R.id.pb_loading_indicattor);

        loadWeatherData();
    }

    private void loadWeatherData(){
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new FetchWeaatherTask().execute(location);
    }

    private void showWeatherDataView(){
        mDisplayErrorMessage.setVisibility(View.INVISIBLE);
        mWeatherTextView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mDisplayErrorMessage.setVisibility(View.VISIBLE);
        mWeatherTextView.setVisibility(View.INVISIBLE);
    }

    public class FetchWeaatherTask extends AsyncTask<String, Void, String[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected String[] doInBackground(String... params) {
            /* if there is no zip code there's nothing to lookup ..*/

            if (params.length == 0){
                return null;
            }

            String location = params[0];
            URL weatherRequestUrl = NetworkUtils.buildUrl(location);

            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(weatherRequestUrl);

                String[] simpleJsonWeatherData = OpenWeatherJsonUtils
                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

                return simpleJsonWeatherData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (weatherData != null){

               showWeatherDataView();
                 /*
                 * Iterate through the array and append the Strings to the TextView. The reason why we add
                 * the "\n\n\n" after the String is to give visual separation between each String in the
                 * TextView. Later, we'll learn about a better way to display lists of data.
                 */
                 for (String weatherString : weatherData){
                     mWeatherTextView.append((weatherString) + "\n\n\n");
                 }
            } else {
                showErrorMessage();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.action_refresh){
            mWeatherTextView.setText("");
            loadWeatherData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

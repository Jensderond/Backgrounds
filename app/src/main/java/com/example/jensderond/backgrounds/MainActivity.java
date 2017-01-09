package com.example.jensderond.backgrounds;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ResourceBundle;

import static android.R.attr.angle;

public class MainActivity extends Activity implements Response.Listener, Response.ErrorListener, SensorEventListener{

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    Button start;
    TextView textView;
    RequestQueue requestQueue;
    ImageView imageView;
    ImageView profile_image;
    HorizontalScrollView scrollView;
    ProgressBar progressBar;

    private SensorManager mSensorManager;
    private Sensor gyroscope;
    private Sensor magnetometer;
    public static int x;
    public static int y;
    public static int z;
    int newx = 0;
    int newy = 0;

    WebView webView;
    Activity activity = this;
    private static final String CLIENT_ID = "?client_id=YOUR_CLIENT_TOKEN_HERE";
    private static final String API = "https://api.unsplash.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        start = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.tview1);
        requestQueue = Volley.newRequestQueue(this);
        imageView = (ImageView) findViewById(R.id.imageView);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        scrollView = (HorizontalScrollView) findViewById(R.id.scrollview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
//        EXAMPLE CODE FOR A WEBVIEW
//        webView = (WebView) findViewById(R.id.wbview);
//        webView.getSettings().setJavaScriptEnabled(false);
//        webView.setWebViewClient(new WebViewClient());
//        webView.loadUrl(user.getProfile_link());

//        SENSOR TEST
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);



        trustAllSSL();

    }



    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            x = (int) Math.pow(sensorEvent.values[0], 2);
            y = (int) Math.pow(sensorEvent.values[1], 2);
            z = (int) Math.pow(sensorEvent.values[2], 2);

            scrollView.smoothScrollTo(z, 100);

//            Log.i("xAxis", String.valueOf(x));
//            Log.i("yAxis", String.valueOf(y));
            Log.i("zAxis", String.valueOf(z));

        }
    }


    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onResume() {
        super.onResume();
//        mSensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }



    protected void onStart() {
        super.onStart();

        final MyJSONObjRequest request = new MyJSONObjRequest(Request.Method.GET, "https://api.unsplash.com/photos/random/?client_id=a5b669605224fb7f9b3d24ea23d600c298a7377d308cd5b96ecca4ebef3f0726",new JSONObject(), this,this);
        request.setTag("randomRequestTAG");

        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                progressBar.setVisibility(View.VISIBLE);
                requestQueue.add(request);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if( requestQueue != null )
            requestQueue.cancelAll("randomRequestTAG");
    }

    /***
     * Catches the error of the API call
     * @param error
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        Log.i("",error.getMessage());
    }

    /***
     * Catches the response of the API call
     * @param response
     */
    @Override
    public void onResponse(Object response) {
        try {
            JSONObject objectUser = ((JSONObject) response).getJSONObject("user");
            User user = new User(objectUser);

            String color = ((JSONObject) response).getString("color");
            imageView.setBackgroundColor(Color.parseColor(color));

            JSONObject objectUrl = ((JSONObject) response).getJSONObject("urls");
            String imageUrl = objectUrl.getString("full");

            Log.i("User name: ",user.getName());
            Log.i("Profile link: ",user.getProfile_link());
            Log.i("Color: ",color);
            Log.i("Image url: ",imageUrl);
            textView.setText(user.getName());
            DownloadImage(imageView, imageUrl, user.getName(), user.getId());
            DownloadImage(profile_image, user.getProfile_image(), user.getName(),user.getId());

        }
        catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    public void DownloadImage(final ImageView view, String url, final String yourTitle, final String yourDescription) {

        ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                view.setImageBitmap(response);
                if (response.getWidth() > 200) {
                    progressBar.setVisibility(View.INVISIBLE);
                    scrollView.scrollTo(response.getWidth() / 2, 0);
                }
            }
        }, 0, 0, null, null);
        requestQueue.add(ir);
    }

    public void trustAllSSL(){
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }
}

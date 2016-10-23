/*
This activity shows the Latitude and Longitude of the mobile
 */

package io.particle.cloudsdk.example_app;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.kaazing.gateway.client.impl.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v7.appcompat.R.id.time;

public class ValueActivity extends AppCompatActivity {

    private static final String ARG_VALUE = "ARG_VALUE";
    private static final String ARG_DEVICEID = "ARG_DEVICEID";
    private static String mobileValue = "";
    private TextView tv;
    Intent lcationService;
    Intent intent;
    PendingIntent pendingIntent;
    LocationManager locationManager;
    Location oldLocation;
    String latitude="";
    String longitude = "";
    TrackGPS gps;
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int INITIAL_REQUEST=1337;
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value);

        tv = (TextView) findViewById(R.id.value);
        tv.setText(String.valueOf(getIntent().getIntExtra(ARG_VALUE, 0)));
        intent = new Intent(ValueActivity.this, LocationBroadcast.class);
        pendingIntent = PendingIntent.getBroadcast(ValueActivity.this, 0, intent,0);

        requestPermissions(LOCATION_PERMS,LOCATION_REQUEST);

        Async.executeAsync(ParticleCloud.get(ValueActivity.this), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                ParticleDevice device = ParticleCloud.getDevice(getIntent().getStringExtra(ARG_DEVICEID));
                Object variable;
                try {
                    variable = device.getVariable("mobile");
                    mobileValue = variable.toString();
                } catch (Exception e) {
                    Toaster.l(ValueActivity.this, e.getMessage());
                    variable = -1;
                }
                return variable;
            }

            @Override
            public void onSuccess(Object i) { // this goes on the main thread
                gps = new TrackGPS(ValueActivity.this);

                if(gps.canGetLocation()){
                    longitude = Double.toString(gps.getLongitude());
                    latitude = Double.toString(gps .getLatitude());

                    Toast.makeText(getApplicationContext(),"Longitude:"+longitude+"\nLatitude:"+latitude,Toast.LENGTH_SHORT).show();
                }
                else
                {
                    gps.showSettingsAlert();
                }
                tv.setText(mobileValue.toString() + "/" + latitude + "/" + longitude);
                if(Double.parseDouble(mobileValue) == 1.0) {
                    new HttpAsyncTask().execute("https://api.spark.io/v1/devices/240038000347353138383138/getlocation?access_token=30fb4b3a089d0c9b3460d6351ffd9c6fc0193357");
                }
                setupAlarm(10000);
                Toast.makeText(getApplicationContext(),mobileValue.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(ParticleCloudException e) {
                e.printStackTrace();
            }
        });

//        findViewById(R.id.refresh_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //...
//                // Do network work on background thread
//                Async.executeAsync(ParticleCloud.get(ValueActivity.this), new Async.ApiWork<ParticleCloud, Object>() {
//                    @Override
//                    public Object callApi(ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
//                        ParticleDevice device = ParticleCloud.getDevice(getIntent().getStringExtra(ARG_DEVICEID));
//                        Object variable;
//                        try {
//                            variable = device.getVariable("mobile");
//                            mobileValue = variable.toString();
//                        } catch (ParticleDevice.VariableDoesNotExistException e) {
//                            Toaster.l(ValueActivity.this, e.getMessage());
//                            variable = -1;
//                        }
//                        return variable;
//                    }
//
//
//                    @Override
//                    public void onSuccess(Object i) { // this goes on the main thread
//                        tv.setText(mobileValue.toString());
//                    }
//
//
//
//                    @Override
//                    public void onFailure(ParticleCloudException e) {
//                        e.printStackTrace();
//                    }
//                });
//            }
//        });
    }

    public static Intent buildIntent(Context ctx, String value, String deviceid) {
        Intent intent = new Intent(ctx, ValueActivity.class);
        intent.putExtra(ARG_VALUE, value);
        mobileValue = value;
        intent.putExtra(ARG_DEVICEID, deviceid);
        return intent;
    }

    private void setupAlarm(int seconds) {

        Long time = new GregorianCalendar().getTimeInMillis()+ (seconds);
        Intent intentAlarm = new Intent(this, LocationBroadcast.class);
        Log.d("location", "Setup the alarm");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

        // Finish the currently running activity
        //ValueActivity.this.finish();
        Toast.makeText(this,mobileValue.toString(),Toast.LENGTH_SHORT).show();
    }

    private class HttpAsyncTask extends AsyncTask<String, String, String> {

        Boolean errorThrown = true;
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0]);
        }


//        signUPcreateAccountBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
//                startActivity(loginIntent);
//            }
//        });

        public String POST(String url) {
            InputStream inputStream = null;
            String result = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("location", "Android:" + latitude+"/"+longitude);
 //               jsonObject.put("latitude", "11.2");
//                jsonObject.put("Password", "");
//                jsonObject.put("ConfirmPassword", "");

                json = jsonObject.toString();
                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);

                httpPost.setHeader("accept", "json");
                httpPost.setHeader("Content-type", "application/json");

                org.apache.http.HttpResponse httpResponse = httpclient.execute(httpPost);

                inputStream = httpResponse.getEntity().getContent();
                result = convertInputStreamToString(inputStream);
                // convert inputstream to string
                if (result.contains("invalid") == true) {
                    errorThrown = true;
                    Log.d("IOT", "result" + result);

                } else
                    errorThrown = false;
                Log.d("IOT", "inputStream result" + result);

            } catch (Exception e) {
                Log.d("json", "e.getLocalizedMessage()" + e.getLocalizedMessage());
            }

            //  return result
            return result;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if(errorThrown == false) {
                Toast.makeText(getBaseContext(), "Post Successful", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();
            }
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }

    }

}


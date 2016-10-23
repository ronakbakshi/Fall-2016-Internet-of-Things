/*
This activity is used for login to Particle cloud
 */

package io.particle.cloudsdk.example_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

public class LoginActivity extends AppCompatActivity {
    String mobileValue = "";
    AutoLogin autoLogin;
    String savedUsername = "";
    String savedPassword = "";
    String email="";
    String password="";

    @Override
    protected void onStop() {
        autoLogin.setUserName(getApplicationContext(),email);
        autoLogin.setPassword(getApplicationContext(),password);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        autoLogin.setUserName(getApplicationContext(),email);
        autoLogin.setPassword(getApplicationContext(),password);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        autoLogin = new AutoLogin();

        if(autoLogin.getUserName(LoginActivity.this).length() > 0)
        {
            savedUsername = autoLogin.getUserName(LoginActivity.this);
            savedPassword = autoLogin.getPassword(LoginActivity.this);
            AsyncTask task = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    try {

                            ParticleCloud.get(LoginActivity.this).logIn(savedUsername, savedPassword);
                        } catch (final ParticleCloudException e) {
                        Runnable mainThread = new Runnable() {
                            @Override
                            public void run() {
                                Toaster.l(LoginActivity.this, e.getBestMessage());
                                e.printStackTrace();
                                Log.d("info", e.getBestMessage());
//                                            Log.d("info", e.getCause().toString());
                            }
                        };
                        runOnUiThread(mainThread);

                    }
                    return null;
                }

            };

            Async.executeAsync(ParticleCloud.get(getApplicationContext()), new Async.ApiWork<ParticleCloud, Object>() {

                private ParticleDevice mDevice;

                @Override
                public Object callApi(ParticleCloud sparkCloud) throws ParticleCloudException, IOException {
                    sparkCloud.logIn(savedUsername, savedPassword);
                    sparkCloud.getDevices();
                    mDevice = sparkCloud.getDevice("240038000347353138383138");
                    Object obj;

                    try {
                        obj = mDevice.getVariable("mobile");
                        mobileValue = obj.toString();
                        Log.d("BANANA", "analogvalue: " + obj);
                    } catch (ParticleDevice.VariableDoesNotExistException e) {
                        Toaster.s(LoginActivity.this, "Error reading variable");
                        obj = -1;
                    }

                    return -1;

                }

                @Override
                public void onSuccess(Object value) {
                    Toaster.l(LoginActivity.this, "Logged in");
                    Intent intent = ValueActivity.buildIntent(LoginActivity.this,mobileValue , mDevice.getID());
                    startActivity(intent);
                }

                @Override
                public void onFailure(ParticleCloudException e) {
                    Toaster.l(LoginActivity.this, e.getBestMessage());
                    e.printStackTrace();
                    Log.d("info", e.getBestMessage());
                }
            });
        }

        findViewById(R.id.login_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        email = ((EditText) findViewById(R.id.email)).getText().toString();
                        password = ((EditText) findViewById(R.id.password)).getText().toString();

                        // Don't:
                        AsyncTask task = new AsyncTask() {
                            @Override
                            protected Object doInBackground(Object[] params) {
                                try {
                                    if(autoLogin.getUserName(LoginActivity.this).length() == 0)
                                    {
                                        ParticleCloud.get(LoginActivity.this).logIn(email, password);
                                        autoLogin.setUserName(getApplicationContext(),email);
                                        autoLogin.setPassword(getApplicationContext(),password);
                                    }
                                    else
                                    {
                                        String savedUsername = autoLogin.getUserName(LoginActivity.this);
                                        String savedPassword = autoLogin.getPassword(LoginActivity.this);
                                        ParticleCloud.get(LoginActivity.this).logIn(savedUsername, savedPassword);
                                    }
                                    //ParticleCloud.get(LoginActivity.this).logIn(email, password);

                                } catch (final ParticleCloudException e) {
                                    Runnable mainThread = new Runnable() {
                                        @Override
                                        public void run() {
                                            Toaster.l(LoginActivity.this, e.getBestMessage());
                                            e.printStackTrace();
                                            Log.d("info", e.getBestMessage());
//                                            Log.d("info", e.getCause().toString());
                                        }
                                    };
                                    runOnUiThread(mainThread);

                                }

                                return null;
                            }

                        };
//                        task.execute();

                        //-------

                        // DO!:
                        Async.executeAsync(ParticleCloud.get(v.getContext()), new Async.ApiWork<ParticleCloud, Object>() {

                            private ParticleDevice mDevice;

                            @Override
                            public Object callApi(ParticleCloud sparkCloud) throws ParticleCloudException, IOException {
                                sparkCloud.logIn(email, password);
                                sparkCloud.getDevices();
                                mDevice = sparkCloud.getDevice("240038000347353138383138");
                                Object obj;

                                try {
                                    obj = mDevice.getVariable("mobile");
                                    mobileValue = obj.toString();
                                    Log.d("BANANA", "analogvalue: " + obj);
                                } catch (ParticleDevice.VariableDoesNotExistException e) {
                                    Toaster.s(LoginActivity.this, "Error reading variable");
                                    obj = -1;
                                }

                                return -1;

                            }

                            @Override
                            public void onSuccess(Object value) {
                                Toaster.l(LoginActivity.this, "Logged in");
                                Intent intent = ValueActivity.buildIntent(LoginActivity.this,mobileValue , mDevice.getID());
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(ParticleCloudException e) {
                                Toaster.l(LoginActivity.this, e.getBestMessage());
                                e.printStackTrace();
                                Log.d("info", e.getBestMessage());
                            }
                        });


                    }
                }

        );
    }

}

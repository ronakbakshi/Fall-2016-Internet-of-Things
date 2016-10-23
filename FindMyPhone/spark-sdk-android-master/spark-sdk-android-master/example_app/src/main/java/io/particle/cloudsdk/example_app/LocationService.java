package io.particle.cloudsdk.example_app;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LocationService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "io.particle.cloudsdk.example_app.action.FOO";
    private static final String ACTION_BAZ = "io.particle.cloudsdk.example_app.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "io.particle.cloudsdk.example_app.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "io.particle.cloudsdk.example_app.extra.PARAM2";

    private static final String ARG_VALUE = "ARG_VALUE";
    private static final String ARG_DEVICEID = "ARG_DEVICEID";
    private static String mobileValue = "";

    public LocationService() {
        super("LocationService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
        handleActionFoo(param1, param2);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String param1 ="0";
            final String param2 = "240038000347353138383138";
            handleActionFoo(param1, param2);
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(final String param1, final String param2) {
        Async.executeAsync(ParticleCloud.get(getApplicationContext()), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                ParticleDevice device = ParticleCloud.getDevice(param2);
                Object variable;
                variable = param1;
                mobileValue = variable.toString();
                return variable;
            }

            @Override
            public void onSuccess(Object i) { // this goes on the main thread
                mobileValue = i.toString();
                Toast.makeText(getApplicationContext(),mobileValue,Toast.LENGTH_LONG).show();
                // tv.setText(mobileValue.toString());
            }

            @Override
            public void onFailure(ParticleCloudException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static Intent buildIntent(Context ctx, String value, String deviceid) {
        Intent intent = new Intent(ctx, ValueActivity.class);
        intent.putExtra(ARG_VALUE, value);
        mobileValue = value;
        intent.putExtra(ARG_DEVICEID, deviceid);

        return intent;
    }
}

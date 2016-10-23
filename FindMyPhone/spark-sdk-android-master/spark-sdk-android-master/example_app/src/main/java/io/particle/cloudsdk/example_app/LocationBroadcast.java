package io.particle.cloudsdk.example_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class LocationBroadcast extends BroadcastReceiver {
    public LocationBroadcast() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        Intent i = new Intent(context, ValueActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("ARG_DEVICEID","240038000347353138383138");
        Toast.makeText(context,"Broadcast",Toast.LENGTH_SHORT).show();
        context.startActivity(i);
    }


}

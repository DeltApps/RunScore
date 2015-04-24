package com.deltapps.runscore;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GPSTracker extends Service {
    public GPSTracker() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

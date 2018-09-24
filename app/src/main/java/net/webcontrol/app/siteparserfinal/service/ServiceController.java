package net.webcontrol.app.siteparserfinal.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServiceController extends Service {
    Controller controller = new Controller();

    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        controller.SetControl(this);
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        controller.SetControl(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}

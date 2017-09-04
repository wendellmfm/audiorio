package br.great.jogopervasivo.gcmUtil;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by messiaslima on 26/02/2015.
 * @author messiaslinma
 * @since 1.0
 * @version 1.0
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    /**
     *
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName component = new ComponentName(context.getPackageName(),GcmIntentService.class.getName());
        intent.setComponent(component);
        startWakefulService(context,intent);
        setResultCode(Activity.RESULT_OK);
    }
}

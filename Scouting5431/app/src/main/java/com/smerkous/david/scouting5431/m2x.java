package com.smerkous.david.scouting5431;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import com.att.m2x.android.listeners.ResponseListener;
import com.att.m2x.android.main.M2XAPI;
import com.att.m2x.android.model.Device;
import com.att.m2x.android.network.ApiV2Response;
import com.att.m2x.android.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/*
 *
 * Created by David on 12/16/2015.
 *
 */
public class m2x {
    public volatile static String apiKey = config.apiKey;
    public volatile static String deviceID = config.deviceID;
    public volatile static String stream = config.stream;

    public static void init(Context context)
    {
        M2XAPI.initialize(context, apiKey);
    }

    public static void send(Context context, JSONObject sends, final View view)
    {
        try {
            JSONObject toSend = new JSONObject("{\"timestamp\":\""
                    +DateUtils.dateTimeToString(new Date())
                    +"\",\n \"value\":'["+sends.toString()+"]'}");

            Device.updateDataStreamValue(context, toSend, deviceID, stream, new ResponseListener() {
                @Override
                public void onRequestCompleted(ApiV2Response result, int requestCode) {
                    Snackbar.make(view, "Successfuly Sent!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

                @Override
                public void onRequestError(ApiV2Response error, int requestCode) {
                    Log.i("Terrible", "Error sending to m2x" + String.valueOf(requestCode));
                    Snackbar.make(view, "ERROR sending...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        } catch (JSONException ok) {
            Snackbar.make(view, "ERROR sending...", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public static void recieve(final Context context) {
        Device.listDataStreams(context, deviceID, new ResponseListener() {
            @Override
            public void onRequestCompleted(ApiV2Response result, int requestCode) {
                MainActivity.toaster(context, String.valueOf(result));
            }

            @Override
            public void onRequestError(ApiV2Response error, int requestCode) {
                MainActivity.toaster(context, String.valueOf(error));
            }
        });
    }
}

package com.smerkous.david.scouting5431;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static SeekBar seek;
    public static EditText text;
    public static ToggleButton button;
    public static ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        m2x.init(getApplicationContext());

        FloatingActionButton sender = (FloatingActionButton) findViewById(R.id.send);
        sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                    if(!checkVals(getApplicationContext()))
                        return;
                    showProgress();

                    new AsyncTask<Void, Void, Void>()
                    {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Object vals[] = getVals();
                            JSONObject toSend;
                            try {
                                toSend = jsonParser((int) vals[0], String.valueOf(vals[1]), (Boolean) vals[2]);
                                m2x.send(getApplicationContext(), toSend, view);
                                Thread.sleep(1000);
                            } catch (JSONException | InterruptedException ignored) {}
                            return null;
                        }
                        @Override
                        protected void onPostExecute(Void result) {
                            stopProgress();
                        }
                    }.execute();
            }
        });

        seek = (SeekBar) findViewById(R.id.seekBar);
        text = (EditText) findViewById(R.id.editText);
        button = (ToggleButton) findViewById(R.id.toggleButton);
        m2x.recieve(getApplicationContext());
    }

    public void showProgress()
    {
        loading = new ProgressDialog(MainActivity.this);
        loading.setMessage("Please wait while sending to server...");
        loading.show();
    }

    public void stopProgress()
    {
        loading.dismiss();
    }

    public static boolean checkVals(Context context)
    {
        if(text.getText().length() < 3)
        {
            toaster(context, "Message is too short!");
            return false;
        }
        return true;
    }

    public static Object[] getVals()
    {
        Object toRet[] = {null, null, null};
        toRet[0] = seek.getProgress();
        toRet[1] = text.getText();
        toRet[2] = button.isChecked();
        return toRet;
    }

    public static JSONObject jsonParser(int value, String value2, boolean value3) throws JSONException {
        JSONObject temp = new JSONObject();
        temp.put("INTEGER", value);
        temp.put("STRING", value2);
        temp.put("BOOL", value3);
        return temp;
    }

    public static void toaster(Context context,String message)
    {
        Toast toShow = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toShow.setGravity(Gravity.BOTTOM,10,60);
        toShow.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

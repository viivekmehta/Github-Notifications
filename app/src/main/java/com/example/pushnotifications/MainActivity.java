package com.example.pushnotifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public void startTracking(View view) {

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Instant currentTime = Instant.now();
                Instant previousTime = Instant.now().minusSeconds(10);
                Log.i("Current Time","Current Time ==> "+currentTime.toString()+" and previous time  ==> "+previousTime.toString());
                String url = "https://api.github.com/repos/viivekmehta/Github-Notifications/commits?since="+previousTime.toString()+"&until"+currentTime.toString();
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                if(response != null && response.length()>0) {
                                    Log.i("Success","Success ==> "+response.toString());
                                    sendMessage2(response.toString());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("Error","Error ==> "+error.toString());
                            }
                        }
                );
                requestQueue.add(arrayRequest);
            }
        },0,10000);

    }

    public void sendMessage2(String commitsMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel("default","test",NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(notificationChannel);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        Notification.Builder notification = new Notification.Builder(this, "default")
                .setContentTitle("You have new commit in Github!!")
                .setContentText(commitsMessage)
                .setSmallIcon(R.drawable.kohli)
                .setContentIntent(pendingIntent);
        notificationManager.notify(0,notification.build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}

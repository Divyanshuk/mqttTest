package com.divyanshu.mqtttest;

import android.Manifest;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MqttReceiver extends AppCompatActivity {

    final static private String TAG = "MqttReceiver";
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_receiver);

        final MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName("aakashk_kvjp58");
        options.setPassword("46f406136c5e4e5f874e283f95ed3dfc".toCharArray());

//
//        String clientId = "clientId-xzFiqpzdbZ";
        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(getApplicationContext(), "tcp://io.adafruit.com:1883",
                        clientId);

        runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Log.d(TAG, "onSuccess");
//                    Toast toast = Toast.makeText(getApplicationContext(), "Connection Done", Toast.LENGTH_SHORT);
//                    toast.show();

                            //Subscribing
                            final String topic = "aakashk_kvjp58/f/switch1";
                            int qos = 1;
                            try {
                                IMqttToken subToken = client.subscribe(topic, qos);
                                subToken.setActionCallback(new IMqttActionListener() {
                                    @Override
                                    public void onSuccess(IMqttToken asyncActionToken) {
                                        // The message was published
//                                Toast.makeText(MqttReceiver.this, "Successfully subscribed to: " + topic, Toast.LENGTH_SHORT).show();

                                        //sending the message

                                        // disconnecting after task is done
//                                try {
//                                    IMqttToken disconToken = client.disconnect();
//                                    disconToken.setActionCallback(new IMqttActionListener() {
//                                        @Override
//                                        public void onSuccess(IMqttToken asyncActionToken) {
//                                            // we are now successfully disconnected
//                                        }
//
//                                        @Override
//                                        public void onFailure(IMqttToken asyncActionToken,
//                                                              Throwable exception) {
//                                            // something went wrong, but probably we are disconnected anyway
//                                        }
//                                    });
//                                } catch (MqttException e) {
//                                    e.printStackTrace();
//                                }

                                    }

                                    @Override
                                    public void onFailure(IMqttToken asyncActionToken,
                                                          Throwable exception) {
                                        // The subscription could not be performed, maybe the user was not
                                        // authorized to subscribe on the specified topic e.g. using wildcards
//                                Toast.makeText(MqttReceiver.this, "Couldn't subscribe to: " + topic, Toast.LENGTH_SHORT).show();


                                    }
                                });
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            Log.d(TAG, "onFailure");
//                    Toast toast1 = Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT);
//                    toast1.show();


                        }
                    });


                } catch (MqttException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(runnable, 2000);

            }
        };
        handler.post(runnable);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(MqttReceiver.this, "LOST", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                String phoneNo = "9643165562";
                String message1 = "hello DD";

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message1, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                    ActivityCompat.requestPermissions(MqttReceiver.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            0);
                }

                Toast.makeText(MqttReceiver.this, message.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "received");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

                Toast.makeText(MqttReceiver.this, "completed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

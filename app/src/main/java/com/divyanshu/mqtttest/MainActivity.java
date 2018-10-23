package com.divyanshu.mqtttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

//    MqttAndroidClient MqttClient;

    final static private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText edit_message = (EditText) findViewById(R.id.edit_message);
        Button send_message = (Button) findViewById(R.id.send_message);

        /**
         * Send message Button
         */
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName("aakashk_kvjp58");
        options.setPassword("46f406136c5e4e5f874e283f95ed3dfc".toCharArray());

//
//        String clientId = "clientId-xzFiqpzdbZ";
        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(getApplicationContext(), "tcp://io.adafruit.com:1883",
                        clientId);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    Toast toast = Toast.makeText(getApplicationContext(), "Connection Done", Toast.LENGTH_SHORT);
                    toast.show();

                    //Subscribing
                    final String topic = "aakashk_kvjp58/f";
                    int qos = 1;
                    try {
                        IMqttToken subToken = client.subscribe(topic, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // The message was published
                                Toast.makeText(MainActivity.this, "Successfully subscribed to: " + topic, Toast.LENGTH_SHORT).show();

                                //sending the message

                                String user_message = edit_message.getText().toString();
                                String topic = "aakashk_kvjp58/f/switch1";
                                String payload = user_message;
                                byte[] encodedPayload = new byte[0];
                                try {
                                    encodedPayload = payload.getBytes("UTF-8");
                                    MqttMessage message = new MqttMessage(encodedPayload);
                                    client.publish(topic, message);
                                    Toast.makeText(MainActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

                                } catch (UnsupportedEncodingException | MqttException e) {
                                    e.printStackTrace();
                                }

                                // disconnecting after task is done
                                try {
                                    IMqttToken disconToken = client.disconnect();
                                    disconToken.setActionCallback(new IMqttActionListener() {
                                        @Override
                                        public void onSuccess(IMqttToken asyncActionToken) {
                                            // we are now successfully disconnected
                                        }

                                        @Override
                                        public void onFailure(IMqttToken asyncActionToken,
                                                              Throwable exception) {
                                            // something went wrong, but probably we are disconnected anyway
                                        }
                                    });
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards
                                Toast.makeText(MainActivity.this, "Couldn't subscribe to: " + topic, Toast.LENGTH_SHORT).show();


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
                    Toast toast1 = Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT);
                    toast1.show();


                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

            }
        });

    }
}

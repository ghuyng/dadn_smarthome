package com.example.smarthome

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MQTTService(val context: Context) {

    final val serverUri = "tcp://io.adafruit.com:1883"
    final val clientId = ""
    final val subscriptionTopic = "[path of feed to subscribe]"
    final val username = "ghuyng"
    final val password = "giahuy2k"


    var mqttAndroidClient: MqttAndroidClient = MqttAndroidClient(context, serverUri, clientId)

    init {
        mqttAndroidClient.setCallback(object: MqttCallbackExtended{
            override fun connectionLost(cause: Throwable?) {

            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.w("Mqtt", message.toString())
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

            }

            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                Log.w("mqtt", serverURI)
            }
        })
        connect()
    }

    fun setCallback(callback: MqttCallbackExtended){
        mqttAndroidClient.setCallback(callback)
    }

    private fun connect(){
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false
        mqttConnectOptions.userName = username
        mqttConnectOptions.password = password.toCharArray()

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null,
                object : IMqttActionListener{
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val disconnectedBufferOptions = DisconnectedBufferOptions()
                        disconnectedBufferOptions.isBufferEnabled = true
                        disconnectedBufferOptions.bufferSize = 100
                        disconnectedBufferOptions.isPersistBuffer = false
                        disconnectedBufferOptions.isDeleteOldestMessages = false
                        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                        subscribeToTopic()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.w("Mqtt", "Failed to connect to:"
                                + serverUri + exception.toString())
                    }

                })
        } catch (ex: MqttException){
            ex.printStackTrace()
        }
    }
    private fun subscribeToTopic(){
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null,
                object: IMqttActionListener{
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.w("Mqtt", "Subscribed!")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.w("Mqtt", "Subscribed fail!")
                    }

                })
        } catch (ex: MqttException){
            println("Exception Subscribing")
            ex.printStackTrace()
        }
    }
}
package com.csci448.trentdouglas.trentdouglas_a4

import android.graphics.PointF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity(), SensorEventListener {
    private val LOG_TAG = "mainactivity: "
    private lateinit var boxDrawingView:BoxDrawingView
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "oncreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        boxDrawingView = findViewById(R.id.main_activity)
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

    }
    private lateinit var sensorManager:SensorManager
    private lateinit var accelerometer:Sensor
    private lateinit var lightSensor:Sensor



    override fun onSensorChanged(event: SensorEvent?) {
        if(event!!.sensor.type == Sensor.TYPE_ACCELEROMETER){
            val xAccel = event?.values?.get(0) ?: 0.0f
            val yAccel = event?.values?.get(1) ?: 0.0f
            boxDrawingView.setAcceleration(PointF(xAccel, yAccel))

            Log.d(LOG_TAG, "x accel = ${xAccel}")
            Log.d(LOG_TAG, "y accel = ${yAccel}")
        }
        else if(event!!.sensor.type == Sensor.TYPE_LIGHT){
            Log.d(LOG_TAG, "light! ${event?.values?.get(0)}")
            boxDrawingView.setLight(event?.values?.get(0)!!)
        }

    }

    override fun onResume() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_GAME)
        super.onResume()
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
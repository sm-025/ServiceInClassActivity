package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    var timerBinder: TimerService.TimerBinder? = null
    val handler = Handler(Looper.getMainLooper()) { true }

    val startVal = 20

    val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(compName: ComponentName?, service: IBinder?) {
            (service as TimerService.TimerBinder).setHandler(handler)

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            timerBinder = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindService(Intent(this,
            TimerService::class.java),
            serviceConnection, BIND_AUTO_CREATE)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            timerBinder?.run {

            }

            timerBinder?.start(startVal)
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            timerBinder?.stop()
        }
    }
}
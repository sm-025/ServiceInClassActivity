package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    var SAVE_KEY = "savedTime"
    var RESTORE_KEY = "restore"

    lateinit var timerBinder: TimerService.TimerBinder
    var isConnected = false
    val timerHandler = Handler(Looper.getMainLooper()) {
        timerTextView.text = it.what.toString()
        true
    }

    var startVal = 20
    var restore = false

    lateinit var timerTextView: TextView


    val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(compName: ComponentName?, service: IBinder?) {
            timerBinder = service as TimerService.TimerBinder
            timerBinder.setHandler(timerHandler)
            isConnected = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnected = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = getPreferences(MODE_PRIVATE)

        restore = preferences.getBoolean(RESTORE_KEY, false)
        if (restore) {
            startVal = preferences.getInt(SAVE_KEY, 20)
        }

        timerTextView = findViewById(R.id.textView)

        bindService(Intent(this,
            TimerService::class.java),
            serviceConnection, BIND_AUTO_CREATE)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            timerBinder.start(startVal)
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            timerBinder.stop()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_start -> start()
            R.id.action_stop -> stop()
            R.id.action_pause -> pause()

            else -> return false
        }

        return true
    }

    fun start() {
        if (isConnected) {
            if (!timerBinder.isRunning) {
                timerBinder.start(startVal)
            }
            else {
                timerBinder.pause()
            }
        }
    }

    fun stop() {
        if (isConnected) {
           timerBinder.stop()
        }
    }

    fun pause() {
        if (isConnected) {
            timerBinder.pause()
            with(preferences.edit()) {
                val timeLeft = findViewById<TextView>(R.id.textView).text.toString().toInt()
                putInt(SAVE_KEY, timeLeft)
                putBoolean(RESTORE_KEY, true)
                apply()
            }
        }
    }
}
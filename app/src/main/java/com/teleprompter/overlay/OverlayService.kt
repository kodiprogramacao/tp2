package com.teleprompter.overlay

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.app.NotificationCompat

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private lateinit var scrollView: ScrollView
    private var params: WindowManager.LayoutParams? = null

    private val handler = Handler(Looper.getMainLooper())
    private var isPaused = false
    private var pixelsPerFrame = 2f
    private var scrollAccumulator = 0f

    private val scrollRunnable = object : Runnable {
        override fun run() {
            if (!isPaused) {
                scrollAccumulator += pixelsPerFrame
                if (scrollAccumulator >= 1f) {
                    val step = scrollAccumulator.toInt()
                    scrollView.scrollBy(0, step)
                    scrollAccumulator -= step
                }
            }
            handler.postDelayed(this, 16L)
        }
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundWithNotification()

        val text = intent?.getStringExtra("script_text") ?: ""
        val speed = intent?.getIntExtra("speed", 6) ?: 6
        val fontSize = intent?.getIntExtra("font_size", 22) ?: 22

        // velocidade 1..20 vira pixels por frame (~60fps)
        pixelsPerFrame = speed.coerceIn(1, 20) * 0.3f

        setupOverlay(text, fontSize)

        return START_NOT_STICKY
    }

    private fun startForegroundWithNotification() {
        val channelId = "teleprompter_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Teleprompter", NotificationManager.IMPORTANCE_LOW
            )
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Teleprompter ativo")
            .setContentText("Toque no X na tela para encerrar")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1, notification)
        }
    }

    private fun setupOverlay(text: String, fontSize: Int) {
        val inflater = LayoutInflater.from(this)
        overlayView = inflater.inflate(R.layout.overlay_layout, null)

        scrollView = overlayView.findViewById(R.id.scrollView)
        val textView: TextView = overlayView.findViewById(R.id.textViewScript)
        val buttonPause: Button = overlayView.findViewById(R.id.buttonPause)
        val buttonClose: TextView = overlayView.findViewById(R.id.buttonClose)
        val dragHandle: View = overlayView.findViewById(R.id.dragHandle)

        textView.text = text
        textView.textSize = fontSize.toFloat()

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 100
        }

        windowManager.addView(overlayView, params)

        buttonPause.setOnClickListener {
            isPaused = !isPaused
            buttonPause.text = if (isPaused) "▶" else "❚❚"
        }

        buttonClose.setOnClickListener {
            stopSelf()
        }

        var initialY = 0
        var touchY = 0f

        dragHandle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialY = params!!.y
                    touchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params!!.y = initialY + (event.rawY - touchY).toInt()
                    windowManager.updateViewLayout(overlayView, params)
                    true
                }
                else -> false
            }
        }

        handler.post(scrollRunnable)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(scrollRunnable)
        if (::overlayView.isInitialized) {
            try {
                windowManager.removeView(overlayView)
            } catch (e: Exception) {
                // já removida
            }
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}

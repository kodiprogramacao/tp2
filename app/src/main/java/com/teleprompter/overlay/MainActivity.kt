package com.teleprompter.overlay

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editTextScript: EditText
    private lateinit var seekBarSpeed: SeekBar
    private lateinit var seekBarFontSize: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextScript = findViewById(R.id.editTextScript)
        seekBarSpeed = findViewById(R.id.seekBarSpeed)
        seekBarFontSize = findViewById(R.id.seekBarFontSize)
        val buttonStart: Button = findViewById(R.id.buttonStart)

        buttonStart.setOnClickListener {
            val text = editTextScript.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this, "Cole um texto primeiro", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Settings.canDrawOverlays(this)) {
                requestOverlayPermission()
            } else {
                startTeleprompter(text)
            }
        }
    }

    private fun requestOverlayPermission() {
        Toast.makeText(
            this,
            "Permita 'Exibir sobre outros apps' e volte aqui para apertar Iniciar de novo",
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
    }

    private fun startTeleprompter(text: String) {
        val speed = seekBarSpeed.progress.coerceAtLeast(1)
        val fontSize = seekBarFontSize.progress.coerceAtLeast(10)

        val intent = Intent(this, OverlayService::class.java).apply {
            putExtra("script_text", text)
            putExtra("speed", speed)
            putExtra("font_size", fontSize)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        // Manda o app para segundo plano para você abrir a câmera por cima
        moveTaskToBack(true)
    }
}

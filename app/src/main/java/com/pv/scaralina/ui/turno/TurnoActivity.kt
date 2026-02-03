package com.pv.scaralina.ui.turno

import android.app.Dialog
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import com.pv.scaralina.R
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.pv.scaralina.ui.commons.CercaParolaDialogFragment
import com.pv.scaralina.ui.main.MainActivity


class TurnoActivity : AppCompatActivity() {

    private lateinit var tvContatore: TextView
    private lateinit var btnStartStop: ImageButton
    private lateinit var btnReset: ImageButton
    private var timer: CountDownTimer? = null
    private var isRunning = false
    private var remainingMillis: Long = 0
    private var selectedMinutes = 3L
    private lateinit var btnCercaParola: ImageButton
    private lateinit var btnChiudiPartita: ImageButton
    private lateinit var btnCambiaTurno: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_turno)

        tvContatore = findViewById(R.id.tvContatore)
        btnStartStop = findViewById(R.id.btnTimer)
        btnReset = findViewById(R.id.btnResetTimer)
        btnCercaParola = findViewById(R.id.btnCercaParola)
        btnChiudiPartita = findViewById(R.id.btnChiudiPartita)
        btnCambiaTurno = findViewById(R.id.btnCambiaTurno)


        btnCercaParola.setOnClickListener {
            val dialog = CercaParolaDialogFragment()
            dialog.show(supportFragmentManager, "CercaParolaDialog")
        }

        btnStartStop.setOnClickListener { if (isRunning) pauseTimer() else startTimer() }
        btnReset.setOnClickListener { resetTimer() }

        remainingMillis = selectedMinutes * 60 * 1000
        updateTimerText(remainingMillis)

        btnChiudiPartita.setOnClickListener { showChiudiPartitaDialog() }
        btnCambiaTurno.setOnClickListener { showCambiaTurnoDialog() }

        startTimer()
    }

    // ===========================
    // TIMER FUNZIONI
    // ===========================
    private fun startTimer() {
        timer = object : CountDownTimer(remainingMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingMillis = millisUntilFinished
                updateTimerText(remainingMillis)

                if (millisUntilFinished / 1000 <= 10) {
                    ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
                        .startTone(ToneGenerator.TONE_PROP_BEEP)
                }
            }

            override fun onFinish() {
                isRunning = false
                remainingMillis = selectedMinutes * 60 * 1000
                updateTimerText(remainingMillis)

                ToneGenerator(AudioManager.STREAM_ALARM, 100)
                    .startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD)
            }
        }.start()

        isRunning = true
        btnStartStop.setImageResource(R.drawable.ic_pause_24)
    }

    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
        btnStartStop.setImageResource(R.drawable.ic_play_24)
    }

    private fun resetTimer() {
        timer?.cancel()
        isRunning = false
        remainingMillis = selectedMinutes * 60 * 1000
        updateTimerText(remainingMillis)
        btnStartStop.setImageResource(R.drawable.ic_play_24)
    }

    private fun updateTimerText(millis: Long) {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        tvContatore.text = String.format("%02d:%02d", minutes, seconds)
    }

    // -----------------------------
    // Dialog chiudi partita
    // -----------------------------
    private fun showChiudiPartitaDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_punteggio)

        val editTextPunteggio = dialog.findViewById<EditText>(R.id.editTextPunteggio)
        val btnAdd = dialog.findViewById<ImageButton>(R.id.btnAdd)
        val btnClose = dialog.findViewById<ImageButton>(R.id.btnClose)

        btnAdd.setOnClickListener {
            val punteggio = editTextPunteggio.text.toString().trim()

            if (punteggio.isEmpty()) {
                editTextPunteggio.error = "Inserisci punteggio"
                return@setOnClickListener
            }

            startActivity(Intent(this, MainActivity::class.java))
        }

        btnClose.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()
    }

    // -----------------------------
    // Dialog cambia turno
    // -----------------------------
    private fun showCambiaTurnoDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_punteggio)

        val editTextPunteggio = dialog.findViewById<EditText>(R.id.editTextPunteggio)
        val btnAdd = dialog.findViewById<ImageButton>(R.id.btnAdd)
        val btnClose = dialog.findViewById<ImageButton>(R.id.btnClose)

        btnAdd.setOnClickListener {
            val punteggio = editTextPunteggio.text.toString().trim()

            if (punteggio.isEmpty()) {
                editTextPunteggio.error = "Inserisci punteggio"
                return@setOnClickListener
            }

            startActivity(Intent(this, TurnoActivity::class.java))
        }

        btnClose.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()
    }
}
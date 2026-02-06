package com.pv.scaralina.ui.turno

import android.app.Dialog
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import com.pv.scaralina.R
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pv.scaralina.data.Giocatore
import com.pv.scaralina.data.Partita
import com.pv.scaralina.ui.commons.CercaParolaDialogFragment


class TurnoActivity : AppCompatActivity() {

    private lateinit var tvGiocatorePunteggio1: TextView
    private lateinit var tvGiocatorePunteggio2: TextView
    private lateinit var tvGiocatorePunteggio3: TextView
    private lateinit var tvGiocatorePunteggio4: TextView
    private lateinit var tvGiocatore: TextView
    private lateinit var tvContatore: TextView
    private lateinit var llTimer: LinearLayout
    private lateinit var btnStartStop: ImageButton
    private lateinit var btnReset: ImageButton
    private var timer: CountDownTimer? = null
    private var isRunning = false
    private var remainingMillis: Long = 0
    private var selectedMinutes = 3
    private lateinit var btnCercaParola: ImageButton
    private lateinit var btnChiudiPartita: ImageButton
    private lateinit var btnCambiaTurno: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_turno)

        tvGiocatorePunteggio1 = findViewById(R.id.tvGiocatorePunteggio1)
        tvGiocatorePunteggio2 = findViewById(R.id.tvGiocatorePunteggio2)
        tvGiocatorePunteggio3 = findViewById(R.id.tvGiocatorePunteggio3)
        tvGiocatorePunteggio4 = findViewById(R.id.tvGiocatorePunteggio4)

        llTimer = findViewById(R.id.llTimer)
        tvGiocatore = findViewById(R.id.tvGiocatore)
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

        btnChiudiPartita.setOnClickListener {
            showChiudiPartitaDialog(
                Partita.getGiocatoreCorrente(),
                Partita.giocatori.filter { it != Partita.getGiocatoreCorrente() })
        }
        btnCambiaTurno.setOnClickListener { showCambiaTurnoDialog() }

        tvGiocatore.text = Partita.getGiocatoreCorrente().nome

        avviaTurno()
    }

    private fun avviaTurno() {

        //mostra punteggio corrente dei giocatori
        val numGiocatori = Partita.giocatori.size
        for (i in 0..numGiocatori - 1) {
            if (i == 0) {
                tvGiocatorePunteggio1.text = Partita.giocatori[i].nome + "\n" + Partita.giocatori[i].punteggio
                tvGiocatorePunteggio1.visibility = View.VISIBLE
            }

            if (i == 1) {
                tvGiocatorePunteggio2.text = Partita.giocatori[i].nome + "\n" + Partita.giocatori[i].punteggio
                tvGiocatorePunteggio2.visibility = View.VISIBLE
            }

            if (i == 2) {
                tvGiocatorePunteggio3.text = Partita.giocatori[i].nome + "\n" + Partita.giocatori[i].punteggio
                tvGiocatorePunteggio3.visibility = View.VISIBLE
            }

            if (i == 3) {
                tvGiocatorePunteggio4.text = Partita.giocatori[i].nome + "\n" + Partita.giocatori[i].punteggio
                tvGiocatorePunteggio4.visibility = View.VISIBLE
            }
        }


        if (Partita.timerAbilitato) {
            selectedMinutes = Partita.durataTimer
            remainingMillis = (selectedMinutes * 60 * 1000).toLong()
            llTimer.visibility = View.VISIBLE
            startTimer()
        } else {
            selectedMinutes = 0
            llTimer.visibility = View.GONE
        }
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
                remainingMillis = (selectedMinutes * 60 * 1000).toLong()
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
        remainingMillis = (selectedMinutes * 60 * 1000).toLong()
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

    private fun showChiudiPartitaDialog(giocatore: Giocatore, altriGiocatori: List<Giocatore>) {
        if (giocatore.equals(Partita.getGiocatoreCorrente())) {
            getPunteggioDialog("Chiusura") { punteggio ->
                // Aggiorna il punteggio del giocatore corrente con punteggioChiusura
                Partita.aggiornaPunteggio(Partita.getGiocatoreCorrente(), punteggio) //incremento

                showChiudiPartitaDialog(
                    altriGiocatori.get(0),
                    altriGiocatori.filter { it != altriGiocatori.get(0) })
            }
        } else {
            getPunteggioDialog("Penalità ${giocatore.nome}") { punteggio ->
                // Aggiorna il punteggio del giocatore corrente con punteggioChiusura
                Partita.aggiornaPunteggio(Partita.getGiocatoreCorrente(), punteggio) //incremento
                // Aggiorna il punteggio del giocatore con la penalità
                Partita.aggiornaPunteggio(giocatore, punteggio * (-1)) //decremento

                if (altriGiocatori.size > 1) {
                    showChiudiPartitaDialog(
                        altriGiocatori.filter { it != giocatore }.get(0),
                        altriGiocatori.filter { it != giocatore })
                } else {
                    startActivity(Intent(this, TurnoActivity::class.java))
                }
            }
        }
    }




    // -----------------------------
    // Dialog cambia turno
    // -----------------------------
    private fun showCambiaTurnoDialog() {
        getPunteggioDialog("") { punteggio ->
            Partita.aggiornaPunteggio(Partita.getGiocatoreCorrente(), punteggio)
            Partita.passaGiocatoreSuccessivo()
            startActivity(Intent(this, TurnoActivity::class.java))
        }
    }

    private fun getPunteggioDialog(intestazione: String, onPunteggioObtained: (Int) -> Unit) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_punteggio)

        val editTextPunteggio = dialog.findViewById<EditText>(R.id.editTextPunteggio)
        if (intestazione != null && intestazione.isNotBlank()) {
            editTextPunteggio.setHint(intestazione)
        }

        editTextPunteggio.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.isDigit(source[i])) {
                    return@InputFilter "" // Ritorna una stringa vuota se non è un numero
                }
            }
            null // Permetti l'input
        })

        val btnAdd = dialog.findViewById<ImageButton>(R.id.btnAdd)
        val btnClose = dialog.findViewById<ImageButton>(R.id.btnClose)

        btnAdd.setOnClickListener {
            val punteggio = editTextPunteggio.text.toString().trim()

            if (punteggio.isEmpty()) {
                editTextPunteggio.error = "Inserisci punteggio"
                return@setOnClickListener
            }

            // Chiama il callback con il punteggio ottenuto
            onPunteggioObtained(punteggio.toInt())
            dialog.dismiss() // Chiudi il dialog
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
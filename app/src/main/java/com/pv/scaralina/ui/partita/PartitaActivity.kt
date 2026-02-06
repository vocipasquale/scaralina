package com.pv.scaralina.ui.partita

import android.content.Intent
import com.pv.scaralina.R
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.pv.scaralina.data.Giocatore
import com.pv.scaralina.data.Partita
import com.pv.scaralina.ui.turno.TurnoActivity


class PartitaActivity : AppCompatActivity() {

    private lateinit var seekBarDurata: SeekBar
    private lateinit var tvDurata: TextView
    private var selectedMinutes = 3L
    private lateinit var ckbTimer: CheckBox
    private lateinit var btnStart: ImageButton
    private lateinit var btnBack: ImageButton
    private lateinit var etGiocatore1: EditText
    private lateinit var etGiocatore2: EditText
    private lateinit var etGiocatore3: EditText
    private lateinit var etGiocatore4: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partita)

        seekBarDurata = findViewById(R.id.sbDurataTimer)
        tvDurata = findViewById(R.id.tvDurata)
        ckbTimer = findViewById(R.id.ckbTimer)
        btnStart = findViewById(R.id.btnStart)
        btnBack = findViewById(R.id.btnBack)
        etGiocatore1 = findViewById(R.id.etGiocatore1)
        etGiocatore2 = findViewById(R.id.etGiocatore2)
        etGiocatore3 = findViewById(R.id.etGiocatore3)
        etGiocatore4 = findViewById(R.id.etGiocatore4)


        // ===========================
        // SEEKBAR DURATA
        // ===========================
        seekBarDurata.max = 5
        seekBarDurata.progress = selectedMinutes.toInt()
        tvDurata.text = "$selectedMinutes"

        seekBarDurata.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedMinutes = if (progress < 1) 1 else progress.toLong()
                tvDurata.text = "$selectedMinutes"
//                if (!isRunning) {
//                    remainingMillis = selectedMinutes * 60 * 1000
//                    updateTimerText(remainingMillis)
//                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        updateTimerControls(ckbTimer.isChecked)
        ckbTimer.setOnCheckedChangeListener { _, isChecked ->
            updateTimerControls(isChecked)
        }

        btnStart.setOnClickListener {
            if(checkPartita()){

                if (etGiocatore1.text?.isNotBlank() == true) {
                    Partita.aggiungiGiocatore(Giocatore(etGiocatore1.text.toString(), 0))
                }
                if (etGiocatore2.text?.isNotBlank() == true) {
                    Partita.aggiungiGiocatore(Giocatore(etGiocatore2.text.toString(), 0))
                }
                if (etGiocatore3.text?.isNotBlank() == true) {
                    Partita.aggiungiGiocatore(Giocatore(etGiocatore3.text.toString(), 0))
                }
                if (etGiocatore4.text?.isNotBlank() == true) {
                    Partita.aggiungiGiocatore(Giocatore(etGiocatore4.text.toString(), 0))
                }
                Log.d("PartitaActivity", "Num giocatori: "+Partita.giocatori.size)
                Log.d("PartitaActivity", "giocatori: "+Partita.giocatori)

                if(ckbTimer.isChecked) {
                    Partita.timerAbilitato = true
                    Partita.durataTimer = tvDurata.text?.toString()?.toInt() ?: 0
                }

                startActivity(Intent(this, TurnoActivity::class.java))
            }
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun checkPartita(): Boolean {
        if(etGiocatore1.text?.isBlank() == true){
            return false
        }

        if(etGiocatore2.text?.isBlank() == true){
            return false
        }

        return true
    }

    private fun updateTimerControls(isEnabled: Boolean) {
        seekBarDurata.isEnabled = isEnabled
        tvDurata.isEnabled = isEnabled
    }
}
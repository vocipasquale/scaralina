package com.pv.scaralina.ui.partita

import android.content.Intent
import com.pv.scaralina.R
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.pv.scaralina.ui.turno.TurnoActivity


class PartitaActivity : AppCompatActivity() {

    private lateinit var seekBarDurata: SeekBar
    private lateinit var tvDurata: TextView
    private var selectedMinutes = 3L
    private lateinit var ckbTimer: CheckBox
    private lateinit var btnStart: ImageButton
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partita)

        seekBarDurata = findViewById(R.id.sbDurataTimer)
        tvDurata = findViewById(R.id.tvDurata)
        ckbTimer = findViewById(R.id.ckbTimer)
        btnStart = findViewById(R.id.btnStart)
        btnBack = findViewById(R.id.btnBack)

        // ===========================
        // SEEKBAR DURATA
        // ===========================
        seekBarDurata.max = 5
        seekBarDurata.progress = selectedMinutes.toInt()
        tvDurata.text = "$selectedMinutes min"

        seekBarDurata.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedMinutes = if (progress < 1) 1 else progress.toLong()
                tvDurata.text = "$selectedMinutes min"
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
            startActivity(Intent(this, TurnoActivity::class.java))
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun updateTimerControls(isEnabled: Boolean) {
        seekBarDurata.isEnabled = isEnabled
        tvDurata.isEnabled = isEnabled
    }
}
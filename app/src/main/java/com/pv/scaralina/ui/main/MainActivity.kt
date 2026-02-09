package com.pv.scaralina.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.pv.scaralina.R
import com.pv.scaralina.ScaralinaApp
import com.pv.scaralina.ui.commons.CercaParolaDialogFragment
import com.pv.scaralina.ui.partita.PartitaActivity


class MainActivity : AppCompatActivity() {


    private lateinit var btnCercaParola: ImageButton
    private lateinit var btnNuovaPartita: ImageButton


    private val database by lazy { (application as ScaralinaApp).database }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Mantieni lo schermo acceso
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        btnCercaParola = findViewById(R.id.btnCercaParola)
        btnNuovaPartita = findViewById(R.id.btnNuovaPartita)


        btnCercaParola.setOnClickListener {
            val dialog = CercaParolaDialogFragment()
            dialog.show(supportFragmentManager, "CercaParolaDialog")
        }

        btnNuovaPartita.setOnClickListener {
            startActivity(Intent(this, PartitaActivity::class.java))
        }

    }

}
